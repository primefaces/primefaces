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
package org.primefaces.model.diagram.endpoint;

public enum EndPointAnchor {
    ASSIGN("Assign"),
    AUTO_DEFAULT("AutoDefault"),
    BOTTOM("Bottom"),
    BOTTOM_LEFT("BottomLeft"),
    BOTTOM_RIGHT("BottomRight"),
    CENTER("Center"),
    CONTINUOUS("Continuous"),
    CONTINUOUS_LEFT("ContinuousLeft"),
    CONTINUOUS_RIGHT("ContinuousRight"),
    CONTINUOUS_TOP("ContinuousTop"),
    CONTINUOUS_BOTTOM("ContinuousBottom"),
    LEFT("Left"),
    PERIMETER("Perimeter"),
    RIGHT("Right"),
    TOP("Top"),
    TOP_LEFT("TopLeft"),
    TOP_RIGHT("TopRight");

    private final String text;

    private EndPointAnchor(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return this.text;
    }
}
