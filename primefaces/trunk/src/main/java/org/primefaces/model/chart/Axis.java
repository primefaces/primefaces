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

import java.io.Serializable;

public abstract class Axis implements Serializable {
    
    private String label = "";
    private Object min;
    private Object max;
    private int tickAngle;
    private String tickFormat;
    private String tickInterval;
    private int tickCount;

    public Axis() {

    }
    
    public Axis(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Object getMin() {
        return min;
    }

    public void setMin(Object min) {
        this.min = min;
    }

    public Object getMax() {
        return max;
    }

    public void setMax(Object max) {
        this.max = max;
    }

    public int getTickAngle() {
        return tickAngle;
    }
    public void setTickAngle(int tickAngle) {
        this.tickAngle = tickAngle;
    }

    public String getTickFormat() {
        return tickFormat;
    }
    public void setTickFormat(String tickFormat) {
        this.tickFormat = tickFormat;
    }

    public String getTickInterval() {
        return tickInterval;
    }
    public void setTickInterval(String tickInterval) {
        this.tickInterval = tickInterval;
    }

    public int getTickCount() {
        return tickCount;
    }

    public void setTickCount(int tickCount) {
        this.tickCount = tickCount;
    }
    
    public String getRenderer() {
        return null;
    }
}
