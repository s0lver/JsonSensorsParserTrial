package tamps.cinvestav.s0lver.jsonparser.parserEntities;

import tamps.cinvestav.s0lver.jsonparser.sensorEntities.AccelerometerSample;
import tamps.cinvestav.s0lver.jsonparser.sensorEntities.SimpleLocation;
import tamps.cinvestav.s0lver.jsonparser.sensorEntities.Sensors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SensingUnit {
    List<SensorDataBlock> dataBlocks;

    HashMap<Byte, Boolean> processedMark;
    public SensingUnit(List<SensorDataBlock> dataBlocks) {
        this.dataBlocks = dataBlocks;
        isFullyProcessed = false;
        processedMark = new HashMap<>();

        for (SensorDataBlock dataBlock : dataBlocks) {
            processedMark.put(dataBlock.getType(), false);
        }
    }

    public void markAsRead(byte type) {
        processedMark.replace(type, true);
        if (areAllSensorTypesCovered()) {
            this.isFullyProcessed = true;
        }
    }

    private boolean areAllSensorTypesCovered() {
        if (processedMark.values().contains(Boolean.FALSE)) {
            return false;
        }
        return true;
    }

    private boolean isFullyProcessed;

    public boolean isFullyProcessed() {
        return isFullyProcessed;
    }

    public List<SensorDataBlock> getDataBlocks() {
        return dataBlocks;
    }

    public void printUnit() {
        List<SensorDataBlock> dataBlocks = getDataBlocks();
        System.out.println(String.format("Sensing unit consisting on %s Sensors Data Blocks", dataBlocks.size()));

        for (SensorDataBlock block : dataBlocks) {
            System.out.println(String.format("Data block of %s ", Sensors.getAsString(block.getType())));
            HashMap<String, Object> values = block.getValues();
            switch (block.getType()) {
                case Sensors.ACCELEROMETER:
                    ArrayList<AccelerometerSample> accelerometerSamples = (ArrayList<AccelerometerSample>) values.get("values");
                    System.out.println(String.format("%s accelerometer samples on block", accelerometerSamples.size()));
                    break;
                case Sensors.GPS:
                    SimpleLocation location = (SimpleLocation) values.get("values");
                    System.out.println(String.format("Location data %s", location.toString()));
                    break;
                default:
                    System.out.println("not recognized at this time!");
            }
        }

        System.out.println("");
    }
}
