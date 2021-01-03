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
package org.primefaces.event.map;

import java.util.List;
import javax.faces.component.UIComponent;
import javax.faces.component.behavior.Behavior;
import org.primefaces.event.AbstractAjaxBehaviorEvent;
import org.primefaces.model.map.LatLng;

public class ReverseGeocodeEvent extends AbstractAjaxBehaviorEvent {

    private static final long serialVersionUID = 1L;

    private final LatLng latlng;
    private final List<String> addresses;

    public ReverseGeocodeEvent(UIComponent component, Behavior behavior, LatLng latlng, List<String> addresses) {
        super(component, behavior);
        this.latlng = latlng;
        this.addresses = addresses;
    }

    public LatLng getLatlng() {
        return latlng;
    }

    public List<String> getAddresses() {
        return addresses;
    }
}
