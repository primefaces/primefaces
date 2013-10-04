package org.primefaces.context;

import javax.faces.FacesException;
import javax.faces.context.FacesContext;
import javax.faces.context.FacesContextFactory;
import javax.faces.lifecycle.Lifecycle;

/**
 * {@link FacesContextFactory} to wrap the {@link FacesContext} with our {@link PrimeFacesContext}.
 */
public class PrimeFacesContextFactory extends FacesContextFactory {

	private FacesContextFactory wrapped;
	
	// #6212 - don't remove it 
	public PrimeFacesContextFactory() {
		
	}
	
	public PrimeFacesContextFactory(FacesContextFactory wrapped) {
		this.wrapped = wrapped;
	}
	
	@Override
	public FacesContext getFacesContext(Object context, Object request, Object response, Lifecycle lifecycle)
			throws FacesException {

		return new PrimeFacesContext(wrapped.getFacesContext(context, request, response, lifecycle));
	}

    public FacesContextFactory getWrapped() {
        return wrapped;
    }
}
