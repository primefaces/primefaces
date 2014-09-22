import java.util.Collection;
import java.util.Collections;
import java.util.Arrays;

    public final static String CONTAINER_CLASS = "ui-inputswitch ui-widget ui-widget-content ui-corner-all";
    public final static String ON_LABEL_CLASS = "ui-inputswitch-on ui-state-active";
    public final static String OFF_LABEL_CLASS = "ui-inputswitch-off";
    public final static String HANDLE_CLASS = "ui-inputswitch-handle ui-state-default ";
   
    private final static String DEFAULT_EVENT = "change";
    private static final Collection<String> EVENT_NAMES = Collections.unmodifiableCollection(Arrays.asList(DEFAULT_EVENT));

    @Override
    public Collection<String> getEventNames() {
        return EVENT_NAMES;
    }

    @Override
    public String getDefaultEventName() {
        return DEFAULT_EVENT;
    }