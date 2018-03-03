package controllers;

import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;


public class MainController implements Initializable {

    @FXML
    private JFXButton close,
                      minimize;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        close.setOnAction(e -> {
            System.exit(0);
        });

        minimize.setOnAction(e -> {
            Stage stage = (Stage)((JFXButton)e.getSource()).getScene().getWindow();
            stage.setIconified(true);
        });
    }

}