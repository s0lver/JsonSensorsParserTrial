package tamps.cinvestav.s0lver.readers;

import tamps.cinvestav.s0lver.sensorEntities.Location;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Scanner;

/***
 * Reads a file of location fixes
 */
public class LocationFileReader{
    private final int LATITUDE = 1;
    private final int LONGITUDE = 2;
    private final int ALTITUDE = 3;
    private final int ACCURACY = 4;
    private final int SPEED = 5;
    private final int TIMESTAMP = 6;

    public static final SimpleDateFormat GLOBAL_SIMPLE_DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.ENGLISH);
    public final static String FAKE_LOCATION_PROVIDER = "TimedOutLocation";

    private Scanner scanner;
    private boolean endOfFileReached;

    public LocationFileReader(String path) {
        try {
            this.scanner = new Scanner(new FileReader(path));
            this.endOfFileReached = false;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException(String.format("I couldn't open the file %s. Additionally I hate checked exceptions", path));
        }
    }

    private Location processLine(String line) {
        Location fix;
        String[] slices = line.split(",");
        if (slices[0].equals("Si")) {
            try {
                fix = new Location("CustomProvider");
                fix.setLatitude(Double.parseDouble(slices[LATITUDE]));
                fix.setLongitude(Double.parseDouble(slices[LONGITUDE]));
                fix.setAltitude(Double.parseDouble(slices[ALTITUDE]));
                fix.setAccuracy(Float.parseFloat(slices[ACCURACY]));
                fix.setSpeed(Float.parseFloat(slices[SPEED]));
                fix.setTime(GLOBAL_SIMPLE_DATE_FORMAT.parse(slices[TIMESTAMP]).getTime());
            } catch (ParseException e) {
                e.printStackTrace();
                throw new RuntimeException("I couldn't parse the date, and I hate checked exceptions");
            }
        }else{
            fix = new Location(FAKE_LOCATION_PROVIDER);
        }
        return fix;
    }

    public Location readLine() {
        if (endOfFileReached == false) {
            if (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                Location location = processLine(line);
                return location;
            } else {
                scanner.close();
                endOfFileReached = true;
            }
        }
        return null;
    }
}
