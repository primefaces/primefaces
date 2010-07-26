package org.primefaces.event;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.FacesListener;

public interface NodeCollapseListener extends FacesListener {

    public void processNodeCollapse(NodeCollapseEvent event) throws AbortProcessingException;
}
