import javax.faces.component.UIComponentBase;
import javax.faces.context.FacesContext;
import javax.faces.component.UINamingContainer;
import javax.el.ValueExpression;
import javax.el.MethodExpression;
import javax.faces.render.Renderer;
import java.io.IOException;
import javax.faces.component.UIComponent;
import javax.faces.event.AbortProcessingException;
import javax.faces.application.ResourceDependencies;
import javax.faces.application.ResourceDependency;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.TimeZone;
import java.util.logging.Logger;
import javax.faces.FacesException;
import javax.faces.component.UIForm;
import javax.faces.component.UIViewRoot;
import javax.faces.component.UniqueIdVendor;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.FacesEvent;
import org.primefaces.event.timeline.TimelineAddEvent;
import org.primefaces.event.timeline.TimelineDragDropEvent;
import org.primefaces.event.timeline.TimelineLazyLoadEvent;
import org.primefaces.event.timeline.TimelineModificationEvent;
import org.primefaces.event.timeline.TimelineRangeEvent;
import org.primefaces.event.timeline.TimelineSelectEvent;
import org.primefaces.model.timeline.TimelineEvent;
import org.primefaces.model.timeline.TimelineGroup;
import org.primefaces.model.timeline.TimelineModel;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.Constants;
import org.primefaces.util.DateUtils;
import org.primefaces.visit.UIDataContextCallback;
import javax.faces.event.BehaviorEvent;
import org.primefaces.util.ComponentTraversalUtils;

    private final static Logger logger = Logger.getLogger(Timeline.class.getName());
    
    private static final Map<String, Class<? extends BehaviorEvent>> BEHAVIOR_EVENT_MAPPING = Collections.unmodifiableMap(new HashMap<String, Class<? extends BehaviorEvent>>() {{
        put("add", TimelineAddEvent.class);
        put("change", TimelineModificationEvent.class);
        put("changed", TimelineModificationEvent.class);
        put("edit", TimelineModificationEvent.class);
        put("delete", TimelineModificationEvent.class);
        put("select", TimelineSelectEvent.class);
        put("rangechange", TimelineRangeEvent.class);
        put("rangechanged", TimelineRangeEvent.class);
        put("lazyload", TimelineLazyLoadEvent.class);
        put("drop", TimelineDragDropEvent.class);
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
        FacesContext context = FacesContext.getCurrentInstance();

        if (isSelfRequest(context)) {
            Map<String, String> params = context.getExternalContext().getRequestParameterMap();
            String eventName = params.get(Constants.RequestParams.PARTIAL_BEHAVIOR_EVENT_PARAM);
            String clientId = this.getClientId(context);

            AjaxBehaviorEvent behaviorEvent = (AjaxBehaviorEvent) event;

            if ("add".equals(eventName)) {
                // preset start / end date and the group
                TimeZone targetTZ = ComponentUtils.resolveTimeZone(getTimeZone());
                TimeZone browserTZ = ComponentUtils.resolveTimeZone(getBrowserTimeZone());

                TimelineAddEvent te =
                        new TimelineAddEvent(this, behaviorEvent.getBehavior(),
                                DateUtils.toUtcDate(browserTZ, targetTZ, params.get(clientId + "_startDate")),
                                DateUtils.toUtcDate(browserTZ, targetTZ, params.get(clientId + "_endDate")),
                                getGroup(params.get(clientId + "_group")));
                te.setPhaseId(behaviorEvent.getPhaseId());
                super.queueEvent(te);

                return;
            } else if ("change".equals(eventName) || "changed".equals(eventName)) {
                TimelineEvent clonedEvent = null;
                TimelineEvent timelineEvent = this.getValue().getEvent(params.get(clientId + "_eventIdx"));

                if (timelineEvent != null) {
                    clonedEvent = new TimelineEvent();
                    clonedEvent.setData(timelineEvent.getData());
                    clonedEvent.setEditable(timelineEvent.isEditable());
                    clonedEvent.setStyleClass(timelineEvent.getStyleClass());

                    // update start / end date and the group
                    TimeZone targetTZ = ComponentUtils.resolveTimeZone(getTimeZone());
                    TimeZone browserTZ = ComponentUtils.resolveTimeZone(getBrowserTimeZone());
                    clonedEvent.setStartDate(DateUtils.toUtcDate(browserTZ, targetTZ, params.get(clientId + "_startDate")));
                    clonedEvent.setEndDate(DateUtils.toUtcDate(browserTZ, targetTZ, params.get(clientId + "_endDate")));
                    clonedEvent.setGroup(getGroup(params.get(clientId + "_group")));
                }

                TimelineModificationEvent te = new TimelineModificationEvent(this, behaviorEvent.getBehavior(), clonedEvent);
                te.setPhaseId(behaviorEvent.getPhaseId());
                super.queueEvent(te);

                return;
            } else if ("edit".equals(eventName) || "delete".equals(eventName)) {
                TimelineEvent clonedEvent = null;
                TimelineEvent timelineEvent = this.getValue().getEvent(params.get(clientId + "_eventIdx"));

                if (timelineEvent != null) {
                    clonedEvent = new TimelineEvent();
                    clonedEvent.setData(timelineEvent.getData());
                    clonedEvent.setStartDate((Date) timelineEvent.getStartDate().clone());
                    clonedEvent.setEndDate(timelineEvent.getEndDate() != null ? (Date) timelineEvent.getEndDate().clone() : null);
                    clonedEvent.setEditable(timelineEvent.isEditable());
                    clonedEvent.setGroup(timelineEvent.getGroup());
                    clonedEvent.setStyleClass(timelineEvent.getStyleClass());
                }

                TimelineModificationEvent te = new TimelineModificationEvent(this, behaviorEvent.getBehavior(), clonedEvent);
                te.setPhaseId(behaviorEvent.getPhaseId());
                super.queueEvent(te);

                return;
            } else if ("select".equals(eventName)) {
                TimelineEvent timelineEvent = this.getValue().getEvent(params.get(clientId + "_eventIdx"));
                TimelineSelectEvent te = new TimelineSelectEvent(this, behaviorEvent.getBehavior(), timelineEvent);
                te.setPhaseId(behaviorEvent.getPhaseId());
                super.queueEvent(te);

                return;
            } else if ("rangechange".equals(eventName) || "rangechanged".equals(eventName)) {
                TimeZone targetTZ = ComponentUtils.resolveTimeZone(getTimeZone());
                TimeZone browserTZ = ComponentUtils.resolveTimeZone(getBrowserTimeZone());

                TimelineRangeEvent te =
                        new TimelineRangeEvent(this, behaviorEvent.getBehavior(),
                                DateUtils.toUtcDate(browserTZ, targetTZ, params.get(clientId + "_startDate")),
                                DateUtils.toUtcDate(browserTZ, targetTZ, params.get(clientId + "_endDate")));
                te.setPhaseId(behaviorEvent.getPhaseId());
                super.queueEvent(te);

                return;
            } else if ("lazyload".equals(eventName)) {
                TimeZone targetTZ = ComponentUtils.resolveTimeZone(getTimeZone());
                TimeZone browserTZ = ComponentUtils.resolveTimeZone(getBrowserTimeZone());

                TimelineLazyLoadEvent te =
                        new TimelineLazyLoadEvent(this, behaviorEvent.getBehavior(),
                                DateUtils.toUtcDate(browserTZ, targetTZ, params.get(clientId + "_startDateFirst")),
                                DateUtils.toUtcDate(browserTZ, targetTZ, params.get(clientId + "_endDateFirst")),
                                DateUtils.toUtcDate(browserTZ, targetTZ, params.get(clientId + "_startDateSecond")),
                                DateUtils.toUtcDate(browserTZ, targetTZ, params.get(clientId + "_endDateSecond")));
                te.setPhaseId(behaviorEvent.getPhaseId());
                super.queueEvent(te);

                return;
            } else if ("drop".equals(eventName)) {
                Object data = null;
                final String dragId = params.get(clientId + "_dragId");
                final String uiDataId = params.get(clientId + "_uiDataId");

                if (dragId != null && uiDataId != null) {
                    // draggable is within a data iteration component
                    UIDataContextCallback contextCallback = new UIDataContextCallback(dragId);
                    context.getViewRoot().invokeOnComponent(context, uiDataId, contextCallback);
                    data = contextCallback.getData();
                }

                // preset start / end date, group, dragId and data object
                TimeZone targetTZ = ComponentUtils.resolveTimeZone(getTimeZone());
                TimeZone browserTZ = ComponentUtils.resolveTimeZone(getBrowserTimeZone());

                TimelineDragDropEvent te =
                        new TimelineDragDropEvent(this, behaviorEvent.getBehavior(),
                                DateUtils.toUtcDate(browserTZ, targetTZ, params.get(clientId + "_startDate")),
                                DateUtils.toUtcDate(browserTZ, targetTZ, params.get(clientId + "_endDate")),
                                getGroup(params.get(clientId + "_group")), dragId, data);
                te.setPhaseId(behaviorEvent.getPhaseId());
                super.queueEvent(te);

                return;
            }
        }

        super.queueEvent(event);
    }

    private String getGroup(String groupParam) {
        List<TimelineGroup> groups = this.getValue().getGroups();
        if (groups == null || groupParam == null) {
            return groupParam;
        }

        int idx = groupParam.indexOf("</span>");
        if (idx > -1) {
            groupParam = groupParam.substring(0, idx);
            int idxGroupOrder = groupParam.indexOf("#");
            if (idxGroupOrder > -1) {
                String groupOrder = groupParam.substring(idxGroupOrder + 1);
                return groups.get(Integer.valueOf(groupOrder)).getId();
            }
        }

        return groupParam;
    }

    private boolean isSelfRequest(FacesContext context) {
        return this.getClientId(context)
                .equals(context.getExternalContext().getRequestParameterMap().get(
                        Constants.RequestParams.PARTIAL_SOURCE_PARAM));
    }

    private String clientId = null;
    private StringBuilder idBuilder = new StringBuilder();
    private UIComponent parentComponent;

    public UIComponent getParentComponent() {
        return parentComponent;
    }

    public void setParentComponent(UIComponent parentComponent) {
        this.parentComponent = parentComponent;
    }
    
    @Override
    public String getClientId(FacesContext context) {
        if(this.clientId != null) {
            return this.clientId;
        }

        String id = getId();
        if(id == null) {
            UniqueIdVendor parentUniqueIdVendor = ComponentTraversalUtils.closestUniqueIdVendor(this);
            
            if(parentUniqueIdVendor == null) {
                UIViewRoot viewRoot = context.getViewRoot();
                
                if(viewRoot != null) {
                    id = viewRoot.createUniqueId();
                }
                else {
                    throw new FacesException("Cannot create clientId for " + this.getClass().getCanonicalName());
                }
            }
            else {
                id = parentUniqueIdVendor.createUniqueId(context, null);
            }
            
            this.setId(id);
        }

        if(this.parentComponent != null && !(this.parentComponent instanceof UIForm)) {
            this.setParent(this.parentComponent);
        }

        UIComponent namingContainer = ComponentTraversalUtils.closestNamingContainer(this);
        if(namingContainer != null) {
            String containerClientId = namingContainer.getContainerClientId(context);
            
            if(containerClientId != null) {                
                this.clientId = this.idBuilder.append(containerClientId).append(UINamingContainer.getSeparatorChar(context)).append(id).toString();
                this.idBuilder.setLength(0);
            }
            else
            {
                this.clientId = id;
            }
        }
        else
        {
            this.clientId = id;
        }

        Renderer renderer = getRenderer(context);
        if(renderer != null) {
            this.clientId = renderer.convertClientId(context, this.clientId);
        }

        return this.clientId;
    }
    
    @Override
    public void setId(String id) {
        super.setId(id);

        //clear
        this.clientId = null;
    }


