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

import javax.faces.FacesException;
import javax.faces.context.FacesContext;
import javax.faces.context.FacesContextFactory;
import javax.faces.lifecycle.Lifecycle;

/**
 * {@link FacesContextFactory} to wrap the {@link FacesContext} with our {@link PrimeFacesContext}.
 */
public class PrimeFacesContextFactory extends FacesContextFactory {

    private FacesContextFactory wrapped;

    // #6212 - don't remove it
    @SuppressWarnings("deprecation") // the default constructor is deprecated in JSF 2.3
    public PrimeFacesContextFactory() {

    }

    @SuppressWarnings("deprecation") // the default constructor is deprecated in JSF 2.3
    public PrimeFacesContextFactory(FacesContextFactory wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public FacesContext getFacesContext(Object context, Object request, Object response, Lifecycle lifecycle)
            throws FacesException {

        FacesContext wrappedContext = wrapped.getFacesContext(context, request, response, lifecycle);

        if (wrappedContext instanceof PrimeFacesContext) {
            return wrappedContext;
        }

        return new PrimeFacesContext(wrappedContext);
    }

    @Override
    public FacesContextFactory getWrapped() {
        return wrapped;
    }
}
