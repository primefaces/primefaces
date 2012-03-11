/*
 * Copyright 2009-2012 Prime Teknoloji.
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
package org.primefaces.component.feedreader;


import java.io.IOException;
import java.util.List;
import java.util.Map;
import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import org.primefaces.renderkit.CoreRenderer;

public class FeedReaderRenderer extends CoreRenderer {
    
    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        FeedReader reader = (FeedReader) component;
        Map<String,Object> requestMap = context.getExternalContext().getRequestMap();
        String var = reader.getVar();
        int size = reader.getSize();
        
        try {
            List entries = new FeedInput().parse(reader.getValue(), size);
            
            for(Object f : entries) {
                requestMap.put(var, f);
                renderChildren(context, reader);
            }
            
            requestMap.remove(var);
            
        } catch(Exception e) {
            UIComponent errorFacet = reader.getFacet("error");
            if(errorFacet != null) {
                errorFacet.encodeAll(context);
            }
        }
    }
    
    @Override
    public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
        //Rendering happens on encodeEnd
    }

    @Override
    public boolean getRendersChildren() {
        return true;
    }
}
