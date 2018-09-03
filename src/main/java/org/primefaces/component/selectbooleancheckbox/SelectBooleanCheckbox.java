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
package org.primefaces.component.selectbooleancheckbox;

import javax.faces.application.ResourceDependencies;
import javax.faces.application.ResourceDependency;

@ResourceDependencies({
        @ResourceDependency(library = "primefaces", name = "components.css"),
        @ResourceDependency(library = "primefaces", name = "jquery/jquery.js"),
        @ResourceDependency(library = "primefaces", name = "core.js"),
        @ResourceDependency(library = "primefaces", name = "components.js")
})
public class SelectBooleanCheckbox extends SelectBooleanCheckboxBase {

    public static final String COMPONENT_TYPE = "org.primefaces.component.SelectBooleanCheckbox";

    public static final String STYLE_CLASS = "ui-checkbox ui-widget";
    public static final String CHECKBOX_BOX_CLASS = "ui-checkbox-box ui-widget ui-corner-all ui-state-default";
    public static final String CHECKBOX_INPUT_WRAPPER_CLASS = "ui-helper-hidden";
    public static final String CHECKBOX_ICON_CLASS = "ui-checkbox-icon";
    public static final String CHECKBOX_CHECKED_ICON_CLASS = "ui-icon ui-icon-check";
    public static final String LABEL_CLASS = "ui-checkbox-label";

    @Override
    public String getInputClientId() {
        return this.getClientId(getFacesContext()) + "_input";
    }

    @Override
    public String getValidatableInputClientId() {
        return this.getInputClientId();
    }

    @Override
    public String getLabelledBy() {
        return (String) getStateHelper().get("labelledby");
    }

    @Override
    public void setLabelledBy(String labelledBy) {
        getStateHelper().put("labelledby", labelledBy);
    }
}