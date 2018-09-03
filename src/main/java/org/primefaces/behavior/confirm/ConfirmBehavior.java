/**
 * Copyright 2009-2018 PrimeTek.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.primefaces.behavior.confirm;

import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.component.behavior.ClientBehaviorContext;
import javax.faces.context.FacesContext;
import org.primefaces.behavior.base.AbstractBehavior;
import org.primefaces.component.api.Confirmable;
import org.primefaces.json.JSONObject;

public class ConfirmBehavior extends AbstractBehavior {

    public final static String BEHAVIOR_ID = "org.primefaces.behavior.ConfirmBehavior";

    public enum PropertyKeys {
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

        /**
         * Holds the type which ought to be passed to
         * {@link javax.faces.view.facelets.TagAttribute#getObject(javax.faces.view.facelets.FaceletContext, java.lang.Class) }
         * when creating the behavior.
         * @return the expectedType the expected object type
         */
        public Class<?> getExpectedType() {
            return expectedType;
        }
    }

    @Override
    public String getScript(ClientBehaviorContext behaviorContext) {
        if (isDisabled()) {
            return null;
        }

        FacesContext context = behaviorContext.getFacesContext();
        UIComponent component = behaviorContext.getComponent();
        String source = component.getClientId(context);
        String headerText = JSONObject.quote(this.getHeader());
        String messageText = JSONObject.quote(this.getMessage());
        String beforeShow = JSONObject.quote(this.getBeforeShow());

        if (component instanceof Confirmable) {
            String sourceProperty = (source == null) ? "source:this" : "source:\"" + source + "\"";
            String script = "PrimeFaces.confirm({" + sourceProperty
                                                   + ",escape:" + this.isEscape()
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
    protected Enum<?>[] getAllProperties() {
        return PropertyKeys.values();
    }

    public String getHeader() {
        return eval(PropertyKeys.header, null);
    }

    public void setHeader(String header) {
        setLiteral(PropertyKeys.header, header);
    }

    public String getMessage() {
        return eval(PropertyKeys.message, null);
    }

    public void setMessage(String message) {
        setLiteral(PropertyKeys.message, message);
    }

    public String getIcon() {
        return eval(PropertyKeys.icon, null);
    }

    public void setIcon(String icon) {
        setLiteral(PropertyKeys.icon, icon);
    }

    public boolean isDisabled() {
        return eval(PropertyKeys.disabled, Boolean.FALSE);
    }

    public void setDisabled(boolean disabled) {
        setLiteral(PropertyKeys.disabled, disabled);
    }

    public String getBeforeShow() {
        return eval(PropertyKeys.beforeShow, null);
    }

    public void setBeforeShow(String beforeShow) {
        setLiteral(PropertyKeys.beforeShow, beforeShow);
    }

    public boolean isEscape() {
        return eval(PropertyKeys.escape, Boolean.TRUE);
    }

    public void setEscape(boolean escape) {
        setLiteral(PropertyKeys.escape, escape);
    }
}
