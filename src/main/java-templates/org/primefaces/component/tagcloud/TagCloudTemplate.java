import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.FacesEvent;
import java.util.Map;
import java.util.HashMap;
import org.primefaces.util.Constants;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.tagcloud.TagCloudItem;
import org.primefaces.model.tagcloud.TagCloudModel;
import javax.faces.event.BehaviorEvent;

    public final static String STYLE_CLASS = "ui-tagcloud ui-widget ui-widget-content ui-corner-all";
    private final static String DEFAULT_EVENT = "select";

    private static final Map<String, Class<? extends BehaviorEvent>> BEHAVIOR_EVENT_MAPPING = Collections.unmodifiableMap(new HashMap<String, Class<? extends BehaviorEvent>>() {{
        put("select", SelectEvent.class);
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

    public void queueEvent(FacesEvent event) {
        FacesContext context = getFacesContext();

        if(event instanceof AjaxBehaviorEvent) {
            AjaxBehaviorEvent behaviorEvent = (AjaxBehaviorEvent) event;
            Map<String,String> params = context.getExternalContext().getRequestParameterMap();
            String eventName = params.get(Constants.RequestParams.PARTIAL_BEHAVIOR_EVENT_PARAM);
            String clientId = this.getClientId(context);

            if(eventName.equals(DEFAULT_EVENT)) {
                int itemIndex = Integer.parseInt(params.get(clientId + "_itemIndex"));
                TagCloudModel model = this.getModel();
                
                if(model != null) {
                    TagCloudItem item = model.getTags().get(itemIndex);
                    SelectEvent selectEvent = new SelectEvent(this, behaviorEvent.getBehavior(), item);
                    selectEvent.setPhaseId(behaviorEvent.getPhaseId());

                    super.queueEvent(selectEvent);
                }
            }
        } 
        else {
            super.queueEvent(event);
        }
    }