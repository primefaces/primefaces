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
package org.primefaces.model.map;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class DefaultMapModel implements MapModel, Serializable {

	private List<Marker> markers;
	
	private List<Polyline> polylines;
	
	private List<Polygon> polygons;
        
    private List<Circle> circles;
        
    private List<Rectangle> rectangles;
	
	private final static String MARKER_ID_PREFIX = "marker";
	
	private final static String POLYLINE_ID_PREFIX = "polyline_";
	
	private final static String POLYGON_ID_PREFIX = "polygon_";
        
	private final static String CIRCLE_ID_PREFIX = "circle_";
	
    private final static String RECTANGLE_ID_PREFIX = "rectangle_";
	
	public DefaultMapModel() {
		markers = new ArrayList<Marker>();
		polylines = new ArrayList<Polyline>();
		polygons = new ArrayList<Polygon>();
		circles = new ArrayList<Circle>();
		rectangles = new ArrayList<Rectangle>();
	}

	public List<Marker> getMarkers() {
		return markers;
	}

	public List<Polyline> getPolylines() {
		return polylines;
	}

	public List<Polygon> getPolygons() {
		return polygons;
	}
	
    public List<Circle> getCircles() {
		return circles;
	}
        
    public List<Rectangle> getRectangles() {
		return rectangles;
	}

	public void addOverlay(Overlay overlay) {
		if(overlay instanceof Marker) {
			overlay.setId(MARKER_ID_PREFIX + UUID.randomUUID().toString());
			markers.add((Marker) overlay);
		}
		else if(overlay instanceof Polyline) {
			overlay.setId(POLYLINE_ID_PREFIX + UUID.randomUUID().toString());
			polylines.add((Polyline) overlay);
		}
		else if(overlay instanceof Polygon) {
			overlay.setId(POLYGON_ID_PREFIX + UUID.randomUUID().toString());
			polygons.add((Polygon) overlay);
		}
		else if(overlay instanceof Circle) {
			overlay.setId(CIRCLE_ID_PREFIX + UUID.randomUUID().toString());
			circles.add((Circle) overlay);
		}
		else if(overlay instanceof Rectangle) {
			overlay.setId(RECTANGLE_ID_PREFIX + UUID.randomUUID().toString());
			rectangles.add((Rectangle) overlay);
		}
	}

	@SuppressWarnings("unchecked")
	public Overlay findOverlay(String id) {
		List list = null;
		
		if(id.startsWith(MARKER_ID_PREFIX))
			list = markers;
		else if(id.startsWith(POLYLINE_ID_PREFIX))
			list = polylines;
		else if(id.startsWith(POLYGON_ID_PREFIX))
			list = polygons;
		else if(id.startsWith(CIRCLE_ID_PREFIX))
			list = circles;
		else if(id.startsWith(RECTANGLE_ID_PREFIX))
			list = rectangles;
		
		for(Iterator iterator = list.iterator(); iterator.hasNext();) {
			Overlay overlay = (Overlay) iterator.next();
			
			if(overlay.getId().equals(id))
				return overlay;
		}
		
		return null;
	}
}