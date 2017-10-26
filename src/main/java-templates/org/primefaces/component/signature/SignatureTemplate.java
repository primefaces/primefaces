import javax.el.ValueExpression;
import javax.faces.context.FacesContext;
import org.primefaces.component.api.UITree;

public static final String STYLE_CLASS = "ui-inputfield ui-widget ui-state-default ui-corner-all";
public static final String READONLY_STYLE_CLASS = "ui-widget ui-widget-content ui-corner-all";

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
    
    public String resolveStyleClass() {
        String styleClass = STYLE_CLASS; 
        
        if(isReadonly()) {
            styleClass = READONLY_STYLE_CLASS;
        } 
        
        return styleClass;
    }