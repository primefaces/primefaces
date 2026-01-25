/*
 * The MIT License
 *
 * Copyright (c) 2009-2026 PrimeTek Informatics
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.primefaces.component.chart;

import org.primefaces.cdk.api.FacesComponentDescription;
import org.primefaces.event.ItemSelectEvent;

import java.util.Map;

import jakarta.faces.application.ResourceDependency;
import jakarta.faces.component.FacesComponent;
import jakarta.faces.event.AjaxBehaviorEvent;
import jakarta.faces.event.BehaviorEvent;
import jakarta.faces.event.FacesEvent;

@FacesComponent(value = Chart.COMPONENT_TYPE, namespace = Chart.COMPONENT_FAMILY)
@FacesComponentDescription("Chart.js component using raw JSON or XDev model.")
@ResourceDependency(library = "primefaces", name = "jquery/jquery.js")
@ResourceDependency(library = "primefaces", name = "core.js")
@ResourceDependency(library = "primefaces", name = "components.js")
@ResourceDependency(library = "primefaces", name = "moment/moment.js")
@ResourceDependency(library = "primefaces", name = "chart/chart.js")
public class Chart extends ChartBaseImpl {

    public static final String COMPONENT_TYPE = "org.primefaces.component.Chart";

    @Override
    public void queueEvent(FacesEvent event) {
        if (isAjaxBehaviorEvent(event, ClientBehaviorEventKeys.itemSelect)) {
            BehaviorEvent behaviorEvent = (AjaxBehaviorEvent) event;
            Map<String, String> map = getFacesContext().getExternalContext().getRequestParameterMap();
            int itemIndex = Integer.parseInt(map.get("itemIndex"));
            int dataSetIndex = Integer.parseInt(map.get("dataSetIndex"));
            String data = map.get("data");

            ItemSelectEvent itemSelectEvent = new ItemSelectEvent(this, behaviorEvent.getBehavior(), itemIndex, dataSetIndex, data);

            super.queueEvent(itemSelectEvent);
        }
        else {
            super.queueEvent(event);
        }
    }
}
