package controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;
import io.reactivex.Observable;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.RxLogger;
import utils.Utils;

import java.net.URL;

import java.util.ResourceBundle;
import java.util.Stack;



public class MainController implements Initializable {
    //Buttons
    @FXML
    private JFXButton   close,
                        minimize,
                        config,
                        train,
                        classifier;

    //Labels
    @FXML
    private Label   appName,
                    descriptionOfThirdSection,
                    descriptionOfSecondSection,
                    projectDescription,
                    appNameL;


    //Hyperlinks
    @FXML
    private Hyperlink   developer,
                        copyright,
                        github,
                        facebook,
                        linkedin;

    //Panes
    @FXML
    private AnchorPane  menuPane,
                        defaultPaneSection2,
                        defaultPaneSection3,
                        configPaneSection2,
                        configPaneSection3,
                        trainPaneSection2,
                        trainPaneSection3,
                        classifierPaneSection2,
                        classifierPaneSection3;

    //Console Text Area
    @FXML
    private JFXTextArea console;

    //Button names
    private final String CONFIG         = "Налаштування";
    private final String TRAINING       = "Навчання";
    private final String CLASSIFICATION = "Класифікація";

    //Logger
    protected final Logger log = LoggerFactory.getLogger(MainController.class);
    //protected RxLogger rx = new RxLogger(log);
    //Stack for switching the order of the panes
    public int counter = 0;
    Stack<AnchorPane> stackS2 = new Stack<>();
    Stack<AnchorPane> stackS3 = new Stack<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Utils.setLogger(console);

//        Utils.logProperty().addListener((observable, oldValue, newValue) -> {
//            Platform.runLater(new Runnable() {
//                @Override
//                public void run() {
//                    console.appendText(newValue + "\n");
//                }
//            });
//        });

        stackS2.push(defaultPaneSection2);
        stackS3.push(defaultPaneSection3);

        close.setOnAction(e -> {
            Platform.exit();
        });

        minimize.setOnAction(e -> {
//            Stage stage = (Stage)((JFXButton)e.getSource()).getScene().getWindow();
//            stage.setIconified(true);

            Timeline timeline = new Timeline(new KeyFrame(
                    Duration.millis(2500),
                    ae -> {
                       log.info("sd");
                    }));
            timeline.setCycleCount(Animation.INDEFINITE);
            timeline.play();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Timeline timeline = new Timeline(new KeyFrame(
                            Duration.millis(1000),
                            ae -> {
                                log.info("sk");
                            }));
                    timeline.setCycleCount(Animation.INDEFINITE);
                    timeline.play();
                }
            }).run();
        });

        config.setOnAction(e -> {
            Utils.switchPane(   configPaneSection2, configPaneSection3,
                                stackS2, stackS3,
                                CONFIG, "Ntcn",
                                descriptionOfSecondSection, descriptionOfThirdSection   );
        });

        train.setOnAction(e -> {
            Utils.switchPane(   configPaneSection2, configPaneSection3,
                                stackS2, stackS3,
                                TRAINING, "sdf",
                                descriptionOfSecondSection, descriptionOfThirdSection   );
        });

        classifier.setOnAction(e -> {
            Utils.switchPane(   configPaneSection2, configPaneSection3,
                                stackS2, stackS3,
                                CLASSIFICATION, "Ntcn",
                                descriptionOfSecondSection, descriptionOfThirdSection   );
        });

    }

}