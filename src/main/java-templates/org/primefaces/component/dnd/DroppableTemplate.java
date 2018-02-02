import org.primefaces.event.DragDropEvent;
import javax.el.ValueExpression;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import javax.faces.component.UIComponent;
import javax.faces.component.UIData;
import javax.faces.event.FacesEvent;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.FacesException;
import org.primefaces.util.Constants;
import org.primefaces.expression.SearchExpressionFacade;
import javax.faces.event.BehaviorEvent;

    private final static String DEFAULT_EVENT = "drop";

    private static final Map<String, Class<? extends BehaviorEvent>> BEHAVIOR_EVENT_MAPPING = Collections.unmodifiableMap(new HashMap<String, Class<? extends BehaviorEvent>>() {{
        put("drop", null);
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

        if(ComponentUtils.isRequestSource(this, context)) {
            Map<String,String> params = context.getExternalContext().getRequestParameterMap();
            String eventName = params.get(Constants.RequestParams.PARTIAL_BEHAVIOR_EVENT_PARAM);
            String clientId = getClientId(context);

            AjaxBehaviorEvent behaviorEvent = (AjaxBehaviorEvent) event;

            if(eventName.equals("drop")) {
                String dragId = params.get(clientId + "_dragId");
                String dropId = params.get(clientId + "_dropId");
                DragDropEvent dndEvent = null;
                String datasourceId = getDatasource();

                if(datasourceId != null) {
                    UIData datasource = findDatasource(context, this, datasourceId);
                    String[] idTokens = dragId.split(String.valueOf(UINamingContainer.getSeparatorChar(context)));
                    int rowIndex = Integer.parseInt(idTokens[idTokens.length - 2]);
                    datasource.setRowIndex(rowIndex);
                    Object data = datasource.getRowData();
                    datasource.setRowIndex(-1);

                    dndEvent = new DragDropEvent(this, behaviorEvent.getBehavior(), dragId, dropId, data);
                }
                else {
                    dndEvent = new DragDropEvent(this, behaviorEvent.getBehavior(), dragId, dropId);
                }

                super.queueEvent(dndEvent);
            }
            
        }
        else {
            super.queueEvent(event);
        }
    }

    protected UIData findDatasource(FacesContext context, Droppable droppable, String datasourceId) {
        UIComponent datasource = SearchExpressionFacade.resolveComponent(context, droppable, datasourceId);
        
        if(datasource == null)
            throw new FacesException("Cannot find component \"" + datasourceId + "\" in view.");
        else
            return (UIData) datasource;
    }