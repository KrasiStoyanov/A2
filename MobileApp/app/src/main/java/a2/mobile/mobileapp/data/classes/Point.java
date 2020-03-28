package a2.mobile.mobileapp.data.classes;

import java.util.List;
import java.util.UUID;

public class Point {
    private UUID id;
    private List<Integer> locationZones;

    public List<Double> coordinates;
    public String title;
    public String interest;

    Point() {
        this.id = UUID.randomUUID();
    }

    public Point(List<Double> coordinates) {
        this.id = UUID.randomUUID();

        this.coordinates = coordinates;
    }

    public Point(List<Double> coordinates, String title) {
        this.id = UUID.randomUUID();

        this.coordinates = coordinates;
        this.title = title;
    }

    public Point(List<Double> coordinates, String title, String interest) {
        this.id = UUID.randomUUID();

        this.coordinates = coordinates;
        this.title = title;
        this.interest = interest;
    }

    public Point(List<Double> coordinates, List<Integer> locationZones, String title, String interest) {
        this.id = UUID.randomUUID();

        this.coordinates = coordinates;
        this.locationZones = locationZones;
        this.title = title;
        this.interest = interest;
    }

    public UUID getId() {
        return this.id;
    }

    public List<Integer> getLocationZones() {
        return this.locationZones;
    }
}
