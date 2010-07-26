package org.primefaces.event;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.FacesListener;

public interface NodeSelectListener extends FacesListener {

    public void processNodeSelect(NodeSelectEvent event) throws AbortProcessingException;
}
