<div align="center">
<h1><img src="https://i.imgur.com/W1iyehs.png"></h1>
<img src="	https://img.shields.io/github/license/liashchynskyi/neuronix.svg">
<img src="https://img.shields.io/badge/java-1.8.0__151-lightgrey.svg">
<img src="https://img.shields.io/badge/nd4j-v.0.9.1-red.svg">
<img src="https://img.shields.io/badge/dl4j-v.0.9.1-orange.svg">
<img src="https://img.shields.io/badge/cuda-v8.0-blue.svg">
<img src="https://img.shields.io/badge/backend-cpu|gpu-green.svg">
</div>

<div align="center">
<img src="https://i.imgur.com/eO3iDfK.png">
</div>

**Neuronix** is a program module was developed for biomedical image classification using **GPU** and convolutional neural networks. Built with [DL4J](https://deeplearning4j.org/).

- <a href="#features">Features</a>
- <a href="#system-requirements">System requirements</a>
- <a href="#installation">Installation</a>
- <a href="#how-to-build-a-model">How to build a model?</a>
- <a href="#training">Training</a>
- <a href="#classification">Classification</a>
- <a href="#configuration">Configuration</a>
- <a href="#switching-to-gpu">Switching to GPU</a>


# Features

 - Building your own CNN model or use [pre-trained](https://github.com/liashchynskyi/neuronix/tree/master/pre_trained)
 - Saving/loading model to/from **json** file
 - Separated classes to train and test your model
 - Tune your model so as you want

If you want to add something else &mdash;  all feature requests and contributions are welcome!

# System requirements
To use this project, your computer must appropriates to these minimal requirements:

 - Dual Core **CPU**, 2.5 Ghz (something like AMD Athlon 64 x2 4800+ or faster)
 - 4 GB of **RAM** (you can even use **DDR2 RAM** but it's slightly **slower**)
 - **GPU** with **2 GB** of memory and **CUDA 8.0** support

And that's it :wink:

# Installation

Before you begin need to install the following software on your computer:

 1. **CUDA 8.0** library (download [here](https://developer.nvidia.com/cuda-toolkit-archive))
 2. **cuDNN v6.0** library for CUDA 8.0 (download [here](https://developer.nvidia.com/rdp/cudnn-archive))
 3. **Java 8** (1.8.0_151 or newer, download [here](http://www.oracle.com/technetwork/java/javase/downloads/java-archive-javase8-2177648.html?printOnly=1))

If you have AMD CPU see [this](https://github.com/deeplearning4j/deeplearning4j/issues/4287) and [this](https://deeplearning4j.org/native#fallback-mode) to avoid mistakes :smirk: Then download JAR from [releases page](https://github.com/liashchynskyi/neuronix/releases) and add it to your project.

# How to build a model?

You can build your own models by this way.
```java
import neuronix.models.json.Layer;
import neuronix.models.json.Model;
import neuronix.utils.Utils;

public class Test {
    public static void main (String[] args) throws IOException {
        Model model = new Model();
        model.setModelName("CoolModel");
        model.setImageSize(224);
        model.setChannels(3);
        model.setBatchSize(5);
        model.setSeed(42);
        model.setIterations(1);
        model.setRegularization(true);
        model.setL2(1e-54);
        model.setLearningRate(1e-7);
        model.setNumLabels(5);
        model.setMiniBatch(true);
        model.setActivation("relu");
        model.setWeightInit("relu");
        model.setGradientNormalization("RenormalizeL2PerLayer");
        model.setOptimizationAlgo("STOCHASTIC_GRADIENT_DESCENT");
        model.setUpdater("nesterovs");
        model.setMomentum(0.9);

        Layer initial = new Layer();
        initial.setId(0);
        initial.setType("init");
        initial.setName("cnn1");
        initial.setOut(50);
        initial.setKernel(new int[]{5, 5});
        initial.setStride(new int[]{1, 1});
        initial.setPadding(new int[]{0, 0});
        initial.setBias(0);

        Layer pool1 = new Layer();
        pool1.setId(1);
        pool1.setType("pool");
        pool1.setName("maxpool1");
        pool1.setKernel(new int[]{2, 2});
        pool1.setStride(new int[]{2, 2});

        Layer conv2 = new Layer();
        conv2.setId(2);
        conv2.setType("conv");
        conv2.setName("cnn1");
        conv2.setOut(100);
        conv2.setKernel(new int[]{5, 5});
        conv2.setStride(new int[]{1, 1});
        conv2.setPadding(new int[]{0, 0});
        conv2.setBias(0);

        Layer dense = new Layer();
        dense.setId(3);
        dense.setType("dense");
        dense.setOut(500);
        dense.setActivation("relu");

        Layer output = new Layer();
        output.setId(4);
        output.setType("output");
        output.setOut(5);
        output.setActivation("softmax");
        output.setLoss("NEGATIVELOGLIKELIHOOD");

        model.addLayer(initial);
        model.addLayer(pool1);
        model.addLayer(conv2);
        model.addLayer(dense);
        model.addLayer(output);

        String modeljson = Utils.encodeJson(model);
        Utils.writeJSON("path", modeljson);
    }
}
```

Or you can load a previously created model as shown [here](https://github.com/liashchynskyi/neuronix/blob/master/json/generated.json). After that you can build your model:
```java
JsonModelBuilder  builder = new JsonModelBuilder(model);
MultiLayerNetwork network = builder.init(0, 0).build();
```

# Training
```java
Trainer trainer = new Trainer(200, 1, 1e-3, 80);
trainer.setImagesPath('path/to/your/images/jpg');
trainer.setPathToNeuralNetModel('your/json/model');
trainer.setRandomSeed(42);
double[] results = trainer.train();
```

# Classification
```java
Classifier classifier = new Classifier("path/to/images", "savedModelNameWithoutBinExtension", new Random(42));
ObservableList<ClassificationResult> results = classifier.classify();
```

# Configuration
Also you can define the following params by using a `Prefs` class.

```java
Prefs.setCurrentLoadDir("path"); //where json models are stored
Prefs.setCurrentSaveDir("path"); //where trained .bin models are stored
Prefs.setCurrentSaveState(true); //if true - your model will be saved after training
Prefs.setCurrentWorkspaceState(false); // if true - set SINGLE workspace mode
```

[More](https://deeplearning4j.org/workspaces) about workspaces.

# Switching to GPU

Want to do it faster? Switch to GPU by setting system var `BACKEND_GPU_PRIORITY` to a higher value than `BACKEND_CPU_PRIORITY`.