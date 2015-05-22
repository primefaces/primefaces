import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import javax.faces.event.FacesEvent;
import javax.faces.event.AjaxBehaviorEvent;
import org.primefaces.event.ToggleSelectEvent;
import org.primefaces.util.Constants;

    public final static String STYLE_CLASS = "ui-selectcheckboxmenu ui-widget ui-state-default ui-corner-all";
    public final static String LABEL_CONTAINER_CLASS = "ui-selectcheckboxmenu-label-container";
    public final static String LABEL_CLASS = "ui-selectcheckboxmenu-label ui-corner-all";
    public final static String TRIGGER_CLASS = "ui-selectcheckboxmenu-trigger ui-state-default ui-corner-right";
    public final static String PANEL_CLASS = "ui-selectcheckboxmenu-panel ui-widget-content ui-corner-all ui-helper-hidden";
    public final static String LIST_CLASS = "ui-selectcheckboxmenu-items ui-selectcheckboxmenu-list ui-widget-content ui-widget ui-corner-all ui-helper-reset";
    public final static String ITEM_CLASS = "ui-selectcheckboxmenu-item ui-selectcheckboxmenu-list-item ui-corner-all ui-helper-clearfix";

    private final static String DEFAULT_EVENT = "change";

    private static final Collection<String> EVENT_NAMES = Collections.unmodifiableCollection(Arrays.asList(DEFAULT_EVENT, "toggleSelect"));

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
        String eventName = context.getExternalContext().getRequestParameterMap().get(Constants.RequestParams.PARTIAL_BEHAVIOR_EVENT_PARAM);
        
        if(event instanceof AjaxBehaviorEvent && eventName.equals("toggleSelect")) {
            Map<String,String> params = context.getExternalContext().getRequestParameterMap();
            String clientId = this.getClientId(context);
            boolean checked = Boolean.valueOf(params.get(clientId + "_checked"));
            ToggleSelectEvent toggleSelectEvent = new ToggleSelectEvent(this, ((AjaxBehaviorEvent) event).getBehavior(), checked);
            toggleSelectEvent.setPhaseId(event.getPhaseId());

            super.queueEvent(toggleSelectEvent);
        }
        else {
            super.queueEvent(event);
        }
    }
