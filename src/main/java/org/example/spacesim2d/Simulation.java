package org.example.spacesim2d;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

public class Simulation {
    private List<SpaceObject> objects;
    private double G = 2.0e-3; // 6.67430e-11;
    private int step;
    private int width;
    private int height;

    public Simulation(List<SpaceObject> objects, int step, int width, int height) {
        this.objects = objects;
        this.step = step;
        this.width = width;
        this.height = height;
    }

    public Simulation(List<SpaceObject> objects, int width, int height) {
        this(objects, 0, width, height);
    }

    public Simulation(int width, int height) {
        this(new ArrayList<>(), 0, width, height);
    }

    public void add(SpaceObject object) {
        objects.add(object);
    }

    public void remove(SpaceObject object) {
        objects.remove(object);
    }

    public void update(GraphicsContext gc, double speed) {
        move(speed);
        checkCollisions();
        checkCollisionsWithWalls();
        draw(gc);
    }

    public void move(double speed) {
        for (SpaceObject object : objects) {
            object.move(speed);
            object.accelerate();
        }
    }

    public void collide(SpaceObject obj1, SpaceObject obj2) {
        // get object masses
        double m1 = obj1.getMass();
        double m2 = obj2.getMass();

        // get object velocities
        double[] v1 = {obj1.getVx(), obj1.getVy()};
        double[] v2 = {obj2.getVx(), obj2.getVy()};

        // get vector from 1st to 2nd object
        double[] vec = obj1.getVectorToObject(obj2);
        double dx = vec[0];
        double dy = vec[1];

        double distance = obj1.getDistanceToObject(obj2);

        double[] normal = obj1.getNormalVectorToObject(obj2);
        double[] tangent = SpaceObject.getTangent(normal);

        // get object velocities expressed as normal and tangent
        double[] vn1 = SpaceObject.convertVecToNormal(v1, normal);
        double[] vn2 = SpaceObject.convertVecToNormal(v2, normal);
        double vn1x = vn1[0];
        double vn2x = vn2[0];

        // calculate new normal velocities (only normal part changes for elastic collision)
        vn1[0] = (vn1x * (m1-m2) + 2 * vn2x * m2) / (m1 + m2);
        vn2[0] = (vn2x * (m2-m1) + 2 * vn1x * m1) / (m1 + m2);

        // convert normal velocities to original coordinate system
        double[] final_v1 = SpaceObject.convertNormalToOriginal(vn1, normal);
        double[] final_v2 = SpaceObject.convertNormalToOriginal(vn2, normal);

        // set new velocities
        obj1.setVx(final_v1[0]);
        obj1.setVy(final_v1[1]);
        obj2.setVx(final_v2[0]);
        obj2.setVy(final_v2[1]);
    }

    public void checkCollisions() {
        for (int i = 0; i < objects.size() - 1; i++) {
            SpaceObject obj1 = objects.get(i);
            for (int j = i + 1; j < objects.size(); j++) {
                SpaceObject obj2 = objects.get(j);

                // apply gravity
                obj1.calculateGravity(obj2, G);

                // check if close by x
                if (Math.abs(obj1.getX() - obj2.getX()) < obj1.getRadius() + obj2.getRadius()) {
                    // check if close by y
                    if (Math.abs(obj1.getY() - obj2.getY()) < obj1.getRadius() + obj2.getRadius()) {
                        // check if touching using a^2 + b^2 = c^2
                        double dx = Math.abs(obj1.getX() - obj2.getX());
                        double dy = Math.abs(obj1.getY() - obj2.getY());
                        float distance = (float) Math.sqrt(dx * dx + dy * dy);
                        if (distance < obj1.getRadius() + obj2.getRadius()) {
                            collide(obj1, obj2);
                        }
                    }
                }
            }
        }
    }

    public void checkCollisionsWithWalls() {
        for (SpaceObject object : objects) {
            if (object.getX() < object.getRadius() || object.getX() > width - object.getRadius()) {
                object.setVx(-object.getVx());
                // move object back to avoid sticking to the wall
                object.setX(object.getX() + object.getVx());
            }
            if (object.getY() < object.getRadius() || object.getY() > height - object.getRadius()) {
                object.setVy(-object.getVy());
                // move object back to avoid sticking to the wall
                object.setY(object.getY() + object.getVy());
            }
        }
    }

    public void draw(GraphicsContext gc) {
        // clear canvas
        gc.clearRect(0, 0, width, height);
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, width, height);
        objects.forEach(object -> object.draw(gc));
    }
}
