package fr.ledcom.tj3.taskjugglerplugin.language.references;

import com.intellij.lang.refactoring.NamesValidator;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Pattern;

/** TaskJuggler ids: a letter or underscore followed by letters, digits and underscores. */
public class TJNamesValidator implements NamesValidator {

    private static final Pattern ID = Pattern.compile("[a-zA-Z_][a-zA-Z0-9_]*");

    @Override
    public boolean isKeyword(@NotNull String name, Project project) {
        return false;
    }

    @Override
    public boolean isIdentifier(@NotNull String name, Project project) {
        return ID.matcher(name).matches();
    }
}
