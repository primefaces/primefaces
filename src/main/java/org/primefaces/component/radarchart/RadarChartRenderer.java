/**
 * The MIT License
 *
 * Copyright (c) 2009-2019 PrimeTek
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
package org.primefaces.component.radarchart;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.component.charts.ChartRenderer;
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
        wb.init("RadarChart", chart.resolveWidgetVar(context), clientId);

        encodeConfig(context, chart.getModel());
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
        encodeLegend(context, radarOptions.getLegend(), (radarOptions.getScales() != null
                    || radarOptions.getElements() != null || radarOptions.getTitle() != null || radarOptions.getTooltip() != null));

        writer.write("}");
    }
}
