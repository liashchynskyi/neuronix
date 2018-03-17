package utils;

import com.jfoenix.controls.JFXTextArea;
import java.io.IOException;
import java.io.OutputStream;
import javafx.application.Platform;
import javafx.scene.control.TextArea;

public class TextAreaOutputStream extends OutputStream {
    
    private TextArea textArea;
    
    public TextAreaOutputStream (TextArea textArea) {
        this.textArea = textArea;
    }
    
    @Override
    public void write (int b) throws IOException {
        Platform.runLater(new Runnable() {
            @Override
            public void run () {
                textArea.appendText(String.valueOf((char) b));
                textArea.positionCaret(textArea.getLength());
            }
        });
    }
}