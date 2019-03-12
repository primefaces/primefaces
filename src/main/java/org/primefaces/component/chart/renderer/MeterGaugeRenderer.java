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

import org.primefaces.component.chart.Chart;
import org.primefaces.model.chart.MeterGaugeChartModel;
import org.primefaces.util.EscapeUtils;

import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

public class MeterGaugeRenderer extends BasePlotRenderer {

    @Override
    protected void encodeData(FacesContext context, Chart chart) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        MeterGaugeChartModel model = (MeterGaugeChartModel) chart.getModel();

        writer.write(",data:[[" + escapeChartData(model.getValue()) + "]]");
    }

    @Override
    protected void encodeOptions(FacesContext context, Chart chart) throws IOException {
        super.encodeOptions(context, chart);

        ResponseWriter writer = context.getResponseWriter();
        MeterGaugeChartModel model = (MeterGaugeChartModel) chart.getModel();
        String gaugeLabel = model.getGaugeLabel();
        double min = model.getMin();
        double max = model.getMax();
        Integer intervalInnerRadius = model.getIntervalInnerRadius();
        Integer intervalOuterRadius = model.getIntervalOuterRadius();

        encodeNumberList(context, "intervals", model.getIntervals());
        encodeNumberList(context, "ticks", model.getTicks());

        if (gaugeLabel != null) {
            writer.write(",gaugeLabel:\"" + EscapeUtils.forJavaScript(gaugeLabel) + "\"");
            writer.write(",gaugeLabelPosition:\"" + model.getGaugeLabelPosition() + "\"");
        }

        writer.write(",showTickLabels:" + model.isShowTickLabels());
        writer.write(",labelHeightAdjust:" + model.getLabelHeightAdjust());

        if (intervalInnerRadius != null) {
            writer.write(",intervalInnerRadius:" + intervalInnerRadius);
        }
        if (intervalOuterRadius != null) {
            writer.write(",intervalOuterRadius:" + intervalOuterRadius);
        }

        if (min != Double.MIN_VALUE) {
            writer.write(",min:" + min);
        }
        if (max != Double.MAX_VALUE) {
            writer.write(",max:" + max);
        }
    }

    private void encodeNumberList(FacesContext context, String name, List<Number> values) throws IOException {
        if (values != null) {
            ResponseWriter writer = context.getResponseWriter();

            writer.write("," + name + ":[");
            for (Iterator<Number> it = values.iterator(); it.hasNext(); ) {
                Number number = it.next();
                writer.write(number.toString());

                if (it.hasNext()) {
                    writer.write(",");
                }
            }
            writer.write("]");
        }
    }

}
