package fr.ledcom.tj3.taskjugglerplugin.tj3;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.configurations.PathEnvironmentVariableUtil;
import com.intellij.execution.process.CapturingProcessHandler;
import com.intellij.execution.process.ProcessOutput;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Runs the TaskJuggler command line tool. */
public final class TJ3 {

    /** A diagnostic of tj3: {@code /path/file.tjp:12: Error: message}. */
    public record Message(@NotNull Path file, int line, boolean error, @NotNull String text) {
    }

    private static final Pattern MESSAGE = Pattern.compile("^(.+?):(\\d+): (Error|Warning): (.*)$");
    private static final int TIMEOUT_MS = 60_000;

    private TJ3() {
    }

    /** The configured tj3 executable, or {@code null} if it cannot be found. */
    public static @Nullable File executable() {
        String configured = TJSettings.getInstance().getState().tj3Path;
        if (configured == null || configured.isBlank()) configured = "tj3";
        File file = new File(configured);
        if (file.isAbsolute() || configured.contains(File.separator)) return file.canExecute() ? file : null;
        return PathEnvironmentVariableUtil.findInPath(configured);
    }

    public static @NotNull GeneralCommandLine commandLine(@NotNull File executable, @NotNull Path workDir,
                                                          @NotNull List<String> arguments) {
        return new GeneralCommandLine(executable.getAbsolutePath())
                .withParameters(arguments)
                .withWorkDirectory(workDir.toFile())
                .withCharset(StandardCharsets.UTF_8)
                .withParentEnvironmentType(GeneralCommandLine.ParentEnvironmentType.CONSOLE);
    }

    /** Runs {@code tj3 --check-syntax} on the given main project file. */
    public static @NotNull List<Message> checkSyntax(@NotNull File executable, @NotNull Path mainFile)
            throws ExecutionException {
        GeneralCommandLine command = commandLine(executable, mainFile.getParent(),
                List.of("--check-syntax", "--no-color", "--silent", mainFile.toAbsolutePath().toString()));
        ProcessOutput output = new CapturingProcessHandler(command).runProcess(TIMEOUT_MS);
        if (output.isTimeout()) return List.of();
        return parse(output.getStdout() + "\n" + output.getStderr(), mainFile.getParent());
    }

    static @NotNull List<Message> parse(@NotNull String output, @NotNull Path baseDir) {
        List<Message> messages = new ArrayList<>();
        for (String line : output.split("\\R")) {
            Matcher m = MESSAGE.matcher(line);
            if (!m.matches()) continue;
            Path file = baseDir.resolve(m.group(1)).normalize();
            messages.add(new Message(file, Integer.parseInt(m.group(2)), "Error".equals(m.group(3)), m.group(4)));
        }
        return messages;
    }
}
