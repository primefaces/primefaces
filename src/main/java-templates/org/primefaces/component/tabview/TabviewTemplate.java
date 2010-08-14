import org.primefaces.component.tabview.Tab;

    public boolean isTabChangeRequest(FacesContext context) {
        return context.getExternalContext().getRequestParameterMap().containsKey(this.getClientId(context) + "_tabChange");
    }

    public boolean isContentLoadRequest(FacesContext context) {
        return context.getExternalContext().getRequestParameterMap().containsKey(this.getClientId(context) + "_contentLoad");
    }
    
	public void broadcast(javax.faces.event.FacesEvent event) throws javax.faces.event.AbortProcessingException {
		super.broadcast(event);

		FacesContext facesContext = FacesContext.getCurrentInstance();
		MethodExpression me = getTabChangeListener();

		if(me != null && event instanceof org.primefaces.event.TabChangeEvent) {
			me.invoke(facesContext.getELContext(), new Object[] {event});
		}
	}

    public Tab findTabToLoad(FacesContext context) {
        String newTabId = context.getExternalContext().getRequestParameterMap().get(this.getClientId(context) + "_newTab");
        
        for(UIComponent component : getChildren()) {
            if(component.getClientId().equals(newTabId))
                return (Tab) component;
        }

        return null;
    }