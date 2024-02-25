package io.github.glandais.solitaire.klondike.printer.gui;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
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
        Group group = new Group();
        Scene scene = new Scene(group, Constants.WIDTH, Constants.HEIGHT);
        scene.setOnMouseClicked(e -> klondikeGuiPrinter.clicked(e.getSceneX(), e.getSceneY(), e.getClickCount()));
        scene.setOnMousePressed(e -> klondikeGuiPrinter.pressed(e.getSceneX(), e.getSceneY()));
        scene.setOnMouseDragged(e -> klondikeGuiPrinter.dragged(e.getSceneX(), e.getSceneY()));
        scene.setOnMouseReleased(e -> klondikeGuiPrinter.released(e.getSceneX(), e.getSceneY()));

        // The canvas to be rendered upon
        Canvas canvas = new Canvas(Constants.WIDTH, Constants.HEIGHT);
        stage.setTitle("klondike");
        stage.setScene(scene);
        stage.setResizable(false);
        GraphicsContext graphicsContext = canvas.getGraphicsContext2D();

        // The main game loop
        Timeline gameLoop = new Timeline();
        gameLoop.setCycleCount(Timeline.INDEFINITE);

        PerspectiveCamera camera = new PerspectiveCamera(false);
        group.getChildren().add(canvas);
        group.getChildren().add(camera);

        KeyFrame keyFrame = new KeyFrame(
                Duration.seconds(1.0f / 60.0f), // 60 FPS
                ae -> {
                    graphicsContext.setFill(Color.DARKGREEN);
                    graphicsContext.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
                    scene.setCamera(camera);

                    klondikeGuiPrinter.update(1.0f / 60.0f);
                    klondikeGuiPrinter.render(graphicsContext);
                });

        gameLoop.getKeyFrames().add(keyFrame);
        gameLoop.play();

        // shows the screen
        stage.show();
    }
}
