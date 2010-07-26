/*
 * Copyright 2009 Prime Technology.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.primefaces.context;

import javax.faces.FacesException;
import javax.faces.context.ExternalContext;
import javax.faces.context.ExternalContextFactory;

public class PrimeExternalContextFactory extends ExternalContextFactory {

	private ExternalContextFactory delegate;
	
	public PrimeExternalContextFactory(ExternalContextFactory delegate) {
		super();
		this.delegate = delegate;
	}

	@Override
	public ExternalContext getExternalContext(Object context, Object request, Object response) throws FacesException {
		ExternalContext externalContext = delegate.getExternalContext(context, request, response);
		
		return new PrimeExternalContext(externalContext);
	}

	@Override
	public ExternalContextFactory getWrapped() {
		return delegate;
	}
}