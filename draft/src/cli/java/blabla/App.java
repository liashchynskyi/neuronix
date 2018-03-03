package blabla;

import org.apache.commons.io.FilenameUtils;
import org.datavec.api.io.filters.BalancedPathFilter;
import org.datavec.api.io.labels.ParentPathLabelGenerator;
import org.datavec.api.split.FileSplit;
import org.datavec.api.split.InputSplit;
import org.datavec.image.loader.NativeImageLoader;
import org.datavec.image.recordreader.ImageRecordReader;
import org.datavec.image.transform.FlipImageTransform;
import org.datavec.image.transform.ImageTransform;
import org.datavec.image.transform.WarpImageTransform;
import org.deeplearning4j.api.storage.StatsStorage;
import org.deeplearning4j.datasets.datavec.RecordReaderDataSetIterator;
import org.deeplearning4j.datasets.iterator.MultipleEpochsIterator;
import org.deeplearning4j.eval.Evaluation;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.*;
import org.deeplearning4j.nn.conf.distribution.Distribution;
import org.deeplearning4j.nn.conf.distribution.GaussianDistribution;
import org.deeplearning4j.nn.conf.distribution.NormalDistribution;
import org.deeplearning4j.nn.conf.inputs.InputType;
import org.deeplearning4j.nn.conf.inputs.InvalidInputTypeException;
import org.deeplearning4j.nn.conf.layers.*;
import org.deeplearning4j.nn.graph.ComputationGraph;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.api.IterationListener;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.deeplearning4j.ui.api.UIServer;
import org.deeplearning4j.ui.stats.StatsListener;
import org.deeplearning4j.ui.storage.InMemoryStatsStorage;
import org.deeplearning4j.util.ModelSerializer;
import org.deeplearning4j.zoo.model.GoogLeNet;
import org.deeplearning4j.zoo.model.SimpleCNN;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.activations.IActivation;
import org.nd4j.linalg.activations.impl.ActivationLReLU;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.dataset.api.preprocessor.DataNormalization;
import org.nd4j.linalg.dataset.api.preprocessor.ImagePreProcessingScaler;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.learning.config.AdaDelta;
import org.nd4j.linalg.learning.config.Adam;
import org.nd4j.linalg.learning.config.Nesterovs;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Random;


public class App 
{
    protected static final Logger log = LoggerFactory.getLogger(App.class);
    protected static int height = 128;
    protected static int width = 128;
    protected static int channels = 3;

    protected static int numLabels = 5;
    protected static int epochs = 20;
    protected static int batchSize = 30;

    protected static long seed = 42;
    protected static Random rng = new Random(seed);
    protected static int listenerFreq = 1;
    protected static int iterations = 1;
    protected static boolean save = true;
    private int[] inputShape = new int[] {3, 128, 128};
    protected static String modelType = "AlexNet"; // LeNet, AlexNet or Custom but you need to fill it out

    public void run(String[] args) throws Exception {

        log.info("Load data....");
        /**cd
         * Data Setup -> organize and limit data file paths:
         *  - mainPath = path to image files
         *  - fileSplit = define basic dataset split with limits on format
         *  - pathFilter = define additional file load filter to limit size and balance batch content
         **/
        File parentDir = new File("T:/DISK_D/PETRO/Development/development/java/projects/cytology/untitled/src/main/cyt");

        FileSplit filesInDir = new FileSplit(parentDir, NativeImageLoader.ALLOWED_FORMATS, rng);
        ParentPathLabelGenerator labelMaker = new ParentPathLabelGenerator();

        BalancedPathFilter pathFilter = new BalancedPathFilter(rng, NativeImageLoader.ALLOWED_FORMATS, labelMaker);

        InputSplit[] filesInDirSplit = filesInDir.sample(pathFilter, 80, 20);
        InputSplit trainData = filesInDirSplit[0];
        InputSplit testData = filesInDirSplit[1];

        /**
         * Data Setup -> transformation
         *  - Transform = how to tranform images and generate large dataset to train on
         **/
        ImageTransform flipTransform1 = new FlipImageTransform(rng);
        ImageTransform flipTransform2 = new FlipImageTransform(new Random(123));
        ImageTransform warpTransform = new WarpImageTransform(rng, 42);
//        ImageTransform colorTransform = new ColorConversionTransform(new Random(seed), COLOR_BGR2YCrCb);
        List<ImageTransform> transforms = Arrays.asList(new ImageTransform[]{flipTransform1, warpTransform, flipTransform2});

        /**
         * Data Setup -> normalization
         *  - how to normalize images and generate large dataset to train on
         **/
        DataNormalization scaler = new ImagePreProcessingScaler(0, 1);

        log.info("Build model....");

        // Uncomment below to try AlexNet. Note change height and width to at least 100
//        MultiLayerNetwork network = new AlexNet(height, width, channels, numLabels, seed, iterations).init();

        MultiLayerNetwork network;
        switch (modelType) {
            case "LeNet":
                network = lenetModel();
                break;
            case "AlexNet":
                network = alexnetModel();
                break;
            case "custom":
                network = customModel();
                break;
            default:
                throw new InvalidInputTypeException("Incorrect model provided.");
        }
        network.init();
        // network.setListeners(new ScoreIterationListener(listenerFreq));
        UIServer uiServer = UIServer.getInstance();
        StatsStorage statsStorage = new InMemoryStatsStorage();
        uiServer.attach(statsStorage);
        network.setListeners((IterationListener)new StatsListener( statsStorage),new ScoreIterationListener(iterations));
        /**
         * Data Setup -> define how to load data into net:
         *  - recordReader = the reader that loads and converts image data pass in inputSplit to initialize
         *  - dataIter = a generator that only loads one batch at a time into memory to save memory
         *  - trainIter = uses MultipleEpochsIterator to ensure model runs through the data for all epochs
         **/
        ImageRecordReader recordReader = new ImageRecordReader(height, width, channels, labelMaker);
        DataSetIterator dataIter;
        MultipleEpochsIterator trainIter;


        log.info("Train model....");
        // Train without transformations
        recordReader.initialize(trainData, null);
        dataIter = new RecordReaderDataSetIterator(recordReader, batchSize, 1, numLabels);
        scaler.fit(dataIter);
        dataIter.setPreProcessor(scaler);
        trainIter = new MultipleEpochsIterator(epochs, dataIter);
        network.fit(trainIter);

        // Train with transformations
        for (ImageTransform transform : transforms) {
            System.out.print("\nTraining on transformation: " + transform.getClass().toString() + "\n\n");
            recordReader.initialize(trainData, transform);
            dataIter = new RecordReaderDataSetIterator(recordReader, batchSize, 1, numLabels);
            scaler.fit(dataIter);
            dataIter.setPreProcessor(scaler);
            trainIter = new MultipleEpochsIterator(epochs, dataIter);
            network.fit(trainIter);
        }

        log.info("Evaluate model....");
        recordReader.initialize(testData);
        dataIter = new RecordReaderDataSetIterator(recordReader, batchSize, 1, numLabels);
        scaler.fit(dataIter);
        dataIter.setPreProcessor(scaler);
        Evaluation eval = network.evaluate(dataIter);
        log.info(eval.stats(true));

        // Example on how to get predict results with trained model. Result for first example in minibatch is printed
        dataIter.reset();
        DataSet testDataSet = dataIter.next();
        List<String> allClassLabels = recordReader.getLabels();
        int labelIndex = testDataSet.getLabels().argMax(1).getInt(0);
        int[] predictedClasses = network.predict(testDataSet.getFeatures());
        String expectedResult = allClassLabels.get(labelIndex);
        String modelPrediction = allClassLabels.get(predictedClasses[0]);
        System.out.print("\nFor a single example that is labeled " + expectedResult + " the model predicted " + modelPrediction + "\n\n");
        if (save) {
            log.info("Save model....");
            String basePath = FilenameUtils.concat(System.getProperty("user.dir"), "src/main/saved/");
            ModelSerializer.writeModel(network, basePath + "model.bin", true);
        }
        log.info("****************Example finished********************");
    }

    private ConvolutionLayer convInit(String name, int in, int out, int[] kernel, int[] stride, int[] pad, double bias) {
        return new ConvolutionLayer.Builder(kernel, stride, pad).name(name).nIn(in).nOut(out).biasInit(bias).build();
    }

    private ConvolutionLayer conv3x3(String name, int out, double bias) {
        return new ConvolutionLayer.Builder(new int[]{3,3}, new int[] {1,1}, new int[] {1,1}).name(name).nOut(out).biasInit(bias).build();
    }

    private ConvolutionLayer conv5x5(String name, int out, int[] stride, int[] pad, double bias) {
        return new ConvolutionLayer.Builder(new int[]{5,5}, stride, pad).name(name).nOut(out).biasInit(bias).build();
    }

    private SubsamplingLayer maxPool(String name,  int[] kernel) {
        return new SubsamplingLayer.Builder(kernel, new int[]{2,2}).name(name).build();
    }

    private DenseLayer fullyConnected(String name, int out, double bias, double dropOut, Distribution dist) {
        return new DenseLayer.Builder().name(name).nOut(out).biasInit(bias).dropOut(dropOut).dist(dist).build();
    }

    public MultiLayerNetwork lenetModel() {
        /**

         85% Accuracy
         90% Precision Cytology 6 classes
         regularization: true
         l2: 0.05
         learning: 1e-2
         activation: IDENTITY, RELU
         weight: RELU
         optimization: LINE_GRADIENT_DESCENT


         **/
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
                .seed(seed)
                .iterations(iterations)
                .dist(new NormalDistribution(0.0, 0.01))
                .regularization(true).l2(0.5) // tried 0.05 //false //85%
                .activation(Activation.RELU) // RELU
                .learningRate(1e-3) // tried 0.00001, 0.00005, 0.000001
                .weightInit(WeightInit.RELU) //XAVIER
                .gradientNormalization(GradientNormalization.RenormalizeL2PerLayer)
                .optimizationAlgo(OptimizationAlgorithm.LINE_GRADIENT_DESCENT)//STOCHASTIC_GRADIENT_DESCENT
                .updater(new Adam())
                .list()
                .layer(0, convInit("cnn1", channels, 50 ,  new int[]{5, 5}, new int[]{1, 1}, new int[]{0, 0}, 0))
                .layer(1, maxPool("maxpool1", new int[]{2,2}))
                .layer(2, conv5x5("cnn2", 100, new int[]{5, 5}, new int[]{1, 1}, 0))
                .layer(3, maxPool("maxool2", new int[]{2,2}))
                .layer(4, new DenseLayer.Builder()
                        .nOut(500)
                        .dropOut(0.5)
                        .build())
                .layer(5, new OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
                        .nOut(numLabels)
                        .dropOut(0.5)
                        .activation(Activation.SOFTMAX)
                        .build())
                .backprop(true).pretrain(false)
                .setInputType(InputType.convolutional(height, width, channels))
                .build();

        return new MultiLayerNetwork(conf);

    }

    public MultiLayerNetwork alexnetModel() {
        /**
         * AlexNet model interpretation based on the original paper ImageNet Classification with Deep Convolutional Neural Networks
         * and the imagenetExample code referenced.
         * http://papers.nips.cc/paper/4824-imagenet-classification-with-deep-convolutional-neural-networks.pdf
         **/

        double nonZeroBias = 1;
        double dropOut = 0.5;

        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
                .seed(seed)
                .weightInit(WeightInit.DISTRIBUTION)
                .dist(new NormalDistribution(0.0, 0.01))
                .activation(Activation.RELU)
                .updater(new Nesterovs(0.9))
                .iterations(iterations)
                .gradientNormalization(GradientNormalization.RenormalizeL2PerLayer) // normalize to prevent vanishing or exploding gradients
                .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
                .learningRate(1e-2)
                .biasLearningRate(1e-2*2)
                .learningRateDecayPolicy(LearningRatePolicy.Step)
                .lrPolicyDecayRate(0.1)
                .lrPolicySteps(100000)
                .regularization(true)
                .l2(5 * 1e-4)
                .list()
                .layer(0, convInit("cnn1", channels, 96, new int[]{11, 11}, new int[]{4, 4}, new int[]{3, 3}, 0))
                .layer(1, new LocalResponseNormalization.Builder().name("lrn1").build())
                .layer(2, maxPool("maxpool1", new int[]{3,3}))
                .layer(3, conv5x5("cnn2", 256, new int[] {1,1}, new int[] {2,2}, nonZeroBias))
                .layer(4, new LocalResponseNormalization.Builder().name("lrn2").build())
                .layer(5, maxPool("maxpool2", new int[]{3,3}))
                .layer(6,conv3x3("cnn3", 384, 0))
                .layer(7,conv3x3("cnn4", 384, nonZeroBias))
                .layer(8,conv3x3("cnn5", 256, nonZeroBias))
                .layer(9, maxPool("maxpool3", new int[]{3,3}))
                .layer(10, fullyConnected("ffn1", 4096, nonZeroBias, dropOut, new GaussianDistribution(0, 0.005)))
                .layer(11, fullyConnected("ffn2", 4096, nonZeroBias, dropOut, new GaussianDistribution(0, 0.005)))
                .layer(12, new OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
                        .name("output")
                        .nOut(numLabels)
                        .activation(Activation.SOFTMAX)
                        .build())
                .backprop(true)
                .pretrain(false)
                .setInputType(InputType.convolutional(height, width, channels))
                .build();

        return new MultiLayerNetwork(conf);

    }

    public  MultiLayerNetwork customModel() {
        MultiLayerConfiguration conf =
                new NeuralNetConfiguration.Builder()
                        .trainingWorkspaceMode(WorkspaceMode.SEPARATE).seed(seed)
                        .activation(Activation.IDENTITY).weightInit(WeightInit.RELU)
                        .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
                        .updater(new AdaDelta())
                        .convolutionMode(ConvolutionMode.Same).list()
                        // block 1
                        .layer(0, new ConvolutionLayer.Builder(new int[] {7, 7}).name("image_array")
                                .nIn(inputShape[0]).nOut(16).build())
                        .layer(1, new BatchNormalization.Builder().build())
                        .layer(2, new ConvolutionLayer.Builder(new int[] {7, 7}).nIn(16).nOut(16)
                                .build())
                        .layer(3, new BatchNormalization.Builder().build())
                        .layer(4, new ActivationLayer.Builder().activation(Activation.RELU).build())
                        .layer(5, new SubsamplingLayer.Builder(SubsamplingLayer.PoolingType.AVG,
                                new int[] {2, 2}).build())
                        .layer(6, new DropoutLayer.Builder(0.5).build())

                        // block 2
                        .layer(7, new ConvolutionLayer.Builder(new int[] {5, 5}).nOut(32).build())
                        .layer(8, new BatchNormalization.Builder().build())
                        .layer(9, new ConvolutionLayer.Builder(new int[] {5, 5}).nOut(32).build())
                        .layer(10, new BatchNormalization.Builder().build())
                        .layer(11, new ActivationLayer.Builder().activation(Activation.RELU).build())
                        .layer(12, new SubsamplingLayer.Builder(SubsamplingLayer.PoolingType.AVG,
                                new int[] {2, 2}).build())
                        .layer(13, new DropoutLayer.Builder(0.5).build())

                        // block 3
                        .layer(14, new ConvolutionLayer.Builder(new int[] {3, 3}).nOut(64).build())
                        .layer(15, new BatchNormalization.Builder().build())
                        .layer(16, new ConvolutionLayer.Builder(new int[] {3, 3}).nOut(64).build())
                        .layer(17, new BatchNormalization.Builder().build())
                        .layer(18, new ActivationLayer.Builder().activation(Activation.RELU).build())
                        .layer(19, new SubsamplingLayer.Builder(SubsamplingLayer.PoolingType.AVG,
                                new int[] {2, 2}).build())
                        .layer(20, new DropoutLayer.Builder(0.5).build())

                        // block 4
                        .layer(21, new ConvolutionLayer.Builder(new int[] {3, 3}).nOut(128).build())
                        .layer(22, new BatchNormalization.Builder().build())
                        .layer(23, new ConvolutionLayer.Builder(new int[] {3, 3}).nOut(128).build())
                        .layer(24, new BatchNormalization.Builder().build())
                        .layer(25, new ActivationLayer.Builder().activation(Activation.RELU).build())
                        .layer(26, new SubsamplingLayer.Builder(SubsamplingLayer.PoolingType.AVG,
                                new int[] {2, 2}).build())
                        .layer(27, new DropoutLayer.Builder(0.5).build())


                        // block 5
                        .layer(28, new ConvolutionLayer.Builder(new int[] {3, 3}).nOut(256).build())
                        .layer(29, new BatchNormalization.Builder().build())
                        .layer(30, new ConvolutionLayer.Builder(new int[] {3, 3}).nOut(numLabels)
                                .build())
                        .layer(31, new GlobalPoolingLayer.Builder(PoolingType.AVG).build())
                        .layer(32, new ActivationLayer.Builder().activation(Activation.SOFTMAX).build())
                        .layer(33, new OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
                                .nOut(numLabels)
                                .activation(Activation.SOFTMAX)
                                .build())
                        .setInputType(InputType.convolutional(inputShape[2], inputShape[1],
                                inputShape[0]))
                        .backprop(true).pretrain(false).build();
        return new MultiLayerNetwork(conf);
    }

    public static void main(String[] args) throws Exception {
        new App().run(args);
    }
}
