package library.learn.com.basetools.http;

import com.google.gson.TypeAdapter;
import com.google.gson.internal.bind.TypeAdapters;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

/**
 * Gson转换时，支持Json/JsonArray转换成String
 * Created byjay on 2016/12/21.
 */

public class StringAdapter extends TypeAdapter<String> {
    public String read(JsonReader in) throws IOException {
        switch (in.peek()) {
            default:
                return TypeAdapters.STRING.read(in);
            case BEGIN_OBJECT:
            case BEGIN_ARRAY:
                return TypeAdapters.JSON_ELEMENT.read(in).toString();
        }
    }

    public void write(JsonWriter out, String value) throws IOException {
        out.value(value);
    }
}
