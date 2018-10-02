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
package org.primefaces.model.charts;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * The data configuration for Charts
 */
public class ChartData implements Serializable {

    private List<ChartDataSet> dataSet;
    private Object labels;

    public ChartData() {
        dataSet = new ArrayList<ChartDataSet>();
    }

    public ChartData(List<ChartDataSet> dataSet, Object labels) {
        this.dataSet = dataSet;
        this.labels = labels;
    }

    /**
     * Gets the dataSet
     *
     * @return dataSet
     */
    public List<ChartDataSet> getDataSet() {
        return dataSet;
    }

    /**
     * Gets the labels
     *
     * @return labels
     */
    public Object getLabels() {
        return labels;
    }

    /**
     * Sets the labels
     *
     * @param labels the object of labels as String or List
     */
    public void setLabels(Object labels) {
        this.labels = labels;
    }

    /**
     * Adds a new dataSet as {@link ChartDataSet} data to dataSet option
     *
     * @param newDataSet
     */
    public void addChartDataSet(ChartDataSet newDataSet) {
        dataSet.add(newDataSet);
    }
}
