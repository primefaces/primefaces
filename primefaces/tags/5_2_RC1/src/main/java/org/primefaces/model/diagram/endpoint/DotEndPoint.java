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

public class DotEndPoint extends EndPoint {
    
    private int radius = 10;

    public DotEndPoint() {
        super();
    }

    public DotEndPoint(EndPointAnchor anchor) {
        super(anchor);
    }
    
    public DotEndPoint(EndPointAnchor anchor, int radius) {
        super(anchor);
        this.radius = radius;
    }
    
    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    @Override
    public String getType() {
        return "Dot";
    }
    
    @Override
    public String toJS(StringBuilder sb) {
        return sb.append("['Dot', {radius:").append(radius).append("}]").toString();
    }
}
