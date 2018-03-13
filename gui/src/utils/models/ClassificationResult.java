package utils.models;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;


public class ClassificationResult {
    
    private StringProperty imageName;
    private StringProperty email;
    
    public ClassificationResult (String imageName, String email) {
        this.imageName = new SimpleStringProperty(imageName);
        this.email = new SimpleStringProperty(email);
    }
    
    public String getImageName () {
        return imageName.get();
    }
    
    public StringProperty imageNameProperty () {
        return imageName;
    }
    
    public String getEmail () {
        return email.get();
    }
    
    public StringProperty emailProperty () {
        return email;
    }
}