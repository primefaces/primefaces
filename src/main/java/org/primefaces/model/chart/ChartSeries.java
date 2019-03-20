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
import java.io.Serializable;
import java.io.Writer;
import java.util.LinkedHashMap;
import java.util.Map;

public class ChartSeries implements Serializable {

    private static final long serialVersionUID = 1L;

    private String label;

    private Map<Object, Number> data = new LinkedHashMap<Object, Number>();

    private AxisType xaxis;

    private AxisType yaxis;

    public ChartSeries() {
    }

    public ChartSeries(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Map<Object, Number> getData() {
        return data;
    }

    public void setData(Map<Object, Number> data) {
        this.data = data;
    }

    public void set(Object x, Number y) {
        this.data.put(x, y);
    }

    public String getRenderer() {
        return null;
    }

    public boolean isFill() {
        return false;
    }

    public AxisType getXaxis() {
        return xaxis;
    }

    public void setXaxis(AxisType xaxis) {
        this.xaxis = xaxis;
    }

    public AxisType getYaxis() {
        return yaxis;
    }

    public void setYaxis(AxisType yaxis) {
        this.yaxis = yaxis;
    }

    public void encode(Writer writer) throws IOException {
        String renderer = this.getRenderer();
        writer.write("{");
        writer.write("label:\"" + EscapeUtils.forJavaScript(label) + "\"");

        if (renderer != null) writer.write(",renderer: $.jqplot." + renderer);
        if (xaxis != null) writer.write(",xaxis:\"" + xaxis + "\"");
        if (yaxis != null) writer.write(",yaxis:\"" + yaxis + "\"");

        writer.write("}");
    }
}
