import org.primefaces.model.DualListModel;
import org.primefaces.util.MessageFactory;
import javax.faces.application.FacesMessage;

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