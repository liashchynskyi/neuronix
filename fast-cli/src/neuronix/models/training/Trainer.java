package neuronix.models.training;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Random;
import neuronix.config.Prefs;
import neuronix.models.json.JsonModelBuilder;
import neuronix.models.json.Model;
import neuronix.utils.Utils;
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
import org.deeplearning4j.eval.Evaluation;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.optimize.api.IterationListener;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.deeplearning4j.ui.stats.StatsListener;
import org.deeplearning4j.ui.storage.InMemoryStatsStorage;
import org.deeplearning4j.util.ModelSerializer;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.dataset.api.preprocessor.DataNormalization;
import org.nd4j.linalg.dataset.api.preprocessor.ImagePreProcessingScaler;

public class Trainer {
    
    private String imagesPath;
    private String pathToNeuralNetModel;
    private Random randomSeed;
    private int    epochNumber, iterationNumber, splitTrainTest;
    private double learningRate;
    private double trainingResults[];
    
    public Trainer (int epochNumber, int iterationNumber, double learningRate, int splitTrainTest) {
        this.epochNumber = epochNumber;
        this.iterationNumber = iterationNumber;
        this.learningRate = learningRate;
        this.splitTrainTest = splitTrainTest;
    }
    
    public String getImagesPath () {
        return imagesPath;
    }
    
    public void setImagesPath (String imagesPath) {
        this.imagesPath = imagesPath;
    }
    
    public String getPathToNeuralNetModel () {
        return pathToNeuralNetModel;
    }
    
    public void setPathToNeuralNetModel (String pathToNeuralNetModel) {
        this.pathToNeuralNetModel = pathToNeuralNetModel;
    }
    
    public int getEpochNumber () {
        return epochNumber;
    }
    
    public void setEpochNumber (int epochNumber) {
        this.epochNumber = epochNumber;
    }
    
    public int getIterationNumber () {
        return iterationNumber;
    }
    
    public void setIterationNumber (int iterationNumber) {
        this.iterationNumber = iterationNumber;
    }
    
    public double getLearningRate () {
        return learningRate;
    }
    
    public void setLearningRate (double learningRate) {
        this.learningRate = learningRate;
    }
    
    public void setRandomSeed (int rng) {
        this.randomSeed = new Random(rng);
    }
    
    public Random getRandomSeed () {
        return randomSeed;
    }
    
    public int getSplitTrainTest () {
        return splitTrainTest;
    }
    
    public void setSplitTrainTest (int splitTrainTest) {
        this.splitTrainTest = splitTrainTest;
    }
    
    public double[] getTrainingResults () {
        return trainingResults;
    }
    
    public double[] train () {
        if (getImagesPath() != null && getPathToNeuralNetModel() != null) {
            Utils.getLogger().info("Start training...");
                try {
                    String           modeljson = Utils.readJSON(getPathToNeuralNetModel());
                    Model            encoded   = Utils.decodeJson(modeljson);
                    JsonModelBuilder builder   = new JsonModelBuilder(encoded);
    
                    MultiLayerNetwork network = builder.init(getIterationNumber(), getLearningRate())
                                                       .build();
    
                    File parentDir = new File(getImagesPath());
                    FileSplit filesInDir = new FileSplit(parentDir, NativeImageLoader.ALLOWED_FORMATS,
                                                         getRandomSeed());
                    ParentPathLabelGenerator labelMaker = new ParentPathLabelGenerator();
                    BalancedPathFilter pathFilter = new BalancedPathFilter(getRandomSeed(),
                                                                           NativeImageLoader.ALLOWED_FORMATS,
                                                                           labelMaker);
    
                    InputSplit[] filesInDirSplit = filesInDir.sample(pathFilter, getSplitTrainTest(),
                                                                     100 - splitTrainTest);
                    InputSplit trainData = filesInDirSplit[0];
                    InputSplit testData  = filesInDirSplit[1];
    
                    ImageRecordReader recordReader = new ImageRecordReader(encoded.getImageSize(),
                                                                           encoded.getImageSize(),
                                                                           encoded.getChannels(),
                                                                           labelMaker);
                    ImageTransform transform    = new MultiImageTransform(getRandomSeed(), new CropImageTransform(
                        10), new FlipImageTransform(), new ScaleImageTransform(10), new WarpImageTransform(
                        10));
    
                    DataNormalization scaler = new ImagePreProcessingScaler(0, 1);
                    recordReader.initialize(trainData, transform);
    
                    List<String> labels = recordReader.getLabels();
                    Integer      numL   = labels.size();
    
                    DataSetIterator dataIter = new RecordReaderDataSetIterator(recordReader, encoded.getBatchSize(),
                                                                               1, encoded.getNumLabels());
    
                    scaler.fit(dataIter);
                    dataIter.setPreProcessor(scaler);
                    StatsStorage statsStorage = new InMemoryStatsStorage();
                    network.setListeners((IterationListener) new StatsListener(statsStorage), new ScoreIterationListener(encoded.getIterations()));
    
                    for (int e = 1; e <= getEpochNumber(); e++) {
                        network.fit(dataIter);
                    }
                    
                    recordReader.reset();
                    recordReader.initialize(testData);
                    dataIter = new RecordReaderDataSetIterator(recordReader, encoded.getBatchSize(),
                                                               1, encoded.getNumLabels());
                    scaler.fit(dataIter);
                    dataIter.setPreProcessor(scaler);
                    Evaluation eval = network.evaluate(dataIter);
                    
                    trainingResults = new double[]{
                        eval.accuracy(),
                        eval.precision(),
                        eval.recall(),
                        eval.f1()
                    };
                    
                    if (Prefs.currentSaveStateProperty().get()) {
                        String basePath = Prefs.getCurrentLoadDir();
                        ModelSerializer.writeModel(network,
                                                   basePath + "\\" + String.format("%s_%d_%d_%d.%s", encoded.getModelName(),
                                                                                   encoded.getImageSize(),
                                                                                   numL,
                                                                                   Math.round(eval.accuracy() * 100.0),
                                                                                   "bin"), true);
                        Model meta = new Model();
                        meta.setLabels(labels);
                        meta.setNumLabels(numL);
                        meta.setImageSize(encoded.getImageSize());
                        String enc = Utils.encodeJson(meta);
                        File dir = new File(Prefs.getCurrentSaveDir() + "\\meta");
                        if (!dir.exists()) dir.mkdir();
                        Utils.writeJSON(dir.getAbsolutePath() + "\\" + String.format("%s_%d_%d_%d.%s", encoded.getModelName(),
                                                                                     encoded.getImageSize(),
                                                                                     numL,
                                                                                     Math.round(eval.accuracy() * 100.0),
                                                                                     "json"), enc);
                        Utils.getLogger().info("Training has done...");
                    }
                    
                }
                catch (IOException e) {
                    Utils.getLogger().error(e.toString());
                }
        }
        else {
            Utils.getLogger().error("Oops! Looks like you didn't set paths for your images and your model...");
        }
        return getTrainingResults();
    }
    
}