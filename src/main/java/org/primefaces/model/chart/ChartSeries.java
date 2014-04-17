/*
 * Copyright 2009-2014 PrimeTek.
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

import java.io.IOException;
import java.io.Serializable;
import java.io.Writer;
import java.util.LinkedHashMap;
import java.util.Map;

public class ChartSeries implements Serializable {

    private String label;

    private Map<Object,Number> data = new LinkedHashMap<Object, Number>();
    
    private AxisType xaxis;
    
    private AxisType yaxis;

    public ChartSeries() {}

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
        writer.write("label:'" + label + "'");

        if(renderer != null) writer.write(",renderer: $.jqplot." + renderer);
        if(xaxis != null) writer.write(",xaxis:\"" + xaxis + "\"");
        if(yaxis != null) writer.write(",yaxis:\"" + yaxis + "\"");

        writer.write("}");
    }
}
