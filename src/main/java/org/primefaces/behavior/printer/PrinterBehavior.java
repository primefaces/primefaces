/**
 * Copyright 2009-2019 PrimeTek.
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
package org.primefaces.behavior.printer;

import javax.faces.application.ResourceDependencies;
import javax.faces.application.ResourceDependency;
import javax.faces.component.behavior.ClientBehaviorContext;
import javax.faces.context.FacesContext;
import org.primefaces.behavior.base.AbstractBehavior;

import org.primefaces.expression.SearchExpressionFacade;

@ResourceDependencies({
        @ResourceDependency(library = "primefaces", name = "jquery/jquery.js"),
        @ResourceDependency(library = "primefaces", name = "jquery/jquery-plugins.js"),
        @ResourceDependency(library = "primefaces", name = "printer/printer.js"),
        @ResourceDependency(library = "primefaces", name = "core.js")
    })
public class PrinterBehavior extends AbstractBehavior {

    public enum PropertyKeys {
        target(String.class);

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
        FacesContext context = behaviorContext.getFacesContext();

        String components = SearchExpressionFacade.resolveClientId(
                context, behaviorContext.getComponent(), getTarget());

        return "PrimeFaces.expressions.SearchExpressionFacade.resolveComponentsAsSelector('" + components + "').jqprint();return false;";
    }

    @Override
    protected Enum<?>[] getAllProperties() {
        return PropertyKeys.values();
    }

    public String getTarget() {
        return eval(PropertyKeys.target, null);
    }

    public void setTarget(String target) {
        put(PropertyKeys.target, target);
    }
}
