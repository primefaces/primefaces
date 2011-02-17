
    public final static String CONTAINER_CLASS = "ui-fileupload";

    public void broadcast(javax.faces.event.FacesEvent event) throws javax.faces.event.AbortProcessingException {
		super.broadcast(event);
		
		FacesContext facesContext = FacesContext.getCurrentInstance();
		MethodExpression me = getFileUploadListener();
		
		if (me != null && event instanceof org.primefaces.event.FileUploadEvent) {
			me.invoke(facesContext.getELContext(), new Object[] {event});
		}
	}