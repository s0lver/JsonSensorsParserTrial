package tamps.cinvestav.s0lver.serializers;

import com.google.gson.Gson;
import com.google.gson.stream.JsonWriter;
import tamps.cinvestav.s0lver.parserEntities.AccelerometerSample;
import tamps.cinvestav.s0lver.parserEntities.LocationForJson;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

public class JsonSensorSerializer {
    File outputFile;
    Gson gson;

    public JsonSensorSerializer(String path) {
        outputFile = new File(path);
        gson = new Gson();
    }

    public void writeJsonStream(List<LocationForJson> locations) throws IOException {
        JsonWriter writer = new JsonWriter(new OutputStreamWriter(new FileOutputStream(outputFile, false), "UTF-8"));
        writer.setIndent("  ");
        writer.beginArray();
        for (LocationForJson location : locations) {
            gson.toJson(location, LocationForJson.class, writer);
        }
        writer.endArray();
        writer.close();
    }

    public void writeJsonStreamGeneric(List<Object> sensorsData) throws IOException {
        JsonWriter writer = new JsonWriter(new OutputStreamWriter(new FileOutputStream(outputFile, false), "UTF-8"));
        writer.setIndent("  ");
        writer.beginArray();
        for (Object sensorData : sensorsData) {
            if (sensorData instanceof LocationForJson) {
                gson.toJson(sensorData, LocationForJson.class, writer);
            } else if (sensorData instanceof ArrayList<?>) {

                ArrayList<?> sensorsDataCollection = (ArrayList<?>) sensorData;
                if (!sensorsDataCollection.isEmpty()) {
                    // The different types of accumulated samples
                    if (sensorsDataCollection.get(0) instanceof AccelerometerSample) {
                        writer.beginArray();
                        for (Object accSample : sensorsDataCollection) {
                            gson.toJson(accSample, AccelerometerSample.class, writer);
                        }
                        writer.endArray();
                    }
                }

            }
        }

        writer.endArray();
        writer.close();
    }
}
