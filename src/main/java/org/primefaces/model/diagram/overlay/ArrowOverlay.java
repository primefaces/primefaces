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
package org.primefaces.model.diagram.overlay;

import java.io.Serializable;

public class ArrowOverlay implements Overlay, Serializable {

    private static final long serialVersionUID = 1L;

    private int width = 20;

    private int length = 20;

    private double location = 0.5;

    private int direction = 1;

    private double foldback = 0.623;

    private String paintStyle;

    public ArrowOverlay() {

    }

    public ArrowOverlay(int width, int length, double location, int direction) {
        this.width = width;
        this.length = length;
        this.location = location;
        this.direction = direction;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public double getLocation() {
        return location;
    }

    public void setLocation(double location) {
        this.location = location;
    }

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    public double getFoldback() {
        return foldback;
    }

    public void setFoldback(double foldback) {
        this.foldback = foldback;
    }

    public String getPaintStyle() {
        return paintStyle;
    }

    public void setPaintStyle(String paintStyle) {
        this.paintStyle = paintStyle;
    }

    @Override
    public String getType() {
        return "Arrow";
    }

    @Override
    public String toJS(StringBuilder sb) {
        sb.append("['Arrow',{location:").append(location);

        if (width != 20) sb.append(",width:").append(width);
        if (length != 20) sb.append(",length:").append(length);
        if (direction != 1) sb.append(",direction:").append(direction);
        if (foldback != 0.623) sb.append(",foldback:").append(foldback);
        if (paintStyle != null) sb.append(",paintStyle:{").append(paintStyle).append("}");

        sb.append("}]");

        return sb.toString();
    }
}
