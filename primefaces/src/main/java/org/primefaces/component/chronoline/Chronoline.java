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
package org.primefaces.component.chronoline;

import javax.faces.application.ResourceDependency;

@ResourceDependency(library = "primefaces", name = "components.css")
public class Chronoline extends ChronolineBase {

    public static final String COMPONENT_TYPE = "org.primefaces.component.Chronoline";

    public static final String STYLE_CLASS = "ui-chronoline ui-widget";
    public static final String ALIGN_LEFT_CLASS = "ui-chronoline-left";
    public static final String ALIGN_RIGHT_CLASS = "ui-chronoline-right";
    public static final String ALIGN_TOP_CLASS = "ui-chronoline-top";
    public static final String ALIGN_BOTTOM_CLASS = "ui-chronoline-bottom";
    public static final String ALIGN_ALTERNATE_CLASS = "ui-chronoline-alternate";
    public static final String LAYOUT_VERTICAL_CLASS = "ui-chronoline-vertical";
    public static final String LAYOUT_HORIZONTAL_CLASS = "ui-chronoline-horizontal";
    public static final String EVENT_CLASS = "ui-chronoline-event";
    public static final String EVENT_OPPOSITE_CLASS = "ui-chronoline-event-opposite";
    public static final String EVENT_CONTENT_CLASS = "ui-chronoline-event-content";
    public static final String EVENT_SEPARATOR_CLASS = "ui-chronoline-event-separator";
    public static final String EVENT_MARKER_CLASS = "ui-chronoline-event-marker";
    public static final String EVENT_CONNECTOR_CLASS = "ui-chronoline-event-connector";
}