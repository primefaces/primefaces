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
package org.primefaces.component.steps;

import javax.faces.application.ResourceDependencies;
import javax.faces.application.ResourceDependency;

@ResourceDependencies({
        @ResourceDependency(library = "primefaces", name = "components.css"),
        @ResourceDependency(library = "primefaces", name = "jquery/jquery.js"),
        @ResourceDependency(library = "primefaces", name = "core.js"),
        @ResourceDependency(library = "primefaces", name = "components.js")
})
public class Steps extends StepsBase {

    public static final String COMPONENT_TYPE = "org.primefaces.component.Steps";

    public static final String CONTAINER_CLASS = "ui-steps ui-widget ui-helper-clearfix";
    public static final String READONLY_CONTAINER_CLASS = "ui-steps ui-steps-readonly ui-widget ui-helper-clearfix";
    public static final String INACTIVE_ITEM_CLASS = "ui-steps-item ui-state-default ui-state-disabled ui-corner-all";
    public static final String ACTIVE_ITEM_CLASS = "ui-steps-item ui-state-highlight ui-corner-all";
    public static final String VISITED_ITEM_CLASS = "ui-steps-item ui-state-default ui-corner-all";
    public static final String STEP_NUMBER_CLASS = "ui-steps-number";
    public static final String STEP_TITLE_CLASS = "ui-steps-title";
}