package neuronix.config;

import java.util.prefs.Preferences;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Prefs {
    
    private static Preferences prefs;
    private static StringProperty  currentLoadDir        = new SimpleStringProperty();
    private static StringProperty  currentSaveDir        = new SimpleStringProperty();
    private static BooleanProperty currentSaveState      = new SimpleBooleanProperty();
    private static BooleanProperty currentWorkspaceState = new SimpleBooleanProperty();
    
    public static String getCurrentLoadDir () {
        return currentLoadDir.get();
    }
    
    public static StringProperty currentLoadDirProperty () {
        return currentLoadDir;
    }
    
    public static void setCurrentLoadDir (String currentLoadDir) {
        Prefs.currentLoadDir.set(currentLoadDir);
        prefs.put("loadDir", currentLoadDir);
    }
    
    public static String getCurrentSaveDir () {
        return currentSaveDir.get();
    }
    
    public static StringProperty currentSaveDirProperty () {
        return currentSaveDir;
    }
    
    public static void setCurrentSaveDir (String currentSaveDir) {
        Prefs.currentSaveDir.set(currentSaveDir);
        prefs.put("saveDir", currentSaveDir);
    }
    
    public static boolean isCurrentSaveState () {
        return currentSaveState.get();
    }
    
    public static BooleanProperty currentSaveStateProperty () {
        return currentSaveState;
    }
    
    public static void setCurrentSaveState (boolean currentSaveState) {
        Prefs.currentSaveState.set(currentSaveState);
        prefs.putBoolean("saveState", currentSaveState);
    }
    
    public static boolean isCurrentWorkspaceState () {
        return currentWorkspaceState.get();
    }
    
    public static BooleanProperty currentWorkspaceStateProperty () {
        return currentWorkspaceState;
    }
    
    public static void setCurrentWorkspaceState (boolean currentWorkspaceState) {
        Prefs.currentWorkspaceState.set(currentWorkspaceState);
        prefs.putBoolean("workspaceState", currentWorkspaceState);
    }
    
    public void set (String prop, String value) {
        prefs.put(prop, value);
    }
    
    public String get(String prop) {
        return prefs.get(prop, null);
    }
    
}