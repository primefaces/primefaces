/**
 * PrimeFaces Google Maps Widget
 */
PrimeFaces.widget.GMap = PrimeFaces.widget.BaseWidget.extend({
    
    init: function(cfg) {
        this._super(cfg);
        
        var _self = this;
	
        if(this.jq.is(':visible')) {
            this.render();
        }
        else {
            var hiddenParent = this.jq.parents('.ui-hidden-container:first'),
            hiddenParentWidget = hiddenParent.data('widget');

            if(hiddenParentWidget) {
                hiddenParentWidget.addOnshowHandler(function() {
                    return _self.render();
                });
            }
        }
    },
    
    render: function() {
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
        var xmlDoc = $(responseXML.documentElement),
        updates = xmlDoc.find("update"),
        infoWindow = this.getInfoWindow();

        for(var i=0; i < updates.length; i++) {
            var update = updates.eq(i),
            id = update.attr('id'),
            content = update.text();

            if(id == infoWindow.id){
                this.cfg.infoWindowContent = content;
                infoWindow.setContent('<div id="' + id + '_content">' + content + '</div>');

                infoWindow.open(this.getMap(), this.selectedOverlay);
            }
            else {
                PrimeFaces.ajax.AjaxUtils.updateElement.call(this, id, content);
            }
        }

        PrimeFaces.ajax.AjaxUtils.handleResponse.call(this, xmlDoc);

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
            var markerDragBehavior = this.cfg.behaviors['markerDrag'];

            var ext = {
                params: [
                    {name: this.id + '_markerId', value: marker.id},
                    {name: this.id + '_lat', value: event.latLng.lat()},
                    {name: this.id + '_lng', value: event.latLng.lng()}
                ]
            };

            markerDragBehavior.call(this, event, ext);
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
            var overlaySelectBehavior = this.cfg.behaviors['overlaySelect'];

            var ext = {
                params: [
                    {name: this.id + '_overlayId', value: overlay.id}
                ]
            };

            overlaySelectBehavior.call(this, event, ext);
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
            var stateChangeBehavior = this.cfg.behaviors['stateChange'],
            bounds = this.map.getBounds();

            var ext = {
                params: [
                    {name: this.id + '_northeast', value: bounds.getNorthEast().lat() + ',' + bounds.getNorthEast().lng()},
                    {name: this.id + '_southwest', value: bounds.getSouthWest().lat() + ',' + bounds.getSouthWest().lng()},
                    {name: this.id + '_center', value: bounds.getCenter().lat() + ',' + bounds.getCenter().lng()},
                    {name: this.id + '_zoom', value: this.map.getZoom()}
                ]
            };

            stateChangeBehavior.call(this, event, ext);
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
            var pointSelectBehavior = this.cfg.behaviors['pointSelect'];

            var ext = {
                params: [
                    {name: this.id + '_pointLatLng', value: event.latLng.lat() + ',' + event.latLng.lng()}
                ]
            };

            pointSelectBehavior.call(this, event, ext);
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
    
    hasBehavior: function(event) {
        if(this.cfg.behaviors) {
            return this.cfg.behaviors[event] != undefined;
        }

        return false;
    }
 
});