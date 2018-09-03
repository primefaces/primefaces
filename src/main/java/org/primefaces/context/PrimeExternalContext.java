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

import javax.faces.context.ExternalContext;
import javax.faces.context.ExternalContextWrapper;
import javax.faces.context.FacesContext;

public class PrimeExternalContext extends ExternalContextWrapper {

    private ExternalContext wrapped;

    @SuppressWarnings("deprecation") // the default constructor is deprecated in JSF 2.3
    public PrimeExternalContext(ExternalContext wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public ExternalContext getWrapped() {
        return wrapped;
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
