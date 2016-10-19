package tamps.cinvestav.s0lver.deserializers;

import tamps.cinvestav.s0lver.parserEntities.SensingUnit;
import tamps.cinvestav.s0lver.parserEntities.SensorDataBlock;
import tamps.cinvestav.s0lver.sensorEntities.AccelerometerSample;
import tamps.cinvestav.s0lver.sensorEntities.Location;
import tamps.cinvestav.s0lver.sensorEntities.Sensors;

import javax.json.Json;
import javax.json.stream.JsonParser;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class JsonDeserializer {
    private final JsonParser jsonParser;
    private File fileInput;
    private boolean fileDone;

    public JsonDeserializer(String filePath) throws FileNotFoundException {
        this.fileInput = new File(filePath);
        this.fileDone = false;
        this.jsonParser = Json.createParser(new FileInputStream(fileInput));
    }

    public List<SensingUnit> deserializeWholeFile() {
        if (fileDone) {
            return null;
        }

        List<SensingUnit> sensingUnitList = new ArrayList<>();
        while (true) {
            JsonParser.Event event = jsonParser.next();
            if (event == JsonParser.Event.START_ARRAY) {
                // Start of file, the root [
                continue;
            }
            else if (event == JsonParser.Event.END_ARRAY) {
                // EOF found
                break;
            } else {
                SensingUnit sensingUnit = readSensingUnit(jsonParser);
                sensingUnitList.add(sensingUnit);
            }
        }

        fileDone = true;
        return sensingUnitList;
    }

    public SensingUnit readSensingUnit() {
        if (fileDone) {
            return null;
        }

        JsonParser.Event event = jsonParser.next();
        // Start of file, the root [
        if (event == JsonParser.Event.START_ARRAY) {
            // Then read another one
            jsonParser.next();
        }

        if (event == JsonParser.Event.END_ARRAY) {
            // EOF found
            fileDone = true;
            return null;
        }

        SensingUnit sensingUnit = readSensingUnit(jsonParser);
        return sensingUnit;
    }

    private SensingUnit readSensingUnit(JsonParser jsonParser) {
        List<SensorDataBlock> sensorDataBlockList = new ArrayList<>();
        SensorDataBlock sensorDataBlock = null;

        // Reading all sensor data blocks
        while (true) {
            // Reading the type
            JsonParser.Event event = jsonParser.next();

            // Checking end of sensing unit
            if (event == JsonParser.Event.END_OBJECT) {
                break;
            }

            byte type = Byte.valueOf(jsonParser.getString());

            switch (type) {
                case Sensors.GPS:
                    sensorDataBlock = deserializeLocationBlock(jsonParser);
                    sensorDataBlockList.add(sensorDataBlock);
                    break;
                case Sensors.ACCELEROMETER:
                    sensorDataBlock = deserializeAccelerometerBlock(jsonParser);
                    sensorDataBlockList.add(sensorDataBlock);
                    break;
            }
        }

        return new SensingUnit(sensorDataBlockList);
    }

    private SensorDataBlock deserializeLocationBlock(JsonParser jsonParser) {
        // Reading the beginning of object
        jsonParser.next();
        jsonParser.next();
        jsonParser.next();
        long timestamp = jsonParser.getLong();

        // Reading the beginning of "v"
        jsonParser.next();

        jsonParser.next();

        jsonParser.next();
        jsonParser.next();
        double latitude = jsonParser.getBigDecimal().doubleValue();

        jsonParser.next();
        jsonParser.next();
        double longitude = jsonParser.getBigDecimal().doubleValue();

        jsonParser.next();
        jsonParser.next();
        double altitude = jsonParser.getBigDecimal().doubleValue();

        jsonParser.next();
        jsonParser.next();
        double accuracy = jsonParser.getBigDecimal().doubleValue();

        jsonParser.next();
        jsonParser.next();
        double speed = jsonParser.getBigDecimal().doubleValue();

        Location tmpLocation = new Location("FILE");
        tmpLocation.setLatitude(latitude);
        tmpLocation.setLongitude(longitude);
        tmpLocation.setAltitude(altitude);
        tmpLocation.setAccuracy((float) accuracy);
        tmpLocation.setSpeed((float) speed);
        tmpLocation.setTime(timestamp);

        HashMap<String, Object> values = new HashMap<>();
        values.put("values", tmpLocation);

        SensorDataBlock sensorDataBlock = new SensorDataBlock(Sensors.GPS, timestamp, values);

        // Read closing } }
        jsonParser.next();
        jsonParser.next();

        return sensorDataBlock;
    }

    private SensorDataBlock deserializeAccelerometerBlock(JsonParser jsonParser) {
        // Reading the beginning of object
        jsonParser.next();
        jsonParser.next();
        jsonParser.next();
        long timestamp = jsonParser.getLong();

        // Reading the beginning of "v"
        jsonParser.next();

        // Reading the beginning of array
        jsonParser.next();

        List<AccelerometerSample> accelerometerSamples = new ArrayList<>();

        while (true) {
            // Reading token
            JsonParser.Event next = jsonParser.next();

            // Is it the end of array? then exit!
            if (next == JsonParser.Event.END_ARRAY) {
                break;
            }else{
                // Then it is the beginning of an accelerometer object
                // Reading key "x"
                jsonParser.next();
                // Reading value x
                jsonParser.next();
                float x = jsonParser.getBigDecimal().floatValue();

                // Reading key "y"
                jsonParser.next();
                // Reading value y
                jsonParser.next();
                float y = jsonParser.getBigDecimal().floatValue();

                // Reading key "z"
                jsonParser.next();
                // Reading value z
                jsonParser.next();
                float z = jsonParser.getBigDecimal().floatValue();

                accelerometerSamples.add(new AccelerometerSample(x, y, z, timestamp));

                // End of current object
                jsonParser.next();
            }
        }

        // We are currently at ], we need to read closing }
        jsonParser.next();

        HashMap<String, Object> values = new HashMap<>();
        values.put("values", accelerometerSamples);

        SensorDataBlock sensorDataBlock = new SensorDataBlock(Sensors.ACCELEROMETER, timestamp, values);
        return sensorDataBlock;
    }

    private void printEventType(JsonParser.Event next) {
        switch (next) {
            case END_ARRAY:
                System.out.println("end array");
                break;
            case END_OBJECT:
                System.out.println("End object");
                break;
            case KEY_NAME:
                System.out.println("Key name");
                break;
            case START_ARRAY:
                System.out.println("Start array");
                break;
            case START_OBJECT:
                System.out.println("Start object");
                break;
            case VALUE_FALSE:
                System.out.println("Value false");
                break;
            case VALUE_NULL:
                System.out.println("Value null");
                break;
            case VALUE_NUMBER:
                System.out.println("Value number");
                break;
            case VALUE_STRING:
                System.out.println("Value string");
                break;
            case VALUE_TRUE:
                System.out.println("Value true");
                break;
        }
    }
}
