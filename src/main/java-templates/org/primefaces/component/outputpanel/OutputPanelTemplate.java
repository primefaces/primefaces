import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.HashMap;
import org.primefaces.event.CloseEvent;
import org.primefaces.event.MoveEvent;
import org.primefaces.util.Constants;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.FacesEvent;
import javax.faces.event.PhaseId;
import javax.el.ELContext;
import javax.faces.event.BehaviorEvent;
    
    public static final String CONTAINER_CLASS = "ui-outputpanel ui-widget";
    public static final String LOADING_CLASS = "ui-outputpanel-loading ui-widget";

    private final static String DEFAULT_EVENT = "load";

    private static final Map<String, Class<? extends BehaviorEvent>> BEHAVIOR_EVENT_MAPPING = Collections.unmodifiableMap(new HashMap<String, Class<? extends BehaviorEvent>>() {{
        put("load", null);
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

    public boolean isContentLoad(FacesContext context) {
        String clientId = this.getClientId(context);

        return context.getExternalContext().getRequestParameterMap().containsKey(clientId + "_load");
    }