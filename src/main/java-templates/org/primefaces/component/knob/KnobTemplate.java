import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

    private static final Collection<String> EVENT_NAMES = Collections.unmodifiableCollection(Arrays.asList("change"));

    public Collection<String> getEventNames() {
            return EVENT_NAMES;
    }

    @Override
    public String getDefaultEventName() {
        return "change";
    }
