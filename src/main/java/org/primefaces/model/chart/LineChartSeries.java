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
import java.io.Writer;

public class LineChartSeries extends ChartSeries {

    private String markerStyle = "filledCircle";
    private boolean showLine = true;
    private boolean showMarker = true;
    private boolean fill = false;
    private double fillAlpha = 1;
    private boolean disableStack;

    public LineChartSeries() {}

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
        writer.write("label:'" + this.getLabel() + "'");
        writer.write(",renderer: $.jqplot." + renderer);
        
        if(xaxis != null) writer.write(",xaxis:\"" + xaxis + "\"");
        if(yaxis != null) writer.write(",yaxis:\"" + yaxis + "\"");
        if(disableStack) writer.write(",disableStack:true");
        
        if(fill) {
            writer.write(",fill:true");
            writer.write(",fillAlpha:" + this.getFillAlpha());
        }

        writer.write(",showLine:" + this.isShowLine());
        writer.write(",markerOptions:{show:" + this.isShowMarker()+ ", style:'" + this.getMarkerStyle() + "'}");

        writer.write("}");
    }

}