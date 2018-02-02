import javax.el.ValueExpression;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import javax.faces.component.EditableValueHolder;
import javax.faces.component.UIComponent;
import javax.faces.event.FacesEvent;
import javax.faces.event.AjaxBehaviorEvent;
import org.primefaces.component.inplace.Inplace;
import org.primefaces.util.Constants;
import javax.faces.event.BehaviorEvent;

    public static final String CONTAINER_CLASS = "ui-inplace ui-hidden-container";
    public static final String DISPLAY_CLASS = "ui-inplace-display";
    public static final String DISABLED_DISPLAY_CLASS = "ui-inplace-display-disabled";
    public static final String CONTENT_CLASS = "ui-inplace-content";
    public static final String EDITOR_CLASS = "ui-inplace-editor";
    public static final String SAVE_BUTTON_CLASS = "ui-inplace-save";
    public static final String CANCEL_BUTTON_CLASS = "ui-inplace-cancel";

    private static final Map<String, Class<? extends BehaviorEvent>> BEHAVIOR_EVENT_MAPPING = Collections.unmodifiableMap(new HashMap<String, Class<? extends BehaviorEvent>>() {{
        put("save", null);
        put("cancel", null);
    }});

    private static final Collection<String> EVENT_NAMES = BEHAVIOR_EVENT_MAPPING.keySet();

    @Override
    public Map<String, Class<? extends BehaviorEvent>> getBehaviorEventMapping() {
         return BEHAVIOR_EVENT_MAPPING;
    }

    @Override
    public Collection<String> getEventNames() {
        return EVENT_NAMES;
    }


    @Override
    public void processDecodes(FacesContext context) {
        if(shouldSkipChildren(context)) {
            this.decode(context);
        }
        else {
            super.processDecodes(context);
        }
    }

    @Override
    public void processValidators(FacesContext context) {
        if(!shouldSkipChildren(context)) {
            super.processValidators(context);
        }
    }

    @Override
    public void processUpdates(FacesContext context) {
        if(!shouldSkipChildren(context)) {
            super.processUpdates(context);
        }
    }

    private boolean shouldSkipChildren(FacesContext context) {
        return context.getExternalContext().getRequestParameterMap().containsKey(this.getClientId(context) + "_cancel")||this.isDisabled();
    }

    public boolean isValid() {
        boolean valid = true;
        
        for(Iterator<UIComponent> it = this.getFacetsAndChildren(); it.hasNext();) {
            UIComponent component = it.next();
            if(component instanceof EditableValueHolder && !((EditableValueHolder) component).isValid()) {
                valid = false;
                break;
            }
        }
        
        return valid;
    }