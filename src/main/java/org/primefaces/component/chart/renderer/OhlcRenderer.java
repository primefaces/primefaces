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
package org.primefaces.component.chart.renderer;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.component.chart.Chart;
import org.primefaces.model.chart.OhlcChartModel;
import org.primefaces.model.chart.OhlcChartSeries;

public class OhlcRenderer extends CartesianPlotRenderer {

    @Override
    protected void encodeData(FacesContext context, Chart chart) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        OhlcChartModel model = (OhlcChartModel) chart.getModel();
        List<OhlcChartSeries> data = model.getData();

        writer.write(",data:[[");
        for (Iterator<OhlcChartSeries> it = data.iterator(); it.hasNext(); ) {
            OhlcChartSeries s = it.next();
            writer.write("[");
            writer.write(escapeChartData(s.getValue()));
            writer.write(",");
            writer.write(escapeChartData(s.getOpen()));
            writer.write(",");
            writer.write(escapeChartData(s.getHigh()));
            writer.write(",");
            writer.write(escapeChartData(s.getLow()));
            writer.write(",");
            writer.write(escapeChartData(s.getClose()));
            writer.write("]");

            if (it.hasNext()) {
                writer.write(",");
            }
        }

        writer.write("]]");
    }

    @Override
    protected void encodeOptions(FacesContext context, Chart chart) throws IOException {
        super.encodeOptions(context, chart);

        ResponseWriter writer = context.getResponseWriter();
        OhlcChartModel model = (OhlcChartModel) chart.getModel();

        if (model.isCandleStick()) {
            writer.write(",candleStick:true");
        }

        if (model.isZoom()) {
            writer.write(",zoom:true");
        }

        if (model.isAnimate()) {
            writer.write(",animate:true");
        }

        if (model.isShowDatatip()) {
            writer.write(",datatip:true");
            if (model.getDatatipFormat() != null) {
                writer.write(",datatipFormat:\"" + model.getDatatipFormat() + "\"");
            }
        }
    }
}
