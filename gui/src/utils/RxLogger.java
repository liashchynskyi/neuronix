package utils;

import com.jfoenix.controls.JFXTextArea;
import io.reactivex.Observable;
import javafx.application.Platform;
import javafx.scene.layout.AnchorPane;
import org.slf4j.Logger;

public class RxLogger {
    Logger logger;
    JFXTextArea console;

    public RxLogger(Logger logger) {
        this.logger = logger;
    }

    public void lo(String text) {
         Observable.create(subscriber -> {
            logger.info(text);
            subscriber.onNext(text);
        }).subscribe(res -> console.appendText(res + "\n"));
    }
}