package utils;

import com.jfoenix.controls.JFXTextArea;
import javafx.application.Platform;
import javafx.concurrent.Task;
import org.junit.internal.runners.statements.RunAfters;

import java.io.IOException;
import java.io.OutputStream;

public class TextAreaOutputStream extends OutputStream{
    private JFXTextArea textArea;

    public TextAreaOutputStream(JFXTextArea textArea) {
        this.textArea = textArea;
    }

    @Override
    public void write(int b) throws IOException {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                textArea.appendText(String.valueOf((char)b));
                textArea.positionCaret(textArea.getLength());
            }
        });
    }
}