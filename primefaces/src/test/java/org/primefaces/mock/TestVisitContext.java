/* 
 * The MIT License
 *
 * Copyright (c) 2009-2019 PrimeTek
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
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
