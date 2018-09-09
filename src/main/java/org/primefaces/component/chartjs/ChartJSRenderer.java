package org.primefaces.component.chartjs;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.WidgetBuilder;

public class ChartJSRenderer extends CoreRenderer {

    @Override
    public void decode(FacesContext context, UIComponent component) {
        super.decodeBehaviors(context, component);
    }

    @Override
    public void encodeEnd(FacesContext facesContext, UIComponent component) throws IOException {
        ChartJS chart = (ChartJS) component;

        encodeMarkup(facesContext, chart);
        encodeScript(facesContext, chart);
    }

    protected void encodeMarkup(FacesContext context, UIComponent component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        ChartJS chart = (ChartJS) component;

        String clientId = chart.getClientId(context);
        String style = chart.getStyle();
        String styleClass = chart.getStyleClass();
        styleClass = (styleClass != null) ? "ui-chart " + styleClass : "ui-chart";

        writer.startElement("div", null);
        writer.writeAttribute("id", clientId, null);
        writer.writeAttribute("class", styleClass, "styleClass");

        writer.startElement("canvas", null);
        writer.writeAttribute("id", clientId + "_canvas", null);

        if (style != null) {
            writer.writeAttribute("style", style, "style");
        }

        writer.endElement("canvas");
        writer.endElement("div");
    }

    protected void encodeScript(FacesContext context, ChartJS chart) throws IOException {
        String clientId = chart.getClientId(context);

        WidgetBuilder wb = getWidgetBuilder(context)
                .init("BaseChart", chart.resolveWidgetVar(), clientId)
                .nativeAttr("config", chart.getModel().toJson());

        encodeClientBehaviors(context, chart);

        wb.finish();
    }
}
