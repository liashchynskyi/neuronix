package controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXProgressBar;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
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
import utils.prefs.Prefs;

public class ClassificationController {
    private ChooseModelController chooseModelController;
    private JFXProgressBar progressBar;
    private JFXButton classifyForest;
    private Prefs                 prefs;
    private String                imagesPath;
    private String                singleImage;
    private Random                rng = new Random(42);
    private boolean last = false;
    
    public ClassificationController (ChooseModelController chooseModelController, Prefs prefs,
                                     JFXProgressBar progressBar, JFXButton classifyForest) {
        this.chooseModelController = chooseModelController;
        this.prefs = prefs;
        this.progressBar = progressBar;
        this.classifyForest = classifyForest;
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
    
    public void classify () {
        if (chooseModelController.getCurrentClassifierModel() != null && (getImagesPath() != null || getSingleImage() != null)) {
            new Thread(() -> {
                try {
                    Utils.updateProgress(progressBar, classifyForest, true);
                    MultiLayerNetwork net = ModelSerializer.restoreMultiLayerNetwork(chooseModelController.getCurrentClassifierModel(), true);
                    if (this.last == false) {
                        List<String> images = Utils.findFilesInDirectory(getImagesPath());
    
                        DataNormalization scaler = new ImagePreProcessingScaler(0, 1);
                        NativeImageLoader loader = new NativeImageLoader(224, 224, 3);
                        
                        images.stream().forEach((elem) -> {
                            File file = new File(elem);
                            INDArray image = null;
                            try {
                                image = loader.asMatrix(file);
                            }
                            catch (IOException e) {
                                e.printStackTrace();
                            }
                            scaler.transform(image);
                            INDArray output = net.output(image);
                            System.out.println(output.toString());
                        });
                        
                    }
                    else {
                        File file = new File(getSingleImage());
    
                        DataNormalization scaler = new ImagePreProcessingScaler(0, 1);
                        NativeImageLoader loader = new NativeImageLoader(224, 224, 3);
                        
                        INDArray image = loader.asMatrix(file);
                        scaler.transform(image);
                        INDArray output = net.output(image);
                        System.out.println(output.toString());
                        
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