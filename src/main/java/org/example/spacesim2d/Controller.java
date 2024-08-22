package org.example.spacesim2d;

import javafx.scene.Scene;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;

public class Controller {
    private Scene scene;
    private Simulation simulation;
    private GraphicsContext gc;
    private double speed = 1;
    private int width;
    private int height;
    private double mouseX;
    private double mouseY;
    private SpaceObject preview;
    private double defaultRadius = 40;
    private double minRadius = 5;
    private double maxRadius = 100;
    private double defaultDensity = 5;
    private double minDensity = 0.01;
    private double previewVelocityScale = 1.5e-2;
    private boolean sizeChange = false;
    private Ship ship;
    private double densityChangeScale = 0.03;
    private double radiusChangeScale = 0.3;

    public Controller(Scene scene, Simulation simulation, GraphicsContext gc) {
        this.scene = scene;
        this.simulation = simulation;
        this.gc = gc;
        scene.setOnMousePressed(this::handleMousePress);
        scene.setOnMouseReleased(this::handleMouseRelease);
        scene.setOnScroll(this::handleScroll);
        width = (int) scene.getWidth();
        height = (int) scene.getHeight();
    }

    public void loop() {
        simulation.update(speed);
        clear();
        simulation.drawSimulationObjects(gc);
        if (preview != null) {
            preview.draw(gc);
            // draw arrow
            gc.setStroke(Color.YELLOW);
            gc.setLineWidth(3);
            gc.strokeLine(preview.getX(), preview.getY(), mouseX, mouseY);
        }
    }

    public void clear() {
        // clear canvas
        gc.clearRect(0, 0, width, height);
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, width, height);
    }

    public void initSim() {
        simulation.drawSimulationObjects(gc);
    }

    private void handleMousePress(MouseEvent clickEvent) {
        double x = clickEvent.getX();
        double y = clickEvent.getY();
        mouseX = x;
        mouseY = y;
        if (clickEvent.isPrimaryButtonDown()) {
            preview = new SpaceObject(defaultRadius, defaultDensity, x, y);
            scene.setOnMouseDragged(dragEvent -> {
                mouseX = dragEvent.getX();
                mouseY = dragEvent.getY();
            });
        } else if (clickEvent.isSecondaryButtonDown()) {
            SpaceObject temp = simulation.getObjectAt(x, y);
            if (temp != null) {
                simulation.remove(temp);
                if (temp == ship) {
                    ship = null;
                }
            } else if (ship == null) {
                ship = new Ship(5, 80, x, y, 0, 0, 0, scene);
                simulation.add(ship);
            }
        }
    }

    private void handleMouseRelease(MouseEvent clickEvent) {
        if (preview != null && simulation.getObjectAt(preview.getX(), preview.getY()) == null) {
            SpaceObject closest = simulation.getObjectClosestTo(preview.getX(), preview.getY());
            if (closest == null || preview.getDistanceToObject(closest) > preview.getRadius() + closest.getRadius()) {
                SpaceObject mouse = new SpaceObject(mouseX, mouseY);
                double[] normal = preview.getNormalVectorToObject(mouse);
                double distance = preview.getDistanceToObject(mouse);
                preview.setVx(normal[0] * distance * previewVelocityScale);
                preview.setVy(normal[1] * distance * previewVelocityScale);
                simulation.add(preview);
            }
        }
        scene.setOnMouseDragged(null);
        preview = null;
    }

    private void handleScroll(ScrollEvent scrollEvent) {
        sizeChange = true;
        mouseX = scrollEvent.getX();
        mouseY = scrollEvent.getY();
        double densityChange = scrollEvent.getDeltaX();
        double radiusChange = scrollEvent.getDeltaY();
        defaultDensity = Math.max(defaultDensity + densityChange * densityChangeScale, minDensity);
        defaultRadius = Math.min(Math.max(defaultRadius + radiusChange * radiusChangeScale, minRadius), maxRadius);
        preview = new SpaceObject(defaultRadius, defaultDensity, mouseX, mouseY);
        scene.setOnMouseMoved(e -> {
            sizeChange = false;
            preview = null;
            if (ship != null) {
                scene.setOnMouseMoved(ship::updateMouse);
            } else {
                scene.setOnMouseMoved(null);
            }
        });
    }
}
