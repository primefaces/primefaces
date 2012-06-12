import org.primefaces.context.RequestContext;

    public void broadcast(javax.faces.event.FacesEvent event) throws javax.faces.event.AbortProcessingException {
		super.broadcast(event); //backward compatibility

		FacesContext facesContext = FacesContext.getCurrentInstance();
		MethodExpression me = getListener();

		if (me != null) {
			me.invoke(facesContext.getELContext(), new Object[] {});
		}

        ValueExpression expr = getValueExpression("stop");
        if(expr != null) {
            RequestContext requestContext = RequestContext.getCurrentInstance();
            requestContext.addCallbackParam("stop", expr.getValue(facesContext.getELContext()));
        }
	}

    public boolean isPartialSubmitSet() {
        return (getStateHelper().get(PropertyKeys.partialSubmit) != null) || (this.getValueExpression("partialSubmit") != null);
    }