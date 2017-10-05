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

import javax.faces.context.FacesContext;
import javax.faces.context.FacesContextWrapper;
import javax.faces.context.ResponseWriter;

public class ScriptCollectingFacesContext extends FacesContextWrapper {

    private final FacesContext wrapped;
    private ScriptCollectingResponseWriter wrappedWriter;

    public ScriptCollectingFacesContext(FacesContext wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public FacesContext getWrapped() {
        return wrapped;
    }

    @Override
    public void setResponseWriter(ResponseWriter writer) {
        if (getPartialViewContext().isAjaxRequest()) {
            getWrapped().setResponseWriter(writer);
        }
        else {
            if (wrappedWriter == null) {
                wrappedWriter = new ScriptCollectingResponseWriter();
            }

            wrappedWriter.setWrapped(writer);
            getWrapped().setResponseWriter(wrappedWriter);
        }
    }
}
