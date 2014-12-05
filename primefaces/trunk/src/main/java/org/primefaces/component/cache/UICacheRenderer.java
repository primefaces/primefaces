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
package org.primefaces.component.cache;

import java.io.IOException;
import java.io.StringWriter;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import org.primefaces.cache.CacheProvider;
import org.primefaces.context.RequestContext;
import org.primefaces.renderkit.CoreRenderer;

public class UICacheRenderer extends CoreRenderer {
    
    @Override
    public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
        UICache uiCache = (UICache) component;
        
        if(!uiCache.isDisabled()) {
            ResponseWriter writer = context.getResponseWriter();
            CacheProvider cacheProvider = RequestContext.getCurrentInstance().getApplicationContext().getCacheProvider();
            String key = uiCache.getKey();
            String region = uiCache.getRegion();
           
            if(key == null) {
                key = uiCache.getClientId(context);
            }
            
            if(region == null) {
                region = context.getViewRoot().getViewId();
            }
            
            String output = (String) cacheProvider.get(region, key);
            if(output == null) {
                StringWriter stringWriter = new StringWriter();
                ResponseWriter clonedWriter = writer.cloneWithWriter(stringWriter);
                context.setResponseWriter(clonedWriter);
                renderChildren(context, uiCache);
                
                output = stringWriter.getBuffer().toString();
                cacheProvider.put(region, key, output);
                context.setResponseWriter(writer);
                
                uiCache.setCacheSetInCurrentRequest(true);
            }
            
            writer.write(output);
        }
        else {
            renderChildren(context, uiCache);
        }
    }
    
    @Override
    public boolean getRendersChildren() {
        return true;
    }
}
