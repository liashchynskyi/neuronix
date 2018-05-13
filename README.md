<div align="center">
<img src="https://i.imgur.com/W1iyehs.png">
<br><br>
<img src="	https://img.shields.io/github/license/liashchynskyi/neuronix.svg">
<img src="https://img.shields.io/badge/java-1.8.0__151-lightgrey.svg">
<img src="https://img.shields.io/badge/nd4j-v.0.9.1-red.svg">
<img src="https://img.shields.io/badge/dl4j-v.0.9.1-orange.svg">
<img src="https://img.shields.io/badge/cuda-v8.0-blue.svg">
<img src="https://img.shields.io/badge/backend-cpu|gpu-green.svg">
</div>

---

<div align="center">
<img src="https://i.imgur.com/eO3iDfK.png">
</div>

**Neuronix** is a program module was developed for biomedical image classification using **GPU** and convolutional neural networks. Built with [DL4J](https://deeplearning4j.org/).

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

If you have AMD CPU see [this](https://github.com/deeplearning4j/deeplearning4j/issues/4287) and [this](https://deeplearning4j.org/native#fallback-mode) to avoid mistakes :smirk: