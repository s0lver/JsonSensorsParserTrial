package tamps.cinvestav.s0lver.parserEntities;

public class LocationForJson extends AbstractParserRecord {
    private double latitude;
    private double longitude;
    private double altitude;
    private double accuracy;
    private double speed;

    public LocationForJson(double latitude, double longitude, double altitude, double accuracy, double speed, long timestamp) {
        super(timestamp, Sensors.GPS);
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
        this.accuracy = accuracy;
        this.speed = speed;
    }

    public LocationForJson(Location location) {
        super(location.getTime(), Sensors.GPS);
        this.latitude = location.getLatitude();
        this.longitude = location.getLongitude();
        this.altitude = location.getAltitude();
        this.accuracy = location.getAccuracy();
        this.speed = location.getSpeed();
    }

    public String toString() {
        return String.format("lat:%s, long:%s, alt:%s, acc:%s, speed:%s, time:%s", latitude, longitude, altitude, accuracy, speed, timestamp);
    }
}
