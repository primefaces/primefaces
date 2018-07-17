/**
 * Copyright 2009-2018 PrimeTek.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.primefaces.context;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import javax.faces.context.ExternalContext;
import javax.faces.context.ExternalContextWrapper;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

public class PrimeExternalContext extends ExternalContextWrapper {

    private ExternalContext wrapped;
    private HttpServletRequest httpServletRequest;

    @SuppressWarnings("deprecation") // the default constructor is deprecated in JSF 2.3
    public PrimeExternalContext(ExternalContext wrapped) {
        this.wrapped = wrapped;

        extractHttpServletRequest();
    }

    @Override
    public ExternalContext getWrapped() {
        return wrapped;
    }

    public String getRemoteAddr() {
        return httpServletRequest.getRemoteAddr();
    }

    protected void extractHttpServletRequest() {
        Object request = wrapped.getRequest();
        if (request instanceof HttpServletRequest) {
            httpServletRequest = (HttpServletRequest) request;
        }
        else if (isLiferay()) {
            httpServletRequest = LiferayUtil.getHttpServletRequest(request);
        }
    }

    protected boolean isLiferay() {
        return LiferayUtil.isLiferay();
    }

    public static PrimeExternalContext getCurrentInstance(FacesContext facesContext) {
        ExternalContext externalContext = facesContext.getExternalContext();

        while (externalContext != null) {
            if (externalContext instanceof PrimeExternalContext) {
                return (PrimeExternalContext) externalContext;
            }

            if (externalContext instanceof ExternalContextWrapper) {
                externalContext = ((ExternalContextWrapper) externalContext).getWrapped();
            }
            else {
                return null;
            }
        }

        return null;
    }

    private static final class LiferayUtil {

        // Class initialization is lazy and thread-safe. For more details on this pattern, see
        // http://stackoverflow.com/questions/7420504/threading-lazy-initialization-vs-static-lazy-initialization and
        // http://docs.oracle.com/javase/specs/jls/se7/html/jls-12.html#jls-12.4.2
        private static final Method GET_HTTP_SERVLET_REQUEST_METHOD;

        static {

            Method getHttpServletRequestMethod = null;
            String portalUtilClassName = "com.liferay.portal.kernel.util.PortalUtil";
            String getHttpServletRequestMethodName = "getHttpServletRequest";

            try {
                Class<?> portalUtilClass;

                try {

                    // Class name for legacy Liferay 6.2 and below support.
                    portalUtilClass = Class.forName("com.liferay.portal.util.PortalUtil");
                }
                catch (ClassNotFoundException | NoClassDefFoundError e) {
                    portalUtilClass = Class.forName(portalUtilClassName);
                }

                Class<?> portletRequestClass = Class.forName("javax.portlet.PortletRequest");
                getHttpServletRequestMethod =
                        portalUtilClass.getMethod(getHttpServletRequestMethodName, new Class[]{portletRequestClass});
            }
            catch (ClassNotFoundException | NoClassDefFoundError e) {
                // Do nothing.
            }
            catch (NoSuchMethodException | SecurityException e) {
                throw new RuntimeException("Liferay's " + portalUtilClassName + " was detected, but the " +
                        getHttpServletRequestMethodName + " method could not be obtained via reflection.", e);
            }

            GET_HTTP_SERVLET_REQUEST_METHOD = getHttpServletRequestMethod;
        }

        private LiferayUtil() {
            throw new AssertionError();
        }

        private static boolean isLiferay() {
            return GET_HTTP_SERVLET_REQUEST_METHOD != null;
        }

        private static HttpServletRequest getHttpServletRequest(Object request) {

            try {
                return (HttpServletRequest) GET_HTTP_SERVLET_REQUEST_METHOD.invoke(null, new Object[]{request});
            }
            catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                throw new RuntimeException("Failed to obtain HttpServletRequest in Liferay.", e);
            }
        }
    }
}
