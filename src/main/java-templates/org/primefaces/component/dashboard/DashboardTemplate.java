import javax.faces.component.UIComponent;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import org.primefaces.event.DashboardReorderEvent;
import org.primefaces.util.Constants;
import org.primefaces.model.DashboardModel;
import org.primefaces.model.DashboardColumn;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.FacesEvent;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import javax.faces.event.PhaseId;
import javax.faces.event.BehaviorEvent;

    public static final String CONTAINER_CLASS = "ui-dashboard";
	public static final String COLUMN_CLASS = "ui-dashboard-column";

    private static final Map<String, Class<? extends BehaviorEvent>> BEHAVIOR_EVENT_MAPPING = Collections.unmodifiableMap(new HashMap<String, Class<? extends BehaviorEvent>>() {{
        put("reorder", DashboardReorderEvent.class);
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
    public void queueEvent(FacesEvent event) {
        FacesContext context = getFacesContext();
        
        if(ComponentUtils.isRequestSource(this, context)) {
            Map<String, String> params = context.getExternalContext().getRequestParameterMap();
            String eventName = params.get(Constants.RequestParams.PARTIAL_BEHAVIOR_EVENT_PARAM);
            String clientId = this.getClientId(context);
            AjaxBehaviorEvent behaviorEvent = (AjaxBehaviorEvent) event;

            if(eventName.equals("reorder")) {
                String widgetClientId = params.get(clientId + "_widgetId");
                Integer itemIndex = Integer.valueOf(params.get(clientId + "_itemIndex"));
                Integer receiverColumnIndex = Integer.valueOf(params.get(clientId + "_receiverColumnIndex"));
                String senderIndexParam = clientId + "_senderColumnIndex";
                Integer senderColumnIndex = null;

                if(params.containsKey(senderIndexParam)) {
                    senderColumnIndex = Integer.valueOf(params.get(senderIndexParam));
                }

                String[] idTokens = widgetClientId.split(":");
                String widgetId = idTokens.length == 1 ? idTokens[0] : idTokens[idTokens.length - 1];

                DashboardReorderEvent reorderEvent = new DashboardReorderEvent(this, behaviorEvent.getBehavior(), widgetId, itemIndex, receiverColumnIndex, senderColumnIndex);
                reorderEvent.setPhaseId(behaviorEvent.getPhaseId());

                updateDashboardModel(this.getModel(), widgetId, itemIndex, receiverColumnIndex, senderColumnIndex);
                
                super.queueEvent(reorderEvent);
            }
        }
        else {
            super.queueEvent(event);
        }
    }

    protected void updateDashboardModel(DashboardModel model, String widgetId, Integer itemIndex, Integer receiverColumnIndex, Integer senderColumnIndex) {		
		if(senderColumnIndex == null) {
			//Reorder widget in same column
			DashboardColumn column = model.getColumn(receiverColumnIndex);
			column.reorderWidget(itemIndex, widgetId);
		} else {
			//Transfer widget
			DashboardColumn oldColumn = model.getColumn(senderColumnIndex);
			DashboardColumn newColumn = model.getColumn(receiverColumnIndex);
			
			model.transferWidget(oldColumn, newColumn, widgetId, itemIndex);
		}
	}


    @Override
    public void processDecodes(FacesContext context) {
        if(ComponentUtils.isRequestSource(this, context)) {
            this.decode(context);
        }
        else {
            super.processDecodes(context);
        }
    }

    @Override
    public void processValidators(FacesContext context) {
        if(!ComponentUtils.isRequestSource(this, context)) {
            super.processValidators(context);
        }
    }

    @Override
    public void processUpdates(FacesContext context) {
        if(!ComponentUtils.isRequestSource(this, context)) {
            super.processUpdates(context);
        }
    }
