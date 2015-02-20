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

public class LabelOverlay implements Overlay, Serializable {

    private String label;
    
    private String styleClass;
    
    private double location = 0.5;

    public LabelOverlay() {
    }
    
    public LabelOverlay(String label) {
        this.label = label;
    }

    public LabelOverlay(String label, String styleClass, double location) {
        this(label);
        this.styleClass = styleClass;
        this.location = location;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getStyleClass() {
        return styleClass;
    }

    public void setStyleClass(String styleClass) {
        this.styleClass = styleClass;
    }

    public double getLocation() {
        return location;
    }

    public void setLocation(double location) {
        this.location = location;
    }
  
    public String getType() {
        return "Label";
    }

    public String toJS(StringBuilder sb) {
        sb.append("['Label',{label:'").append(label).append("'");
        
        if(styleClass != null) sb.append(",cssClass:'").append(styleClass).append("'");
        if(location != 0.5) sb.append(",location:").append(location);
        
        sb.append("}]");
        
        return sb.toString();
    }
}
