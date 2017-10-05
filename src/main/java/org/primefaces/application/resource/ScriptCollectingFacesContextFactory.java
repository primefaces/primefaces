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
package org.primefaces.application.resource;

import javax.faces.FacesException;
import javax.faces.context.FacesContext;
import javax.faces.context.FacesContextFactory;
import javax.faces.lifecycle.Lifecycle;

public class ScriptCollectingFacesContextFactory extends FacesContextFactory {

    private FacesContextFactory wrapped;

    // #6212 - don't remove it
    public ScriptCollectingFacesContextFactory() {

    }

    public ScriptCollectingFacesContextFactory(FacesContextFactory wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public FacesContext getFacesContext(Object context, Object request, Object response, Lifecycle lifecycle)
        throws FacesException {
        
        FacesContext wrappedContext = wrapped.getFacesContext(context, request, response, lifecycle);

        if (wrappedContext instanceof ScriptCollectingFacesContext) {
            return wrappedContext;
        }

        return new ScriptCollectingFacesContext(wrappedContext);
    }

    @Override
    public FacesContextFactory getWrapped() {
        return wrapped;
    }
}

