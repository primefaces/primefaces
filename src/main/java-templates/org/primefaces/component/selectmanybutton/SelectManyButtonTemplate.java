import java.util.Collection;
import java.util.Collections;
import java.util.Arrays;

    public final static String STYLE_CLASS = "ui-selectmanybutton ui-buttonset ui-widget ui-corner-all";
    public final static String MOBILE_STYLE_CLASS = "ui-selectmanybutton ui-controlgroup ui-controlgroup-horizontal ui-corner-all";
    public final static String MOBILE_ITEMS_CLASS = "ui-controlgroup-controls";
    public final static String MOBILE_LABEL_CLASS = "ui-btn ui-corner-all ui-btn-inherit";

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