import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class AppLoader extends Application {
    private double xOffset, yOffset;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("main.fxml"));

        root.setOnMousePressed((e) -> {
            xOffset = e.getSceneX();
            yOffset = e.getSceneY();
        });
        root.setOnMouseDragged((e) -> {
            stage.setX(e.getScreenX() - xOffset);
            stage.setY(e.getScreenY() - yOffset);
        });

        Scene scene = new Scene(root, 1000, 600);
        scene.setFill(Color.TRANSPARENT);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setScene(scene);

        stage.show();
    }
}

