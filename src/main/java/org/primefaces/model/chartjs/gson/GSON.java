package org.primefaces.model.chartjs.gson;

import java.util.Collection;

import org.primefaces.model.chartjs.color.Color;
import org.primefaces.model.chartjs.enums.EnumSerializer;
import org.primefaces.model.chartjs.javascript.JavaScriptFunction;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

public class GSON {

    public static final GSON INSTANCE = new GSON();

    private final Gson gson;

    private GSON() {
        gson = new GsonBuilder()
                .registerTypeAdapter(Color.class, EnumSerializer.INSTANCE)
                .registerTypeAdapter(JavaScriptFunction.class, new JavaScriptFunction.JavaScriptFunctionTypeAdapter())
                .registerTypeHierarchyAdapter(Enum.class, EnumSerializer.INSTANCE)
                .registerTypeHierarchyAdapter(Collection.class, new CollectionJsonSerializer())
                .setPrettyPrinting()
                .create();
    }

    public String toJson(Object src) {
        return gson.toJson(src);
    }

    public <T> T fromJson(String json, Class<T> classOfT) throws JsonSyntaxException {
        return gson.fromJson(json, classOfT);
    }
}
