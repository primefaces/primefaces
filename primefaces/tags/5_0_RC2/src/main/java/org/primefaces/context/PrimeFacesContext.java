package org.primefaces.context;

import javax.faces.context.FacesContext;
import javax.faces.context.FacesContextWrapper;

/**
 * Custom {@link FacesContextWrapper} to init and release our {@link RequestContext}.
 */
public class PrimeFacesContext extends FacesContextWrapper {

	private final FacesContext wrapped;
	
	public PrimeFacesContext(FacesContext wrapped) {
		this.wrapped = wrapped;
		
		RequestContext.setCurrentInstance(new DefaultRequestContext(wrapped), wrapped);
	}

	@Override
	public FacesContext getWrapped() {
		return wrapped;
	}
	
	@Override
	public void release() {
		RequestContext.releaseThreadLocalCache();

		wrapped.release();
	}
}
