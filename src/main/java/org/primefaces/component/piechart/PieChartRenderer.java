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
package org.primefaces.component.piechart;

import java.io.IOException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import org.primefaces.component.charts.ChartRenderer;
import org.primefaces.model.charts.ChartModel;
import org.primefaces.model.charts.pie.PieChartOptions;
import org.primefaces.util.WidgetBuilder;

public class PieChartRenderer extends ChartRenderer {

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        PieChart chart = (PieChart) component;
        String clientId = chart.getClientId(context);
        String style = chart.getStyle();
        String styleClass = chart.getStyleClass();

        encodeMarkup(context, clientId, style, styleClass);
        encodeScript(context, chart);
    }

    protected void encodeScript(FacesContext context, PieChart chart) throws IOException {
        String clientId = chart.getClientId(context);

        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("PieChart", chart.resolveWidgetVar(), clientId);

        encodeConfig(context, (ChartModel) chart.getModel());
        encodeClientBehaviors(context, chart);

        wb.finish();
    }

    @Override
    protected void encodeOptions(FacesContext context, String type, Object options) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        if (options == null) {
            return;
        }

        PieChartOptions pieOptions = (PieChartOptions) options;

        writer.write(",\"options\":{");

        writer.write("\"animation\":{");
        writer.write("\"animateRotate\":" + pieOptions.isAnimateRotate());
        writer.write(",\"animateScale\":" + pieOptions.isAnimateScale());
        writer.write("}");

        if (pieOptions.getCutoutPercentage() != null) {
            writer.write(",\"cutoutPercentage\":" + pieOptions.getCutoutPercentage());
        }

        if (pieOptions.getRotation() != null) {
            writer.write(",\"rotation\":" + pieOptions.getRotation());
        }

        if (pieOptions.getCircumference() != null) {
            writer.write(",\"cutoutPercentage\":" + pieOptions.getCircumference());
        }

        encodeElements(context, pieOptions.getElements(), true);
        encodeTitle(context, pieOptions.getTitle(), true);
        encodeTooltip(context, pieOptions.getTooltip(), true);

        writer.write("}");
    }
}
