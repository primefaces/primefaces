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

import java.util.ArrayList;
import java.util.List;

public class MeterGaugeChartModel extends ChartModel {
 
    private Number value;
    private List<Number> intervals;
    private List<Number> ticks;
    private String gaugeLabel;
    private String gaugeLabelPosition = "inside";
    private double min = Double.MIN_VALUE;
    private double max = Double.MAX_VALUE;
    private boolean showTickLabels = true;
    private Integer intervalInnerRadius;
    private Integer intervalOuterRadius;
    private int labelHeightAdjust = -25;
    
    public MeterGaugeChartModel() {
        intervals = new ArrayList<Number>();
    }

    public MeterGaugeChartModel(Number value, List<Number> intervals) {
        this.value = value;
        this.intervals = intervals;
    }
    
    public MeterGaugeChartModel(Number value, List<Number> intervals, List<Number> ticks) {
        this.value = value;
        this.intervals = intervals;
        this.ticks = ticks;
    }
    
    public void addInterval(Number interval) {
        this.intervals.add(interval);
    }
    
    public List<Number> getIntervals() {
        return intervals;
    }

    public void setIntervals(List<Number> intervals) {
        this.intervals = intervals;
    }

    public Number getValue() {
        return value;
    }

    public void setValue(Number value) {
        this.value = value;
    }

    public List<Number> getTicks() {
        return ticks;
    }
    public void setTicks(List<Number> ticks) {
        this.ticks = ticks;
    }

    public String getGaugeLabel() {
        return gaugeLabel;
    }
    public void setGaugeLabel(String gaugeLabel) {
        this.gaugeLabel = gaugeLabel;
    }

    public String getGaugeLabelPosition() {
        return gaugeLabelPosition;
    }

    public void setGaugeLabelPosition(String gaugeLabelPosition) {
        this.gaugeLabelPosition = gaugeLabelPosition;
    }

    public double getMin() {
        return min;
    }
    public void setMin(double min) {
        this.min = min;
    }

    public double getMax() {
        return max;
    }
    public void setMax(double max) {
        this.max = max;
    }

    public boolean isShowTickLabels() {
        return showTickLabels;
    }
    public void setShowTickLabels(boolean showTickLabels) {
        this.showTickLabels = showTickLabels;
    }

    public Integer getIntervalInnerRadius() {
        return intervalInnerRadius;
    }

    public void setIntervalInnerRadius(Integer intervalInnerRadius) {
        this.intervalInnerRadius = intervalInnerRadius;
    }

    public Integer getIntervalOuterRadius() {
        return intervalOuterRadius;
    }

    public void setIntervalOuterRadius(Integer intervalOuterRadius) {
        this.intervalOuterRadius = intervalOuterRadius;
    }

    public int getLabelHeightAdjust() {
        return labelHeightAdjust;
    }
    public void setLabelHeightAdjust(int labelHeightAdjust) {
        this.labelHeightAdjust = labelHeightAdjust;
    }
}
