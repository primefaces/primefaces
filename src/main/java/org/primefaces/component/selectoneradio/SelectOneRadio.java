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
package org.primefaces.component.selectoneradio;

import java.util.List;
import javax.faces.application.ResourceDependencies;
import javax.faces.application.ResourceDependency;
import javax.faces.component.UINamingContainer;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

@ResourceDependencies({
        @ResourceDependency(library = "primefaces", name = "components.css"),
        @ResourceDependency(library = "primefaces", name = "jquery/jquery.js"),
        @ResourceDependency(library = "primefaces", name = "core.js"),
        @ResourceDependency(library = "primefaces", name = "components.js")
})
public class SelectOneRadio extends SelectOneRadioBase implements org.primefaces.component.api.Widget {


    public static final String COMPONENT_TYPE = "org.primefaces.component.SelectOneRadio";

    public final static String[] SUPPORTED_EVENTS = new String[]{"onchange", "onclick"};

    public final static String STYLE_CLASS = "ui-selectoneradio ui-widget";
    public final static String NATIVE_STYLE_CLASS = "ui-selectoneradio ui-selectoneradio-native ui-widget";

    private int index = -1;

    public String getRadioButtonId(FacesContext context) {
        index++;

        return getClientId(context) + UINamingContainer.getSeparatorChar(context) + index;
    }

    private List<SelectItem> selectItems;

    public void setSelectItems(List<SelectItem> selectItems) {
        this.selectItems = selectItems;
    }

    public List<SelectItem> getSelectItems() {
        return selectItems;
    }
}