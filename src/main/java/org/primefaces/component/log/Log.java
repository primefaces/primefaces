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
package org.primefaces.component.log;

import javax.faces.application.ResourceDependencies;
import javax.faces.application.ResourceDependency;

@ResourceDependencies({
        @ResourceDependency(library = "primefaces", name = "components.css"),
        @ResourceDependency(library = "primefaces", name = "log/log.css"),
        @ResourceDependency(library = "primefaces", name = "jquery/jquery.js"),
        @ResourceDependency(library = "primefaces", name = "jquery/jquery-plugins.js"),
        @ResourceDependency(library = "primefaces", name = "core.js"),
        @ResourceDependency(library = "primefaces", name = "components.js"),
        @ResourceDependency(library = "primefaces", name = "log/log.js")
})
public class Log extends LogBase {

    public static final String COMPONENT_TYPE = "org.primefaces.component.Log";

    public static final String CONTAINER_CLASS = "ui-log ui-widget ui-widget-content ui-corner-all";
    public static final String HEADER_CLASS = "ui-log-header ui-widget-header ui-helper-clearfix";
    public static final String CONTENT_CLASS = "ui-log-content";
    public static final String ITEMS_CLASS = "ui-log-items";
    public static final String CLEAR_BUTTON_CLASS = "ui-log-button ui-log-clear ui-corner-all";
    public static final String ALL_BUTTON_CLASS = "ui-log-button ui-log-all ui-corner-all";
    public static final String INFO_BUTTON_CLASS = "ui-log-button ui-log-info ui-corner-all";
    public static final String DEBUG_BUTTON_CLASS = "ui-log-button ui-log-debug ui-corner-all";
    public static final String WARN_BUTTON_CLASS = "ui-log-button ui-log-warn ui-corner-all";
    public static final String ERROR_BUTTON_CLASS = "ui-log-button ui-log-error ui-corner-all";
}