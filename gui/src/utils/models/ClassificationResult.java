package utils.models;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;


public class ClassificationResult extends RecursiveTreeObject<ClassificationResult> {
    
    private ListProperty<String> results   = new SimpleListProperty();
    private StringProperty       className = new SimpleStringProperty();
    
    public ClassificationResult (ObservableList<String> results, String className) {
        this.setResults(results);
        this.setClassName(className);
    }
    
    public ObservableList<String> getResults () {
        return results.get();
    }
    
    public void setResults (ObservableList<String> results) {
        this.results.set(results);
    }
    
    public ListProperty<String> resultsProperty () {
        return results;
    }
    
    public String getClassName () {
        return className.get();
    }
    
    public void setClassName (String className) {
        this.className.set(className);
    }
    
    public StringProperty classNameProperty () {
        return className;
    }
}