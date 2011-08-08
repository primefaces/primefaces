import org.primefaces.model.TreeTableModel;
import org.primefaces.model.TreeNode;
import javax.faces.model.DataModel;
import javax.faces.event.FacesEvent;
import java.util.Collection;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import javax.faces.event.AjaxBehaviorEvent;
import org.primefaces.util.Constants;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.NodeUnselectEvent;
import org.primefaces.event.NodeExpandEvent;
import org.primefaces.event.NodeCollapseEvent;

	public final static String CONTAINER_CLASS = "ui-treetable ui-widget";
    public final static String HEADER_CLASS = "ui-treetable-header ui-widget-header ui-corner-top";
	public final static String DATA_CLASS = "ui-treetable-data";
    public final static String FOOTER_CLASS = "ui-treetable-footer ui-widget-header";
    public final static String COLUMN_HEADER_CLASS = "ui-state-default";
    public final static String ROW_CLASS = "ui-widget-content";
    public final static String COLUMN_CONTENT_WRAPPER = "ui-tt-c";
    public final static String EXPAND_ICON = "ui-treetable-toggler ui-icon ui-icon-triangle-1-e";
    public final static String COLLAPSE_ICON = "ui-treetable-toggler ui-icon ui-icon-triangle-1-s";

    private static final Collection<String> EVENT_NAMES = Collections.unmodifiableCollection(Arrays.asList("select","unselect", "expand", "collapse", "colResize"));

    @Override
    public Collection<String> getEventNames() {
        return EVENT_NAMES;
    }

    private boolean isRequestSource(FacesContext context) {
        return this.getClientId(context).equals(context.getExternalContext().getRequestParameterMap().get(Constants.PARTIAL_SOURCE_PARAM));
    }

    @Override
    public void queueEvent(FacesEvent event) {
        FacesContext context = FacesContext.getCurrentInstance();

        if(isRequestSource(context)) {
            Map<String,String> params = context.getExternalContext().getRequestParameterMap();
            String eventName = params.get(Constants.PARTIAL_BEHAVIOR_EVENT_PARAM);
            String clientId = this.getClientId(context);
            FacesEvent wrapperEvent = null;

            AjaxBehaviorEvent behaviorEvent = (AjaxBehaviorEvent) event;

            if(eventName.equals("expand")) {
                String nodeKey = params.get(clientId + "_expand");
                this.setRowKey(nodeKey);
                TreeNode node = this.getRowNode();

                wrapperEvent = new NodeExpandEvent(this, behaviorEvent.getBehavior(), node);
            } else if(eventName.equals("collapse")) {
                String nodeKey = params.get(clientId + "_collapse");
                this.setRowKey(nodeKey);
                TreeNode node = this.getRowNode();

                wrapperEvent = new NodeCollapseEvent(this, behaviorEvent.getBehavior(), node);
            }
 
            wrapperEvent.setPhaseId(behaviorEvent.getPhaseId());

            super.queueEvent(wrapperEvent);
        }
        else {
            super.queueEvent(event);
        }
    }