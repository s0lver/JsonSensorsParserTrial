package tamps.cinvestav.s0lver.deserializer;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import tamps.cinvestav.s0lver.parserEntities.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class JsonSensorDeserializer {
    private File fileInput;
    private Gson gson;

    public JsonSensorDeserializer(String filePath) {
        this.fileInput = new File(filePath);
        this.gson = new Gson();
    }

    public List<List<AbstractParserRecord>> readJsonStream() throws IOException {
        JsonReader reader = new JsonReader(new InputStreamReader(new FileInputStream(fileInput), "UTF-8"));
        List<List<AbstractParserRecord>> sensorsData = new ArrayList<>();
        reader.beginArray();

        while (reader.hasNext()) {
            JsonToken peek = reader.peek();
            if (peek.equals(JsonToken.BEGIN_OBJECT)) {
                AbstractParserRecord sensorRecord = gson.fromJson(reader, AbstractParserRecord.class);
                if (sensorRecord.getType() == Sensors.GPS) {
                    List<AbstractParserRecord> locationUpdate = new ArrayList<>();
                    locationUpdate.add(sensorRecord);
                    sensorsData.add(locationUpdate);
                }
                else{
                    System.out.println("Individual sensor sample not recognized");
                }
            }else if (peek.equals(JsonToken.BEGIN_ARRAY)){
                ArrayList<AbstractParserRecord> samples = gson.fromJson(reader, ArrayList.class);
                if (samples != null && !samples.isEmpty()) {
                    if (samples.get(0).getType() == Sensors.ACCELEROMETER) {
                        sensorsData.add(samples);
                    }else{
                        System.out.println("Not known by the moment");
                    }
                }
            }
        }

        reader.endArray();
        reader.close();
        return sensorsData;
    }
}
