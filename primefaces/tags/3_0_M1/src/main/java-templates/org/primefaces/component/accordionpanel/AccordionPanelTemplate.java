import org.primefaces.component.tabview.Tab;
import javax.el.ValueExpression;

    public boolean isTabChangeRequest(FacesContext context) {
        return context.getExternalContext().getRequestParameterMap().containsKey(this.getClientId(context) + "_tabChange");
    }

    public boolean isContentLoadRequest(FacesContext context) {
        return context.getExternalContext().getRequestParameterMap().containsKey(this.getClientId(context) + "_contentLoad");
    }

    private boolean isSelfRequest(FacesContext context) {
        return isTabChangeRequest(context) || isContentLoadRequest(context);
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

    @Override
    public void processDecodes(FacesContext context) {
        if(isSelfRequest(context)) {
            this.decode(context);
        }
        else {
            super.processDecodes(context);
        }
    }

    @Override
    public void processValidators(FacesContext context) {
        if(!isSelfRequest(context)) {
            super.processValidators(context);
        }
    }

    @Override
    public void processUpdates(FacesContext context) {
        if(!isSelfRequest(context)) {
            super.processUpdates(context);
        }

        ValueExpression expr = this.getValueExpression("activeIndex");
        if(expr != null) {
            expr.setValue(getFacesContext().getELContext(), getActiveIndex());
            resetActiveIndex();
        }
    }

    protected void resetActiveIndex() {
		getStateHelper().remove(PropertyKeys.activeIndex);
    }