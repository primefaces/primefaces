
    public void broadcast(javax.faces.event.FacesEvent event) throws javax.faces.event.AbortProcessingException {
		super.broadcast(event); //backward compatibility

		FacesContext facesContext = FacesContext.getCurrentInstance();
		MethodExpression me = getListener();

		if (me != null) {
			me.invoke(facesContext.getELContext(), new Object[] {});
		}
	}