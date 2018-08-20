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
package org.primefaces.component.selectmanymenu;

import javax.faces.application.ResourceDependencies;
import javax.faces.application.ResourceDependency;

@ResourceDependencies({
        @ResourceDependency(library = "primefaces", name = "components.css"),
        @ResourceDependency(library = "primefaces", name = "jquery/jquery.js"),
        @ResourceDependency(library = "primefaces", name = "core.js"),
        @ResourceDependency(library = "primefaces", name = "components.js")
})
public class SelectManyMenu extends SelectManyMenuBase {

    public static final String COMPONENT_TYPE = "org.primefaces.component.SelectManyMenu";

    public static final String CONTAINER_CLASS = "ui-selectmanymenu ui-inputfield ui-widget ui-widget-content ui-corner-all";
    public static final String LIST_CONTAINER_CLASS = "ui-selectlistbox-listcontainer";
    public static final String LIST_CLASS = "ui-selectlistbox-list";
    public static final String ITEM_CLASS = "ui-selectlistbox-item ui-corner-all";
    public static final String FILTER_CONTAINER_CLASS = "ui-selectlistbox-filter-container";
    public static final String FILTER_CLASS = "ui-selectlistbox-filter ui-inputfield ui-widget ui-state-default ui-corner-all";
    public static final String FILTER_ICON_CLASS = "ui-icon ui-icon-search";
    public static final String[] DOM_EVENTS = {"onchange", "onclick", "ondblclick"};

    @Override
    public String getInputClientId() {
        return getClientId(getFacesContext()) + "_input";
    }

    @Override
    public String getValidatableInputClientId() {
        return getInputClientId();
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