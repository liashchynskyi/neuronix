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

public class Early {
    protected static final Logger log = LoggerFactory.getLogger(CG.class);
    protected static long seed = 42;
    protected static Random rng = new Random(seed);
    protected static int height = 128;
    protected static int width = 128;
    protected static int channels = 3;
    protected static int numLabels = 5;//6;
    protected static int batchSize = 5;
    protected static int iterations = 1;
    protected static int epochs = 20;

    public static void main(String[] args) throws IOException {
        File parentDir = new File("T:/DISK_D/PETRO/Development/development/java/projects/cytology/untitled/src/main/cyt");

        FileSplit filesInDir = new FileSplit(parentDir, NativeImageLoader.ALLOWED_FORMATS, rng);
        ParentPathLabelGenerator labelMaker = new ParentPathLabelGenerator();

        BalancedPathFilter pathFilter = new BalancedPathFilter(rng, NativeImageLoader.ALLOWED_FORMATS, labelMaker);

        InputSplit[] filesInDirSplit = filesInDir.sample(pathFilter, 80, 20);
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


        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
                .seed(seed)
                .iterations(iterations)
                .dist(new NormalDistribution(0.0, 0.01))
                .regularization(true).l2(0.05)
                .activation(Activation.RELU)
                .learningRate(1e-2)
                .weightInit(WeightInit.RELU)
                .gradientNormalization(GradientNormalization.RenormalizeL2PerLayer)
                .optimizationAlgo(OptimizationAlgorithm.LINE_GRADIENT_DESCENT)//STOCHASTIC_GRADIENT_DESCENT
                .updater(new Nesterovs(0.9))
                .list()
                .layer(0, convInit("cnn1", channels, 50 ,  new int[]{5, 5}, new int[]{1, 1}, new int[]{0, 0}, 0))
                .layer(1, maxPool("maxpool1", new int[]{2,2}))
                .layer(2, conv5x5("cnn2", 100, new int[]{5, 5}, new int[]{1, 1}, 0))
                .layer(3, maxPool("maxpool2", new int[]{2,2}))
                .layer(4, new DenseLayer.Builder()
                        .nOut(500)
                        .build())
                .layer(5, new OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
                        .nOut(numLabels)
                        .activation(Activation.SOFTMAX)
                        .build())
                .backprop(true).pretrain(false)
                .setInputType(InputType.convolutional(height, width, channels))
                .build();

        String dir = FilenameUtils.concat(System.getProperty("user.dir"), "src/main/saved/");


        EarlyStoppingConfiguration esConf = new EarlyStoppingConfiguration.Builder()
                .epochTerminationConditions(new MaxEpochsTerminationCondition(4000))
                .iterationTerminationConditions(new MaxTimeIterationTerminationCondition(30, TimeUnit.MINUTES))
                .scoreCalculator(new DataSetLossCalculator(testIter, true))
                .evaluateEveryNEpochs(1)
                .saveLastModel(true)
                .modelSaver(new LocalFileModelSaver(dir))
                .build();

        EarlyStoppingTrainer trainer = new EarlyStoppingTrainer(esConf, conf, trainIter);

        EarlyStoppingResult result = trainer.fit();


        log.info("Termination reason: " + result.getTerminationReason());
        log.info("Termination details: " + result.getTerminationDetails());
        log.info("Total epochs: " + result.getTotalEpochs());
        log.info("The best epoch number: " + result.getBestModelEpoch());
        log.info("Score at the best epoch: " + result.getBestModelScore());

        MultiLayerNetwork net = (MultiLayerNetwork) result.getBestModel();
        Evaluation eval = new Evaluation(numLabels);
        testIter.reset();
        while (testIter.hasNext()) {
            DataSet next = testIter.next();
            INDArray output = net.output(next.getFeatures());
            eval.eval(next.getLabels(), output);
        }
        log.info(eval.stats());


    }

    private static ConvolutionLayer convInit(String name, int in, int out, int[] kernel, int[] stride, int[] pad, double bias) {
        return new ConvolutionLayer.Builder(kernel, stride, pad).name(name).nIn(in).nOut(out).biasInit(bias).build();
    }

    private static ConvolutionLayer conv3x3(String name, int out, double bias) {
        return new ConvolutionLayer.Builder(new int[]{3,3}, new int[] {1,1}, new int[] {1,1}).name(name).nOut(out).biasInit(bias).build();
    }

    private static ConvolutionLayer conv5x5(String name, int out, int[] stride, int[] pad, double bias) {
        return new ConvolutionLayer.Builder(new int[]{5,5}, stride, pad).name(name).nOut(out).biasInit(bias).build();
    }

    private static SubsamplingLayer maxPool(String name, int[] kernel) {
        return new SubsamplingLayer.Builder(kernel, new int[]{2,2}).name(name).build();
    }

    private static DenseLayer fullyConnected(String name, int out, double bias, double dropOut, Distribution dist) {
        return new DenseLayer.Builder().name(name).nOut(out).biasInit(bias).dropOut(dropOut).dist(dist).build();
    }

}