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
package org.primefaces.component.radarchart;

import java.io.IOException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import org.primefaces.component.charts.ChartRenderer;
import org.primefaces.model.charts.ChartModel;
import org.primefaces.model.charts.radar.RadarChartOptions;
import org.primefaces.util.WidgetBuilder;

public class RadarChartRenderer extends ChartRenderer {

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        RadarChart chart = (RadarChart) component;
        String clientId = chart.getClientId(context);
        String style = chart.getStyle();
        String styleClass = chart.getStyleClass();

        encodeMarkup(context, clientId, style, styleClass);
        encodeScript(context, chart);
    }

    protected void encodeScript(FacesContext context, RadarChart chart) throws IOException {
        String clientId = chart.getClientId(context);

        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("RadarChart", chart.resolveWidgetVar(), clientId);

        encodeConfig(context, (ChartModel) chart.getModel());
        encodeClientBehaviors(context, chart);

        wb.finish();
    }

    @Override
    protected void encodeOptions(FacesContext context, String type, Object options) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        if (options == null) {
            return;
        }

        RadarChartOptions radarOptions = (RadarChartOptions) options;

        writer.write(",\"options\":{");

        encodeScales(context, type, radarOptions.getScales(), false);
        encodeElements(context, radarOptions.getElements(), radarOptions.getScales() != null);
        encodeTitle(context, radarOptions.getTitle(), (radarOptions.getScales() != null || radarOptions.getElements() != null));
        encodeTooltip(context, radarOptions.getTooltip(), (radarOptions.getScales() != null
                || radarOptions.getElements() != null || radarOptions.getTitle() != null));

        writer.write("}");
    }
}
