package utils;

import com.jfoenix.controls.JFXTextArea;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Stack;

public class Utils {

    static OutputStream os;
    static StringProperty log = new SimpleStringProperty("");

    public static String getLog() {
        return log.get();
    }

    public static StringProperty logProperty() {
        return log;
    }

    public static void log(String log) {
        Utils.log.set(log);
    }

    public static void switchPane(AnchorPane pane, Stack stack, String name, Label description) {
        pane.toFront();
        stack.pop();
        stack.push(pane);
        description.setText(name);
    }
    public static void switchPane(AnchorPane pane2, AnchorPane pane3,
                                  Stack stack2, Stack stack3,
                                  String name2, String name3,
                                  Label desc2, Label desc3, TranslateTransition open3, TranslateTransition open2) {

        open3 = new TranslateTransition(new Duration(350), pane3);
        open3.setToX(0);

        open2 = new TranslateTransition(new Duration(350), pane2);
        open2.setToY(0);

        pane2.toFront();
        pane3.toFront();

        AnchorPane pane33 = (AnchorPane)  stack3.pop();
        AnchorPane pane22 = (AnchorPane)  stack2.pop();

        TranslateTransition hide3 = new TranslateTransition(new Duration(550), pane33);
        TranslateTransition hide2 = new TranslateTransition(new Duration(550), pane22);

        hide3.setToX(pane33.getPrefWidth());
        hide2.setToY((pane22.getPrefHeight() + 32));

        stack2.push(pane2);
        stack3.push(pane3);
        desc2.setText(name2);
        desc3.setText(name3);
        if (pane3.getTranslateX()!= 0){
            open3.play();
            hide3.play();
            open2.play();
            hide2.play();
        }
//        if (pane2.getTranslateY() != 0) {
//
//        }

    }

    public static void setLogger(JFXTextArea console) {
        os = new TextAreaOutputStream(console);
        StreamAppender.setStaticOutputStream(os);
    }

}