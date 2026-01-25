/*
 * The MIT License
 *
 * Copyright (c) 2009-2026 PrimeTek Informatics
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
package org.primefaces.component.gmap;

import org.primefaces.behavior.ajax.AjaxBehavior;
import org.primefaces.model.map.Circle;
import org.primefaces.model.map.LatLng;
import org.primefaces.model.map.MapModel;
import org.primefaces.model.map.Marker;
import org.primefaces.model.map.Polygon;
import org.primefaces.model.map.Polyline;
import org.primefaces.model.map.Rectangle;
import org.primefaces.model.map.Symbol;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.EscapeUtils;
import org.primefaces.util.WidgetBuilder;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import jakarta.faces.FacesException;
import jakarta.faces.component.behavior.ClientBehavior;
import jakarta.faces.context.FacesContext;
import jakarta.faces.context.ResponseWriter;
import jakarta.faces.render.FacesRenderer;

@FacesRenderer(rendererType = GMap.DEFAULT_RENDERER, componentFamily = GMap.COMPONENT_FAMILY)
public class GMapRenderer extends CoreRenderer<GMap> {

    @Override
    public void decode(FacesContext context, GMap component) {
        decodeBehaviors(context, component);
    }

    @Override
    public void encodeEnd(FacesContext facesContext, GMap component) throws IOException {
        encodeMarkup(facesContext, component);
        encodeScript(facesContext, component);
    }

    protected void encodeMarkup(FacesContext context, GMap component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = component.getClientId(context);

        writer.startElement("div", component);
        writer.writeAttribute("id", clientId, null);
        if (component.getStyle() != null) {
            writer.writeAttribute("style", component.getStyle(), null);
        }
        if (component.getStyleClass() != null) {
            writer.writeAttribute("class", component.getStyleClass(), null);
        }

        writer.endElement("div");
    }

    protected void encodeScript(FacesContext context, GMap component) throws IOException {
        String widgetVar = component.resolveWidgetVar(context);
        GMapInfoWindow infoWindow = component.getInfoWindow();


        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("GMap", component)
                .attr("mapTypeId", component.getType().toUpperCase())
                .attr("center", component.getCenter())
                .attr("zoom", component.getZoom())
                .attr("apiKey", component.getApiKey())
                .attr("apiVersion", component.getApiVersion())
                .attr("libraries", component.getLibraries());


        if (!component.isFitBounds()) {
            wb.attr("fitBounds", false);
        }

        //Overlays
        encodeOverlays(context, component);

        //Controls
        if (component.isDisableDefaultUI()) {
            wb.attr("disableDefaultUI", true);
        }
        if (!component.isNavigationControl()) {
            wb.attr("navigationControl", false);
        }
        if (!component.isMapTypeControl()) {
            wb.attr("mapTypeControl", false);
        }
        if (component.isStreetView()) {
            wb.attr("streetViewControl", true);
        }

        //Options
        if (!component.isDraggable()) {
            wb.attr("draggable", false);
        }
        if (component.isDisableDoubleClickZoom()) {
            wb.attr("disableDoubleClickZoom", true);
        }
        if (!component.isScrollWheel()) {
            wb.attr("scrollwheel", false);
        }

        //Client events
        if (component.getOnPointClick() != null) {
            wb.callback("onPointClick", "function(event)", component.getOnPointClick() + ";");
        }

        /*
         * Behaviors
         * - Adds hook to show info window if one defined
         * - Encodes behaviors
         */
        if (infoWindow != null) {
            Map<String, List<ClientBehavior>> behaviorEvents = component.getClientBehaviors();
            List<ClientBehavior> overlaySelectBehaviors = behaviorEvents.get("overlaySelect");
            if (overlaySelectBehaviors != null) {
                for (ClientBehavior clientBehavior : overlaySelectBehaviors) {
                    ((AjaxBehavior) clientBehavior).setOnsuccess("PF('" + widgetVar + "').openWindow(data)");
                }
            }

            List<ClientBehavior> overlayDblSelectBehaviors = behaviorEvents.get("overlayDblSelect");
            if (overlayDblSelectBehaviors != null) {
                for (ClientBehavior clientBehavior : overlayDblSelectBehaviors) {
                    ((AjaxBehavior) clientBehavior).setOnsuccess("PF('" + widgetVar + "').openWindow(data)");
                }
            }
        }

        encodeClientBehaviors(context, component);

        wb.finish();
    }

    protected void encodeOverlays(FacesContext context, GMap component) throws IOException {
        MapModel<?> model = component.getModel();
        ResponseWriter writer = context.getResponseWriter();

        //Overlays
        if (model != null) {
            if (!model.getMarkers().isEmpty()) {
                encodeMarkers(context, component);
            }
            if (!model.getPolylines().isEmpty()) {
                encodePolylines(context, component);
            }
            if (!model.getPolygons().isEmpty()) {
                encodePolygons(context, component);
            }
            if (!model.getCircles().isEmpty()) {
                encodeCircles(context, component);
            }
            if (!model.getRectangles().isEmpty()) {
                encodeRectangles(context, component);
            }
        }

        GMapInfoWindow infoWindow = component.getInfoWindow();

        if (infoWindow != null) {
            writer.write(",infoWindow: new google.maps.InfoWindow({");
            writer.write("id:'" + infoWindow.getClientId(context) + "'");
            writer.write("})");
        }
    }

    protected void encodeMarkers(FacesContext context, GMap map) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        MapModel model = map.getModel();

        writer.write(",markers:[");

        for (Iterator<Marker> iterator = model.getMarkers().iterator(); iterator.hasNext(); ) {
            Marker marker = iterator.next();
            encodeMarker(context, marker);

            if (iterator.hasNext()) {
                writer.write(",");
            }
        }
        writer.write("]");
    }

    protected void encodeMarker(FacesContext context, Marker marker) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        writer.write("new google.maps.Marker({");
        writer.write("position:new google.maps.LatLng(" + marker.getLatlng().getLat() + ", " + marker.getLatlng().getLng() + ")");

        writer.write(",id:'" + marker.getId() + "'");
        if (marker.getTitle() != null) {
            writer.write(",title:\"" + EscapeUtils.forJavaScript(marker.getTitle()) + "\"");
        }
        if (marker.getIcon() != null) {
            writer.write(",icon:");
            encodeIcon(context, marker.getIcon());
        }
        if (marker.getShadow() != null) {
            writer.write(",shadow:'" + marker.getShadow() + "'");
        }
        if (marker.getCursor() != null) {
            writer.write(",cursor:'" + marker.getCursor() + "'");
        }
        if (marker.isDraggable()) {
            writer.write(",draggable: true");
        }
        if (!marker.isVisible()) {
            writer.write(",visible: false");
        }
        if (marker.isFlat()) {
            writer.write(",flat: true");
        }
        if (marker.getZindex() > Integer.MIN_VALUE) {
            writer.write(",zIndex:" + marker.getZindex());
        }
        if (marker.getAnimation() != null) {
            writer.write(",animation: google.maps.Animation." + marker.getAnimation().name());
        }

        writer.write("})");
    }

    protected void encodeIcon(FacesContext context, Object icon) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        if (icon instanceof String) {
            writer.write("'" + icon + "'");
        }
        else if (icon instanceof Symbol) {
            encodeIcon(context, (Symbol) icon);
        }
        else {
            throw new FacesException("GMap marker icon must be String or Symbol");
        }
    }

    protected void encodeIcon(FacesContext context, Symbol symbol) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        writer.write("{path:'" + symbol.getPath() + "'");
        if (symbol.getAnchor() != null) {
            writer.write(",anchor:new google.maps.Point(" + symbol.getAnchor().getX()
                    + "," + symbol.getAnchor().getY() + ")");
        }
        if (symbol.getFillColor() != null) {
            writer.write(",fillColor:'" + symbol.getFillColor() + "'");
        }
        if (symbol.getFillOpacity() != null) {
            writer.write(",fillOpacity:" + symbol.getFillOpacity());
        }
        if (symbol.getRotation() != null) {
            writer.write(",rotation:" + symbol.getRotation());
        }
        if (symbol.getScale() != null) {
            writer.write(",scale:" + symbol.getScale());
        }
        if (symbol.getStrokeColor() != null) {
            writer.write(",strokeColor:'" + symbol.getStrokeColor() + "'");
        }
        if (symbol.getStrokeOpacity() != null) {
            writer.write(",strokeOpacity:" + symbol.getStrokeOpacity());
        }
        if (symbol.getStrokeWeight() != null) {
            writer.write(",strokeWeight:" + symbol.getStrokeWeight());
        }
        writer.write("}");
    }

    protected void encodePolylines(FacesContext context, GMap component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        MapModel model = component.getModel();

        writer.write(",polylines:[");

        for (Iterator<Polyline> lines = model.getPolylines().iterator(); lines.hasNext(); ) {
            Polyline polyline = lines.next();

            writer.write("new google.maps.Polyline({");
            writer.write("id:'" + polyline.getId() + "'");

            encodePaths(context, polyline.getPaths());

            writer.write(",strokeOpacity:" + polyline.getStrokeOpacity());
            writer.write(",strokeWeight:" + polyline.getStrokeWeight());

            if (polyline.getStrokeColor() != null) {
                writer.write(",strokeColor:'" + polyline.getStrokeColor() + "'");
            }
            if (polyline.getZindex() > Integer.MIN_VALUE) {
                writer.write(",zIndex:" + polyline.getZindex());
            }
            if (polyline.getIcons() != null) {
                writer.write(", icons:" + polyline.getIcons());
            }

            writer.write("})");

            if (lines.hasNext()) {
                writer.write(",");
            }
        }

        writer.write("]");
    }

    protected void encodePolygons(FacesContext context, GMap component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        MapModel model = component.getModel();

        writer.write(",polygons:[");

        for (Iterator<Polygon> polygons = model.getPolygons().iterator(); polygons.hasNext(); ) {
            Polygon polygon = polygons.next();

            writer.write("new google.maps.Polygon({");
            writer.write("id:'" + polygon.getId() + "'");

            encodePaths(context, polygon.getPaths());

            writer.write(",strokeOpacity:" + polygon.getStrokeOpacity());
            writer.write(",strokeWeight:" + polygon.getStrokeWeight());
            writer.write(",fillOpacity:" + polygon.getFillOpacity());

            if (polygon.getStrokeColor() != null) {
                writer.write(",strokeColor:'" + polygon.getStrokeColor() + "'");
            }
            if (polygon.getFillColor() != null) {
                writer.write(",fillColor:'" + polygon.getFillColor() + "'");
            }
            if (polygon.getZindex() > Integer.MIN_VALUE) {
                writer.write(",zIndex:" + polygon.getZindex());
            }

            writer.write("})");

            if (polygons.hasNext()) {
                writer.write(",");
            }
        }

        writer.write("]");
    }

    protected void encodeCircles(FacesContext context, GMap component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        MapModel model = component.getModel();

        writer.write(",circles:[");

        for (Iterator<Circle> circles = model.getCircles().iterator(); circles.hasNext(); ) {
            Circle circle = circles.next();

            writer.write("new google.maps.Circle({");
            writer.write("id:'" + circle.getId() + "'");

            writer.write(",center:new google.maps.LatLng(" + circle.getCenter().getLat() + ", " + circle.getCenter().getLng() + ")");
            writer.write(",radius:" + circle.getRadius());

            writer.write(",strokeOpacity:" + circle.getStrokeOpacity());
            writer.write(",strokeWeight:" + circle.getStrokeWeight());
            writer.write(",fillOpacity:" + circle.getFillOpacity());

            if (circle.getStrokeColor() != null) {
                writer.write(",strokeColor:'" + circle.getStrokeColor() + "'");
            }
            if (circle.getFillColor() != null) {
                writer.write(",fillColor:'" + circle.getFillColor() + "'");
            }
            if (circle.getZindex() > Integer.MIN_VALUE) {
                writer.write(",zIndex:" + circle.getZindex());
            }

            writer.write("})");

            if (circles.hasNext()) {
                writer.write(",");
            }
        }

        writer.write("]");
    }

    protected void encodeRectangles(FacesContext context, GMap component) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        MapModel model = component.getModel();

        writer.write(",rectangles:[");

        for (Iterator<Rectangle> rectangles = model.getRectangles().iterator(); rectangles.hasNext(); ) {
            Rectangle rectangle = rectangles.next();

            writer.write("new google.maps.Rectangle({");
            writer.write("id:'" + rectangle.getId() + "'");

            LatLng ne = rectangle.getBounds().getNorthEast();
            LatLng sw = rectangle.getBounds().getSouthWest();

            writer.write(",bounds:new google.maps.LatLngBounds( new google.maps.LatLng("
                    + sw.getLat() + "," + sw.getLng() + "), new google.maps.LatLng(" + ne.getLat() + "," + ne.getLng() + "))");

            writer.write(",strokeOpacity:" + rectangle.getStrokeOpacity());
            writer.write(",strokeWeight:" + rectangle.getStrokeWeight());
            writer.write(",fillOpacity:" + rectangle.getFillOpacity());

            if (rectangle.getStrokeColor() != null) {
                writer.write(",strokeColor:'" + rectangle.getStrokeColor() + "'");
            }
            if (rectangle.getFillColor() != null) {
                writer.write(",fillColor:'" + rectangle.getFillColor() + "'");
            }
            if (rectangle.getZindex() > Integer.MIN_VALUE) {
                writer.write(",zIndex:" + rectangle.getZindex());
            }

            writer.write("})");

            if (rectangles.hasNext()) {
                writer.write(",");
            }
        }

        writer.write("]");
    }

    protected void encodePaths(FacesContext context, List<LatLng> paths) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        writer.write(",path:[");
        for (Iterator<LatLng> coords = paths.iterator(); coords.hasNext(); ) {
            LatLng coord = coords.next();

            writer.write("new google.maps.LatLng(" + coord.getLat() + ", " + coord.getLng() + ")");

            if (coords.hasNext()) {
                writer.write(",");
            }

        }
        writer.write("]");
    }

    @Override
    public void encodeChildren(FacesContext context, GMap component) throws IOException {
        //Do Nothing
    }

    @Override
    public boolean getRendersChildren() {
        return true;
    }
}
