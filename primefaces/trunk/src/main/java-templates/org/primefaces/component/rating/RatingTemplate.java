import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import org.primefaces.event.RateEvent;
import org.primefaces.util.Constants;
import javax.faces.event.BehaviorEvent;
import javax.faces.event.FacesEvent;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import javax.faces.event.PhaseId;

    private final static String DEFAULT_EVENT = "rate";

    private static final Collection<String> EVENT_NAMES = Collections.unmodifiableCollection(Arrays.asList(DEFAULT_EVENT));

    private Map<String,BehaviorEvent> customEvents = new HashMap<String,BehaviorEvent>();

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
        String eventName = context.getExternalContext().getRequestParameterMap().get(Constants.PARTIAL_BEHAVIOR_EVENT_PARAM);

        if(eventName != null && eventName.equals("rate") && event instanceof BehaviorEvent) {
            customEvents.put("rate", (BehaviorEvent) event);
        } else {
            super.queueEvent(event);
        }
    }

    @Override
    public void validate(FacesContext context) {
        super.validate(context);

        if(isValid()) {
            for(Iterator<String> customEventIter = customEvents.keySet().iterator(); customEventIter.hasNext();) {
                BehaviorEvent behaviorEvent = customEvents.get(customEventIter.next());
                RateEvent rateEvent = new RateEvent(this, behaviorEvent.getBehavior(), getValue());

                if(behaviorEvent.getPhaseId().equals(PhaseId.APPLY_REQUEST_VALUES)) {
                    rateEvent.setPhaseId(PhaseId.PROCESS_VALIDATIONS);
                }
                
                super.queueEvent(rateEvent);
            }
        }
        customEvents = null;
    }