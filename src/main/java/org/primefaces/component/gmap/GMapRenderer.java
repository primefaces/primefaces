/*
 * Copyright 2009-2011 Prime Technology.
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

import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.event.map.MarkerDragEvent;
import org.primefaces.event.map.OverlaySelectEvent;
import org.primefaces.event.map.PointSelectEvent;
import org.primefaces.event.map.StateChangeEvent;
import org.primefaces.model.map.LatLng;
import org.primefaces.model.map.LatLngBounds;
import org.primefaces.model.map.MapModel;
import org.primefaces.model.map.Marker;
import org.primefaces.model.map.Polygon;
import org.primefaces.model.map.Polyline;
import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.ComponentUtils;

public class GMapRenderer extends CoreRenderer {
	
	@Override
	public void decode(FacesContext facesContext, UIComponent component) {
		GMap map = (GMap) component;
		Map<String,String> params = facesContext.getExternalContext().getRequestParameterMap();
		String clientId = map.getClientId();
		MapModel model = map.getModel();
		
		/**
		 * Respond to events
		 */
		if(params.containsKey(clientId + "_overlaySelected")) {
			String id = params.get(clientId + "_overlayId");
			map.queueEvent(new OverlaySelectEvent(map, model.findOverlay(id)));
			
		} else if(params.containsKey(clientId + "_markerDragged")) {
			String id = params.get(clientId + "_markerId");
			Marker marker = (Marker) model.findOverlay(id);
			double lat = Double.valueOf(params.get(clientId + "_lat"));
			double lng = Double.valueOf(params.get(clientId + "_lng"));
			marker.setLatlng(new LatLng(lat, lng));
			
			map.queueEvent(new MarkerDragEvent(map, marker));
			
		} else if(params.containsKey(clientId + "_stateChanged")) {
			String[] centerLoc = params.get(clientId + "_center").split(",");
			String[] northeastLoc = params.get(clientId + "_northeast").split(",");
			String[] southwestLoc = params.get(clientId + "_southwest").split(",");
			int zoomLevel = Integer.valueOf(params.get(clientId + "_zoom"));
			
			LatLng center = new LatLng(Double.valueOf(centerLoc[0]), Double.valueOf(centerLoc[1]));
			LatLng northeast = new LatLng(Double.valueOf(northeastLoc[0]), Double.valueOf(northeastLoc[1]));
			LatLng southwest = new LatLng(Double.valueOf(southwestLoc[0]), Double.valueOf(southwestLoc[1]));
			
			map.queueEvent(new StateChangeEvent(map, new LatLngBounds(center, northeast, southwest), zoomLevel));
			
		} else if(params.containsKey(clientId + "_pointSelected")) {
			String[] latlng = params.get(clientId + "_pointLatLng").split(",");
			LatLng position = new LatLng(Double.valueOf(latlng[0]), Double.valueOf(latlng[1]));
			
			map.queueEvent(new PointSelectEvent(map, position));
		}
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
	
	protected void encodeScript(FacesContext facesContext, GMap map) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = map.getClientId();
		
		writer.startElement("script", null);
		writer.writeAttribute("type", "text/javascript", null);

		writer.write(map.resolveWidgetVar() + " = new PrimeFaces.widget.GMap('" + clientId + "',{");
		
		//Required configuration
		writer.write("mapTypeId:google.maps.MapTypeId." + map.getType().toUpperCase());
		writer.write(",center:new google.maps.LatLng(" + map.getCenter() + ")");
		writer.write(",zoom:" + map.getZoom());
		
		encodeEventListeners(facesContext, map);
		
		encodeOverlays(facesContext, map);
		
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
		
		writer.write("});");
		
		writer.endElement("script");
	}

	protected void encodeOverlays(FacesContext facesContext, GMap map) throws IOException {
		MapModel model = map.getModel();
		ResponseWriter writer = facesContext.getResponseWriter();
		
		//Overlays
		if(model != null) {
			if(!model.getMarkers().isEmpty()) 
				encodeMarkers(facesContext, map);
			if(!model.getPolylines().isEmpty()) 
				encodePolylines(facesContext, map);
			if(!model.getPolygons().isEmpty()) 
				encodePolygons(facesContext, map);
		}
		
		//Overlay select listener
		GMapInfoWindow infoWindow = map.getInfoWindow();
		
		if(map.getOverlaySelectListener() != null) {
			writer.write(",hasOverlaySelectListener:true");
			
			if(map.getOnOverlaySelectUpdate() != null)
				writer.write(",onOverlaySelectUpdate:'" + ComponentUtils.findClientIds(facesContext, map, map.getOnOverlaySelectUpdate()) + "'");
			
			if(infoWindow != null) {
				writer.write(",infoWindow: new google.maps.InfoWindow({");
				writer.write("id:'" + infoWindow.getClientId(facesContext) + "'");
				writer.write("})");
			}

			if(map.getOnOverlaySelectStart() != null)
				writer.write(",onOverlaySelectStart:function(xhr) {" + map.getOnOverlaySelectStart() + "}");
			if(map.getOnOverlaySelectComplete() != null)
				writer.write(",onOverlaySelectComplete:function(xhr, status, args) {" + map.getOnOverlaySelectComplete() + "}");
		}
	}

	protected void encodeEventListeners(FacesContext facesContext, GMap map) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		String clientId = map.getClientId();
				
		if(map.getStateChangeListener() != null) {
			writer.write(",hasStateChangeListener: true");
			if(map.getOnStateChangeUpdate() != null) writer.write(",onStateChangeUpdate:'" + ComponentUtils.findClientIds(facesContext, map, map.getOnStateChangeUpdate()) + "'");
		}
		
		if(map.getPointSelectListener() != null) {
			writer.write(",hasPointSelectListener: true");
			if(map.getOnPointSelectUpdate() != null) writer.write(",onPointSelectUpdate:'" + ComponentUtils.findClientIds(facesContext, map, map.getOnPointSelectUpdate()) + "'");
		}
	}

	protected void encodeMarkers(FacesContext facesContext, GMap map) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		MapModel model = map.getModel();
	
		writer.write(",markers:[");
		
		for(Iterator<Marker> iterator = model.getMarkers().iterator(); iterator.hasNext();) {
			Marker marker = (Marker) iterator.next();
			encodeMarker(facesContext, marker);
			
			if(iterator.hasNext())
				writer.write(",");
		}	
		writer.write("]");
		
		if(map.getMarkerDragListener() != null) {
			writer.write(",hasMarkerDragListener:true");
			if(map.getOnMarkerDragUpdate() != null)
				writer.write(",onMarkerDragUpdate:'" + ComponentUtils.findClientIds(facesContext, map, map.getOnMarkerDragUpdate()) + "'");
		}
	}
	
	protected void encodeMarker(FacesContext facesContext, Marker marker) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		
		writer.write("new google.maps.Marker({");
		writer.write("position:new google.maps.LatLng(" + marker.getLatlng().getLat() + ", " + marker.getLatlng().getLng() + ")");
		
		writer.write(",id:'" + marker.getId() + "'");
		if(marker.getTitle() != null) writer.write(",title:'" + marker.getTitle() + "'");
		if(marker.getIcon() != null) writer.write(",icon:'" + marker.getIcon() + "'");
		if(marker.getShadow() != null) writer.write(",shadow:'" + marker.getShadow() + "'");
		if(marker.getCursor() != null) writer.write(",cursor:'" + marker.getCursor() + "'");
		if(marker.isDraggable()) writer.write(",draggable: true");
		if(!marker.isVisible()) writer.write(",visible: false");
		if(!marker.isFlat()) writer.write(",flat: true");
		
		writer.write("})"); 
	}
	
	protected void encodePolylines(FacesContext facesContext, GMap map) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		MapModel model = map.getModel();
		
		writer.write(",polylines:[");
		
		for(Iterator<Polyline> lines = model.getPolylines().iterator(); lines.hasNext();) {
			Polyline polyline = (Polyline) lines.next();
			
			writer.write("new google.maps.Polyline({");
			writer.write("id:'" + polyline.getId() + "'");
			
			encodePaths(facesContext, polyline.getPaths());
			
			writer.write(",strokeOpacity:" + polyline.getStrokeOpacity());
			writer.write(",strokeWeight:" + polyline.getStrokeWeight());
			
			if(polyline.getStrokeColor() != null) writer.write(",strokeColor:'" + polyline.getStrokeColor() + "'");
			
			writer.write("})");
			
			if(lines.hasNext())
				writer.write(",");
		}
		
		writer.write("]");
	}
	
	protected void encodePolygons(FacesContext facesContext, GMap map) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		MapModel model = map.getModel();
		
		writer.write(",polygons:[");
		
		for(Iterator<Polygon> polygons = model.getPolygons().iterator(); polygons.hasNext();) {
			Polygon polygon = (Polygon) polygons.next();
			
			writer.write("new google.maps.Polygon({");
			writer.write("id:'" + polygon.getId() + "'");
			
			encodePaths(facesContext, polygon.getPaths());
			
			writer.write(",strokeOpacity:" + polygon.getStrokeOpacity());
			writer.write(",strokeWeight:" + polygon.getStrokeWeight());
			writer.write(",fillOpacity:" + polygon.getFillOpacity());
			
			if(polygon.getStrokeColor() != null) writer.write(",strokeColor:'" + polygon.getStrokeColor() + "'");
			if(polygon.getFillColor() != null) writer.write(",fillColor:'" + polygon.getFillColor() + "'");
			
			writer.write("})");
			
			if(polygons.hasNext())
				writer.write(",");
		}
		
		writer.write("]");
	}
	
	protected void encodePaths(FacesContext facesContext, List<LatLng> paths) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		
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