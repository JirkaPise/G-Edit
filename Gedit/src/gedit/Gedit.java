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
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javax.imageio.ImageIO;

public class Gedit extends Application {

    Label label = new Label();
    Canvas canvas = null;
    private Color currColor = Color.RED;
    Button save = new Button("Save");

    @Override
    public void start(Stage primaryStage) {
        ColorPicker colorPicker = new ColorPicker(currColor);
        canvas = new Canvas();

        colorPicker.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                currColor = colorPicker.getValue();
            }
        });

        canvas.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                draw();
            }
        });

        canvas.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                draw();
            }
        });

        save.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                FileChooser fileChooser = new FileChooser();

                FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("png files (*.png)", "*.png");
                fileChooser.getExtensionFilters().add(extFilter);

                File file = fileChooser.showSaveDialog(primaryStage);

                if (file != null) {
                    try {
                        WritableImage writableImage = new WritableImage((int) canvas.getWidth(),(int) canvas.getHeight());
                        canvas.snapshot(null, writableImage);

                        RenderedImage renderedImage = SwingFXUtils.fromFXImage(writableImage, null);
                        ImageIO.write(renderedImage, "png", file);
                    } catch (IOException ex) {
                        System.out.println("FILE OUTPUT ERROR");
                    }
                }

            }
        });

        AnchorPane root = new AnchorPane();

        root.getChildren().addAll(canvas, label, colorPicker, save);
        colorPicker.setLayoutX(10);
        colorPicker.setLayoutY(10);
        save.setLayoutX(400);
        save.setLayoutY(10);

        label.setText("0 : 0");
        label.setLayoutX(150);
        label.setLayoutY(15);
        label.setStyle("-fx-border-style: solid;");
        label.setStyle("-fx-background-color: E8A698;");

        canvas.widthProperty().bind(root.widthProperty());
        canvas.heightProperty().bind(root.heightProperty());

        Scene scene = new Scene(root, 600, 400);

        primaryStage.setTitle("Kreslení myší");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    private void draw() {
        if (canvas != null) {
            GraphicsContext gc = canvas.getGraphicsContext2D();
            canvas.addEventHandler(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    gc.setStroke(currColor);
                    gc.beginPath();
                    gc.moveTo(event.getX(), event.getY());
                    gc.getStroke();
                }
            });

            canvas.addEventHandler(MouseEvent.MOUSE_DRAGGED, new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    gc.setStroke(currColor);
                    drawText((int) event.getX() + " : " + (int) event.getY(), gc);
                    gc.lineTo(event.getX(), event.getY());
                    gc.stroke();
                }
            });

            canvas.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    if (event.getButton() == MouseButton.SECONDARY) {
                        clearAll(gc);
                    }
                }
            });
        }
    }

    private void clearAll(GraphicsContext gc) {
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    public void drawText(String souradnice, GraphicsContext gc) {
        label.setText(souradnice);
    }
}
