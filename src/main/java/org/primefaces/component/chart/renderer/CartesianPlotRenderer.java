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
import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.CartesianChartModel;
import org.primefaces.util.EscapeUtils;

import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

public abstract class CartesianPlotRenderer extends BasePlotRenderer {

    @Override
    protected void encodeOptions(FacesContext context, Chart chart) throws IOException {
        super.encodeOptions(context, chart);

        ResponseWriter writer = context.getResponseWriter();
        CartesianChartModel model = (CartesianChartModel) chart.getModel();
        Map<AxisType, Axis> axes = model.getAxes();

        writer.write(",axes:{");
        for (Iterator<AxisType> it = axes.keySet().iterator(); it.hasNext(); ) {
            AxisType axisType = it.next();
            Axis axis = model.getAxes().get(axisType);

            encodeAxis(context, axisType, axis);

            if (it.hasNext()) {
                writer.write(",");
            }
        }
        writer.write("}");

        if (model.isShowDatatip()) {
            writer.write(",datatip:true");

            String datatipEditor = model.getDatatipEditor();
            if (model.getDatatipFormat() != null) {
                writer.write(",datatipFormat:\"" + model.getDatatipFormat() + "\"");
            }

            if (datatipEditor != null) {
                writer.write(",datatipEditor:" + datatipEditor);
            }
        }
    }

    protected void encodeAxis(FacesContext context, AxisType axisType, Axis axis) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String label = axis.getLabel();
        Object min = axis.getMin();
        Object max = axis.getMax();
        String renderer = axis.getRenderer();
        int tickAngle = axis.getTickAngle();
        String tickFormat = axis.getTickFormat();
        Object tickInterval = axis.getTickInterval();
        int tickCount = axis.getTickCount();

        writer.write(axisType.toString() + ": {");
        writer.write("label:\"" + EscapeUtils.forJavaScript(label) + "\"");

        if (min != null) {
            if (min instanceof String) {
                writer.write(",min:\"" + min + "\"");
            }
            else {
                writer.write(",min:" + min);
            }
        }

        if (max != null) {
            if (max instanceof String) {
                writer.write(",max:\"" + max + "\"");
            }
            else {
                writer.write(",max:" + max);
            }
        }

        if (renderer != null) {
            writer.write(",renderer:$.jqplot." + renderer);
        }

        writer.write(",tickOptions:{");
        writer.write("angle:" + tickAngle);
        if (tickFormat != null) {
            writer.write(",formatString:\"" + tickFormat + "\"");
        }
        writer.write("}");

        if (tickInterval != null) {
            writer.write(",tickInterval:\"" + tickInterval + "\"");
        }
        if (tickCount != 0) {
            writer.write(",numberTicks:" + tickCount);
        }

        writer.write("}");
    }
}
