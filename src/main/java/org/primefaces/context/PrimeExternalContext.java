/*
 * Copyright 2009-2015 PrimeTek.
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

import java.lang.reflect.Method;
import javax.faces.FacesException;
import javax.faces.context.ExternalContext;
import javax.faces.context.ExternalContextWrapper;
import javax.servlet.http.HttpServletRequest;

public class PrimeExternalContext extends ExternalContextWrapper {

    private ExternalContext wrapped;
    private HttpServletRequest httpServletRequest;
    
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
            try {
                Class<?> portletRequestClass = Class.forName("javax.portlet.PortletRequest");
                Class<?> portalUtilClass = Class.forName("com.liferay.portal.util.PortalUtil");
                Method method = portalUtilClass.getMethod("getHttpServletRequest", new Class[] { portletRequestClass });
                httpServletRequest = (HttpServletRequest) method.invoke(null, new Object[] { request });
            }
            catch (Exception ex) {
                throw new FacesException(ex);
            }
        }
    }
    
    protected boolean isLiferay() {
        try {
            Class.forName("com.liferay.portal.util.PortalUtil");
            return true;
        }
        catch (ClassNotFoundException e) {
            return false;
        }
    }
}
