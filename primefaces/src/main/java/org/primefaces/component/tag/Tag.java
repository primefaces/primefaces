/*
 * The MIT License
 *
 * Copyright (c) 2009-2026 PrimeFaces
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
package org.primefaces.component.tag;

import org.primefaces.cdk.api.FacesComponentInfo;

import jakarta.faces.application.ResourceDependency;
import jakarta.faces.component.FacesComponent;

@FacesComponent(value = Tag.COMPONENT_TYPE, namespace = Tag.COMPONENT_FAMILY)
@FacesComponentInfo(description = "Tag component is used to categorize content.")
@ResourceDependency(library = "primefaces", name = "components.css")
public class Tag extends TagBaseImpl {
    public static final String COMPONENT_TYPE = "org.primefaces.component.Tag";

    public static final String STYLE_CLASS = "p-tag p-widget";
    public static final String SEVERITY_INFO_CLASS = "p-tag-info";
    public static final String SEVERITY_SUCCESS_CLASS = "p-tag-success";
    public static final String SEVERITY_WARNING_CLASS = "p-tag-warning";
    public static final String SEVERITY_DANGER_CLASS = "p-tag-danger";
    public static final String SEVERITY_SECONDARY_CLASS = "p-tag-secondary";
    public static final String SEVERITY_HELP_CLASS = "p-tag-help";
    public static final String ROUNDED_CLASS = "p-tag-rounded";
    public static final String OUTLINED_CLASS = "p-tag-outlined";
    public static final String ICON_CLASS = "p-tag-icon";
    public static final String VALUE_CLASS = "p-tag-value";
}
