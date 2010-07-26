import org.primefaces.event.map.OverlaySelectEvent;
import org.primefaces.event.map.StateChangeEvent;
import org.primefaces.event.map.PointSelectEvent;
import org.primefaces.event.map.MarkerDragEvent;

	public void broadcast(javax.faces.event.FacesEvent event) throws javax.faces.event.AbortProcessingException {
		super.broadcast(event);
		
		FacesContext facesContext = FacesContext.getCurrentInstance();
		MethodExpression me = null;
		
		if(event instanceof OverlaySelectEvent) {
			me = getOverlaySelectListener();
		} else if(event instanceof StateChangeEvent) {
			me = getStateChangeListener();
		} else if(event instanceof PointSelectEvent) {
			me = getPointSelectListener();
		} else if(event instanceof MarkerDragEvent) {
			me = getMarkerDragListener();
		}
		
		if(me != null) {
			me.invoke(facesContext.getELContext(), new Object[] {event});
		}
	}
	
	public GMapInfoWindow getInfoWindow() {
		for(UIComponent kid : getChildren()) {
			if(kid instanceof GMapInfoWindow)
				return (GMapInfoWindow) kid;
		}
		
		return null;
	}
	
	public boolean hasEventListener() {
		return getOverlaySelectListener() != null || getStateChangeListener() != null || getPointSelectListener() != null || getMarkerDragListener() != null;
	}