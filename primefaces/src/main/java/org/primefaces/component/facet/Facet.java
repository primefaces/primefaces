/*
 * The MIT License
 *
 * Copyright (c) 2009-2026 PrimeTek Informatics
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
package org.primefaces.component.facet;

import java.io.IOException;

import jakarta.faces.component.FacesComponent;
import jakarta.faces.context.FacesContext;

@FacesComponent(value = Facet.COMPONENT_TYPE, namespace = Facet.COMPONENT_FAMILY)
public class Facet extends FacetBaseImpl {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";
    public static final String COMPONENT_TYPE = COMPONENT_FAMILY + ".Facet";
    public static final String DEFAULT_RENDERER = null;

    public Facet() {
        setRendererType(DEFAULT_RENDERER);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    @Override
    public boolean getRendersChildren() {
        return true;
    }

    @Override
    public void encodeAll(FacesContext context) throws IOException {

    }

    @Override
    public void encodeChildren(FacesContext context) throws IOException {

    }

    @Override
    public void encodeBegin(FacesContext context) throws IOException {

    }

    @Override
    public void encodeEnd(FacesContext context) throws IOException {

    }

    @Override
    public void encodeExplicitly(FacesContext context) throws IOException {
        // Call the actual rendering methods from the parent class
        super.encodeBegin(context);
        super.encodeChildren(context);
        super.encodeEnd(context);
    }
}
