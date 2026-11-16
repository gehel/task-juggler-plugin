package fr.ledcom.tj3.taskjugglerplugin.tj3;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import org.jetbrains.annotations.NotNull;

/** Application settings of the TaskJuggler integration. */
@Service(Service.Level.APP)
@State(name = "TaskJugglerSettings", storages = @Storage("taskjuggler.xml"))
public final class TJSettings implements PersistentStateComponent<TJSettings.State> {

    public static class State {
        /** Path to the tj3 executable, or just "tj3" to look it up on the PATH. */
        public String tj3Path = "tj3";
        /** Run "tj3 --check-syntax" on saved files and show its errors in the editor. */
        public boolean checkSyntax = true;
    }

    private State state = new State();

    public static TJSettings getInstance() {
        return ApplicationManager.getApplication().getService(TJSettings.class);
    }

    @Override
    public @NotNull State getState() {
        return state;
    }

    @Override
    public void loadState(@NotNull State state) {
        this.state = state;
    }
}
