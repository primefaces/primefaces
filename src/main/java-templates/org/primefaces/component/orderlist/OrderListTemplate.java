import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.UnselectEvent;
import org.primefaces.event.ReorderEvent;
import org.primefaces.util.Constants;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.FacesEvent;
import javax.faces.event.PhaseId;

    public static final String CONTAINER_CLASS = "ui-orderlist ui-grid ui-widget";
    public static final String LIST_CLASS = "ui-widget-content ui-orderlist-list";
    public static final String CONTROLS_CLASS = "ui-orderlist-controls ui-grid-col-2";
    public static final String CAPTION_CLASS = "ui-orderlist-caption ui-widget-header ui-corner-top";
    public static final String ITEM_CLASS = "ui-orderlist-item ui-corner-all";
    public static final String MOVE_UP_BUTTON_CLASS = "ui-orderlist-button-move-up";
    public static final String MOVE_DOWN_BUTTON_CLASS = "ui-orderlist-button-move-down";
    public static final String MOVE_TOP_BUTTON_CLASS = "ui-orderlist-button-move-top";
    public static final String MOVE_BOTTOM_BUTTON_CLASS = "ui-orderlist-button-move-bottom";
    public static final String MOVE_UP_BUTTON_ICON_CLASS = "ui-icon ui-icon-arrow-1-n";
    public static final String MOVE_DOWN_BUTTON_ICON_CLASS = "ui-icon ui-icon-arrow-1-s";
    public static final String MOVE_TOP_BUTTON_ICON_CLASS = "ui-icon ui-icon-arrowstop-1-n";
    public static final String MOVE_BOTTOM_BUTTON_ICON_CLASS = "ui-icon ui-icon-arrowstop-1-s";

    private static final Collection<String> EVENT_NAMES = Collections.unmodifiableCollection(Arrays.asList("select","unselect","reorder"));

    private Map<String,AjaxBehaviorEvent> customEvents = new HashMap<String,AjaxBehaviorEvent>();
    
    @Override
    public Collection<String> getEventNames() {
        return EVENT_NAMES;
    }

    private Map<String,AjaxBehaviorEvent> getCustomEvents() {
        if(customEvents == null) {
            customEvents = new HashMap<String,AjaxBehaviorEvent>();
        }

        return customEvents;
    }

    @Override
    public void queueEvent(FacesEvent event) {
        FacesContext context = getFacesContext();

        if(isRequestSource(context) && (event instanceof AjaxBehaviorEvent)) {
            String eventName = context.getExternalContext().getRequestParameterMap().get(Constants.RequestParams.PARTIAL_BEHAVIOR_EVENT_PARAM);
            customEvents.put(eventName, (AjaxBehaviorEvent) event);
        } 
        else {
            super.queueEvent(event);
        }
    }

    @Override
    public void validate(FacesContext context) {
        super.validate(context);

        if(isValid() && customEvents != null) {
            for(Iterator<String> customEventIter = customEvents.keySet().iterator(); customEventIter.hasNext();) {
                String eventName = customEventIter.next();
                AjaxBehaviorEvent behaviorEvent = customEvents.get(eventName);
                Map<String,String> params = context.getExternalContext().getRequestParameterMap();
                String clientId = this.getClientId(context);
                List<?> list = (List) this.getValue();
                FacesEvent wrapperEvent = null;

                if(eventName.equals("select")) {
                    int itemIndex = Integer.parseInt(params.get(clientId + "_itemIndex"));
                    boolean metaKey = Boolean.valueOf(params.get(clientId + "_metaKey"));
                    boolean ctrlKey = Boolean.valueOf(params.get(clientId + "_ctrlKey"));
                    wrapperEvent = new SelectEvent(this, behaviorEvent.getBehavior(), list.get(itemIndex), metaKey, ctrlKey);
                }
                else if(eventName.equals("unselect")) {
                    int itemIndex = Integer.parseInt(params.get(clientId + "_itemIndex"));
                    wrapperEvent = new UnselectEvent(this, behaviorEvent.getBehavior(), list.get(itemIndex));
                }
                else if(eventName.equals("reorder")) {
                    wrapperEvent = behaviorEvent;
                }

                wrapperEvent.setPhaseId(behaviorEvent.getPhaseId());
                
                super.queueEvent(wrapperEvent);
            }
        }
    }

    private boolean isRequestSource(FacesContext context) {
        return this.getClientId(context).equals(context.getExternalContext().getRequestParameterMap().get(Constants.RequestParams.PARTIAL_SOURCE_PARAM));
    }