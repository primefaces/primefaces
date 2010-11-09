import java.util.Map;
import java.util.LinkedHashMap;

    private Map<String,Number> data = new LinkedHashMap<String,Number>();

    public void set(String category, Number value) {
        data.put(category, value);
    }

    public Map<String,Number> getData() {
        return data;
    }

    public ChartSeries(String label) {
        setLabel(label);
    }

    private String key;

    public void setKey(String key) {
        this.key = key;
    }

    public String getKey() {
        if(this.key == null) {
            this.key = this.getId();
        }
        
        return this.key;
    }