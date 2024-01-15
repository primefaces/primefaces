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
package org.primefaces.behavior.printer;

import javax.faces.application.ResourceDependency;
import javax.faces.component.behavior.ClientBehaviorContext;
import javax.faces.context.FacesContext;

import org.json.JSONObject;
import org.primefaces.behavior.base.AbstractBehavior;
import org.primefaces.behavior.base.BehaviorAttribute;
import org.primefaces.expression.SearchExpressionUtils;
import org.primefaces.util.Constants;
import org.primefaces.util.LangUtils;

@ResourceDependency(library = "primefaces", name = "jquery/jquery.js")
@ResourceDependency(library = "primefaces", name = "jquery/jquery-plugins.js")
@ResourceDependency(library = "primefaces", name = "printer/printer.js")
@ResourceDependency(library = "primefaces", name = "core.js")
public class PrinterBehavior extends AbstractBehavior {

    public enum PropertyKeys implements BehaviorAttribute {
        target(String.class),
        configuration(String.class);

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

        String component = SearchExpressionUtils.resolveClientIdForClientSide(
                    context, behaviorContext.getComponent(), getTarget());

        String config = getConfiguration();
        if (LangUtils.isNotBlank(config)) {
            // escape it for safety
            JSONObject jsonObject = new JSONObject('{' + config + '}');
            config = jsonObject.toString();
        }
        else {
            config = Constants.EMPTY_STRING;
        }

        return String.format("PrimeFaces.expressions.SearchExpressionFacade.resolveComponentsAsSelector(document.body,'%s').print(%s);return false;",
                    component, config);
    }

    @Override
    protected BehaviorAttribute[] getAllAttributes() {
        return PropertyKeys.values();
    }

    public String getTarget() {
        return eval(PropertyKeys.target, null);
    }

    public void setTarget(String target) {
        put(PropertyKeys.target, target);
    }

    public String getConfiguration() {
        return eval(PropertyKeys.configuration, null);
    }

    public void setConfiguration(String configuration) {
        put(PropertyKeys.configuration, configuration);
    }
}
