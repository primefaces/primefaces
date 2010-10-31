import javax.faces.component.UINamingContainer;

import org.primefaces.event.FileUploadEvent;

	private String inputFileId;

	public void broadcast(javax.faces.event.FacesEvent event) throws javax.faces.event.AbortProcessingException {
		super.broadcast(event);
		
		FacesContext facesContext = FacesContext.getCurrentInstance();
		MethodExpression me = getFileUploadListener();
		
		if (me != null) {
			me.invoke(facesContext.getELContext(), new Object[] {event});
		}
	}
	
	String getInputFileId(FacesContext facesContext) {
		if(inputFileId == null) {
			inputFileId = this.getClientId(facesContext).replaceAll(String.valueOf(UINamingContainer.getSeparatorChar(facesContext)), "_") + "_file";
		}
		
		return inputFileId;
	}