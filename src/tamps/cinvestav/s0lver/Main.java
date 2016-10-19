package tamps.cinvestav.s0lver;

import tamps.cinvestav.s0lver.deserializers.JsonDeserializer;
import tamps.cinvestav.s0lver.parserEntities.SensingUnit;
import tamps.cinvestav.s0lver.parserEntities.SensorDataBlock;
import tamps.cinvestav.s0lver.sensorEntities.AccelerometerSample;
import tamps.cinvestav.s0lver.sensorEntities.Location;
import tamps.cinvestav.s0lver.sensorEntities.Sensors;
import tamps.cinvestav.s0lver.readers.LocationFileReader;

import javax.json.Json;
import javax.json.stream.JsonGenerator;
import javax.json.stream.JsonGeneratorFactory;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {
    public static final String CSV_RAW_INPUT_FILE = "locations.csv";
    public static final String OUTPUT_LOCATIONS_FILE = "locations.json";
    public static final String OUTPUT_MIXED_SENSORS_FILE = "mixed-sensors.json";
    public static final String OUTPUT_NEW_MIXED_SENSORS_FILE = "mixed-sensors-new.json";
    public static final String LATITUDE = "latitude";
    public static final String LONGITUDE = "longitude";
    public static final String ALTITUDE = "altitude";
    public static final String ACCURACY = "accuracy";
    public static final String SPEED = "speed";

    private static String USER_HOME_DIR = System.getProperty("user.home");
    private static String DESKTOP_PATH = USER_HOME_DIR + File.separator + "Desktop" + File.separator;

    public static void main(String[] args) throws IOException {


        String fullCsvRawInputPath = DESKTOP_PATH + "tmp" + File.separator + CSV_RAW_INPUT_FILE;
        String fullLocationsOutputPath = DESKTOP_PATH + "tmp" + File.separator + OUTPUT_LOCATIONS_FILE;

        Main app = new Main();

        String fullNewMixedOutputPath = DESKTOP_PATH + "tmp" + File.separator + OUTPUT_NEW_MIXED_SENSORS_FILE;
        // app.serializeWithJavaX(fullCsvRawInputPath, fullNewMixedOutputPath);

        JsonDeserializer jsonDeserializer = new JsonDeserializer(fullNewMixedOutputPath);

//        app.deserializeWholeFile(jsonDeserializer);
//        app.deserializeWholeFile(jsonDeserializer);

        app.deserializeByUnit(fullNewMixedOutputPath);

    }

    private void deserializeByUnit(String fullNewMixedOutputPath) throws FileNotFoundException {
        JsonDeserializer jsonDeserializer = new JsonDeserializer(fullNewMixedOutputPath);
        List<SensingUnit> sensingUnitList = new ArrayList<>();

        SensingUnit sensingUnit = jsonDeserializer.readSensingUnit();
        while (sensingUnit != null) {
            sensingUnitList.add(sensingUnit);
            sensingUnit = jsonDeserializer.readSensingUnit();
        }

        for (SensingUnit su : sensingUnitList) {
            su.printUnit();
        }

    }

    private void deserializeWholeFile(JsonDeserializer jsonDeserializer) throws FileNotFoundException {

        List<SensingUnit> sensingUnitList = jsonDeserializer.deserializeWholeFile();
        if (sensingUnitList != null) {
            for (SensingUnit su : sensingUnitList) {
                su.printUnit();
            }
        } else {
            System.out.println("No sensing units reported by deserializer");
        }
    }








    private void serializeWithJavaX(String fullCsvRawInputPath, String fullMixedOutputPath) throws FileNotFoundException {
        List<SensingUnit> sensingUnitList = readFileAsSensorUnits(fullCsvRawInputPath);
        JsonGenerator generator = instantiateGenerator(fullMixedOutputPath, true);

        generator.writeStartArray();

        for (SensingUnit sensingUnit : sensingUnitList) {
            generator.writeStartObject();

            List<SensorDataBlock> dataBlocks = sensingUnit.getDataBlocks();
            for (SensorDataBlock dataBlock : dataBlocks) {
                generator.writeStartObject(String.valueOf(dataBlock.getType()));
                generator.write("w", dataBlock.getTimestamp());

                HashMap<String, Object> values = dataBlock.getValues();
                switch (dataBlock.getType()) {
                    case Sensors.ACCELEROMETER:
                        List<AccelerometerSample> accelerationValues = (List<AccelerometerSample>) values.get("values");
                        writeAccelerationsInJson(generator, accelerationValues);
                        break;
                    case Sensors.GPS:
                        Location locationFix = (Location) values.get("values");
                        writeLocationInJson(generator, locationFix);
                        break;
                }

                generator.writeEnd();
            }

            generator.writeEnd();
        }

        generator.writeEnd();
        generator.close();
    }

    private List<SensingUnit> readFileAsSensorUnits(String fullCsvRawInputPath) {
        LocationFileReader reader = new LocationFileReader(fullCsvRawInputPath);
        Location locationRead = reader.readLine();

        List<SensingUnit> sensingUnitList = new ArrayList<>();
        while (locationRead != null) {
            HashMap<String, Object> mapValues = buildValuesMap(locationRead);
            SensorDataBlock sensorDataBlockLocation = new SensorDataBlock(Sensors.GPS, locationRead.getTime(), mapValues);
            SensorDataBlock sensorDataBlockAccelerometer = buildAccelerometerDataBlock(locationRead.getTime());

            List<SensorDataBlock> sensorDataBlockList = new ArrayList<>();
            sensorDataBlockList.add(sensorDataBlockLocation);
            sensorDataBlockList.add(sensorDataBlockAccelerometer);
            SensingUnit sensingUnit = new SensingUnit(sensorDataBlockList);
            sensingUnitList.add(sensingUnit);

            locationRead = reader.readLine();
        }
        return sensingUnitList;
    }

    private JsonGenerator instantiateGenerator(String fullMixedOutputPath, boolean pretty) throws FileNotFoundException {
        if (pretty) {
            Map<String, Object> properties = new HashMap<>(1);
            properties.put(JsonGenerator.PRETTY_PRINTING, true);
            JsonGeneratorFactory jf = Json.createGeneratorFactory(properties);
            return jf.createGenerator(new FileOutputStream(fullMixedOutputPath, false));
        }
        return Json.createGenerator(new FileOutputStream(fullMixedOutputPath, false));
    }

    private void writeAccelerationsInJson(JsonGenerator generator, List<AccelerometerSample> accelerationValues) {
        generator.writeStartArray("v");
        for (AccelerometerSample sample : accelerationValues) {
            generator.writeStartObject();
            generator.write("x", sample.getX());
            generator.write("y", sample.getY());
            generator.write("z", sample.getZ());
            generator.writeEnd();
        }
        generator.writeEnd();
    }

    private void writeLocationInJson(JsonGenerator generator, Location locationFix) {
        generator.writeStartObject("v");
        generator.write(LATITUDE, locationFix.getLatitude());
        generator.write(LONGITUDE, locationFix.getLongitude());
        generator.write(ALTITUDE, locationFix.getAltitude());
        generator.write(ACCURACY, locationFix.getAccuracy());
        generator.write(SPEED, locationFix.getSpeed());
        generator.writeEnd();
    }

    private SensorDataBlock buildAccelerometerDataBlock(long timestamp) {
        HashMap<String, Object> mapValues = new HashMap<>();
        List<AccelerometerSample> accelerometerSamples = buildAccelerometerSamples();
        mapValues.put("values", accelerometerSamples);
        SensorDataBlock accelerometerSensorDataBlock = new SensorDataBlock(Sensors.ACCELEROMETER, timestamp, mapValues);
        return accelerometerSensorDataBlock;

    }

    private HashMap<String, Object> buildValuesMap(Location location) {
        HashMap<String, Object> valuesMap = new HashMap<>();
        valuesMap.put("values", location);

        return valuesMap;
    }

    private List<AccelerometerSample> buildAccelerometerSamples() {
        List<AccelerometerSample> samples = new ArrayList<>();
        samples.add(new AccelerometerSample(10, 9, 8, System.currentTimeMillis()));
        samples.add(new AccelerometerSample(1, 2, 3, System.currentTimeMillis()));

        return samples;
    }
}
