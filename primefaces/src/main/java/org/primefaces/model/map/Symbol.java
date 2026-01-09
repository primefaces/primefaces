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
package org.primefaces.model.map;

import java.io.Serializable;
import java.util.Objects;

public class Symbol implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Path defining the shape of the symbol. You can define a custom path using SVG path notation. Note: Vector paths
     * on a polyline must fit within a 22x22px square. If your path includes points outside this square, then you must
     * adjust the symbol's scale property to a fractional value, such as 0.2, so that the resulting scaled points fit
     * within the square.
     */
    private String path;

    /**
     * Position of the symbol relative to the marker or polyline. The coordinates of the symbol's path are translated
     * left and up by the anchor's x and y coordinates respectively. By default, a symbol is anchored at (0, 0). The
     * position is expressed in the same coordinate system as the symbol's path.
     */
    private Point anchor;

    /**
     * Color of the symbol's fill (that is, the region bordered by the stroke). All CSS3 colors are supported except for
     * extended named colors. For symbols on markers, the default is 'black'. For symbols on polylines, the default is
     * the stroke color of the corresponding polyline.
     */
    private String fillColor;

    /**
     * Relative opacity (that is, lack of transparency) of the symbol's fill. The values range from 0.0 (fully
     * transparent) to 1.0 (fully opaque). The default is 0.0.
     */
    private Double fillOpacity;

    /**
     * Angle by which to rotate the symbol, expressed clockwise in degrees. By default, a symbol marker has a rotation
     * of 0, and a symbol on a polyline is rotated by the angle of the edge on which it lies. Setting the rotation of a
     * symbol on a polyline will fix the rotation of the symbol such that it will no longer follow the curve of the
     * line.
     */
    private Double rotation;

    /**
     * Amount by which the symbol is scaled in size. For symbol markers, the default scale is 1. After scaling the
     * symbol may be of any size. For symbols on a polyline, the default scale is the stroke weight of the polyline.
     * After scaling, the symbol must lie inside a 22x22px square, centered at the symbol's anchor.
     */
    private Double scale;

    /**
     * Color of the symbol's outline. All CSS3 colors are supported except for extended named colors. For symbols on
     * markers, the default is 'black'. For symbols on a polyline, the default color is the stroke color of the
     * polyline.
     */
    private String strokeColor;

    /**
     * Relative opacity (that is, lack of transparency) of the symbol's stroke. The values range from 0.0 (fully
     * transparent) to 1.0 (fully opaque). For symbol markers, the default is 1.0. For symbols on polylines, the default
     * is the stroke opacity of the polyline.
     */
    private Double strokeOpacity;

    /**
     * Weight of the symbol's outline. The default is the scale of the symbol.
     */
    private Double strokeWeight;

    public Symbol() {
        // NOOP
    }

    public Symbol(String path) {
        this();
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Point getAnchor() {
        return anchor;
    }

    public void setAnchor(Point anchor) {
        this.anchor = anchor;
    }

    public String getFillColor() {
        return fillColor;
    }

    public void setFillColor(String fillColor) {
        this.fillColor = fillColor;
    }

    public Double getFillOpacity() {
        return fillOpacity;
    }

    public void setFillOpacity(Double fillOpacity) {
        this.fillOpacity = fillOpacity;
    }

    public Double getRotation() {
        return rotation;
    }

    public void setRotation(Double rotation) {
        this.rotation = rotation;
    }

    public Double getScale() {
        return scale;
    }

    public void setScale(Double scale) {
        this.scale = scale;
    }

    public String getStrokeColor() {
        return strokeColor;
    }

    public void setStrokeColor(String strokeColor) {
        this.strokeColor = strokeColor;
    }

    public Double getStrokeOpacity() {
        return strokeOpacity;
    }

    public void setStrokeOpacity(Double strokeOpacity) {
        this.strokeOpacity = strokeOpacity;
    }

    public Double getStrokeWeight() {
        return strokeWeight;
    }

    public void setStrokeWeight(Double strokeWeight) {
        this.strokeWeight = strokeWeight;
    }

    @Override
    public String toString() {
        return "Symbol{" + "path=" + path + ", anchor=" + anchor + ", fillColor=" + fillColor + ", fillOpacity="
                + fillOpacity + ", rotation=" + rotation + ", scale=" + scale + ", strokeColor=" + strokeColor
                + ", strokeOpacity=" + strokeOpacity + ", strokeWeight=" + strokeWeight + '}';
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.path);
        hash = 97 * hash + Objects.hashCode(this.anchor);
        hash = 97 * hash + Objects.hashCode(this.fillColor);
        hash = 97 * hash + Objects.hashCode(this.fillOpacity);
        hash = 97 * hash + Objects.hashCode(this.rotation);
        hash = 97 * hash + Objects.hashCode(this.scale);
        hash = 97 * hash + Objects.hashCode(this.strokeColor);
        hash = 97 * hash + Objects.hashCode(this.strokeOpacity);
        hash = 97 * hash + Objects.hashCode(this.strokeWeight);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Symbol other = (Symbol) obj;
        if (!Objects.equals(this.path, other.path)) {
            return false;
        }
        if (!Objects.equals(this.fillColor, other.fillColor)) {
            return false;
        }
        if (!Objects.equals(this.strokeColor, other.strokeColor)) {
            return false;
        }
        if (!Objects.equals(this.anchor, other.anchor)) {
            return false;
        }
        if (!Objects.equals(this.fillOpacity, other.fillOpacity)) {
            return false;
        }
        if (!Objects.equals(this.rotation, other.rotation)) {
            return false;
        }
        if (!Objects.equals(this.scale, other.scale)) {
            return false;
        }
        if (!Objects.equals(this.strokeOpacity, other.strokeOpacity)) {
            return false;
        }
        return Objects.equals(this.strokeWeight, other.strokeWeight);
    }

}
