import org.primefaces.model.mindmap.MindmapNode;
import org.primefaces.event.SelectEvent;
import java.util.Collection;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.HashMap;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.FacesEvent;
import org.primefaces.util.Constants;
import javax.faces.event.BehaviorEvent;
import org.primefaces.util.ComponentUtils;

    public final static String STYLE_CLASS = "ui-mindmap ui-widget ui-widget-content ui-corner-all";

    private static final Map<String, Class<? extends BehaviorEvent>> BEHAVIOR_EVENT_MAPPING = Collections.unmodifiableMap(new HashMap<String, Class<? extends BehaviorEvent>>() {{
        put("select", SelectEvent.class);
        put("dblselect", SelectEvent.class);
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

    private MindmapNode selectedNode = null;

    public MindmapNode getSelectedNode() {
        return selectedNode;
    }

    public String getSelectedNodeKey(FacesContext context) {
        return context.getExternalContext().getRequestParameterMap().get(this.getClientId(context) + "_nodeKey");
    }

    @Override
    public void queueEvent(FacesEvent event) {
        FacesContext context = getFacesContext();
        Map<String,String> params = context.getExternalContext().getRequestParameterMap();
        String clientId = this.getClientId(context);
        AjaxBehaviorEvent behaviorEvent = (AjaxBehaviorEvent) event;
        String eventName = params.get(Constants.RequestParams.PARTIAL_BEHAVIOR_EVENT_PARAM);

        if(eventName.equals("select")||eventName.equals("dblselect")) {
            String nodeKey = params.get(clientId + "_nodeKey");
            MindmapNode node = nodeKey.equals("root") ? this.getValue() : this.findNode(this.getValue(), nodeKey);
            this.selectedNode = node;
        
            super.queueEvent(new SelectEvent(this, behaviorEvent.getBehavior(), node));
        }
    }

    protected MindmapNode findNode(MindmapNode searchRoot, String rowKey) {
		String[] paths = rowKey.split("_");
		
		if(paths.length == 0)
			return null;
		
		int childIndex = Integer.parseInt(paths[0]);
		searchRoot = searchRoot.getChildren().get(childIndex);

		if(paths.length == 1) {
			return searchRoot;
		} 
		else {
			String relativeRowKey = rowKey.substring(rowKey.indexOf("_") + 1);
				
			return findNode(searchRoot, relativeRowKey);
		}
	}

    public boolean isNodeSelectRequest(FacesContext context) {
        if (!ComponentUtils.isRequestSource(this, context)) {
            return false;
        }
        Map<String,String> params = context.getExternalContext().getRequestParameterMap();
        String eventName = params.get(Constants.RequestParams.PARTIAL_BEHAVIOR_EVENT_PARAM);
        return eventName.equals("select");
    }