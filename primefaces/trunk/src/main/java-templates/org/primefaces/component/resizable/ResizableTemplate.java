import java.util.Arrays;
import java.util.Map;
import java.util.Collection;
import java.util.Collections;
import org.primefaces.event.ResizeEvent;
import org.primefaces.util.Constants;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.FacesEvent;
import javax.faces.event.PhaseId;

        private final static String DEFAULT_EVENT = "resize";

        private static final Collection<String> EVENT_NAMES = Collections.unmodifiableCollection(Arrays.asList(DEFAULT_EVENT));

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
            FacesContext context = FacesContext.getCurrentInstance();
            Map<String,String> params = context.getExternalContext().getRequestParameterMap();

            if(isRequestSource(context)) {
                String eventName = params.get(Constants.PARTIAL_BEHAVIOR_EVENT_PARAM);
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
            return this.getClientId(context).equals(context.getExternalContext().getRequestParameterMap().get(Constants.PARTIAL_SOURCE_PARAM));
        }