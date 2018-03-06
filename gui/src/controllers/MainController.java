package controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;

import eu.hansolo.medusa.Gauge;
import eu.hansolo.medusa.GaugeBuilder;
import eu.hansolo.tilesfx.Tile;
import eu.hansolo.tilesfx.TileBuilder;
import eu.hansolo.tilesfx.chart.ChartData;
import eu.hansolo.tilesfx.tools.FlowGridPane;
import io.reactivex.Observable;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.paint.Stop;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.RxLogger;
import utils.Utils;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryUsage;
import com.sun.management.OperatingSystemMXBean;
import java.net.URL;

import java.util.ResourceBundle;
import java.util.Stack;

import static utils.Utils.createGauge;


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
    @FXML
    private Pane flowScores;

    //Console Text Area
    @FXML
    private JFXTextArea console;

    //Button names
    private final String CONFIG         = "Налаштування";
    private final String TRAINING       = "Навчання";
    private final String CLASSIFICATION = "Класифікація";

    //Logger
    protected final Logger log = LoggerFactory.getLogger(MainController.class);

    //Stack for switching the order of the panes
    Stack<AnchorPane> stackS2 = new Stack<>();
    Stack<AnchorPane> stackS3 = new Stack<>();

    //Gauges
    private Gauge   trainingGauge, accuracyGauge, precisionGauge,
                    recallGauge, f1Gauge;
    private Tile    trainingTile,ramUsesTile, accuracyTile, precisionTile,
                    recallTile, f1Tile;

    //Transition for pane animations
    TranslateTransition openNav3;
    TranslateTransition openNav2;

    //Bean for getting info about system
    OperatingSystemMXBean bean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        trainingGauge = createGauge(Gauge.SkinType.SLIM, 255, 100.00, "%");
        trainingTile  = TileBuilder.create()
                .prefSize(255, 255)
                .backgroundColor(Color.web("#755c62"))
                .skinType(Tile.SkinType.CUSTOM)
                .title("Процес навчання")
                .titleAlignment(TextAlignment.CENTER)
                .startFromZero(true)
                .graphic(trainingGauge)
                .build();

        ramUsesTile  = TileBuilder.create()
                .prefSize(255, 255)
                .skinType(Tile.SkinType.HIGH_LOW)
                .backgroundColor(Color.web("#755c62"))
                .title("Використання ОЗУ")
                .titleAlignment(TextAlignment.CENTER)
                .unit("GB")
                .minValue(0)
                .maxValue(bean.getTotalPhysicalMemorySize() / 1048576.00 / 1024.00)
                .build();

        accuracyGauge = createGauge(Gauge.SkinType.SPACE_X, 127, 100.00, "%");
        accuracyTile  = TileBuilder.create()
                .prefSize(127, 127)
                .skinType(Tile.SkinType.CUSTOM)
                .backgroundColor(Color.web("#755c62"))
                .title("Accuracy")
                .titleAlignment(TextAlignment.CENTER)
                .startFromZero(true)
                .textSize(Tile.TextSize.BIGGER)
                .graphic(accuracyGauge)
                .build();

        precisionGauge = createGauge(Gauge.SkinType.SPACE_X, 127, 100.00, "%");
        precisionTile  = TileBuilder.create()
                .prefSize(127, 127)
                .skinType(Tile.SkinType.CUSTOM)
                .backgroundColor(Color.web("#755c62"))
                .title("Precision")
                .titleAlignment(TextAlignment.CENTER)
                .startFromZero(true)
                .textSize(Tile.TextSize.BIGGER)
                .graphic(precisionGauge)
                .build();

        recallGauge = createGauge(Gauge.SkinType.SPACE_X, 127, 100.00, "%");
        recallTile  = TileBuilder.create()
                .prefSize(127, 127)
                .skinType(Tile.SkinType.CUSTOM)
                .backgroundColor(Color.web("#755c62"))
                .title("Recall")
                .titleAlignment(TextAlignment.CENTER)
                .startFromZero(true)
                .textSize(Tile.TextSize.BIGGER)
                .graphic(recallGauge)
                .build();

        f1Gauge = createGauge(Gauge.SkinType.SPACE_X, 127, 100.00, "%");
        f1Tile  = TileBuilder.create()
                .prefSize(127, 127)
                .skinType(Tile.SkinType.CUSTOM)
                .backgroundColor(Color.web("#755c62"))
                .title("F1")
                .titleAlignment(TextAlignment.CENTER)
                .startFromZero(true)
                .textSize(Tile.TextSize.BIGGER)
                .graphic(f1Gauge)
                .build();


        FlowGridPane scores = new FlowGridPane(4, 1, accuracyTile, precisionTile, recallTile, f1Tile);
        FlowGridPane ampls = new FlowGridPane(2, 1, trainingTile, ramUsesTile);

        trainPaneSection3.getChildren().add(ampls);
        flowScores.getChildren().add(scores);



        stackS2.push(defaultPaneSection2);
        stackS3.push(defaultPaneSection3);

        configPaneSection3.setTranslateX(configPaneSection3.getPrefWidth());
        trainPaneSection3.setTranslateX(trainPaneSection3.getPrefWidth());
        classifierPaneSection3.setTranslateX(classifierPaneSection3.getPrefWidth());

        configPaneSection2.setTranslateY(configPaneSection2.getPrefHeight() + 32);
        trainPaneSection2.setTranslateY(trainPaneSection2.getPrefHeight() + 32);
        classifierPaneSection2.setTranslateY(classifierPaneSection2.getPrefHeight() + 32);

        Utils.monitoreRAM(bean, ramUsesTile);

        close.setOnAction(e -> {
            Platform.exit();
        });

        minimize.setOnAction(e -> {


            Stage stage = (Stage)((JFXButton)e.getSource()).getScene().getWindow();
            stage.setIconified(true);

//            Timeline timeline = new Timeline(new KeyFrame(
//                    Duration.millis(1500),
//                    ae -> {
//                       slimGauge.setValue(slimGauge.getValue() + 7.5);
//                    }));
//            timeline.setCycleCount(Animation.INDEFINITE);
//            timeline.play();



//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    Timeline timeline = new Timeline(new KeyFrame(
//                            Duration.millis(1000),
//                            ae -> {
//                                if (slimGauge.getValue() >= 100)
//                                {
//                                    accuracyGauge.setValue(97.7);
//                                    precisionGauge.setValue(99.9);
//                                    recallGauge.setValue(95.6);
//                                    f1Gauge.setValue(96.01);
//                                    slimGauge.stop();
//
//                                }
//                                else
//                                    slimGauge.setValue(slimGauge.getValue() + 1.15);
//
//
//                            }));
//                    timeline.setCycleCount(Animation.INDEFINITE);
//                    timeline.play();
//                }
//            }).run();
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    Timeline timeline = new Timeline(new KeyFrame(
//                            Duration.millis(2500),
//                            ae -> {
//
//                                long totalMem = bean.getTotalPhysicalMemorySize();
//                                long freeMem = bean.getFreePhysicalMemorySize();
//                                double usageMem = (totalMem - freeMem) / 1048576.00; // in GB
//
//                                ramUsesTile.setValue(usageMem / 1024.00);
//                            }));
//                    timeline.setCycleCount(Animation.INDEFINITE);
//                    timeline.play();
//                }
//            }).run();
        });

        config.setOnAction(e -> {
            Utils.switchPane(   configPaneSection2, configPaneSection3,
                                stackS2, stackS3,
                                CONFIG, "Ntcn",
                                descriptionOfSecondSection, descriptionOfThirdSection, openNav3, openNav2   );
        });

        train.setOnAction(e -> {
            Utils.switchPane(   trainPaneSection2, trainPaneSection3,
                                stackS2, stackS3,
                                TRAINING, "sdf",
                                descriptionOfSecondSection, descriptionOfThirdSection, openNav3, openNav2   );
        });

        classifier.setOnAction(e -> {
            Utils.switchPane(   classifierPaneSection2, classifierPaneSection3,
                                stackS2, stackS3,
                                CLASSIFICATION, "Ntcn",
                                descriptionOfSecondSection, descriptionOfThirdSection, openNav3, openNav2    );
        });

    }

}