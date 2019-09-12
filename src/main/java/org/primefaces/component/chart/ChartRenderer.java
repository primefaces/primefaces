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
package org.primefaces.component.chart;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.component.chart.renderer.*;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.WidgetBuilder;

public class ChartRenderer extends CoreRenderer {

    private static final String TYPE_PIE = "pie";
    private static final String TYPE_LINE = "line";
    private static final String TYPE_BAR = "bar";
    private static final String TYPE_OHLC = "ohlc";
    private static final String TYPE_DONUT = "donut";
    private static final String TYPE_BUBBLE = "bubble";
    private static final String TYPE_METERGAUGE = "metergauge";

    private static final Map<String, org.primefaces.component.chart.renderer.BasePlotRenderer> CHART_RENDERERS;

    static {
        CHART_RENDERERS = new HashMap<>();
        CHART_RENDERERS.put(TYPE_PIE, new PieRenderer());
        CHART_RENDERERS.put(TYPE_LINE, new LineRenderer());
        CHART_RENDERERS.put(TYPE_BAR, new BarRenderer());
        CHART_RENDERERS.put(TYPE_OHLC, new OhlcRenderer());
        CHART_RENDERERS.put(TYPE_DONUT, new DonutRenderer());
        CHART_RENDERERS.put(TYPE_BUBBLE, new BubbleRenderer());
        CHART_RENDERERS.put(TYPE_METERGAUGE, new MeterGaugeRenderer());
    }

    @Override
    public void decode(FacesContext context, UIComponent component) {
        super.decodeBehaviors(context, component);
    }

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        Chart chart = (Chart) component;

        encodeMarkup(context, chart);
        encodeScript(context, chart);
    }

    protected void encodeMarkup(FacesContext context, Chart chart) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String style = chart.getStyle();
        String styleClass = chart.getStyleClass();

        writer.startElement("div", null);
        writer.writeAttribute("id", chart.getClientId(context), null);
        if (style != null) {
            writer.writeAttribute("style", style, "style");
        }
        if (styleClass != null) {
            writer.writeAttribute("class", styleClass, "styleClass");
        }

        writer.endElement("div");
    }

    protected void encodeScript(FacesContext context, Chart chart) throws IOException {
        String type = chart.getType();
        BasePlotRenderer plotRenderer = CHART_RENDERERS.get(type);
        String clientId = chart.getClientId(context);

        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("Chart", chart.resolveWidgetVar(context), clientId)
                .attr("type", type);

        if (chart.isResponsive()) {
            wb.attr("responsive", true);
        }

        plotRenderer.render(context, chart);
        encodeClientBehaviors(context, chart);

        wb.finish();
    }
}
