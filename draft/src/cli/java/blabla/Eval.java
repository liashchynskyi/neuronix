package blabla;

import org.apache.commons.io.FilenameUtils;
import org.datavec.api.io.filters.BalancedPathFilter;
import org.datavec.api.io.labels.ParentPathLabelGenerator;
import org.datavec.api.split.FileSplit;
import org.datavec.api.split.InputSplit;
import org.datavec.image.loader.NativeImageLoader;
import org.datavec.image.recordreader.ImageRecordReader;
import org.datavec.image.transform.*;
import org.deeplearning4j.api.storage.StatsStorage;
import org.deeplearning4j.datasets.datavec.RecordReaderDataSetIterator;
import org.deeplearning4j.datasets.iterator.MultipleEpochsIterator;
import org.deeplearning4j.earlystopping.EarlyStoppingConfiguration;
import org.deeplearning4j.earlystopping.EarlyStoppingModelSaver;
import org.deeplearning4j.earlystopping.EarlyStoppingResult;
import org.deeplearning4j.earlystopping.saver.LocalFileModelSaver;
import org.deeplearning4j.earlystopping.scorecalc.DataSetLossCalculator;
import org.deeplearning4j.earlystopping.termination.MaxEpochsTerminationCondition;
import org.deeplearning4j.earlystopping.termination.MaxScoreIterationTerminationCondition;
import org.deeplearning4j.earlystopping.termination.MaxTimeIterationTerminationCondition;
import org.deeplearning4j.earlystopping.trainer.EarlyStoppingTrainer;
import org.deeplearning4j.eval.Evaluation;
import org.deeplearning4j.nn.api.Model;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.*;
import org.deeplearning4j.nn.conf.distribution.Distribution;
import org.deeplearning4j.nn.conf.distribution.NormalDistribution;
import org.deeplearning4j.nn.conf.inputs.InputType;
import org.deeplearning4j.nn.conf.layers.*;
import org.deeplearning4j.nn.graph.ComputationGraph;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.api.IterationListener;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.deeplearning4j.ui.stats.StatsListener;
import org.deeplearning4j.ui.storage.InMemoryStatsStorage;
import org.deeplearning4j.util.ModelSerializer;
import org.deeplearning4j.zoo.PretrainedType;
import org.deeplearning4j.zoo.ZooModel;
import org.deeplearning4j.zoo.model.AlexNet;
import org.deeplearning4j.zoo.model.GoogLeNet;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.api.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.dataset.api.preprocessor.DataNormalization;
import org.nd4j.linalg.dataset.api.preprocessor.ImagePreProcessingScaler;
import org.nd4j.linalg.learning.config.Adam;
import org.nd4j.linalg.learning.config.Nesterovs;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class Eval {
    protected static final Logger log = LoggerFactory.getLogger(CG.class);
    protected static long seed = 42;
    protected static Random rng = new Random(seed);
    protected static int height = 224;
    protected static int width = 224;
    protected static int channels = 3;
    protected static int numLabels = 5;//6;
    protected static int batchSize = 5;
    protected static int iterations = 1;
    protected static int epochs = 20;

    public static void main(String[] args) throws IOException {
        File parentDir = new File("T:\\DISK_D\\PETRO\\Development\\development\\java\\projects\\cytology\\untitled\\src\\main\\cyt224");

        FileSplit filesInDir = new FileSplit(parentDir, NativeImageLoader.ALLOWED_FORMATS, rng);
        ParentPathLabelGenerator labelMaker = new ParentPathLabelGenerator();

        BalancedPathFilter pathFilter = new BalancedPathFilter(rng, NativeImageLoader.ALLOWED_FORMATS, labelMaker);

        InputSplit[] filesInDirSplit = filesInDir.sample(pathFilter, 99, 1);
        InputSplit trainData = filesInDirSplit[0];
        InputSplit testData = filesInDirSplit[1];

        ImageRecordReader recordReaderTrain = new ImageRecordReader(height,width,channels,labelMaker);
        ImageRecordReader recordReaderTest = new ImageRecordReader(height,width,channels,labelMaker);
        ImageTransform transform = new MultiImageTransform(rng, new CropImageTransform(10), new FlipImageTransform(),new ScaleImageTransform(10), new WarpImageTransform(10));

        DataNormalization scalerTrain = new ImagePreProcessingScaler(0, 1);
        DataNormalization scalerTest = new ImagePreProcessingScaler(0, 1);

        recordReaderTrain.initialize(trainData, transform);
        recordReaderTest.initialize(testData, transform);


        DataSetIterator trainIter = new RecordReaderDataSetIterator(recordReaderTrain, batchSize, 1, numLabels);
        scalerTrain.fit(trainIter);
        trainIter.setPreProcessor(scalerTrain);

        DataSetIterator testIter = new RecordReaderDataSetIterator(recordReaderTrain, batchSize, 1, numLabels);
        scalerTest.fit(testIter);
        testIter.setPreProcessor(scalerTest);

        String dir = FilenameUtils.concat("T:\\DISK_D\\PETRO\\Development\\development\\java\\projects\\neuronix\\pre_trained", "");

        MultiLayerNetwork net = ModelSerializer.restoreMultiLayerNetwork(dir + "\\lenet_20_a90_p93_r90_f89_is224_t20_c.bin", true); //latestModel
        Evaluation eval = new Evaluation(numLabels);
        while (testIter.hasNext()) {
            DataSet next = testIter.next();
            INDArray output = net.output(next.getFeatures());
            eval.eval(next.getLabels(), output);
        }
        log.info(eval.stats());
    }
}