/**
 * __PrimeFaces Google Maps Widget__
 * 
 * GMap is a map component integrated with Google Maps API V3.
 * 
 * @typedef {(google.maps.Marker | google.maps.Circle | google.maps.Polyline | google.maps.Polygon | google.maps.Rectangle) & PrimeFaces.widget.GMap.IdProviding} PrimeFaces.widget.GMap.Overlay
 * An overlay shape that extends the shapes and markers as defined by the maps API. Adds an ID property for identifying
 * the shape or marker.
 * 
 * @typedef PrimeFaces.widget.GMap.OnPointClickCallback Javascript callback to execute when a point on map is clicked.
 * See also {@link GMapCfg.onPointClick}.
 * @param {google.maps.MouseEvent | google.maps.IconMouseEvent} PrimeFaces.widget.GMap.OnPointClickCallback.event The
 * mouse or click event that occurred.
 * 
 * @interface {PrimeFaces.widget.GMap.IdProviding} IdProviding Interface for objects that provide an ID that uniquely
 * identifies the object.
 * @prop {string} IdProviding.id The ID that uniquely identifies this object.
 * 
 * @prop {google.maps.Map} map The current google maps instance.
 * @prop {PrimeFaces.widget.GMap.Overlay} selectedOverlay The currently selected and active overlay shape.
 * @prop {google.maps.LatLngBounds} viewport The spherical portion of the earth's surface that is currently shown in the map
 * viewport.
 * 
 * @interface {PrimeFaces.widget.GMapCfg} cfg The configuration for the {@link  GMap| GMap widget}.
 * You can access this configuration via {@link PrimeFaces.widget.BaseWidget.cfg|BaseWidget.cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 * @extends {PrimeFaces.widget.DeferredWidgetCfg} cfg
 * @extends {google.maps.MapOptions} cfg
 * 
 * @prop {(google.maps.Circle & PrimeFaces.widget.GMap.IdProviding)[]} cfg.circles List of overlay circular shapes added
 * to this map.
 * @prop {boolean} cfg.fitBounds Defines if center and zoom should be calculated automatically to contain all markers on
 * the map.
 * @prop {google.maps.InfoWindow} cfg.infoWindow The current info window instance, if any info window was created yet.
 * @prop {string} cfg.infoWindowContent HTML string with the contents of the info window, as fetched from the server.
 * @prop {(google.maps.Marker & PrimeFaces.widget.GMap.IdProviding)[]} cfg.markers A list of markers to display on the
 * map.
 * @prop {PrimeFaces.widget.GMap.OnPointClickCallback} cfg.onPointClick Javascript callback to execute when a point on
 * map is clicked.
 * @prop {(google.maps.Polygon & PrimeFaces.widget.GMap.IdProviding)[]} cfg.polygons List of overlay polygonal shapes
 * added to this map.
 * @prop {(google.maps.Polyline & PrimeFaces.widget.GMap.IdProviding)[]} cfg.polylines List of overlay polyline shapes
 * added to this map.
 * @prop {(google.maps.Rectangle & PrimeFaces.widget.GMap.IdProviding)[]} cfg.rectangles List of overlay rectangular
 * shapes added to this map.
 */
PrimeFaces.widget.GMap = PrimeFaces.widget.DeferredWidget.extend({

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    init: function(cfg) {
        this._super(cfg);

        this.renderDeferred();
    },

    /**
     * @include
     * @override
     * @protected
     * @inheritdoc
     */
    _render: function() {
        this.map = new google.maps.Map(document.getElementById(this.id), this.cfg);
        this.cfg.fitBounds = !(this.cfg.fitBounds === false);
        this.viewport = this.map.getBounds();

        //conf markers
        if(this.cfg.markers) {
            this.configureMarkers();
        }

        //add polylines
        if(this.cfg.polylines) {
            this.configurePolylines();
        }

        //add polygons
        if(this.cfg.polygons) {
            this.configurePolygons();
        }

        //add circles
        if(this.cfg.circles) {
            this.configureCircles();
        }

        //add rectangles
        if(this.cfg.rectangles) {
            this.configureRectangles();
        }

        //general map events
        this.configureEventListeners();

        //fit auto bounds
        if(this.cfg.fitBounds && this.viewport)
            this.map.fitBounds(this.viewport);

        //bind infowindow domready for dynamic content.
        if(this.cfg.infoWindow){
            var _self = this;
            google.maps.event.addListener(this.cfg.infoWindow, 'domready', function() {
                _self.loadWindow(_self.cfg.infoWindowContent);
            });
        }
    },

    /**
     * Returns the current google maps instance.
     * @return {google.maps.Map} The current map instance.
     */
    getMap: function() {
        return this.map;
    },

    /**
     * The info window that can be displayed to provide detailed information when a marker is selected.
     * @return {google.maps.InfoWindow | undefined} The current info window instance, if any exists.
     */
    getInfoWindow: function() {
        return this.cfg.infoWindow;
    },

    /**
     * Writes the given HTML content into the info window.
     * @private
     * @param {string} content HTML content for the info window. 
     */
    loadWindow: function(content){
        this.jq.find(PrimeFaces.escapeClientId(this.getInfoWindow().id + '_content')).html(content||'');
    },

    /**
     * Loads the contents of the info window from the server and open the info window.
     * @private
     * @param {XMLDocument} responseXML The XML that was returned by the AJAX request made to fetch the contents of the
     * info window. 
     * @return {boolean} `true` if the info window load was initiated successfully, or `false` otherwise.
     */
    openWindow: function(responseXML) {
        var infoWindow = this.getInfoWindow();
        var $this = this;

        PrimeFaces.ajax.Response.handle(responseXML, null, null, {
            widget: infoWindow,
            handle: function(content) {
                $this.cfg.infoWindowContent = content;
                infoWindow.setContent('<div id="' + infoWindow.id + '_content">' + content + '</div>');

                infoWindow.open($this.getMap(), $this.selectedOverlay);
            }
        });

        return true;
    },

    /**
     * Adds and sets up all configured markers for the gmap.
     * @private
     */
    configureMarkers: function() {
        var _self = this;

        for(var i=0; i < this.cfg.markers.length; i++) {
            var marker = this.cfg.markers[i];
            marker.setMap(this.map);

            //extend viewport
            if(this.cfg.fitBounds)
                this.extendView(marker);

            //overlay select
            google.maps.event.addListener(marker, 'click', function(event) {
                _self.fireOverlaySelectEvent(event, this, 1);
            });
            google.maps.event.addListener(marker, 'dblclick', function(event) {
                _self.fireOverlaySelectEvent(event, this, 2);
            });

            //marker drag
            google.maps.event.addListener(marker, 'dragend', function(event) {
                _self.fireMarkerDragEvent(event, this);
            });
        }
    },

    /**
     * Calls the behavior for when a marker was dragged.
     * @private
     * @param {google.maps.MouseEvent | google.maps.IconMouseEvent} event Event that occurred.
     * @param {google.maps.MarkerOptions} marker The marker that was dragged.
     */
    fireMarkerDragEvent: function(event, marker) {
        if(this.hasBehavior('markerDrag')) {
            var ext = {
                params: [
                    {name: this.id + '_markerId', value: marker.id},
                    {name: this.id + '_lat', value: event.latLng.lat()},
                    {name: this.id + '_lng', value: event.latLng.lng()}
                ]
            };

            this.callBehavior('markerDrag', ext);
        }
    },

    /**
     * Finds the geocode for the given address and calls the server-side `geocode` behavior, if such a behavior exists.
     * Use `<p:ajax event="geocode" listener="#{geocodeView.onGeocode}" update="@this" />` on the component to define a
     * behavior.
     * @param {string} address Address for which to find a geocode.
     */
    geocode: function(address) {
        var $this = this;

        if(this.hasBehavior('geocode')) {
            var geocoder = new google.maps.Geocoder(),
                lats = [],
                lngs = [],
                addresses = [];

            geocoder.geocode({'address': address}, function(results, status) {

                if (status == google.maps.GeocoderStatus.OK) {
                    for(var i = 0; i < results.length; i++) {
                        var location = results[i].geometry.location;
                        lats.push(location.lat());
                        lngs.push(location.lng());
                        addresses.push(results[i].formatted_address);
                    }

                    if(results.length) {
                        var ext = {
                            params: [
                                {name: $this.id + '_query', value: address},
                                {name: $this.id + '_addresses', value: addresses.join('_primefaces_')},
                                {name: $this.id + '_lat', value: lats.join()},
                                {name: $this.id + '_lng', value: lngs.join()}
                            ]
                        };

                        $this.callBehavior('geocode', ext);
                    }
                }
                else {
                    PrimeFaces.error('Geocode was not found');
                }
            });

        }
    },

    /**
     * Attempts to find an address for the given lattitude and longitude, and calls the `reverseGeocode` behavior with
     * the result. Use `<p:ajax event="reverseGeocode" listener="#{geocodeView.onReverseGeocode}" update="@this" />` on
     * the component to define a behavior.
     * @param {number} lat Latitude to look up, specified in degrees within the range `[-90, 90]`.
     * @param {number} lng Longitude to look up, specified in degrees within the range `[-180, 180]`.
     */
    reverseGeocode: function(lat, lng) {
        var $this = this;

        if(this.hasBehavior('reverseGeocode')) {
            var geocoder = new google.maps.Geocoder(),
                latlng = new google.maps.LatLng(lat, lng),
                addresses = [];

            geocoder.geocode({'latLng': latlng}, function(results, status) {

                if (status == google.maps.GeocoderStatus.OK) {
                    for(var i = 0; i < results.length; i++) {
                        if (results[i]) {
                            addresses[i] = results[i].formatted_address;
                        }
                    }

                    if(0 < addresses.length) {
                        var ext = {
                            params: [
                                {name: $this.id + '_address', value: addresses.join('_primefaces_')},
                                {name: $this.id + '_lat', value: lat},
                                {name: $this.id + '_lng', value: lng}
                            ]
                        };

                        $this.callBehavior('reverseGeocode', ext);
                    }
                    else {
                        PrimeFaces.error('No results found');
                    }
                }
                else {
                    PrimeFaces.error('Geocoder failed');
                }
           });

        }
    },

    /**
     * Adds the overlay for a polyline shape.
     * @private
     */
    configurePolylines: function() {
        this.addOverlays(this.cfg.polylines);
    },

    /**
     * Adds the overlay for a circle shape.
     * @private
     */
    configureCircles: function() {
        this.addOverlays(this.cfg.circles);
    },

    /**
     * Adds the overlay for a rectangular shape.
     * @private
     */
    configureRectangles: function() {
        this.addOverlays(this.cfg.rectangles);
    },

    /**
     * Adds the overlay for a polygonal shape.
     * @private
     */
    configurePolygons: function() {
        this.addOverlays(this.cfg.polygons);
    },

    /**
     * Triggers the behavior for when an overlay shape was selected.
     * @private
     * @param {google.maps.MouseEvent | google.maps.IconMouseEvent} event The event that occurred.
     * @param {PrimeFaces.widget.GMap.Overlay} overlay The shape that was selected.
     * @param {number} clickCount whether it was single or double click
     */
    fireOverlaySelectEvent: function(event, overlay, clickCount) {
        this.selectedOverlay = overlay;
        
        var ext = {
                params: [
                    {name: this.id + '_overlayId', value: overlay.id}
                ]
            };

        if (clickCount === 1 && this.hasBehavior('overlaySelect')) {
            this.callBehavior('overlaySelect', ext);
        }
        if (clickCount === 2 && this.hasBehavior('overlayDblSelect')) {
            this.callBehavior('overlayDblSelect', ext);
        }
    },

    /**
     * Adds some event listeners for click events and sets up some behaviors.
     * @private
     */
    configureEventListeners: function() {
        var _self = this;

        this.cfg.formId = $(PrimeFaces.escapeClientId(this.id)).parents('form:first').attr('id');

        //client side events
        if(this.cfg.onPointClick) {
            google.maps.event.addListener(this.map, 'click', function(event) {
                _self.cfg.onPointClick(event);
            });
        }

        //behaviors
        this.configureStateChangeListener();
        this.configurePointSelectListener();
    },

    /**
     * Sets up the event listeners for when the state of this map has changed.
     * @private
     */
    configureStateChangeListener: function() {
        var _self = this,
        onStateChange = function(event) {
            _self.fireStateChangeEvent(event);
        };

        google.maps.event.addListener(this.map, 'zoom_changed', onStateChange);
        google.maps.event.addListener(this.map, 'dragend', onStateChange);
    },

    /**
     * Triggers the behavior for when the state of this map has changed.
     * @private
     * @param {never} event The event that triggered the state change.
     */
    fireStateChangeEvent: function(event) {
        if(this.hasBehavior('stateChange')) {
            var bounds = this.map.getBounds();

            var ext = {
                params: [
                    {name: this.id + '_northeast', value: bounds.getNorthEast().lat() + ',' + bounds.getNorthEast().lng()},
                    {name: this.id + '_southwest', value: bounds.getSouthWest().lat() + ',' + bounds.getSouthWest().lng()},
                    {name: this.id + '_center', value: bounds.getCenter().lat() + ',' + bounds.getCenter().lng()},
                    {name: this.id + '_zoom', value: this.map.getZoom()}
                ]
            };

            this.callBehavior('stateChange', ext);
        }
    },

    /**
     * Sets up the event listeners for when a point on the map was selected.
     * @private
     */
    configurePointSelectListener: function() {
        var _self = this;

        google.maps.event.addListener(this.map, 'click', function(event) {
            _self.firePointSelectEvent(event, 1);
        });
        google.maps.event.addListener(this.map, 'dblclick', function(event) {
            _self.firePointSelectEvent(event, 2);
        });
    },

    /**
     * Triggers the behavior for when a point on the map was selected.
     * @private
     * @param {google.maps.MouseEvent | google.maps.IconMouseEvent} event The event that triggered the point selection.
     * @param {number} clickCount whether it was single or double click
     */
    firePointSelectEvent: function(event, clickCount) {
        var ext = {
                params: [
                    {name: this.id + '_pointLatLng', value: event.latLng.lat() + ',' + event.latLng.lng()}
                ]
            };
        
        if (clickCount === 1 && this.hasBehavior('pointSelect')) {
            this.callBehavior('pointSelect', ext);
        }
        if (clickCount === 2 && this.hasBehavior('pointDblSelect')) {
            this.callBehavior('pointDblSelect', ext);
        }
    },

    /**
     * Adds an overlay shape (circle, polyline, or polygon) to this map.
     * @private
     * @param {PrimeFaces.widget.GMap.Overlay} overlay Overlay shape to add to this map.
     */
    addOverlay: function(overlay) {
        overlay.setMap(this.map);
    },

    /**
     * Adds all overlay shapes (circle, polyline, or polygon) to this map.
     * @param {PrimeFaces.widget.GMap.Overlay[]} overlays A list of overlay shapes to add to this map.
     */
    addOverlays: function(overlays) {
        var _self = this;

        $.each(overlays, function(index, item){
            item.setMap(_self.map);

            _self.extendView(item);

            //bind overlay click event
            google.maps.event.addListener(item, 'click', function(event) {
                _self.fireOverlaySelectEvent(event, item, 1);
            });
            
            google.maps.event.addListener(item, 'dblclick', function(event) {
                _self.fireOverlaySelectEvent(event, item, 2);
            });
        })
    },

    /**
     * Adjusts (zooms out) the viewport of this map so that it fully shows the given shape.
     * @private
     * @param {PrimeFaces.widget.GMap.Overlay} overlay A shape for which to adjust the viewport.
     */
    extendView: function(overlay){
        if( this.cfg.fitBounds && overlay){
            var _self = this;
            this.viewport = this.viewport || new google.maps.LatLngBounds();
            if(overlay instanceof google.maps.Marker)
                this.viewport.extend(overlay.getPosition());

            else if(overlay instanceof google.maps.Circle || overlay instanceof google.maps.Rectangle)
                this.viewport.union(overlay.getBounds());

            else if(overlay instanceof google.maps.Polyline || overlay instanceof google.maps.Polygon)
                overlay.getPath().forEach(function(item, index){
                    _self.viewport.extend(item);
                });
        }
    },

    /**
     * Triggers a resize event and reapplies the current zoom level, redrawing the map. Useful when the browser viewport
     * was resized etc.
     */
    checkResize: function() {
        google.maps.event.trigger(this.map, 'resize');
        this.map.setZoom(this.map.getZoom());
    },

    /**
     * Sets the map viewport to contain the given bounds.
     * 
     * @see https://developers.google.com/maps/documentation/javascript/reference/map?hl=uk#Map.fitBounds
     * 
     * @param {google.maps.LatLngBounds | google.maps.LatLngBoundsLiteral} bounds The new bounds
     * @param {number | google.maps.Padding} [padding] Optional padding around the bounds. 
     */
    fitBounds: function(bounds, padding) {
        //remember the property set by PrimeFaces
        var original = this.map.fitBounds;

        //replace the code by the one provided by google maps api
        this.map.fitBounds = google.maps.Map.prototype.fitBounds;

        //execute fitBounds function
        this.map.fitBounds(bounds, padding);

        //restore PrimeFaces property
        this.map.fitBounds = original;
    }
});