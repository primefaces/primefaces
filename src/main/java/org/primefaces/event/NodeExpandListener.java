package org.primefaces.event;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.FacesListener;

public interface NodeExpandListener extends FacesListener {

    public void processNodeExpand(NodeExpandEvent event) throws AbortProcessingException;
}