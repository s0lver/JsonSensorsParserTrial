package tamps.cinvestav.s0lver.parserEntities;

import java.util.Date;

/***
 * Represents a record of an accelerometer sample
 */
public class AccelerometerSample extends AbstractParserRecord{
    private float x, y, z;

    public AccelerometerSample(float x, float y, float z, long timestamp) {
        super(timestamp, Sensors.ACCELEROMETER);
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getZ() {
        return z;
    }

    @Override
    public String toString() {
        return x + "," + y + "," + z + "," + timestamp;
    }
}
