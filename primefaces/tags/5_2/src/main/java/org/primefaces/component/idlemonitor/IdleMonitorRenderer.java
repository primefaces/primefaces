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
package org.primefaces.component.idlemonitor;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.WidgetBuilder;

public class IdleMonitorRenderer extends CoreRenderer {

    @Override
    public void decode(FacesContext context, UIComponent component) {
        decodeBehaviors(context, component);
    }

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        IdleMonitor monitor = (IdleMonitor) component;
        String clientId = monitor.getClientId(context);
        
        WidgetBuilder wb = getWidgetBuilder(context);
        wb.initWithDomReady("IdleMonitor", monitor.resolveWidgetVar(), clientId, "idlemonitor")
            .attr("timeout", monitor.getTimeout())
            .callback("onidle", "function()", monitor.getOnidle())
            .callback("onactive", "function()", monitor.getOnactive());
        
        encodeClientBehaviors(context, monitor);
        
        wb.finish();
    }
}