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

public class OhlcChartModel extends ChartModel implements Serializable {

    private List<OhlcChartSeries> data;

    public OhlcChartModel() {
        data = new ArrayList<OhlcChartSeries>();
    }

    public OhlcChartModel(List<OhlcChartSeries> data) {
        this.data = data;
    }
    
    public List<OhlcChartSeries> getData() {
        return data;
    }

    public void setData(List<OhlcChartSeries> data) {
        this.data = data;
    }
    
    public void addRecord(OhlcChartSeries ohlc){
        this.data.add(ohlc);
    }
    
    @Override
    public String toString(){
        String out = "[";
        for( int i = 0; i < data.size() ; i++){
            if(i != 0)
                out += ",";
            out += data.get(i).toString();
        }
        
        return out + "]";
    }

    public void clear() {
        this.data.clear();
    }
}