    public final static String CONTAINER_CLASS = "ui-fileupload ui-widget";
    public final static String BUTTON_BAR_CLASS = "ui-fileupload-buttonbar ui-widget-header ui-corner-top";
    public final static String CONTENT_CLASS = "ui-fileupload-content ui-widget-content ui-corner-bottom";
    public final static String FILES_CLASS = "ui-fileupload-files";
    public final static String CHOOSE_BUTTON_CLASS = "ui-fileupload-choose";
    public final static String UPLOAD_BUTTON_CLASS = "ui-fileupload-upload";
    public final static String CANCEL_BUTTON_CLASS = "ui-fileupload-cancel";
    
    public final static String CONTAINER_CLASS_SIMPLE = "ui-fileupload-simple ui-widget";
    public final static String FILENAME_CLASS = "ui-fileupload-filename";
    
    public final static String MOBILE_CONTAINER_CLASS = "ui-fileupload ui-input-text ui-body-inherit ui-corner-all ui-shadow-inset";

    public void broadcast(javax.faces.event.FacesEvent event) throws javax.faces.event.AbortProcessingException {
		super.broadcast(event);
		
		FacesContext facesContext = getFacesContext();
		MethodExpression me = getFileUploadListener();
		
		if (me != null && event instanceof org.primefaces.event.FileUploadEvent) {
			me.invoke(facesContext.getELContext(), new Object[] {event});
		}
	}
