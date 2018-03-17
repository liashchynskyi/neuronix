package controllers;

import static utils.Utils.createGauge;

import com.alibaba.fastjson.JSON;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXProgressBar;
import com.jfoenix.controls.JFXSlider;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXToggleButton;
import com.sun.management.OperatingSystemMXBean;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import eu.hansolo.medusa.Gauge;
import eu.hansolo.tilesfx.Tile;
import eu.hansolo.tilesfx.TileBuilder;
import eu.hansolo.tilesfx.tools.FlowGridPane;
import java.io.File;
import java.lang.management.ManagementFactory;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Stack;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.Utils;
import utils.models.ClassificationResult;
import utils.prefs.Prefs;


public class MainController implements Initializable {
    
    //Logger
    protected final Logger log = LoggerFactory.getLogger(MainController.class);
    
    //Button names
    private final String CONFIG             = "Налаштування";
    private final String TRAINING           = "Навчання";
    private final String CLASSIFICATION     = "Класифікація";
    private final String TRAINING_PR        = "Процес навчання";
    private final String RAM_USES           = "Використання ОЗУ";
    private final String CONFIG_DESC        = "Опис налаштувань";
    private final String CURRENT_CONF       = "Поточні налаштування";
    private final String CLASSIFICATION_RES = "Результати класифікації";
    
    //Stack for switching the order of the panes
    Stack<AnchorPane> stackS2 = new Stack<>();
    Stack<AnchorPane> stackS3 = new Stack<>();
    
    //Transition for pane animations
    TranslateTransition openNav3;
    TranslateTransition openNav2;
    
    //Bean for getting info about system
    OperatingSystemMXBean bean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);
    
    //Buttons
    @FXML
    private JFXButton   close,
                        minimize,
                        config,
                        train,
                        classifier,
                        displayConfig,
                        loadPreTrained,
                        loadCreated,
                        loadImages,
                        runForestRun,
                        chdirWithImages,
                        chSingleImage,
                        classifyForest;
    
    //Checkboxes
    @FXML
    private JFXCheckBox saveModelAfterTraining;
    
    //Toggle Buttons
    @FXML
    private JFXToggleButton workspaceMode;
    
    //Labels
    @FXML
    private Label   appName,
                    descriptionOfThirdSection,
                    descriptionOfSecondSection,
                    projectDescription,
                    appNameL,
                    trainingLabelProgress;
    
    //ComboBoxes
    @FXML
    private JFXComboBox chooseModel, chooseModelClassifier;
    
    //Text Fields
    @FXML
    private JFXTextField    epochsNumber,
                            iterNumber,
                            learningRateNumber;
    
    //Slider
    @FXML
    private JFXSlider splitTrainTest;
    
    //Progress
    @FXML
    private JFXProgressBar progressForestClassify;
    
    //Hyperlinks
    @FXML
    private Hyperlink developer, copyright, github, facebook, linkedin;
    
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
                        classifierPaneSection3,
                        displayConfigPaneSection3,
                        displayClassificationResults;
    @FXML
    private Pane flowScores;
    
    //Text Display Conf
    @FXML
    private Text displayLoadDir, displaySaveDir;
    
    //Table
    @FXML
    private TableView<ClassificationResult> tableResults;
    
    //Icons
    @FXML
    private MaterialDesignIconView  workspaceModeIcon,
                                    trainIcon;
    
    //Console Text Area
    @FXML
    private JFXTextArea console;
    
    //Gauges
    private Gauge trainingGauge, accuracyGauge, precisionGauge, recallGauge, f1Gauge;
    private Tile trainingTile, ramUsesTile, accuracyTile, precisionTile, recallTile, f1Tile;
    
    
    @Override
    public void initialize (URL location, ResourceBundle resources) {
        
        Prefs prefs = new Prefs(saveModelAfterTraining,
                                workspaceMode);
        prefs.toUpdate(displayLoadDir, displaySaveDir, workspaceModeIcon, trainIcon);
        prefs.init();
    
        ChooseModelController chooseModelController = new ChooseModelController(prefs, chooseModel, chooseModelClassifier);
        chooseModelController.populateSavedCombo();
        chooseModelController.populateLoadedCombo();
        
        initializeGauges();
        TrainingController trainingController = new TrainingController(chooseModelController, epochsNumber, iterNumber, learningRateNumber, splitTrainTest,
                                                                       trainingGauge, log, prefs, trainingLabelProgress);
        
        
        defaultPaneSection2.toFront();
        defaultPaneSection3.toFront();
        
        stackS2.push(defaultPaneSection2);
        stackS3.push(defaultPaneSection3);
        
        configPaneSection3.setTranslateX(configPaneSection3.getPrefWidth());
        displayConfigPaneSection3.setTranslateX(displayConfigPaneSection3.getPrefWidth());
        trainPaneSection3.setTranslateX(trainPaneSection3.getPrefWidth());
        classifierPaneSection3.setTranslateX(classifierPaneSection3.getPrefWidth());
        displayClassificationResults.setTranslateX(displayClassificationResults.getPrefWidth());
        
        configPaneSection2.setTranslateY(configPaneSection2.getPrefHeight() + 32);
        trainPaneSection2.setTranslateY(trainPaneSection2.getPrefHeight() + 32);
        classifierPaneSection2.setTranslateY(classifierPaneSection2.getPrefHeight() + 32);
        
        Utils.monitoreRAM(bean, ramUsesTile);
        
        close.setOnAction(e -> {
            Platform.exit();
        });
        
        minimize.setOnAction(e -> {
            Stage stage = (Stage) ((JFXButton) e.getSource()).getScene()
                                                             .getWindow();
            stage.setIconified(true);
        });
        
        config.setOnAction(e -> {
            Utils.switchPane(configPaneSection2, configPaneSection3, stackS2, stackS3, CONFIG,
                             CONFIG_DESC, descriptionOfSecondSection, descriptionOfThirdSection,
                             openNav3, openNav2);
        });
        
        displayConfig.setOnAction(e -> {
            Utils.switch3Pane(displayConfigPaneSection3, stackS3, CURRENT_CONF,
                              descriptionOfThirdSection, openNav3);
        });
        
        train.setOnAction(e -> {
            Utils.switchPane(trainPaneSection2, trainPaneSection3, stackS2, stackS3, TRAINING,
                             TRAINING_PR, descriptionOfSecondSection, descriptionOfThirdSection,
                             openNav3, openNav2);
        });
        
        classifier.setOnAction(e -> {
            Utils.switchPane(classifierPaneSection2, classifierPaneSection3, stackS2, stackS3,
                             CLASSIFICATION, CLASSIFICATION_RES, descriptionOfSecondSection,
                             descriptionOfThirdSection, openNav3, openNav2);
        });
        
        loadPreTrained.setOnAction(e -> {
            DirectoryChooser directoryChooser  = new DirectoryChooser();
            File             selectedDirectory = directoryChooser.showDialog(new Stage());
            prefs.setCurrentLoadDir(selectedDirectory.getAbsolutePath());
        });
        
        loadCreated.setOnAction(e -> {
            DirectoryChooser directoryChooser  = new DirectoryChooser();
            File             selectedDirectory = directoryChooser.showDialog(new Stage());
            prefs.setCurrentSaveDir(selectedDirectory.getAbsolutePath());
        });
        
        saveModelAfterTraining.setOnAction(e -> {
            if (saveModelAfterTraining.isSelected()) {
                prefs.setCurrentSaveState(true);
            }
            else {
                prefs.setCurrentSaveState(false);
            }
        });
        
        workspaceMode.setOnAction(e -> {
            if (workspaceMode.isSelected()) {
                prefs.setCurrentWorkspaceState(true);
                workspaceMode.setText("SIN");
            }
            else {
                prefs.setCurrentWorkspaceState(false);
                workspaceMode.setText("SEP");
            }
        });
        
        loadImages.setOnAction(e -> {
            DirectoryChooser directoryChooser  = new DirectoryChooser();
            File             selectedDirectory = directoryChooser.showDialog(new Stage());
            trainingController.setImagesPath(selectedDirectory.getAbsolutePath());
        });
        
        runForestRun.setOnAction(e -> {
            trainingController.train(accuracyGauge, precisionGauge, recallGauge, f1Gauge);
        });
        
    }
    
    public void initializeGauges () {
        trainingGauge = createGauge(Gauge.SkinType.SLIM, 255, 100.00, "%");
        trainingTile = TileBuilder.create()
                                  .prefSize(255, 255)
                                  .backgroundColor(Color.web("#755c62"))
                                  .skinType(Tile.SkinType.CUSTOM)
                                  .title(TRAINING_PR)
                                  .titleAlignment(TextAlignment.CENTER)
                                  .startFromZero(true)
                                  .graphic(trainingGauge)
                                  .build();
        
        ramUsesTile = TileBuilder.create()
                                 .prefSize(255, 255)
                                 .skinType(Tile.SkinType.HIGH_LOW)
                                 .backgroundColor(Color.web("#755c62"))
                                 .title(RAM_USES)
                                 .titleAlignment(TextAlignment.CENTER)
                                 .unit("GB")
                                 .minValue(0)
                                 .maxValue(bean.getTotalPhysicalMemorySize() / 1048576.00 / 1024.00)
                                 .build();
        
        accuracyGauge = createGauge(Gauge.SkinType.SPACE_X, 127, 100.00, "%");
        accuracyTile = TileBuilder.create()
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
        precisionTile = TileBuilder.create()
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
        recallTile = TileBuilder.create()
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
        f1Tile = TileBuilder.create()
                            .prefSize(127, 127)
                            .skinType(Tile.SkinType.CUSTOM)
                            .backgroundColor(Color.web("#755c62"))
                            .title("F1")
                            .titleAlignment(TextAlignment.CENTER)
                            .startFromZero(true)
                            .textSize(Tile.TextSize.BIGGER)
                            .graphic(f1Gauge)
                            .build();
        
        
        FlowGridPane scores = new FlowGridPane(4, 1, accuracyTile, precisionTile, recallTile,
                                               f1Tile);
        FlowGridPane ampls = new FlowGridPane(2, 1, trainingTile, ramUsesTile);
        
        trainPaneSection3.getChildren()
                         .add(ampls);
        flowScores.getChildren()
                  .add(scores);
    }
    
}