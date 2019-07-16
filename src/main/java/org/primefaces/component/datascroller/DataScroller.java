/**
 * The MIT License
 *
 * Copyright (c) 2009-2019 PrimeTek
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
package org.primefaces.component.datascroller;

import javax.faces.application.ResourceDependencies;
import javax.faces.application.ResourceDependency;
import javax.faces.context.FacesContext;

@ResourceDependencies({
        @ResourceDependency(library = "primefaces", name = "components.css"),
        @ResourceDependency(library = "primefaces", name = "jquery/jquery.js"),
        @ResourceDependency(library = "primefaces", name = "jquery/jquery-plugins.js"),
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
    public static final String VIRTUALSCROLL_WRAPPER_CLASS = "ui-datascroller-virtualscroll-wrapper";

    public boolean isLoadRequest() {
        FacesContext context = getFacesContext();
        return context.getExternalContext().getRequestParameterMap().containsKey(getClientId(context) + "_load");
    }

    public boolean isVirtualScrollingRequest(FacesContext context) {
        return context.getExternalContext().getRequestParameterMap().containsKey(getClientId(context) + "_virtualScrolling");
    }
}