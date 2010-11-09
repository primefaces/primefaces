import java.util.Map;
import java.util.LinkedHashMap;

    private Map<String,Number> data = new LinkedHashMap<String,Number>();

    public void set(String category, Number value) {
        data.put(category, value);
    }

    public Map<String,Number> getData() {
        return data;
    }