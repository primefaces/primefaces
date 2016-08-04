package org.primefaces.util;

import java.beans.BeanInfo;
import java.util.List;
import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.view.AttachedObjectTarget;
import javax.faces.view.EditableValueHolderAttachedObjectTarget;

public class CompositeUtils {
    
    public static boolean isComposite(UIComponent component)
    {
        return UIComponent.isCompositeComponent(component);
    }
    
    public static UIComponent extractEditableValueHolder(UIComponent composite) {
        BeanInfo info = (BeanInfo) composite.getAttributes().get(UIComponent.BEANINFO_KEY);
        List<AttachedObjectTarget> targets = (List<AttachedObjectTarget>) info.getBeanDescriptor()
                .getValue(AttachedObjectTarget.ATTACHED_OBJECT_TARGETS_KEY);

        for (AttachedObjectTarget target : targets) {
            if (target instanceof EditableValueHolderAttachedObjectTarget) {
                UIComponent children = composite.findComponent(target.getName());
                if (children == null) {
                    throw new FacesException(
                            "Cannot find editableValueHolder with name: \"" + target.getName()
                                    + "\" in composite component with id: \"" + composite.getClientId() + "\"");
                }

                return children;
            }
        }

        return null;
    }
    
    /**
     * Gets the deepest <code>EditableValueHolder</code> component contained in parent composite parameter.
     * If not defined, returns parent parameter. Otherwise the deepest <code>EditableValueHolder</code>.
     *
     * @param component root component
     * @return the deepest <code>EditableValueHolder</code> component if any, otherwise the source component.
     */
    public static UIComponent extractDeepestEditableValueHolder(UIComponent component) {
        UIComponent deepest = component;

        if (UIComponent.isCompositeComponent(component)) {
            deepest = extractEditableValueHolder(deepest);
            deepest = extractDeepestEditableValueHolder(deepest);
        }

        return deepest;
    }
}
