/*
 * Copyright 2009-2013 PrimeTek.
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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CartesianChartModel extends ChartModel implements Serializable {

    private List<ChartSeries> series;
    protected Map<AxisType,Axis> axes;
    private boolean stacked = false;
    private boolean breakOnNull = false;
    private boolean zoom = false;
    private boolean animate = false;
    private boolean showDatatip = true;
    private String datatipFormat;
    private boolean fill = false;
    private boolean showPointLabels = false;
    
    public CartesianChartModel() {
        series = new ArrayList<ChartSeries>();
        createAxes();
    }
    
    protected void createAxes() {
        axes = new HashMap<AxisType, Axis>();
        axes.put(AxisType.X, new Axis());
        axes.put(AxisType.Y, new Axis());
    }
    
    public List<ChartSeries> getSeries() {
        return series;
    }

    public void addSeries(ChartSeries chartSeries) {
        this.series.add(chartSeries);
    }

    public void clear() {
        this.series.clear();
    }

    public Map<AxisType, Axis> getAxes() {
        return axes;
    }
    
    public Axis getAxis(AxisType type) {
        return axes.get(type);
    } 

    public boolean isStacked() {
        return stacked;
    }
    public void setStacked(boolean stacked) {
        this.stacked = stacked;
    }

    public boolean isBreakOnNull() {
        return breakOnNull;
    }
    public void setBreakOnNull(boolean breakOnNull) {
        this.breakOnNull = breakOnNull;
    }

    public boolean isZoom() {
        return zoom;
    }
    public void setZoom(boolean zoom) {
        this.zoom = zoom;
    }

    public boolean isAnimate() {
        return animate;
    }
    public void setAnimate(boolean animate) {
        this.animate = animate;
    }

    public boolean isShowDatatip() {
        return showDatatip;
    }
    public void setShowDatatip(boolean showDatatip) {
        this.showDatatip = showDatatip;
    }

    public String getDatatipFormat() {
        return datatipFormat;
    }
    public void setDatatipFormat(String datatipFormat) {
        this.datatipFormat = datatipFormat;
    }

    public boolean isFill() {
        return fill;
    }
    public void setFill(boolean fill) {
        this.fill = fill;
    }

    public boolean isShowPointLabels() {
        return showPointLabels;
    }
    public void setShowPointLabels(boolean showPointLabels) {
        this.showPointLabels = showPointLabels;
    }
}