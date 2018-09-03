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
package org.primefaces.component.signature;

import javax.el.ValueExpression;
import javax.faces.application.ResourceDependencies;
import javax.faces.application.ResourceDependency;
import javax.faces.context.FacesContext;

@ResourceDependencies({
        @ResourceDependency(library = "primefaces", name = "signature/signature.css"),
        @ResourceDependency(library = "primefaces", name = "jquery/jquery.js"),
        @ResourceDependency(library = "primefaces", name = "jquery/jquery-plugins.js"),
        @ResourceDependency(library = "primefaces", name = "core.js"),
        @ResourceDependency(library = "primefaces", name = "components.js"),
        @ResourceDependency(library = "primefaces", name = "signature/signature.js")
})
public class Signature extends SignatureBase {

    public static final String COMPONENT_TYPE = "org.primefaces.component.Signature";

    public static final String STYLE_CLASS = "ui-inputfield ui-widget ui-state-default ui-corner-all";
    public static final String READONLY_STYLE_CLASS = "ui-widget ui-widget-content ui-corner-all";

    @Override
    public void processUpdates(FacesContext context) {
        super.processUpdates(context);
        String base64Value = this.getBase64Value();

        if (base64Value != null) {
            ValueExpression ve = this.getValueExpression(PropertyKeys.base64Value.toString());
            if (ve != null) {
                ve.setValue(context.getELContext(), base64Value);
                getStateHelper().put(PropertyKeys.base64Value, null);
            }
        }
    }

    public String resolveStyleClass() {
        String styleClass = STYLE_CLASS;

        if (isReadonly()) {
            styleClass = READONLY_STYLE_CLASS;
        }

        return styleClass;
    }
}