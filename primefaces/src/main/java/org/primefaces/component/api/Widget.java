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
package org.primefaces.component.api;

import org.primefaces.cdk.api.Property;
import org.primefaces.util.LangUtils;

import java.util.regex.Pattern;

import jakarta.faces.component.UIComponent;
import jakarta.faces.component.UINamingContainer;
import jakarta.faces.context.FacesContext;

public interface Widget {

    String CALLBACK_PRE_CONSTRUCT = "widgetPreConstruct";
    String CALLBACK_POST_CONSTRUCT = "widgetPostConstruct";
    String CALLBACK_POST_REFRESH = "widgetPostRefresh";
    String CALLBACK_PRE_DESTROY = "widgetPreDestroy";

    String ATTR_WIDGET_VAR_PATTERN = Widget.class.getName() + ".ATTR_WIDGET_VAR_PATTERN";

    @Property(description = "Name of the client side widget.")
    String getWidgetVar();

    // backwards compatibility
    default String resolveWidgetVar() {
        return resolveWidgetVar(FacesContext.getCurrentInstance());
    }
    /**
     * Resolves the widget variable name for the component.
     *
     * @param context The FacesContext for the current request.
     * @return The resolved widget variable name.
     */
    default String resolveWidgetVar(FacesContext context) {
        UIComponent component = (UIComponent) this;

        // Check if a custom widget variable name is set
        String userWidgetVar = getWidgetVar();
        if (LangUtils.isNotBlank(userWidgetVar)) {
            return userWidgetVar;
        }

        // Get or create the pattern for widget variable name sanitization
        Pattern pattern = (Pattern) context.getAttributes().get(ATTR_WIDGET_VAR_PATTERN);
        if (pattern == null) {
            // Create a pattern to replace naming container separator with '_'
            pattern = Pattern.compile("-|" + UINamingContainer.getSeparatorChar(context));
            context.getAttributes().put(ATTR_WIDGET_VAR_PATTERN, pattern);
        }

        // Generate a default widget variable name based on the component's client ID
        String widgetVar = "widget_" + component.getClientId(context);
        // Sanitize the widget variable name by replacing invalid characters with '_'
        return pattern.matcher(widgetVar).replaceAll("_");
    }
}
