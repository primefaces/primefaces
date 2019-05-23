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
package org.primefaces.model.chart;

import org.primefaces.util.EscapeUtils;

import java.io.IOException;
import java.io.Writer;

public class LineChartSeries extends ChartSeries {

    private static final long serialVersionUID = 1L;

    private String markerStyle = "filledCircle";
    private boolean showLine = true;
    private boolean showMarker = true;
    private boolean fill = false;
    private double fillAlpha = 1;
    private boolean smoothLine = false;
    private boolean disableStack;

    public LineChartSeries() {
    }

    public LineChartSeries(String title) {
        super(title);
    }

    public String getMarkerStyle() {
        return markerStyle;
    }

    public void setMarkerStyle(String markerStyle) {
        this.markerStyle = markerStyle;
    }

    public boolean isShowLine() {
        return showLine;
    }

    public void setShowLine(boolean showLine) {
        this.showLine = showLine;
    }

    public boolean isShowMarker() {
        return showMarker;
    }

    public void setShowMarker(boolean showMarker) {
        this.showMarker = showMarker;
    }

    @Override
    public boolean isFill() {
        return fill;
    }

    public void setFill(boolean fill) {
        this.fill = fill;
    }

    public double getFillAlpha() {
        return fillAlpha;
    }

    public void setFillAlpha(double fillAlpha) {
        this.fillAlpha = fillAlpha;
    }

    public boolean isDisableStack() {
        return disableStack;
    }

    public void setDisableStack(boolean disableStack) {
        this.disableStack = disableStack;
    }

    public boolean isSmoothLine() {
        return smoothLine;
    }

    public void setSmoothLine(boolean smoothLine) {
        this.smoothLine = smoothLine;
    }

    @Override
    public String getRenderer() {
        return "LineRenderer";
    }

    @Override
    public void encode(Writer writer) throws IOException {
        String renderer = this.getRenderer();
        AxisType xaxis = this.getXaxis();
        AxisType yaxis = this.getYaxis();

        writer.write("{");
        writer.write("label:\"" + EscapeUtils.forJavaScript(this.getLabel()) + "\"");
        writer.write(",renderer: $.jqplot." + renderer);

        if (xaxis != null) writer.write(",xaxis:\"" + xaxis + "\"");
        if (yaxis != null) writer.write(",yaxis:\"" + yaxis + "\"");
        if (disableStack) writer.write(",disableStack:true");


        if (fill) {
            writer.write(",fill:true");
            writer.write(",fillAlpha:" + this.getFillAlpha());
        }

        writer.write(",showLine:" + this.isShowLine());
        writer.write(",markerOptions:{show:" + this.isShowMarker() + ", style:'" + this.getMarkerStyle() + "'}");
        if (smoothLine) {
            writer.write(",rendererOptions:{smooth: true }");
        }
        writer.write("}");
    }

}
