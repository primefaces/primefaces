
    public void broadcast(javax.faces.event.FacesEvent event) throws javax.faces.event.AbortProcessingException {
		super.broadcast(event);
		
		FacesContext facesContext = FacesContext.getCurrentInstance();
		MethodExpression me = getListener();
		
		if (me != null && event instanceof org.primefaces.event.CaptureEvent) {
			me.invoke(facesContext.getELContext(), new Object[] {event});
		}
	}