/*
 * The MIT License
 *
 * Copyright (c) 2009-2024 PrimeTek Informatics
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
package org.primefaces.behavior.confirm;

import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.component.behavior.ClientBehaviorContext;
import javax.faces.context.FacesContext;
import org.primefaces.behavior.base.AbstractBehavior;
import org.primefaces.behavior.base.BehaviorAttribute;
import org.primefaces.component.api.Confirmable;
import org.json.JSONObject;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.FacetUtils;

public class ConfirmBehavior extends AbstractBehavior {

    public static final String BEHAVIOR_ID = "org.primefaces.behavior.ConfirmBehavior";

    public enum PropertyKeys implements BehaviorAttribute {
        source(String.class),
        type(String.class),
        header(String.class),
        message(String.class),
        icon(String.class),
        yesButtonLabel(String.class),
        yesButtonClass(String.class),
        yesButtonIcon(String.class),
        noButtonLabel(String.class),
        noButtonClass(String.class),
        noButtonIcon(String.class),
        disabled(Boolean.class),
        beforeShow(String.class),
        escape(Boolean.class);

        private final Class<?> expectedType;

        PropertyKeys(Class<?> expectedType) {
            this.expectedType = expectedType;
        }

        @Override
        public Class<?> getExpectedType() {
            return expectedType;
        }
    }

    @Override
    public String getScript(ClientBehaviorContext behaviorContext) {
        FacesContext context = behaviorContext.getFacesContext();
        UIComponent component = behaviorContext.getComponent();

        if (isDisabled()) {
            if (component instanceof Confirmable) {
                ((Confirmable) component).setConfirmationScript(null);
            }

            return null;
        }

        String source = getSource();
        String type = JSONObject.quote(getType());
        String headerText = JSONObject.quote(getHeader());

        String messageText;
        UIComponent messageFacetComponent = component.getFacet("confirmMessage");
        if (FacetUtils.shouldRenderFacet(messageFacetComponent)) {
            messageText = JSONObject.quote(ComponentUtils.encodeComponent(messageFacetComponent, context));
        }
        else {
            messageText = JSONObject.quote(getMessage());
        }
        String beforeShow = JSONObject.quote(getBeforeShow());
        String yesButtonClass = JSONObject.quote(getYesButtonClass());
        String yesButtonLabel = JSONObject.quote(getYesButtonLabel());
        String yesButtonIcon = JSONObject.quote(getYesButtonIcon());
        String noButtonClass = JSONObject.quote(getNoButtonClass());
        String noButtonLabel = JSONObject.quote(getNoButtonLabel());
        String noButtonIcon = JSONObject.quote(getNoButtonIcon());

        source = (source == null) ? component.getClientId(context) : source;

        if (component instanceof Confirmable) {
            String sourceProperty = (source == null || "this".equals(source)) ? "source:this" : "source:\"" + source + "\"";
            String icon = getIcon();

            String script = "PrimeFaces.confirm({" + sourceProperty
                                                   + ",type:" + type
                                                   + ",escape:" + isEscape()
                                                   + ",header:" + headerText
                                                   + ",message:" + messageText
                                                   + ",yesButtonClass:" + yesButtonClass
                                                   + ",yesButtonLabel:" + yesButtonLabel
                                                   + ",yesButtonIcon:" + yesButtonIcon
                                                   + ",noButtonClass:" + noButtonClass
                                                   + ",noButtonLabel:" + noButtonLabel
                                                   + ",noButtonIcon:" + noButtonIcon
                                                   + ",icon:\"" + (icon == null ? "" : icon)
                                                   + "\",beforeShow:" + beforeShow
                                                   + "});return false;";
            ((Confirmable) component).setConfirmationScript(script);

            return null;
        }
        else {
            throw new FacesException("Component " + source + " is not a Confirmable. "
                    + "ConfirmBehavior can only be attached to components that implement " + Confirmable.class.getName() + " interface");
        }
    }

    @Override
    protected BehaviorAttribute[] getAllAttributes() {
        return PropertyKeys.values();
    }

    public String getSource() {
        return attributeHandler.eval(PropertyKeys.source, null);
    }

    public void setSource(String source) {
        attributeHandler.put(PropertyKeys.source, source);
    }

    public String getType() {
        return attributeHandler.eval(PropertyKeys.type, "dialog");
    }

    public void setType(String type) {
        attributeHandler.put(PropertyKeys.type, type);
    }

    public String getHeader() {
        return attributeHandler.eval(PropertyKeys.header, null);
    }

    public void setHeader(String header) {
        attributeHandler.put(PropertyKeys.header, header);
    }

    public String getMessage() {
        return attributeHandler.eval(PropertyKeys.message, null);
    }

    public void setMessage(String message) {
        attributeHandler.put(PropertyKeys.message, message);
    }

    public String getIcon() {
        return attributeHandler.eval(PropertyKeys.icon, null);
    }

    public void setIcon(String icon) {
        attributeHandler.put(PropertyKeys.icon, icon);
    }

    public boolean isDisabled() {
        return attributeHandler.eval(PropertyKeys.disabled, Boolean.FALSE);
    }

    public void setDisabled(boolean disabled) {
        attributeHandler.put(PropertyKeys.disabled, disabled);
    }

    public String getBeforeShow() {
        return attributeHandler.eval(PropertyKeys.beforeShow, null);
    }

    public void setBeforeShow(String beforeShow) {
        attributeHandler.put(PropertyKeys.beforeShow, beforeShow);
    }

    public boolean isEscape() {
        return attributeHandler.eval(PropertyKeys.escape, Boolean.TRUE);
    }

    public void setEscape(boolean escape) {
        attributeHandler.put(PropertyKeys.escape, escape);
    }

    public String getYesButtonLabel() {
        return attributeHandler.eval(PropertyKeys.yesButtonLabel, null);
    }

    public void setYesButtonLabel(String yesButtonLabel) {
        attributeHandler.put(PropertyKeys.yesButtonLabel, yesButtonLabel);
    }

    public String getYesButtonClass() {
        return attributeHandler.eval(PropertyKeys.yesButtonClass, null);
    }

    public void setYesButtonClass(String yesButtonClass) {
        attributeHandler.put(PropertyKeys.yesButtonClass, yesButtonClass);
    }

    public String getYesButtonIcon() {
        return attributeHandler.eval(PropertyKeys.yesButtonIcon, null);
    }

    public void setYesButtonIcon(String yesButtonIcon) {
        attributeHandler.put(PropertyKeys.yesButtonIcon, yesButtonIcon);
    }

    public String getNoButtonLabel() {
        return attributeHandler.eval(PropertyKeys.noButtonLabel, null);
    }

    public void setNoButtonLabel(String noButtonLabel) {
        attributeHandler.put(PropertyKeys.noButtonLabel, noButtonLabel);
    }

    public String getNoButtonClass() {
        return attributeHandler.eval(PropertyKeys.noButtonClass, null);
    }

    public void setNoButtonClass(String noButtonClass) {
        attributeHandler.put(PropertyKeys.noButtonClass, noButtonClass);
    }

    public String getNoButtonIcon() {
        return attributeHandler.eval(PropertyKeys.noButtonIcon, null);
    }

    public void setNoButtonIcon(String noButtonIcon) {
        attributeHandler.put(PropertyKeys.noButtonIcon, noButtonIcon);
    }
}
