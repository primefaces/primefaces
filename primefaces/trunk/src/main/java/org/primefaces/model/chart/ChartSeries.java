/*
 * Copyright 2009-2011 Prime Technology.
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

public class ChartSeries implements Serializable {

    private String title;

    private Map<Number,Number> data = new LinkedHashMap<Number, Number>();

    public ChartSeries() {}

    public ChartSeries(String title) {
        this.title = title;
    }

    public ChartSeries(String title, Map<Number, Number> data) {
        this.title = title;
        this.data = data;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    
    public Map<Number, Number> getData() {
        return data;
    }

    public void setData(Map<Number, Number> data) {
        this.data = data;
    }

    public void set(Number x, Number y) {
        this.data.put(x, y);
    }
}
