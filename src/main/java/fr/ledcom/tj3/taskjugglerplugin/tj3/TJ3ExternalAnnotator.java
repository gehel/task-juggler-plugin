package fr.ledcom.tj3.taskjugglerplugin.tj3;

import com.intellij.execution.ExecutionException;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.ExternalAnnotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import fr.ledcom.tj3.taskjugglerplugin.TJFile;
import fr.ledcom.tj3.taskjugglerplugin.language.references.TJIncludeGraph;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Shows the errors reported by {@code tj3 --check-syntax}. TaskJuggler reads the files from disk, so the check runs
 * only when the edited file and the files of its project are saved. For included files, the main files including
 * them are checked.
 */
public class TJ3ExternalAnnotator extends ExternalAnnotator<TJ3ExternalAnnotator.Input, List<TJ3.Message>> {

    private static final Logger LOG = Logger.getInstance(TJ3ExternalAnnotator.class);
    private static final Pattern QUOTED_TOKEN = Pattern.compile("token '([^']+)'");

    public record Input(@NotNull Path executable, @NotNull List<Path> mainFiles, @NotNull Path file) {
    }

    @Override
    public @Nullable Input collectInformation(@NotNull PsiFile file, @NotNull Editor editor, boolean hasErrors) {
        return collect(file);
    }

    @Override
    public @Nullable Input collectInformation(@NotNull PsiFile file) {
        return collect(file);
    }

    private static @Nullable Input collect(PsiFile file) {
        if (!(file instanceof TJFile) || !TJSettings.getInstance().getState().checkSyntax) return null;
        VirtualFile virtualFile = file.getVirtualFile();
        Path path = nioPath(virtualFile);
        if (path == null) return null;
        Path executable = TJ3.executable();
        if (executable == null) return null;

        FileDocumentManager documents = FileDocumentManager.getInstance();
        Set<VirtualFile> scope = TJIncludeGraph.scope(file);
        for (VirtualFile f : scope) {
            Document document = documents.getCachedDocument(f);
            if (document != null && documents.isDocumentUnsaved(document)) return null;
        }
        List<Path> mainFiles = new ArrayList<>();
        PsiManager manager = file.getManager();
        for (VirtualFile f : scope) {
            PsiFile psi = manager.findFile(f);
            Path main = nioPath(f);
            if (psi != null && main != null && isMainFile(psi, scope)) mainFiles.add(main);
        }
        if (mainFiles.isEmpty()) return null;
        return new Input(executable, mainFiles, path);
    }

    /** Path on disk, or null for files that tj3 cannot read (not on the local file system). */
    private static @Nullable Path nioPath(@Nullable VirtualFile file) {
        return file == null || !file.isInLocalFileSystem() ? null : file.getFileSystem().getNioPath(file);
    }

    /** A main file declares the project and is not included by another file of the scope. */
    private static boolean isMainFile(PsiFile file, Set<VirtualFile> scope) {
        if (!"tjp".equalsIgnoreCase(file.getVirtualFile().getExtension())) return false;
        for (VirtualFile other : scope) {
            PsiFile psi = file.getManager().findFile(other);
            if (psi != null && psi != file && TJIncludeGraph.includes(psi).contains(file.getVirtualFile())) return false;
        }
        return true;
    }

    @Override
    public @Nullable List<TJ3.Message> doAnnotate(Input input) {
        if (input == null) return null;
        List<TJ3.Message> result = new ArrayList<>();
        for (Path main : input.mainFiles()) {
            try {
                for (TJ3.Message message : TJ3.checkSyntax(input.executable(), main)) {
                    if (message.file().equals(input.file().normalize())) result.add(message);
                }
            } catch (ExecutionException e) {
                LOG.info("tj3 failed on " + main, e);
            }
        }
        return result;
    }

    @Override
    public void apply(@NotNull PsiFile file, List<TJ3.Message> messages, @NotNull AnnotationHolder holder) {
        if (messages == null) return;
        Document document = PsiDocumentManager.getInstance(file.getProject()).getDocument(file);
        if (document == null) return;
        for (TJ3.Message message : messages) {
            int line = message.line() - 1;
            if (line < 0 || line >= document.getLineCount()) continue;
            TextRange range = rangeFor(document, line, message.text());
            holder.newAnnotation(message.error() ? HighlightSeverity.ERROR : HighlightSeverity.WARNING,
                            "tj3: " + message.text())
                    .range(range)
                    .create();
        }
    }

    /** The token named in the message if found on the line, otherwise the line without indentation. */
    private static TextRange rangeFor(Document document, int line, String message) {
        int start = document.getLineStartOffset(line);
        int end = document.getLineEndOffset(line);
        String text = document.getText(new TextRange(start, end));
        Matcher m = QUOTED_TOKEN.matcher(message);
        if (m.find()) {
            int index = text.indexOf(m.group(1));
            if (index >= 0) return TextRange.from(start + index, m.group(1).length());
        }
        int indent = text.length() - text.stripLeading().length();
        int trailing = text.length() - text.stripTrailing().length();
        if (start + indent >= end - trailing) return new TextRange(start, end);
        return new TextRange(start + indent, end - trailing);
    }
}
