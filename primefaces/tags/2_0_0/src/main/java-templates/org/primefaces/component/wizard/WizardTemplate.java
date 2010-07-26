import org.primefaces.component.tabview.Tab;

	public void processDecodes(FacesContext facesContext) {
		String stepToGoParam = facesContext.getExternalContext().getRequestParameterMap().get("stepToGo");
		String currentStepParam = facesContext.getExternalContext().getRequestParameterMap().get("currentStep");
		
		if(stepToGoParam != null && currentStepParam != null) {
			int stepToGo = Integer.valueOf(facesContext.getExternalContext().getRequestParameterMap().get("stepToGo")).intValue();
			int currentStep = Integer.valueOf(facesContext.getExternalContext().getRequestParameterMap().get("currentStep")).intValue();
			
			if(stepToGo < currentStep) {
				facesContext.renderResponse();
			} else if(stepToGo > currentStep) {
				Tab tabToProcess = (Tab) getChildren().get(currentStep);
				
				tabToProcess.processDecodes(facesContext);
			}
		} else {
			super.processDecodes(facesContext);
		}
    }