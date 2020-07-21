/*
 * The MIT License
 *
 * Copyright (c) 2009-2020 PrimeTek
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
package org.primefaces.component.selectoneradio;

import java.util.List;

import javax.faces.application.ResourceDependency;
import javax.faces.component.UINamingContainer;
import javax.faces.context.FacesContext;

import org.primefaces.util.LangUtils;

@ResourceDependency(library = "primefaces", name = "components.css")
@ResourceDependency(library = "primefaces", name = "jquery/jquery.js")
@ResourceDependency(library = "primefaces", name = "core.js")
@ResourceDependency(library = "primefaces", name = "components.js")
public class SelectOneRadio extends SelectOneRadioBase {

    public static final String COMPONENT_TYPE = "org.primefaces.component.SelectOneRadio";

    public static final List<String> DOM_EVENTS = LangUtils.unmodifiableList("onchange", "onclick");

    public static final String STYLE_CLASS = "ui-selectoneradio ui-widget";
    public static final String NATIVE_STYLE_CLASS = "ui-selectoneradio ui-selectoneradio-native ui-widget";

    private int index = -1;

    public String getRadioButtonId(FacesContext context) {
        index++;

        return this.getClientId(context) + UINamingContainer.getSeparatorChar(context) + index;
    }

    @Override
    public Object saveState(FacesContext context) {
        // reset component for MyFaces view pooling
        index = -1;

        return super.saveState(context);
    }
}