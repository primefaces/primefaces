/*
 * Copyright 2009-2012 Prime Teknoloji.
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
package org.primefaces.component.gmap;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.component.behavior.ClientBehavior;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import org.primefaces.component.behavior.ajax.AjaxBehavior;

import org.primefaces.model.map.Circle;
import org.primefaces.model.map.LatLng;
import org.primefaces.model.map.MapModel;
import org.primefaces.model.map.Marker;
import org.primefaces.model.map.Polygon;
import org.primefaces.model.map.Polyline;
import org.primefaces.model.map.Rectangle;
import org.primefaces.renderkit.CoreRenderer;

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
	
	protected void encodeMarkup(FacesContext facesContext, GMap map) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = map.getClientId();
		
		writer.startElement("div", map);
		writer.writeAttribute("id", clientId, null);
		if(map.getStyle() != null) writer.writeAttribute("style", map.getStyle(), null);
		if(map.getStyleClass() != null) writer.writeAttribute("class", map.getStyleClass(), null);
		
		writer.endElement("div");
	}
	
	protected void encodeScript(FacesContext context, GMap map) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
		String clientId = map.getClientId();
        String widgetVar = map.resolveWidgetVar();
        GMapInfoWindow infoWindow = map.getInfoWindow();
		
        startScript(writer, clientId);

        writer.write("$(function() {");
        
        writer.write("PrimeFaces.cw('GMap','" + widgetVar + "',{");
        writer.write("id:'" + clientId + "'");
		
		//Required configuration
		writer.write(",mapTypeId:google.maps.MapTypeId." + map.getType().toUpperCase());
		writer.write(",center:new google.maps.LatLng(" + map.getCenter() + ")");
		writer.write(",zoom:" + map.getZoom());
        
        if(!map.isFitBounds())writer.write(",fitBounds:false");

        //Overlays
		encodeOverlays(context, map);
		
		//Controls
		if(map.isDisableDefaultUI()) writer.write(",disableDefaultUI:true");
		if(!map.isNavigationControl()) writer.write(",navigationControl:false");
		if(!map.isMapTypeControl()) writer.write(",mapTypeControl:false");
		if(map.isStreetView()) writer.write(",streetViewControl:true");
		
		//Options
		if(!map.isDraggable()) writer.write(",draggable:false");
		if(map.isDisableDoubleClickZoom()) writer.write(",disableDoubleClickZoom:true");
		
		//Client events
		if(map.getOnPointClick() != null) writer.write(",onPointClick:function(event) {" + map.getOnPointClick() + ";}");

        /*
         * Behaviors
         * - Adds hook to show info window if one defined
         * - Encodes behaviors
         */
        if(infoWindow != null) {
            Map<String,List<ClientBehavior>> behaviorEvents = map.getClientBehaviors();
            List<ClientBehavior> overlaySelectBehaviors = behaviorEvents.get("overlaySelect");
            for(ClientBehavior clientBehavior : overlaySelectBehaviors) {
                ((AjaxBehavior) clientBehavior).setOnsuccess(widgetVar + ".openWindow(data)");
            }
        }

        encodeClientBehaviors(context, map);
		
		writer.write("});});");
		
		endScript(writer);
	}

	protected void encodeOverlays(FacesContext context, GMap map) throws IOException {
		MapModel model = map.getModel();
		ResponseWriter writer = context.getResponseWriter();
		
		//Overlays
		if(model != null) {
			if(!model.getMarkers().isEmpty()) 
				encodeMarkers(context, map);
			if(!model.getPolylines().isEmpty()) 
				encodePolylines(context, map);
			if(!model.getPolygons().isEmpty()) 
				encodePolygons(context, map);
			if(!model.getCircles().isEmpty()) 
				encodeCircles(context, map);
			if(!model.getRectangles().isEmpty()) 
				encodeRectangles(context, map);
		}
        
        GMapInfoWindow infoWindow = map.getInfoWindow();

        if(infoWindow != null) {
            writer.write(",infoWindow: new google.maps.InfoWindow({");
            writer.write("id:'" + infoWindow.getClientId(context) + "'");
            writer.write("})");
        }
	}

	protected void encodeMarkers(FacesContext context, GMap map) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
		MapModel model = map.getModel();
	
		writer.write(",markers:[");
		
		for(Iterator<Marker> iterator = model.getMarkers().iterator(); iterator.hasNext();) {
			Marker marker = (Marker) iterator.next();
			encodeMarker(context, marker);
			
			if(iterator.hasNext())
				writer.write(",");
		}	
		writer.write("]");
	}
	
	protected void encodeMarker(FacesContext context, Marker marker) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
		
		writer.write("new google.maps.Marker({");
		writer.write("position:new google.maps.LatLng(" + marker.getLatlng().getLat() + ", " + marker.getLatlng().getLng() + ")");
		
		writer.write(",id:'" + marker.getId() + "'");
		if(marker.getTitle() != null) writer.write(",title:\"" + escapeText(marker.getTitle()) + "\"");
		if(marker.getIcon() != null) writer.write(",icon:'" + marker.getIcon() + "'");
		if(marker.getShadow() != null) writer.write(",shadow:'" + marker.getShadow() + "'");
		if(marker.getCursor() != null) writer.write(",cursor:'" + marker.getCursor() + "'");
		if(marker.isDraggable()) writer.write(",draggable: true");
		if(!marker.isVisible()) writer.write(",visible: false");
		if(marker.isFlat()) writer.write(",flat: true");
		if(marker.getZindex() > Integer.MIN_VALUE) writer.write(",zIndex:" + marker.getZindex());
		
		writer.write("})"); 
	}
	
	protected void encodePolylines(FacesContext context, GMap map) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
		MapModel model = map.getModel();
		
		writer.write(",polylines:[");
		
		for(Iterator<Polyline> lines = model.getPolylines().iterator(); lines.hasNext();) {
			Polyline polyline = (Polyline) lines.next();
			
			writer.write("new google.maps.Polyline({");
			writer.write("id:'" + polyline.getId() + "'");
			
			encodePaths(context, polyline.getPaths());
			
			writer.write(",strokeOpacity:" + polyline.getStrokeOpacity());
			writer.write(",strokeWeight:" + polyline.getStrokeWeight());
			
			if(polyline.getStrokeColor() != null) writer.write(",strokeColor:'" + polyline.getStrokeColor() + "'");
			if(polyline.getZindex() > Integer.MIN_VALUE) writer.write(",zIndex:" + polyline.getZindex());

			writer.write("})");
			
			if(lines.hasNext())
				writer.write(",");
		}
		
		writer.write("]");
	}
	
	protected void encodePolygons(FacesContext context, GMap map) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
		MapModel model = map.getModel();
		
		writer.write(",polygons:[");
		
		for(Iterator<Polygon> polygons = model.getPolygons().iterator(); polygons.hasNext();) {
			Polygon polygon = (Polygon) polygons.next();
			
			writer.write("new google.maps.Polygon({");
			writer.write("id:'" + polygon.getId() + "'");
			
			encodePaths(context, polygon.getPaths());
			
			writer.write(",strokeOpacity:" + polygon.getStrokeOpacity());
			writer.write(",strokeWeight:" + polygon.getStrokeWeight());
			writer.write(",fillOpacity:" + polygon.getFillOpacity());
			
			if(polygon.getStrokeColor() != null) writer.write(",strokeColor:'" + polygon.getStrokeColor() + "'");
			if(polygon.getFillColor() != null) writer.write(",fillColor:'" + polygon.getFillColor() + "'");
			if(polygon.getZindex() > Integer.MIN_VALUE) writer.write(",zIndex:" + polygon.getZindex());
	
            writer.write("})");
			
			if(polygons.hasNext())
				writer.write(",");
		}
		
		writer.write("]");
	}
        
	protected void encodeCircles(FacesContext context, GMap map) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
		MapModel model = map.getModel();
		
		writer.write(",circles:[");
		
		for(Iterator<Circle> circles = model.getCircles().iterator(); circles.hasNext();) {
			Circle circle = (Circle) circles.next();
			
			writer.write("new google.maps.Circle({");
			writer.write("id:'" + circle.getId() + "'");
                        
			writer.write(",center:new google.maps.LatLng(" + circle.getCenter().getLat() + ", " + circle.getCenter().getLng() + ")");
			writer.write(",radius:" + circle.getRadius());
                        
			writer.write(",strokeOpacity:" + circle.getStrokeOpacity());
			writer.write(",strokeWeight:" + circle.getStrokeWeight());
			writer.write(",fillOpacity:" + circle.getFillOpacity());
			
			if(circle.getStrokeColor() != null) writer.write(",strokeColor:'" + circle.getStrokeColor() + "'");
			if(circle.getFillColor() != null) writer.write(",fillColor:'" + circle.getFillColor() + "'");
			if(circle.getZindex() > Integer.MIN_VALUE) writer.write(",zIndex:" + circle.getZindex());

            writer.write("})");
			
			if(circles.hasNext())
				writer.write(",");
		}
		
		writer.write("]");
	}
        
	protected void encodeRectangles(FacesContext context, GMap map) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
		MapModel model = map.getModel();
		
		writer.write(",rectangles:[");
		
		for(Iterator<Rectangle> rectangles = model.getRectangles().iterator(); rectangles.hasNext();) {
			Rectangle rectangle = (Rectangle) rectangles.next();
			
			writer.write("new google.maps.Rectangle({");
			writer.write("id:'" + rectangle.getId() + "'");
			
            LatLng ne = rectangle.getBounds().getNorthEast();
            LatLng sw = rectangle.getBounds().getSouthWest();
                        
			writer.write(",bounds:new google.maps.LatLngBounds( new google.maps.LatLng(" + ne.getLat() + "," + ne.getLng() +"), new google.maps.LatLng(" + sw.getLat() + "," + sw.getLng() +"))");
			
			writer.write(",strokeOpacity:" + rectangle.getStrokeOpacity());
			writer.write(",strokeWeight:" + rectangle.getStrokeWeight());
			writer.write(",fillOpacity:" + rectangle.getFillOpacity());
			
			if(rectangle.getStrokeColor() != null) writer.write(",strokeColor:'" + rectangle.getStrokeColor() + "'");
			if(rectangle.getFillColor() != null) writer.write(",fillColor:'" + rectangle.getFillColor() + "'");
			if(rectangle.getZindex() > Integer.MIN_VALUE) writer.write(",zIndex:" + rectangle.getZindex());

            writer.write("})");
			
			if(rectangles.hasNext())
				writer.write(",");
		}
		
		writer.write("]");
	}
	
	protected void encodePaths(FacesContext context, List<LatLng> paths) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
		
		writer.write(",path:[");
		for(Iterator<LatLng> coords = paths.iterator(); coords.hasNext();) {
			LatLng coord = coords.next();
			
			writer.write("new google.maps.LatLng(" + coord.getLat() + ", " + coord.getLng() + ")");
			
			if(coords.hasNext())
				writer.write(",");
			
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