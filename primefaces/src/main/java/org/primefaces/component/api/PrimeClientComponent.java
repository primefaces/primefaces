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
package org.primefaces.component.api;

import org.primefaces.cdk.api.Property;
import org.primefaces.util.LangUtils;

import java.util.regex.Pattern;

import jakarta.faces.component.UIComponent;
import jakarta.faces.component.UINamingContainer;
import jakarta.faces.context.FacesContext;

public interface PrimeClientComponent {

    String CALLBACK_PRE_CONSTRUCT = "clientPreConstruct";
    String CALLBACK_POST_CONSTRUCT = "clientPostConstruct";
    String CALLBACK_POST_REFRESH = "clientPostRefresh";
    String CALLBACK_PRE_DESTROY = "clientPreDestroy";

    String ATTR_REF_PATTERN = PrimeClientComponent.class.getName() + ".ATTR_REF_PATTERN";

    @Property(description = "Name of the client side component, e.g. to access it via PF('name').",
            implicitDefaultValue = "generated ('pf_' + componentClientId)")
    default String getRef() {
        return null;
    }

    /**
     * The attribute {@code widgetVar} is deprecated in favor of {@link #getRef()}, use {@link #resolveRef()} to get
     * the name of the client side component.
     *
     * @return the name of the client side component set via {@code widgetVar}
     */
    @Property(description = "Deprecated, use ref instead. Name of the client side component.")
    String getWidgetVar();

    default String resolveRef() {
        return resolveRef(FacesContext.getCurrentInstance());
    }

    /**
     * Resolves the name (ref) of the client side component.
     *
     * @param context The FacesContext for the current request.
     * @return The resolved name of the client side component.
     */
    default String resolveRef(FacesContext context) {
        UIComponent component = (UIComponent) this;

        // Check if a custom name is set, ref wins over the deprecated widgetVar
        String ref = getRef();
        if (LangUtils.isNotBlank(ref)) {
            return ref;
        }

        String widgetVar = getWidgetVar();
        if (LangUtils.isNotBlank(widgetVar)) {
            return widgetVar;
        }

        // Get or create the pattern for the sanitization of the generated name
        Pattern pattern = (Pattern) context.getAttributes().get(ATTR_REF_PATTERN);
        if (pattern == null) {
            // Create a pattern to replace naming container separator with '_'
            pattern = Pattern.compile("-|" + UINamingContainer.getSeparatorChar(context));
            context.getAttributes().put(ATTR_REF_PATTERN, pattern);
        }

        // Generate a default ref based on the component's client ID
        String generatedRef = "pf_" + component.getClientId(context);
        // Sanitize the ref by replacing invalid characters with '_'
        return pattern.matcher(generatedRef).replaceAll("_");
    }
}
