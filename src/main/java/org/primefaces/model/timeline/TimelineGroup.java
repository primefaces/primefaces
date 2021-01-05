/*
 * The MIT License
 *
 * Copyright (c) 2009-2021 PrimeTek
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
package org.primefaces.model.timeline;

import java.io.Serializable;
import java.util.List;

public class TimelineGroup<T> implements Serializable {

    private static final long serialVersionUID = 20140413L;

    /**
     * unique group's id
     */
    private String id;

    /**
     * any custom data object (required to show content of the group)
     */
    private T data;

    /**
     * A title for the group, displayed when holding the mouse on the groups label. The title can only contain plain text.
     */
    private String title;

    /**
     * level of the nested groups.
     */
    private Integer treeLevel;

    /**
     *
     * Ids of nested groups.
     */
    private List<String> nestedGroups;

    /**
     * any custom style class for this event in UI (optional)
     */
    private String styleClass;

    /**
     * Order the subgroups by a field name or custom sort function. By default, groups are ordered by first-come, first-show.
     */
    private String subgroupOrder;

    /**
     * Enables stacking within individual subgroups. Example: {'subgroup0': true, 'subgroup1': false, 'subgroup2': true}
     * For each subgroup where stacking is enabled, items will be stacked on top of each other within that subgroup such
     * that they do no overlap. If set to true all subgroups will be stacked. If a value was specified for the order
     * parameter in the options, that ordering will be used when stacking the items.
     */
    private String subgroupStack;

    /**
     * Ability to hide/show specific subgroups.
     * Example: {'hiddenSubgroup0': false, 'subgroup1': true, 'subgroup2': true} If a subgroup is missing from the
     * object, it will default as true (visible).
     */
    private String subgroupVisibility;

    public TimelineGroup() {
    }

    public TimelineGroup(String id, T data) {
        this.id = id;
        this.data = data;
    }

    public TimelineGroup(String id, T data, String title) {
        this(id, data);
        this.title = title;
    }

    public TimelineGroup(String id, T data, int treeLevel) {
        this(id, data);
        this.treeLevel = treeLevel;
    }

    public TimelineGroup(String id, T data, int treeLevel, List<String> nestedGroups) {
        this(id, data, treeLevel);
        this.nestedGroups = nestedGroups;
    }

    public TimelineGroup(String id, T data, String title, int treeLevel, List<String> nestedGroups) {
        this(id, data, treeLevel, nestedGroups);
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public Integer getTreeLevel() {
        return treeLevel;
    }

    public void setTreeLevel(Integer treeLevel) {
        this.treeLevel = treeLevel;
    }

    public List<String> getNestedGroups() {
        return nestedGroups;
    }

    public void setNestedGroups(List<String> nestedGroups) {
        this.nestedGroups = nestedGroups;
    }

    public String getStyleClass() {
        return styleClass;
    }

    public void setStyleClass(String styleClass) {
        this.styleClass = styleClass;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubgroupOrder() {
        return subgroupOrder;
    }

    public void setSubgroupOrder(String subgroupOrder) {
        this.subgroupOrder = subgroupOrder;
    }

    public String getSubgroupStack() {
        return subgroupStack;
    }

    public void setSubgroupStack(String subgroupStack) {
        this.subgroupStack = subgroupStack;
    }

    public String getSubgroupVisibility() {
        return subgroupVisibility;
    }

    public void setSubgroupVisibility(String subgroupVisibility) {
        this.subgroupVisibility = subgroupVisibility;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        TimelineGroup<?> that = (TimelineGroup) o;

        return !(id != null ? !id.equals(that.id) : that.id != null);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

}
