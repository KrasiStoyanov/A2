package a2.mobile.mobileapp.data.classes;

import java.util.List;
import java.util.UUID;

public class Route {
    public UUID id;

    public String title;
    public Point startPoint;
    public Point endPoint;
    public List<PointOfInterest> pointsOfInterest;

    public Route() {
        this.id = UUID.randomUUID();
    }
}
