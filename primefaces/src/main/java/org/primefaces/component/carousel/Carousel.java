/*
 * The MIT License
 *
 * Copyright (c) 2009-2021 PrimeTek
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

import javax.faces.application.ResourceDependency;

@ResourceDependency(library = "primefaces", name = "components.css")
@ResourceDependency(library = "primefaces", name = "jquery/jquery.js")
@ResourceDependency(library = "primefaces", name = "jquery/jquery-plugins.js")
@ResourceDependency(library = "primefaces", name = "core.js")
@ResourceDependency(library = "primefaces", name = "components.js")
public class Carousel extends CarouselBase {

    public static final String COMPONENT_TYPE = "org.primefaces.component.Carousel";

    public static final String STYLE_CLASS = "ui-carousel ui-widget";
    public static final String CONTENT_CLASS = "ui-carousel-content";
    public static final String CONTAINER_CLASS = "ui-carousel-container";
    public static final String INDICATORS_CONTENT_CLASS = "ui-carousel-indicators ui-reset";
    public static final String HORIZONTAL_CLASS = "ui-carousel-horizontal";
    public static final String VERTICAL_CLASS = "ui-carousel-vertical";
    public static final String HEADER_CLASS = "ui-carousel-header";
    public static final String FOOTER_CLASS = "ui-carousel-footer";
    public static final String PREV_BUTTON_CLASS = "ui-carousel-prev ui-link";
    public static final String PREV_BUTTON_ICON_CLASS = "ui-carousel-prev-icon pi";
    public static final String NEXT_BUTTON_CLASS = "ui-carousel-next ui-link";
    public static final String NEXT_BUTTON_ICON_CLASS = "ui-carousel-next-icon pi";
    public static final String ITEMS_CONTENT_CLASS = "ui-carousel-items-content";
    public static final String ITEMS_CONTAINER_CLASS = "ui-carousel-items-container ui-items-hidden";
    public static final String ITEM_CLASS = "ui-carousel-item";

}