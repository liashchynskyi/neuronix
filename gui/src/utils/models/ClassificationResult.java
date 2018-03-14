package utils.models;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;


public class ClassificationResult {
    
    private StringProperty fileName;
    private StringProperty arrResults;
    private StringProperty result;
    
    public ClassificationResult (StringProperty fileName, StringProperty arrResults,
                                 StringProperty result) {
        this.fileName = fileName;
        this.arrResults = arrResults;
        this.result = result;
    }
    
    public String getFileName () {
        return fileName.get();
    }
    
    public StringProperty fileNameProperty () {
        return fileName;
    }
    
    public String getArrResults () {
        return arrResults.get();
    }
    
    public StringProperty arrResultsProperty () {
        return arrResults;
    }
    
    public String getResult () {
        return result.get();
    }
    
    public StringProperty resultProperty () {
        return result;
    }
}