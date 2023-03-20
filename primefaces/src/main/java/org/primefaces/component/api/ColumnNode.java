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

    public Object getUiComp() {
        return uiComp;
    }

    public int getColspan() {
        return colspan;
    }

    public int getLevel() {
        return level;
    }
}
