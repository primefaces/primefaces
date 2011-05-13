import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import org.primefaces.event.RateEvent;
import javax.faces.event.BehaviorEvent;
import javax.faces.event.FacesEvent;

    private final static String DEFAULT_EVENT = "rate";

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
        if(event instanceof BehaviorEvent) {
            BehaviorEvent behaviorEvent = (BehaviorEvent) event;
            String submittedValue = (String) getSubmittedValue();
            Double value = submittedValue == null? 0D : Double.valueOf(submittedValue);

            super.queueEvent(new RateEvent(this, behaviorEvent.getBehavior(), value));
        }
    }