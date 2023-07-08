/*
 * The MIT License
 *
 * Copyright (c) 2009-2023 PrimeTek Informatics
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
package org.primefaces.component.api;

import java.util.Collections;
import java.util.List;
import javax.faces.component.UIComponent;

public class ColumnNode {
    private final ColumnNode parent;
    private final Object uiComp;
    private int colspan = 0;
    private int level = 0;

    public ColumnNode(ColumnNode parent, Object uiComp) {
        this.parent = parent;
        this.uiComp = uiComp;
        this.level = parent != null ? parent.level + 1 : 0;
        if (isLeaf()) {
            incrementColspan();
        }
    }

    void incrementColspan() {
        this.colspan++;
        if (parent != null) {
            parent.incrementColspan();
        }
    }

    boolean isLeaf() {
        return uiComp instanceof UIColumn;
    }

    List<UIComponent> getChildren() {
        if (uiComp instanceof UIComponent && ((UIComponent) uiComp).getChildCount() > 0) {
            return ((UIComponent) uiComp).getChildren();
        }
        return Collections.emptyList();
    }

    public static ColumnNode root(Object column) {
        return new ColumnNode(null, column);
    }

    public Object getUIComp() {
        return uiComp;
    }

    public int getColspan() {
        return colspan;
    }

    public int getLevel() {
        return level;
    }
}
