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
package org.primefaces.component.chart.renderer;

import java.io.IOException;
import java.util.Iterator;

import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.component.chart.Chart;
import org.primefaces.model.chart.CartesianChartModel;
import org.primefaces.model.chart.ChartSeries;
import org.primefaces.model.chart.LineChartModel;

public class LineRenderer extends CartesianPlotRenderer {

    @Override
    protected void encodeData(FacesContext context, Chart chart) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        CartesianChartModel model = (CartesianChartModel) chart.getModel();

        writer.write(",data:[");
        for (Iterator<ChartSeries> it = model.getSeries().iterator(); it.hasNext(); ) {
            ChartSeries series = it.next();

            writer.write("[");
            for (Iterator<Object> x = series.getData().keySet().iterator(); x.hasNext(); ) {
                Object xValue = x.next();
                Number yValue = series.getData().get(xValue);
                String yValueAsString = (yValue == null) ? "null" : escapeChartData(yValue);
                String xValueAsString = escapeChartData(xValue);

                writer.write("[");
                writer.write(xValueAsString + "," + yValueAsString);
                writer.write("]");

                if (x.hasNext()) {
                    writer.write(",");
                }
            }
            writer.write("]");

            if (it.hasNext()) {
                writer.write(",");
            }
        }
        writer.write("]");
    }

    @Override
    protected void encodeOptions(FacesContext context, Chart chart) throws IOException {
        super.encodeOptions(context, chart);

        ResponseWriter writer = context.getResponseWriter();
        LineChartModel model = (LineChartModel) chart.getModel();

        writer.write(",series:[");
        for (Iterator<ChartSeries> it = model.getSeries().iterator(); it.hasNext(); ) {
            ChartSeries series = it.next();
            series.encode(writer);

            if (it.hasNext()) {
                writer.write(",");
            }
        }

        writer.write("]");

        if (model.isStacked()) {
            writer.write(",stackSeries:true");
        }
        if (model.isBreakOnNull()) {
            writer.write(",breakOnNull:true");
        }
        if (model.isZoom()) {
            writer.write(",zoom:true");
        }
        if (model.isAnimate()) {
            writer.write(",animate:true");
        }
        if (model.isShowPointLabels()) {
            writer.write(",showPointLabels:true");
        }

        if (model.isShowDatatip()) {
            writer.write(",datatip:true");
            if (model.getDatatipFormat() != null) {
                writer.write(",datatipFormat:\"" + model.getDatatipFormat() + "\"");
            }
        }
    }
}
