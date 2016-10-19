package tamps.cinvestav.s0lver.sensorEntities;

public class LocationForJson {
    private double latitude;
    private double longitude;
    private double altitude;
    private double accuracy;
    private double speed;
    private long timestamp;
    private byte type;

    public LocationForJson(double latitude, double longitude, double altitude, double accuracy, double speed, long timestamp) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
        this.accuracy = accuracy;
        this.speed = speed;
        this.timestamp = timestamp;
    }

    public LocationForJson(Location location) {
        this.latitude = location.getLatitude();
        this.longitude = location.getLongitude();
        this.altitude = location.getAltitude();
        this.accuracy = location.getAccuracy();
        this.speed = location.getSpeed();
        this.timestamp = location.getTime();
    }

    public String toString() {
        return String.format("lat:%s, long:%s, alt:%s, acc:%s, speed:%s, time:%s", latitude, longitude, altitude, accuracy, speed, timestamp);
    }
}
