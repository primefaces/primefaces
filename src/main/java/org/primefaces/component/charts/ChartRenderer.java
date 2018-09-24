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
package org.primefaces.component.charts;

import java.io.IOException;
import java.util.List;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import org.primefaces.model.charts.ChartData;
import org.primefaces.model.charts.ChartDataSet;
import org.primefaces.model.charts.ChartModel;
import org.primefaces.model.charts.axes.cartesian.CartesianAxes;
import org.primefaces.model.charts.axes.cartesian.CartesianScales;
import org.primefaces.model.charts.axes.radial.RadialScales;
import org.primefaces.model.charts.optionconfig.elements.Elements;
import org.primefaces.model.charts.optionconfig.title.Title;
import org.primefaces.model.charts.optionconfig.tooltip.Tooltip;
import org.primefaces.renderkit.CoreRenderer;
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

            writer.write("\"scales\":{");

            if (scales instanceof CartesianScales) {
                CartesianScales cScales = (CartesianScales) scales;
                List<CartesianAxes> xAxes = cScales.getXAxes();
                if (xAxes != null && !xAxes.isEmpty()) {
                    encodeAxes(context, chartName, "xAxes", xAxes);
                    hasComma = true;
                }

                List<CartesianAxes> yAxes = cScales.getYAxes();
                if (yAxes != null && !yAxes.isEmpty()) {
                    if (hasComma) {
                        writer.write(",");
                    }
                    encodeAxes(context, chartName, "yAxes", yAxes);
                }
            }
            else if (scales instanceof RadialScales) {
                RadialScales rScales = (RadialScales) scales;
                String preString;
                if (rScales.getAngelLines() != null) {
                    writer.write("\"angleLines\":" + rScales.getAngelLines().encode());
                    hasComma = true;
                }

                if (rScales.getGridLines() != null) {
                    preString = hasComma ? "," : "";
                    writer.write(preString + "\"gridLines\":" + rScales.getGridLines().encode());
                    hasComma = true;
                }

                if (rScales.getPointLabels() != null) {
                    preString = hasComma ? "," : "";
                    writer.write(preString + "\"pointLabels\":" + rScales.getPointLabels().encode());
                    hasComma = true;
                }

                if (rScales.getTicks() != null) {
                    preString = hasComma ? "," : "";
                    writer.write(preString + "\"ticks\":" + rScales.getTicks().encode());
                }
            }

            writer.write("}");
        }
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

            writer.write("\"tooltip\":{");
            writer.write(tooltip.encode());
            writer.write("}");
        }
    }
}
