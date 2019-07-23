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
package org.primefaces.model.map;

import java.io.Serializable;
import java.util.*;

public class DefaultMapModel implements MapModel, Serializable {

    private static final long serialVersionUID = 1L;

    private static final String MARKER_ID_PREFIX = "marker";

    private static final String POLYLINE_ID_PREFIX = "polyline_";

    private static final String POLYGON_ID_PREFIX = "polygon_";

    private static final String CIRCLE_ID_PREFIX = "circle_";

    private static final String RECTANGLE_ID_PREFIX = "rectangle_";

    private List<Marker> markers;
    private List<Polyline> polylines;
    private List<Polygon> polygons;
    private List<Circle> circles;
    private List<Rectangle> rectangles;

    public DefaultMapModel() {
        markers = new ArrayList<>();
        polylines = new ArrayList<>();
        polygons = new ArrayList<>();
        circles = new ArrayList<>();
        rectangles = new ArrayList<>();
    }

    @Override
    public List<Marker> getMarkers() {
        return markers;
    }

    @Override
    public List<Polyline> getPolylines() {
        return polylines;
    }

    @Override
    public List<Polygon> getPolygons() {
        return polygons;
    }

    @Override
    public List<Circle> getCircles() {
        return circles;
    }

    @Override
    public List<Rectangle> getRectangles() {
        return rectangles;
    }

    @Override
    public void addOverlay(Overlay overlay) {
        if (overlay instanceof Marker) {
            overlay.setId(MARKER_ID_PREFIX + UUID.randomUUID().toString());
            markers.add((Marker) overlay);
        }
        else if (overlay instanceof Polyline) {
            overlay.setId(POLYLINE_ID_PREFIX + UUID.randomUUID().toString());
            polylines.add((Polyline) overlay);
        }
        else if (overlay instanceof Polygon) {
            overlay.setId(POLYGON_ID_PREFIX + UUID.randomUUID().toString());
            polygons.add((Polygon) overlay);
        }
        else if (overlay instanceof Circle) {
            overlay.setId(CIRCLE_ID_PREFIX + UUID.randomUUID().toString());
            circles.add((Circle) overlay);
        }
        else if (overlay instanceof Rectangle) {
            overlay.setId(RECTANGLE_ID_PREFIX + UUID.randomUUID().toString());
            rectangles.add((Rectangle) overlay);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public Overlay findOverlay(String id) {
        List<? extends Overlay> overlays = Collections.emptyList();

        if (id.startsWith(MARKER_ID_PREFIX)) {
            overlays = markers;
        }
        else if (id.startsWith(POLYLINE_ID_PREFIX)) {
            overlays = polylines;
        }
        else if (id.startsWith(POLYGON_ID_PREFIX)) {
            overlays = polygons;
        }
        else if (id.startsWith(CIRCLE_ID_PREFIX)) {
            overlays = circles;
        }
        else if (id.startsWith(RECTANGLE_ID_PREFIX)) {
            overlays = rectangles;
        }

        for (Overlay overlay : overlays) {
            if (overlay.getId().equals(id)) {
                return overlay;
            }
        }

        return null;
    }
}
