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
package org.primefaces.component.datascroller;

import javax.faces.application.ResourceDependencies;
import javax.faces.application.ResourceDependency;
import javax.faces.context.FacesContext;

@ResourceDependencies({
        @ResourceDependency(library = "primefaces", name = "components.css"),
        @ResourceDependency(library = "primefaces", name = "jquery/jquery.js"),
        @ResourceDependency(library = "primefaces", name = "core.js"),
        @ResourceDependency(library = "primefaces", name = "components.js")
})
public class DataScroller extends DataScrollerBase {

    public static final String COMPONENT_TYPE = "org.primefaces.component.DataScroller";
    public static final String CONTAINER_CLASS = "ui-datascroller ui-widget";
    public static final String INLINE_CONTAINER_CLASS = "ui-datascroller ui-datascroller-inline ui-widget";
    public static final String HEADER_CLASS = "ui-datascroller-header ui-widget-header ui-corner-top";
    public static final String CONTENT_CLASS = "ui-datascroller-content ui-widget-content";
    public static final String LIST_CLASS = "ui-datascroller-list";
    public static final String ITEM_CLASS = "ui-datascroller-item";
    public static final String LOADER_CLASS = "ui-datascroller-loader";
    public static final String LOADING_CLASS = "ui-datascroller-loading";

    public boolean isLoadRequest() {
        FacesContext context = getFacesContext();
        return context.getExternalContext().getRequestParameterMap().containsKey(getClientId(context) + "_load");
    }
}