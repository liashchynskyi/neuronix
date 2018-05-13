package neuronix.config;

import java.util.prefs.Preferences;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Provides the local service to store configuration data
 * @author Petro Liashchynskyi
 */

public class Prefs {
    
    private static Preferences prefs;
    private static StringProperty  currentLoadDir        = new SimpleStringProperty();
    private static StringProperty  currentSaveDir        = new SimpleStringProperty();
    private static BooleanProperty currentSaveState      = new SimpleBooleanProperty();
    private static BooleanProperty currentWorkspaceState = new SimpleBooleanProperty();
    
    /**
     * Returns the path to dir where saved models are stored
     * @return the path to dir where saved models are stored
     */
    public static String getCurrentLoadDir () {
        return currentLoadDir.get();
    }
    
    /**
     * Returns property of current load dir
     * @return currentLoadDir property
     */
    public static StringProperty currentLoadDirProperty () {
        return currentLoadDir;
    }
    
    /**
     * Sets the path where saved models are stored
     * @param currentLoadDir the path to dir where saved models are stored
     */
    public static void setCurrentLoadDir (String currentLoadDir) {
        Prefs.currentLoadDir.set(currentLoadDir);
        prefs.put("loadDir", currentLoadDir);
    }
    
    /**
     * Returns the path where created json models are stored
     * @return the path where created json models are stored
     */
    public static String getCurrentSaveDir () {
        return currentSaveDir.get();
    }
    
    /**
     * Returns property of current save dir
     * @return currentSaveDir property
     */
    public static StringProperty currentSaveDirProperty () {
        return currentSaveDir;
    }
    
    /**
     * Sets the path where created json models are stored
     * @param currentSaveDir the path to dir where created json models are stored
     */
    public static void setCurrentSaveDir (String currentSaveDir) {
        Prefs.currentSaveDir.set(currentSaveDir);
        prefs.put("saveDir", currentSaveDir);
    }
    
    /**
     * Returns current state of saving models
     * @return boolean
     */
    public static boolean isCurrentSaveState () {
        return currentSaveState.get();
    }
    
    /**
     * Returns property of current save state
     * @return currentSaveState property
     */
    public static BooleanProperty currentSaveStateProperty () {
        return currentSaveState;
    }
    
    /**
     * Sets current save state
     * @param currentSaveState if true - model will be saved after training
     */
    public static void setCurrentSaveState (boolean currentSaveState) {
        Prefs.currentSaveState.set(currentSaveState);
        prefs.putBoolean("saveState", currentSaveState);
    }
    
    /**
     * Gets current workspace state
     * @return currentWorkspaceState
     */
    public static boolean isCurrentWorkspaceState () {
        return currentWorkspaceState.get();
    }
    
    /**
     * Returns property of current workspace state
     * @return currentWorkspaceState property
     */
    public static BooleanProperty currentWorkspaceStateProperty () {
        return currentWorkspaceState;
    }
    
    /**
     * Sets current workspace state
     * @param currentWorkspaceState if true - Workspace mode is SINGLE
     */
    public static void setCurrentWorkspaceState (boolean currentWorkspaceState) {
        Prefs.currentWorkspaceState.set(currentWorkspaceState);
        prefs.putBoolean("workspaceState", currentWorkspaceState);
    }
    
    /**
     * Sets another properties
     * @param prop property name
     * @param value property value
     */
    public void set (String prop, String value) {
        prefs.put(prop, value);
    }
    
    /**
     * Gets property by name
     * @param prop property name
     * @return property value
     */
    public String get(String prop) {
        return prefs.get(prop, null);
    }
    
}