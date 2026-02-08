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

import org.primefaces.cdk.api.FacesBehaviorEvent;
import org.primefaces.cdk.api.FacesBehaviorEvents;
import org.primefaces.cdk.api.FacesComponentBase;
import org.primefaces.cdk.api.Property;
import org.primefaces.component.api.StyleAware;
import org.primefaces.component.api.Widget;
import org.primefaces.event.map.GeocodeEvent;
import org.primefaces.event.map.MarkerDragEvent;
import org.primefaces.event.map.OverlaySelectEvent;
import org.primefaces.event.map.PointSelectEvent;
import org.primefaces.event.map.ReverseGeocodeEvent;
import org.primefaces.event.map.StateChangeEvent;

import jakarta.faces.component.UIComponentBase;

@FacesComponentBase
@FacesBehaviorEvents({
    @FacesBehaviorEvent(name = "overlaySelect", event = OverlaySelectEvent.class, description = "Fires when an overlay is selected."),
    @FacesBehaviorEvent(name = "overlayDblSelect", event = OverlaySelectEvent.class, description = "Fires when an overlay is double-clicked."),
    @FacesBehaviorEvent(name = "stateChange", event = StateChangeEvent.class, description = "Fires when map state changes."),
    @FacesBehaviorEvent(name = "pointSelect", event = PointSelectEvent.class, description = "Fires when a point on the map is selected."),
    @FacesBehaviorEvent(name = "pointDblSelect", event = PointSelectEvent.class, description = "Fires when a point on the map is double-clicked."),
    @FacesBehaviorEvent(name = "markerDrag", event = MarkerDragEvent.class, description = "Fires when a marker is dragged."),
    @FacesBehaviorEvent(name = "geocode", event = GeocodeEvent.class, description = "Fires when geocoding is performed."),
    @FacesBehaviorEvent(name = "reverseGeocode", event = ReverseGeocodeEvent.class, description = "Fires when reverse geocoding is performed.")
})
public abstract class GMapBase extends UIComponentBase implements Widget, StyleAware {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.GMapRenderer";

    public GMapBase() {
        setRendererType(DEFAULT_RENDERER);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    @Property(description = "An org.primefaces.model.MapModel instance.")
    public abstract org.primefaces.model.map.MapModel getModel();

    @Property(description = "There are four types of maps available: roadmap, satellite, hybrid, and terrain.")
    public abstract String getType();

    @Property(description = "Center point of the map.")
    public abstract String getCenter();

    @Property(defaultValue = "8", description = "Defines the initial zoom level.")
    public abstract int getZoom();

    @Property(defaultValue = "false", description = "Controls street view support.")
    public abstract boolean isStreetView();

    @Property(defaultValue = "false", description = "Disables default UI controls.")
    public abstract boolean isDisableDefaultUI();

    @Property(defaultValue = "true", description = "Defines visibility of navigation control.")
    public abstract boolean isNavigationControl();

    @Property(defaultValue = "true", description = "Defines visibility of map type control.")
    public abstract boolean isMapTypeControl();

    @Property(defaultValue = "true", description = "Defines draggability of map.")
    public abstract boolean isDraggable();

    @Property(defaultValue = "false", description = "Disables zooming on mouse double click.")
    public abstract boolean isDisableDoubleClickZoom();

    @Property(description = "Javascript callback to execute when a point on map is clicked.")
    public abstract String getOnPointClick();

    @Property(defaultValue = "false", description = "Defines if center and zoom should be calculated automatically to contain all markers on the map.")
    public abstract boolean isFitBounds();

    @Property(defaultValue = "true", description = "Controls scrollwheel zooming on the map.")
    public abstract boolean isScrollWheel();

    @Property(description = "Google Maps API key.")
    public abstract String getApiKey();

    @Property(defaultValue = "weekly", description = "Google Maps API version.")
    public abstract String getApiVersion();

    @Property(description = "Comma-separated list of additional Google Maps libraries to load.")
    public abstract String getLibraries();
}