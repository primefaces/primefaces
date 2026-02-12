/*
 * The MIT License
 *
 * Copyright (c) 2009-2026 PrimeTek Informatics
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
package org.primefaces.component.speeddial;

import org.primefaces.cdk.api.FacesComponentInfo;

import jakarta.faces.application.ResourceDependency;
import jakarta.faces.component.FacesComponent;

@FacesComponent(value = SpeedDial.COMPONENT_TYPE, namespace = SpeedDial.COMPONENT_FAMILY)
@FacesComponentInfo(description = "When pressed, a floating action button can display multiple primary actions that can be performed on a page.")
@ResourceDependency(library = "primefaces", name = "components.css")
@ResourceDependency(library = "primefaces", name = "jquery/jquery.js")
@ResourceDependency(library = "primefaces", name = "jquery/jquery-plugins.js")
@ResourceDependency(library = "primefaces", name = "core.js")
@ResourceDependency(library = "primefaces", name = "components.js")
public class SpeedDial extends SpeedDialBaseImpl {

    public static final String COMPONENT_TYPE = "org.primefaces.component.SpeedDial";

    public static final String CONTAINER_CLASS = "ui-speeddial ui-widget";
    public static final String MASK_CLASS = "ui-speeddial-mask";
    public static final String BUTTON_CLASS = "ui-speeddial-button rounded-button";
    public static final String LIST_CLASS = "ui-speeddial-list";
    public static final String ITEM_CLASS = "ui-speeddial-item";
    public static final String ITEM_BUTTON_CLASS = "ui-speeddial-action";
    public static final String ITEM_ICON_CLASS = "ui-speeddial-action-icon";

}
