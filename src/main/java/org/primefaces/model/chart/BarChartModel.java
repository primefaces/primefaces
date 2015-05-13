/*
 * Copyright 2014 jagatai.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.primefaces.model.chart;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class BarChartModel extends CartesianChartModel {
    
    private int barPadding = 8;
    private int barMargin = 10;
    private int barWidth = 0;
    private boolean stacked = false;
        
    @Override
    public void createAxes() {
        axes = new HashMap<AxisType, Axis>();
        axes.put(AxisType.X, new CategoryAxis());
        axes.put(AxisType.Y, new LinearAxis());
    }

    public String getOrientation() {
        return "vertical";
    }

    public int getBarPadding() {
        return barPadding;
    }

    public void setBarPadding(int barPadding) {
        this.barPadding = barPadding;
    }

    public int getBarMargin() {
        return barMargin;
    }

    public void setBarMargin(int barMargin) {
        this.barMargin = barMargin;
    }
    
	public int getBarWidth() {
		return barWidth;
	}

	public void setBarWidth(int barWidth) {
		this.barWidth = barWidth;
	}

	public boolean isStacked() {
        return stacked;
    }
    public void setStacked(boolean stacked) {
        this.stacked = stacked;
    }
    
    public List<String> getTicks() {
        List<ChartSeries> series = this.getSeries();
        List<String> ticks = new ArrayList<String>();
        
        if(series.size() > 0) {
            Map<Object,Number> firstSeriesData = series.get(0).getData();
            for(Iterator<Object> it = firstSeriesData.keySet().iterator(); it.hasNext();) {
                Object key = it.next();
                
                ticks.add(key.toString());
            }
        }
        
        return ticks;
    }
}
