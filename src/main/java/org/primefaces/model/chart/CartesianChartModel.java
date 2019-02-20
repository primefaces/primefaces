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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CartesianChartModel extends ChartModel {

    private static final long serialVersionUID = 1L;

    protected Map<AxisType, Axis> axes;

    private List<ChartSeries> series;
    private boolean zoom = false;
    private boolean animate = false;
    private boolean showDatatip = true;
    private String datatipFormat;
    private boolean showPointLabels = false;
    private String datatipEditor;

    public CartesianChartModel() {
        series = new ArrayList<>();
        createAxes();
    }

    protected void createAxes() {
        axes = new HashMap<>();
        axes.put(AxisType.X, new LinearAxis());
        axes.put(AxisType.Y, new LinearAxis());
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

    public boolean isShowPointLabels() {
        return showPointLabels;
    }

    public void setShowPointLabels(boolean showPointLabels) {
        this.showPointLabels = showPointLabels;
    }

    public String getDatatipEditor() {
        return datatipEditor;
    }

    public void setDatatipEditor(String datatipEditor) {
        this.datatipEditor = datatipEditor;
    }
}
