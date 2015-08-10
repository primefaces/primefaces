import org.primefaces.component.column.Column;
import org.primefaces.config.ConfigContainer;
import org.primefaces.context.RequestContext;
import java.util.Collection;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import javax.faces.event.FacesEvent;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.component.UIComponent;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.MessageFactory;
import org.primefaces.util.Constants;
import org.primefaces.event.SelectEvent;
import java.util.Map;

    public final static String STYLE_CLASS = "ui-selectonemenu ui-widget ui-state-default ui-corner-all";
    public final static String LABEL_CLASS = "ui-selectonemenu-label ui-inputfield ui-corner-all";
    public final static String TRIGGER_CLASS = "ui-selectonemenu-trigger ui-state-default ui-corner-right";
    public final static String PANEL_CLASS = "ui-selectonemenu-panel ui-widget ui-widget-content ui-corner-all ui-helper-hidden ui-shadow";
    public final static String ITEMS_WRAPPER_CLASS = "ui-selectonemenu-items-wrapper";
    public final static String LIST_CLASS = "ui-selectonemenu-items ui-selectonemenu-list ui-widget-content ui-widget ui-corner-all ui-helper-reset";
    public final static String TABLE_CLASS = "ui-selectonemenu-items ui-selectonemenu-table ui-widget-content ui-widget ui-corner-all ui-helper-reset";
    public final static String ITEM_GROUP_CLASS = "ui-selectonemenu-item-group ui-corner-all";
    public final static String ITEM_CLASS = "ui-selectonemenu-item ui-selectonemenu-list-item ui-corner-all";
    public final static String ROW_CLASS = "ui-selectonemenu-item ui-selectonemenu-row ui-widget-content";
    public final static String FILTER_CONTAINER_CLASS = "ui-selectonemenu-filter-container";
    public final static String FILTER_CLASS = "ui-selectonemenu-filter ui-inputfield ui-inputtext ui-widget ui-state-default ui-corner-all";
    public final static String FILTER_ICON_CLASS = "ui-icon ui-icon-search";

    private static final Collection<String> EVENT_NAMES = Collections.unmodifiableCollection(Arrays.asList("itemSelect","blur","change","valueChange","click","dblclick","focus","keydown","keypress","keyup","mousedown","mousemove","mouseout","mouseover","mouseup","select"));

    public Collection<String> getEventNames() {
        return EVENT_NAMES;    
    }


    public String getDefaultEventName() {
        return "valueChange";    
    }

    public List<Column> getColums() {
        List<Column> columns = new ArrayList<Column>();
        
        for(UIComponent kid : this.getChildren()) {
            if(kid instanceof Column)
                columns.add((Column) kid);
        }

        return columns;
    }

    @Override
    public void queueEvent(FacesEvent event) {
        if(event instanceof AjaxBehaviorEvent) {
            FacesContext context = getFacesContext();
            AjaxBehaviorEvent behaviorEvent = (AjaxBehaviorEvent) event;
            Map<String,String> params = context.getExternalContext().getRequestParameterMap();
            String eventName = params.get(Constants.RequestParams.PARTIAL_BEHAVIOR_EVENT_PARAM);
            
            if("itemSelect".equals(eventName)) {
                Object item = context.getRenderKit().getRenderer("javax.faces.SelectOne", "javax.faces.Menu").getConvertedValue(context, this, this.getSubmittedValue());
                SelectEvent selectEvent = new SelectEvent(this, behaviorEvent.getBehavior(), item);
                selectEvent.setPhaseId(event.getPhaseId());
                super.queueEvent(selectEvent);
            }
            else {
                super.queueEvent(event);
            }
        }
        else {
            super.queueEvent(event);
        }
    }

    @Override
    protected void validateValue(FacesContext context, Object value) {
        if(this.isEditable()) {
            
            //required field validation
            if(isValid() && isRequired() && isEmpty(value)) {
                String requiredMessageStr = getRequiredMessage();
                FacesMessage message;
                if(null != requiredMessageStr) {
                    message = new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                               requiredMessageStr,
                                               requiredMessageStr);
                } else {                    
                    message = MessageFactory.getMessage(REQUIRED_MESSAGE_ID, FacesMessage.SEVERITY_ERROR, new Object[]{MessageFactory.getLabel(context, this)});
                }
                context.addMessage(getClientId(context), message);
                setValid(false);
            }

            ConfigContainer config = RequestContext.getCurrentInstance().getApplicationContext().getConfig();
            
            //other validators
            if(isValid() && (!isEmpty(value) || config.isValidateEmptyFields())) {
                Validator[] validators = getValidators();
                    
                for(Validator validator : validators) {
                    try {
                        validator.validate(context, this, value);
                    }
                    catch(ValidatorException ve) {
                        setValid(false);
                        FacesMessage message;
                        String validatorMessageString = getValidatorMessage();

                        if(null != validatorMessageString) {
                            message =new FacesMessage(FacesMessage.SEVERITY_ERROR, validatorMessageString, validatorMessageString);
                        } 
                        else {
                            Collection<FacesMessage> messages = ve.getFacesMessages();
                            
                            if(null != messages) {
                                message = null;
                                String cid = getClientId(context);
                                for(FacesMessage m : messages) {
                                    context.addMessage(cid, m);
                                }
                            } 
                            else {
                                message = ve.getFacesMessage();
                            }
                        }
                        
                        if(message != null) {
                            context.addMessage(getClientId(context), message);
                        }
                    }
                }
            }
        }
        else {
            super.validateValue(context, value);
        }
    }

    public String getInputClientId() {
        return this.getClientId(getFacesContext()) + "_focus";
    }

    public String getValidatableInputClientId() {
        return this.getClientId(getFacesContext()) + "_input";
    }

    public void setLabelledBy(String labelledBy) {
        getStateHelper().put("labelledby", labelledBy);
    }
    public String getLabelledBy() {
        return (String) getStateHelper().get("labelledby");
    }

    


    