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
import java.util.logging.Logger;
import javax.faces.context.ExternalContext;
import javax.faces.context.ExternalContextWrapper;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import org.primefaces.util.Constants;

public class PrimeExternalContext extends ExternalContextWrapper {

    private final static Logger LOG = Logger.getLogger(PrimeExternalContext.class.getName());

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
            this.httpServletRequest = (HttpServletRequest) request;
        }
        else {

            // Support non-webapp environments (such as Liferay Portal) by allowing other libraries to provide a method
            // for extracting the HttpServletRequest from the request object.
            Method getHttpServletRequestMethod =
                    (Method) wrapped.getApplicationMap()
                            .get(Constants.CUSTOM_GET_HTTP_SERVLET_REQUEST_STATIC_METHOD);

            if (getHttpServletRequestMethod != null) {

                try {
                    this.httpServletRequest =
                            (HttpServletRequest) getHttpServletRequestMethod.invoke(null, new Object[]{request});
                }
                catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                    throw new RuntimeException("Failed to obtain HttpServletRequest with " +
                            getHttpServletRequestMethod.getDeclaringClass().getName() + "." +
                            getHttpServletRequestMethod.getName() + "(request).", e);
                }
            }
            else {
                this.httpServletRequest = null;
            }
        }
    }

    /**
     * @deprecated No replacement available.
     */
    @Deprecated
    protected boolean isLiferay() {

        LOG.warning("Deprecated isLiferay() method called. Returning false since Liferay detection is unnecessary.");
        return false;
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
}
