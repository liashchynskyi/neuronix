package controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXProgressBar;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Stack;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import org.apache.commons.io.FilenameUtils;
import org.datavec.api.io.filters.BalancedPathFilter;
import org.datavec.api.io.labels.ParentPathLabelGenerator;
import org.datavec.api.records.Record;
import org.datavec.api.split.FileSplit;
import org.datavec.api.split.InputSplit;
import org.datavec.image.loader.NativeImageLoader;
import org.datavec.image.recordreader.ImageRecordReader;
import org.deeplearning4j.datasets.DataSets;
import org.deeplearning4j.datasets.datavec.RecordReaderDataSetIterator;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.text.movingwindow.Util;
import org.deeplearning4j.util.ModelSerializer;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.api.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.dataset.api.preprocessor.DataNormalization;
import org.nd4j.linalg.dataset.api.preprocessor.ImagePreProcessingScaler;
import utils.Utils;
import utils.models.ClassificationResult;
import utils.models.json.Model;
import utils.prefs.Prefs;

public class ClassificationController {
    private ChooseModelController chooseModelController;
    private JFXProgressBar progressBar;
    private JFXButton classifyForest;
    private Prefs                 prefs;
    private String                imagesPath;
    private String                singleImage;
    private Random                rng = new Random(42);
    private ResultsTableController resultsTableController;
    private boolean last = false;
    private AnchorPane          pane;
    private Stack               stack;
    private TranslateTransition transition;
    private Label               label;
    
    public ClassificationController (ChooseModelController chooseModelController, ResultsTableController resultsTableController, Prefs prefs,
                                     JFXProgressBar progressBar, JFXButton classifyForest,
                                     AnchorPane pane, Stack stack, TranslateTransition transition, Label label) {
        this.chooseModelController = chooseModelController;
        this.prefs = prefs;
        this.progressBar = progressBar;
        this.classifyForest = classifyForest;
        this.resultsTableController = resultsTableController;
        this.pane = pane;
        this.stack = stack;
        this.transition = transition;
        this.label = label;
    }
    
    public String getImagesPath () {
        return imagesPath;
    }
    
    public void setImagesPath (String imagesPath) {
        this.imagesPath = imagesPath;
        last = false;
    }
    
    public String getSingleImage () {
        return singleImage;
    }
    
    public void setSingleImage (String singleImage) {
        this.singleImage = singleImage;
        last = true;
    }
    
    private void processResults(List<String> images, List<INDArray> outputList) throws Exception {
        ObservableList<ClassificationResult> classificationResults = FXCollections.observableArrayList();
    
        String currentClassifierModel = chooseModelController.getCurrentClassifierModel();
        String modelName = FilenameUtils.getBaseName(currentClassifierModel);
    
        String json =  Utils.readJSON(prefs.getCurrentSaveDir() + "\\meta\\" + modelName + ".json");
        Model meta = Utils.decodeJson(json);
    
        int idx = 0;
    
        for (INDArray el : outputList) {
            double max = el.getDouble(0);
            int index = 0;
        
            for (int i = 0; i < el.length(); i++) {
                double cur = el.getDouble(i);
                if (cur > max) {max = cur; index = i;}
            }
        
            classificationResults.add(new ClassificationResult(
                FilenameUtils.getBaseName(images.get(idx)),
                el.toString(), String.valueOf(meta.getLabels().get(index))));
            ++idx;
        }
    
        resultsTableController.populateTable(classificationResults, meta.getLabels().toString());
        Platform.runLater(() -> {
            Utils.switch3Pane(pane, stack, "Результати класифікації", label, transition);
        });
    }
    
    public void classify () {
        if (chooseModelController.getCurrentClassifierModel() != null && (getImagesPath() != null || getSingleImage() != null)) {
            new Thread(() -> {
                try {
                    
                    List<INDArray> outputList = new ArrayList<>();
                    INDArray output = null;
                    
                    Utils.updateProgress(progressBar, classifyForest, true);
                    MultiLayerNetwork net = ModelSerializer.restoreMultiLayerNetwork(chooseModelController.getCurrentClassifierModel(), true);
                    if (this.last == false) {
                        
                        List<String> images = Utils.findFilesInDirectory(getImagesPath());
    
                        DataNormalization scaler = new ImagePreProcessingScaler(0, 1);
                        NativeImageLoader loader = new NativeImageLoader(224, 224, 3);
                        
                        for (String elem: images) {
                            File file = new File(elem);
                            INDArray image = null;
                            try {
                                image = loader.asMatrix(file);
                                scaler.transform(image);
                                INDArray out = net.output(image);
                                outputList.add(out);
                                System.out.println(out.toString());
                            }
                            catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        
                        processResults(images, outputList);
                        
                    }
                    else {
                        List<String> files = new ArrayList<>();
                        String single = getSingleImage();
                        files.add(single);
                        File file = new File(single);
    
                        DataNormalization scaler = new ImagePreProcessingScaler(0, 1);
                        NativeImageLoader loader = new NativeImageLoader(224, 224, 3);
                        
                        INDArray image = loader.asMatrix(file);
                        scaler.transform(image);
                        INDArray out = net.output(image);
                        outputList.add(out);
                        
                        processResults(files, outputList);
                    }
                    
                    Utils.updateProgress(progressBar, classifyForest, false);
                    
                    return;
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }
}