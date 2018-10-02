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
package org.primefaces.component.chart.renderer;

import org.primefaces.component.chart.Chart;
import org.primefaces.model.chart.BarChartModel;
import org.primefaces.model.chart.ChartSeries;
import org.primefaces.util.EscapeUtils;

import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class BarRenderer extends CartesianPlotRenderer {

    @Override
    protected void encodeData(FacesContext context, Chart chart) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        BarChartModel model = (BarChartModel) chart.getModel();
        boolean horizontal = model.getOrientation().equals("horizontal");

        //data
        writer.write(",data:[");
        for (Iterator<ChartSeries> it = model.getSeries().iterator(); it.hasNext(); ) {
            ChartSeries series = it.next();
            int i = 1;

            writer.write("[");
            for (Iterator<Map.Entry<Object, Number>> its = series.getData().entrySet().iterator(); its.hasNext(); ) {
                Map.Entry<Object, Number> entry = its.next();
                Number value = entry.getValue();
                String valueToRender = value != null ? value.toString() : "null";

                if (horizontal) {
                    writer.write("[");
                    writer.write(valueToRender + "," + i);
                    writer.write("]");

                    i++;
                }
                else {
                    if (model.getDataRenderMode().equals("key")) {
                        writer.write("'" + (String) entry.getKey() + "'," + valueToRender);
                    }
                    else {
                        writer.write(valueToRender);
                    }
                }

                if (its.hasNext()) {
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
        BarChartModel model = (BarChartModel) chart.getModel();
        String orientation = model.getOrientation();
        int barPadding = model.getBarPadding();
        int barMargin = model.getBarMargin();
        int barWidth = model.getBarWidth();
        List<String> ticks = model.getTicks();
        String legendLabel = model.getLegendLabel();

        writer.write(",series:[");
        if (model.getDataRenderMode().equals("key") && legendLabel != null) {
            writer.write("{");
            writer.write("label:\"" + EscapeUtils.forJavaScript(legendLabel) + "\"");
            writer.write("}");
        }
        else {
            for (Iterator<ChartSeries> it = model.getSeries().iterator(); it.hasNext(); ) {
                ChartSeries series = it.next();
                series.encode(writer);

                if (it.hasNext()) {
                    writer.write(",");
                }
            }
        }
        writer.write("]");

        writer.write(",ticks:[");
        for (Iterator<String> tickIt = ticks.iterator(); tickIt.hasNext();) {
            writer.write("\"" + EscapeUtils.forJavaScript(tickIt.next()) + "\"");
            if (tickIt.hasNext()) {
                writer.write(",");
            }
        }
        writer.write("]");

        if (orientation != null) {
            writer.write(",orientation:\"" + orientation + "\"");
        }
        if (barPadding != 8) {
            writer.write(",barPadding:" + barPadding);
        }
        if (barMargin != 10) {
            writer.write(",barMargin:" + barMargin);
        }
        if (barWidth != 0) {
            writer.write(",barWidth:" + barWidth);
        }
        if (model.isStacked()) {
            writer.write(",stackSeries:true");
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
