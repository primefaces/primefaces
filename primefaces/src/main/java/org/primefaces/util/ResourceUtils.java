/*
 * The MIT License
 *
 * Copyright (c) 2009-2023 PrimeTek Informatics
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

import java.io.*;
import java.util.*;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.el.ValueExpression;
import javax.faces.FacesException;
import javax.faces.application.Application;
import javax.faces.application.Resource;
import javax.faces.application.ResourceHandler;
import javax.faces.component.UIComponent;
import javax.faces.component.UIOutput;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.primefaces.context.PrimeApplicationContext;
import org.primefaces.context.PrimeRequestContext;

public class ResourceUtils {

    public static final String RENDERER_SCRIPT = "javax.faces.resource.Script";
    public static final String RENDERER_STYLESHEET = "javax.faces.resource.Stylesheet";
    public static final String RES_NOT_FOUND = "RES_NOT_FOUND";

    /**
     * Used to extract resource name (e.g "#{resource['picture.png'}")
     */
    private static final Pattern RESOURCE_PATTERN = Pattern.compile("^#\\{resource\\['(.+)']}$");

    private ResourceUtils() {
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
        try {
            return toBase64(context, resource.getInputStream(), resource.getContentType());
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
        try {
            try (ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {
                int nRead;
                byte[] data = new byte[16384];

                while ((nRead = is.read(data, 0, data.length)) != -1) {
                    buffer.write(data, 0, nRead);
                }

                buffer.flush();

                return buffer.toByteArray();
            }
        }
        catch (Exception e) {
            throw new FacesException("Could not read InputStream to byte[]", e);
        }
        finally {
            try {
                is.close();
            }
            catch (IOException ex) {
                // ignore
            }
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
        externalContext.setResponseHeader("Cache-Control", "no-cache, no-store, must-revalidate");
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

        if (requestContext.isSecure() && applicationContext.getConfig().isCookiesSecure()) {
            properties.put("secure", true);

            if (applicationContext.getEnvironment().isAtLeastJsf40()) {
                properties.put("SameSite", applicationContext.getConfig().getCookiesSameSite());
            }
        }

        context.getExternalContext().addResponseCookie(name, value, properties);
    }

    public static String appendCacheBuster(String url, boolean cache) {
        if (url == null) {
            return url;
        }
        url += url.contains("?") ? "&" : "?";
        url += Constants.DYNAMIC_CONTENT_CACHE_PARAM + "=" + cache;

        if (!cache) {
            url += "&uid=" + UUID.randomUUID().toString();
        }
        return url;
    }

    public static String getResourceRequestPath(FacesContext context, String resourceName) {
        Resource resource = context.getApplication().getResourceHandler().createResource(resourceName, "primefaces");
        return resource.getRequestPath();
    }

    public static void addComponentResource(FacesContext context, String name, String library, String target) {

        Application application = context.getApplication();

        UIComponent componentResource = application.createComponent(UIOutput.COMPONENT_TYPE);
        componentResource.setRendererType(application.getResourceHandler().getRendererTypeForResourceName(name));
        componentResource.getAttributes().put("name", name);
        componentResource.getAttributes().put("library", library);
        componentResource.getAttributes().put("target", target);

        context.getViewRoot().addComponentResource(context, componentResource, target);
    }

    public static void addComponentResource(FacesContext context, String name, String library) {
        addComponentResource(context, name, library, "head");
    }

    public static void addComponentResource(FacesContext context, String name) {
        addComponentResource(context, name, Constants.LIBRARY, "head");
    }

    public static boolean isScript(UIComponent component) {
        return RENDERER_SCRIPT.equals(component.getRendererType());
    }

    public static boolean isStylesheet(UIComponent component) {
        return RENDERER_STYLESHEET.equals(component.getRendererType());
    }

    public static List<ResourceInfo> getComponentResources(FacesContext context) {
        List<ResourceInfo> resourceInfos = new ArrayList<>();

        List<UIComponent> resources = context.getViewRoot().getComponentResources(context, "head");
        if (resources != null) {
            for (int i = 0; i < resources.size(); i++) {
                UIComponent resource = resources.get(i);
                ResourceUtils.ResourceInfo resourceInfo = newResourceInfo(resource);
                if (resourceInfo != null && !resourceInfos.contains(resourceInfo)) {
                    resourceInfos.add(resourceInfo);
                }
            }
        }

        return resourceInfos;
    }

    public static boolean isInline(ResourceInfo resourceInfo) {
        if (resourceInfo != null) {
            return LangUtils.isBlank(resourceInfo.getLibrary()) && LangUtils.isBlank(resourceInfo.getName());
        }

        return false;
    }

    public static ResourceInfo newResourceInfo(UIComponent component) {

        if (!(component instanceof UIOutput)) {
            return null;
        }

        String library = (String) component.getAttributes().get("library");
        String name = (String) component.getAttributes().get("name");

        return new ResourceInfo(library, name, component);
    }

    public static Resource newResource(ResourceInfo resourceInfo, FacesContext context) {
        Resource resource = context.getApplication().getResourceHandler().createResource(resourceInfo.getName(), resourceInfo.getLibrary());

        if (resource == null) {
            throw new FacesException("Resource '" + resourceInfo.getName() + "' in library '" + resourceInfo.getLibrary() + "' not found!");
        }

        return resource;
    }

    public static String getMonitorKeyCookieName(FacesContext context, ValueExpression monitorKey) {
        String monitorKeyCookieName = Constants.DOWNLOAD_COOKIE + context.getViewRoot().getViewId();
        monitorKeyCookieName = monitorKeyCookieName.replace('/', '_');
        // #9521 remove file extension like .xhtml or .jsf as it violates cookie naming rules
        monitorKeyCookieName = monitorKeyCookieName.substring(0, monitorKeyCookieName.lastIndexOf('.'));
        if (monitorKey != null) {
            String evaluated = (String) monitorKey.getValue(context.getELContext());
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
     * Per default the JSF implementation evaluates resource expressions as String and returns {@link Resource#getRequestPath()}.
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

    public static class ResourceInfo implements Serializable {

        private static final long serialVersionUID = 1L;

        private String library;
        private String name;
        private UIComponent resource;

        public ResourceInfo(String library, String name, UIComponent resource) {
            this.library = library;
            this.name = name;
            this.resource = resource;
        }

        public String getLibrary() {
            return library;
        }

        public void setLibrary(String library) {
            this.library = library;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public UIComponent getResource() {
            return resource;
        }

        public void setResource(UIComponent resource) {
            this.resource = resource;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ResourceInfo that = (ResourceInfo) o;
            return Objects.equals(library, that.library) &&
                    Objects.equals(name, that.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(library, name);
        }
    }
}
