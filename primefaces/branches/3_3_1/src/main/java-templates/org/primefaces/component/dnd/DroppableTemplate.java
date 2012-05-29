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

    private final static String DEFAULT_EVENT = "drop";

    private static final Collection<String> EVENT_NAMES = Collections.unmodifiableCollection(Arrays.asList(DEFAULT_EVENT));

    @Override
    public void queueEvent(FacesEvent event) {
        FacesContext context = FacesContext.getCurrentInstance();

        if(isRequestSource(context)) {
            Map<String,String> params = context.getExternalContext().getRequestParameterMap();
            String eventName = params.get(Constants.PARTIAL_BEHAVIOR_EVENT_PARAM);
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

    private boolean isRequestSource(FacesContext context) {
        return this.getClientId(context).equals(context.getExternalContext().getRequestParameterMap().get(Constants.PARTIAL_SOURCE_PARAM));
    }
    
    @Override
    public Collection<String> getEventNames() {
        return EVENT_NAMES;
    }

    @Override
    public String getDefaultEventName() {
        return DEFAULT_EVENT;
    }

    protected UIData findDatasource(FacesContext context, Droppable droppable, String datasourceId) {
        UIComponent datasource = droppable.findComponent(datasourceId);
        
        if(datasource == null)
            throw new FacesException("Cannot find component \"" + datasourceId + "\" in view.");
        else
            return (UIData) datasource;
    }