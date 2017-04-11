import org.primefaces.event.organigram.OrganigramNodeCollapseEvent;
import org.primefaces.event.organigram.OrganigramNodeDragDropEvent;
import org.primefaces.event.organigram.OrganigramNodeExpandEvent;
import org.primefaces.event.organigram.OrganigramNodeSelectEvent;
import org.primefaces.model.OrganigramNode;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.BehaviorEvent;
import javax.faces.event.FacesEvent;
import org.primefaces.component.api.PrimeClientBehaviorHolder;
import org.primefaces.component.api.Widget;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.Constants;


    private static final String DEFAULT_EVENT = "select";

    private static final Map<String, Class<? extends BehaviorEvent>> BEHAVIOR_EVENT_MAPPING = Collections.unmodifiableMap(new HashMap<String, Class<? extends BehaviorEvent>>() {{
        put("select", OrganigramNodeSelectEvent.class);
        put("expand", OrganigramNodeExpandEvent.class);
        put("collapse", OrganigramNodeCollapseEvent.class);
        put("dragdrop", OrganigramNodeDragDropEvent.class);
        put("contextmenu", OrganigramNodeSelectEvent.class);
    }});

    private static final Collection<String> EVENT_NAMES = BEHAVIOR_EVENT_MAPPING.keySet();

    @Override
    public Map<String, Class<? extends BehaviorEvent>> getBehaviorEventMapping() {
         return BEHAVIOR_EVENT_MAPPING;
    }

    @Override
    public Collection<String> getEventNames()
    {
        return EVENT_NAMES;
    }

    @Override
    public String getDefaultEventName()
    {
        return DEFAULT_EVENT;
    }

    @Override
    public void queueEvent(FacesEvent event)
    {
        FacesContext context = getFacesContext();

        if (isRequestSource(context) && event instanceof AjaxBehaviorEvent)
        {
            Map<String, String> params = context.getExternalContext().getRequestParameterMap();
            String eventName = params.get(Constants.RequestParams.PARTIAL_BEHAVIOR_EVENT_PARAM);
            String clientId = this.getClientId(context);
            FacesEvent wrapperEvent = null;
            AjaxBehaviorEvent behaviorEvent = (AjaxBehaviorEvent) event;

            if (eventName.equals("expand"))
            {
                OrganigramNode node = findTreeNode(getValue(), params.get(clientId + "_expandNode"));
                node.setExpanded(true);

                wrapperEvent = new OrganigramNodeExpandEvent(this, behaviorEvent.getBehavior(), node);
            }
            else if (eventName.equals("collapse"))
            {
                OrganigramNode node = findTreeNode(getValue(), params.get(clientId + "_collapseNode"));
                node.setExpanded(false);

                wrapperEvent = new OrganigramNodeCollapseEvent(this, behaviorEvent.getBehavior(), node);
            }
            else if (eventName.equals("select") || eventName.equals("contextmenu"))
            {
                OrganigramNode node = findTreeNode(getValue(), params.get(clientId + "_selectNode"));

                wrapperEvent = new OrganigramNodeSelectEvent(this, behaviorEvent.getBehavior(), node);
            }
            else if (eventName.equals("dragdrop"))
            {
                OrganigramNode dragNode = findTreeNode(getValue(), params.get(clientId + "_dragNode"));
                OrganigramNode dropNode = findTreeNode(getValue(), params.get(clientId + "_dropNode"));

                // remove node from current parent
                if (dragNode != null && dropNode != null)
                {
                    OrganigramNode sourceNode = dragNode.getParent();

                    if (sourceNode != null)
                    {
                        sourceNode.getChildren().remove(dragNode);
                    }

                    // set new parent
                    dragNode.setParent(dropNode);

                    wrapperEvent = new OrganigramNodeDragDropEvent(this, behaviorEvent.getBehavior(), dragNode, dropNode, sourceNode);
                }
            }

            wrapperEvent.setPhaseId(behaviorEvent.getPhaseId());

            super.queueEvent(wrapperEvent);
        }
        else
        {
            super.queueEvent(event);
        }
    }

    public boolean isRequestSource(FacesContext context)
    {
        return this.getClientId(context).equals(context.getExternalContext().getRequestParameterMap().get(Constants.RequestParams.PARTIAL_SOURCE_PARAM));
    }

    public OrganigramNode findTreeNode(OrganigramNode searchRoot, String rowKey)
    {
        if (rowKey != null && rowKey.equals("root"))
        {
            return getValue();
        }

        return OrganigramHelper.findTreeNode(searchRoot, rowKey);
    }