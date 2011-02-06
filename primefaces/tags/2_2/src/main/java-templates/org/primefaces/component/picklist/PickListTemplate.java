import org.primefaces.model.DualListModel;
import org.primefaces.util.MessageFactory;
import javax.faces.application.FacesMessage;

    public static final String CONTAINER_CLASS = "ui-picklist ui-widget";
    public static final String LIST_CLASS = "ui-widget-content ui-picklist-list";
    public static final String SOURCE_CLASS = LIST_CLASS + " ui-picklist-source";
    public static final String TARGET_CLASS = LIST_CLASS + " ui-picklist-target";
    public static final String SOURCE_CONTROLS = "ui-picklist-source-controls";
    public static final String TARGET_CONTROLS = "ui-picklist-target-controls";
    public static final String ITEM_CLASS = "ui-state-default ui-picklist-item ui-corner-all";
    public static final String BUTTON_CLASS = "ui-picklist-button";
    public static final String ADD_BUTTON_CLASS = BUTTON_CLASS + " ui-picklist-button-add";
    public static final String ADD_ALL_BUTTON_CLASS = BUTTON_CLASS + " ui-picklist-button-add-all";
    public static final String REMOVE_BUTTON_CLASS = BUTTON_CLASS + " ui-picklist-button-remove";
    public static final String REMOVE_ALL_BUTTON_CLASS = BUTTON_CLASS + " ui-picklist-button-remove-all";
    public static final String MOVE_UP_BUTTON_CLASS = BUTTON_CLASS + " ui-picklist-button-move-up";
    public static final String MOVE_DOWN_BUTTON_CLASS = BUTTON_CLASS + " ui-picklist-button-move-down";
    public static final String MOVE_TOP_BUTTON_CLASS = BUTTON_CLASS + " ui-picklist-button-move-top";
    public static final String MOVE_BOTTOM_BUTTON_CLASS = BUTTON_CLASS + " ui-picklist-button-move-bottom";
    public static final String CAPTION_CLASS = "ui-picklist-caption ui-widget-header ui-corner-tl ui-corner-tr";

	protected void validateValue(FacesContext facesContext, Object newValue) {
		super.validateValue(facesContext, newValue);
		
		DualListModel model = (DualListModel) newValue;
		if(isRequired() && model.getTarget().isEmpty()) {
			String requiredMessage = getRequiredMessage();
			FacesMessage message = null;
			
			if(requiredMessage != null)
				message = new FacesMessage(FacesMessage.SEVERITY_ERROR, requiredMessage, requiredMessage);
	        else
	        	message = MessageFactory.getMessage(REQUIRED_MESSAGE_ID, FacesMessage.SEVERITY_ERROR, new Object[]{getClientId(facesContext)});

			facesContext.addMessage(getClientId(facesContext), message);
	        setValid(false);
		}
	}