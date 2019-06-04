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
package org.primefaces.behavior.validate;

import javax.faces.component.UIComponent;
import javax.faces.component.behavior.ClientBehaviorContext;
import org.primefaces.behavior.base.AbstractBehavior;
import org.primefaces.behavior.base.BehaviorAttribute;
import org.primefaces.component.api.InputHolder;

public class ClientValidator extends AbstractBehavior {

    public enum PropertyKeys implements BehaviorAttribute {
        event(String.class),
        disabled(Boolean.class);

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
        if (isDisabled()) {
            return null;
        }

        UIComponent component = behaviorContext.getComponent();
        String target = (component instanceof InputHolder) ? "'" + ((InputHolder) component).getValidatableInputClientId() + "'" : "this";

        return "return PrimeFaces.vi(" + target + ")";
    }

    @Override
    protected BehaviorAttribute[] getAllAttributes() {
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
