import java.util.Arrays;
import java.util.Map;
import java.util.Collection;
import java.util.Collections;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.Constants;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.UnselectEvent;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.FacesEvent;
import javax.faces.convert.Converter;
import javax.faces.component.behavior.Behavior;

    private static final Collection<String> EVENT_NAMES = Collections.unmodifiableCollection(Arrays.asList("blur","change","valueChange","click","dblclick","focus","keydown","keypress","keyup","mousedown","mousemove","mouseout","mouseover","mouseup","select", "itemSelect", "itemUnselect"));
    private static final Collection<String> UNOBSTRUSIVE_EVENT_NAMES = Collections.unmodifiableCollection(Arrays.asList("itemSelect", "itemUnselect"));

    public final static String STYLE_CLASS = "ui-chips ui-widget";
    public final static String CONTAINER_CLASS = "ui-chips-container ui-inputfield ui-state-default ui-corner-all";
    public final static String TOKEN_DISPLAY_CLASS = "ui-chips-token ui-state-active ui-corner-all";
    public final static String TOKEN_LABEL_CLASS = "ui-chips-token-label";
    public final static String TOKEN_CLOSE_ICON_CLASS = "ui-chips-token-icon ui-icon ui-icon-close";
    public final static String TOKEN_INPUT_CLASS = "ui-chips-input-token";

    public String getInputClientId() {
        return this.getClientId(getFacesContext()) + "_input";
    }

    public String getValidatableInputClientId() {
        return this.getInputClientId();
    }

    public void setLabelledBy(String labelledBy) {
        getStateHelper().put("labelledby", labelledBy);
    }

    public String getLabelledBy() {
        return (String) getStateHelper().get("labelledby");
    }
	
    @Override
    public Collection<String> getEventNames() {
        return EVENT_NAMES;
    }

    @Override
    public Collection<String> getUnobstrusiveEventNames() {
        return UNOBSTRUSIVE_EVENT_NAMES;
    }

    @Override
    public void queueEvent(FacesEvent event) {
        FacesContext context = getFacesContext();
        Map<String,String> params = context.getExternalContext().getRequestParameterMap();
        String eventName = params.get(Constants.RequestParams.PARTIAL_BEHAVIOR_EVENT_PARAM);

        if(eventName != null && event instanceof AjaxBehaviorEvent) {
            AjaxBehaviorEvent ajaxBehaviorEvent = (AjaxBehaviorEvent) event;

            if(eventName.equals("itemSelect")) {
                Object selectedItemValue = convertValue(context, params.get(this.getClientId(context) + "_itemSelect"));
                SelectEvent selectEvent = new SelectEvent(this, (Behavior) ajaxBehaviorEvent.getBehavior(), selectedItemValue);
                selectEvent.setPhaseId(ajaxBehaviorEvent.getPhaseId());
                super.queueEvent(selectEvent);
            }
            else if(eventName.equals("itemUnselect")) {
                Object unselectedItemValue = convertValue(context, params.get(this.getClientId(context) + "_itemUnselect"));
                UnselectEvent unselectEvent = new UnselectEvent(this, (Behavior) ajaxBehaviorEvent.getBehavior(), unselectedItemValue);
                unselectEvent.setPhaseId(ajaxBehaviorEvent.getPhaseId());
                super.queueEvent(unselectEvent);
            }
            else {
                //e.g. blur, focus, change
                super.queueEvent(event);
            }
        }
        else {
            //e.g. valueChange
            super.queueEvent(event);
        }
    }
	
	private Object convertValue(FacesContext context, String submittedItemValue) {
        Converter converter = ComponentUtils.getConverter(context, this);

        if(converter == null)
            return submittedItemValue;
        else 
            return converter.getAsObject(context, this, submittedItemValue);
    }