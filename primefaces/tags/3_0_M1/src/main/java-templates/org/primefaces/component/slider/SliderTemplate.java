    public void broadcast(javax.faces.event.FacesEvent event) throws javax.faces.event.AbortProcessingException {
		super.broadcast(event);

		FacesContext facesContext = FacesContext.getCurrentInstance();
		MethodExpression me = getSlideEndListener();

		if (me != null && event instanceof org.primefaces.event.SlideEndEvent) {
			me.invoke(facesContext.getELContext(), new Object[] {event});
		}
	}