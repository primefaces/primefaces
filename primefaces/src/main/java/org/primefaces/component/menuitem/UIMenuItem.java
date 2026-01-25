/*
 * The MIT License
 *
 * Copyright (c) 2009-2026 PrimeTek Informatics
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.primefaces.component.menuitem;

import org.primefaces.cdk.api.FacesComponentDescription;
import org.primefaces.component.api.DialogReturnAware;
import org.primefaces.util.ComponentUtils;

import java.util.List;
import java.util.Map;

import jakarta.el.MethodExpression;
import jakarta.faces.component.FacesComponent;
import jakarta.faces.component.UIComponent;
import jakarta.faces.component.UIParameter;
import jakarta.faces.context.FacesContext;
import jakarta.faces.event.ActionEvent;
import jakarta.faces.event.FacesEvent;

@FacesComponent(value = UIMenuItem.COMPONENT_TYPE, namespace = UIMenuItem.COMPONENT_FAMILY)
@FacesComponentDescription("MenuItem is a component that represents a menu item.")
public class UIMenuItem extends UIMenuItemBaseImpl implements DialogReturnAware {

    public static final String COMPONENT_TYPE = "org.primefaces.component.UIMenuItem";

    private String confirmationScript;

    @Override
    public void decode(FacesContext facesContext) {
        if (isDisabled()) {
            return;
        }

        Map<String, String> params = facesContext.getExternalContext().getRequestParameterMap();
        String clientId = getClientId(facesContext);

        if (params.containsKey(clientId)) {
            queueEvent(new ActionEvent(this));
        }

        ComponentUtils.decodeBehaviors(facesContext, this);
    }

    @Override
    public void queueEvent(FacesEvent e) {
        FacesContext context = getFacesContext();
        if (isDialogReturnEvent(e, context)) {
            queueDialogReturnEvent(e, context, this, super::queueEvent);
        }
        else {
            super.queueEvent(e);
        }
    }

    @Override
    public boolean shouldRenderChildren() {
        int childCount = getChildCount();
        if (childCount > 0) {
            for (int i = 0; i < childCount; i++) {
                UIComponent child = getChildren().get(i);
                if (!(child instanceof UIParameter)) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public boolean isPartialSubmitSet() {
        return (getStateHelper().get(PropertyKeys.partialSubmit) != null) || (getValueExpression(PropertyKeys.partialSubmit.toString()) != null);
    }

    @Override
    public boolean isResetValuesSet() {
        return (getStateHelper().get(PropertyKeys.resetValues) != null) || (getValueExpression(PropertyKeys.resetValues.toString()) != null);
    }

    @Override
    public String getHref() {
        return getUrl();
    }

    @Override
    public boolean isDynamic() {
        return false;
    }

    @Override
    public Map<String, List<String>> getParams() {
        return ComponentUtils.getUIParams(this);
    }

    @Override
    public String getCommand() {
        MethodExpression expr = super.getActionExpression();
        return expr != null ? expr.getExpressionString() : null;
    }

    @Override
    public boolean isAjaxified() {
        return getUrl() == null && getOutcome() == null && isAjax();
    }

    @Override
    public void setParam(String key, Object value) {
        throw new UnsupportedOperationException("Use UIParameter component instead to add parameters.");
    }

    @Override
    public String getConfirmationScript() {
        return confirmationScript;
    }

    @Override
    public void setConfirmationScript(String confirmationScript) {
        this.confirmationScript = confirmationScript;
    }

    @Override
    public boolean requiresConfirmation() {
        return confirmationScript != null;
    }
}
