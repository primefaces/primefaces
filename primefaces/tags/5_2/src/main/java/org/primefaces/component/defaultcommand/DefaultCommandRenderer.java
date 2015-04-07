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
package org.primefaces.component.defaultcommand;

import java.io.IOException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.expression.SearchExpressionFacade;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.WidgetBuilder;

public class DefaultCommandRenderer extends CoreRenderer {
    
    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        DefaultCommand command = (DefaultCommand) component;

        UIComponent target = SearchExpressionFacade.resolveComponent(context, command, command.getTarget());
        
        String clientId = command.getClientId(context);
        WidgetBuilder wb = getWidgetBuilder(context);
        wb.initWithDomReady("DefaultCommand", command.resolveWidgetVar(), clientId)
                .attr("target", target.getClientId(context));
        
        String scope = command.getScope();
        if(scope != null) {
            UIComponent scopeComponent = SearchExpressionFacade.resolveComponent(context, command, scope);
            wb.attr("scope", scopeComponent.getClientId(context));
        }

        wb.finish();
    }
}
