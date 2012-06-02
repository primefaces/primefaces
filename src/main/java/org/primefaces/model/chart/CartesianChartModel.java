/*
 * Copyright 2009-2012 Prime Teknoloji.
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
import java.util.List;

public class CartesianChartModel extends ChartModel implements Serializable {

    private List<ChartSeries> series = new ArrayList<ChartSeries>();

    public List<ChartSeries> getSeries() {
        return series;
    }

    public void addSeries(ChartSeries chartSeries) {
        this.series.add(chartSeries);
    }

    public void clear() {
        this.series.clear();
    }
}
