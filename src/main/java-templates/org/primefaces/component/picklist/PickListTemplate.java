import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import org.primefaces.model.DualListModel;
import org.primefaces.util.MessageFactory;
import org.primefaces.component.picklist.PickList;
import org.primefaces.event.TransferEvent;
import org.primefaces.util.Constants;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.event.FacesEvent;
import javax.faces.event.AjaxBehaviorEvent;
import java.util.HashMap;
import java.util.Iterator;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.UnselectEvent;
import org.primefaces.event.ReorderEvent;

    public static final String CONTAINER_CLASS = "ui-picklist ui-widget ui-helper-clearfix";
    public static final String LIST_CLASS = "ui-widget-content ui-picklist-list";
    public static final String LIST_WRAPPER_CLASS = "ui-picklist-list-wrapper";
    public static final String SOURCE_CLASS = LIST_CLASS + " ui-picklist-source";
    public static final String TARGET_CLASS = LIST_CLASS + " ui-picklist-target";
    public static final String BUTTONS_CLASS = "ui-picklist-buttons";
    public static final String BUTTONS_CELL_CLASS = "ui-picklist-buttons-cell";
    public static final String SOURCE_CONTROLS = "ui-picklist-source-controls ui-picklist-buttons";
    public static final String TARGET_CONTROLS = "ui-picklist-target-controls ui-picklist-buttons";
    public static final String ITEM_CLASS = "ui-picklist-item ui-corner-all";
    public static final String ITEM_DISABLED_CLASS = "ui-state-disabled";
    public static final String CAPTION_CLASS = "ui-picklist-caption ui-widget-header ui-corner-tl ui-corner-tr";
    public static final String ADD_BUTTON_CLASS = "ui-picklist-button-add";
    public static final String ADD_ALL_BUTTON_CLASS = "ui-picklist-button-add-all";
    public static final String REMOVE_BUTTON_CLASS = "ui-picklist-button-remove";
    public static final String REMOVE_ALL_BUTTON_CLASS = "ui-picklist-button-remove-all";
    public static final String ADD_BUTTON_ICON_CLASS = "ui-icon ui-icon-arrow-1-e";
    public static final String ADD_ALL_BUTTON_ICON_CLASS = "ui-icon ui-icon-arrowstop-1-e";
    public static final String REMOVE_BUTTON_ICON_CLASS = "ui-icon ui-icon-arrow-1-w";
    public static final String REMOVE_ALL_BUTTON_ICON_CLASS = "ui-icon ui-icon-arrowstop-1-w";
    public static final String VERTICAL_ADD_BUTTON_ICON_CLASS = "ui-icon ui-icon-arrow-1-s";
    public static final String VERTICAL_ADD_ALL_BUTTON_ICON_CLASS = "ui-icon ui-icon-arrowstop-1-s";
    public static final String VERTICAL_REMOVE_BUTTON_ICON_CLASS = "ui-icon ui-icon-arrow-1-n";
    public static final String VERTICAL_REMOVE_ALL_BUTTON_ICON_CLASS = "ui-icon ui-icon-arrowstop-1-n";
    public static final String MOVE_UP_BUTTON_CLASS = "ui-picklist-button-move-up";
    public static final String MOVE_DOWN_BUTTON_CLASS = "ui-picklist-button-move-down";
    public static final String MOVE_TOP_BUTTON_CLASS = "ui-picklist-button-move-top";
    public static final String MOVE_BOTTOM_BUTTON_CLASS = "ui-picklist-button-move-bottom";
    public static final String MOVE_UP_BUTTON_ICON_CLASS = "ui-icon ui-icon-arrow-1-n";
    public static final String MOVE_DOWN_BUTTON_ICON_CLASS = "ui-icon ui-icon-arrow-1-s";
    public static final String MOVE_TOP_BUTTON_ICON_CLASS = "ui-icon ui-icon-arrowstop-1-n";
    public static final String MOVE_BOTTOM_BUTTON_ICON_CLASS = "ui-icon ui-icon-arrowstop-1-s";
    public static final String FILTER_CLASS = "ui-picklist-filter ui-inputfield ui-inputtext ui-widget ui-state-default ui-corner-all";
    public static final String FILTER_CONTAINER = "ui-picklist-filter-container";

    private static final Collection<String> EVENT_NAMES = Collections.unmodifiableCollection(Arrays.asList("transfer", "select", "unselect", "reorder"));

    private Map<String,AjaxBehaviorEvent> customEvents = new HashMap<String,AjaxBehaviorEvent>();

    @Override
    public Collection<String> getEventNames() {
        return EVENT_NAMES;
    }

	protected void validateValue(FacesContext facesContext, Object newValue) {
		super.validateValue(facesContext, newValue);
		
		DualListModel model = (DualListModel) newValue;
		if(isRequired() && model.getTarget().isEmpty()) {
			String requiredMessage = getRequiredMessage();
			FacesMessage message = null;
			
			if(requiredMessage != null) {
				message = new FacesMessage(FacesMessage.SEVERITY_ERROR, requiredMessage, requiredMessage);
            }
            else {
                String label = this.getLabel();
                if(label == null) {
                    label = this.getClientId(facesContext);
                }

	        	message = MessageFactory.getMessage(REQUIRED_MESSAGE_ID, FacesMessage.SEVERITY_ERROR, new Object[]{label});

            }

			facesContext.addMessage(getClientId(facesContext), message);
	        setValid(false);
		}
	}

 @Override
    public void validate(FacesContext context) {
        super.validate(context);
        if (isValid() && customEvents != null) {
            for (Iterator<String> customEventIter = customEvents.keySet().iterator(); customEventIter.hasNext();) {
                String eventName = customEventIter.next();
                AjaxBehaviorEvent behaviorEvent = customEvents.get(eventName);
                Map<String, String> params = context.getExternalContext().getRequestParameterMap();
                String clientId = this.getClientId(context);
                DualListModel<?> list = (DualListModel<?>) this.getValue();
                FacesEvent wrapperEvent = null;

                if (eventName.equals("select")) {
                    String listName = params.get(clientId + "_listName");
                    int itemIndex = Integer.parseInt(params.get(clientId + "_itemIndex"));

                    if (listName.equals("target")) {
                        wrapperEvent = new SelectEvent(this, behaviorEvent.getBehavior(), list.getTarget().get(itemIndex));
                    } 
                    else {
                        wrapperEvent = new SelectEvent(this, behaviorEvent.getBehavior(), list.getSource().get(itemIndex));
                    }
                } 
                else if (eventName.equals("unselect")) {
                    String listName = params.get(clientId + "_listName");
                    int itemIndex = Integer.parseInt(params.get(clientId + "_itemIndex"));

                    if (listName.equals("target")) {
                        wrapperEvent = new UnselectEvent(this, behaviorEvent.getBehavior(), list.getTarget().get(itemIndex));
                    } 
                    else {
                        wrapperEvent = new UnselectEvent(this, behaviorEvent.getBehavior(), list.getSource().get(itemIndex));
                    }
                } 
                else if (eventName.equals("reorder")) {
                    wrapperEvent = behaviorEvent;
                }

                wrapperEvent.setPhaseId(behaviorEvent.getPhaseId());

                super.queueEvent(wrapperEvent);
            }
        }
    }

    @Override
    public void queueEvent(FacesEvent event) {
        FacesContext context = getFacesContext();

        if(isRequestSource(context) && event instanceof AjaxBehaviorEvent) {
            Map<String,String[]> paramValues = context.getExternalContext().getRequestParameterValuesMap();
            Map<String,String> params = context.getExternalContext().getRequestParameterMap();

            String eventName = params.get(Constants.RequestParams.PARTIAL_BEHAVIOR_EVENT_PARAM);
            String clientId = this.getClientId(context);

            AjaxBehaviorEvent behaviorEvent = (AjaxBehaviorEvent) event;

            if(eventName.equals("transfer")) {
                String[] items = paramValues.get(clientId + "_transferred");
                boolean isAdd = Boolean.valueOf(params.get(clientId + "_add"));
                List transferredItems = new ArrayList();
                this.populateModel(context, items, transferredItems);
                TransferEvent transferEvent = new TransferEvent(this, behaviorEvent.getBehavior(), transferredItems, isAdd);
                transferEvent.setPhaseId(event.getPhaseId());

                super.queueEvent(transferEvent);
            }
            else if (eventName.equals("select")) {
                customEvents.put(eventName, (AjaxBehaviorEvent) event);
            }
            else if (eventName.equals("unselect")) {
                customEvents.put(eventName, (AjaxBehaviorEvent) event);
            } 
            else if (eventName.equals("reorder")) {
                customEvents.put(eventName, (AjaxBehaviorEvent) event);
            }
        }
        else {
            super.queueEvent(event);
        }
    }

    private boolean isRequestSource(FacesContext context) {
        return this.getClientId(context).equals(context.getExternalContext().getRequestParameterMap().get(Constants.RequestParams.PARTIAL_SOURCE_PARAM));
    }

    @SuppressWarnings("unchecked")
	public void populateModel(FacesContext context, String[] values, List model) {
		Converter converter = this.getConverter();

		if (values != null) {
	        for(String item : values) {            
				if(item == null || item.trim().equals(""))
					continue;
				                    
				Object convertedValue = converter != null ? converter.getAsObject(context, this, item) : item;
				
				if(convertedValue != null) {
					model.add(convertedValue);
	            }
			}
		}
	}