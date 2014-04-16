import org.primefaces.util.HTML;
import java.util.logging.Logger;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.FacesEvent;
import java.util.Collections;
import java.util.Collection;
import java.util.Arrays;
import java.util.Map;
import org.primefaces.component.button.Button;
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
            String eventName = params.get(Constants.RequestParams.PARTIAL_BEHAVIOR_EVENT_PARAM);

            if(eventName.equals("dialogReturn")) {
                AjaxBehaviorEvent behaviorEvent = (AjaxBehaviorEvent) event;
                Map<String,Object> session = context.getExternalContext().getSessionMap();
                String dcid = params.get(this.getClientId(context) + "_pfdlgcid");
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
    
    public String resolveMobileStyleClass() {
        String icon = getIcon();
        String iconPos = getIconPos();
        Object value = getValue();
        String styleClass = "ui-btn ui-shadow ui-corner-all";
            
        if(value != null && icon != null) {
            styleClass = styleClass + " " + icon + " ui-btn-icon-" + iconPos;
        } else if(value == null && icon != null) {
            styleClass = styleClass + " " + icon + " ui-btn-icon-notext";
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
    
    public boolean isResetValuesSet() {
        return (getStateHelper().get(PropertyKeys.resetValues) != null) || (this.getValueExpression("resetValues") != null);
    }
    
    private String confirmationScript;
    
    public String getConfirmationScript() {
        return this.confirmationScript;
    }
    public void setConfirmationScript(String confirmationScript) {
        this.confirmationScript = confirmationScript;
    }
    public boolean requiresConfirmation() {
        return this.confirmationScript != null;
    }
    
    public boolean isAjaxified() {
        return !getType().equals("reset") && !getType().equals("button") && isAjax();
    }