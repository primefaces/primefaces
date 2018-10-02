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
package org.primefaces.event;

import javax.faces.component.UIComponent;
import javax.faces.component.behavior.Behavior;

public class ItemSelectEvent extends AbstractAjaxBehaviorEvent {

    private int itemIndex;

    private int seriesIndex;

    private int dataSetIndex;

    public ItemSelectEvent(UIComponent source, Behavior behavior, int itemIndex, int index) {
        super(source, behavior);
        this.itemIndex = itemIndex;
        this.dataSetIndex = index;
        this.seriesIndex = index;
    }

    public int getItemIndex() {
        return itemIndex;
    }

    /**
     * Gets the index of series on JqPlot
     *
     * @return seriesIndex
     */
    public int getSeriesIndex() {
        return seriesIndex;
    }

    /**
     * Gets the index of dataSet on ChartJs
     *
     * @return dataSetIndex
     */
    public int getDataSetIndex() {
        return dataSetIndex;
    }
}
