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
package org.primefaces.model.mindmap;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DefaultMindmapNode implements MindmapNode, Serializable {

    private static final long serialVersionUID = 1L;

    private MindmapNode parent;

    private List<MindmapNode> children;

    private String label;

    private Object data;

    private String fill;

    private boolean selectable;

    public DefaultMindmapNode() {
    }

    public DefaultMindmapNode(String label) {
        this.label = label;
        this.children = new ArrayList<>();
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

    @Override
    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public MindmapNode getParent() {
        return this.parent;
    }

    @Override
    public Object getData() {
        return this.data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    @Override
    public void setParent(MindmapNode parent) {
        if (this.parent != null) {
            this.parent.getChildren().remove(this);
        }

        this.parent = parent;

        if (this.parent != null) {
            this.parent.getChildren().add(this);
        }
    }

    @Override
    public String getFill() {
        return this.fill;
    }

    public void setFill(String fill) {
        this.fill = fill;
    }

    @Override
    public void addNode(MindmapNode node) {
        node.setParent(this);
    }

    @Override
    public boolean isSelectable() {
        return selectable;
    }

    @Override
    public void setSelectable(boolean selectable) {
        this.selectable = selectable;
    }

    @Override
    public List<MindmapNode> getChildren() {
        return this.children;
    }

    public void setChildren(List<MindmapNode> children) {
        this.children = children;
    }
}
