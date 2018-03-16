package controllers;

import com.jfoenix.controls.JFXSlider;
import com.jfoenix.controls.JFXTextField;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import javafx.application.Platform;
import org.datavec.api.io.filters.BalancedPathFilter;
import org.datavec.api.io.labels.ParentPathLabelGenerator;
import org.datavec.api.split.FileSplit;
import org.datavec.api.split.InputSplit;
import org.datavec.image.loader.NativeImageLoader;
import org.datavec.image.recordreader.ImageRecordReader;
import org.datavec.image.transform.CropImageTransform;
import org.datavec.image.transform.FlipImageTransform;
import org.datavec.image.transform.ImageTransform;
import org.datavec.image.transform.MultiImageTransform;
import org.datavec.image.transform.ScaleImageTransform;
import org.datavec.image.transform.WarpImageTransform;
import org.deeplearning4j.api.storage.StatsStorage;
import org.deeplearning4j.datasets.datavec.RecordReaderDataSetIterator;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.optimize.api.IterationListener;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.deeplearning4j.ui.stats.StatsListener;
import org.deeplearning4j.ui.storage.InMemoryStatsStorage;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.dataset.api.preprocessor.DataNormalization;
import org.nd4j.linalg.dataset.api.preprocessor.ImagePreProcessingScaler;
import utils.Utils;
import utils.models.json.JsonModelBuilder;
import utils.models.json.Model;

public class TrainingController {
    private String imagesPath;
    private ChooseModelController chooseModelController;
    private JFXTextField epochsNumber, iterNumber, learningRate;
    private JFXSlider splitTrainTest;
    private Random    rng;
    
    
    public TrainingController (ChooseModelController chooseModelController,
                               JFXTextField epochsNumber, JFXTextField iterNumber,
                               JFXTextField learningRate, JFXSlider splitTrainTest) {
        this.chooseModelController = chooseModelController;
        this.epochsNumber = epochsNumber;
        this.iterNumber = iterNumber;
        this.learningRate = learningRate;
        this.splitTrainTest = splitTrainTest;
    }
    
    public String getImagesPath () {
        return imagesPath;
    }
    
    public void setImagesPath (String imagesPath) {
        this.imagesPath = imagesPath;
    }
    
    public void train () {
        
        if (chooseModelController.getCurrentSavedModel() != null && getImagesPath() != null)
        Platform.runLater(() -> {
            try {
                String modeljson = Utils.readJSON(this.chooseModelController.getCurrentSavedModel());
                Model             encoded = Utils.decodeJson(modeljson);
                JsonModelBuilder  builder = new JsonModelBuilder(encoded);
                MultiLayerNetwork network = builder.init(Integer.parseInt(this.iterNumber.getText()),
                                                         Double.parseDouble(this.learningRate.getText()))
                                                   .build();
                
                this.rng = new Random(42);
                File                     parentDir       = new File(getImagesPath());
                FileSplit                filesInDir      = new FileSplit(parentDir, NativeImageLoader.ALLOWED_FORMATS, rng);
                ParentPathLabelGenerator labelMaker      = new ParentPathLabelGenerator();
                BalancedPathFilter       pathFilter      = new BalancedPathFilter(rng, NativeImageLoader.ALLOWED_FORMATS, labelMaker);
                InputSplit[]             filesInDirSplit = filesInDir.sample(pathFilter, (int) splitTrainTest.getValue(), (int) 100 - this.splitTrainTest.getValue());
                InputSplit               trainData       = filesInDirSplit[0];
                InputSplit               testData        = filesInDirSplit[1];
    
                ImageRecordReader recordReader = new ImageRecordReader(encoded.getImageSize(), encoded.getImageSize(), encoded.getChannels(), labelMaker);
                ImageTransform    transform    = new MultiImageTransform(rng, new CropImageTransform(10), new FlipImageTransform(), new ScaleImageTransform(10), new WarpImageTransform(10));
    
                DataNormalization scaler = new ImagePreProcessingScaler(0, 1);
                recordReader.initialize(trainData, transform);
    
                DataSetIterator dataIter = new RecordReaderDataSetIterator(recordReader, encoded.getBatchSize(), 1, encoded.getNumLabels());
    
                scaler.fit(dataIter);
                dataIter.setPreProcessor(scaler);
    
                StatsStorage statsStorage = new InMemoryStatsStorage();
                network.setListeners((IterationListener)new StatsListener(statsStorage), new ScoreIterationListener(encoded.getIterations()));
                
                for (int e = 0; e <= Integer.parseInt(epochsNumber.getText()); e++) {
                    network.fit(dataIter);
                }
    
    
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        });
        
    }
    
}