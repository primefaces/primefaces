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
package org.primefaces.util;

import java.io.IOException;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import org.primefaces.config.PrimeConfiguration;

/**
 * Helper to generate scripts for widgets without HTML markup.
 */
public class ScriptOnlyWidgetBuilder extends WidgetBuilder {
    
    public ScriptOnlyWidgetBuilder(FacesContext context, PrimeConfiguration configuration) {
        super(context, configuration);
    }
    
    /**
     * The {@link WidgetBuilder#renderScriptBlock(java.lang.String)} renders the script tag with the clientId + "_s".
     * Therefore the script tag is automatically removed from the DOM via our BaseWidget on the client side.
     * If a widget is rendered without HTML markup, the script should be rendered without the additional "_s",
     * so that our BaseWidget does NOT remove it automatically.
     * This fixes #3265 and makes the component updatable without a parent container.
     * 
     * @param id The component clientId.
     * @throws IOException 
     */
    @Override
    protected void renderScriptBlock(String id) throws IOException {
        ResponseWriter rw = context.getResponseWriter();
        rw.startElement("script", null);
        rw.writeAttribute("id", id, null);
        rw.writeAttribute("type", "text/javascript", null);
    }    
}
