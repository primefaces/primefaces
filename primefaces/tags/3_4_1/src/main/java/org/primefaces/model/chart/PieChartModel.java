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
import java.util.LinkedHashMap;
import java.util.Map;

public class PieChartModel extends ChartModel implements Serializable {

    private Map<String,Number> data;

    public PieChartModel() {
        data = new LinkedHashMap<String, Number>();
    }

    public PieChartModel(Map<String, Number> data) {
        this.data = data;
    }
    
    public Map<String, Number> getData() {
        return data;
    }

    public void setData(Map<String, Number> data) {
        this.data = data;
    }

    public void set(String category, Number data) {
        this.data.put(category, data);
    }

    public void clear() {
        this.data.clear();
    }
}