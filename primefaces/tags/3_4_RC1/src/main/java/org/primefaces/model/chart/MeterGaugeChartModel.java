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

public class MeterGaugeChartModel extends ChartModel implements Serializable {
 
    private Number value;
    private List<Number> intervals;
    private List<Number> ticks;
    
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

}
