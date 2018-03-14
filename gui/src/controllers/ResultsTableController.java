package controllers;

import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import utils.models.ClassificationResult;

public class ResultsTableController {
  private TableView<ClassificationResult> table;
    
    public ResultsTableController (TableView table) {
        this.table = table;
        TableColumn<ClassificationResult, String> fileName = new TableColumn<>("Ім'я файлу");
        TableColumn<ClassificationResult, String> resultslist = new TableColumn<>("Список результатів");
        TableColumn<ClassificationResult, String> result = new TableColumn<>("Результат");
    
        fileName.setCellValueFactory((cell) -> {
            return cell.getValue().fileNameProperty();
        });
        resultslist.setCellValueFactory((cell) -> {
            return cell.getValue().arrResultsProperty();
        });
        result.setCellValueFactory((cell) -> {
            return cell.getValue().resultProperty();
        });
        
        this.table.getColumns().addAll(fileName, resultslist, result);
        
    }
    
    public void populateTable (ObservableList<ClassificationResult> list) {
        this.table.getItems().addAll(list);
    }
    
    public ObservableList<ClassificationResult> getItems () {
        return this.table.getItems();
    }
    
}