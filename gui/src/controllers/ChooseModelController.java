package controllers;

import com.jfoenix.controls.JFXComboBox;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import utils.prefs.Prefs;

public class ChooseModelController {
    
    private Prefs       prefs;
    private JFXComboBox comboBoxSavedModel, comboBoxLoadedModel;
    private String currentSavedModel, currentClassifierModel;
    
    public ChooseModelController (Prefs prefs, JFXComboBox savedCombo, JFXComboBox loadCombo) {
        this.prefs = prefs;
        this.comboBoxSavedModel = savedCombo;
        this.comboBoxLoadedModel = loadCombo;
    }
    
    public List<String> getModels (boolean saved) {
        String modelsDir;
        if (saved) { modelsDir = prefs.getCurrentSaveDir(); }
        else { modelsDir = prefs.getCurrentLoadDir(); }
        List<String> results = new ArrayList<>();
        File[]       files   = new File(modelsDir).listFiles();
        for (File file : files) {
            if (file.isFile()) {
                results.add(file.getName());
            }
        }
        return results;
    }
    
    public void populateSavedCombo () {
        comboBoxSavedModel.getItems()
                          .addAll(getModels(true));
        comboBoxSavedModel.valueProperty()
                          .addListener((o, t, t1) -> {
                              this.currentSavedModel = o.getValue()
                                                        .toString();
                              System.out.println(this.getCurrentSavedModel());
                          });
        
    }
    
    public void populateLoadedCombo () {
        comboBoxLoadedModel.getItems().addAll(getModels(false));
        comboBoxLoadedModel.valueProperty().addListener((o, t, t1) -> {
            this.currentClassifierModel = o.getValue().toString();
            System.out.println(this.getCurrentClassifierModel());
        });
    }
    
    public String getCurrentSavedModel () {
        return currentSavedModel;
    }
    
    public String getCurrentClassifierModel () {
        return currentClassifierModel;
    }
}