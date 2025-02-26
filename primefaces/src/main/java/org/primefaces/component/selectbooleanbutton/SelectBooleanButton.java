/*
 * The MIT License
 *
 * Copyright (c) 2009-2025 PrimeTek Informatics
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
package org.primefaces.component.selectbooleanbutton;

import org.primefaces.util.HTML;
import org.primefaces.util.LangUtils;

import jakarta.faces.application.ResourceDependency;

@ResourceDependency(library = "primefaces", name = "components.css")
@ResourceDependency(library = "primefaces", name = "jquery/jquery.js")
@ResourceDependency(library = "primefaces", name = "jquery/jquery-plugins.js")
@ResourceDependency(library = "primefaces", name = "core.js")
@ResourceDependency(library = "primefaces", name = "components.js")
public class SelectBooleanButton extends SelectBooleanButtonBase {

    public static final String COMPONENT_TYPE = "org.primefaces.component.SelectBooleanButton";

    public static final String STYLE_CLASS = "ui-selectbooleanbutton ui-widget";

    public String resolveStyleClass(boolean checked, boolean disabled) {
        String icon = checked ? getOnIcon() : getOffIcon();
        String styleClass = null;

        if (LangUtils.isBlank(getOnLabel()) && LangUtils.isBlank(getOffLabel())) {
            styleClass = HTML.BUTTON_ICON_ONLY_BUTTON_CLASS;
        }
        else {
            styleClass = icon != null ? HTML.BUTTON_TEXT_ICON_LEFT_BUTTON_CLASS : HTML.BUTTON_TEXT_ONLY_BUTTON_CLASS;
        }

        if (disabled) {
            styleClass = styleClass + " ui-state-disabled";
        }

        if (checked) {
            styleClass = styleClass + " ui-state-active";
        }

        if (!isValid()) {
            styleClass = styleClass + " ui-state-error";
        }

        String userStyleClass = getStyleClass();
        if (userStyleClass != null) {
            styleClass = styleClass + " " + userStyleClass;
        }

        return styleClass;
    }
}