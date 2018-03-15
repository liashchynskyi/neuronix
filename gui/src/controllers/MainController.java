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
import com.jfoenix.controls.JFXTreeTableColumn;
import com.jfoenix.controls.JFXTreeTableRow;
import com.jfoenix.controls.JFXTreeTableView;
import com.sun.deploy.util.ArrayUtil;
import com.sun.management.OperatingSystemMXBean;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import eu.hansolo.medusa.Gauge;
import eu.hansolo.tilesfx.Tile;
import eu.hansolo.tilesfx.TileBuilder;
import eu.hansolo.tilesfx.tools.FlowGridPane;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Stack;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableColumn.CellDataFeatures;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.Utils;
import utils.models.ClassificationResult;
import utils.models.json.JsonModelBuilder;
import utils.models.json.Layer;
import utils.models.json.Model;
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
    private JFXCheckBox saveModelAfterTraining,
                        displayConsole,
                        saveLogs;
    
    //Toggle Buttons
    @FXML
    private JFXToggleButton gpuMode, workspaceMode;
    
    //Labels
    @FXML
    private Label   appName,
                    descriptionOfThirdSection,
                    descriptionOfSecondSection,
                    projectDescription,
                    appNameL;
    
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
    private MaterialDesignIconView  gpuModeIcon,
                                    workspaceModeIcon,
                                    logIcon,
                                    consoleIcon,
                                    trainIcon;
    
    //Console Text Area
    @FXML
    private JFXTextArea console;
    
    //Gauges
    private Gauge trainingGauge, accuracyGauge, precisionGauge, recallGauge, f1Gauge;
    private Tile trainingTile, ramUsesTile, accuracyTile, precisionTile, recallTile, f1Tile;
    
    
    @Override
    public void initialize (URL location, ResourceBundle resources) {
        
        //Try with table
    
//        ObservableList<ClassificationResult> list = FXCollections.observableArrayList(
//            new ClassificationResult("name1", "mailto2"),
//            new ClassificationResult("name2", "mailto2")
//           );
//
//        TableColumn<ClassificationResult, String> userNameCol //
//            = new TableColumn<ClassificationResult, String>("User Name");
//
//
//
//        // Create column Email (Data type of String).
//        TableColumn<ClassificationResult, String> emailCol//
//            = new TableColumn<ClassificationResult, String>("Email");
//
//
//        userNameCol.setCellValueFactory((cell) -> {
//            return cell.getValue().imageNameProperty();
//        });
//
//        emailCol.setCellValueFactory((cell) -> {
//            return cell.getValue().emailProperty();
//        });
//
//        tableResults.getColumns().addAll(userNameCol, emailCol);
//        tableResults.setItems(list);
//
        
//        log.info("Test logger into file");
//        log.debug("Test logger into file");
    
    
//        Model json = new Model();
//
//        Layer convInit = new Layer();
//        convInit.setId(0);
//        convInit.setName("conv1");
//        convInit.setType("conv");
//        convInit.setChannels(3);
//        convInit.setOut(50);
//        convInit.setKernel(new int[]{5, 5});
//        convInit.setStride(new int[]{1, 1});
//        convInit.setPadding(new int[]{0, 0});
//        convInit.setBias(0);
//
//
//        json.setName("LeNet");
//        json.setImageSize(224);
//        json.setChannels(3);
//        json.setLabels(Arrays.asList("label1", "label2"));
//        json.setSeed(42);
//        json.setIterations(1);
//        json.setRegularization(true);
//        json.setL2(5e-1);
//        json.setLearningRate(7e-3);
//        json.setActivation("relu");
//        json.setWeightInit("relu");
//        json.setGradientNormalization("gr");
//        json.setOptimizationAlgo("op");
//        json.setUpdater("nesterovs");
//        json.setMomentum(0.9);
//        json.setMiniBatch(true);
//        json.setLayers(Arrays.asList(convInit));
//
//        String jsonObj = JSON.toJSONString(json, true);
//        System.out.println(jsonObj);
//        try {
//            FileWriter file = new FileWriter(FilenameUtils.concat(System.getProperty("user.dir"), "gui/src/model.json"));
//            file.write(jsonObj);
//            file.close();
//            Model deser = JSON.parseObject(jsonObj, Model.class);
//            System.out.println(deser.getActivation());
//        }
//        catch (IOException e) {
//            e.printStackTrace();
//        }
//
    
//        try {
//            System.out.println(Utils.readJSON(FilenameUtils.concat(System.getProperty("user.dir"), "gui/src/model.json")));
//        }
//        catch (IOException e) {
//
//        }
    
//        try {
//            String modeljson = Utils.readJSON(FilenameUtils.concat(System.getProperty("user.dir"), "gui/src/model.json"));
//            Model encoded = Utils.decodeJson(modeljson);
//            JsonModelBuilder builder = new JsonModelBuilder(encoded);
//            MultiLayerNetwork network = builder.init().build();
//            //System.out.println(encoded.getLayers().get(5).getLoss());
//            System.out.println(network.toString());
//        }
//        catch (IOException e) {
//
//        }
    
        //End
        
        Prefs prefs = new Prefs(saveModelAfterTraining, displayConsole, /*saveLogs,*/ gpuMode,
                                workspaceMode);
        prefs.toUpdate(displayLoadDir, displaySaveDir, gpuModeIcon, workspaceModeIcon, trainIcon,
                       consoleIcon/*, logIcon*/);
        prefs.init();
    
        ChooseModelController chooseModelController = new ChooseModelController(prefs, chooseModel, chooseModelClassifier);
        chooseModelController.populateSavedCombo();
        
        
        initializeGauges();
        
//        chooseModel.getItems()
//                   .addAll("LeNet_224", "LeNet_128");
        //chooseModel.setValue("LeNet ASH67");
        
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
        
        displayConsole.setOnAction(e -> {
            if (displayConsole.isSelected()) {
                prefs.setCurrentConsoleState(true);
            }
            else {
                prefs.setCurrentConsoleState(false);
            }
        });
        
//        saveLogs.setOnAction(e -> {
//            if (saveLogs.isSelected()) {
//                prefs.setCurrentLogState(true);
//            }
//            else {
//                prefs.setCurrentLogState(false);
//            }
//        });
        
        gpuMode.setOnAction(e -> {
            if (gpuMode.isSelected()) {
                prefs.setCurrentGpuState(true);
            }
            else {
                prefs.setCurrentGpuState(false);
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