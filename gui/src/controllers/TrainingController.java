package controllers;

import com.jfoenix.controls.JFXSlider;
import com.jfoenix.controls.JFXTextField;
import eu.hansolo.medusa.Gauge;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import javafx.application.Platform;
import javafx.scene.control.Label;
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
import org.slf4j.Logger;
import utils.Utils;
import utils.models.json.JsonModelBuilder;
import utils.models.json.Model;
import utils.prefs.Prefs;

public class TrainingController {
    private String imagesPath;
    private ChooseModelController chooseModelController;
    private JFXTextField epochsNumber, iterNumber, learningRate;
    private JFXSlider splitTrainTest;
    private Random    rng;
    private Gauge     progress;
    private Logger    log;
    private Prefs     prefs;
    private Label     label;
    
    
    public TrainingController (ChooseModelController chooseModelController,
                               JFXTextField epochsNumber, JFXTextField iterNumber,
                               JFXTextField learningRate, JFXSlider splitTrainTest,
                               Gauge progress, Logger log, Prefs prefs, Label label) {
        this.chooseModelController = chooseModelController;
        this.epochsNumber = epochsNumber;
        this.iterNumber = iterNumber;
        this.learningRate = learningRate;
        this.splitTrainTest = splitTrainTest;
        this.progress = progress;
        this.log = log;
        this.prefs = prefs;
        this.label = label;
    }
    
    public String getImagesPath () {
        return imagesPath;
    }
    
    public void setImagesPath (String imagesPath) {
        this.imagesPath = imagesPath;
    }
    
    public void train (Gauge a, Gauge p, Gauge r, Gauge f) {
        
        if (chooseModelController.getCurrentSavedModel() != null && getImagesPath() != null)
        new Thread(() -> {
            try {
                log.info("Build model from JSON...");
                Utils.updateTrainLabel(this.label, "Читання файлу моделі...");
                String modeljson = Utils.readJSON(this.chooseModelController.getCurrentSavedModel());
                Utils.updateTrainLabel(this.label, "Декодування моделі...");
                Model             encoded = Utils.decodeJson(modeljson);
                JsonModelBuilder  builder = new JsonModelBuilder(encoded, prefs);
                Utils.updateTrainLabel(this.label, "Будування моделі...");
                MultiLayerNetwork network = builder.init(Integer.parseInt(this.iterNumber.getText()),
                                                         Double.parseDouble(this.learningRate.getText()))
                                                   .build();
    
                Utils.updateTrainLabel(this.label, "Завантаження зображень...");
                this.rng = new Random(42);
                File                     parentDir       = new File(getImagesPath());
                FileSplit                filesInDir      = new FileSplit(parentDir, NativeImageLoader.ALLOWED_FORMATS, rng);
                ParentPathLabelGenerator labelMaker      = new ParentPathLabelGenerator();
                BalancedPathFilter       pathFilter      = new BalancedPathFilter(rng, NativeImageLoader.ALLOWED_FORMATS, labelMaker);
                Utils.updateTrainLabel(this.label, "Формування навчальної та тестової вибірки...");
                InputSplit[]             filesInDirSplit = filesInDir.sample(pathFilter, (int) splitTrainTest.getValue(), (int) 100 - this.splitTrainTest.getValue());
                InputSplit               trainData       = filesInDirSplit[0];
                InputSplit               testData        = filesInDirSplit[1];
                
                log.info("Read images...");
                Utils.updateTrainLabel(this.label, "Читання зображень...");
                
                ImageRecordReader recordReader = new ImageRecordReader(encoded.getImageSize(), encoded.getImageSize(), encoded.getChannels(), labelMaker);
                ImageTransform    transform    = new MultiImageTransform(rng, new CropImageTransform(10), new FlipImageTransform(), new ScaleImageTransform(10), new WarpImageTransform(10));
                Utils.updateTrainLabel(this.label, "Нормалізація даних...");
                DataNormalization scaler = new ImagePreProcessingScaler(0, 1);
                recordReader.initialize(trainData, transform);
    
                DataSetIterator dataIter = new RecordReaderDataSetIterator(recordReader, encoded.getBatchSize(), 1, encoded.getNumLabels());
    
                scaler.fit(dataIter);
                dataIter.setPreProcessor(scaler);
                StatsStorage statsStorage = new InMemoryStatsStorage();
                network.setListeners((IterationListener)new StatsListener(statsStorage), new ScoreIterationListener(encoded.getIterations()));
                
                log.info("Train model...");
                Utils.updateTrainLabel(this.label, "Навчання моделі...");
                
                for (int e = 1; e <= Integer.parseInt(epochsNumber.getText()); e++) {
                    network.fit(dataIter);
                    Utils.updateProgress(progress, e, Integer.parseInt(epochsNumber.getText()));
                }
    
                log.info("Evaluate model.....");
                Utils.updateTrainLabel(this.label, "Перевірка моделі на тестовій вибірці...");
                recordReader.initialize(testData);
                dataIter = new RecordReaderDataSetIterator(recordReader, encoded.getBatchSize(), 1, encoded.getNumLabels());
                scaler.fit(dataIter);
                dataIter.setPreProcessor(scaler);
                Evaluation eval = network.evaluate(dataIter);
                log.info(eval.stats(true));
                Utils.updateTrainLabel(this.label, "Вивід результатів...");
                Utils.updateScores(a, p, r, f, new double[]{
                    eval.accuracy(),
                    eval.precision(),
                    eval.recall(),
                    eval.f1()
                });
                if (prefs.currentSaveStateProperty().get()) {
                    log.info("Save model....");
                    Utils.updateTrainLabel(this.label, "Зберігання моделі...");
                    String basePath = prefs.getCurrentLoadDir();
                    ModelSerializer.writeModel(network, basePath + "\\" + encoded.getModelName() + "_" + encoded.getImageSize() + ".bin", true);
                }
                log.info("Training is done! Returning to main thread...");
                Utils.updateTrainLabel(this.label, "Готово! Навчання завершено!");
                return;
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
        
    }
    
}