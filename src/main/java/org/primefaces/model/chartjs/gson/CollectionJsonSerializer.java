package org.primefaces.model.chartjs.gson;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.primefaces.model.chartjs.objects.OptionalArray;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

public class CollectionJsonSerializer<T> extends TypeAdapter<Collection<T>> {

    @Override
    public void write(JsonWriter jsonWriter, Collection<T> src) throws IOException {
        if (src instanceof OptionalArray && src.size() == 1) {
            jsonWriter.jsonValue(GSON.INSTANCE.toJson(((List<T>) src).get(0)));
        }
        else if (src == null || src.isEmpty()) {
            jsonWriter.nullValue();
        }
        else {
            jsonWriter.beginArray();

            for (Object child : src) {
                jsonWriter.jsonValue(GSON.INSTANCE.toJson(child));
            }

            jsonWriter.endArray();
        }
    }

    @Override
    public Collection<T> read(JsonReader jsonReader) {
        return null;
    }
}
