package tamps.cinvestav.s0lver;

import tamps.cinvestav.s0lver.deserializer.AnotherDeserializer;
import tamps.cinvestav.s0lver.deserializer.JsonSensorDeserializer;
import tamps.cinvestav.s0lver.parserEntities.AbstractParserRecord;
import tamps.cinvestav.s0lver.parserEntities.AccelerometerSample;
import tamps.cinvestav.s0lver.parserEntities.Location;
import tamps.cinvestav.s0lver.parserEntities.LocationForJson;
import tamps.cinvestav.s0lver.readers.LocationFileReader;
import tamps.cinvestav.s0lver.serializers.JsonSensorSerializer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static final String CSV_RAW_INPUT_FILE = "locations.csv";
    public static final String OUTPUT_LOCATIONS_FILE = "locations.json";
    public static final String OUTPUT_MIXED_SENSORS_FILE = "mixed-sensors.json";

    private static String USER_HOME_DIR = System.getProperty("user.home");
    private static String DESKTOP_PATH = USER_HOME_DIR + File.separator + "Desktop" + File.separator;

    public static void main(String[] args) throws IOException {


        String fullCsvRawInputPath = DESKTOP_PATH + "tmp" + File.separator + CSV_RAW_INPUT_FILE;
        String fullLocationsOutputPath = DESKTOP_PATH + "tmp" + File.separator + OUTPUT_LOCATIONS_FILE;
        String fullMixedOutputPath = DESKTOP_PATH + "tmp" + File.separator + OUTPUT_MIXED_SENSORS_FILE;

        Main app = new Main();
//        app.parseRawCsvFile(fullCsvRawInputPath, fullLocationsOutputPath);
//        app.testMixedSerializer(fullCsvRawInputPath, fullMixedOutputPath);

//        app.readJsonFile();
        app.useAnotherDeserializer();
    }

    private void useAnotherDeserializer() throws IOException {
        String fullJsonMixedSensorsFile = DESKTOP_PATH + "tmp" + File.separator + OUTPUT_MIXED_SENSORS_FILE;
        AnotherDeserializer ad = new AnotherDeserializer(fullJsonMixedSensorsFile);
        ad.deserializeFile();
    }

    private void testMixedSerializer(String fullCsvRawInputPath, String fullMixedOutputPath) throws IOException {
        LocationFileReader reader = new LocationFileReader(fullCsvRawInputPath);

        JsonSensorSerializer serializer = new JsonSensorSerializer(fullMixedOutputPath);
        List<Object> sensorsData = new ArrayList<>();

        Location location = reader.readLine();
        while (location != null) {
            sensorsData.add(new LocationForJson(location));
            List<AccelerometerSample> accelerometerSamples = buildAccelerometerSamples();
            sensorsData.add(accelerometerSamples);
            location = reader.readLine();
        }

        serializer.writeJsonStreamGeneric(sensorsData);
    }

    private List<AccelerometerSample> buildAccelerometerSamples() {
        List<AccelerometerSample> samples = new ArrayList<>();
        samples.add(new AccelerometerSample(10, 9, 8, System.currentTimeMillis()));
        samples.add(new AccelerometerSample(1, 2, 3, System.currentTimeMillis()));

        return samples;
    }

    private void readJsonFile() throws IOException {
        String fullJsonMixedSensorsFile = DESKTOP_PATH + "tmp" + File.separator + OUTPUT_MIXED_SENSORS_FILE;

        JsonSensorDeserializer deserializer = new JsonSensorDeserializer(fullJsonMixedSensorsFile);
        List<List<AbstractParserRecord>> sensorDataCollection = deserializer.readJsonStream();

        for (List<AbstractParserRecord> thisSensorsData : sensorDataCollection) {
            for (AbstractParserRecord individualData : thisSensorsData) {
                System.out.println(individualData);
            }
        }
    }

    private void parseRawCsvFile(String fullCsvRawInputPath, String fullLocationsOutputPath) throws IOException {
        LocationFileReader reader = new LocationFileReader(fullCsvRawInputPath);

        JsonSensorSerializer serializer = new JsonSensorSerializer(fullLocationsOutputPath);
        List<LocationForJson> locations = new ArrayList<>();

        Location location = reader.readLine();
        while (location != null) {
            locations.add(new LocationForJson(location));
            location = reader.readLine();
        }

        serializer.writeJsonStream(locations);
    }
}
