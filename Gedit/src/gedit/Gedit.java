package gedit;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class Gedit extends Application {

    private final BorderPane root = new BorderPane();
    private final Canvas canvas = new Canvas(1600, 900);
    private final GraphicsContext gc = canvas.getGraphicsContext2D();

    @Override
    public void start(Stage primaryStage) {
        nastavCanvas();
        root.setCenter(canvas);
        Scene scene = new Scene(root);
        primaryStage.setTitle("G-Edit 3000");
        primaryStage.setScene(scene);
        primaryStage.show();

    }

    private void nastavCanvas() {
        canvas.addEventHandler(MouseEvent.MOUSE_CLICKED, (event) -> {

        });

        canvas.addEventHandler(MouseEvent.MOUSE_DRAGGED, (event) -> {

        });

        canvas.addEventHandler(MouseEvent.MOUSE_RELEASED, (event) -> {

        });

    }

    public static void main(String[] args) {
        launch(args);
    }

}
