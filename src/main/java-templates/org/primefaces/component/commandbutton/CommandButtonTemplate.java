import org.primefaces.util.HTML;
import java.util.logging.Logger;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.FacesEvent;
import java.util.Collections;
import java.util.Collection;
import java.util.Arrays;
import java.util.Map;
import org.primefaces.event.data.PageEvent;
import org.primefaces.util.Constants;
import org.primefaces.event.SelectEvent;

    private static final Collection<String> EVENT_NAMES = Collections.unmodifiableCollection(Arrays.asList("click","dialogReturn"));
        
    private final static Logger logger = Logger.getLogger(CommandButton.class.getName());

    @Override
    public Collection<String> getEventNames() {
        return EVENT_NAMES;
    }

    @Override
    public String getDefaultEventName() {
        return "click";
    }
    
    @Override
    public void queueEvent(FacesEvent event) {
        FacesContext context = getFacesContext();

        if(event instanceof AjaxBehaviorEvent) {
            Map<String,String> params = context.getExternalContext().getRequestParameterMap();
            String eventName = params.get(Constants.PARTIAL_BEHAVIOR_EVENT_PARAM);

            if(eventName.equals("dialogReturn")) {
                AjaxBehaviorEvent behaviorEvent = (AjaxBehaviorEvent) event;
                Map<String,Object> session = context.getExternalContext().getSessionMap();
                String dcid = params.get(this.getClientId(context) + "_dcid");
                Object selectedValue = session.get(dcid);
                session.remove(dcid);
        
                event = new SelectEvent(this, behaviorEvent.getBehavior(), selectedValue);
                super.queueEvent(event);
            }
            else if(eventName.equals("click")) {
                super.queueEvent(event);
            }
        } 
        else {
            super.queueEvent(event);
        }
    }
            
    public String resolveIcon() {
        String icon = getIcon();
    
        if(icon == null) {
            icon = getImage();
            
            if(icon != null)
                logger.info("image attribute is deprecated to define an icon, use icon attribute instead.");
        }
    
        return icon;
    }

    public String resolveStyleClass() {
        String icon = resolveIcon();
        Object value = getValue();
        String styleClass = ""; 
    
        if(value != null && icon == null) {
            styleClass = HTML.BUTTON_TEXT_ONLY_BUTTON_CLASS;
        }
        else if(value != null && icon != null) {
            styleClass = getIconPos().equals("left") ? HTML.BUTTON_TEXT_ICON_LEFT_BUTTON_CLASS : HTML.BUTTON_TEXT_ICON_RIGHT_BUTTON_CLASS;
        }
        else if(value == null && icon != null) {
            styleClass = HTML.BUTTON_ICON_ONLY_BUTTON_CLASS;
        }
    
        if(isDisabled()) {
            styleClass = styleClass + " ui-state-disabled";
        } 
    
        String userStyleClass = getStyleClass();
        if(userStyleClass != null) {
            styleClass = styleClass + " " + userStyleClass;
        }
    
        return styleClass;
    }
    
    public boolean isPartialSubmitSet() {
        return (getStateHelper().get(PropertyKeys.partialSubmit) != null) || (this.getValueExpression("partialSubmit") != null);
    }