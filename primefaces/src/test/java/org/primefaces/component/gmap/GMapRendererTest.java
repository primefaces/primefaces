/*
 * The MIT License
 *
 * Copyright (c) 2009-2026 PrimeFaces
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
package org.primefaces.component.gmap;

import org.primefaces.model.map.LatLng;
import org.primefaces.model.map.Marker;
import org.primefaces.model.map.MarkerLabel;
import org.primefaces.model.map.Point;
import org.primefaces.model.map.Symbol;

import java.io.IOException;

import jakarta.faces.context.FacesContext;
import jakarta.faces.context.ResponseWriter;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GMapRendererTest {

    private String encode(Marker<?> marker) throws IOException {
        StringBuilder out = new StringBuilder();
        ResponseWriter writer = mock(ResponseWriter.class);
        doAnswer(invocation -> out.append((String) invocation.getArgument(0))).when(writer).write(anyString());
        FacesContext context = mock(FacesContext.class);
        when(context.getResponseWriter()).thenReturn(writer);

        new GMapRenderer().encodeMarker(context, marker);
        return out.toString();
    }

    @Test
    void encodesAdvancedMarkerOptions() throws IOException {
        Marker<Long> marker = new Marker<>(new LatLng(36.5, 30.25), "Konyaalti", 1L, "https://example.com/pin.png");
        marker.setId("m1");
        marker.setDraggable(true);
        marker.setVisible(false);
        marker.setZindex(3);
        marker.setShadow("ignored.png");

        assertEquals("{position:{lat:36.5,lng:30.25},id:'m1',title:\"Konyaalti\",icon:\"https:\\/\\/example.com\\/pin.png\""
                + ",gmpDraggable:true,visible:false,zIndex:3}", encode(marker));
    }

    @Test
    void encodesSymbolAndLabelWithoutGoogleMapsObjects() throws IOException {
        Symbol symbol = new Symbol("M0 0L10 10");
        symbol.setAnchor(new Point(15.0, 30.0));
        symbol.setScale(2.0);
        MarkerLabel label = new MarkerLabel();
        label.setText("A");
        label.setColor("#fff");

        Marker<Long> marker = new Marker<>(new LatLng(1, 2));
        marker.setId("m2");
        marker.setIcon(symbol);
        marker.setLabel(label);

        assertEquals("{position:{lat:1.0,lng:2.0},id:'m2',icon:{path:'M0 0L10 10',anchor:{x:15.0,y:30.0},scale:2.0}"
                + ",label:{text:\"A\",color:\"#fff\"}}", encode(marker));
    }
}
