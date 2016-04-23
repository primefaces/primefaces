/*
 * Copyright 2009-2016 PrimeTek.
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
package org.primefaces.renderkit;

import java.io.IOException;
import javax.faces.FacesWrapper;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.ConverterException;
import javax.faces.render.Renderer;
import org.primefaces.config.PrimeConfiguration;
import org.primefaces.context.RequestContext;
import org.primefaces.metadata.transformer.MetadataTransformerExecutor;

// Don't use the JSF 2.2 RendererWrapper, we need to support JSF 2.0 and 2.1
public class PrimeRendererWrapper extends Renderer implements FacesWrapper<Renderer> {

    private final Renderer wrapped;
    private boolean initialized = false;
    private PrimeConfiguration config;
    
    public PrimeRendererWrapper(Renderer wrapped) {
        this.wrapped = wrapped;
    }
    
    protected void lazyInit() {
        if (!initialized) {
            RequestContext requestContext = RequestContext.getCurrentInstance();
            config = requestContext.getApplicationContext().getConfig();

            initialized = true;
        }
    }
    
    @Override
    public String convertClientId(FacesContext context, String clientId) {
        return wrapped.convertClientId(context, clientId);
    }
    
    @Override
    public Object getConvertedValue(FacesContext context, UIComponent component, Object submittedValue) throws ConverterException {
        return wrapped.getConvertedValue(context, component, submittedValue);
    }

    @Override
    public void decode(FacesContext context, UIComponent component) {
        wrapped.decode(context, component);
    }

    @Override
    public void encodeBegin(FacesContext context, UIComponent component) throws IOException {
        
        lazyInit();

        MetadataTransformerExecutor.execute(config, component);

        wrapped.encodeBegin(context, component);
    }

    @Override
    public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
        wrapped.encodeChildren(context, component);
    }

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        wrapped.encodeEnd(context, component);
    }

    @Override
    public boolean getRendersChildren() {
        return wrapped.getRendersChildren();
    }
    
    @Override
    public Renderer getWrapped() {
        return wrapped;
    }
    
}
