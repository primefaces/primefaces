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
import java.util.Map;

public class DonutChartModel extends ChartModel {

    private static final long serialVersionUID = 1L;

    private List<Map<String, Number>> data;
    private int sliceMargin;
    private boolean fill = true;
    private boolean showDataLabels = false;
    private String dataFormat;
    private String dataLabelFormatString;
    private int dataLabelThreshold;
    private boolean showDatatip = true;
    private String datatipFormat = "%s - %d";
    private String datatipEditor;

    public DonutChartModel() {
        data = new ArrayList<>();
    }

    public DonutChartModel(List<Map<String, Number>> data) {
        this.data = data;
    }

    public List<Map<String, Number>> getData() {
        return data;
    }

    public void setData(List<Map<String, Number>> data) {
        this.data = data;
    }

    public void addCircle(Map<String, Number> circle) {
        this.data.add(circle);
    }

    public void clear() {
        this.data.clear();
    }

    public int getSliceMargin() {
        return sliceMargin;
    }

    public void setSliceMargin(int sliceMargin) {
        this.sliceMargin = sliceMargin;
    }

    public boolean isFill() {
        return fill;
    }

    public void setFill(boolean fill) {
        this.fill = fill;
    }

    public boolean isShowDataLabels() {
        return showDataLabels;
    }

    public void setShowDataLabels(boolean showDataLabels) {
        this.showDataLabels = showDataLabels;
    }

    public String getDataFormat() {
        return dataFormat;
    }

    public void setDataFormat(String dataFormat) {
        this.dataFormat = dataFormat;
    }

    public String getDataLabelFormatString() {
        return dataLabelFormatString;
    }

    public void setDataLabelFormatString(String dataLabelFormatString) {
        this.dataLabelFormatString = dataLabelFormatString;
    }

    public int getDataLabelThreshold() {
        return dataLabelThreshold;
    }

    public void setDataLabelThreshold(int dataLabelThreshold) {
        this.dataLabelThreshold = dataLabelThreshold;
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

    public String getDatatipEditor() {
        return datatipEditor;
    }

    public void setDatatipEditor(String datatipEditor) {
        this.datatipEditor = datatipEditor;
    }
}
