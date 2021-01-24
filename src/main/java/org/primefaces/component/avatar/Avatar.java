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
package org.primefaces.component.avatar;

import javax.faces.application.ResourceDependency;

@ResourceDependency(library = "primefaces", name = "components.css")
public class Avatar extends AvatarBase {
    public static final String COMPONENT_TYPE = "org.primefaces.component.Avatar";

    public static final String STYLE_CLASS = "ui-avatar ui-widget";
    public static final String IMAGE_CLASS = "ui-avatar-image";
    public static final String CIRCLE_CLASS = "ui-avatar-circle";
    public static final String SIZE_LARGE_CLASS = "ui-avatar-lg";
    public static final String SIZE_XLARGE_CLASS = "ui-avatar-xl";
    public static final String SIZE_TEXT_CLASS = "ui-avatar-text";
    public static final String ICON_CLASS = "ui-avatar-icon";
}
