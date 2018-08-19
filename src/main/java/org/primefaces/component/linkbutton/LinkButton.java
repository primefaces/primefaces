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
package org.primefaces.component.linkbutton;

import java.util.List;
import java.util.Map;

import javax.faces.application.ResourceDependencies;
import javax.faces.application.ResourceDependency;

import org.primefaces.util.ComponentUtils;
import org.primefaces.util.HTML;

@ResourceDependencies({
        @ResourceDependency(library = "primefaces", name = "components.css"),
        @ResourceDependency(library = "primefaces", name = "jquery/jquery.js"),
        @ResourceDependency(library = "primefaces", name = "core.js"),
        @ResourceDependency(library = "primefaces", name = "components.js")
})
public class LinkButton extends LinkButtonBase {

    public static final String COMPONENT_TYPE = "org.primefaces.component.LinkButton";

    public static final String STYLE_CLASS = "ui-linkbutton " + HTML.BUTTON_TEXT_ONLY_BUTTON_CLASS;
    public static final String DISABLED_STYLE_CLASS = STYLE_CLASS + " ui-state-disabled";

    @Override
    public Map<String, List<String>> getParams() {
        return ComponentUtils.getUIParams(this);
    }
}