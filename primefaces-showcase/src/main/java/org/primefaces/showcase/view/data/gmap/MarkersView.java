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
package org.primefaces.showcase.view.data.gmap;

import org.primefaces.model.map.DefaultMapModel;
import org.primefaces.model.map.LatLng;
import org.primefaces.model.map.MapModel;
import org.primefaces.model.map.Marker;
import org.primefaces.model.map.Point;
import org.primefaces.model.map.Symbol;

import java.io.Serializable;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;

@Named
@RequestScoped
public class MarkersView implements Serializable {

    private MapModel<Long> simpleModel;

    @PostConstruct
    public void init() {
        simpleModel = new DefaultMapModel<>();

        simpleModel.addOverlay(new Marker<>(new LatLng(36.879466, 30.667648), "Konyaalti", 1L));
        simpleModel.addOverlay(new Marker<>(new LatLng(36.883707, 30.689216), "Ataturk Parki", 2L));

        Marker marker3 = new Marker<>(new LatLng(36.879703, 30.706707), "Karaalioglu Parki", 3L,
                "https://developers.google.com/maps/documentation/javascript/examples/full/images/beachflag.png");
        simpleModel.addOverlay(marker3);

        Marker marker4 = new Marker<>(new LatLng(36.885233, 30.702323), "Kaleici", 4L);
        Symbol symbol = new Symbol(
                "M10.453 14.016l6.563-6.609-1.406-1.406-5.156 5.203-2.063-2.109-1.406 1.406zM12 2.016q2.906 0 4.945"
              + " 2.039t2.039 4.945q0 1.453-0.727 3.328t-1.758 3.516-2.039 3.070-1.711 2.273l-0.75"
              + " 0.797q-0.281-0.328-0.75-0.867t-1.688-2.156-2.133-3.141-1.664-3.445-0.75-3.375q0-2.906"
              + " 2.039-4.945t4.945-2.039z");
        symbol.setFillColor("#fff");
        symbol.setFillOpacity(.7);
        symbol.setScale(2.0);
        symbol.setAnchor(new Point(15.0, 30.0));
        marker4.setIcon(symbol);
        simpleModel.addOverlay(marker4);
    }

    public MapModel<Long> getSimpleModel() {
        return simpleModel;
    }
}
