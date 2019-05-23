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

public class BarChartSeries extends ChartSeries {

    private static final long serialVersionUID = 1L;

    private boolean disableStack;

    public BarChartSeries() {
    }

    public boolean isDisableStack() {
        return disableStack;
    }

    public void setDisableStack(boolean disableStack) {
        this.disableStack = disableStack;
    }

    @Override
    public String getRenderer() {
        return "BarRenderer";
    }

    @Override
    public void encode(Writer writer) throws IOException {
        writer.write("{");
        writer.write("label:\"" + EscapeUtils.forJavaScript(this.getLabel()) + "\"");

        writer.write(",renderer: $.jqplot." + getRenderer());

        AxisType xaxis = getXaxis();
        if (xaxis != null) {
            writer.write(",xaxis:\"" + xaxis + "\"");
        }

        AxisType yaxis = getYaxis();
        if (yaxis != null) {
            writer.write(",yaxis:\"" + yaxis + "\"");
        }

        if (disableStack) writer.write(",disableStack:true");

        writer.write("}");
    }
}
