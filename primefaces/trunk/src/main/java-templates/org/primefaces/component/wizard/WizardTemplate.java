import org.primefaces.component.tabview.Tab;
import org.primefaces.event.FlowEvent;
import javax.faces.component.UIComponent;

	public final static String BACK_BUTTON_CLASS = "ui-wizard-nav-back";
	public final static String NEXT_BUTTON_CLASS = "ui-wizard-nav-next";
	
	private Tab current;

	public void processDecodes(FacesContext context) {
        //If flow goes back, skip to rendering
		if(isBackRequest(context)) {
            context.renderResponse();
		}
        else {
			getStepToProcess().processDecodes(context);
		}
    }
	
	public void processValidators(FacesContext context) {
		if(isWizardRequest(context)) {
			current.processValidators(context);
		}
        else {
			super.processValidators(context);
		}
    }
	
	public void processUpdates(FacesContext context) {
		if(isWizardRequest(context)) {
			current.processUpdates(context);
		} 
        else {
			super.processUpdates(context);
		}
	}
	
	public Tab getStepToProcess() {
		if(current == null) {
			String currentStepId = getStep();
			
			for(UIComponent child : getChildren()) {
				if(child.getId().equals(currentStepId)) {
					current = (Tab) child;
					
					break;
				}
			}
		}
		
		return current;
	}
	
	public boolean isWizardRequest(FacesContext context) {
		return context.getExternalContext().getRequestParameterMap().containsKey(getClientId(context) + "_wizardRequest");
	}
	
	public boolean isBackRequest(FacesContext context) {
		return isWizardRequest(context) && context.getExternalContext().getRequestParameterMap().containsKey(getClientId(context) + "_backRequest");
	}