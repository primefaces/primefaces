/**
 * The MIT License
 *
 * Copyright (c) 2009-2019 PrimeTek
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
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
