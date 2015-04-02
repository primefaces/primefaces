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
package org.primefaces.model.diagram.connector;

public class StateMachineConnector extends Connector {
    
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

    public String getType() {
        return "StateMachine";
    }

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
