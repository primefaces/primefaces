import java.util.HashMap;
import java.util.Map;

	public void broadcast(javax.faces.event.FacesEvent event) throws javax.faces.event.AbortProcessingException {
		getParent().broadcast(event);
	}

    private static Map<String,String> locationMap;

    {
        locationMap = new HashMap<String,String>();
        locationMap.put("top", "north");
        locationMap.put("bottom", "south");
        locationMap.put("left", "west");
        locationMap.put("right", "east");
        locationMap.put("center", "center");
    }

    public String getLocation() {
        return locationMap.get(this.getPosition());
    }

    public String getCollapseIcon() {
        return "ui-icon-triangle-1-" + this.getLocation().substring(0,1);
    }