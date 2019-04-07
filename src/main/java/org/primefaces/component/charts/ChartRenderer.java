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
package org.primefaces.component.charts;

import java.io.IOException;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.model.charts.ChartData;
import org.primefaces.model.charts.ChartDataSet;
import org.primefaces.model.charts.ChartModel;
import org.primefaces.model.charts.axes.AxesScale;
import org.primefaces.model.charts.axes.cartesian.CartesianAxes;
import org.primefaces.model.charts.axes.cartesian.CartesianScales;
import org.primefaces.model.charts.axes.radial.RadialScales;
import org.primefaces.model.charts.optionconfig.elements.Elements;
import org.primefaces.model.charts.optionconfig.legend.Legend;
import org.primefaces.model.charts.optionconfig.title.Title;
import org.primefaces.model.charts.optionconfig.tooltip.Tooltip;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.ChartUtils;
import org.primefaces.util.EscapeUtils;

public class ChartRenderer extends CoreRenderer {

    @Override
    public void decode(FacesContext context, UIComponent component) {
        super.decodeBehaviors(context, component);
    }

    protected void encodeMarkup(FacesContext context, String clientId, String style, String styleClass) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        styleClass = (styleClass != null) ? "ui-chart " + styleClass : "ui-chart";

        writer.startElement("div", null);
        writer.writeAttribute("id", clientId, null);
        writer.writeAttribute("class", styleClass, "styleClass");

        writer.startElement("canvas", null);
        writer.writeAttribute("id", clientId + "_canvas", null);
        if (style != null) writer.writeAttribute("style", style, "style");
        writer.endElement("canvas");

        writer.endElement("div");
    }

    protected void encodeConfig(FacesContext context, ChartModel model) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        ChartData data = model.getData();
        Object options = model.getOptions();

        writer.write(",\"config\":{");

        writer.write("\"type\":\"" + model.getType() + "\"");
        encodeData(context, data);
        encodeOptions(context, model.getType(), options);

        writer.write("}");

        String extender = model.getExtender();
        if (extender != null) {
            writer.write(",\"extender\":" + extender);
        }
    }

    protected void encodeData(FacesContext context, ChartData data) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        if (data == null) {
            return;
        }

        List<ChartDataSet> dataSetList = data.getDataSet();

        writer.write(",\"data\":{");

        writer.write("\"datasets\":[");

        for (int i = 0; i < dataSetList.size(); i++) {
            ChartDataSet dataSet = dataSetList.get(i);

            if (dataSet != null) {
                if (i != 0) {
                    writer.write(",");
                }

                writer.write(dataSet.encode());
            }
        }

        writer.write("]");

        Object labels = data.getLabels();
        if (labels != null) {
            writer.write(",\"labels\":");
            writeLabels(context, labels);
        }

        writer.write("}");
    }

    protected void writeLabels(FacesContext context, Object labels) throws IOException {
        boolean isList = labels instanceof List;

        if (isList) {
            ResponseWriter writer = context.getResponseWriter();
            List labelList = (List) labels;

            writer.write("[");
            for (int i = 0; i < labelList.size(); i++) {
                if (i != 0) {
                    writer.write(",");
                }

                Object item = labelList.get(i);
                if (item instanceof String) {
                    writer.write("\"" + EscapeUtils.forJavaScript((String) item) + "\"");
                }
                else {
                    writeLabels(context, item);
                }
            }
            writer.write("]");
        }
    }

    protected void encodeOptions(FacesContext context, String type, Object options) throws IOException {
        // implemented by chart components
    }

    protected void encodeScales(FacesContext context, String chartName, Object scales, boolean hasComma) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        if (scales != null) {
            if (hasComma) {
                writer.write(",");
            }
            hasComma = false;

            if (scales instanceof CartesianScales) {
                writer.write("\"scales\":{");
                CartesianScales cScales = (CartesianScales) scales;
                encodeScaleCommon(writer, cScales);
                List<CartesianAxes> xAxes = cScales.getXAxes();
                if (xAxes != null && !xAxes.isEmpty()) {
                    writer.write(",");
                    encodeAxes(context, chartName, "xAxes", xAxes);
                }

                List<CartesianAxes> yAxes = cScales.getYAxes();
                if (yAxes != null && !yAxes.isEmpty()) {
                    writer.write(",");
                    encodeAxes(context, chartName, "yAxes", yAxes);
                }

                writer.write("}");
            }
            else if (scales instanceof RadialScales) {
                writer.write("\"scale\":{");
                RadialScales rScales = (RadialScales) scales;
                encodeScaleCommon(writer, rScales);
                if (rScales.getAngelLines() != null) {
                    writer.write(",\"angleLines\":" + rScales.getAngelLines().encode());
                }

                if (rScales.getGridLines() != null) {
                    writer.write(",\"gridLines\":" + rScales.getGridLines().encode());
                }

                if (rScales.getPointLabels() != null) {
                    writer.write(",\"pointLabels\":" + rScales.getPointLabels().encode());
                }

                if (rScales.getTicks() != null) {
                    writer.write(",\"ticks\":" + rScales.getTicks().encode());
                }

                writer.write("}");
            }
        }
    }

    protected void encodeScaleCommon(ResponseWriter writer, AxesScale scale) throws IOException {
        ChartUtils.writeDataValue(writer, "display", scale.isDisplay(), false);
        ChartUtils.writeDataValue(writer, "weight", scale.getWeight(), true);
    }

    protected void encodeAxes(FacesContext context, String chartName, String name, List<CartesianAxes> axes) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        writer.write("\"" + name + "\":[");
        for (int i = 0; i < axes.size(); i++) {
            CartesianAxes data = axes.get(i);

            if (chartName.equals("bar")) {
                data.setOffset(true);
            }

            if (i != 0) {
                writer.write(",");
            }

            writer.write("{");
            writer.write(data.encode());
            writer.write("}");
        }
        writer.write("]");
    }

    protected void encodeElements(FacesContext context, Elements elements, boolean hasComma) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        if (elements != null) {
            if (hasComma) {
                writer.write(",");
            }

            writer.write("\"elements\":{");
            writer.write(elements.encode());
            writer.write("}");
        }
    }

    protected void encodeTitle(FacesContext context, Title title, boolean hasComma) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        if (title != null) {
            if (hasComma) {
                writer.write(",");
            }

            writer.write("\"title\":{");
            writer.write(title.encode());
            writer.write("}");
        }
    }

    protected void encodeTooltip(FacesContext context, Tooltip tooltip, boolean hasComma) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        if (tooltip != null) {
            if (hasComma) {
                writer.write(",");
            }

            writer.write("\"tooltips\":{");
            writer.write(tooltip.encode());
            writer.write("}");
        }
    }

    protected void encodeLegend(FacesContext context, Legend legend, boolean hasComma) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        if (legend != null) {
            if (hasComma) {
                writer.write(",");
            }

            writer.write("\"legend\":{");
            writer.write(legend.encode());
            writer.write("}");
        }
    }
}
