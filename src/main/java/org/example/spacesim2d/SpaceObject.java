package org.example.spacesim2d;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class SpaceObject {
    private String name;
    private double radius;
    private double mass;
    private double x;
    private double y;
    private double vx;
    private double vy;
    private double fx = 0;
    private double fy = 0;

    public SpaceObject(String name, double radius, double density, double x, double y, double vx, double vy) {
        this.name = name;
        this.radius = radius;
        this.mass = getMassFromDensity(density);
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
    }

    public SpaceObject(double radius, double density, double x, double y, double vx, double vy) {
        this("Unnamed", radius, density, x, y, vx, vy);
    }

    public SpaceObject(String name, double radius, double mass, double x, double y) {
        this(name, radius, mass, x, y, 0, 0);
    }

    public SpaceObject(double x, double y) {
        this("Unnamed", 0, 0, x, y, 0, 0);
    }

    public SpaceObject(String name, double x, double y) {
        this(name, 0, 0, x, y, 0, 0);
    }

    public SpaceObject(double radius, double mass, double x, double y) {
        this("Unnamed", radius, mass, x, y, 0, 0);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public double getMass() {
        return mass;
    }

    public void setMass(double mass) {
        this.mass = mass;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getVx() {
        return vx;
    }

    public void setVx(double vx) {
        this.vx = vx;
    }

    public double getVy() {
        return vy;
    }

    public void setVy(double vy) {
        this.vy = vy;
    }

    public double getFx() {
        return fx;
    }

    public void setFx(double fx) {
        this.fx = fx;
    }

    public double getFy() {
        return fy;
    }

    public void setFy(double fy) {
        this.fy = fy;
    }

    public double getArea() {
        return Math.PI * Math.pow(radius, 2);
    }

    public double getDensity() {
        return mass / getArea();
    }

    public double getMassFromDensity(double density) {
        return density * getArea();
    }

    public double getVelocity() {
        return Math.sqrt(vx * vx + vy * vy);
    }


    public double[] getVectorToObject(SpaceObject other) {
        // returns the vector from this object to the other object
        // in the form of [dx, dy]
        return new double[]{
                other.getX() - this.getX(),
                other.getY() - this.getY()
        };
    }

    public double getDistanceToObject(SpaceObject other) {
        // returns the distance between the centers of this object and the other object
        double[] vec = getVectorToObject(other);
        double dx = vec[0];
        double dy = vec[1];
        return Math.sqrt(dx * dx + dy * dy);
    }

    public double[] getNormalVectorToObject(SpaceObject other) {
        // returns the normal vector from this object to the other object
        // in the form of [nx, ny]
        double distance = getDistanceToObject(other);
        double[] vec = getVectorToObject(other);
        return new double[]{
                vec[0] / distance,
                vec[1] / distance
        };
    }

    public static double[] getTangent(double[] normal) {
        // returns the tangent vector to the normal vector
        // in the form of [tx, ty]
        return new double[]{
                -normal[1],
                normal[0]
        };
    }

    public static double[] convertVecToNormal(double[] vector, double[] normal) {
        double[] tangent = getTangent(normal);
        double x = vector[0];
        double y = vector[1];
        double nx = x * normal[0] + y * normal[1];
        double ny = x * tangent[0] + y * tangent[1];
        return new double[]{nx, ny};
    }

    public static double[] convertNormalToOriginal(double[] vector, double[] normal) {
        double[] tangent = getTangent(normal);
        double nx = vector[0];
        double ny = vector[1];
        double x = nx * normal[0] + ny * tangent[0];
        double y = nx * normal[1] + ny * tangent[1];
        return new double[]{x, y};
    }

    public void addForce(double fx, double fy) {
        this.fx += fx;
        this.fy += fy;
    }

    public void calculateGravity(SpaceObject other, double G) {
        double m1 = mass;
        double m2 = other.getMass();
        double[] vec = getVectorToObject(other);
        double dx = vec[0];
        double dy = vec[1];
        double distance = getDistanceToObject(other);
        double force = (G * m1 * m2) / (distance * distance);
        double fx = force * dx / distance;
        double fy = force * dy / distance;
        addForce(fx, fy);
        other.addForce(-fx, -fy);
    }

    public void move(double speed) {
        x += vx * speed;
        y += vy * speed;
    }

    public void accelerate() {
        vx += fx / mass;
        vy += fy / mass;
        fx = 0;
        fy = 0;
    }

    public void draw(GraphicsContext gc) {
        // draw the object on the canvas
        // the color is based on the density of the object
        // density of around 0 - 10 goes from dark green to fully green
        // density of around 10 - 100 goes from fully green to white
        gc.setFill(Color.rgb((int) Math.min(Math.max(60 * Math.log(getDensity() - 9), 0), 255), (int) Math.min(55 + 20 * getDensity(), 255), (int) Math.min(Math.max(60 * Math.log(getDensity() - 7), 0), 255)));
        gc.fillOval(x - radius, y - radius, 2 * radius, 2 * radius);
    }
}
