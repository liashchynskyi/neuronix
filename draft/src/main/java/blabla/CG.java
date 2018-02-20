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
import org.deeplearning4j.eval.Evaluation;
import org.deeplearning4j.nn.api.Model;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.ComputationGraphConfiguration;
import org.deeplearning4j.nn.conf.GradientNormalization;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
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

public class CG {
    protected static final Logger log = LoggerFactory.getLogger(CG.class);
    protected static long seed = 42;
    protected static Random rng = new Random(seed);
    protected static int height = 128;
    protected static int width = 128;
    protected static int channels = 3;
    protected static int numLabels = 5;//6;
    protected static int batchSize = 5; //5 for cyt 100%
    protected static int iterations = 1; // 1 for cyt
    protected static int epochs = 20; //20 cyt 100%

    public static void main(String[] args) throws IOException {
        File parentDir = new File("T:/DISK_D/PETRO/Development/development/java/projects/cytology/untitled/src/main/hist");

        FileSplit filesInDir = new FileSplit(parentDir, NativeImageLoader.ALLOWED_FORMATS, rng);
        ParentPathLabelGenerator labelMaker = new ParentPathLabelGenerator();

        BalancedPathFilter pathFilter = new BalancedPathFilter(rng, NativeImageLoader.ALLOWED_FORMATS, labelMaker);

        InputSplit[] filesInDirSplit = filesInDir.sample(pathFilter, 80, 20); //85, 15 100% cyt
        InputSplit trainData = filesInDirSplit[0];
        InputSplit testData = filesInDirSplit[1];

        ImageRecordReader recordReader = new ImageRecordReader(height,width,channels,labelMaker);
        ImageTransform transform = new MultiImageTransform(rng, new CropImageTransform(10), new FlipImageTransform(),new ScaleImageTransform(10), new WarpImageTransform(10));

        DataNormalization scaler = new ImagePreProcessingScaler(0, 1);
        recordReader.initialize(trainData, transform);



        DataSetIterator dataIter = new RecordReaderDataSetIterator(recordReader, batchSize, 1, numLabels);
        scaler.fit(dataIter);
        dataIter.setPreProcessor(scaler);

        MultipleEpochsIterator trainIter = new MultipleEpochsIterator(epochs, dataIter);



        MultiLayerNetwork network = new MultiLayerNetwork(leNet());
//        AlexNet net = new AlexNet(numLabels, seed, iterations);
//        net.setInputShape(new int[][]{{3,128,128}});
//        MultiLayerConfiguration config = net.conf();
//
//        MultiLayerNetwork network = new MultiLayerNetwork(config);
//        network.init();
//
       StatsStorage statsStorage = new InMemoryStatsStorage();
        network.setListeners((IterationListener)new StatsListener( statsStorage), new ScoreIterationListener(iterations));

        log.info("Train model.....");
        network.fit(trainIter);

        log.info("Evaluate model.....");
        recordReader.initialize(testData);
        dataIter = new RecordReaderDataSetIterator(recordReader, batchSize, 1, numLabels);
        scaler.fit(dataIter);
        dataIter.setPreProcessor(scaler);
        Evaluation eval = network.evaluate(dataIter);
        log.info(eval.stats(true));
        log.info("Save model....");
        String basePath = FilenameUtils.concat(System.getProperty("user.dir"), "src/main/saved/");
        ModelSerializer.writeModel(network, basePath + "model.bin", true);



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
    private static MultiLayerConfiguration leNet() {
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
                .seed(seed)
                .iterations(iterations)
                .dist(new NormalDistribution(0.0, 0.01))
                .regularization(true)
                .l2(6*2e-6) //5*1e-7 100% cyt // 54*1e-5 13*1e-4 2*1e-5 80% hist
                .activation(Activation.RELU) // RELU
                .learningRate(7*1e-3) //5*1e-3 100% cyt // 7*1e-3 80% hist
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
        return conf;
    }
    private static MultiLayerConfiguration custom() {
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
                .seed(seed)
                .iterations(iterations)
                .dist(new NormalDistribution(0.0, 0.01))
                .regularization(true)
                .l2(0.05)
                .activation(Activation.RELU)
                .learningRate(1e-2)
                .weightInit(WeightInit.XAVIER)
                .gradientNormalization(GradientNormalization.RenormalizeL2PerLayer)
                .optimizationAlgo(OptimizationAlgorithm.LINE_GRADIENT_DESCENT)
                .updater(new Nesterovs(0.9))
                .list()
                .layer(0, new ConvolutionLayer.Builder()
                        .nIn(channels)
                        .kernelSize(11, 11)
                        .stride(3, 3)
                        .padding(0, 0)
                        .biasInit(0)
                        .nOut(64)
                        .build())
                .layer(1, new SubsamplingLayer.Builder()
                        .kernelSize(4, 4)
                        .stride(1, 1)
                        .padding(0, 0)
                        .build())
                .layer(2, new ConvolutionLayer.Builder()
                        .kernelSize(5, 5)
                        .stride(5, 5)
                        .padding(1, 1)
                        .biasInit(0)
                        .nOut(128)
                        .build())
                .layer(3, new SubsamplingLayer.Builder()
                        .kernelSize(4, 4)
                        .stride(1, 1)
                        .padding(1, 1)
                        .build())
                .layer(4, new DenseLayer.Builder()
                        .dropOut(1e-4)
                        .nOut(500)
                        .build())
                .layer(5, new OutputLayer.Builder()
                        .lossFunction(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
                        .nOut(numLabels)
                        .activation(Activation.SOFTMAX)
                        .build())
                .backprop(true)
                .pretrain(false)
                .setInputType(InputType.convolutional(height, width, channels))
                .build();
        return conf;
    }
}