/**
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
package org.primefaces.component.carousel;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.application.ResourceDependencies;
import javax.faces.application.ResourceDependency;
import javax.faces.component.UIComponent;

@ResourceDependencies({
        @ResourceDependency(library = "primefaces", name = "components.css"),
        @ResourceDependency(library = "primefaces", name = "jquery/jquery.js"),
        @ResourceDependency(library = "primefaces", name = "jquery/jquery-plugins.js"),
        @ResourceDependency(library = "primefaces", name = "core.js"),
        @ResourceDependency(library = "primefaces", name = "components.js"),
        @ResourceDependency(library = "primefaces", name = "touch/touchswipe.js")
})
public class Carousel extends CarouselBase {

    public static final String COMPONENT_TYPE = "org.primefaces.component.Carousel";

    public static final String CONTAINER_CLASS = "ui-carousel ui-widget ui-widget-content ui-corner-all ui-hidden-container";
    public static final String ITEM_CLASS = "ui-carousel-item ui-widget-content ui-corner-all";
    public static final String HEADER_CLASS = "ui-carousel-header ui-widget-header ui-corner-all";
    public static final String HEADER_TITLE_CLASS = "ui-carousel-header-title";
    public static final String FOOTER_CLASS = "ui-carousel-footer ui-widget-header ui-corner-all";
    public static final String HORIZONTAL_NEXT_BUTTON = "ui-carousel-button ui-carousel-next-button ui-icon ui-icon-circle-triangle-e";
    public static final String HORIZONTAL_PREV_BUTTON = "ui-carousel-button ui-carousel-prev-button ui-icon ui-icon-circle-triangle-w";
    public static final String VERTICAL_NEXT_BUTTON = "ui-carousel-button ui-carousel-next-button ui-icon ui-icon-circle-triangle-s";
    public static final String VERTICAL_PREV_BUTTON = "ui-carousel-button ui-carousel-prev-button ui-icon ui-icon-circle-triangle-n";
    public static final String VIEWPORT_CLASS = "ui-carousel-viewport";
    public static final String ITEMS_CLASS = "ui-carousel-items";
    public static final String VERTICAL_VIEWPORT_CLASS = "ui-carousel-viewport ui-carousel-vertical-viewport";
    public static final String PAGE_LINKS_CONTAINER_CLASS = "ui-carousel-page-links";
    public static final String PAGE_LINK_CLASS = "ui-icon ui-carousel-page-link ui-icon-radio-off";
    public static final String DROPDOWN_CLASS = "ui-carousel-dropdown ui-widget ui-state-default ui-corner-left";
    public static final String RESPONSIVE_DROPDOWN_CLASS = "ui-carousel-dropdown-responsive ui-widget ui-state-default ui-corner-left";
    public static final String TOGGLER_LINK_CLASS = "ui-carousel-titlebar-icon ui-corner-all ui-state-default";

    private static final Logger LOGGER = Logger.getLogger(Carousel.class.getName());

    public int getRenderedChildCount() {
        int i = 0;

        for (UIComponent child : getChildren()) {
            if (child.isRendered()) {
                i++;
            }
        }

        return i;
    }

    @Override
    public void setRows(int rows) {
        super.setRows(rows);
        setNumVisible(rows);

        LOGGER.log(Level.WARNING, "rows is deprecated, use numVisible instead.");
    }
}