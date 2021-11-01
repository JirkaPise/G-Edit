package gedit;

import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javax.imageio.ImageIO;

public class Gedit extends Application {

    Canvas canvas = null;
    Point2D lineStart = null;
    Point2D rectStart = null;
    Point2D rectEnd = null;
    Scene scene;

    private Color currColor = Color.RED;
    BorderPane root = new BorderPane();
    HBox hbox = new HBox();
    ColorPicker colorPicker = new ColorPicker(currColor);
    GraphicsContext gc;
    ComboBox<enumNastroj> comboboxNastroje = new ComboBox<>();
    Button buttonClear = new Button("Clear");
    Spinner<Integer> spinnerSirkaCary = new Spinner<>();
    Pane pane = new Pane();
    ObservableList<Rectangle> listRect = FXCollections.observableArrayList();
    Button buttonUloz = new Button("Ulož");
    Button buttonNacti = new Button("Načti");

    @Override
    public void start(Stage primaryStage) {

        canvas = new Canvas();
        gc = canvas.getGraphicsContext2D();
        pane.getChildren().add(canvas);
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

        buttonNacti.setOnAction((event) -> {

            FileChooser fileChooser = new FileChooser();

            FileChooser.ExtensionFilter extFilt = new FileChooser.ExtensionFilter("png files", "*.png");
            fileChooser.getExtensionFilters().add(extFilt);
            primaryStage.setWidth(1600);
            primaryStage.setHeight(1200);
            File file = fileChooser.showOpenDialog(primaryStage);

            Image image = new Image(file.toURI().toString());

            if (!(image.getWidth() <= 500 || image.getHeight() <= 250 || image.getWidth() >= 1600 || image.getHeight() >= 1200)) {
                newCanvas(primaryStage, image.getWidth(), image.getHeight());
                gc.drawImage(image, 0, 0, image.getWidth(), image.getHeight());
            } else if (image.getWidth() > 1600 || image.getHeight() > 1200) {
                newCanvas(primaryStage, image.getWidth() / 2, image.getHeight() / 2);
                gc.drawImage(image, 0, 0, image.getWidth() / 2, image.getHeight() / 2);
            } else if ((image.getWidth() < 500 || image.getHeight() < 250)) {
                newCanvas(primaryStage, 500, 250);
                gc.drawImage(image, 0, 0, 500, 250);
            }
        });

        buttonUloz.setOnAction(
                (event) -> {
                    FileChooser fileChooser = new FileChooser();

                    FileChooser.ExtensionFilter extFilt = new FileChooser.ExtensionFilter("png files", "*.png");
                    fileChooser.getExtensionFilters().add(extFilt);

                    File file = fileChooser.showSaveDialog(primaryStage);

                    if (file != null) {
                        try {
                            WritableImage writableImage = new WritableImage((int) canvas.getWidth(), (int) canvas.getHeight());
                            canvas.snapshot(null, writableImage);

                            RenderedImage renderedImage = SwingFXUtils.fromFXImage(writableImage, null);
                            ImageIO.write(renderedImage, "png", file);

                        } catch (IOException e) {
                            System.out.println("File output error");
                        }
                    }
                }
        );

        hbox.getChildren()
                .addAll(colorPicker, comboboxNastroje, buttonClear, spinnerSirkaCary, buttonUloz, buttonNacti);
        root.setCenter(pane);

        root.setTop(hbox);

        canvas.widthProperty()
                .bind(root.widthProperty());
        canvas.heightProperty()
                .bind(root.heightProperty());

        scene = new Scene(root, 800, 600);

        primaryStage.setTitle(
                "G-Edit 3000");
        primaryStage.setScene(scene);

        primaryStage.show();
    }

    private void newCanvas(Stage primaryStage, double width, double height) {
        if (canvas != null) {
            canvas = new Canvas(width, height);

            root.setPrefSize(width, height + 30);
            scene.setRoot(root);
            root.setCenter(pane);
            pane.getChildren().add(canvas);
            root.setTop(hbox);
            GraphicsContext gc = canvas.getGraphicsContext2D();
            primaryStage.setWidth(width + 10);
            primaryStage.setHeight(height + 60);
            clearAll();
        }
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
                        case KRUH:
                            gc.setFill(currColor);
                            rectStart = new Point2D(event.getX(), event.getY());
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
                            gc.setLineWidth(spinnerSirkaCary.getValue() * 3
                            );
                            lineStart = new Point2D(event.getX(), event.getY());
                            break;
                        case OBDELNIK:

                            break;
                        case KRUH:

                            break;
                        default:
                            break;
                    }
                }

            });

            canvas.addEventHandler(MouseEvent.MOUSE_RELEASED, (event) -> {
                rectEnd = null;
                pane.getChildren().clear();
                pane.getChildren().add(canvas);
                if (null != comboboxNastroje.getValue()) {
                    switch (comboboxNastroje.getValue()) {
                        case TUZKA:
                            break;
                        case GUMA:
                            break;
                        case OBDELNIK:
                            gc.setFill(currColor);
                            gc.fillRect(rectStart.getX(), rectStart.getY(), event.getX() - rectStart.getX(), event.getY() - rectStart.getY());
                            break;
                        case KRUH:
                            gc.setFill(currColor);
                            gc.fillOval(rectStart.getX(), rectStart.getY(), event.getX() - rectStart.getX(), event.getY() - rectStart.getY());
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
