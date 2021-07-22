/*
 * The MIT License
 *
 * Copyright (c) 2009-2021 PrimeTek
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
package org.primefaces.component.donutchart;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.component.charts.ChartRenderer;
import org.primefaces.model.charts.donut.DonutChartOptions;
import org.primefaces.util.WidgetBuilder;

public class DonutChartRenderer extends ChartRenderer {

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        DonutChart chart = (DonutChart) component;
        String clientId = chart.getClientId(context);
        String style = chart.getStyle();
        String styleClass = chart.getStyleClass();

        encodeMarkup(context, clientId, style, styleClass);
        encodeScript(context, chart);
    }

    protected void encodeScript(FacesContext context, DonutChart chart) throws IOException {
        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("DonutChart", chart);

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

        DonutChartOptions donutOptions = (DonutChartOptions) options;

        writer.write(",options:{");

        writer.write("animation:{");
        writer.write("animateRotate:" + donutOptions.isAnimateRotate());
        writer.write(",animateScale:" + donutOptions.isAnimateScale());
        writer.write("}");

        if (donutOptions.getCutoutPercentage() != null) {
            writer.write(",cutoutPercentage:" + donutOptions.getCutoutPercentage());
        }

        if (donutOptions.getRotation() != null) {
            writer.write(",rotation:" + donutOptions.getRotation());
        }

        if (donutOptions.getCircumference() != null) {
            writer.write(",circumference:" + donutOptions.getCircumference());
        }

        encodeElements(context, donutOptions.getElements(), true);
        encodeTitle(context, donutOptions.getTitle(), true);
        encodeTooltip(context, donutOptions.getTooltip(), true);
        encodeLegend(context, donutOptions.getLegend(), true);
        encodeAnimation(context, donutOptions.getAnimation(), true);

        writer.write("}");
    }
}
