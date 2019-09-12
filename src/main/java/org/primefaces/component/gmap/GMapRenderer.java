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
package org.primefaces.component.gmap;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.component.behavior.ClientBehavior;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.behavior.ajax.AjaxBehavior;
import org.primefaces.model.map.*;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.EscapeUtils;
import org.primefaces.util.WidgetBuilder;

public class GMapRenderer extends CoreRenderer {

    @Override
    public void decode(FacesContext context, UIComponent component) {
        decodeBehaviors(context, component);
    }

    @Override
    public void encodeEnd(FacesContext facesContext, UIComponent component) throws IOException {
        GMap map = (GMap) component;

        encodeMarkup(facesContext, map);
        encodeScript(facesContext, map);
    }

    protected void encodeMarkup(FacesContext context, GMap map) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = map.getClientId(context);

        writer.startElement("div", map);
        writer.writeAttribute("id", clientId, null);
        if (map.getStyle() != null) {
            writer.writeAttribute("style", map.getStyle(), null);
        }
        if (map.getStyleClass() != null) {
            writer.writeAttribute("class", map.getStyleClass(), null);
        }

        writer.endElement("div");
    }

    protected void encodeScript(FacesContext context, GMap map) throws IOException {
        String clientId = map.getClientId(context);
        String widgetVar = map.resolveWidgetVar(context);
        GMapInfoWindow infoWindow = map.getInfoWindow();


        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("GMap", map.resolveWidgetVar(context), clientId)
                .nativeAttr("mapTypeId", "google.maps.MapTypeId." + map.getType().toUpperCase())
                .nativeAttr("center", "new google.maps.LatLng(" + map.getCenter() + ")")
                .attr("zoom", map.getZoom());


        if (!map.isFitBounds()) {
            wb.attr("fitBounds", false);
        }

        //Overlays
        encodeOverlays(context, map);

        //Controls
        if (map.isDisableDefaultUI()) {
            wb.attr("disableDefaultUI", true);
        }
        if (!map.isNavigationControl()) {
            wb.attr("navigationControl", false);
        }
        if (!map.isMapTypeControl()) {
            wb.attr("mapTypeControl", false);
        }
        if (map.isStreetView()) {
            wb.attr("streetViewControl", true);
        }

        //Options
        if (!map.isDraggable()) {
            wb.attr("draggable", false);
        }
        if (map.isDisableDoubleClickZoom()) {
            wb.attr("disableDoubleClickZoom", true);
        }
        if (!map.isScrollWheel()) {
            wb.attr("scrollwheel", false);
        }

        //Client events
        if (map.getOnPointClick() != null) {
            wb.callback("onPointClick", "function(event)", map.getOnPointClick() + ";");
        }

        /*
         * Behaviors
         * - Adds hook to show info window if one defined
         * - Encodes behaviors
         */
        if (infoWindow != null) {
            Map<String, List<ClientBehavior>> behaviorEvents = map.getClientBehaviors();
            List<ClientBehavior> overlaySelectBehaviors = behaviorEvents.get("overlaySelect");
            for (ClientBehavior clientBehavior : overlaySelectBehaviors) {
                ((AjaxBehavior) clientBehavior).setOnsuccess("PF('" + widgetVar + "').openWindow(data)");
            }
        }

        encodeClientBehaviors(context, map);

        wb.finish();
    }

    protected void encodeOverlays(FacesContext context, GMap map) throws IOException {
        MapModel model = map.getModel();
        ResponseWriter writer = context.getResponseWriter();

        //Overlays
        if (model != null) {
            if (!model.getMarkers().isEmpty()) {
                encodeMarkers(context, map);
            }
            if (!model.getPolylines().isEmpty()) {
                encodePolylines(context, map);
            }
            if (!model.getPolygons().isEmpty()) {
                encodePolygons(context, map);
            }
            if (!model.getCircles().isEmpty()) {
                encodeCircles(context, map);
            }
            if (!model.getRectangles().isEmpty()) {
                encodeRectangles(context, map);
            }
        }

        GMapInfoWindow infoWindow = map.getInfoWindow();

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
            writer.write(",icon:'" + marker.getIcon() + "'");
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

        writer.write("})");
    }

    protected void encodePolylines(FacesContext context, GMap map) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        MapModel model = map.getModel();

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

    protected void encodePolygons(FacesContext context, GMap map) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        MapModel model = map.getModel();

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

    protected void encodeCircles(FacesContext context, GMap map) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        MapModel model = map.getModel();

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

    protected void encodeRectangles(FacesContext context, GMap map) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        MapModel model = map.getModel();

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
    public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
        //Do Nothing
    }

    @Override
    public boolean getRendersChildren() {
        return true;
    }
}
