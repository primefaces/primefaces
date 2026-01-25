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
package org.primefaces.util;

import org.primefaces.context.PrimeApplicationContext;
import org.primefaces.context.PrimeRequestContext;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jakarta.el.ValueExpression;
import jakarta.faces.FacesException;
import jakarta.faces.application.Application;
import jakarta.faces.application.Resource;
import jakarta.faces.application.ResourceHandler;
import jakarta.faces.component.UIComponent;
import jakarta.faces.component.UIOutput;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;

public class ResourceUtils {

    public static final String RENDERER_SCRIPT = "jakarta.faces.resource.Script";
    public static final String RENDERER_STYLESHEET = "jakarta.faces.resource.Stylesheet";
    public static final String RES_NOT_FOUND = "RES_NOT_FOUND";

    /**
     * Used to extract resource name (e.g. "#{resource['picture.png'}")
     */
    private static final Pattern RESOURCE_PATTERN = Pattern.compile("^#\\{resource\\['(.+)']}$");

    private ResourceUtils() {
        // prevent instantiation
    }

    public static String getResourceURL(FacesContext context, String value) {
        if (LangUtils.isBlank(value)) {
            return Constants.EMPTY_STRING;
        }
        else if (value.contains(ResourceHandler.RESOURCE_IDENTIFIER)) {
            return value;
        }
        else {
            String url = context.getApplication().getViewHandler().getResourceURL(context, value);

            return context.getExternalContext().encodeResourceURL(url);
        }
    }

    public static String encodeResourceURL(FacesContext context, String src, boolean cache) {
        return context.getExternalContext().encodeResourceURL(ResourceUtils.appendCacheBuster(src, cache));
    }

    public static String toBase64(FacesContext context, Resource resource) {
        try (InputStream is = resource.getInputStream()) {
            return toBase64(context, is, resource.getContentType());
        }
        catch (IOException e) {
            throw new FacesException("Could not open InputStream from Resource[library=" + resource.getLibraryName()
                    + ", name=" + resource.getResourceName() + "]", e);
        }
    }

    public static String toBase64(FacesContext context, InputStream is) {
        return toBase64(context, toByteArray(is), null);
    }

    public static String toBase64(FacesContext context, Consumer<OutputStream> writer, String contentType) {
        return toBase64(context, toByteArray(writer), contentType);
    }

    public static String toBase64(FacesContext context, InputStream is, String contentType) {
        return toBase64(context, toByteArray(is), contentType);
    }

    public static String toBase64(FacesContext context, byte[] bytes) {
        return toBase64(context, bytes, null);
    }

    public static String toBase64(FacesContext context, byte[] bytes, String contentType) {
        String base64 = Base64.getEncoder().withoutPadding().encodeToString(bytes);
        if (contentType == null) {
            // try to guess content type from magic numbers
            if (base64.startsWith("R0lGOD")) {
                contentType = "image/gif";
            }
            else if (base64.startsWith("iVBORw")) {
                contentType = "image/png";
            }
            else if (base64.startsWith("/9j/")) {
                contentType = "image/jpeg";
            }
            else if (base64.startsWith("UklGR")) {
                contentType = "image/webp";
            }
        }
        return "data:" + contentType + ";base64," + base64;
    }

    public static byte[] toByteArray(Consumer<OutputStream> os) {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        os.accept(buffer);

        return buffer.toByteArray();
    }

    public static byte[] toByteArray(InputStream is) {
        if (is == null) {
            throw new IllegalArgumentException("InputStream must not be null");
        }
        try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[8192];
            int read;
            while ((read = is.read(buffer)) != -1) {
                output.write(buffer, 0, read);
            }

            return output.toByteArray();
        }
        catch (IOException e) {
            throw new FacesException("Could not read InputStream to byte[]", e);
        }
    }

    /**
     * Adds no cache pragma to the response to ensure it is not cached.  Dynamic downloads should always add this
     * to prevent caching and for GDPR.
     *
     * @param externalContext the ExternalContext we add the pragma to
     * @see <a href="https://github.com/primefaces/primefaces/issues/6359">FileDownload: configure Cache-Control</a>
     */
    public static void addNoCacheControl(ExternalContext externalContext) {
        addNoCacheControl(externalContext, false);
    }

    /**
     * Adds no cache pragma to the response to ensure it is not cached.  Dynamic downloads should always add this
     * to prevent caching and for GDPR.
     *
     * @param externalContext the ExternalContext we add the pragma to
     * @param store used to add no-store or exclude it if false
     * @see <a href="https://github.com/primefaces/primefaces/issues/6359">FileDownload: configure Cache-Control</a>
     */
    public static void addNoCacheControl(ExternalContext externalContext, boolean store) {
        String cacheControl = "no-cache, no-store, must-revalidate";
        if (store) {
            cacheControl = "no-cache, must-revalidate";
        }
        externalContext.setResponseHeader("Cache-Control", cacheControl);
        externalContext.setResponseHeader("Pragma", "no-cache");
        externalContext.setResponseHeader("Expires", "0");
    }

    /**
     * Adds the cookie represented by the arguments to the response. If the current HTTP conversation is secured
     * over SSL (e.g. https:) then the cookie is set to secure=true and sameSite=Strict.
     *
     * @param context the FacesContext contains the External context we add the cookie to
     * @param name To be passed as the first argument to the <code>Cookie</code> constructor.
     * @param value To be passed as the second argument to the <code>Cookie</code> constructor.
     * @param properties A <code>Map</code> containing key/value pairs to be passed
     * as arguments to the setter methods as described above.
     */
    public static void addResponseCookie(FacesContext context, String name, String value, Map<String, Object> properties) {
        if (properties == null) {
            properties = new HashMap<>(3);
        }

        PrimeRequestContext requestContext = PrimeRequestContext.getCurrentInstance(context);
        PrimeApplicationContext applicationContext = requestContext.getApplicationContext();

        boolean isSecure = requestContext.isSecure() && applicationContext.getConfig().isCookiesSecure();

        properties.put("secure", isSecure);

        String sameSite = applicationContext.getConfig().getCookiesSameSite();
        // "None" is only allowed when Secure attribute so default to Lax if unsecure
        if (LangUtils.isBlank(sameSite) || (!isSecure && "None".equalsIgnoreCase(sameSite))) {
            sameSite = "Lax";
        }
        properties.put("SameSite", sameSite);

        context.getExternalContext().addResponseCookie(name, value, properties);
    }

    public static String appendCacheBuster(String url, boolean cache) {
        if (url == null) {
            return url;
        }
        url += url.contains("?") ? "&" : "?";
        url += Constants.DYNAMIC_CONTENT_CACHE_PARAM + "=" + cache;

        if (!cache) {
            url += "&uid=" + UUID.randomUUID();
        }
        return url;
    }

    public static String getResourceRequestPath(FacesContext context, String resourceName) {
        Resource resource = context.getApplication().getResourceHandler().createResource(resourceName, "primefaces");
        return resource.getRequestPath();
    }

    public static boolean isScript(UIComponent component) {
        return RENDERER_SCRIPT.equals(component.getRendererType());
    }

    public static boolean isStylesheet(UIComponent component) {
        return RENDERER_STYLESHEET.equals(component.getRendererType());
    }

    public static String getResourceName(UIComponent component) {
        return (String) component.getAttributes().get("name");
    }

    public static String getResourceLibrary(UIComponent component) {
        return (String) component.getAttributes().get("library");
    }

    public static boolean isInline(UIComponent component) {
        if (component != null) {
            return LangUtils.isBlank(getResourceName(component))
                    && LangUtils.isBlank(getResourceLibrary(component));
        }

        return false;
    }

    public static String getMonitorKeyCookieName(FacesContext context, ValueExpression monitorKey) {
        String monitorKeyCookieName = Constants.DOWNLOAD_COOKIE + context.getViewRoot().getViewId();
        monitorKeyCookieName = monitorKeyCookieName.replace('/', '_');
        // #9521 remove file extension like .xhtml or .faces as it violates cookie naming rules
        monitorKeyCookieName = monitorKeyCookieName.substring(0, monitorKeyCookieName.lastIndexOf('.'));
        if (monitorKey != null) {
            String evaluated = monitorKey.getValue(context.getELContext());
            if (LangUtils.isNotBlank(evaluated)) {
                monitorKeyCookieName += "_" + evaluated;
            }
        }
        return monitorKeyCookieName;
    }


    public static boolean isResourceNotFound(Resource resource) {
        return resource != null && (RES_NOT_FOUND.equals(resource.toString()) || RES_NOT_FOUND.equals(resource.getRequestPath()));
    }

    /**
     * Per default the Faces implementation evaluates resource expressions as String and returns {@link Resource#getRequestPath()}.
     * This method resolves the expression to the {@link Resource} itself.
     *
     * @param facesContext The {@link FacesContext}
     * @param valueExpression The {@link ValueExpression}
     *
     * @return Null if the valueExpression is not of the form #{resource['path/to/resource']} or #{resource['library:name']}.
     * Otherwise the value obtained by {@link ResourceHandler#createResource(java.lang.String)}.
     */
    public static Resource evaluateResourceExpression(FacesContext facesContext, ValueExpression valueExpression) {
        Resource resource = null;

        if (valueExpression != null) {
            String expressionString = valueExpression.getExpressionString();

            Matcher matcher = RESOURCE_PATTERN.matcher(expressionString);
            if (matcher.find()) {
                String[] resourceInfo = matcher.group(1).split(":");

                Application application = facesContext.getApplication();
                ResourceHandler resourceHandler = application.getResourceHandler();

                if (resourceInfo.length == 2) {
                    String resourceLibrary = resourceInfo[0];
                    String resourceName = resourceInfo[1];
                    resource = resourceHandler.createResource(resourceName, resourceLibrary);
                }
                else {
                    String resourceName = resourceInfo[0];
                    resource = resourceHandler.createResource(resourceName);
                }
            }
        }

        return !isResourceNotFound(resource) ? resource : null;
    }

    /**
     * Adds a JavaScript resource to the view.
     *
     * @param context The FacesContext
     * @param libraryName The library name containing the resource
     * @param resourceName The name of the JavaScript resource
     */
    public static void addJavascriptResource(FacesContext context, String libraryName, String resourceName) {
        addResource(context, RENDERER_SCRIPT, libraryName, resourceName);
    }
    /**
     * Adds a JavaScript resource from the default PrimeFaces library to the view.
     *
     * @param context The FacesContext
     * @param resourceName The name of the JavaScript resource
     */
    public static void addJavascriptResource(FacesContext context, String resourceName) {
        addJavascriptResource(context, Constants.LIBRARY, resourceName);
    }

    /**
     * Adds a CSS resource to the view.
     *
     * @param context The FacesContext
     * @param libraryName The library name containing the resource
     * @param resourceName The name of the CSS resource
     */
    public static void addStyleSheetResource(FacesContext context, String libraryName, String resourceName) {
        addResource(context, RENDERER_STYLESHEET, libraryName, resourceName);
    }

    /**
     * Adds a CSS resource from the default PrimeFaces library to the view.
     *
     * @param context The FacesContext
     * @param resourceName The name of the CSS resource
     */
    public static void addStyleSheetResource(FacesContext context, String resourceName) {
        addStyleSheetResource(context, Constants.LIBRARY, resourceName);
    }

    /**
     * Adds a resource to the view if it hasn't been rendered yet.
     *
     * @param context The FacesContext
     * @param type The renderer type (CSS or JS)
     * @param libraryName The library name containing the resource
     * @param resourceName The name of the resource
     */
    public static void addResource(FacesContext context, String type, String libraryName, String resourceName) {
        boolean isRendered = context.getApplication().getResourceHandler().isResourceRendered(context, resourceName, libraryName);
        if (!isRendered) {
            addResourceToHead(context, type, libraryName, resourceName);
        }
    }

    /**
     * Adds a resource component to the specified target, avoiding duplicates based on ID.
     *
     * @param context The FacesContext
     * @param type The renderer type
     * @param libraryName The library name
     * @param resourceName The resource name
     * @param target The target location ("head" or "body")
     * @return The added or existing UIComponent
     */
    public static UIComponent addScriptResourceToTarget(FacesContext context, String type, String libraryName, String resourceName, String target) {
        UIOutput outputResource = createResource(type);

        if (libraryName != null) {
            outputResource.getAttributes().put("library", libraryName);
        }

        outputResource.getAttributes().put("name", resourceName);
        context.getViewRoot().addComponentResource(context, outputResource, target);
        return outputResource;
    }

    /**
     * Creates a new UIOutput component with the specified renderer type.
     *
     * @param type The renderer type
     * @return New UIOutput component
     */
    public static UIOutput createResource(String type) {
        UIOutput outputScript = new UIOutput();
        outputScript.setRendererType(type);
        return outputScript;
    }

    /**
     * Adds a resource to the head section.
     */
    public static void addResourceToHead(FacesContext context, String type, String libraryName, String resourceName) {
        addScriptResourceToTarget(context, type, libraryName, resourceName, "head");
    }

    /**
     * Adds a resource to the body section.
     */
    public static void addResourceToBody(FacesContext context, String type, String libraryName, String resourceName) {
        addScriptResourceToTarget(context, type, libraryName, resourceName, "body");
    }
}
