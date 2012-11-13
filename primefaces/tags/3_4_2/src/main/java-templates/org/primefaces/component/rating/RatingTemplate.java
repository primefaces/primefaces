import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import org.primefaces.event.RateEvent;
import org.primefaces.util.Constants;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.FacesEvent;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import javax.faces.event.PhaseId;

    public static final String CONTAINER_CLASS = "ui-rating";
    public static final String CANCEL_CLASS = "ui-rating-cancel";
    public static final String STAR_CLASS = "ui-rating-star";
    public static final String STAR_ON_CLASS = "ui-rating-star ui-rating-star-on";

    private final static String DEFAULT_EVENT = "rate";

    private static final Collection<String> EVENT_NAMES = Collections.unmodifiableCollection(Arrays.asList("rate","cancel"));

    private Map<String,AjaxBehaviorEvent> customEvents = new HashMap<String,AjaxBehaviorEvent>();

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

        if(event instanceof AjaxBehaviorEvent) {
            String eventName = context.getExternalContext().getRequestParameterMap().get(Constants.PARTIAL_BEHAVIOR_EVENT_PARAM);

            if(eventName.equals("rate")) {
                customEvents.put(eventName, (AjaxBehaviorEvent) event);
            }
            else if(eventName.equals("cancel")) {
                super.queueEvent(event);
            }
        } 
        else {
            super.queueEvent(event);
        }
    }

    @Override
    public void validate(FacesContext context) {
        super.validate(context);

        if(isValid()) {
            for(Iterator<String> customEventIter = customEvents.keySet().iterator(); customEventIter.hasNext();) {
                AjaxBehaviorEvent behaviorEvent = customEvents.get(customEventIter.next());
                RateEvent rateEvent = new RateEvent(this, behaviorEvent.getBehavior(), getValue());

                rateEvent.setPhaseId(behaviorEvent.getPhaseId());
                
                super.queueEvent(rateEvent);
            }
        }
    }