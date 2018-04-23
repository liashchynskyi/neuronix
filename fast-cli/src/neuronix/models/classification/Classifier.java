package neuronix.models.classification;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import neuronix.config.Prefs;
import neuronix.models.json.Model;
import neuronix.utils.Utils;
import org.apache.commons.io.FilenameUtils;
import org.datavec.image.loader.NativeImageLoader;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.util.ModelSerializer;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.api.preprocessor.DataNormalization;
import org.nd4j.linalg.dataset.api.preprocessor.ImagePreProcessingScaler;

public class Classifier {
    
    private String imagesPath;
    private String modelName;
    private Random randomSeed = new Random(42);
    private boolean singleImage = false;
    
    public Classifier () {
    }
    
    public Classifier (String imagesPath, String modelName, Random randomSeed) {
        this.imagesPath = imagesPath;
        this.modelName = modelName;
        this.randomSeed = randomSeed;
    }
    
    public String getImagesPath () {
        return imagesPath;
    }
    
    public void setImagesPath (String imagesPath) {
        this.imagesPath = imagesPath;
    }
    
    public String getModelName () {
        return modelName;
    }
    
    public String getModelPath () {
        return Prefs.getCurrentSaveDir() + "\\" + modelName + ".bin";
    }
    
    public void setModelName (String modelName) {
        this.modelName = modelName;
    }
    
    public Random getRandomSeed () {
        return randomSeed;
    }
    
    public void setRandomSeed (Random randomSeed) {
        this.randomSeed = randomSeed;
    }
    
    public boolean isSingleImage () {
        return singleImage;
    }
    
    public void setSingleImage (boolean singleImage) {
        this.singleImage = singleImage;
    }
    
    public ObservableList<ClassificationResult> classify () throws Exception {
        ObservableList<ClassificationResult> results = null;
        if (getImagesPath() != null && getModelName() != null) {
            Utils.getLogger().info("Start classification...");
    
            List<INDArray>   outputList = new ArrayList<>();
            INDArray output     = null;
    
            MultiLayerNetwork net = ModelSerializer.restoreMultiLayerNetwork(getModelPath(), true);
            
            String json =  Utils.readJSON(Prefs.getCurrentSaveDir() + "\\meta\\" + getModelName() + ".json");
            Model  meta = Utils.decodeJson(json);
            
            if (!this.isSingleImage()) {
                List<String> images = Utils.findFilesInDirectory(getImagesPath());
    
                DataNormalization scaler = new ImagePreProcessingScaler(0, 1);
                NativeImageLoader loader = new NativeImageLoader(meta.getImageSize(), meta.getImageSize(), 3);
    
                for (String elem: images) {
                    File     file  = new File(elem);
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
    
                results =  processResults(images, outputList, meta);
            }
            else {
                List<String> files = new ArrayList<>();
                String single = getImagesPath();
                files.add(single);
                File file = new File(single);
    
                DataNormalization scaler = new ImagePreProcessingScaler(0, 1);
                NativeImageLoader loader = new NativeImageLoader(meta.getImageSize(), meta.getImageSize(), 3);
    
                INDArray image = loader.asMatrix(file);
                scaler.transform(image);
                INDArray out = net.output(image);
                outputList.add(out);
    
                results =  processResults(files, outputList, meta);
            }
        }
        else {
            Utils.getLogger().error("Oops! Looks like you didn't set paths for your images and your model...");
        }
        return results;
    }
    
    private ObservableList<ClassificationResult> processResults (List<String> images, List<INDArray> outputList, Model meta) throws Exception {
        ObservableList<ClassificationResult> classificationResults = FXCollections.observableArrayList();
        int                                  idx                   = 0;
    
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
        return classificationResults;
    }
    
}