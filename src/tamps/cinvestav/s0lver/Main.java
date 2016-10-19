package tamps.cinvestav.s0lver;

import tamps.cinvestav.s0lver.deserializers.JsonDeserializer;
import tamps.cinvestav.s0lver.parserEntities.SensingUnit;
import tamps.cinvestav.s0lver.readers.LocationsAsSensingUnitsReader;
import tamps.cinvestav.s0lver.serializer.JsonSerializer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static final String CSV_RAW_INPUT_FILE = "locations.csv";
    public static final String OUTPUT_NEW_MIXED_SENSORS_FILE = "mixed-sensors.json";

    private static String USER_HOME_DIR = System.getProperty("user.home");
    private static String DESKTOP_PATH = USER_HOME_DIR + File.separator + "Desktop" + File.separator;

    private JsonDeserializer jsonDeserializer;
    private JsonSerializer jsonSerializer;
    private LocationsAsSensingUnitsReader sensingUnitsReader;

    public Main(String rawInputFilePath, String outputJsonFilePath) throws FileNotFoundException {
        sensingUnitsReader = new LocationsAsSensingUnitsReader(rawInputFilePath);
        jsonSerializer = new JsonSerializer(outputJsonFilePath, true);
        jsonDeserializer = new JsonDeserializer(outputJsonFilePath);
    }

    public static void main(String[] args) throws IOException {
        String fullCsvRawInputPath = DESKTOP_PATH + "tmp" + File.separator + CSV_RAW_INPUT_FILE;

        String fullNewMixedOutputPath = DESKTOP_PATH + "tmp" + File.separator + OUTPUT_NEW_MIXED_SENSORS_FILE;
        Main app = new Main(fullCsvRawInputPath, fullNewMixedOutputPath);

        app.serializeFile();
        app.deserializeWholeFile();

        app = new Main(fullCsvRawInputPath, fullNewMixedOutputPath);
        app.deserializeByUnit();
    }

    private void serializeFile() throws FileNotFoundException {
        List<SensingUnit> sensingUnits = sensingUnitsReader.readFileAsSensorUnits();
        jsonSerializer.serialize(sensingUnits);
    }

    private void deserializeByUnit() throws FileNotFoundException {
        List<SensingUnit> sensingUnitList = new ArrayList<>();

        SensingUnit sensingUnit = jsonDeserializer.readSensingUnit();
        while (sensingUnit != null) {
            sensingUnitList.add(sensingUnit);
            sensingUnit = jsonDeserializer.readSensingUnit();
        }

        printUnits(sensingUnitList);
    }

    private void printUnits(List<SensingUnit> sensingUnitList) {
        for (SensingUnit su : sensingUnitList) {
            su.printUnit();
        }
    }

    private void deserializeWholeFile() throws FileNotFoundException {
        List<SensingUnit> sensingUnitList = jsonDeserializer.deserializeWholeFile();
        if (sensingUnitList != null) {
            printUnits(sensingUnitList);
        } else {
            System.out.println("No sensing units reported by deserializer");
        }
    }
}
