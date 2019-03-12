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
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.component.chart.Chart;
import org.primefaces.model.chart.DonutChartModel;

public class DonutRenderer extends BasePlotRenderer {

    @Override
    protected void encodeData(FacesContext context, Chart chart) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        DonutChartModel model = (DonutChartModel) chart.getModel();
        List data = model.getData();

        writer.write(",data:[");
        for (int i = 0; i < data.size(); i++) {
            if (i != 0) {
                writer.write(",");
            }

            writer.write("[");
            Map<String, Number> map = (Map) data.get(i);
            for (Iterator<String> it = map.keySet().iterator(); it.hasNext(); ) {
                String key = it.next();
                Number value = map.get(key);

                writer.write("[" + escapeChartData(key) + "," + value + "]");

                if (it.hasNext()) {
                    writer.write(",");
                }
            }
            writer.write("]");
        }

        writer.write("]");
    }

    @Override
    protected void encodeOptions(FacesContext context, Chart chart) throws IOException {
        super.encodeOptions(context, chart);

        ResponseWriter writer = context.getResponseWriter();
        DonutChartModel model = (DonutChartModel) chart.getModel();
        int sliceMargin = model.getSliceMargin();
        boolean fill = model.isFill();
        boolean showDataLabels = model.isShowDataLabels();
        String dataFormat = model.getDataFormat();
        String dataLabelFormatString = model.getDataLabelFormatString();
        int dataLabelThreshold = model.getDataLabelThreshold();

        if (sliceMargin != 0) {
            writer.write(",sliceMargin:" + sliceMargin);
        }
        if (!fill) {
            writer.write(",fill:false");
        }
        if (showDataLabels) {
            writer.write(",showDataLabels:true");
        }
        if (dataFormat != null) {
            writer.write(",dataFormat:\"" + dataFormat + "\"");
        }
        if (dataLabelFormatString != null) {
            writer.write(",dataLabelFormatString:\"" + dataLabelFormatString + "\"");
        }
        if (dataLabelThreshold > 0 && dataLabelThreshold < 100) {
            writer.write(",dataLabelThreshold:" + dataLabelThreshold);
        }

        if (model.isShowDatatip()) {
            writer.write(",datatip:true");

            String datatipFormat = model.getDatatipFormat();
            String datatipEditor = model.getDatatipEditor();

            if (datatipFormat != null) {
                writer.write(",datatipFormat:\"" + model.getDatatipFormat() + "\"");
            }

            if (datatipEditor != null) {
                writer.write(",datatipEditor:" + datatipEditor);
            }
        }
    }
}
