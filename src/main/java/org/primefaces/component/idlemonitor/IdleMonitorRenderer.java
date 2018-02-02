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
package org.primefaces.component.idlemonitor;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.WidgetBuilder;

public class IdleMonitorRenderer extends CoreRenderer {

    @Override
    public void decode(FacesContext context, UIComponent component) {
        decodeBehaviors(context, component);
    }

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        IdleMonitor idleMonitor = (IdleMonitor) component;
        String clientId = idleMonitor.getClientId(context);

        WidgetBuilder wb = getWidgetBuilder(context);
        wb.initWithDomReady("IdleMonitor", idleMonitor.resolveWidgetVar(), clientId)
                .attr("timeout", idleMonitor.getTimeout())
                .attr("multiWindowSupport", idleMonitor.isMultiWindowSupport())
                .attr("contextPath", context.getExternalContext().getRequestContextPath())
                .callback("onidle", "function()", idleMonitor.getOnidle())
                .callback("onactive", "function()", idleMonitor.getOnactive());

        encodeClientBehaviors(context, idleMonitor);

        wb.finish();
    }
}
