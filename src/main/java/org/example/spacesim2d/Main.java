package org.example.spacesim2d;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;

public class Main extends Application {
    private Simulation simulation;
    private GraphicsContext gc;
    private int width = 1400;
    private int height = 800;
    private double speed = 1;
    private double playerInGame = 0;

    @Override
    public void start(Stage stage) throws IOException {
        Pane root = new Pane();
        Canvas canvas = new Canvas(width, height);
        gc = canvas.getGraphicsContext2D();
        root.getChildren().add(canvas);
        Scene scene = new Scene(root);
        stage.setTitle("Space Sim 2D");
        stage.setScene(scene);
        stage.show();

        simulation = new Simulation(width, height);

        Controller controller = new Controller(scene, simulation, gc);
        controller.initSim();


        Timeline timeline = new Timeline(
                new KeyFrame(
                        Duration.millis(16), e -> controller.loop()
                )
        );
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    public static void main(String[] args) {
        launch();
    }
}