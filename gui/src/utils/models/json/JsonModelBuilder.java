package utils.models.json;


import java.util.List;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.GradientNormalization;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration.ListBuilder;
import org.deeplearning4j.nn.conf.Updater;
import org.deeplearning4j.nn.conf.distribution.NormalDistribution;
import org.deeplearning4j.nn.conf.layers.ConvolutionLayer;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.conf.layers.SubsamplingLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.learning.config.IUpdater;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import org.nd4j.linalg.lossfunctions.LossFunctions.LossFunction;

public class JsonModelBuilder {
    
    private Model             model;
    private ListBuilder       listBuilder;
    private MultiLayerNetwork network;
    
    public JsonModelBuilder (Model model) {
        this.model = model;
    }
    
    
    public JsonModelBuilder init () {
        listBuilder = new NeuralNetConfiguration.Builder()
            .seed(this.model.getSeed())
            .iterations(this.model.getIterations())
            .dist(new NormalDistribution(0.0, 0.01))
            .regularization(this.model.getRegularization())
            .l2(this.model.getL2())
            .activation(Activation.fromString(this.model.getActivation().toUpperCase()))
            .learningRate(this.model.getLearningRate())
            .weightInit(WeightInit.valueOf(this.model.getWeightInit().toUpperCase()))
            .gradientNormalization(GradientNormalization.valueOf(this.model.getGradientNormalization()))
            .optimizationAlgo(OptimizationAlgorithm.valueOf(this.model.getOptimizationAlgo()))
            .updater(Updater.valueOf(this.model.getUpdater().toUpperCase()))
            .momentum(this.model.getMomentum())
            .miniBatch(this.model.getMiniBatch())
            .list();
        return this;
    }
    
    public MultiLayerNetwork build () {
        
        List<Layer> layers = this.model.getLayers();
        for (Layer l : layers) {
            String type = l.getType();
            switch (type) {
                case "init": {
                    this.listBuilder.layer(l.getId(), new ConvolutionLayer
                        .Builder(l.getKernel(), l.getStride(), l.getPadding()).name(l.getName())
                                                                              .nIn(l.getIn())
                                                                              .nOut(l.getOut())
                                                                              .biasInit(l.getBias())
                                                                              .build());
                    break;
                }
                case "conv": {
                    this.listBuilder.layer(l.getId(), new ConvolutionLayer
                        .Builder(l.getKernel(), l.getStride(), l.getPadding()).name(l.getName())
                                                                              .nOut(l.getOut())
                                                                              .biasInit(l.getBias())
                                                                              .build());
                    break;
                }
                case "pool": {
                    this.listBuilder.layer(l.getId(), new SubsamplingLayer.Builder(l.getKernel(),
                                                                                   l.getStride())
                                                                                    .name(l.getName())
                                                                                    .build());
                    break;
                }
                case "dense": {
                    this.listBuilder.layer(l.getId(), new DenseLayer.Builder()
                                                                    .nOut(l.getOut())
                                                                    .activation(Activation.fromString(l.getActivation().toUpperCase()))
                                                                    .build());
                    break;
                }
                case "output": {
                    this.listBuilder.layer(l.getId(), new OutputLayer.Builder(LossFunction.valueOf(l.getLoss().toUpperCase()))
                        .nOut(l.getOut())
                        .activation(Activation.fromString(l.getActivation().toUpperCase()))
                        .build());
                    break;
                }
            }
            
        }
        
        return new MultiLayerNetwork(this.listBuilder.build());
    }
}