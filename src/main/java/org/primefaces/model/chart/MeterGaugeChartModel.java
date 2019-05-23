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

import java.util.ArrayList;
import java.util.List;

public class MeterGaugeChartModel extends ChartModel {

    private static final long serialVersionUID = 1L;

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
        intervals = new ArrayList<>();
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
