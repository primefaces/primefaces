import javax.faces.component.UIComponent;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import org.primefaces.model.Visibility;
import org.primefaces.event.ToggleEvent;
import org.primefaces.util.Constants;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.FacesEvent;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import javax.faces.event.PhaseId;

    public static final String FIELDSET_CLASS = "ui-fieldset ui-widget ui-widget-content ui-corner-all";
    public static final String TOGGLEABLE_FIELDSET_CLASS = FIELDSET_CLASS + " ui-fieldset-toggleable";
    public static final String CONTENT_CLASS = "ui-fieldset-content";
    public static final String LEGEND_CLASS = "ui-fieldset-legend ui-corner-all ui-state-default";
    public static final String TOGGLER_MINUS_CLASS = "ui-fieldset-toggler ui-icon ui-icon-minusthick";
    public static final String TOGGLER_PLUS_CLASS = "ui-fieldset-toggler ui-icon ui-icon-plusthick";

    private static final Collection<String> EVENT_NAMES = Collections.unmodifiableCollection(Arrays.asList("toggle"));

    @Override
    public void queueEvent(FacesEvent event) {
        FacesContext context = FacesContext.getCurrentInstance();
        
        if(isRequestSource(context)) {
            Map<String,String> params = context.getExternalContext().getRequestParameterMap();
            String eventName = params.get(Constants.PARTIAL_BEHAVIOR_EVENT_PARAM);
            String clientId = this.getClientId(context);
            AjaxBehaviorEvent behaviorEvent = (AjaxBehaviorEvent) event;
            
            if(eventName.equals("toggle")) {
                Visibility visibility = this.isCollapsed() ? Visibility.HIDDEN : Visibility.VISIBLE;

                super.queueEvent(new ToggleEvent(this, behaviorEvent.getBehavior(), visibility));
            }
        }
        else {
            super.queueEvent(event);
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