import javax.el.ValueExpression;
import javax.faces.context.FacesContext;
import org.primefaces.component.api.UITree;

    public void processUpdates(FacesContext context) {
        super.processUpdates(context);
        String base64Value = this.getBase64Value();

        if(base64Value != null) {
            ValueExpression ve = this.getValueExpression(PropertyKeys.base64Value.toString());
            if(ve != null) {
                ve.setValue(context.getELContext(), base64Value);
                getStateHelper().put(PropertyKeys.base64Value, null);
            }
        }
    }