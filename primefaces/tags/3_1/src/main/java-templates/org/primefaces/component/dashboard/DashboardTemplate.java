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

    public static final String CONTAINER_CLASS = "ui-dashboard";
	public static final String COLUMN_CLASS = "ui-dashboard-column";

    private static final Collection<String> EVENT_NAMES = Collections.unmodifiableCollection(Arrays.asList("reorder"));

    @Override
    public void queueEvent(FacesEvent event) {
        FacesContext context = FacesContext.getCurrentInstance();
        
        if(isRequestSource(context)) {
            Map<String, String> params = context.getExternalContext().getRequestParameterMap();
            String eventName = params.get(Constants.PARTIAL_BEHAVIOR_EVENT_PARAM);
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
    public Collection<String> getEventNames() {
        return EVENT_NAMES;
    }

    @Override
    public void processDecodes(FacesContext context) {
        if(isRequestSource(context)) {
            this.decode(context);
        }
        else {
            super.processDecodes(context);
        }
    }

    @Override
    public void processValidators(FacesContext context) {
        if(!isRequestSource(context)) {
            super.processValidators(context);
        }
    }

    @Override
    public void processUpdates(FacesContext context) {
        if(!isRequestSource(context)) {
            super.processUpdates(context);
        }
    }

    private boolean isRequestSource(FacesContext context) {
        return this.getClientId(context).equals(context.getExternalContext().getRequestParameterMap().get(Constants.PARTIAL_SOURCE_PARAM));
    }