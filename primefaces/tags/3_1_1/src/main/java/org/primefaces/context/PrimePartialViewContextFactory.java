/*
 * Copyright 2009-2012 Prime Teknoloji.
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

import javax.faces.context.FacesContext;
import javax.faces.context.PartialViewContext;
import javax.faces.context.PartialViewContextFactory;

public class PrimePartialViewContextFactory extends PartialViewContextFactory {

    private PartialViewContextFactory parent;
    
    public PrimePartialViewContextFactory(PartialViewContextFactory parent) {
        this.parent = parent;
    }

    @Override
    public PartialViewContextFactory getWrapped() {
        return this.parent;
    }

    @Override
    public PartialViewContext getPartialViewContext(FacesContext fc) {
        PartialViewContext parentContext = getWrapped().getPartialViewContext(fc);
        
        return new PrimePartialViewContext(parentContext);
    }
}