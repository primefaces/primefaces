/*
 * The MIT License
 *
 * Copyright (c) 2009-2024 PrimeTek Informatics
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
package org.primefaces.model.charts.bar;

import java.io.IOException;

import org.primefaces.util.ChartUtils;
import org.primefaces.util.FastStringWriter;

/**
 * Set the corners of bars in charts as per the
 * <a href="https://www.chartjs.org/docs/latest/charts/bar.html#borderradius">Chartjs documentation</a>
 */
public class BarCorner {

    private int topLeft;
    private int bottomLeft;
    private int topRight;
    private int bottomRight;

    /**
     * Get the top left corner radius in pixel
     * @return the radius
     */
    public int getTopLeft() {
        return topLeft;
    }
    /**
     * Set the top left corner radius in pixel
     * @param topLeft the radius
     */
    public void setTopLeft(int topLeft) {
        this.topLeft = topLeft;
    }
    /**
     * Get the bottom left corner radius in pixel
     * @return the radius
     */
    public int getBottomLeft() {
        return bottomLeft;
    }
    /**
     * Set the bottom left corner radius in pixel
     * @param bottomLeft the radius
     */
    public void setBottomLeft(int bottomLeft) {
        this.bottomLeft = bottomLeft;
    }
    /**
     * Get the top right corner radius in pixel
     * @return the radius
     */
    public int getTopRight() {
        return topRight;
    }
    /**
     * Get the top right corner radius in pixel
     * @param topRight the radius
     */
    public void setTopRight(int topRight) {
        this.topRight = topRight;
    }
    /**
     * Get the bottom right corner radius in pixel
     * @return the radius
     */
    public int getBottomRight() {
        return bottomRight;
    }
    /**
     * Set the bottom right corner radius in pixel
     * @param bottomRight the radius
     */
    public void setBottomRight(int bottomRight) {
        this.bottomRight = bottomRight;
    }

    /**
     * encodes and writes the object
     * @param fsw the writer in which we send the encoded value
     * @throws IOException on error writing data
     */
    public void encode(FastStringWriter fsw) throws IOException {
        fsw.write("{");
        ChartUtils.writeDataValue(fsw, "topLeft", topLeft, false);
        ChartUtils.writeDataValue(fsw, "topRight", topRight, true);
        ChartUtils.writeDataValue(fsw, "bottomLeft", bottomLeft, true);
        ChartUtils.writeDataValue(fsw, "bottomRight", bottomRight, true);
        fsw.write("}");
    }

}
