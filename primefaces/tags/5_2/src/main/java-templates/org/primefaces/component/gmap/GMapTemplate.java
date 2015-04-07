import org.primefaces.event.map.OverlaySelectEvent;
import org.primefaces.event.map.StateChangeEvent;
import org.primefaces.event.map.PointSelectEvent;
import org.primefaces.event.map.MarkerDragEvent;
import org.primefaces.event.map.GeocodeEvent;
import org.primefaces.event.map.ReverseGeocodeEvent;
import org.primefaces.model.map.LatLngBounds;
import org.primefaces.model.map.LatLng;
import org.primefaces.model.map.MapModel;
import org.primefaces.model.map.Marker;
import org.primefaces.model.map.GeocodeResult;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import javax.faces.component.UIComponent;
import javax.faces.event.FacesEvent;
import javax.faces.event.AjaxBehaviorEvent;
import org.primefaces.util.Constants;
import org.primefaces.context.RequestContext;

    private static final Collection<String> EVENT_NAMES = Collections.unmodifiableCollection(Arrays.asList("overlaySelect","stateChange", "pointSelect", "markerDrag", "geocode", "reverseGeocode"));

    @Override
    public void queueEvent(FacesEvent event) {
        FacesContext context = getFacesContext();
        Map<String,String> params = context.getExternalContext().getRequestParameterMap();
        String eventName = params.get(Constants.RequestParams.PARTIAL_BEHAVIOR_EVENT_PARAM);
        String clientId = this.getClientId(context);

        if(isSelfRequest(context)) {

            AjaxBehaviorEvent behaviorEvent = (AjaxBehaviorEvent) event;
            FacesEvent wrapperEvent = null;

            if(eventName.equals("overlaySelect")) {
                wrapperEvent = new OverlaySelectEvent(this, behaviorEvent.getBehavior(), this.getModel().findOverlay(params.get(clientId + "_overlayId")));

                //if there is info window, update and show it
                GMapInfoWindow infoWindow = getInfoWindow();
                if(infoWindow != null) {
                    RequestContext.getCurrentInstance().update(infoWindow.getClientId(context));
                }
            }
            else if(eventName.equals("stateChange")) {
                String[] centerLoc = params.get(clientId + "_center").split(",");
                String[] northeastLoc = params.get(clientId + "_northeast").split(",");
                String[] southwestLoc = params.get(clientId + "_southwest").split(",");
                int zoomLevel = Integer.valueOf(params.get(clientId + "_zoom"));

                LatLng center = new LatLng(Double.valueOf(centerLoc[0]), Double.valueOf(centerLoc[1]));
                LatLng northeast = new LatLng(Double.valueOf(northeastLoc[0]), Double.valueOf(northeastLoc[1]));
                LatLng southwest = new LatLng(Double.valueOf(southwestLoc[0]), Double.valueOf(southwestLoc[1]));

                wrapperEvent = new StateChangeEvent(this, behaviorEvent.getBehavior(), new LatLngBounds(northeast, southwest), zoomLevel, center);
            }
            else if(eventName.equals("pointSelect")) {
                String[] latlng = params.get(clientId + "_pointLatLng").split(",");
                LatLng position = new LatLng(Double.valueOf(latlng[0]), Double.valueOf(latlng[1]));

                wrapperEvent = new PointSelectEvent(this, behaviorEvent.getBehavior(), position);
            }
            else if(eventName.equals("markerDrag")) {
                Marker marker = (Marker) this.getModel().findOverlay(params.get(clientId + "_markerId"));
                double lat = Double.valueOf(params.get(clientId + "_lat"));
                double lng = Double.valueOf(params.get(clientId + "_lng"));
                marker.setLatlng(new LatLng(lat, lng));

                wrapperEvent = new MarkerDragEvent(this, behaviorEvent.getBehavior(), marker);
            }
            else if(eventName.equals("geocode")) {
                List<GeocodeResult> results = new ArrayList<GeocodeResult>();
                String query = params.get(clientId + "_query");
                String[] addresses = params.get(clientId + "_addresses").split("_primefaces_");                
                String[] lats = params.get(clientId + "_lat").split(",");
                String[] lngs = params.get(clientId + "_lng").split(",");

                for(int i = 0; i < addresses.length; i++) {
                    results.add(new GeocodeResult(addresses[i], new LatLng(Double.valueOf(lats[i]), Double.valueOf(lngs[i]))));
                }
                
                wrapperEvent = new GeocodeEvent(this, behaviorEvent.getBehavior(), query, results);
            }
            else if(eventName.equals("reverseGeocode")) {                
                List<String> addresses = new ArrayList<String>();
                String[] results = params.get(clientId + "_address").split("_primefaces_");
                for (int i = 0; i < results.length; i++) {
                    addresses.add(results[i]);
                }

                double lat = Double.valueOf(params.get(clientId + "_lat"));
                double lng = Double.valueOf(params.get(clientId + "_lng"));
                LatLng coord = new LatLng(lat, lng);

                wrapperEvent = new ReverseGeocodeEvent(this, behaviorEvent.getBehavior(), coord, addresses);
            }

            wrapperEvent.setPhaseId(behaviorEvent.getPhaseId());

            super.queueEvent(wrapperEvent);
        }
        else {
            super.queueEvent(event);
        }
    }
	
	public GMapInfoWindow getInfoWindow() {
		for(UIComponent kid : getChildren()) {
			if(kid instanceof GMapInfoWindow)
				return (GMapInfoWindow) kid;
		}
		
		return null;
	}
	
    private boolean isSelfRequest(FacesContext context) {
        return this.getClientId(context).equals(context.getExternalContext().getRequestParameterMap().get(Constants.RequestParams.PARTIAL_SOURCE_PARAM));
    }

    @Override
    public Collection<String> getEventNames() {
        return EVENT_NAMES;
    }

    