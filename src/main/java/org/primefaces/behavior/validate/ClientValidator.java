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
package org.primefaces.behavior.validate;

import javax.faces.component.UIComponent;
import javax.faces.component.behavior.ClientBehaviorContext;
import org.primefaces.behavior.base.AbstractBehavior;
import org.primefaces.component.api.InputHolder;

public class ClientValidator extends AbstractBehavior {

    public enum PropertyKeys {
        event(String.class),
        disabled(Boolean.class);

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

        UIComponent component = behaviorContext.getComponent();
        String target = (component instanceof InputHolder) ? "'" + ((InputHolder) component).getValidatableInputClientId() + "'" : "this";

        return "return PrimeFaces.vi(" + target + ")";
    }

    @Override
    protected Enum<?>[] getAllProperties() {
        return PropertyKeys.values();
    }

    public String getEvent() {
        return eval(PropertyKeys.event, null);
    }

    public void setEvent(String event) {
        put(PropertyKeys.event, event);
    }

    public boolean isDisabled() {
        return eval(PropertyKeys.disabled, false);
    }

    public void setDisabled(boolean disabled) {
        put(PropertyKeys.disabled, disabled);
    }
}
