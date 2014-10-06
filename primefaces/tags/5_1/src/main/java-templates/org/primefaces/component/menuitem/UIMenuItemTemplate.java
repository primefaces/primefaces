import java.util.List;
import javax.faces.component.UIComponent;
import java.util.Map;
import javax.faces.event.ActionEvent;
import javax.el.MethodExpression;
import javax.faces.component.UIParameter;
import org.primefaces.util.ComponentUtils;

	public void decode(FacesContext facesContext) {
		Map<String,String> params = facesContext.getExternalContext().getRequestParameterMap();
		String clientId = getClientId(facesContext);
		
		if(params.containsKey(clientId)) {
			this.queueEvent(new ActionEvent(this));
		}
	}
	
	public boolean shouldRenderChildren() {
		if(getChildCount() == 0)
			return false;
		else {
			for(UIComponent child : getChildren()) {
				if(! (child instanceof UIParameter) ) {
					return true;
				}
			}
		}
		
		return false;
	}

    public boolean isPartialSubmitSet() {
        return (getStateHelper().get(PropertyKeys.partialSubmit) != null) || (this.getValueExpression("partialSubmit") != null); 
    }
    
    public boolean isResetValuesSet() {
        return (getStateHelper().get(PropertyKeys.resetValues) != null) || (this.getValueExpression("resetValues") != null);
    }

    public String getHref() {
        return this.getUrl();
    }

    public boolean isDynamic() {
        return false;
    }

    public Map<String, List<String>> getParams() {
        return ComponentUtils.getUIParams(this);
    }

    public String getCommand() {
        MethodExpression expr = super.getActionExpression();
        return expr != null ? expr.getExpressionString() : null;
    }
    
    public boolean isAjaxified() {
    	return getUrl() == null && getOutcome() == null && isAjax();
    }

    public void setParam(String key, Object value) {
        throw new UnsupportedOperationException("Use UIParameter component instead to add parameters.");
    }