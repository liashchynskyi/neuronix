package controllers;

import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.text.Text;
import utils.models.ClassificationResult;

public class ResultsTableController {
  private TableView<ClassificationResult> table;
  private Text availableLabels;
    
    public ResultsTableController (TableView table, Text availableLabels) {
        this.table = table;
        this.availableLabels = availableLabels;
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
    
    public void populateTable (ObservableList<ClassificationResult> list, String text) {
        this.table.getItems().clear();
        this.table.getItems().addAll(list);
        this.availableLabels.setText(text);
    }
    
    public ObservableList<ClassificationResult> getItems () {
        return this.table.getItems();
    }
    
}