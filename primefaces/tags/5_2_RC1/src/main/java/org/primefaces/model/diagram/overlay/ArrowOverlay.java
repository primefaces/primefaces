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
package org.primefaces.model.diagram.overlay;

import java.io.Serializable;

public class ArrowOverlay implements Overlay, Serializable {
    
    private int width = 20;
    
    private int length = 20;
    
    private double location = 0.5;
    
    private int direction = 1;
    
    private double foldback = 0.623;
    
    private String paintStyle;

    public ArrowOverlay() {
        
    }
    
    public ArrowOverlay(int width, int length, double location, int direction) {
        this.width = width;
        this.length = length;
        this.location = location;
        this.direction = direction;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public double getLocation() {
        return location;
    }

    public void setLocation(double location) {
        this.location = location;
    }

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    public double getFoldback() {
        return foldback;
    }

    public void setFoldback(double foldback) {
        this.foldback = foldback;
    }

    public String getPaintStyle() {
        return paintStyle;
    }

    public void setPaintStyle(String paintStyle) {
        this.paintStyle = paintStyle;
    }

    public String getType() {
        return "Arrow";
    }

    public String toJS(StringBuilder sb) {
        sb.append("['Arrow',{location:").append(location);
        
        if(width != 20) sb.append(",width:").append(width);
        if(length != 20) sb.append(",length:").append(length);
        if(direction != 1) sb.append(",direction:").append(direction);
        if(foldback != 0.623) sb.append(",foldback:").append(foldback);
        if(paintStyle != null) sb.append(",paintStyle:{").append(paintStyle).append("}");
        
        sb.append("}]");
        
        return sb.toString();
    }    
}
