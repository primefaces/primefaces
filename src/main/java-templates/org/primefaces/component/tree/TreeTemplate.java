import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.NodeExpandEvent;
import org.primefaces.event.NodeCollapseEvent;

	public void broadcast(javax.faces.event.FacesEvent event) throws javax.faces.event.AbortProcessingException {
		super.broadcast(event);
		
		FacesContext facesContext = FacesContext.getCurrentInstance();
		MethodExpression me = null;

		if(event instanceof NodeSelectEvent) {
			me = getNodeSelectListener();
		} else if(event instanceof NodeExpandEvent) {
			me = getNodeExpandListener();
		} else if(event instanceof NodeCollapseEvent) {
			me = getNodeCollapseListener();
		}
		
		if (me != null) {
			me.invoke(facesContext.getELContext(), new Object[] {event});
		}
	}