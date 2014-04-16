/*
 * Copyright 2009-2014 PrimeTek.
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
package org.primefaces.mobile.renderkit;

import java.io.OutputStream;
import java.io.Writer;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import javax.faces.FactoryFinder;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseStream;
import javax.faces.context.ResponseWriter;
import javax.faces.render.ClientBehaviorRenderer;
import javax.faces.render.RenderKit;
import javax.faces.render.RenderKitFactory;
import javax.faces.render.Renderer;
import javax.faces.render.ResponseStateManager;

public class MobileRenderKit extends RenderKit {

    public final static String RENDER_KIT_ID = "PRIMEFACES_MOBILE";
        
    private RenderKit defaultRenderKit = null;
      
    private ConcurrentHashMap<String, HashMap<String, Renderer>> rendererFamilies;

    public MobileRenderKit() {
        rendererFamilies = new ConcurrentHashMap<String, HashMap<String, Renderer>>();
    }

    @Override
    public void addRenderer(String family, String rendererType, Renderer renderer) {
        HashMap<String,Renderer> renderers = rendererFamilies.get(family);
        if(renderers == null) {
            renderers = new HashMap<String,Renderer>();
            rendererFamilies.put(family, renderers);
        }

        renderers.put(rendererType, renderer);
    }

    @Override
    public Renderer getRenderer(String family, String rendererType) {
        Renderer renderer = null;
        HashMap<String,Renderer> renderers = rendererFamilies.get(family);
        
        //Look in our renderers first
        if(renderers != null) {
            renderer = renderers.get(rendererType);
        }
        
        //Then default renderers
        if(renderer == null) {
            renderer = getDefaultRenderKit().getRenderer(family, rendererType);
        }
        
        return renderer;
    }
    
    @Override
    public Iterator<String> getRendererTypes(String componentFamily) {
        Map<String,Renderer> family = rendererFamilies.get(componentFamily);
        
        if(family != null) {
            return family.keySet().iterator();
        } 
        else {
            Set<String> empty = Collections.emptySet();
            return empty.iterator();
        }
    }

    @Override
    public void addClientBehaviorRenderer(String type, ClientBehaviorRenderer renderer) {
        getDefaultRenderKit().addClientBehaviorRenderer(type, renderer);
    }

    @Override
    public ClientBehaviorRenderer getClientBehaviorRenderer(String type) {
        return getDefaultRenderKit().getClientBehaviorRenderer(type);
    }

    @Override
    public Iterator<String> getClientBehaviorRendererTypes() {
        return getDefaultRenderKit().getClientBehaviorRendererTypes();
    }

    @Override
    public Iterator<String> getComponentFamilies() {
        return rendererFamilies.keySet().iterator();
    }

    @Override
    public ResponseStateManager getResponseStateManager() {
        return getDefaultRenderKit().getResponseStateManager();
    }

    @Override
    public ResponseWriter createResponseWriter(Writer writer, String contentTypeList, String encoding) {
        return getDefaultRenderKit().createResponseWriter(writer, contentTypeList, encoding);
    }

    @Override
    public ResponseStream createResponseStream(OutputStream out) {
        return getDefaultRenderKit().createResponseStream(out);
    }
    
    private RenderKit getDefaultRenderKit() {
        if(defaultRenderKit == null) {
            RenderKitFactory renderKitFactory = (RenderKitFactory) FactoryFinder.getFactory(FactoryFinder.RENDER_KIT_FACTORY);
            FacesContext facesContext = FacesContext.getCurrentInstance();
            
            defaultRenderKit = renderKitFactory.getRenderKit(facesContext, RenderKitFactory.HTML_BASIC_RENDER_KIT);
        }

        return defaultRenderKit;
    }
}
