/*
 * The MIT License
 *
 * Copyright (c) 2009-2024 PrimeTek Informatics
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
package org.primefaces.model.dashboard;

import java.util.Arrays;
import java.util.Collection;

/**
 * @deprecated use DefaultDashboardWidget
 */
@Deprecated
public class DefaultDashboardColumn extends DefaultDashboardWidget {

    private static final long serialVersionUID = 1L;

    public DefaultDashboardColumn() {
        super();
    }

    public DefaultDashboardColumn(String widgetId, String styleClass) {
        super(widgetId, styleClass);
        getWidgets().addAll(Arrays.asList(widgetId));
        setStyleClass(styleClass);
    }

    public DefaultDashboardColumn(String style, String styleClass, Collection<String> widgets) {
        super(style, styleClass, widgets);
    }

    public DefaultDashboardColumn(Collection<String> widgets) {
        super(widgets);
    }

}
