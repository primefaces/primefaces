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
package org.primefaces.model.charts.donut;

import org.primefaces.model.charts.ChartData;
import org.primefaces.model.charts.ChartModel;

/**
 * Defines the donut model used to create donut chart component.
 */
public class DonutChartModel extends ChartModel {

    private static final long serialVersionUID = 1L;

    private ChartData data;
    private DonutChartOptions options;

    public DonutChartModel() { }

    public DonutChartModel(ChartData data, DonutChartOptions options) {
        this.data = data;
        this.options = options;
    }

    /**
     * Gets the type
     *
     * @return type of current chart
     */
    @Override
    public String getType() {
        return "doughnut";
    }

    /**
     * Gets the data to create dataSets
     *
     * @return data
     */
    @Override
    public ChartData getData() {
        return data;
    }

    /**
     * Sets the data to create dataSets
     *
     * @param data The {@link ChartData} object
     */
    public void setData(ChartData data) {
        this.data = data;
    }

    /**
     * Gets the configuration options
     *
     * @return options
     */
    @Override
    public DonutChartOptions getOptions() {
        return options;
    }

    /**
     * Sets the configuration options
     *
     * @param options The {@link DonutChartOptions} object
     */
    public void setOptions(DonutChartOptions options) {
        this.options = options;
    }
}
