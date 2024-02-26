package io.github.glandais.solitaire.klondike.printer.gui;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.concurrent.CountDownLatch;

public class KlondikeGuiApplication extends Application {

    public static KlondikeGuiPrinter klondikeGuiPrinter;

    public static final CountDownLatch exitCdl = new CountDownLatch(1);

    public static void start() {
        new Thread(() -> {
            Application.launch();
            exitCdl.countDown();
            System.exit(0);
        }).start();
    }

    @Override
    public void start(Stage stage) {

        // Sets everything needed for the objects to render
//        BorderPane root = new BorderPane();
//        Button undo = new Button("Undo");
//        undo.setOnAction(e -> klondikeGuiPrinter.undo());
//        root.setBottom(new ToolBar(undo));
        Group root = new Group();
        Scene scene = new Scene(root, Constants.WIDTH, Constants.HEIGHT);
        scene.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.BACK) {
                klondikeGuiPrinter.undo();
            } else if (e.getButton() == MouseButton.FORWARD) {
                klondikeGuiPrinter.redo();
            } else if (e.getButton() == MouseButton.PRIMARY) {
                klondikeGuiPrinter.mouseClicked(e.getX(), e.getY(), e.getClickCount());
            }
        });
        scene.setOnMousePressed(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                klondikeGuiPrinter.mousePressed(e.getX(), e.getY());
            }
        });
        scene.setOnMouseDragged(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                klondikeGuiPrinter.mouseDragged(e.getX(), e.getY());
            }
        });
        scene.setOnMouseReleased(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                klondikeGuiPrinter.mouseReleased(e.getX(), e.getY());
            }
        });
        scene.setOnKeyReleased(e -> klondikeGuiPrinter.keyReleased(e.getCode()));

        // The canvas to be rendered upon
        Canvas canvas = new Canvas(Constants.WIDTH, Constants.HEIGHT);
        stage.setTitle("klondike");
        stage.setScene(scene);
        stage.setResizable(true);
        GraphicsContext graphicsContext = canvas.getGraphicsContext2D();

        // The main game loop
        Timeline gameLoop = new Timeline();
        gameLoop.setCycleCount(Timeline.INDEFINITE);

        root.getChildren().add(canvas);

        KeyFrame keyFrame = new KeyFrame(
                Duration.seconds(1.0f / 60.0f), // 60 FPS
                ae -> {
                    graphicsContext.setFill(Color.DARKGREEN);
                    graphicsContext.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

                    klondikeGuiPrinter.update(1.0f / 60.0f);
                    klondikeGuiPrinter.render(graphicsContext);
                });

        gameLoop.getKeyFrames().add(keyFrame);
        gameLoop.play();

        // shows the screen
        stage.show();
    }
}
