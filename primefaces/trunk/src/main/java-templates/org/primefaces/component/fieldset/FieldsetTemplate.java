
	public void broadcast(javax.faces.event.FacesEvent event) throws javax.faces.event.AbortProcessingException {
		super.broadcast(event);

		FacesContext facesContext = FacesContext.getCurrentInstance();

		if(event instanceof org.primefaces.event.ToggleEvent) {
			MethodExpression toggleMe = getToggleListener();

			if(toggleMe != null) {
				toggleMe.invoke(facesContext.getELContext(), new Object[] {event});
			}
		}
	}