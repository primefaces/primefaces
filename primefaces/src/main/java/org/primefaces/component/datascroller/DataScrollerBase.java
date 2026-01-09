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
package org.primefaces.component.datascroller;

import org.primefaces.cdk.api.FacesBehaviorEvent;
import org.primefaces.cdk.api.FacesBehaviorEvents;
import org.primefaces.cdk.api.FacesComponentBase;
import org.primefaces.cdk.api.Property;
import org.primefaces.component.api.PrimeClientBehaviorHolder;
import org.primefaces.component.api.PrimeUIData;
import org.primefaces.component.api.StyleAware;
import org.primefaces.component.api.Widget;

import jakarta.faces.component.behavior.ClientBehaviorHolder;
import jakarta.faces.event.AjaxBehaviorEvent;

@FacesComponentBase
@FacesBehaviorEvents({
    @FacesBehaviorEvent(name = "load", event = AjaxBehaviorEvent.class, defaultEvent = true, description = "Fired when the data is loaded.")
})
public abstract class DataScrollerBase extends PrimeUIData implements Widget, StyleAware, ClientBehaviorHolder, PrimeClientBehaviorHolder {

    public static final String COMPONENT_FAMILY = "org.primefaces.component";

    public static final String DEFAULT_RENDERER = "org.primefaces.component.DataScrollerRenderer";

    public DataScrollerBase() {
        setRendererType(DEFAULT_RENDERER);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    @Property(defaultValue = "0", description = "Number of items to fetch.")
    public abstract int getChunkSize();

    @Property(defaultValue = "document", description = "Defines the target to listen for scroll event, valid values are \"document\" and \"inline\".")
    public abstract String getMode();

    @Property(description = "Defines pixel height of the viewport in inline mode.")
    public abstract String getScrollHeight();

    @Property(defaultValue = "10", description = "Percentage height of the buffer between the bottom of the page and the scroll position to initiate " +
            "the load for the new chunk. Value is defined in integer and default is 10 meaning load would happen after 90% of the viewport is scrolled down.")
    public abstract int getBuffer();

    @Property(defaultValue = "false", description = "Loads data on demand as the scrollbar gets close to the bottom.")
    public abstract boolean isVirtualScroll();

    @Property(defaultValue = "false", description = "If the scrollAtBottom is enabled, scroll position is at bottom and data loading starts from the bottom.")
    public abstract boolean isStartAtBottom();
}
