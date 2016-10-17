package tamps.cinvestav.s0lver.deserializer;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;

import java.io.*;

public class AnotherDeserializer {
    // http://www.studytrails.com/java/json/java-google-json-parse-json-token-by-token/
    private File fileInput;

//    public static void main(String[] args) throws MalformedURLException, IOException
//    {
//        String url = "http://freemusicarchive.org/api/get/albums.json?api_key=60BLHNQCAOUFPIBZ&limit=1";
//        String json = IOUtils.toString(new URL(url));
//        // use the reader to read the json to a stream of tokens
//        JsonReader reader = new JsonReader(new StringReader(json));
//        // we call the handle object method to handle the full json object. This
//        // implies that the first token in JsonToken.BEGIN_OBJECT, which is
//        // always true.
//        handleObject(reader);
//    }


    public AnotherDeserializer(String filePath) {
        this.fileInput = new File(filePath);
    }

    public void deserializeFile() throws IOException {
        JsonReader reader = new JsonReader(new InputStreamReader(new FileInputStream(fileInput), "UTF-8"));
        handleArray(reader);
    }

    /**
     * Handle an Object. Consume the first token which is BEGIN_OBJECT. Within
     * the Object there could be array or non array tokens. We write handler
     * methods for both. Noe the peek() method. It is used to find out the type
     * of the next token without actually consuming it.
     *
     * @param reader
     * @throws IOException
     */
    private void handleObject(JsonReader reader) throws IOException {
        reader.beginObject();
        while (reader.hasNext()) {
            JsonToken token = reader.peek();
            if (token.equals(JsonToken.BEGIN_ARRAY))
                handleArray(reader);
            else if (token.equals(JsonToken.END_OBJECT)) {
                reader.endObject();
                return;
            } else
                handleNonArrayToken(reader, token);
        }

    }

    /**
     * Handle a json array. The first token would be JsonToken.BEGIN_ARRAY.
     * Arrays may contain objects or primitives.
     *
     * @param reader
     * @throws IOException
     */
    private void handleArray(JsonReader reader) throws IOException {
        reader.beginArray();
        while (true) {
            JsonToken token = reader.peek();
            if (token.equals(JsonToken.END_ARRAY)) {
                System.out.println("Closing array");
                reader.endArray();
                break;
            } else if (token.equals(JsonToken.BEGIN_OBJECT)) {
                System.out.println("Starting obj");
                handleObject(reader);
            } else if (token.equals(JsonToken.END_OBJECT)) {
                System.out.println("Closing obj");
                reader.endObject();
            } else if (token.equals(JsonToken.BEGIN_ARRAY)) {
                System.out.println("Starting array");
                handleArray(reader);
            }
            else
                handleNonArrayToken(reader, token);
        }
    }

    /**
     * Handle non array non object tokens
     *
     * @param reader
     * @param token
     * @throws IOException
     */
    private void handleNonArrayToken(JsonReader reader, JsonToken token) throws IOException {
        if (token.equals(JsonToken.NAME))
            System.out.println("Name = " + reader.nextName());
        else if (token.equals(JsonToken.STRING))
            System.out.println(reader.nextString());
        else if (token.equals(JsonToken.NUMBER))
            System.out.println(reader.nextDouble());
        else
            reader.skipValue();
    }
}
