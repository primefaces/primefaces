import org.primefaces.model.Visibility;
import org.primefaces.event.ToggleEvent;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import javax.faces.event.FacesEvent;
import javax.faces.event.AjaxBehaviorEvent;
import org.primefaces.util.Constants;
import org.primefaces.component.api.UIColumn;

    private final static String DEFAULT_EVENT = "toggle";

    private static final Collection<String> EVENT_NAMES = Collections.unmodifiableCollection(Arrays.asList("toggle"));

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
        String eventName = params.get(Constants.RequestParams.PARTIAL_BEHAVIOR_EVENT_PARAM);
        
        if(event instanceof AjaxBehaviorEvent && eventName.equals("toggle")) {
            String clientId = this.getClientId(context);
            Visibility visibility = Visibility.valueOf(params.get(clientId + "_visibility"));
            int index = Integer.parseInt(params.get(clientId + "_index"));

            super.queueEvent(new ToggleEvent(this, ((AjaxBehaviorEvent) event).getBehavior(), visibility, index));
        }
        else {
            super.queueEvent(event);
        }
    }