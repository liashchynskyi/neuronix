package utils;

import com.jfoenix.controls.JFXButton;
import java.util.HashMap;
import java.util.Map;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

public class Mapper {
    private static Map<String, JFXButton> buttonsMap = new HashMap<>();
    private static Map<String, AnchorPane> aPanesMap = new HashMap<>();
    private static Map<String, Label>     labelsMap  = new HashMap<>();
    
    public static void putButton(String key, JFXButton button) {
        buttonsMap.put(key, button);
    }
    
    public static JFXButton getButton(String key) {
        return buttonsMap.get(key);
    }
    
    public static void putAnchorPane(String key, AnchorPane pane) {
        aPanesMap.put(key, pane);
    }
    
    public static AnchorPane getAPane(String key) {
        return aPanesMap.get(key);
    }
    
    public static void putLabel(String key, Label label) {
        labelsMap.put(key, label);
    }
    
    public static Label getLabel(String key) {
        return labelsMap.get(key);
    }
    
} 