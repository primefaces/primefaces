/*
 * Copyright 2009-2012 Prime Teknoloji.
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
package org.primefaces.model.mindmap;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DefaultMindmapNode implements MindmapNode, Serializable {
    
    private MindmapNode parent;
    
    private List<MindmapNode> children;
    
    private String label;
    
    private Object data;
    
    private String fill;
    
    private boolean selectable;
   
    public DefaultMindmapNode() {}
    
    public DefaultMindmapNode(String label) {
        this.label = label;
        this.children = new ArrayList<MindmapNode>();
        this.selectable = true;
    }
    
    public DefaultMindmapNode(String label, Object data) {
        this(label);
        this.data = data;
    }
    
    public DefaultMindmapNode(String label, Object data, String fill) {
        this(label, data);
        this.fill = fill;
    }
    
    public DefaultMindmapNode(String label, Object data, String fill, boolean selectable) {
        this(label, data, fill);
        this.selectable = selectable;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
    
    public MindmapNode getParent() {
        return this.parent;
    }

    public Object getData() {
        return this.data;
    }
    
    public void setData(Object data) {
        this.data = data;
    }

    
    public void setParent(MindmapNode parent) {
        if(this.parent != null) {
            this.parent.getChildren().remove(this);
        }
        
        this.parent = parent;
        
        if(this.parent != null) {
            this.parent.getChildren().add(this);
        }
    }

    public String getFill() {
        return this.fill;
    }
    
    public void setFill(String fill) {
        this.fill = fill;
    }

    public void addNode(MindmapNode node) {
        node.setParent(this);
    }

    public boolean isSelectable() {
        return selectable;
    }

    public void setSelectable(boolean selectable) {
        this.selectable = selectable;
    }
    
    public List<MindmapNode> getChildren() {
        return this.children;
    }

    public void setChildren(List<MindmapNode> children) {
        this.children = children;
    }
}
