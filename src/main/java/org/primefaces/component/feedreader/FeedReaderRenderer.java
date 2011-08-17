/*
 * Copyright 2009-2011 Prime Technology.
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

import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;
import java.io.IOException;
import java.net.URL;
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
        
        try {
            URL feedSource = new URL(reader.getValue());
            SyndFeedInput input = new SyndFeedInput();
            SyndFeed feed = input.build(new XmlReader(feedSource));

            for(Object f : feed.getEntries()) {
                SyndEntry entry = (SyndEntry) f;
                
                requestMap.put(var, entry);

                renderChildren(context, reader);
            }
            
            requestMap.remove(var);
            
        } catch(Exception e) {
            throw new FacesException(e);
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
