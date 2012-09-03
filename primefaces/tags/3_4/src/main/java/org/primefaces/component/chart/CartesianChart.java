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
package org.primefaces.component.chart;

import java.util.*;
import javax.faces.convert.Converter;
import org.primefaces.model.chart.CartesianChartModel;
import org.primefaces.model.chart.ChartSeries;

public class CartesianChart extends UIChart {
    
    /**
     * Finds the categories using first series
     *
     * @return List of categories
     */
    public List<String> getCategories() {
        CartesianChartModel model = (CartesianChartModel) this.getValue();
        List<ChartSeries> series = model.getSeries();
        List<String> categories = new ArrayList<String>();
        Converter converter = this.getConverter();
        
        if(series.size() > 0) {
            Map<Object,Number> firstSeriesData = series.get(0).getData();
            for(Iterator<Object> it = firstSeriesData.keySet().iterator(); it.hasNext();) {
                Object key = it.next();
                
                if(key instanceof String) {
                    categories.add(key.toString());
                } 
                else if(key instanceof Date) {
                    String formattedDate = converter != null ? converter.getAsString(getFacesContext(), this, key) : key.toString();
                    
                    categories.add(formattedDate);
                } 
                else {
                    break;
                }
            }
        }
        return categories;
    }
}
