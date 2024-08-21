package org.example.spacesim2d;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Ship extends SpaceObject {
    private Color color = Color.BLUE;
    private double fuel;
    private double fuelConsumption;  // per tick
    private double orientation;  // in radians

    public Ship(double radius, double mass, double x, double y, double vx, double vy, double fuel, double fuelConsumption, double orientation) {
        super("player", radius, mass, x, y, vx, vy);
        this.fuel = fuel;
        this.fuelConsumption = fuelConsumption;
        this.orientation = orientation;
    }

    @Override
    public void draw(GraphicsContext gc) {
        gc.setFill(color);
        double x = getX();
        double y = getY();
        // draw ship as a triangle
        double[] xPoints = {x, x - 10, x + 10};
        double[] yPoints = {y, y + 20, y + 20};
        gc.fillPolygon(xPoints, yPoints, 3);
    }
}
