/*
 * The MIT License
 *
 * Copyright (c) 2009-2025 PrimeTek Informatics
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
package org.primefaces.component.galleria;

import java.util.Collections;
import java.util.Map;

import jakarta.faces.application.ResourceDependency;
import jakarta.faces.component.FacesComponent;
import jakarta.faces.event.BehaviorEvent;

@FacesComponent(value = Galleria.COMPONENT_TYPE, namespace = Galleria.COMPONENT_FAMILY)
@ResourceDependency(library = "primefaces", name = "components.css")
@ResourceDependency(library = "primefaces", name = "galleria/galleria.css")
@ResourceDependency(library = "primefaces", name = "jquery/jquery.js")
@ResourceDependency(library = "primefaces", name = "jquery/jquery-plugins.js")
@ResourceDependency(library = "primefaces", name = "core.js")
@ResourceDependency(library = "primefaces", name = "components.js")
@ResourceDependency(library = "primefaces", name = "galleria/galleria.js")
public class Galleria extends GalleriaBaseImpl {

    public static final String COMPONENT_TYPE = "org.primefaces.component.Galleria";

    public static final String CONTAINER_CLASS = "ui-galleria ui-widget ui-widget-content";
    public static final String CONTENT_CLASS = "ui-galleria-content";
    public static final String HEADER_CLASS = "ui-galleria-header";
    public static final String FOOTER_CLASS = "ui-galleria-footer";
    public static final String ITEMS_CLASS = "ui-galleria-items";
    public static final String ITEM_CLASS = "ui-galleria-item";
    public static final String CAPTION_ITEMS_CLASS = "ui-galleria-caption-items";
    public static final String CAPTION_ITEM_CLASS = "ui-galleria-caption-item";
    public static final String INDICATORS_CLASS = "ui-galleria-indicators";
    public static final String INDICATOR_CLASS = "ui-galleria-indicator";
    public static final String THUMBNAIL_ITEMS_CLASS = "ui-galleria-thumbnail-items";
    public static final String THUMBNAIL_ITEM_CLASS = "ui-galleria-thumbnail-item";
    public static final String THUMBNAIL_ITEM_CONTENT_CLASS = "ui-galleria-thumbnail-item-content";

    @Override
    public Map<String, Class<? extends BehaviorEvent>> getBehaviorEventMapping() {
        return Collections.emptyMap();
    }
}
