/*
 * The MIT License
 *
 * Copyright (c) 2009-2021 PrimeTek
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
package org.primefaces.component.divider;

import javax.faces.application.ResourceDependency;

@ResourceDependency(library = "primefaces", name = "components.css")
public class Divider extends DividerBase {
    public static final String COMPONENT_TYPE = "org.primefaces.component.Divider";

    public static final String STYLE_CLASS = "ui-divider ui-widget";
    public static final String CONTENT_CLASS = "ui-divider-content";
    public static final String HORIZONTAL_CLASS = "ui-divider-horizontal";
    public static final String VERTICAL_CLASS = "ui-divider-vertical";
    public static final String SOLID_CLASS = "ui-divider-solid";
    public static final String DASHED_CLASS = "ui-divider-dashed";
    public static final String DOTTED_CLASS = "ui-divider-dotted";
    public static final String ALIGN_LEFT_CLASS = "ui-divider-left";
    public static final String ALIGN_CENTER_CLASS = "ui-divider-center";
    public static final String ALIGN_RIGHT_CLASS = "ui-divider-right";
    public static final String ALIGN_TOP_CLASS = "ui-divider-top";
    public static final String ALIGN_BOTTOM_CLASS = "ui-divider-bottom";
}
