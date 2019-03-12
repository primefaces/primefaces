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
package org.primefaces.model.diagram.connector;

public class StateMachineConnector extends Connector {

    private static final long serialVersionUID = 1L;

    private int curviness = 10;

    private int margin = 5;

    private int proximityLimit = 80;

    private int loopbackRadius = 25;

    private boolean showLoopback = true;

    private Orientation orientation = Orientation.CLOCKWISE;

    public StateMachineConnector() {
    }

    public StateMachineConnector(int curviness, int margin, int proximityLimit, int loopbackRadius, boolean showLoopback, Orientation orientation) {
        this.curviness = curviness;
        this.margin = margin;
        this.proximityLimit = proximityLimit;
        this.loopbackRadius = loopbackRadius;
        this.showLoopback = showLoopback;
        this.orientation = orientation;
    }

    public int getCurviness() {
        return curviness;
    }

    public void setCurviness(int curviness) {
        this.curviness = curviness;
    }

    public int getMargin() {
        return margin;
    }

    public void setMargin(int margin) {
        this.margin = margin;
    }

    public int getProximityLimit() {
        return proximityLimit;
    }

    public void setProximityLimit(int proximityLimit) {
        this.proximityLimit = proximityLimit;
    }

    public int getLoopbackRadius() {
        return loopbackRadius;
    }

    public void setLoopbackRadius(int loopbackRadius) {
        this.loopbackRadius = loopbackRadius;
    }

    public boolean isShowLoopback() {
        return showLoopback;
    }

    public void setShowLoopback(boolean showLoopback) {
        this.showLoopback = showLoopback;
    }

    public Orientation getOrientation() {
        return orientation;
    }

    public void setOrientation(Orientation orientation) {
        this.orientation = orientation;
    }

    @Override
    public String getType() {
        return "StateMachine";
    }

    @Override
    public String toJS(StringBuilder sb) {
        return sb.append("['StateMachine',{curviness:").append(curviness)
                .append(",margin:").append(margin)
                .append(",proximityLimit:").append(proximityLimit)
                .append(",loopbackRadius:").append(loopbackRadius)
                .append(",showLoopback:").append(showLoopback)
                .append(",orientation:'").append(orientation.toString()).append("'}]").toString();
    }

    public enum Orientation {
        CLOCKWISE("clockwise"),
        ANTICLOCKWISE("anticlockwise");

        private final String text;

        private Orientation(final String text) {
            this.text = text;
        }

        @Override
        public String toString() {
            return this.text;
        }
    }
}
