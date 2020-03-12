package a2.mobile.mobileapp;

import java.util.List;
import java.util.UUID;

public class Point {
    private UUID id;
    public List<Double> coordinates;
    public String interest;

    Point() {
        this.id = UUID.randomUUID();
    }

    public Point(List<Double> coordinates) {
        this.id = UUID.randomUUID();

        this.coordinates = coordinates;
    }

    public Point(List<Double> coordinates, String interest) {
        this.id = UUID.randomUUID();

        this.coordinates = coordinates;
        this.interest = interest;
    }

    public UUID getId() {
        return this.id;
    }
}
