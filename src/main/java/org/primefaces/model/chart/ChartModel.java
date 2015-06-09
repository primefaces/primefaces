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

public class ChartModel implements Serializable {

    private String title;
    private boolean shadow = true;
    private String seriesColors;
    private String negativeSeriesColors;
    private String legendPosition;
    private int legendCols;
    private int legendRows;
    private LegendPlacement legendPlacement;
    private boolean mouseoverHighlight = true;
    private String extender;
    private boolean resetAxesOnResize = true;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isShadow() {
        return shadow;
    }

    public void setShadow(boolean shadow) {
        this.shadow = shadow;
    }

    public String getSeriesColors() {
        return seriesColors;
    }

    public void setSeriesColors(String seriesColors) {
        this.seriesColors = seriesColors;
    }

    public String getNegativeSeriesColors() {
        return negativeSeriesColors;
    }

    public void setNegativeSeriesColors(String negativeSeriesColors) {
        this.negativeSeriesColors = negativeSeriesColors;
    }
    
    public String getLegendPosition() {
        return legendPosition;
    }

    public void setLegendPosition(String legendPosition) {
        this.legendPosition = legendPosition;
    }

    public int getLegendCols() {
        return legendCols;
    }

    public void setLegendCols(int legendCols) {
        this.legendCols = legendCols;
    }

    public int getLegendRows() {
        return legendRows;
    }

    public void setLegendRows(int legendRows) {
        this.legendRows = legendRows;
    }

    public LegendPlacement getLegendPlacement() {
        return legendPlacement;
    }

    public void setLegendPlacement(LegendPlacement legendPlacement) {
        this.legendPlacement = legendPlacement;
    }
    
    public boolean isMouseoverHighlight() {
        return mouseoverHighlight;
    }

    public void setMouseoverHighlight(boolean mouseoverHighlight) {
        this.mouseoverHighlight = mouseoverHighlight;
    }

    public boolean isResetAxesOnResize() {
        return resetAxesOnResize;
    }

    public void setResetAxesOnResize(boolean resetAxesOnResize) {
        this.resetAxesOnResize = resetAxesOnResize;
    }
 
    public String getExtender() {
        return extender;
    }

    public void setExtender(String extender) {
        this.extender = extender;
    }
}
