import java.util.Arrays;
import java.util.Map;
import java.util.HashMap;
import java.util.Collection;
import java.util.Collections;
import org.primefaces.event.ResizeEvent;
import org.primefaces.util.Constants;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.FacesEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.BehaviorEvent;

        private final static String DEFAULT_EVENT = "resize";

        private static final Map<String, Class<? extends BehaviorEvent>> BEHAVIOR_EVENT_MAPPING = Collections.unmodifiableMap(new HashMap<String, Class<? extends BehaviorEvent>>() {{
            put("resize", ResizeEvent.class);
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

        @Override
        public void queueEvent(FacesEvent event) {
            FacesContext context = getFacesContext();
            Map<String,String> params = context.getExternalContext().getRequestParameterMap();

            if(isRequestSource(context)) {
                String eventName = params.get(Constants.RequestParams.PARTIAL_BEHAVIOR_EVENT_PARAM);
                String clientId = getClientId(context);
                
                if(eventName.equals("resize")) {
                    AjaxBehaviorEvent behaviorEvent = (AjaxBehaviorEvent) event;
                    int width = Integer.parseInt(params.get(clientId + "_width"));
                    int height = Integer.parseInt(params.get(clientId + "_height"));

                    super.queueEvent(new ResizeEvent(this, behaviorEvent.getBehavior(), width, height));
                }
                
            } else {
                super.queueEvent(event);
            }
        }

        private boolean isRequestSource(FacesContext context) {
            return this.getClientId(context).equals(context.getExternalContext().getRequestParameterMap().get(Constants.RequestParams.PARTIAL_SOURCE_PARAM));
        }