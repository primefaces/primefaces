/*
 * The MIT License
 *
 * Copyright (c) 2009-2020 PrimeTek
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
package org.primefaces.model.diagram;

import org.primefaces.model.diagram.connector.Connector;
import org.primefaces.model.diagram.endpoint.EndPoint;
import org.primefaces.model.diagram.overlay.Overlay;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Connection implements Serializable {

    private static final long serialVersionUID = 1L;

    private EndPoint source;

    private EndPoint target;

    private Connector connector;

    private List<Overlay> overlays;

    private boolean detachable = true;

    public Connection() {
        overlays = new ArrayList<>();
    }

    public Connection(EndPoint source, EndPoint target) {
        this();
        this.source = source;
        this.target = target;
    }

    public Connection(EndPoint source, EndPoint target, Connector connector) {
        this(source, target);
        this.connector = connector;
    }

    public EndPoint getSource() {
        return source;
    }

    public void setSource(EndPoint source) {
        this.source = source;
    }

    public EndPoint getTarget() {
        return target;
    }

    public void setTarget(EndPoint target) {
        this.target = target;
    }

    public Connector getConnector() {
        return connector;
    }

    public void setConnector(Connector connector) {
        this.connector = connector;
    }

    public List<Overlay> getOverlays() {
        return overlays;
    }

    public void setOverlays(List<Overlay> overlays) {
        this.overlays = overlays;
    }

    public boolean isDetachable() {
        return detachable;
    }

    public void setDetachable(boolean detachable) {
        this.detachable = detachable;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Connection that = (Connection) o;
        return Objects.equals(source, that.source) &&
                Objects.equals(target, that.target);
    }

    @Override
    public int hashCode() {
        return Objects.hash(source, target);
    }
}
