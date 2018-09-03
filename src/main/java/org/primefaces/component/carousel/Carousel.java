/**
 * Copyright 2009-2018 PrimeTek.
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

    private static final Logger logger = Logger.getLogger(Carousel.class.getName());

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

        logger.log(Level.WARNING, "rows is deprecated, use numVisible instead.");
    }
}