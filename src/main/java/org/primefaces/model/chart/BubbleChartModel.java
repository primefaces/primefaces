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
package org.primefaces.model.chart;

import java.util.ArrayList;
import java.util.List;

public class BubbleChartModel extends CartesianChartModel {

    private static final long serialVersionUID = 1L;

    private List<BubbleChartSeries> data;
    private boolean bubbleGradients = false;
    private double bubbleAlpha = 1.0;
    private boolean showLabels = true;

    public BubbleChartModel() {
        data = new ArrayList<>();
    }

    public BubbleChartModel(List<BubbleChartSeries> data) {
        this.data = data;
    }

    public List<BubbleChartSeries> getData() {
        return data;
    }

    public void setData(List<BubbleChartSeries> data) {
        this.data = data;
    }

    public void add(BubbleChartSeries bubble) {
        this.data.add(bubble);
    }

    @Deprecated
    public void addBubble(BubbleChartSeries bubble) {
        this.data.add(bubble);
    }

    @Override
    public void clear() {
        this.data.clear();
    }

    public boolean isBubbleGradients() {
        return bubbleGradients;
    }

    public void setBubbleGradients(boolean bubbleGradients) {
        this.bubbleGradients = bubbleGradients;
    }

    public double getBubbleAlpha() {
        return bubbleAlpha;
    }

    public void setBubbleAlpha(double bubbleAlpha) {
        this.bubbleAlpha = bubbleAlpha;
    }

    public boolean isShowLabels() {
        return showLabels;
    }

    public void setShowLabels(boolean showLabels) {
        this.showLabels = showLabels;
    }
}
