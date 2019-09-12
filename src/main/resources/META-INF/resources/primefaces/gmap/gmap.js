/**
 * PrimeFaces Google Maps Widget
 */
PrimeFaces.widget.GMap = PrimeFaces.widget.DeferredWidget.extend({

    init: function(cfg) {
        this._super(cfg);

        this.renderDeferred();
    },

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

    getMap: function() {
        return this.map;
    },

    getInfoWindow: function() {
        return this.cfg.infoWindow;
    },

    loadWindow: function(content){
        this.jq.find(PrimeFaces.escapeClientId(this.getInfoWindow().id + '_content')).html(content||'');
    },

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
                _self.fireOverlaySelectEvent(event, this);
            });

            //marker drag
            google.maps.event.addListener(marker, 'dragend', function(event) {
                _self.fireMarkerDragEvent(event, this);
            });
        }
    },

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

    configurePolylines: function() {
        this.addOverlays(this.cfg.polylines);
    },

    configureCircles: function() {
        this.addOverlays(this.cfg.circles);
    },

    configureRectangles: function() {
        this.addOverlays(this.cfg.rectangles);
    },

    configurePolygons: function() {
        this.addOverlays(this.cfg.polygons);
    },

    fireOverlaySelectEvent: function(event, overlay) {
        this.selectedOverlay = overlay;

        if(this.hasBehavior('overlaySelect')) {
            var ext = {
                params: [
                    {name: this.id + '_overlayId', value: overlay.id}
                ]
            };

            this.callBehavior('overlaySelect', ext);
        }
    },

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

    configureStateChangeListener: function() {
        var _self = this,
        onStateChange = function(event) {
            _self.fireStateChangeEvent(event);
        };

        google.maps.event.addListener(this.map, 'zoom_changed', onStateChange);
        google.maps.event.addListener(this.map, 'dragend', onStateChange);
    },

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

    configurePointSelectListener: function() {
        var _self = this;

        google.maps.event.addListener(this.map, 'click', function(event) {
            _self.firePointSelectEvent(event);
        });
    },

    firePointSelectEvent: function(event) {
        if(this.hasBehavior('pointSelect')) {
            var ext = {
                params: [
                    {name: this.id + '_pointLatLng', value: event.latLng.lat() + ',' + event.latLng.lng()}
                ]
            };

            this.callBehavior('pointSelect', ext);
        }
    },

    addOverlay: function(overlay) {
        overlay.setMap(this.map);
    },

    addOverlays: function(overlays) {
        var _self = this;

        $.each(overlays, function(index, item){
            item.setMap(_self.map);

            _self.extendView(item);

            //bind overlay click event
            google.maps.event.addListener(item, 'click', function(event) {
                _self.fireOverlaySelectEvent(event, item);
            });
        })
    },

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

    checkResize: function() {
        google.maps.event.trigger(this.map, 'resize');
        this.map.setZoom(this.map.getZoom());
    },

    /**
     * Sets the Map viewport to contain the given bounds.
     * 
     * @see https://developers.google.com/maps/documentation/javascript/reference/map?hl=uk#Map.fitBounds
     * @param bounds the new bounds
     * @param padding (optional) google.maps.Padding
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