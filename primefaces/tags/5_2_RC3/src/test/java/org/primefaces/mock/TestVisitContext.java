/*
 * Copyright 2009-2014 PrimeTek.
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
package org.primefaces.mock;

import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;
import javax.faces.component.UIComponent;
import javax.faces.component.visit.VisitCallback;
import javax.faces.component.visit.VisitContext;
import javax.faces.component.visit.VisitHint;
import javax.faces.component.visit.VisitResult;
import javax.faces.context.FacesContext;

public class TestVisitContext extends VisitContext {

    private FacesContext facesContext;
    private Set<VisitHint> hints;
    
    public TestVisitContext(FacesContext facesContext) {
        this(facesContext, null);
    }

    public TestVisitContext(FacesContext facesContext, Set<VisitHint> hints) {
        this.facesContext = facesContext;

        EnumSet<VisitHint> hintsEnumSet = ((hints == null) || (hints.isEmpty()))
                ? EnumSet.noneOf(VisitHint.class)
                : EnumSet.copyOf(hints);

        this.hints = Collections.unmodifiableSet(hintsEnumSet);
    }

    @Override
    public FacesContext getFacesContext() {
        return facesContext;
    }

    @Override
    public Collection<String> getIdsToVisit() {
        return VisitContext.ALL_IDS;
    }

    @Override
    public Collection<String> getSubtreeIdsToVisit(UIComponent component) {
        return VisitContext.ALL_IDS;
    }

    @Override
    public Set<VisitHint> getHints() {
        return hints;
    }

    @Override
    public VisitResult invokeVisitCallback(UIComponent component, VisitCallback callback) {
        return callback.visit(this, component);
    }
}
