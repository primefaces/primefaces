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
package org.primefaces.model.diagram.endpoint;

public class RectangleEndPoint extends EndPoint {

    private int width = 20;
    
    private int height = 20;

    public RectangleEndPoint() {
        super();
    }
    
    public RectangleEndPoint(EndPointAnchor anchor) {
        super(anchor);
    }

    public RectangleEndPoint(EndPointAnchor anchor, int width, int height) {
        this(anchor);
        this.width = width;
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
    
    @Override
    public String getType() {
        return "Rectangle";
    }
    
    @Override
    public String toJS(StringBuilder sb) {
        return sb.append("['Rectangle', {width:").append(width).append(",height:").append(height).append("}]").toString();
    }
    
}
