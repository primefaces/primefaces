	
    public final static String STYLE_CLASS = "ui-inputfield ui-widget ui-state-default ui-corner-all";

    public void broadcast(javax.faces.event.FacesEvent event) throws javax.faces.event.AbortProcessingException {
		super.broadcast(event);
		
		FacesContext facesContext = FacesContext.getCurrentInstance();
		MethodExpression me = getSelectListener();
		
		if (me != null && event instanceof org.primefaces.event.SelectEvent) {
			me.invoke(facesContext.getELContext(), new Object[] {event});
		}
	}