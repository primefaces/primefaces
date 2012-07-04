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

import java.util.List;

public interface MindmapNode {
    
    public List<MindmapNode> getChildren();
    
    public MindmapNode getParent();
    
    public void setParent(MindmapNode node);
    
    public Object getData();
    
    public String getLabel();
    
    public String getFill();
    
    public void addNode(MindmapNode node);
    
    public void setSelectable(boolean selectable);
    
    public boolean isSelectable();
}
