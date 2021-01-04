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
package org.primefaces.component.badge;

import javax.faces.application.ResourceDependency;

@ResourceDependency(library = "primefaces", name = "components.css")
public class Badge extends BadgeBase {
    public static final String COMPONENT_TYPE = "org.primefaces.component.Badge";
    public static final String DEFAULT_STYLE_CLASS = "ui-badge";
    public static final String OVERLAY_CLASS = "ui-overlay-badge";
    public static final String NO_GUTTER_CLASS = "ui-badge-no-gutter";
    public static final String DOT_CLASS = "ui-badge-dot";
    public static final String SIZE_LARGE_CLASS = "ui-badge-lg";
    public static final String SIZE_XLARGE_CLASS = "ui-badge-xl";
    public static final String SEVERITY_INFO_CLASS = "ui-badge-info";
    public static final String SEVERITY_SUCCESS_CLASS = "ui-badge-success";
    public static final String SEVERITY_WARNING_CLASS = "ui-badge-warning";
    public static final String SEVERITY_DANGER_CLASS = "ui-badge-danger";
}
