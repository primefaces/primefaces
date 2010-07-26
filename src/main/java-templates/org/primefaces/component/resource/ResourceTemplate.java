import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ComponentSystemEvent;

import org.primefaces.component.resource.Resource;


	public String toString() {
		return getName();
	}
	
	@Override
	public void processEvent(ComponentSystemEvent event) throws AbortProcessingException {
		FacesContext facesContext = getFacesContext();
		javax.faces.component.UIViewRoot viewroot = facesContext.getViewRoot();
		if(!resourceExists(facesContext, getName())) {
			viewroot.addComponentResource(facesContext, this, "head");
		}
	}