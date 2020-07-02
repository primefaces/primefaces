# GMap

GMap is a map component integrated with Google Maps API V3.

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
| model | null | MapModel | An org.primefaces.model.MapModel instance.
| style | null | String | Inline style of the map container.
| styleClass | null | String | Style class of the map container.
| type | null | String | Type of the map.
| center | null | String | Center point of the map.
| zoom | 8 | Integer | Defines the initial zoom level.
| streetView | false | Boolean | Controls street view support.
| disableDefaultUI | false | Boolean | Disables default UI controls
| navigationControl | true | Boolean | Defines visibility of navigation control.
| mapTypeControl | true | Boolean | Defines visibility of map type control.
| draggable | true | Boolean | Defines draggability of map.
| disabledDoubleClickZoom | false | Boolean | Disables zooming on mouse double click.
| onPointClick | null | String | Javascript callback to execute when a point on map is clicked.
| fitBounds | true | Boolean | Defines if center and zoom should be calculated automatically to contain all markers on the map.
| scrollWheel | false | Boolean | Controls scrollwheel zooming on the map.

## Getting started with GMap
First thing to do is placing V3 of the Google Maps API that the GMap is based on. Make sure to
register a key for your map.

```js
<script src="https://maps.googleapis.com/maps/api/js? key=YOUR_API_KEY&callback=initMap" async defer></script>
```
As Google Maps api states, mandatory sensor parameter is used to specify if your application
requires a sensor like GPS locator. Four options are required to place a gmap on a page, these are
center, zoom, type and style.

```xhtml
<p:gmap center="41.381542, 2.122893" zoom="15" type="hybrid" style="width:600px;height:400px" />
```
_center_ : Center of the map in lat, lng format
_zoom_ : Zoom level of the map
_type_ : Type of map, valid values are, "hybrid", "satellite", "hybrid" and "terrain".
_style_ : Dimensions of the map.


## MapModel
GMap is backed by an _org.primefaces.model.map.MapModel_ instance, PrimeFaces provides
_org.primefaces.model.map.DefaultMapModel_ as the default implementation. API Docs of all GMap
related model classes are available at the end of GMap section and also at javadocs of PrimeFaces.

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
Geocoding support is provided by client side api. Results are then passed to the backing bean using
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

## Ajax Behavior Events
GMap provides many custom ajax behavior events for you to hook-in to various features.

| Event | Listener Parameter | Fired |
| --- | --- | --- |
| overlaySelect | org.primefaces.event.map.OverlaySelectEvent | When an overlay is selected.
| stateChange | org.primefaces.event.map.StateChangeEvent | When map state changes.
| pointSelect | org.primefaces.event.map.PointSelectEvent | When an empty point is selected.
| markerDrag | org.primefaces.event.map.MarkerDragEvent | When a marker is dragged.

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
In case you need to access native google maps api with javascript, use provided _getMap()_ method.

```js
var gmap = PF('yourWidgetVar').getMap();
//gmap is a google.maps.Map instance
```
Full map api is provided at;

http://code.google.com/apis/maps/documentation/javascript/reference.html

## GMap API
_org.primefaces.model.map.MapModel_ ( _org.primefaces.model.map.DefaultMapModel_ is the default
implementation)


| Method | Description |
| --- | --- |
| addOverlay(Overlay overlay) |  Adds an overlay to map
| List<Marker> getMarkers() | Returns the list of markers
| List<Polyline> getPolylines() | Returns the list of polylines
| List<Polygon> getPolygons() | Returns the list of polygons
| List<Circle> getCircles() | Returns the list of circles
| List<Rectangle> getRectangles() | Returns the list of rectangles.
| Overlay findOverlay(String id) | Finds an overlay by it’s unique id


_org.primefaces.model.map.Overlay_

| Property | Default | Type | Description
| --- | --- | --- | --- |
| id | null | String | Id of the overlay, generated and used internally
| data | null | Object | Data represented in marker
| zindex | null | Integer | Z-Index of the overlay

_org.primefaces.model.map.Marker_ extends _org.primefaces.model.map.Overlay_

| Property | Default | Type | Description
| --- | --- | --- | --- |
| title | null | String | Text to display on rollover
| latlng | null | LatLng | Location of the marker
| icon | null | String | Icon of the foreground
| shadow | null | String | Shadow image of the marker
| cursor | pointer | String | Cursor to display on rollover
| draggable | 0 | Boolean | Defines if marker can be dragged
| clickable | 1 | Boolean | Defines if marker can be dragged
| flat | 0 | Boolean | If enabled, shadow image is not displayed
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
paths | null | List | List of coordinates
strokeColor | null | String | Color of a line
strokeOpacity | 1 | Double | Opacity of a line
strokeWeight | 1 | Integer | Weight of a line
fillColor | null | String | Background color of the polygon
fillOpacity | 1 | Double | Opacity of the polygon

_org.primefaces.model.map.Circle_ extends _org.primefaces.model.map.Overlay_

| Property | Default | Type | Description
| --- | --- | --- | --- |
center | null | LatLng | Center of the circle
radius | null | Double | Radius of the circle.
strokeColor | null | String | Stroke color of the circle.
strokeOpacity | 1 | Double | Stroke opacity of circle.
strokeWeight | 1 | Integer | Stroke weight of the circle.
fillColor | null | String | Background color of the circle.
fillOpacity | 1 | Double | Opacity of the circle.

_org.primefaces.model.map.Rectangle_ extends _org.primefaces.model.map.Overlay_

| Property | Default | Type | Description
| --- | --- | --- | --- |
bounds | null | LatLngBounds | Boundaries of the rectangle.
strokeColor | null | String | Stroke color of the rectangle.
strokeOpacity | 1 | Double | Stroke opacity of rectangle.
strokeWeight | 1 | Integer | Stroke weight of the rectangle.
fillColor | null | String | Background color of the rectangle.
fillOpacity | 1 | Double | Opacity of the rectangle.

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
All classes in event api extends from _javax.faces.event.FacesEvent_.

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