
    public final static String CONTAINER_CLASS = "ui-fileupload ui-widget";
    public final static String BROWSER_CLASS = "ui-fileupload-browser ui-state-default ui-corner-all ui-helper-hidden";
    public final static String TABLE_CLASS = "ui-fileupload-files";
    public final static String CONTROLS_CLASS = "ui-fileupload-controls";
    public final static String UPLOAD_BUTTON_CLASS = "ui-fileupload-upload-button";
    public final static String CANCEL_BUTTON_CLASS = "ui-fileupload-cancel-button";

    public void broadcast(javax.faces.event.FacesEvent event) throws javax.faces.event.AbortProcessingException {
		super.broadcast(event);
		
		FacesContext facesContext = FacesContext.getCurrentInstance();
		MethodExpression me = getFileUploadListener();
		
		if (me != null && event instanceof org.primefaces.event.FileUploadEvent) {
			me.invoke(facesContext.getELContext(), new Object[] {event});
		}
	}