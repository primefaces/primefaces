import org.primefaces.model.Visibility;
import org.primefaces.event.ToggleEvent;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.HashMap;
import javax.faces.component.UIComponent;
import javax.faces.event.FacesEvent;
import javax.faces.event.AjaxBehaviorEvent;
import org.primefaces.util.Constants;
import org.primefaces.component.api.UIColumn;
import javax.faces.event.BehaviorEvent;
import org.primefaces.expression.SearchExpressionFacade;

    private final static String DEFAULT_EVENT = "toggle";

    private static final Map<String, Class<? extends BehaviorEvent>> BEHAVIOR_EVENT_MAPPING = Collections.unmodifiableMap(new HashMap<String, Class<? extends BehaviorEvent>>() {{
        put("toggle", ToggleEvent.class);
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

    private UIComponent dataSourceComponent = null;

    public UIComponent getDataSourceComponent() {
        if(dataSourceComponent == null) {
            FacesContext context = getFacesContext();
            String tableId = SearchExpressionFacade.resolveClientIds(context, this, this.getDatasource());
            dataSourceComponent = context.getViewRoot().findComponent(tableId);
        }

        return dataSourceComponent;
    }

