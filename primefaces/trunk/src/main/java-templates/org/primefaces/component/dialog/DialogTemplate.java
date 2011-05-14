import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import org.primefaces.event.CloseEvent;
import org.primefaces.util.Constants;
import javax.faces.event.BehaviorEvent;
import javax.faces.event.FacesEvent;

    private final static String DEFAULT_EVENT = "close";

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
        String eventName = context.getExternalContext().getRequestParameterMap().get(Constants.PARTIAL_BEHAVIOR_EVENT_PARAM);

        if(eventName != null && eventName.equals("close") && event instanceof BehaviorEvent) {
            setVisible(false);
            CloseEvent closeEvent = new CloseEvent(this, ((BehaviorEvent) event).getBehavior());
            super.queueEvent(closeEvent);
        } else {
            super.queueEvent(event);
        }
    }