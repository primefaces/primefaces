import java.util.HashMap;
import java.util.Map;

	public void broadcast(javax.faces.event.FacesEvent event) throws javax.faces.event.AbortProcessingException {
		getParent().broadcast(event);
	}

    public String getCollapseIcon() {
        return "ui-icon-triangle-1-" + this.getPosition().substring(0,1);
    }