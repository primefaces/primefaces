import org.primefaces.component.tabview.Tab;
import org.primefaces.event.FlowEvent;

	public final static String BACK_BUTTON_CLASS = "ui-wizard-nav-back";
	public final static String NEXT_BUTTON_CLASS = "ui-wizard-nav-next";
	
	private Tab tabToProcess;

	public void processDecodes(FacesContext facesContext) {
		if(isWizardRequest(facesContext)) {
                        String currentStep = facesContext.getExternalContext().getRequestParameterMap().get(getClientId(facesContext) + "_currentStep");
                        setStep(currentStep);

			//If flow goes back, skip to rendering
			if(isBackRequest(facesContext)) {
				facesContext.renderResponse();
			} else {
				getStepToProcess(facesContext).processDecodes(facesContext);
			}
			
		} else {
			super.processDecodes(facesContext);
		}
    }
	
	public void processValidators(FacesContext facesContext) {
		if(isWizardRequest(facesContext)) {
			getStepToProcess(facesContext).processValidators(facesContext);
		} else {
			super.processValidators(facesContext);
		}
    }
	
	public void processUpdates(FacesContext facesContext) {
		if(isWizardRequest(facesContext)) {
			getStepToProcess(facesContext).processUpdates(facesContext);
		} else {
			super.processUpdates(facesContext);
		}
	}
	
	public Tab getStepToProcess(FacesContext facesContext) {
		if(tabToProcess == null) {
			String currentStep = getStep();
			
			for(javax.faces.component.UIComponent child : getChildren()) {
				if(child.getId().equals(currentStep)) {
					tabToProcess = (Tab) child;
					
					break;
				}
			}
		}
		
		return tabToProcess;
	}
	
	public boolean isWizardRequest(FacesContext facesContext) {
		return facesContext.getExternalContext().getRequestParameterMap().containsKey(getClientId(facesContext) + "_wizardRequest");
	}
	
	public boolean isBackRequest(FacesContext facesContext) {
		return facesContext.getExternalContext().getRequestParameterMap().containsKey(getClientId(facesContext) + "_backRequest");
	}