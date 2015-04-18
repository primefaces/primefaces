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
import java.util.Map;

public class DonutChartModel extends ChartModel {

    private List<Map<String,Number>> data;
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
        data = new ArrayList<Map<String, Number>>();
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
    
    public void addCircle(Map<String, Number> circle){
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