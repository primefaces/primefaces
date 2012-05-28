
    public final static String CONTAINER_CLASS = "ui-fileupload ui-widget";
    public final static String BUTTON_BAR_CLASS = "fileupload-buttonbar ui-widget-header ui-corner-top";
    public final static String CONTENT_CLASS = "fileupload-content ui-widget-content ui-corner-bottom";
    public final static String FILES_CLASS = "files";
    public final static String CHOOSE_BUTTON_CLASS = "fileinput-button";
    public final static String UPLOAD_BUTTON_CLASS = "start";
    public final static String CANCEL_BUTTON_CLASS = "cancel";

    public void broadcast(javax.faces.event.FacesEvent event) throws javax.faces.event.AbortProcessingException {
		super.broadcast(event);
		
		FacesContext facesContext = FacesContext.getCurrentInstance();
		MethodExpression me = getFileUploadListener();
		
		if (me != null && event instanceof org.primefaces.event.FileUploadEvent) {
			me.invoke(facesContext.getELContext(), new Object[] {event});
		}
	}