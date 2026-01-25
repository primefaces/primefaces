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
package org.primefaces.component.card;

import org.primefaces.cdk.api.FacesComponentBase;
import org.primefaces.cdk.api.Facet;
import org.primefaces.cdk.api.Property;
import org.primefaces.component.api.StyleAware;

import jakarta.faces.component.UIComponent;
import jakarta.faces.component.UIComponentBase;

@FacesComponentBase
public abstract class CardBase extends UIComponentBase implements StyleAware {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.CardRenderer";

    public CardBase() {
        setRendererType(DEFAULT_RENDERER);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    @Facet(description = "Header content of the card.")
    public abstract UIComponent getHeaderFacet();

    @Facet(description = "Title content of the card.")
    public abstract UIComponent getTitleFacet();

    @Facet(description = "Subtitle content of the card.")
    public abstract UIComponent getSubtitleFacet();

    @Facet(description = "Footer content of the card.")
    public abstract UIComponent getFooterFacet();

    @Property(description = "Header text of the card.")
    public abstract String getHeader();

    @Property(description = "Title text of the card.")
    public abstract String getTitle();

    @Property(description = "Subtitle text of the card.")
    public abstract String getSubtitle();

    @Property(description = "Footer text of the card.")
    public abstract String getFooter();
}