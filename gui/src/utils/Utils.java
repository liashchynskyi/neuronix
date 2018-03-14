package utils;

import com.jfoenix.controls.JFXProgressBar;
import com.jfoenix.controls.JFXTextArea;
import com.sun.management.OperatingSystemMXBean;
import eu.hansolo.medusa.Gauge;
import eu.hansolo.medusa.GaugeBuilder;
import eu.hansolo.tilesfx.Tile;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Stack;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.util.Duration;
import org.apache.commons.io.FilenameUtils;

public class Utils {
    
    static OutputStream os;
    
    public static void switch3Pane (AnchorPane pane3, Stack stack, String name, Label description,
                                    TranslateTransition open3) {
        open3 = new TranslateTransition(new Duration(350), pane3);
        open3.setToX(0);
        
        pane3.toFront();
        
        
        AnchorPane          pane33 = (AnchorPane) stack.pop();
        TranslateTransition hide3  = new TranslateTransition(new Duration(100), pane33);
        hide3.setToX(pane33.getPrefWidth());
        
        stack.push(pane3);
        description.setText(name);
        
        if (pane3.getTranslateX() != 0) {
            open3.play();
            hide3.play();
        }
        
        
    }
    
    public static void switchPane (AnchorPane pane2, AnchorPane pane3, Stack stack2, Stack stack3,
                                   String name2, String name3, Label desc2, Label desc3,
                                   TranslateTransition open3, TranslateTransition open2) {
        
        open3 = new TranslateTransition(new Duration(350), pane3);
        open3.setToX(0);
        
        open2 = new TranslateTransition(new Duration(350), pane2);
        open2.setToY(0);
        
        pane2.toFront();
        pane3.toFront();
        
        AnchorPane pane33 = (AnchorPane) stack3.pop();
        AnchorPane pane22 = (AnchorPane) stack2.pop();
        
        TranslateTransition hide3 = new TranslateTransition(new Duration(100), pane33);
        TranslateTransition hide2 = new TranslateTransition(new Duration(100), pane22);
        
        hide3.setToX(pane33.getPrefWidth());
        hide2.setToY((pane22.getPrefHeight() + 32));
        
        stack2.push(pane2);
        stack3.push(pane3);
        desc2.setText(name2);
        desc3.setText(name3);
        if (pane3.getTranslateX() != 0) {
            open3.play();
            hide3.play();
            open2.play();
            hide2.play();
        }
    }
    
    public static void setLogger (JFXTextArea console) {
        os = new TextAreaOutputStream(console);
        StreamAppender.setStaticOutputStream(os);
        
    }
    
    public static Gauge createGauge (final Gauge.SkinType TYPE, int size, double max, String unit) {
        return GaugeBuilder.create()
                           .skinType(TYPE)
                           .prefSize(size, size)
                           .animated(true)
                           .unit(unit)
                           .valueColor(Color.web("#ffffff"))
                           .unitColor(Color.web("#ffffff"))
                           .barColor(Tile.ORANGE)
                           .needleColor(Color.web("#ffffff"))
                           .barBackgroundColor(Color.web("#8e4a49"))
                           .tickLabelColor(Color.web("#ffffff"))
                           .majorTickMarkColor(Color.web("#ffffff"))
                           .minorTickMarkColor(Color.web("#ffffff"))
                           .mediumTickMarkColor(Color.web("#ffffff"))
                           .backgroundPaint(Paint.valueOf("#755c62"))
                           .maxValue(max)
                           .build();
    }
    
    public static void monitoreRAM (OperatingSystemMXBean bean, Tile tile) {
        new Thread(new Runnable() {
            @Override
            public void run () {
                Timeline timeline = new Timeline(new KeyFrame(Duration.millis(2500), ae -> {
                    
                    long   totalMem = bean.getTotalPhysicalMemorySize();
                    long   freeMem  = bean.getFreePhysicalMemorySize();
                    double usageMem = (totalMem - freeMem) / 1048576.00; // in GB
                    
                    tile.setValue(usageMem / 1024.00);
                }));
                timeline.setCycleCount(Animation.INDEFINITE);
                timeline.play();
            }
        }).run();
    }
    
    public static void updateProgress (Gauge gauge, int epoch, int numEpochs) {
        Platform.runLater(() -> {
            double value = 0.0;
            value = (double) epoch / (double) numEpochs * 100;
            gauge.setValue(value);
        });
    }
    
    public static void updateProgress (JFXProgressBar bar, int image, int numImages) {
        Platform.runLater(() -> {
            double value = 0.0;
            value = (double) image / (double) numImages * 100;
            bar.setProgress(value);
        });
    }
    
    public static void updateProgress (JFXProgressBar bar) {
        bar.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);
    }
    
    public static void writeJSON(String path, String json) throws IOException {
        FileWriter file = new FileWriter(path);
        file.write(json);
        file.close();
    }
    
    public static String readJSON (String path) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, Charset.forName("UTF8"));
    }
    
    
}