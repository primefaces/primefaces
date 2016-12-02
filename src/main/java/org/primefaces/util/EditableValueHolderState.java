package org.primefaces.util;

import javax.faces.component.ContextCallback;
import javax.faces.component.EditableValueHolder;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;

import org.primefaces.component.api.InputHolder;

public class EditableValueHolderState {

    private UIComponent parent;

    private UIComponent target;

    private String clientId;

    private boolean valid;

    private boolean required;

    void setParent(UIComponent parent) {
        this.parent = parent;
    }

    void setTarget(UIComponent target) {
        this.target = target;
    }

    public UIComponent getTarget() {
        return target;
    }

    public String getClientId() {
        return clientId;
    }

    public boolean isValid() {
        return valid;
    }

    public boolean isRequired() {
        return required;
    }

    EditableValueHolderState eval(FacesContext context) {
        if (CompositeUtils.isCompositeComponent(parent)) {
            parent.invokeOnComponent(context, parent.getClientId(context), new InputValueHolderContextCallback());
        } else if(target != null) {
            evalAttrs(context, target);
        }
        return this;
    }

    private void evalAttrs(FacesContext context, UIComponent target) {
        clientId = (target instanceof InputHolder) ? ((InputHolder) target).getInputClientId() : target.getClientId(context);
        valid = target instanceof EditableValueHolder ? ((EditableValueHolder) target).isValid() : true;
        required = (target instanceof UIInput) ? ((UIInput) target).isRequired() : false;
    }

    private class InputValueHolderContextCallback implements ContextCallback {

        @Override
        public void invokeContextCallback(FacesContext context, UIComponent target) {
            evalAttrs(context, EditableValueHolderState.this.target);
        }
    }
}
