package org.primefaces.util;

import java.beans.BeanInfo;
import java.util.List;

import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.view.AttachedObjectTarget;
import javax.faces.view.EditableValueHolderAttachedObjectTarget;

public class CompositeUtils {

    public static boolean isCompositeComponent(UIComponent component) {
        return component != null && UIComponent.isCompositeComponent(component);
    }

    /**
     * Gets the last <code>EditableValueHolder</code> component contained in parent composite parameter. If not defined, returns parent
     * parameter. Otherwise the last <code>EditableValueHolder</code>.
     *
     * @param component root component
     * @return the deepest <code>EditableValueHolder</code> component if any, otherwise the source component.
     */
    public static EditableValueHolderState lastEditableValueHolder(UIComponent component, FacesContext context) {
        EditableValueHolderState state = new EditableValueHolderState();
        lastEditableValueHolder(component, state);
        return state.eval(context);
    }

    /**
     * Gets the first <code>EditableValueHolder</code> component contained in parent composite parameter. If not defined, returns parent
     * parameter. Otherwise the deepest <code>EditableValueHolder</code>.
     *
     * @param component root component
     * @return the deepest <code>EditableValueHolder</code> component if any, otherwise the source component.
     */
    public static EditableValueHolderState editableValueHolder(UIComponent component, FacesContext context) {
        EditableValueHolderState state = new EditableValueHolderState();
        editableValueHolder(component, state);
        return state.eval(context);
    }

    private static void editableValueHolder(UIComponent component, EditableValueHolderState state) {
        if (isCompositeComponent(component)) {
            BeanInfo info = (BeanInfo) component.getAttributes().get(UIComponent.BEANINFO_KEY);
            List<AttachedObjectTarget> targets = (List<AttachedObjectTarget>) info.getBeanDescriptor()
                    .getValue(AttachedObjectTarget.ATTACHED_OBJECT_TARGETS_KEY);

            for (AttachedObjectTarget target : targets) {
                if (target instanceof EditableValueHolderAttachedObjectTarget) {
                    UIComponent children = component.findComponent(target.getName());
                    if (children == null) {
                        throw new FacesException("Cannot find editableValueHolder with name: \"" + target.getName()
                                + "\" in composite component with id: \"" + component.getClientId() + "\"");
                    }

                    state.setTarget(children);
                    state.setParent(component);
                    break;
                }
            }
        } else if (state.isEditableValueHolder(component)) {
            state.setTarget(component);
        }
    }

    private static void lastEditableValueHolder(UIComponent component, EditableValueHolderState state) {
        editableValueHolder(component, state);
        if (state.getTarget() != component) {
            lastEditableValueHolder(state.getTarget(), state);
        }
    }
}
