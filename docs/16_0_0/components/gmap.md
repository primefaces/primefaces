# GMap

GMap is a map component integrated with Google Maps API V3.

[See this widget in the JavaScript API Docs.](../jsdocs/classes/src_PrimeFaces.PrimeFaces.widget.GMap-1.html)

## Info

| Name | Value |
| --- | --- |
| Tag | gmap
| Component Class | org.primefaces.component.gmap.GMap
| Component Type | org.primefaces.component.Gmap
| Component Family | org.primefaces.component |
| Renderer Type | org.primefaces.component.GmapRenderer
| Renderer Class | org.primefaces.component.gmap.GmapRenderer

## Attributes

| Name | Default | Type | Description | 
| --- | --- | --- | --- |
| id | null | String | Unique identifier of the component.
| rendered | true | Boolean | Boolean value to specify the rendering of the component, when set to false component will not be rendered.
| binding | null | Object | An el expression that maps to a server side UIComponent instance in a backing bean.
| widgetVar | null | String | Name of the client side widget.
| apiKey | null | String | Google Maps API key. Required for asynchronous loading if Google Maps is not already loaded via script tag.
| apiVersion | weekly | String | Google Maps API version. Only used for asynchronous loading. Valid values: 'weekly', 'beta', 'alpha', or a specific version number.
| center | null | String | Center point of the map.
| disabledDoubleClickZoom | false | Boolean | Disables zooming on mouse double click.
| disableDefaultUI | false | Boolean | Disables default UI controls
| draggable | true | Boolean | Defines draggability of map.
| fitBounds | true | Boolean | Defines if center and zoom should be calculated automatically to contain all markers on the map.
| libraries | null | String | Comma-separated list of additional Google Maps libraries to load (e.g., 'places,geometry'). Only used for asynchronous loading.
| mapTypeControl | true | Boolean | Defines visibility of map type control.
| model | null | MapModel | An org.primefaces.model.MapModel instance.
| navigationControl | true | Boolean | Defines visibility of navigation control.
| onPointClick | null | String | Javascript callback to execute when a point on map is clicked.
| scrollWheel | false | Boolean | Controls scrollwheel zooming on the map.
| streetView | false | Boolean | Controls street view support.
| style | null | String | Inline style of the map container.
| styleClass | null | String | Style class of the map container.
| type | null | String | There are four types of maps available: roadmap, satellite, hybrid, and terrain.
| zoom | 8 | Integer | Defines the initial zoom level.

## Getting started with GMap
First thing to do is placing V3 of the Google Maps API that the GMap is based on. Make sure to
register an API key for your map and replace `YOUR_API_KEY` in the script.

```js
<script src="https://maps.googleapis.com/maps/api/js?key=YOUR_API_KEY"></script>
```
As Google Maps API states, mandatory sensor parameter is used to specify if your application
requires a sensor like GPS locator. Four options are required to place a `GMap` on a page, these are
center, zoom, type and style.

```xhtml
<p:gmap center="41.381542, 2.122893" zoom="15" type="hybrid" style="width:600px;height:400px" />
```
_center_ : Center of the map in lat, lng format
_zoom_ : Zoom level of the map
_type_ : Type of map, valid values are, "hybrid", "satellite", "hybrid" and "terrain".
_style_ : Dimensions of the map.

## Asynchronous Loading of Google Maps API
GMap supports both static and asynchronous loading of the Google Maps API. The widget automatically detects if Google Maps is already loaded and will use asynchronous loading only when needed.

### Static Loading (Traditional Method)
If you prefer to load Google Maps statically via a script tag, simply include the script in your page before the GMap component:

```html
<script src="https://maps.googleapis.com/maps/api/js?key=YOUR_API_KEY"></script>
```

```xhtml
<p:gmap center="41.381542, 2.122893" zoom="15" type="hybrid" style="width:600px;height:400px" />
```

### Asynchronous Loading (Dynamic Library Import)
For better performance and on-demand loading, you can let GMap load Google Maps asynchronously. The widget uses Google's Dynamic Library Import API to load libraries only when needed.

To enable asynchronous loading, provide the `apiKey` attribute (and optionally `apiVersion` and `libraries`):

```xhtml
<p:gmap center="41.381542, 2.122893" zoom="15" type="hybrid" 
        style="width:600px;height:400px" 
        apiKey="YOUR_API_KEY" />
```

#### Loading Additional Libraries
If your application needs additional Google Maps libraries (such as Places API or Geometry library), specify them using the `libraries` attribute:

```xhtml
<p:gmap center="41.381542, 2.122893" zoom="15" type="hybrid" 
        style="width:600px;height:400px" 
        apiKey="YOUR_API_KEY"
        libraries="places,geometry" />
```

#### Specifying API Version
You can control which version of the Google Maps API to load using the `apiVersion` attribute:

```xhtml
<p:gmap center="41.381542, 2.122893" zoom="15" type="hybrid" 
        style="width:600px;height:400px" 
        apiKey="YOUR_API_KEY"
        apiVersion="weekly" />
```

Valid values for `apiVersion`:
- `weekly` (default) - Latest weekly release
- `beta` - Beta release
- `alpha` - Alpha release
- Specific version number (e.g., `3.55`)

### When to Use Each Method

**Use Static Loading when:**
- You have multiple GMap components on the same page (more efficient to load once)
- You need strict control over when Google Maps loads
- You're using legacy code that already includes the script tag

**Use Asynchronous Loading when:**
- You want improved initial page load performance
- You prefer on-demand loading of Google Maps libraries
- You're building single-page applications (SPAs) where maps may not always be needed

**Note:** If Google Maps is already loaded statically, the widget will automatically use the existing instance and ignore the `apiKey`, `apiVersion`, and `libraries` attributes.

For more information about Google Maps Dynamic Library Import API, see the [official documentation](https://developers.google.com/maps/documentation/javascript/load-maps-js-api).

## MapModel
GMap is backed by an _org.primefaces.model.map.MapModel_ instance, PrimeFaces provides
_org.primefaces.model.map.DefaultMapModel_ as the default implementation. API documents of all GMap
related model classes are available at the end of `GMap` section and also at Javadocs of PrimeFaces.

## Markers
A marker is represented by _org.primefaces.model.map.Marker._

```xhtml
<p:gmap center="41.381542, 2.122893" zoom="15" type="hybrid" style="width:600px;height:400px" model="#{mapBean.model}"/>
```
```java
public class MapBean {
    private MapModel model = new DefaultMapModel();

    public MapBean() {
        model.addOverlay(new Marker(new LatLng(36.879466, 30.667648), "M1"));
        //more overlays
    }
    public MapModel getModel() { 
        return this.model; 
    }
}
```
## Polylines
A polyline is represented by _org.primefaces.model.map.Polyline_.

```xhtml
<p:gmap center="41.381542, 2.122893" zoom="15" type="hybrid" style="width:600px;height:400px" model="#{mapBean.model}"/>
```

```java
public class MapBean {
    private MapModel model;

    public MapBean() {
        model = new DefaultMapModel();
        Polyline polyline = new Polyline();
        polyline.getPaths().add(new LatLng(36.879466, 30.667648));
        polyline.getPaths().add(new LatLng(36.883707, 30.689216));
        polyline.getPaths().add(new LatLng(36.879703, 30.706707));
        polyline.getPaths().add(new LatLng(36.885233, 37.702323));
        model.addOverlay(polyline);
    }
    public MapModel getModel() { 
        return this.model; 
    }
}
```
## Polygons
A polygon is represented by _org.primefaces.model.map.Polygon_.

```xhtml
<p:gmap center="41.381542, 2.122893" zoom="15" type="hybrid" style="width:600px;height:400px" model="#{mapBean.model}"/>
```
```java
public class MapBean {
    private MapModel model;

    public MapBean() {
        model = new DefaultMapModel();
        Polygon polygon = new Polygon();
        polyline.getPaths().add(new LatLng(36.879466, 30.667648));
        polyline.getPaths().add(new LatLng(36.883707, 30.689216));
        polyline.getPaths().add(new LatLng(36.879703, 30.706707));
        model.addOverlay(polygon);
    }
    public MapModel getModel() { 
        return this.model; 
    }
}
```
## Circles
A circle is represented by _org.primefaces.model.map.Circle_.

```xhtml
<p:gmap center="41.381542, 2.122893" zoom="15" type="hybrid" style="width:600px;height:400px" model="#{mapBean.model}"/>
```

```java
public class MapBean {
    private MapModel model;

    public MapBean() {
        model = new DefaultMapModel();
        Circle circle = new Circle(new LatLng(36.879466, 30.667648), 500);
        model.addOverlay(circle);
    }
    public MapModel getModel() { 
        return this.model; 
    }
}
```
## Rectangles
A circle is represented by _org.primefaces.model.map.Rectangle_.

```xhtml
<p:gmap center="41.381542, 2.122893" zoom="15" type="hybrid" style="width:600px;height:400px" model="#{mapBean.model}"/>
```
```java
public class MapBean {
    private MapModel model;

    public MapBean() {
        model = new DefaultMapModel();
        LatLng coord1 = new LatLng(36.879466, 30.667648);
        LatLng coord2 = new LatLng(36.883707, 30.689216);
        Rectangle rectangle = new Rectangle(coord1, coord2);
        model.addOverlay(circle);
    }
    public MapModel getModel() { 
        return this.model; 
    }
}
```
## GeoCoding
Geocoding support is provided by client side API. Results are then passed to the backing bean using
_GeocodeEvent_ and _ReverseGeocodeEvent_ instances via ajax behavior callbacks.

```xhtml
<p:gmap widgetVar="pmap">
    <p:ajax event="geocode" listener="#{bean.onGeocode}" />
</p:map>

<script>
    PF('pmap').geocode('Barcelona');
</script>
```
```java
public void onGeocode(GeocodeEvent event) {
    List<GeocodeResult> results = event.getResults();
}
```

## AJAX Behavior Events
GMap provides many custom AJAX behavior events for you to hook-in to various features.

| Event | Listener Parameter | Fired |
| --- | --- | --- |
| geocode | org.primefaces.event.map.GeocodeEvent | When the map is geocoded
| markerDrag | org.primefaces.event.map.MarkerDragEvent | When a marker is dragged.
| overlayDblSelect | org.primefaces.event.map.OverlaySelectEvent | When an overlay is double clicked.
| overlaySelect | org.primefaces.event.map.OverlaySelectEvent | When an overlay is selected.
| pointDblSelect | org.primefaces.event.map.PointSelectEvent | When an empty point is double clicked.
| pointSelect | org.primefaces.event.map.PointSelectEvent | When an empty point is selected.
| reverseGeocode | org.primefaces.event.map.ReverseGeocodeEvent | When a geocode is reversed.
| stateChange | org.primefaces.event.map.StateChangeEvent | When map state changes.

Following example displays a FacesMessage about the selected marker with growl component.

```xhtml
<h:form>
    <p:growl id="growl" />
    <p:gmap center="41.381542, 2.122893" zoom="15" type="hybrid" style="width:600px;height:400px" model="#{mapBean.model}">
        <p:ajax event="overlaySelect" listener="#{mapBean.onMarkerSelect}" update="growl" />
    </p:gmap>
</h:form>
```
```java
public class MapBean {
    private MapModel model;
    public MapBean() {
        model = new DefaultMapModel();
        //add markers
    }
    public MapModel getModel() {
        return model
    }
    public void onMarkerSelect(OverlaySelectEvent event) {
        Marker selectedMarker = (Marker) event.getOverlay();
        //add facesmessage
    }
}
```
## InfoWindow
A common use case is displaying an info window when a marker is selected. _gmapInfoWindow_ is
used to implement this special use case. Following example, displays an info window that contains
an image of the selected marker data.


```xhtml
<h:form>
    <p:gmap center="41.381542, 2.122893" zoom="15" type="hybrid" style="width:600px;height:400px" model="#{mapBean.model}">
        <p:ajax event="overlaySelect" listener="#{mapBean.onMarkerSelect}" />
        <p:gmapInfoWindow>
            <p:graphicImage value="/images/#{mapBean.marker.data.image}" />
            <h:outputText value="#{mapBean.marker.data.title}" />
        </p:gmapInfoWindow>
    </p:gmap>
</h:form>
```
```java
public class MapBean {
    private MapModel model;
    private Marker marker;

    public MapBean() {
        model = new DefaultMapModel();
        //add markers
    }
    public MapModel getModel() { 
        return model; 
    }
    public Marker getMarker() { 
        return marker; 
    }
    public void onMarkerSelect(OverlaySelectEvent event) {
        this.marker = (Marker) event.getOverlay();
    }
}
```
## Street View
StreeView is enabled simply by setting _streetView_ option to true.


```xhtml
<p:gmap center="41.381542, 2.122893" zoom="15" type="hybrid" style="width:600px;height:400px" streetView="true" />
```
## Map Controls
Controls on map can be customized via attributes like _navigationControl_ and _mapTypeControl_.
Alternatively setting _disableDefaultUI_ to true will remove all controls at once.

```xhtml
<p:gmap center="41.381542, 2.122893" zoom="15" type="terrain" style="width:600px;height:400px"
    mapTypeControl="false" navigationControl="false" />
```
## Native Google Maps API
In case you need to access native google maps API with javascript, use provided _getMap()_ method.

```js
var gmap = PF('yourWidgetVar').getMap();
//gmap is a google.maps.Map instance
```
Full map API is provided at;

http://code.google.com/apis/maps/documentation/javascript/reference.html

## GMap API
_org.primefaces.model.map.MapModel_ ( _org.primefaces.model.map.DefaultMapModel_ is the default
implementation)


| Method | Description |
| --- | --- |
| List<Circle> getCircles() | Returns the list of circles
| List<Marker> getMarkers() | Returns the list of markers
| List<Polygon> getPolygons() | Returns the list of polygons
| List<Polyline> getPolylines() | Returns the list of polylines
| List<Rectangle> getRectangles() | Returns the list of rectangles.
| Overlay findOverlay(String id) | Finds an overlay by it’s unique id
| void addOverlay(Overlay overlay) |  Adds an overlay to map


_org.primefaces.model.map.Overlay_

| Property | Default | Type | Description
| --- | --- | --- | --- |
| id | null | String | Id of the overlay, generated and used internally
| data | null | Object | Data represented in marker
| zindex | null | Integer | Z-Index of the overlay

_org.primefaces.model.map.Marker_ extends _org.primefaces.model.map.Overlay_

| Property | Default | Type | Description
| --- | --- | --- | --- |
| animation | null | Animation | Enumeration of either DROP or BOUNCE
| clickable | 1 | Boolean | Defines if marker can be dragged
| cursor | pointer | String | Cursor to display on rollover
| draggable | 0 | Boolean | Defines if marker can be dragged
| flat | 0 | Boolean | If enabled, shadow image is not displayed
| icon | null | String | Icon of the foreground
| latlng | null | LatLng | Location of the marker
| shadow | null | String | Shadow image of the marker
| title | null | String | Text to display on rollover
| visible | 1 | Boolean | Defines visibility of the marker

_org.primefaces.model.map.Polyline_ extends _org.primefaces.model.map.Overlay_

| Property | Default | Type | Description
| --- | --- | --- | --- |
| paths | null | List | List of coordinates
| strokeColor | null | String | Color of a line
| strokeOpacity | 1 | Double | Opacity of a line
| strokeWeight | 1 | Integer | Width of a line

_org.primefaces.model.map.Polygon_ extends _org.primefaces.model.map.Overlay_

| Property | Default | Type | Description
| --- | --- | --- | --- |
fillColor | null | String | Background color of the polygon
fillOpacity | 1 | Double | Opacity of the polygon
paths | null | List | List of coordinates
strokeColor | null | String | Color of a line
strokeOpacity | 1 | Double | Opacity of a line
strokeWeight | 1 | Integer | Weight of a line

_org.primefaces.model.map.Circle_ extends _org.primefaces.model.map.Overlay_

| Property | Default | Type | Description
| --- | --- | --- | --- |
center | null | LatLng | Center of the circle
fillColor | null | String | Background color of the circle.
fillOpacity | 1 | Double | Opacity of the circle.
radius | null | Double | Radius of the circle.
strokeColor | null | String | Stroke color of the circle.
strokeOpacity | 1 | Double | Stroke opacity of circle.
strokeWeight | 1 | Integer | Stroke weight of the circle.

_org.primefaces.model.map.Rectangle_ extends _org.primefaces.model.map.Overlay_

| Property | Default | Type | Description
| --- | --- | --- | --- |
bounds | null | LatLngBounds | Boundaries of the rectangle.
fillColor | null | String | Background color of the rectangle.
fillOpacity | 1 | Double | Opacity of the rectangle.
strokeColor | null | String | Stroke color of the rectangle.
strokeOpacity | 1 | Double | Stroke opacity of rectangle.
strokeWeight | 1 | Integer | Stroke weight of the rectangle.

_org.primefaces.model.map.LatLng_

| Property | Default | Type | Description
| --- | --- | --- | --- |
lat | null | double | Latitude of the coordinate
lng | null | double | Longitude of the coordinate

_org.primefaces.model.map.LatLngBounds_

| Property | Default | Type | Description
| --- | --- | --- | --- |
center | null | LatLng | Center coordinate of the boundary
northEast | null | LatLng | NorthEast coordinate of the boundary
southWest | null | LatLng | SouthWest coordinate of the boundary

_org.primefaces.model.map.GeocodeResult_

| Property | Default | Type | Description
| --- | --- | --- | --- |
address | null | String | String representation of the address.
latLng | null | LatLng | Coordinates of the address.

## GMap Event API
All classes in event API extends from _jakarta.faces.event.FacesEvent_.

_org.primefaces.event.map.MarkerDragEvent_

| Property | Default | Type | Description
| --- | --- | --- | --- |
marker | null | Marker | Dragged marker instance

_org.primefaces.event.map.OverlaySelectEvent_

| Property | Default | Type | Description
| --- | --- | --- | --- |
overlay | null | Overlay | Selected overlay instance

_org.primefaces.event.map.PointSelectEvent_

| Property | Default | Type | Description
| --- | --- | --- | --- |
latLng | null | LatLng | Coordinates of the selected point

_org.primefaces.event.map.StateChangeEvent_

| Property | Default | Type | Description
| --- | --- | --- | --- |
bounds | null | LatLngBounds | Boundaries of the map
zoomLevel | 0 | Integer | Zoom level of the map

_org.primefaces.event.map.GeocodeEvent_

| Property | Default | Type | Description
| --- | --- | --- | --- |
query | null | String | Query of the geocode search.
results | null | List<GeocodeResult> | List of results represented as GeocodeResult.

_org.primefaces.event.map.ReverseGeocodeEvent_

| Property | Default | Type | Description
| --- | --- | --- | --- |
latlng | null | LatLng | Coordinates of the reverse geocode query.
addresses | null | List<String> | List of results represented as strings.