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
package org.primefaces.model;

import java.util.List;

/**
 * Model for a node in the organigram component.
 */
public interface OrganigramNode {
    // -------------- data properties --------------

    String getType();

    void setType(String type);

    Object getData();

    void setData(Object data);

    List<OrganigramNode> getChildren();

    void setChildren(List<OrganigramNode> children);

    OrganigramNode getParent();

    void setParent(OrganigramNode parent);

    void setRowKey(String rowKey);

    String getRowKey();

    // -------------- state properties --------------
    boolean isExpanded();

    void setExpanded(boolean expanded);

    int getChildCount();

    boolean isLeaf();

    // -------------- option properties --------------
    boolean isSelectable();

    void setSelectable(boolean selectable);

    boolean isDraggable();

    void setDraggable(boolean draggable);

    boolean isDroppable();

    void setDroppable(boolean droppable);

    boolean isCollapsible();

    void setCollapsible(boolean collapsible);

    // -------------- methods --------------
    void clearParent();
}
