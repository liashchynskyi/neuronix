package controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;

import eu.hansolo.medusa.Gauge;
import eu.hansolo.medusa.GaugeBuilder;
import eu.hansolo.tilesfx.Tile;
import eu.hansolo.tilesfx.TileBuilder;
import eu.hansolo.tilesfx.tools.FlowGridPane;
import io.reactivex.Observable;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.TextAlignment;
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
    @FXML
    private Pane flowScores;

    private Gauge slimGauge;
    private Tile slimTile;
    private Gauge slimGauge2, slimGauge3, slimGauge4, slimGauge5, slimGauge6;
    private Tile slimTile2, slimTile3, slimTile4, slimTile5, slimTile6;




    Stack<AnchorPane> stackS2 = new Stack<>();
    Stack<AnchorPane> stackS3 = new Stack<>();

    OperatingSystemMXBean bean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);
    private Gauge createGauge(final Gauge.SkinType TYPE, int size, double max, String unit) {
        return GaugeBuilder.create()
                .skinType(TYPE)
                .prefSize(size, size)
                .animated(true)
                .unit(unit)
                .valueColor(Color.web("#ffffff"))
                .unitColor(Color.web("#ffffff"))
                .barColor(Tile.ORANGE)
                .needleColor(Tile.FOREGROUND)
                .barBackgroundColor(Color.web("#8e4a49"))
                .tickLabelColor(Tile.GRAY)
                .majorTickMarkColor(Tile.FOREGROUND)
                .minorTickMarkColor(Tile.FOREGROUND)
                .mediumTickMarkColor(Tile.FOREGROUND)
                .backgroundPaint(Paint.valueOf("#755c62"))
                .maxValue(max)
                .build();
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        slimGauge = createGauge(Gauge.SkinType.SLIM, 255, 100.00, "%");
        slimTile  = TileBuilder.create()
                .prefSize(255, 255)
                .backgroundColor(Color.web("#755c62"))
                .skinType(Tile.SkinType.CUSTOM)
                .title("Процес навчання")
                .titleAlignment(TextAlignment.CENTER)
                .startFromZero(true)
                .graphic(slimGauge)
                .build();
        slimGauge2 = createGauge(Gauge.SkinType.SLIM, 255, bean.getTotalPhysicalMemorySize() / 1048576.00 / 1024.00, "GB");
        slimTile2  = TileBuilder.create()
                .prefSize(255, 255)
                .skinType(Tile.SkinType.CUSTOM)
                .backgroundColor(Color.web("#755c62"))
                .title("Використання ОЗУ")
                .titleAlignment(TextAlignment.CENTER)
                .startFromZero(false)
                .graphic(slimGauge2)
                .build();

        slimGauge3 = createGauge(Gauge.SkinType.SLIM, 127, 100.00, "%");
        slimTile3  = TileBuilder.create()
                .prefSize(127, 127)
                .skinType(Tile.SkinType.CUSTOM)
                .backgroundColor(Color.web("#755c62"))
                .title("Accuracy")
                .titleAlignment(TextAlignment.CENTER)
                .startFromZero(true)
                .textSize(Tile.TextSize.BIGGER)
                .graphic(slimGauge3)
                .build();

        slimGauge4 = createGauge(Gauge.SkinType.SLIM, 127, 100.00, "%");
        slimTile4  = TileBuilder.create()
                .prefSize(127, 127)
                .skinType(Tile.SkinType.CUSTOM)
                .backgroundColor(Color.web("#755c62"))
                .title("Precision")
                .titleAlignment(TextAlignment.CENTER)
                .startFromZero(true)
                .textSize(Tile.TextSize.BIGGER)
                .graphic(slimGauge4)
                .build();
        slimGauge5 = createGauge(Gauge.SkinType.SLIM, 127, 100.00, "%");
        slimTile5  = TileBuilder.create()
                .prefSize(127, 127)
                .skinType(Tile.SkinType.CUSTOM)
                .backgroundColor(Color.web("#755c62"))
                .title("Recall")
                .titleAlignment(TextAlignment.CENTER)
                .startFromZero(true)
                .textSize(Tile.TextSize.BIGGER)
                .graphic(slimGauge5)
                .build();

        slimGauge6 = createGauge(Gauge.SkinType.SLIM, 127, 100.00, "%");
        slimTile6  = TileBuilder.create()
                .prefSize(127, 127)
                .skinType(Tile.SkinType.CUSTOM)
                .backgroundColor(Color.web("#755c62"))
                .title("F1")
                .titleAlignment(TextAlignment.CENTER)
                .startFromZero(true)
                .textSize(Tile.TextSize.BIGGER)
                .graphic(slimGauge6)
                .build();

        FlowGridPane scores = new FlowGridPane(4, 1, slimTile3, slimTile4, slimTile5, slimTile6);
        FlowGridPane ampls = new FlowGridPane(2, 1, slimTile, slimTile2);

        trainPaneSection3.getChildren().add(ampls);
        flowScores.getChildren().add(scores);




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

//            Timeline timeline = new Timeline(new KeyFrame(
//                    Duration.millis(1500),
//                    ae -> {
//                       slimGauge.setValue(slimGauge.getValue() + 7.5);
//                    }));
//            timeline.setCycleCount(Animation.INDEFINITE);
//            timeline.play();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Timeline timeline = new Timeline(new KeyFrame(
                            Duration.millis(1000),
                            ae -> {
                                if (slimGauge.getValue() == 100)
                                {
                                    slimGauge3.setValue(97.7);
                                    slimGauge4.setValue(99.9);
                                    slimGauge5.setValue(95.6);
                                    slimGauge6.setValue(96.01);
                                    slimGauge.stop();

                                }
                                else
                                    slimGauge.setValue(slimGauge.getValue() + 20);


                            }));
                    timeline.setCycleCount(Animation.INDEFINITE);
                    timeline.play();
                }
            }).run();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Timeline timeline = new Timeline(new KeyFrame(
                            Duration.millis(100),
                            ae -> {

                                long totalMem = bean.getTotalPhysicalMemorySize();
                                long freeMem = bean.getFreePhysicalMemorySize();
                                double usageMem = (totalMem - freeMem) / 1048576.00; // in GB

                                slimGauge2.setValue(usageMem / 1024.00);
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
            Utils.switchPane(   trainPaneSection2, trainPaneSection3,
                                stackS2, stackS3,
                                TRAINING, "sdf",
                                descriptionOfSecondSection, descriptionOfThirdSection   );
        });

        classifier.setOnAction(e -> {
            Utils.switchPane(   classifierPaneSection2, classifierPaneSection3,
                                stackS2, stackS3,
                                CLASSIFICATION, "Ntcn",
                                descriptionOfSecondSection, descriptionOfThirdSection   );
        });

    }

}