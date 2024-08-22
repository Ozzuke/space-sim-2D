package org.example.spacesim2d;

import javafx.scene.Scene;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

public class Ship extends SpaceObject {
    private Color color = Color.RED;
    private Color flame = Color.YELLOW;
    private double flameScale = 1;
    private double orientation;  // in radians
    private double mouseX = 0;
    private double mouseY = 0;
    private double mouseForceScale = 2.0e-2;
    private int updated = 0;
    private int updateRate = 10;
    private double mouseEdgeBuffer = 20;
    private double acceleration = 0;
    private double moveNear = 0;
    private double scale = 1;
    private double moveFar;
    private double width;
    private double height;

    public Ship(double radius, double density, double x, double y, double vx, double vy, double orientation, Scene scene) {
        super("player", radius, density, x, y, vx, vy);
        this.orientation = orientation;
        scene.setOnMouseMoved(this::updateMouse);
        this.width = scene.getWidth();
        this.height = scene.getHeight();
        this.moveFar = Math.min(width, height) / 2;
    }

    void updateMouse(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
        updated = updateRate;
    }

    private double[] getMouseForce() {
        SpaceObject mouse = new SpaceObject(mouseX, mouseY);
        double[] normal = getNormalVectorToObject(mouse);
        double distance = Math.min(getDistanceToObject(mouse), moveFar);
        distance = Math.max(distance - moveNear, 0);
        double force = Math.max(Math.log(distance * scale), 0);

        // update orientation
        orientation = Math.atan2(normal[1], normal[0]) + Math.PI / 2;

        return new double[]{normal[0] * force, normal[1] * force};
    }

    @Override
    public void accelerate() {
        if (updated > 0 || mouseX > mouseEdgeBuffer && mouseY > mouseEdgeBuffer && mouseX < width - mouseEdgeBuffer && mouseY < height - mouseEdgeBuffer) {
            double[] mouseForce = getMouseForce();
            double mouseFx = mouseForce[0];
            double mouseFy = mouseForce[1];
            acceleration = Math.sqrt(mouseFx * mouseFx + mouseFy * mouseFy);
            addForce(mouseFx * getMass() * mouseForceScale, mouseFy * getMass() * mouseForceScale);
        } else {
            orientation = Math.atan2(getVy(), getVx()) + Math.PI / 2;
            acceleration = 0;
        }
        updated--;
        super.accelerate();
    }

    public static void rotatePoints(double[] xPoints, double[] yPoints, double[] origin, double angle) {
        if (origin.length == 2) {
            for (int i = 0; i < xPoints.length; i++) {
                double xo = origin[0];
                double yo = origin[1];
                double x = xPoints[i] - xo;
                double y = yPoints[i] - yo;
                xPoints[i] = x * Math.cos(angle) - y * Math.sin(angle) + xo;
                yPoints[i] = y * Math.cos(angle) + x * Math.sin(angle) + yo;
            }
        }
    }

    @Override
    public void draw(GraphicsContext gc) {
        double x = getX();
        double y = getY();
        double r = getRadius();

        // draw ship as a triangle
        gc.setFill(color);
        double[] xPoints = {x + 0, x + 1.5 * r, x - 1.5 * r};
        double[] yPoints = {y - 2.5 * r, y + r, y + r};
        rotatePoints(xPoints, yPoints, new double[]{x, y}, orientation);
        gc.fillPolygon(xPoints, yPoints, 3);
        // draw flame
        gc.setFill(flame);
        xPoints = new double[]{x - 0.5 * r, x + 0.5 * r, x + 0};
        yPoints = new double[]{y + r, y + r, y + r + Math.random() * (r + r * acceleration * flameScale)};
        rotatePoints(xPoints, yPoints, new double[]{x, y}, orientation);
        gc.fillPolygon(xPoints, yPoints, 3);
    }
}
