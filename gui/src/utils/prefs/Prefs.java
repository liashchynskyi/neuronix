package utils.prefs;

import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXToggleButton;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import java.util.prefs.Preferences;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.text.Text;
import org.apache.commons.io.FilenameUtils;

public class Prefs {
    
    private Preferences prefs;
    
    private JFXCheckBox     save;
    private JFXCheckBox     console;
   // private JFXCheckBox     log;
    private JFXToggleButton gpu;
    private JFXToggleButton workspace;
    
    private Text loadDir, saveDir;
    
    private MaterialDesignIconView gpuMode;
    private MaterialDesignIconView workspaceMode;
    private MaterialDesignIconView trainIc;
    private MaterialDesignIconView consoleIc;
    private MaterialDesignIconView logIc;
    private StringProperty  currentLoadDir        = new SimpleStringProperty();
    private StringProperty  currentSaveDir        = new SimpleStringProperty();
    private BooleanProperty currentSaveState      = new SimpleBooleanProperty();
    private BooleanProperty currentConsoleState   = new SimpleBooleanProperty();
    //private BooleanProperty currentLogState       = new SimpleBooleanProperty();
    private BooleanProperty currentGpuState       = new SimpleBooleanProperty();
    private BooleanProperty currentWorkspaceState = new SimpleBooleanProperty();
    
    
    public Prefs (JFXCheckBox save, JFXCheckBox console, /*JFXCheckBox log,*/ JFXToggleButton gpu,
                  JFXToggleButton workspace) {
        this.save = save;
        this.console = console;
        //this.log = log;
        this.gpu = gpu;
        this.workspace = workspace;
    }
    
    
    public String getCurrentLoadDir () {
        return currentLoadDir.get();
    }
    
    
    public void setCurrentLoadDir (String currentLoadDir) {
        this.currentLoadDir.set(currentLoadDir);
        prefs.put("loadDir", currentLoadDir);
    }
    
    public StringProperty currentLoadDirProperty () {
        return currentLoadDir;
    }
    
    
    public String getCurrentSaveDir () {
        return currentSaveDir.get();
    }
    
    
    public void setCurrentSaveDir (String currentSaveDir) {
        this.currentSaveDir.set(currentSaveDir);
        prefs.put("saveDir", currentSaveDir);
    }
    
    
    public StringProperty currentSaveDirProperty () {
        return currentSaveDir;
    }
    
    
    public boolean isCurrentSaveState () {
        return currentSaveState.get();
    }
    
    
    public void setCurrentSaveState (boolean currentSaveState) {
        this.currentSaveState.set(currentSaveState);
        prefs.putBoolean("saveState", currentSaveState);
        if (currentSaveState) {
            trainIc.setGlyphName("CHECKBOX_MARKED_OUTLINE");
        }
        else {
            trainIc.setGlyphName("CHECKBOX_BLANK_OUTLINE");
        }
    }
    
    
    public BooleanProperty currentSaveStateProperty () {
        return currentSaveState;
    }
    
    
    public boolean isCurrentConsoleState () {
        return currentConsoleState.get();
    }
    
    
    public void setCurrentConsoleState (boolean currentConsoleState) {
        this.currentConsoleState.set(currentConsoleState);
        prefs.putBoolean("consoleState", currentConsoleState);
        if (currentConsoleState) {
            consoleIc.setGlyphName("CHECKBOX_MARKED_OUTLINE");
        }
        else {
            consoleIc.setGlyphName("CHECKBOX_BLANK_OUTLINE");
        }
    }
    
    
    public BooleanProperty currentConsoleStateProperty () {
        return currentConsoleState;
    }
    
    
//    public boolean isCurrentLogState () {
//        return currentLogState.get();
//    }
//
//
//    public void setCurrentLogState (boolean currentLogState) {
//        this.currentLogState.set(currentLogState);
//        prefs.putBoolean("logState", currentLogState);
//        if (currentLogState) {
//            logIc.setGlyphName("CHECKBOX_MARKED_OUTLINE");
//        }
//        else {
//            logIc.setGlyphName("CHECKBOX_BLANK_OUTLINE");
//        }
//    }
//
//
//    public BooleanProperty currentLogStateProperty () {
//        return currentLogState;
//    }
    
    
    public boolean isCurrentGpuState () {
        return currentGpuState.get();
    }
    
    
    public void setCurrentGpuState (boolean currentGpuState) {
        this.currentGpuState.set(currentGpuState);
        prefs.putBoolean("gpuState", currentGpuState);
        if (currentGpuState) {
            gpuMode.setGlyphName("TOGGLE_SWITCH");
            gpu.setText("Вкл.");
        }
        else {
            gpuMode.setGlyphName("TOGGLE_SWITCH_OFF");
            gpu.setText("Викл.");
        }
    }
    
    
    public BooleanProperty currentGpuStateProperty () {
        return currentGpuState;
    }
    
    
    public boolean isCurrentWorkspaceState () {
        return currentWorkspaceState.get();
    }
    
    
    public void setCurrentWorkspaceState (boolean currentWorkspaceState) {
        this.currentWorkspaceState.set(currentWorkspaceState);
        prefs.putBoolean("workspaceState", currentWorkspaceState);
        if (currentWorkspaceState) {
            workspaceMode.setGlyphName("TOGGLE_SWITCH");
            workspace.setText("SIN");
        }
        else {
            workspaceMode.setGlyphName("TOGGLE_SWITCH_OFF");
            workspace.setText("SEP");
        }
    }
    
    
    public BooleanProperty currentWorkspaceStateProperty () {
        return currentWorkspaceState;
    }
    
    
    public void toUpdate (Text load, Text save, MaterialDesignIconView gpuMode,
                          MaterialDesignIconView workspaceMode, MaterialDesignIconView trainIc,
                          MaterialDesignIconView consoleIc /*MaterialDesignIconView logIc*/) {
        this.loadDir = load;
        this.saveDir = save;
        this.gpuMode = gpuMode;
        this.workspaceMode = workspaceMode;
        this.trainIc = trainIc;
        this.consoleIc = consoleIc;
        //this.logIc = logIc;
    }
    
    public void set (String prop, String value) {
        prefs.put(prop, value);
    }
    
    public String get(String prop) {
        return prefs.get(prop, null);
    }
    
    public void init () {
        
        prefs = Preferences.userRoot()
                           .node("Neuronix");
        
        setCurrentLoadDir(prefs.get("loadDir", FilenameUtils.concat(System.getProperty("user.dir"),
                                                                    "pre_trained")));
        setCurrentSaveDir(
            prefs.get("saveDir", FilenameUtils.concat(System.getProperty("user.dir"), "saved")));
        setCurrentSaveState(prefs.getBoolean("saveState", true));
        setCurrentConsoleState(prefs.getBoolean("consoleState", false));
        //setCurrentLogState(prefs.getBoolean("logState", true));
        setCurrentGpuState(prefs.getBoolean("gpuState", false));
        setCurrentWorkspaceState(prefs.getBoolean("workspaceState", false));
        
        loadDir.textProperty()
               .bind(this.currentLoadDirProperty());
        saveDir.textProperty()
               .bind(this.currentSaveDirProperty());
        save.selectedProperty()
            .bindBidirectional(this.currentSaveStateProperty());
        console.selectedProperty()
               .bindBidirectional(this.currentConsoleStateProperty());
//        log.selectedProperty()
//           .bindBidirectional(this.currentLogStateProperty());
        gpu.selectedProperty()
           .bindBidirectional(this.currentGpuStateProperty());
        workspace.selectedProperty()
                 .bindBidirectional(this.currentWorkspaceStateProperty());
        
    }
    
}