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
package org.primefaces.component.defaultcommand;

import java.io.IOException;
import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.WidgetBuilder;

public class DefaultCommandRenderer extends CoreRenderer {
    
    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        DefaultCommand command = (DefaultCommand) component;
        
        String scope = command.getScope();
        UIComponent target = command.findComponent(command.getTarget());
        if(target == null) {
            throw new FacesException("Cannot find component \"" + command.getTarget() + "\" in view.");
        }
        
        String clientId = command.getClientId(context);
        WidgetBuilder wb = getWidgetBuilder(context);
        wb.widget("DefaultCommand", command.resolveWidgetVar(), clientId, true)
                .attr("target", target.getClientId(context));
        
        if(scope != null) {
            UIComponent scopeComponent = command.findComponent(scope);
            if(scopeComponent == null) {
                throw new FacesException("Cannot find component \"" + scope + "\" in view.");
            }
            
            wb.attr("scope", scopeComponent.getClientId(context));
        }

        startScript(writer, clientId);
        writer.write(wb.build());
        endScript(writer);
    }
}
