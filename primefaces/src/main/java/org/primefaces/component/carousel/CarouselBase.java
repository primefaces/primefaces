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
package org.primefaces.component.carousel;

import org.primefaces.cdk.api.FacesBehaviorEvent;
import org.primefaces.cdk.api.FacesBehaviorEvents;
import org.primefaces.cdk.api.FacesComponentBase;
import org.primefaces.cdk.api.Facet;
import org.primefaces.cdk.api.Property;
import org.primefaces.component.api.StyleAware;
import org.primefaces.component.api.TouchAware;
import org.primefaces.component.api.UITabPanel;
import org.primefaces.component.api.Widget;
import org.primefaces.event.PageChangeEvent;
import org.primefaces.model.ResponsiveOption;

import java.util.List;

import jakarta.faces.component.UIComponent;

@FacesComponentBase
@FacesBehaviorEvents({
    @FacesBehaviorEvent(name = "pageChange", event = PageChangeEvent.class, description = "Fires when a page is changed.", defaultEvent = true)
})
public abstract class CarouselBase extends UITabPanel implements Widget, TouchAware, StyleAware {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.CarouselRenderer";

    public CarouselBase() {
        setRendererType(DEFAULT_RENDERER);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    @Facet(description = "Allows custom HTML in the header. Alternative to headerText.")
    public abstract UIComponent getHeaderFacet();

    @Facet(description = "Allows custom HTML in the footer. Alternative to footerText.")
    public abstract UIComponent getFooterFacet();

    @Property(defaultValue = "0", description = "Index of the first page.")
    public abstract int getPage();

    @Property(defaultValue = "true", description = "When enabled, displays paginator.")
    public abstract boolean isPaginator();

    @Property(defaultValue = "false", description = "Defines if scrolling would be infinite.")
    public abstract boolean isCircular();

    @Property(defaultValue = "0", description = "Time in milliseconds to scroll items automatically, whereas 0 means disabled.")
    public abstract int getAutoplayInterval();

    @Property(defaultValue = "1", description = "Number of items per page.")
    public abstract int getNumVisible();

    @Property(defaultValue = "1", description = "Number of items to scroll.")
    public abstract int getNumScroll();

    @Property(description = "A list of breakpoint ResponsiveOption for responsive design.")
    public abstract List<ResponsiveOption> getResponsiveOptions();

    @Property(defaultValue = "horizontal", description = "Specifies the layout of the component, valid values are \"horizontal\" and \"vertical\".")
    public abstract String getOrientation();

    @Property(defaultValue = "300px", description = "Height of the viewport in vertical layout.")
    public abstract String getVerticalViewPortHeight();

    @Property(description = "Style class of the carousel content.")
    public abstract String getContentStyleClass();

    @Property(description = "Style class of the carousel container.")
    public abstract String getContainerStyleClass();

    @Property(description = "Style class of the carousel indicators content.")
    public abstract String getIndicatorsContentStyleClass();

    @Property(description = "Text content of the header.")
    public abstract String getHeaderText();

    @Property(description = "Text content of the footer.")
    public abstract String getFooterText();

    @Property(description = "Client side callback to execute when a page is changed.")
    public abstract String getOnPageChange();

}
