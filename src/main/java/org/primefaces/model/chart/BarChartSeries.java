/**
 * Copyright 2009-2018 PrimeTek.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
