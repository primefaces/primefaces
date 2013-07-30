import javax.el.ValueExpression;
import org.primefaces.context.RequestContext;

    public void broadcast(javax.faces.event.FacesEvent event) throws javax.faces.event.AbortProcessingException {
		super.broadcast(event); //backward compatibility

		FacesContext facesContext = getFacesContext();
		MethodExpression me = getListener();

		if (me != null) {
			me.invoke(facesContext.getELContext(), new Object[] {});
		}

        ValueExpression expr = getValueExpression("stop");
        if(expr != null) {
        	Boolean stop = (Boolean) expr.getValue(facesContext.getELContext());
        	
        	if (Boolean.TRUE.equals(stop)) {
        		String widgetVar = resolveWidgetVar();
        		RequestContext requestContext = RequestContext.getCurrentInstance();
        		requestContext.execute("PF('" + widgetVar + "').stop();");
        	}
        }
	}

    public boolean isPartialSubmitSet() {
        return (getStateHelper().get(PropertyKeys.partialSubmit) != null) || (this.getValueExpression("partialSubmit") != null);
    }
    
    public boolean isResetValuesSet() {
        return (getStateHelper().get(PropertyKeys.resetValues) != null) || (this.getValueExpression("resetValues") != null);
    }
    
    public boolean isAjaxified() {
        return true;
    }