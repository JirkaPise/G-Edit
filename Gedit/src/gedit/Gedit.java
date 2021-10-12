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
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
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
    Point2D rectStart = null;
    private Color currColor = Color.RED;
    BorderPane root = new BorderPane();
    HBox hbox = new HBox();
    ColorPicker colorPicker = new ColorPicker(currColor);
    GraphicsContext gc;
    ComboBox<enumNastroj> comboboxNastroje = new ComboBox<>();
    Button buttonClear = new Button("Clear");
    Spinner<Integer> spinnerSirkaCary = new Spinner<>();

    @Override
    public void start(Stage primaryStage) {

        canvas = new Canvas();
        gc = canvas.getGraphicsContext2D();
        comboboxNastroje.getItems().addAll(enumNastroj.values());
        comboboxNastroje.getSelectionModel().select(enumNastroj.TUZKA);
        SpinnerValueFactory valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 20, 2);
        spinnerSirkaCary.setValueFactory(valueFactory);
        

        colorPicker.setOnAction((ActionEvent event) -> {
            currColor = colorPicker.getValue();
        });

        canvas.widthProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            draw();
        });

        canvas.heightProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            draw();
        });
        
        buttonClear.setOnAction((event) -> {
            clearAll();
        });

        hbox.getChildren().addAll(colorPicker, comboboxNastroje,buttonClear,spinnerSirkaCary);
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
                if (null != comboboxNastroje.getValue()) {
                    switch (comboboxNastroje.getValue()) {
                        case TUZKA:
                            gc.setStroke(currColor);
                            gc.setLineWidth(spinnerSirkaCary.getValue());
                            lineStart = new Point2D(event.getX(), event.getY());
                            break;
                        case GUMA:
                            gc.setStroke(Color.WHITESMOKE);
                            lineStart = new Point2D(event.getX(), event.getY());
                            break;
                        case OBDELNIK:
                            rectStart = new Point2D(event.getX(), event.getY());
                            break;
                        case KRUH:
                            break;
                        default:
                            break;
                    }
                }

            });

            canvas.addEventHandler(MouseEvent.MOUSE_DRAGGED, (MouseEvent event) -> {
                if (null != comboboxNastroje.getValue()) {
                    switch (comboboxNastroje.getValue()) {
                        case TUZKA:
                            gc.strokeLine(lineStart.getX(), lineStart.getY(), event.getX(), event.getY());
                            gc.setLineWidth(spinnerSirkaCary.getValue());
                            lineStart = new Point2D(event.getX(), event.getY());
                            break;
                        case GUMA:
                            gc.strokeLine(lineStart.getX(), lineStart.getY(), event.getX(), event.getY());
                            gc.setLineWidth(spinnerSirkaCary.getValue() * 3);
                            lineStart = new Point2D(event.getX(), event.getY());
                            break;
                        case OBDELNIK:
                            gc.setFill(currColor);
                            gc.fillPolygon(new double[]{rectStart.getX(), rectStart.getX(), event.getX(), event.getX()}, new double[]{rectStart.getY(), event.getY(), event.getY(), rectStart.getY()}, 4);
                            break;
                        case KRUH:
                            break;
                        default:
                            break;
                    }
                }

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
