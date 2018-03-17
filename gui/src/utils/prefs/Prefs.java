package utils.prefs;

import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXToggleButton;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.prefs.Preferences;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.text.Text;
import org.apache.commons.io.FilenameUtils;
import utils.Utils;

public class Prefs {
    
    private Preferences prefs;
    
    private JFXCheckBox     save;
    private JFXToggleButton workspace;
    
    private Text loadDir, saveDir;
    
    private MaterialDesignIconView workspaceMode;
    private MaterialDesignIconView trainIc;
    private StringProperty  currentLoadDir        = new SimpleStringProperty();
    private StringProperty  currentSaveDir        = new SimpleStringProperty();
    private BooleanProperty currentSaveState      = new SimpleBooleanProperty();
    private BooleanProperty currentWorkspaceState = new SimpleBooleanProperty();
    
    
    public Prefs (JFXCheckBox save,
                  JFXToggleButton workspace) {
        this.save = save;
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
    
    
    public void toUpdate (Text load, Text save,
                          MaterialDesignIconView workspaceMode, MaterialDesignIconView trainIc
                          ) {
        this.loadDir = load;
        this.saveDir = save;
        this.workspaceMode = workspaceMode;
        this.trainIc = trainIc;
     
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
        setCurrentWorkspaceState(prefs.getBoolean("workspaceState", false));
        
        loadDir.textProperty()
               .bind(this.currentLoadDirProperty());
        saveDir.textProperty()
               .bind(this.currentSaveDirProperty());
        save.selectedProperty()
            .bindBidirectional(this.currentSaveStateProperty());
        workspace.selectedProperty()
                 .bindBidirectional(this.currentWorkspaceStateProperty());
        
    }
    
}