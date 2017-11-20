import java.util.Collection;
import java.util.Collections;
import java.util.Arrays;
import java.util.Map;
import java.util.HashMap;
import javax.faces.event.BehaviorEvent;

    public final static String STYLE_CLASS = "ui-selectonebutton ui-buttonset ui-widget ui-corner-all";
    public final static String MOBILE_STYLE_CLASS = "ui-selectonebutton ui-controlgroup ui-controlgroup-horizontal ui-corner-all";
    public final static String MOBILE_ITEMS_CLASS = "ui-controlgroup-controls";
    public final static String MOBILE_LABEL_CLASS = "ui-btn ui-corner-all ui-btn-inherit";

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