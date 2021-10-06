package gedit;

import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javax.imageio.ImageIO;

public class Gedit extends Application {

    Canvas canvas = null;
    Point2D lineStart = null;
    private Color currColor = Color.RED;
    BorderPane root = new BorderPane();
    HBox hbox = new HBox();
    ColorPicker colorPicker = new ColorPicker(currColor);
    GraphicsContext gc ;

    @Override
    public void start(Stage primaryStage) {

        canvas = new Canvas();
        gc = canvas.getGraphicsContext2D();
        
        colorPicker.setOnAction((ActionEvent event) -> {
            currColor = colorPicker.getValue();
        });

        canvas.widthProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            draw();
        });

        canvas.heightProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            draw();
        });

        hbox.getChildren().addAll(colorPicker);
        root.setCenter(canvas);
        root.setTop(hbox);

        canvas.widthProperty().bind(root.widthProperty());
        canvas.heightProperty().bind(root.heightProperty());

        Scene scene = new Scene(root, 600, 400);

        primaryStage.setTitle("G-Edit 3000");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    private void draw() {
        if (canvas != null) {    
            canvas.addEventHandler(MouseEvent.MOUSE_PRESSED, (MouseEvent event) -> {
                gc.setStroke(currColor);
                lineStart = new Point2D(event.getX(), event.getY());
            });

            canvas.addEventHandler(MouseEvent.MOUSE_DRAGGED, (MouseEvent event) -> {
                gc.strokeLine(lineStart.getX(), lineStart.getY(), event.getX(), event.getY());
                lineStart = new Point2D(event.getX(), event.getY());
            });

            canvas.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent event) -> {
//                if (event.getButton() == MouseButton.SECONDARY) {
//                    clearAll(gc);
//                }
            });
        }
    }

    private void clearAll() {
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

}
