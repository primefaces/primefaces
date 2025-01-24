/*
 * The MIT License
 *
 * Copyright (c) 2009-2025 PrimeTek Informatics
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
package org.primefaces.renderkit;

import org.primefaces.context.PrimeApplicationContext;
import org.primefaces.util.Constants;
import org.primefaces.util.HTML;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.FactoryFinder;
import javax.faces.application.Application;
import javax.faces.application.ViewHandler;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.RenderKit;
import javax.faces.render.RenderKitFactory;

public class RendererUtils {

    public static final String SCRIPT_TYPE = "text/javascript";

    private static final Logger LOGGER = Logger.getLogger(RendererUtils.class.getName());

    private static Method methodViewRootGetDoctype = null;
    private static Method methodDoctypeGetRootElement = null;
    private static Method methodDoctypeGetPublic = null;
    private static Method methodDoctypeGetSystem = null;

    private RendererUtils() {
        // Hide constructor
    }

    public static void encodeCheckbox(FacesContext context, boolean checked) throws IOException {
        encodeCheckbox(context, checked, false, false, null);
    }

    public static void encodeCheckbox(FacesContext context, boolean checked, boolean partialSelected, boolean disabled, String styleClass) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String icon;
        String boxClass = disabled ? HTML.CHECKBOX_BOX_CLASS + " ui-state-disabled" : HTML.CHECKBOX_BOX_CLASS;
        boxClass += checked ? " ui-state-active" : Constants.EMPTY_STRING;
        String containerClass = (styleClass == null) ? HTML.CHECKBOX_CLASS : HTML.CHECKBOX_CLASS + " " + styleClass;

        if (checked) {
            icon = HTML.CHECKBOX_CHECKED_ICON_CLASS;
        }
        else if (partialSelected) {
            icon = HTML.CHECKBOX_PARTIAL_CHECKED_ICON_CLASS;
        }
        else {
            icon = HTML.CHECKBOX_UNCHECKED_ICON_CLASS;
        }

        writer.startElement("div", null);
        writer.writeAttribute("class", containerClass, null);

        writer.startElement("div", null);
        writer.writeAttribute("class", boxClass, null);

        writer.startElement("span", null);
        writer.writeAttribute("class", icon, null);
        writer.endElement("span");

        writer.endElement("div");

        writer.endElement("div");
    }

    /**
     * Duplicate code from OmniFaces project under apache license:
     * <a href="https://github.com/omnifaces/omnifaces/blob/master/license.txt">https://github.com/omnifaces/omnifaces/blob/master/license.txt</a>
     *
     * Returns the {@link RenderKit} associated with the "current" view ID or view handler.
     * <p>
     * The current view ID is the view ID that's set for the view root that's associated with the current faces context.
     * Or if there is none, then the current view handler will be assumed, which is the view handler that's associated
     * with the requested view.
     *
     * @return The {@link RenderKit} associated with the "current" view ID or view handler.
     * @throws NullPointerException When faces context is unavailable.
     * @see <a href="https://github.com/omnifaces/omnifaces">Omnifaces</a>
     */
    public static RenderKit getRenderKit(FacesContext context) {
        String renderKitId = null;
        UIViewRoot view = context.getViewRoot();

        if (view != null) {
            renderKitId = view.getRenderKitId();
        }

        if (renderKitId == null) {
            Application application = context.getApplication();
            ViewHandler viewHandler = application.getViewHandler();

            if (viewHandler != null) {
                renderKitId = viewHandler.calculateRenderKitId(context);
            }

            if (renderKitId == null) {
                renderKitId = application.getDefaultRenderKitId();

                if (renderKitId == null) {
                    renderKitId = RenderKitFactory.HTML_BASIC_RENDER_KIT;
                }
            }
        }

        return ((RenderKitFactory) FactoryFinder.getFactory(FactoryFinder.RENDER_KIT_FACTORY)).getRenderKit(context, renderKitId);
    }

    /**
     * HTML5 Doctype does not require the script type on JavaScript files.
     *
     * @param context the FacesContext
     * @throws IOException if any error occurs
     */
    public static void encodeScriptTypeIfNecessary(FacesContext context) throws IOException {
        if (isOutputHtml5Doctype(context)) {
            return;
        }
        ResponseWriter writer = context.getResponseWriter();
        writer.writeAttribute("type", SCRIPT_TYPE, null);
    }

    /**
     * Returns <code>true</code> if the view root associated with the given {@link FacesContext}
     * will be rendered with a HTML5 doctype.
     *
     * @param context Involved faces context.
     * @return <code>true</code> if the view root associated with the given faces context
     *      will be rendered with a HTML5 doctype.
     */
    public static boolean isOutputHtml5Doctype(FacesContext context) {
        PrimeApplicationContext applicationContext = PrimeApplicationContext.getCurrentInstance(context);
        String html5ComplianceSetting = applicationContext.getConfig().getHtml5Compliance();

        if ("true".equalsIgnoreCase(html5ComplianceSetting)) {
            return true;
        }

        if ("auto".equalsIgnoreCase(html5ComplianceSetting)) {
            UIViewRoot viewRoot = context.getViewRoot();
            if (viewRoot == null) {
                return false;
            }

            if (!PrimeApplicationContext.getCurrentInstance(context).getEnvironment().isAtLeastJsf40()) {
                return false;
            }

            try {
                if (methodViewRootGetDoctype == null) {
                    methodViewRootGetDoctype = viewRoot.getClass().getMethod("getDoctype");
                }
                Object doctype = methodViewRootGetDoctype.invoke(viewRoot);
                if (doctype == null) {
                    return false;
                }

                if (methodDoctypeGetSystem == null) {
                    methodDoctypeGetRootElement = doctype.getClass().getMethod("getRootElement");
                    methodDoctypeGetPublic = doctype.getClass().getMethod("getPublic");
                    methodDoctypeGetSystem = doctype.getClass().getMethod("getSystem");
                }

                String rootElement = (String) methodDoctypeGetRootElement.invoke(doctype);
                String publicVal = (String) methodDoctypeGetPublic.invoke(doctype);
                String system = (String) methodDoctypeGetSystem.invoke(doctype);

                return "html".equalsIgnoreCase(rootElement)
                        && publicVal == null
                        && system == null;
            }
            catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Could not detect Doctype of current view!", e);
            }
        }

        return false;
    }
}
