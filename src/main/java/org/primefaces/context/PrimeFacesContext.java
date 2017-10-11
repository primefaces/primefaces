/**
 * Copyright 2009-2017 PrimeTek.
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

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.FacesContextWrapper;
import javax.faces.context.ResponseWriter;
import org.primefaces.application.resource.CollectScriptsResponseWriter;
import org.primefaces.application.resource.CollectScriptsState;

/**
 * Custom {@link FacesContextWrapper} to init and release our {@link RequestContext}.
 */
public class PrimeFacesContext extends FacesContextWrapper {

    private final FacesContext wrapped;
    private final boolean collectScripts;

    private CollectScriptsState collectScriptsState;
    private PrimeExternalContext externalContext;

    public PrimeFacesContext(FacesContext wrapped) {
        this.wrapped = wrapped;

        RequestContext requestContext = new DefaultRequestContext(wrapped);
        RequestContext.setCurrentInstance(requestContext, wrapped);
        
        collectScripts = requestContext.getApplicationContext().getConfig().isCollectScripts();
        if (collectScripts) {
            collectScriptsState = new CollectScriptsState();
        }
    }

    @Override
    public ExternalContext getExternalContext() {
        if (externalContext == null) {
            externalContext = new PrimeExternalContext(wrapped.getExternalContext());
        }
        return externalContext;
    }

    @Override
    public void setResponseWriter(ResponseWriter writer) {
        if (!getPartialViewContext().isAjaxRequest() && collectScripts) {
            getWrapped().setResponseWriter(new CollectScriptsResponseWriter(writer, collectScriptsState));
        }
        else {
            getWrapped().setResponseWriter(writer);
        }
    }
    
    @Override
    public FacesContext getWrapped() {
        return wrapped;
    }

    @Override
    public void release() {
        RequestContext requestContext = RequestContext.getCurrentInstance(wrapped);
        if (requestContext != null) {
            requestContext.release();
        }

        super.release();
    }
}
