package utils.prefs;

import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXToggleButton;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.text.Text;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import java.util.prefs.Preferences;

public class Prefs {
    Preferences prefs;

    JFXCheckBox save; JFXCheckBox console; JFXCheckBox log;
    JFXToggleButton gpu; JFXToggleButton workspace;

    Text loadDir, saveDir;

    MaterialDesignIconView gpuMode;
    MaterialDesignIconView workspaceMode; MaterialDesignIconView trainIc;
    MaterialDesignIconView consoleIc; MaterialDesignIconView logIc;

    public String getCurrentLoadDir() {
        return currentLoadDir.get();
    }

    public StringProperty currentLoadDirProperty() {
        return currentLoadDir;
    }

    public void setCurrentLoadDir(String currentLoadDir) {
        this.currentLoadDir.set(currentLoadDir);
        prefs.put("loadDir", currentLoadDir);

    }

    public String getCurrentSaveDir() {
        return currentSaveDir.get();
    }

    public StringProperty currentSaveDirProperty() {
        return currentSaveDir;
    }

    public void setCurrentSaveDir(String currentSaveDir) {
        this.currentSaveDir.set(currentSaveDir);
        prefs.put("saveDir", currentSaveDir);
    }

    public boolean isCurrentSaveState() {
        return currentSaveState.get();
    }

    public BooleanProperty currentSaveStateProperty() {
        return currentSaveState;
    }

    public void setCurrentSaveState(boolean currentSaveState) {
        this.currentSaveState.set(currentSaveState);
        prefs.putBoolean("saveState", currentSaveState);
        if (currentSaveState) {
            trainIc.setGlyphName("CHECKBOX_MARKED_OUTLINE");
        } else {
            trainIc.setGlyphName("CHECKBOX_BLANK_OUTLINE");
        }
    }

    public boolean isCurrentConsoleState() {
        return currentConsoleState.get();
    }

    public BooleanProperty currentConsoleStateProperty() {
        return currentConsoleState;
    }

    public void setCurrentConsoleState(boolean currentConsoleState) {
        this.currentConsoleState.set(currentConsoleState);
        prefs.putBoolean("consoleState", currentConsoleState);
        if (currentConsoleState) {
            consoleIc.setGlyphName("CHECKBOX_MARKED_OUTLINE");
        } else {
            consoleIc.setGlyphName("CHECKBOX_BLANK_OUTLINE");
        }
    }

    public boolean isCurrentLogState() {
        return currentLogState.get();
    }

    public BooleanProperty currentLogStateProperty() {
        return currentLogState;
    }

    public void setCurrentLogState(boolean currentLogState) {
        this.currentLogState.set(currentLogState);
        prefs.putBoolean("logState", currentLogState);
        if (currentLogState) {
            logIc.setGlyphName("CHECKBOX_MARKED_OUTLINE");
        } else {
            logIc.setGlyphName("CHECKBOX_BLANK_OUTLINE");
        }
    }

    public boolean isCurrentGpuState() {
        return currentGpuState.get();
    }

    public BooleanProperty currentGpuStateProperty() {
        return currentGpuState;
    }

    public void setCurrentGpuState(boolean currentGpuState) {
        this.currentGpuState.set(currentGpuState);
        prefs.putBoolean("gpuState", currentGpuState);
        if (currentGpuState) {
            gpuMode.setGlyphName("TOGGLE_SWITCH");
        } else {
            gpuMode.setGlyphName("TOGGLE_SWITCH_OFF");
        }
    }

    public boolean isCurrentWorkspaceState() {
        return currentWorkspaceState.get();
    }

    public BooleanProperty currentWorkspaceStateProperty() {
        return currentWorkspaceState;
    }

    public void setCurrentWorkspaceState(boolean currentWorkspaceState) {
        this.currentWorkspaceState.set(currentWorkspaceState);
        prefs.putBoolean("workspaceState", currentWorkspaceState);
        if (currentWorkspaceState) {
            workspaceMode.setGlyphName("TOGGLE_SWITCH");
        } else {
            workspaceMode.setGlyphName("TOGGLE_SWITCH_OFF");
        }
    }

    StringProperty currentLoadDir = new SimpleStringProperty();
    StringProperty currentSaveDir = new SimpleStringProperty();
    BooleanProperty currentSaveState = new SimpleBooleanProperty();
    BooleanProperty currentConsoleState = new SimpleBooleanProperty();
    BooleanProperty currentLogState = new SimpleBooleanProperty();
    BooleanProperty currentGpuState = new SimpleBooleanProperty();
    BooleanProperty currentWorkspaceState = new SimpleBooleanProperty();

    public Prefs(JFXCheckBox save, JFXCheckBox console, JFXCheckBox log,
                 JFXToggleButton gpu, JFXToggleButton workspace) {

        this.save = save;
        this.console = console;
        this.log = log;
        this.gpu = gpu;
        this.workspace = workspace;

    }

    public void toUpdate(Text load, Text save, MaterialDesignIconView gpuMode,
                         MaterialDesignIconView workspaceMode, MaterialDesignIconView trainIc,
                         MaterialDesignIconView consoleIc, MaterialDesignIconView logIc) {
        this.loadDir = load;
        this.saveDir = save;
        this.gpuMode = gpuMode;
        this.workspaceMode = workspaceMode;
        this.trainIc = trainIc;
        this.consoleIc = consoleIc;
        this.logIc = logIc;
    }

    public void init() {
        prefs = Preferences.userRoot().node("Neuronix");

        setCurrentLoadDir(prefs.get("loadDir", FilenameUtils.concat(System.getProperty("user.dir"), "pre_trained")));
        setCurrentSaveDir(prefs.get("saveDir", FilenameUtils.concat(System.getProperty("user.dir"), "saved")));
        setCurrentSaveState(prefs.getBoolean("saveState", true));
        setCurrentConsoleState(prefs.getBoolean("consoleState", false));
        setCurrentLogState(prefs.getBoolean("logState", true));
        setCurrentGpuState(prefs.getBoolean("gpuState", false));
        setCurrentWorkspaceState(prefs.getBoolean("workspaceState", false));

        loadDir.textProperty().bind(this.currentLoadDirProperty());
        saveDir.textProperty().bind(this.currentSaveDirProperty());
        save.selectedProperty().bindBidirectional(this.currentSaveStateProperty());
        console.selectedProperty().bindBidirectional(this.currentConsoleStateProperty());
        log.selectedProperty().bindBidirectional(this.currentLogStateProperty());
        gpu.selectedProperty().bindBidirectional(this.currentGpuStateProperty());
        workspace.selectedProperty().bindBidirectional(this.currentWorkspaceStateProperty());

    }

}