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
package org.primefaces.component.rowtoggler;

import javax.faces.component.UIComponentBase;


abstract class RowTogglerBase extends UIComponentBase {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.RowTogglerRenderer";

    public enum PropertyKeys {

        expandLabel,
        collapseLabel,
        tabindex;
    }

    public RowTogglerBase() {
        setRendererType(DEFAULT_RENDERER);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    public String getExpandLabel() {
        return (String) getStateHelper().eval(PropertyKeys.expandLabel, null);
    }

    public void setExpandLabel(String expandLabel) {
        getStateHelper().put(PropertyKeys.expandLabel, expandLabel);
    }

    public String getCollapseLabel() {
        return (String) getStateHelper().eval(PropertyKeys.collapseLabel, null);
    }

    public void setCollapseLabel(String collapseLabel) {
        getStateHelper().put(PropertyKeys.collapseLabel, collapseLabel);
    }

    public String getTabindex() {
        return (String) getStateHelper().eval(PropertyKeys.tabindex, "0");
    }

    public void setTabindex(String tabindex) {
        getStateHelper().put(PropertyKeys.tabindex, tabindex);
    }

}