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
package org.primefaces.util;

import org.primefaces.context.PrimeApplicationContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import jakarta.faces.context.FacesContext;

public final class ThemeUtils {

    public static final String THEMING_LIBRARY = "primefaces-theming";
    public static final String DEFAULT_THEME = "saga-blue";
    public static final String COMPONENTS = "components";

    private static final String TOKEN_THEME_ATTRIBUTE = ThemeUtils.class.getName() + ".TOKEN_THEME";
    private static final Map<String, Boolean> TOKEN_THEMES = new ConcurrentHashMap<>();

    private ThemeUtils() {
        // NOOP
    }

    /**
     * @return the evaluated primefaces.THEME context param, the default theme if not set
     */
    public static String resolveTheme(FacesContext context) {
        String themeParamValue = PrimeApplicationContext.getCurrentInstance(context).getConfig().getTheme();
        if (themeParamValue == null) {
            return DEFAULT_THEME;
        }
        return context.getApplication().evaluateExpressionGet(context, themeParamValue, String.class);
    }

    /**
     * A token based theme is either a theme shipped by primefaces-theming (e.g. "aura" resolves to primefaces-theming:aura.css)
     * or a custom one given as "library:name" (e.g. "mytheme:mytheme.css"). Both only contain tokens and are combined with
     * primefaces-theming:components.css.
     */
    public static boolean isTokenTheme(FacesContext context, String theme) {
        if (theme == null || "none".equals(theme) || COMPONENTS.equals(theme)) {
            return false;
        }
        if (theme.indexOf(':') > 0) {
            return true;
        }
        return TOKEN_THEMES.computeIfAbsent(theme,
                t -> context.getApplication().getResourceHandler().createResource(t + ".css", THEMING_LIBRARY) != null);
    }

    /**
     * @return true if the theme of the current request is a token based theme (evaluated once per request)
     */
    public static boolean isTokenThemeActive(FacesContext context) {
        Map<Object, Object> attributes = context.getAttributes();
        Boolean active = (Boolean) attributes.get(TOKEN_THEME_ATTRIBUTE);
        if (active == null) {
            active = isTokenTheme(context, resolveTheme(context));
            attributes.put(TOKEN_THEME_ATTRIBUTE, active);
        }
        return active;
    }

    /**
     * A stylesheet of the PrimeFaces core library (component CSS), replaced by a token based theme.
     * PrimeIcons are not part of the theme.
     */
    public static boolean isCoreStyleSheet(String resourceName, String libraryName) {
        return Constants.LIBRARY.equals(libraryName)
                && resourceName != null
                && resourceName.endsWith(".css")
                && !resourceName.startsWith("primeicons/");
    }
}
