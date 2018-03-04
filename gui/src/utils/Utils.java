package utils;

import com.jfoenix.controls.JFXTextArea;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

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
    public static void switchPane(  AnchorPane pane2, AnchorPane pane3,
                                    Stack stack2, Stack stack3,
                                    String name2, String name3,
                                    Label desc2, Label desc3 ) {
        pane2.toFront();
        pane3.toFront();
        stack2.pop();
        stack3.pop();
        stack2.push(pane2);
        stack3.push(pane3);
        desc2.setText(name2);
        desc3.setText(name3);
    }

    public static void setLogger(JFXTextArea console) {
        os = new TextAreaOutputStream(console);
        StreamAppender.setStaticOutputStream(os);
    }

}