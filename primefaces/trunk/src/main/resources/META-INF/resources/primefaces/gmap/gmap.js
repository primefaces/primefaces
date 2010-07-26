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
	var m = this;
	
	for(var i=0; i < this.cfg.markers.length; i++) {
		var marker = this.cfg.markers[i];
		marker.setMap(this.map);
		
		if(this.cfg.hasOverlaySelectListener) {
			this.addOverlaySelectListener(marker);
		}
				
		if(this.cfg.hasMarkerDragListener) {
			google.maps.event.addListener(marker, 'dragend', function(event) {
				var params = {};
				params[PrimeFaces.PARTIAL_PROCESS_PARAM] = m.id;
				params[m.id + '_markerDragged'] = true;
				params[m.id + '_markerId'] = this.id;
				params[m.id + '_lat'] = event.latLng.lat();
				params[m.id + '_lng'] = event.latLng.lng();
				
				if(m.cfg.onMarkerDragUpdate) {
					params[PrimeFaces.PARTIAL_UPDATE_PARAM] = m.cfg.onMarkerDragUpdate;
				}
				
				PrimeFaces.ajax.AjaxRequest(m.cfg.url, {formId:m.cfg.formId}, params);
			});
		}
	}
}

PrimeFaces.widget.GMap.prototype.configurePolylines = function() {
	this.addOverlays(this.cfg.polylines);
}

PrimeFaces.widget.GMap.prototype.configurePolygons = function() {
	this.addOverlays(this.cfg.polygons);
}

PrimeFaces.widget.GMap.prototype.addOverlaySelectListener = function(overlay) {
	var m = this;
	
	google.maps.event.addListener(overlay, 'click', function() {
		var requestConfig = {formId:m.cfg.formId};
			
		var params = {};
		params[PrimeFaces.PARTIAL_PROCESS_PARAM] = m.id;
		params[m.id + '_overlaySelected'] = true;
		params[m.id + '_overlayId'] = this.id;
		
		if(m.cfg.onOverlaySelectStart) requestConfig.onstart = m.cfg.onOverlaySelectStart;
		if(m.cfg.onOverlaySelectComplete) requestConfig.oncomplete = m.cfg.onOverlaySelectComplete;
		
		if(m.cfg.infoWindow) {
			params[PrimeFaces.PARTIAL_UPDATE_PARAM] = m.cfg.infoWindow.id;

			/**
			 * Custom PPR success handler to open a window
			 */
			var gmapOverlay = this;
			requestConfig.onsuccess = function(responseXML) {
				var xmlDoc = responseXML.documentElement,
				components = xmlDoc.getElementsByTagName("component"),
				state = xmlDoc.getElementsByTagName("state")[0].firstChild.data;
				
				PrimeFaces.ajax.AjaxUtils.updateState(state);
				
				var content = components[0].childNodes[1].firstChild.data;
				m.getInfoWindow().setContent(content);
				
				m.openWindow(gmapOverlay);
				
				return false;		//stop regular ppr process
			}
			
		} else if(m.cfg.onOverlaySelectUpdate) {
			params[PrimeFaces.PARTIAL_UPDATE_PARAM] = m.cfg.onOverlaySelectUpdate;
		}

		PrimeFaces.ajax.AjaxRequest(m.cfg.url, requestConfig, params);
	});
}

PrimeFaces.widget.GMap.prototype.configureEventListeners = function() {
	var m = this;
	
	//client side events
	if(this.cfg.onPointClick) {
		google.maps.event.addListener(this.map, 'click', function(event) {
			m.cfg.onPointClick(event);
		});
	}
	
	//server side events
	if(this.cfg.hasStateChangeListener)
		this.configureStateChangeListener();
	if(this.cfg.hasPointSelectListener)
		this.configurePointSelectListener();
}

PrimeFaces.widget.GMap.prototype.configureStateChangeListener = function() {
	var m = this,
	sendStateChangeEvent = function() {
		var params = {};
		params[PrimeFaces.PARTIAL_PROCESS_PARAM] = m.id;
		params[m.id + '_stateChanged'] = true;
		params[m.id + '_northeast'] = m.map.getBounds().getNorthEast().lat() + "," + m.map.getBounds().getNorthEast().lng();
		params[m.id + '_southwest'] = m.map.getBounds().getSouthWest().lat() + "," + m.map.getBounds().getSouthWest().lng();
		params[m.id + '_center'] = m.map.getBounds().getCenter().lat() + "," + m.map.getBounds().getCenter().lng();
		params[m.id + '_zoom'] = m.map.getZoom();

		if(m.cfg.onStateChangeUpdate) {
			params[PrimeFaces.PARTIAL_UPDATE_PARAM] = m.cfg.onStateChangeUpdate;
		}
		
		PrimeFaces.ajax.AjaxRequest(m.cfg.url, {formId:m.cfg.formId}, params);
	};
	
	google.maps.event.addListener(this.map, 'zoom_changed', sendStateChangeEvent);
	google.maps.event.addListener(this.map, 'dragend', sendStateChangeEvent);
}

PrimeFaces.widget.GMap.prototype.configurePointSelectListener = function() {	
	var m = this;
	
	google.maps.event.addListener(this.map, 'click', function(event) {
		var params = {};
		params[PrimeFaces.PARTIAL_PROCESS_PARAM] = m.id;
		params[m.id + '_pointSelected'] = true;
		params[m.id + '_pointLatLng'] = event.latLng.lat() + "," + event.latLng.lng();

		if(m.cfg.onPointSelectUpdate) {
			params[PrimeFaces.PARTIAL_UPDATE_PARAM] = m.cfg.onPointSelectUpdate;
		}
		
		PrimeFaces.ajax.AjaxRequest(m.cfg.url, {formId:m.cfg.formId}, params);
	});
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