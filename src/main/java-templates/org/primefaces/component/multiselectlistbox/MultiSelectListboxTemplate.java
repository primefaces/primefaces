import java.util.Collection;
import java.util.Collections;
import java.util.Arrays;
import java.util.Map;
import java.util.HashMap;
import javax.faces.event.BehaviorEvent;
        
    public static String CONTAINER_CLASS = "ui-multiselectlistbox ui-widget ui-helper-clearfix";
    public static String LIST_CONTAINER_CLASS = "ui-multiselectlistbox-listcontainer";
    public static String LIST_HEADER_CLASS = "ui-multiselectlistbox-header ui-widget-header ui-corner-top";
    public static String LIST_CLASS = "ui-multiselectlistbox-list ui-inputfield ui-widget-content";
    public static String ITEM_CLASS = "ui-multiselectlistbox-item";

    private final static String DEFAULT_EVENT = "change";

    private static final Map<String, Class<? extends BehaviorEvent>> BEHAVIOR_EVENT_MAPPING = Collections.unmodifiableMap(new HashMap<String, Class<? extends BehaviorEvent>>() {{
        put("change", null);
    }});

    private static final Collection<String> EVENT_NAMES = BEHAVIOR_EVENT_MAPPING.keySet();

    @Override
    public Map<String, Class<? extends BehaviorEvent>> getBehaviorEventMapping() {
         return BEHAVIOR_EVENT_MAPPING;
    }

    @Override
    public Collection<String> getEventNames() {
        return EVENT_NAMES;
    }

    @Override
    public String getDefaultEventName() {
        return DEFAULT_EVENT;
    }
