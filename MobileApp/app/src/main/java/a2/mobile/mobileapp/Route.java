package a2.mobile.mobileapp;

public class Route {
    public String title;
    public Point startPoint;
    public Point endPoint;

    Route(String title, Point startPoint, Point endPoint) {
        this.title = title;
        this.startPoint = startPoint;
        this.endPoint = endPoint;
    }
}
