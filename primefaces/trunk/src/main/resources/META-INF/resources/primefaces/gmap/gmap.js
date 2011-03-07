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

PrimeFaces.widget.GMap.prototype.openWindow = function(marker) {
	this.getInfoWindow().open(this.getMap(), marker);
}

PrimeFaces.widget.GMap.prototype.configureMarkers = function() {
	var _self = this;
	
	for(var i=0; i < this.cfg.markers.length; i++) {
		var marker = this.cfg.markers[i];
		marker.setMap(this.map);
		
		if(this.cfg.hasOverlaySelectListener) {
			this.addOverlaySelectListener(marker);
		}
				
		if(this.cfg.hasMarkerDragListener) {
			google.maps.event.addListener(marker, 'dragend', function(event) {
                _self.fireMarkerDragEvent(event, this);
			});
		}
	}
}

PrimeFaces.widget.GMap.prototype.fireMarkerDragEvent = function(event, marker) {
    var options = {
        source: this.id,
        process: this.id,
        formId: this.cfg.formId
    };

    if(this.cfg.onMarkerDragUpdate) {
        options.update = this.cfg.onMarkerDragUpdate;
    }

    var params = {};
    params[this.id + '_markerDragged'] = true;
    params[this.id + '_markerId'] = marker.id;
    params[this.id + '_lat'] = event.latLng.lat();
    params[this.id + '_lng'] = event.latLng.lng();

    options.params = params;

    PrimeFaces.ajax.AjaxRequest(options);
}

PrimeFaces.widget.GMap.prototype.configurePolylines = function() {
	this.addOverlays(this.cfg.polylines);
}

PrimeFaces.widget.GMap.prototype.configurePolygons = function() {
	this.addOverlays(this.cfg.polygons);
}

PrimeFaces.widget.GMap.prototype.addOverlaySelectListener = function(overlay) {
	var _self = this;
	
	google.maps.event.addListener(overlay, 'click', function(event) {
        _self.fireOverlaySelectEvent(event, this);
	});
}

PrimeFaces.widget.GMap.prototype.fireOverlaySelectEvent = function(event, overlay) {
    var _self = this,
    options = {
        source: this.id,
        process: this.id,
        formId: this.cfg.formId
    };
   
    var params = {};
    params[this.id + '_overlaySelected'] = true;
    params[this.id + '_overlayId'] = overlay.id;

    if(this.cfg.onOverlaySelectStart) options.onstart = this.cfg.onOverlaySelectStart;
    if(this.cfg.onOverlaySelectComplete) options.oncomplete = this.cfg.onOverlaySelectComplete;

    if(this.cfg.infoWindow) {
        var infoWindow = this.getInfoWindow();
        options.update = infoWindow.id;

        options.onsuccess = function(responseXML) {
            var xmlDoc = responseXML.documentElement,
            updates = xmlDoc.getElementsByTagName("update");

            for(var i=0; i < updates.length; i++) {
                var id = updates[i].attributes.getNamedItem("id").nodeValue,
                content = updates[i].firstChild.data;

                if(id == infoWindow.id){
                    infoWindow.setContent(content);

                    _self.openWindow(overlay);
                }
                else {
                    PrimeFaces.ajax.AjaxUtils.updateElement(id, content);
                }
            }

            return false;
        };

    } else if(this.cfg.onOverlaySelectUpdate) {
        options.update = this.cfg.onOverlaySelectUpdate;
    }

    options.params = params;

    PrimeFaces.ajax.AjaxRequest(options);
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
	
	//server side events
	if(this.cfg.hasStateChangeListener)
		this.configureStateChangeListener();
	if(this.cfg.hasPointSelectListener)
		this.configurePointSelectListener();
}

PrimeFaces.widget.GMap.prototype.configureStateChangeListener = function() {
    var _self = this,
    onStateChange = function() {
        _self.fireStateChangeEvent();
    };

	google.maps.event.addListener(this.map, 'zoom_changed', onStateChange);
	google.maps.event.addListener(this.map, 'dragend', onStateChange);
}

PrimeFaces.widget.GMap.prototype.fireStateChangeEvent = function() {
    var options = {
        source: this.id,
        process: this.id,
        formId: this.cfg.formId
    };

    if(this.cfg.onStateChangeUpdate) {
        options.update = this.cfg.onStateChangeUpdate;
    }

    var params = {};
    params[this.id + '_stateChanged'] = true;
    params[this.id + '_northeast'] = this.map.getBounds().getNorthEast().lat() + "," + this.map.getBounds().getNorthEast().lng();
    params[this.id + '_southwest'] = this.map.getBounds().getSouthWest().lat() + "," + this.map.getBounds().getSouthWest().lng();
    params[this.id + '_center'] = this.map.getBounds().getCenter().lat() + "," + this.map.getBounds().getCenter().lng();
    params[this.id + '_zoom'] = this.map.getZoom();

    options.params = params;

    PrimeFaces.ajax.AjaxRequest(options);
}

PrimeFaces.widget.GMap.prototype.configurePointSelectListener = function() {	
	var _self = this;
	
	google.maps.event.addListener(this.map, 'click', function(event) {
		_self.firePointSelectEvent(event);
	});
}

PrimeFaces.widget.GMap.prototype.firePointSelectEvent = function(event) {
    var options = {
        source: this.id,
        process: this.id,
        formId: this.cfg.formId
    };

    if(this.cfg.onPointSelectUpdate) {
        options.update = this.cfg.onPointSelectUpdate;
    }
    
    var params = {};
    params[this.id + '_pointSelected'] = true;
    params[this.id + '_pointLatLng'] = event.latLng.lat() + "," + event.latLng.lng();
 
    options.params = params;

    PrimeFaces.ajax.AjaxRequest(options);
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