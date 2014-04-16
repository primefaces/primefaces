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
package org.primefaces.component.fragment;

import java.io.IOException;
import java.util.Map;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import org.primefaces.context.RequestContext;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.Constants;

public class FragmentRenderer extends CoreRenderer {
    
    @Override
	public void encodeBegin(FacesContext context, UIComponent component) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
        Fragment fragment = (Fragment) component;
        String clientId = fragment.getClientId(context);
        Map<Object,Object> attrs = RequestContext.getCurrentInstance().getAttributes();
        attrs.put(Constants.FRAGMENT_ID, clientId);
        
        if(fragment.isAutoUpdate()) {
            attrs.put(Constants.FRAGMENT_AUTO_RENDERED, true);
        }
        
        writer.startElement("div", component);
        writer.writeAttribute("id", clientId, "id");
	}
    
    @Override
	public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
		context.getResponseWriter().endElement("div");
        
        RequestContext.getCurrentInstance().getAttributes().remove(Constants.FRAGMENT_ID);
	}
}
