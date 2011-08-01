PrimeFaces.widget.GMap = function(id, cfg) {
	this.id = id;
	this.cfg = cfg;
	
	this.map = new google.maps.Map(document.getElementById(this.id), this.cfg);
	
	//conf markers
	if(this.cfg.markers) {
		this.configureMarkers();
	}
	
	//add polylines
	if(this.cfg.polylines) {
		this.configurePolylines();
	}
	
	//add polylines
	if(this.cfg.polygons) {
		this.configurePolygons();
	}
	
	//general map events
	this.configureEventListeners();
}

PrimeFaces.widget.GMap.prototype.getMap = function() {
	return this.map;
}

PrimeFaces.widget.GMap.prototype.getInfoWindow = function() {
	return this.cfg.infoWindow;
}

PrimeFaces.widget.GMap.prototype.openWindow = function(responseXML) {
    var xmlDoc = $(responseXML.documentElement),
    updates = xmlDoc.find("update"),
    infoWindow = this.getInfoWindow();

    for(var i=0; i < updates.length; i++) {
        var update = updates.eq(i),
        id = update.attr('id'),
        content = update.text();

        if(id == infoWindow.id){
            infoWindow.setContent(content);

            infoWindow.open(this.getMap(), this.selectedOverlay);
        }
        else {
            PrimeFaces.ajax.AjaxUtils.updateElement(id, content);
        }
    }

    PrimeFaces.ajax.AjaxUtils.handleResponse.call(this, xmlDoc);

    return true;
}

PrimeFaces.widget.GMap.prototype.configureMarkers = function() {
	var _self = this;
	
	for(var i=0; i < this.cfg.markers.length; i++) {
		var marker = this.cfg.markers[i];
		marker.setMap(this.map);

        //overlay select
        google.maps.event.addListener(marker, 'click', function(event) {
            _self.fireOverlaySelectEvent(event, this);
        });

        //marker drag
        google.maps.event.addListener(marker, 'dragend', function(event) {
            _self.fireMarkerDragEvent(event, this);
        });
	}
}

PrimeFaces.widget.GMap.prototype.fireMarkerDragEvent = function(event, marker) {
    if(this.hasBehavior('markerDrag')) {
        var markerDragBehavior = this.cfg.behaviors['markerDrag'];

        var ext = {
            params: {}
        };
        ext.params[this.id + '_markerId'] = marker.id;
        ext.params[this.id + '_lat'] = event.latLng.lat();
        ext.params[this.id + '_lng'] = event.latLng.lng();

        markerDragBehavior.call(this, event, ext);
    }
}

PrimeFaces.widget.GMap.prototype.configurePolylines = function() {
	this.addOverlays(this.cfg.polylines);
}

PrimeFaces.widget.GMap.prototype.configurePolygons = function() {
	this.addOverlays(this.cfg.polygons);
}

PrimeFaces.widget.GMap.prototype.fireOverlaySelectEvent = function(event, overlay) {
    this.selectedOverlay = overlay;
    
    if(this.hasBehavior('overlaySelect')) {
        var overlaySelectBehavior = this.cfg.behaviors['overlaySelect'];

        var ext = {
            params: {}
        };
        ext.params[this.id + '_overlayId'] = overlay.id;

        overlaySelectBehavior.call(this, event, ext);
    }
}

PrimeFaces.widget.GMap.prototype.configureEventListeners = function() {
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
}

PrimeFaces.widget.GMap.prototype.configureStateChangeListener = function() {
    var _self = this,
    onStateChange = function(event) {
        _self.fireStateChangeEvent(event);
    };

	google.maps.event.addListener(this.map, 'zoom_changed', onStateChange);
	google.maps.event.addListener(this.map, 'dragend', onStateChange);
}

PrimeFaces.widget.GMap.prototype.fireStateChangeEvent = function(event) {
    if(this.hasBehavior('stateChange')) {
        var stateChangeBehavior = this.cfg.behaviors['stateChange'];

        var ext = {
            params: {}
        };
        ext.params[this.id + '_northeast'] = this.map.getBounds().getNorthEast().lat() + "," + this.map.getBounds().getNorthEast().lng();
        ext.params[this.id + '_southwest'] = this.map.getBounds().getSouthWest().lat() + "," + this.map.getBounds().getSouthWest().lng();
        ext.params[this.id + '_center'] = this.map.getBounds().getCenter().lat() + "," + this.map.getBounds().getCenter().lng();
        ext.params[this.id + '_zoom'] = this.map.getZoom();

        stateChangeBehavior.call(this, event, ext);
    }
}

PrimeFaces.widget.GMap.prototype.configurePointSelectListener = function() {	
	var _self = this;
	
	google.maps.event.addListener(this.map, 'click', function(event) {
		_self.firePointSelectEvent(event);
	});
}

PrimeFaces.widget.GMap.prototype.firePointSelectEvent = function(event) {
    if(this.hasBehavior('pointSelect')) {
        var pointSelectBehavior = this.cfg.behaviors['pointSelect'];

        var ext = {
            params: {}
        };
        ext.params[this.id + '_pointLatLng'] = event.latLng.lat() + "," + event.latLng.lng();

        pointSelectBehavior.call(this, event, ext);
    }
}

PrimeFaces.widget.GMap.prototype.addOverlay = function(overlay) {
	overlay.setMap(this.map);
}

PrimeFaces.widget.GMap.prototype.addOverlays = function(overlays) {
	for(var i=0; i < overlays.length; i++) {
		overlays[i].setMap(this.map);
		
		if(this.cfg.hasOverlaySelectListener) {
			this.addOverlaySelectListener(overlays[i]);
		}
	}
}

PrimeFaces.widget.GMap.prototype.checkResize = function() {
    google.maps.event.trigger(this.map, 'resize');
    this.map.setZoom(this.map.getZoom());
}

PrimeFaces.widget.GMap.prototype.hasBehavior = function(event) {
    if(this.cfg.behaviors) {
        return this.cfg.behaviors[event] != undefined;
    }
    
    return false;
}