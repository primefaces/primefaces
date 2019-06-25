/**
 * The MIT License
 *
 * Copyright (c) 2009-2019 PrimeTek
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

public class ConfirmBehavior extends AbstractBehavior {

    public static final String BEHAVIOR_ID = "org.primefaces.behavior.ConfirmBehavior";

    public enum PropertyKeys implements BehaviorAttribute {
        header(String.class),
        message(String.class),
        icon(String.class),
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

        String source = component.getClientId(context);
        String headerText = JSONObject.quote(getHeader());
        String messageText = JSONObject.quote(getMessage());
        String beforeShow = JSONObject.quote(getBeforeShow());

        if (component instanceof Confirmable) {
            String sourceProperty = (source == null) ? "source:this" : "source:\"" + source + "\"";
            String script = "PrimeFaces.confirm({" + sourceProperty
                                                   + ",escape:" + isEscape()
                                                   + ",header:" + headerText
                                                   + ",message:" + messageText
                                                   + ",icon:\"" + getIcon()
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

    public String getHeader() {
        return eval(PropertyKeys.header, null);
    }

    public void setHeader(String header) {
        put(PropertyKeys.header, header);
    }

    public String getMessage() {
        return eval(PropertyKeys.message, null);
    }

    public void setMessage(String message) {
        put(PropertyKeys.message, message);
    }

    public String getIcon() {
        return eval(PropertyKeys.icon, null);
    }

    public void setIcon(String icon) {
        put(PropertyKeys.icon, icon);
    }

    public boolean isDisabled() {
        return eval(PropertyKeys.disabled, Boolean.FALSE);
    }

    public void setDisabled(boolean disabled) {
        put(PropertyKeys.disabled, disabled);
    }

    public String getBeforeShow() {
        return eval(PropertyKeys.beforeShow, null);
    }

    public void setBeforeShow(String beforeShow) {
        put(PropertyKeys.beforeShow, beforeShow);
    }

    public boolean isEscape() {
        return eval(PropertyKeys.escape, Boolean.TRUE);
    }

    public void setEscape(boolean escape) {
        put(PropertyKeys.escape, escape);
    }
}
