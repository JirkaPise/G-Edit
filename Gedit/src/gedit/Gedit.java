package gedit;

import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.Observable;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventType;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javax.imageio.ImageIO;
import seznam.AbstrDoubleList;

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
    int pocet = 0;
    boolean jePosledni = true;
    ComboBox<enumNastroj> comboboxNastroje = new ComboBox<>();
    Button buttonClear = new Button("Clear");
    Spinner<Integer> spinnerSirkaCary = new Spinner<>();
    Pane pane = new Pane();
    ObservableList<Rectangle> listRect = FXCollections.observableArrayList();
    ObservableList<Circle> listCirc = FXCollections.observableArrayList();
    Button buttonUloz = new Button("Ulož");
    Button buttonNacti = new Button("Načti");
    Label labelPointer = new Label("0:0");
    AbstrDoubleList<WritableImage> listKrokuZpet = new AbstrDoubleList<>();

    @Override
    public void start(Stage primaryStage) {
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("ikona.png")));
        canvas = new Canvas();
        gc = canvas.getGraphicsContext2D();
        pane.getChildren().add(canvas);
        comboboxNastroje.getItems().addAll(enumNastroj.values());
        comboboxNastroje.getSelectionModel().select(enumNastroj.TUZKA);
        SpinnerValueFactory valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 20, 2);
        spinnerSirkaCary.setValueFactory(valueFactory);
        initt();

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

        canvas.addEventHandler(MouseEvent.MOUSE_MOVED, (event) -> {
            labelPointer.setText((int) event.getX() + ":" + (int) event.getY());
        });

        hbox.getChildren()
                .addAll(colorPicker, comboboxNastroje, buttonClear, spinnerSirkaCary, buttonUloz, buttonNacti, labelPointer);
        root.setCenter(pane);

        root.setTop(hbox);

        canvas.widthProperty()
                .bind(root.widthProperty());
        canvas.heightProperty()
                .bind(root.heightProperty());

        scene = new Scene(root, 800, 600);

        scene.setOnKeyPressed((event) -> {
            if (event.isControlDown() && event.getCode() == KeyCode.Z) {
                if (!listKrokuZpet.jePrazdny()) {
                    try {
                        WritableImage wi;
                        if (jePosledni) {
                            wi = new WritableImage((int) canvas.getWidth(), (int) canvas.getHeight());
                            canvas.snapshot(null, wi);
                            listKrokuZpet.vlozPosledni(wi);
                            listKrokuZpet.zpristupniPredchudce();
                            jePosledni = false;
                        }
                        wi = listKrokuZpet.zpristupniAktualni();
                        gc.drawImage(wi, 0, 0, wi.getWidth(), wi.getHeight());
                        listKrokuZpet.zpristupniPredchudce();

                    } catch (Exception e) {
                        System.err.println(e.getMessage());
                    }
                }
            }

            if (event.isControlDown() && event.getCode() == KeyCode.Y) {
                if (!listKrokuZpet.jePrazdny()) {
                    try {
                        if (jePosledni) {
                            listKrokuZpet.odeberPosledni();
                            jePosledni = false;
                        }
                        listKrokuZpet.zpristupniNaslednika();
                        WritableImage wi = listKrokuZpet.zpristupniAktualni();
                        gc.drawImage(wi, 0, 0, wi.getWidth(), wi.getHeight());
                    } catch (Exception e) {
                        System.err.println(e.getMessage());
                    }
                }
            }
        });

        primaryStage.setTitle(
                "G-Edit 3000");
        primaryStage.setScene(scene);
        clearAll();
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
            gc = canvas.getGraphicsContext2D();
            primaryStage.setWidth(width + 10);
            primaryStage.setHeight(height + 60);

            canvas.widthProperty()
                    .bind(root.widthProperty());
            canvas.heightProperty()
                    .bind(root.heightProperty());
            clearAll();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    private void draw() {

        if (canvas != null) {

            canvas.addEventHandler(MouseEvent.MOUSE_PRESSED, (MouseEvent event) -> {

                if (event.getButton() == MouseButton.PRIMARY) {

                    pocet++;
                    if (pocet % 2 == 0) {
                        WritableImage wi = new WritableImage((int) canvas.getWidth(), (int) canvas.getHeight());
                        canvas.snapshot(null, wi);
                        listKrokuZpet.vlozPosledni(wi);
                    }

                    if (null != comboboxNastroje.getValue()) {
                        switch (comboboxNastroje.getValue()) {
                            case TUZKA:
                                gc.setStroke(currColor);
                                gc.setLineWidth(spinnerSirkaCary.getValue());
                                lineStart = new Point2D(event.getX(), event.getY());
                                break;
                            case GUMA:
                                gc.setStroke(Color.WHITE);
                                lineStart = new Point2D(event.getX(), event.getY());
                                break;
                            case OBDELNIK:
                            case KRUH:
                                gc.setFill(currColor);
                                rectStart = new Point2D(event.getX(), event.getY());
                                break;
                            case ORIZNOUT:
                                rectStart = new Point2D(event.getX(), event.getY());
                                break;
                            default:
                                break;
                        }
                    }
                } else {
                    WritableImage writableImage = new WritableImage((int) canvas.getWidth(), (int) canvas.getHeight());
                    SnapshotParameters params = new SnapshotParameters();
                    PixelReader pr = writableImage.getPixelReader();
                    params.setViewport(new Rectangle2D(0, 0, 1, 1));
                    canvas.snapshot(params, writableImage);
                    colorPicker.setValue(pr.getColor((int) event.getX(), (int) event.getY()));
                    currColor = colorPicker.getValue();
                }
            });

            canvas.addEventHandler(MouseEvent.MOUSE_DRAGGED, (MouseEvent event) -> {
                labelPointer.setText((int) event.getX() + ":" + (int) event.getY());
                if (event.getButton() == MouseButton.PRIMARY) {
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
                                pane.getChildren().removeAll(listRect);
                                listRect.clear();
                                Rectangle rect1 = new Rectangle(rectStart.getX(), rectStart.getY(), event.getX() - rectStart.getX(), event.getY() - rectStart.getY());
                                rect1.setFill(currColor);
                                listRect.add(rect1);
                                pane.getChildren().addAll(listRect);

                                break;
                            case KRUH:
//                            pane.getChildren().removeAll(listCirc);
//                            listCirc.clear();
//                            Circle circ = new Circle(rectStart.getX(), rectStart.getY(), (rectStart.distance(event.getX(), event.getY())));
//                            circ.setFill(currColor);
//                            listCirc.add(circ);
//                            pane.getChildren().addAll(listCirc);
                                break;

                            case ORIZNOUT:
                                pane.getChildren().removeAll(listRect);
                                listRect.clear();
                                Rectangle rect = new Rectangle(rectStart.getX(), rectStart.getY(), event.getX() - rectStart.getX(), event.getY() - rectStart.getY());
                                rect.setFill(null);
                                rect.setStroke(Color.DIMGREY);
                                listRect.add(rect);
                                pane.getChildren().addAll(listRect);

                                break;
                            default:
                                break;
                        }
                    }
                }

            });

            canvas.addEventHandler(MouseEvent.MOUSE_RELEASED, (event) -> {
                rectEnd = null;
                pane.getChildren().clear();
                pane.getChildren().add(canvas);
                if (event.getButton() == MouseButton.PRIMARY) {

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
                            case ORIZNOUT:
                                WritableImage writableImage = new WritableImage((int) (event.getX() - rectStart.getX()), (int) (event.getY() - rectStart.getY()));
                                SnapshotParameters params = new SnapshotParameters();
                                params.setViewport(new Rectangle2D(rectStart.getX(), rectStart.getY(), event.getX() - rectStart.getX(), event.getY() - rectStart.getY()));
                                canvas.snapshot(params, writableImage);
                                gc.setFill(Color.WHITE);
                                gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
                                gc.drawImage(writableImage, rectStart.getX(), rectStart.getY());

                                break;
                            default:
                                break;
                        }
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
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

    }

    private void initt() {
        buttonNacti.setMaxSize(100, 50);
        buttonUloz.setMaxSize(100, 50);
        buttonClear.setMaxSize(100, 50);
        spinnerSirkaCary.setMaxSize(75, 50);
        colorPicker.setMaxSize(125, 50);

        buttonClear.setMinSize(50, 0);

        hbox.setPadding(new Insets(5));
        hbox.setSpacing(5);

        pane.setMinWidth(575);
    }
}
