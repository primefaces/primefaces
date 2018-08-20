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
package org.primefaces.component.outputlabel;

import javax.faces.component.html.HtmlOutputLabel;


abstract class OutputLabelBase extends HtmlOutputLabel {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.OutputLabelRenderer";

    public enum PropertyKeys {

        indicateRequired;
    }

    public OutputLabelBase() {
        setRendererType(DEFAULT_RENDERER);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    public String getIndicateRequired() {
        return (String) getStateHelper().eval(PropertyKeys.indicateRequired, "auto");
    }

    public void setIndicateRequired(String indicateRequired) {
        getStateHelper().put(PropertyKeys.indicateRequired, indicateRequired);
    }

}