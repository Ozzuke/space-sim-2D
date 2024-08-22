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
        initSim(scene);

        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(16), e -> simulation.update(gc, speed)));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void initSim(Scene scene) {
        simulation.add(new SpaceObject(80, 3, 100, 270, 0, 0));
        simulation.add(new SpaceObject(30, 20, 600, 330, 0, 0));
        simulation.add(new SpaceObject(50, 5, 400, 400, 0, 0));
        simulation.add(new Ship(5, 100, 400, 300, 0, 0,  0, scene));
        simulation.draw(gc);
    }

    public static void main(String[] args) {
        launch();
    }
}