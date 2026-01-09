/*
 * The MIT License
 *
 * Copyright (c) 2009-2026 PrimeTek Informatics
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

import org.primefaces.component.column.Column;
import org.primefaces.component.columngroup.ColumnGroup;
import org.primefaces.component.columns.Columns;
import org.primefaces.component.patch.UIDataPatch;
import org.primefaces.model.LazyDataModel;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.ELUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.faces.FacesException;
import jakarta.faces.application.ProjectStage;
import jakarta.faces.component.UIComponent;
import jakarta.faces.component.visit.VisitCallback;
import jakarta.faces.component.visit.VisitContext;
import jakarta.faces.component.visit.VisitContextWrapper;
import jakarta.faces.component.visit.VisitResult;
import jakarta.faces.context.FacesContext;
import jakarta.faces.event.PhaseId;

/**
 * Enhanced version of the Faces UIData.
 */
public class PrimeUIData extends UIDataPatch {

    private static final Logger LOGGER = Logger.getLogger(PrimeUIData.class.getName());

    public enum PropertyKeys {
        rowIndexVar,
        lazy,
    }

    public boolean isLazy() {
        return (boolean) getStateHelper().eval(PropertyKeys.lazy, () -> {
            boolean lazy = false;
            FacesContext context = getFacesContext();

            try {
                // if not set by xhtml, we need to check the type of the value binding
                Class<?> type = ELUtils.getType(context, getValueExpression("value"), this::getValue);

                if (type == null) {
                    if (LOGGER.isLoggable(Level.WARNING) && context.isProjectStage(ProjectStage.Development)) {
                        LOGGER.warning("Unable to automatically determine the `lazy` attribute, fallback to false. "
                                + "Either define the `lazy` attribute on the component or make sure the `value` attribute doesn't resolve to `null`. "
                                + "clientId: " + getClientId());
                    }
                }
                else {
                    lazy = LazyDataModel.class.isAssignableFrom(type);
                }
            }
            catch (Exception e) {
                LOGGER.severe("Exception occurred while determining the `lazy` attribute, fallback to false. "
                        + "To prevent this error set the `lazy` property directly on the component. "
                        + "Error: " + e.getMessage() + ". clientId: " + getClientId());
            }

            // remember in ViewState, to not do the same check again
            setLazy(lazy);

            return lazy;
        });
    }

    public void setLazy(boolean lazy) {
        getStateHelper().put(PropertyKeys.lazy, lazy);
    }

    public String getRowIndexVar() {
        return (String) getStateHelper().eval(PropertyKeys.rowIndexVar, null);
    }

    public void setRowIndexVar(String rowIndexVar) {
        getStateHelper().put(PropertyKeys.rowIndexVar, rowIndexVar);
    }

    @Override
    protected void iterate(FacesContext context, PhaseId phaseId) {
        processFacets(context, phaseId);
        if (requiresColumns()) {
            processColumnFacets(context, phaseId);
        }

        if (shouldSkipChildren(context)) {
            return;
        }

        setRowIndex(-1);
        processChildren(context, phaseId);
        setRowIndex(-1);
    }

    protected void processFacets(FacesContext context, PhaseId phaseId) {
        if (getFacetCount() > 0) {
            for (UIComponent facet : getFacets().values()) {
                process(context, facet, phaseId);
            }
        }
    }

    protected void processColumnFacets(FacesContext context, PhaseId phaseId) {
        for (int i = 0; i < getChildCount(); i++) {
            UIComponent child = getChildren().get(i);
            if (child.isRendered() && (child.getFacetCount() > 0)) {
                for (UIComponent facet : child.getFacets().values()) {
                    process(context, facet, phaseId);
                }
            }
        }
    }

    protected boolean shouldProcessChild(FacesContext context, int rowIndex, PhaseId phaseId) {
        return true;
    }

    protected void processChildren(FacesContext context, PhaseId phaseId) {
        int first = getFirst();
        int rows = getRows();
        int last = rows == 0 ? getRowCount() : (first + rows);

        List<UIComponent> iterableChildren = null;

        for (int rowIndex = first; rowIndex < last; rowIndex++) {
            setRowIndex(rowIndex);

            if (!isRowAvailable()) {
                break;
            }

            if (!shouldProcessChild(context, rowIndex, phaseId)) {
                continue;
            }

            if (iterableChildren == null) {
                iterableChildren = getIterableChildren();
            }

            for (int i = 0; i < iterableChildren.size(); i++) {
                UIComponent child = iterableChildren.get(i);
                if (child.isRendered()) {
                    if (child instanceof Column) {
                        for (UIComponent grandkid : child.getChildren()) {
                            process(context, grandkid, phaseId);
                        }
                    }
                    else {
                        process(context, child, phaseId);
                    }
                }
            }
        }
    }

    protected void process(FacesContext context, UIComponent component, PhaseId phaseId) {
        if (phaseId == PhaseId.APPLY_REQUEST_VALUES) {
            component.processDecodes(context);
        }
        else if (phaseId == PhaseId.PROCESS_VALIDATIONS) {
            component.processValidators(context);
        }
        else if (phaseId == PhaseId.UPDATE_MODEL_VALUES) {
            component.processUpdates(context);
        }

    }

    @Override
    protected void setRowIndexWithoutRowStatePreserved(int rowIndex) {
        saveDescendantState();
        setRowModel(rowIndex);
        restoreDescendantState();
    }

    public void setRowModel(int rowIndex) {
        //update rowIndex
        getStateHelper().put(UIDataPatch.PropertyKeys.rowIndex, rowIndex);
        getDataModel().setRowIndex(rowIndex);

        //clear datamodel
        if (rowIndex == -1) {
            setDataModel(null);
        }

        //update var
        String var = getVar();
        if (var != null) {
            String rowIndexVar = getRowIndexVar();
            Map<String, Object> requestMap = getFacesContext().getExternalContext().getRequestMap();

            if (rowIndex == -1) {
                oldVar = requestMap.remove(var);

                if (rowIndexVar != null) {
                    requestMap.remove(rowIndexVar);
                }
            }
            else if (isRowAvailable()) {
                requestMap.put(var, getRowData());

                if (rowIndexVar != null) {
                    requestMap.put(rowIndexVar, rowIndex);
                }
            }
            else {
                requestMap.remove(var);

                if (rowIndexVar != null) {
                    requestMap.put(rowIndexVar, rowIndex);
                }

                if (oldVar != null) {
                    requestMap.put(var, oldVar);
                    oldVar = null;
                }
            }
        }
    }

    @Override
    protected void saveDescendantState() {
        FacesContext context = getFacesContext();

        if (getChildCount() > 0) {
            for (int i = 0; i < getChildCount(); i++) {
                UIComponent kid = getChildren().get(i);
                saveDescendantState(kid, context);
            }
        }

        if (getFacetCount() > 0) {
            for (UIComponent facet : getFacets().values()) {
                saveDescendantState(facet, context);
            }
        }
    }

    @Override
    protected void restoreDescendantState() {
        FacesContext context = getFacesContext();

        if (getChildCount() > 0) {
            for (int i = 0; i < getChildCount(); i++) {
                UIComponent kid = getChildren().get(i);
                restoreDescendantState(kid, context);
            }
        }

        if (getFacetCount() > 0) {
            for (UIComponent facet : getFacets().values()) {
                restoreDescendantState(facet, context);
            }
        }
    }

    protected boolean shouldSkipChildren(FacesContext context) {
        return false;
    }

    @Override
    public boolean visitTree(VisitContext context, VisitCallback callback) {
        return super.visitTree(new VisitContextImpl(context), callback);
    }

    @Override
    protected boolean visitColumnsAndColumnFacets(VisitContext context, VisitCallback callback, boolean visitRows) {
        if (!requiresColumns()) {
            return false;
        }

        if (visitRows) {
            setRowIndex(-1);
        }

        if (getChildCount() > 0) {
            for (int i = 0; i < getChildCount(); i++) {
                UIComponent child = getChildren().get(i);
                VisitResult result = context.invokeVisitCallback(child, callback); // visit the column directly
                if (result == VisitResult.COMPLETE) {
                    return true;
                }
                else if (result == VisitResult.REJECT) {
                    continue;
                }

                if (child instanceof UIColumn) {
                    if (child.getFacetCount() > 0) {
                        if (child instanceof Columns) {
                            Columns columns = (Columns) child;
                            for (int j = 0; j < columns.getRowCount(); j++) {
                                columns.setRowIndex(j);
                                boolean value = visitColumnFacets(context, callback, child);
                                if (value) {
                                    return true;
                                }
                            }
                            columns.setRowIndex(-1);
                        }
                        else {
                            boolean value = visitColumnFacets(context, callback, child);
                            if (value) {
                                return true;
                            }
                        }

                    }
                }
                else if (child instanceof ColumnGroup) {
                    visitColumnGroup(context, callback, (ColumnGroup) child);
                }
            }
        }

        return false;
    }

    protected boolean visitColumnGroup(VisitContext context, VisitCallback callback, ColumnGroup group) {
        if (group.getChildCount() > 0) {
            for (int i = 0; i < group.getChildCount(); i++) {
                UIComponent row = group.getChildren().get(i);
                if (row.getChildCount() > 0) {
                    for (int j = 0; j < row.getChildCount(); j++) {
                        UIComponent col = row.getChildren().get(j);
                        if (col instanceof Column) {
                            boolean value = visitColumnFacets(context, callback, col);
                            if (value) {
                                return true;
                            }
                        }
                        else if (col instanceof Columns) {
                            if (col.getFacetCount() > 0) {
                                Columns columns = (Columns) col;
                                for (int k = 0; k < columns.getRowCount(); k++) {
                                    columns.setRowIndex(k);

                                    boolean value = visitColumnFacets(context, callback, columns);
                                    if (value) {
                                        columns.setRowIndex(-1);
                                        return true;
                                    }
                                }

                                columns.setRowIndex(-1);
                            }
                        }
                    }
                }
            }
        }

        return false;
    }

    protected boolean visitColumnFacets(VisitContext context, VisitCallback callback, UIComponent component) {
        if (component.getFacetCount() > 0) {
            for (UIComponent columnFacet : component.getFacets().values()) {
                if (columnFacet.visitTree(context, callback)) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    protected boolean visitRows(VisitContext context, VisitCallback callback, boolean visitRows) {
        if (!(context instanceof VisitContextImpl)) {
            throw new FacesException();
        }

        boolean requiresColumns = requiresColumns();
        int processed = 0;
        int rowIndex = 0;
        int rows = 0;
        if (visitRows) {
            rowIndex = getFirst() - 1;
            rows = getRows();
        }

        while (true) {
            if (visitRows) {
                if ((rows > 0) && (++processed > rows)) {
                    break;
                }

                setRowIndex(++rowIndex);
                if (!isRowAvailable()) {
                    break;
                }
            }

            if (getChildCount() > 0) {
                for (int i = 0; i < getChildCount(); i++) {
                    UIComponent kid = getChildren().get(i);
                    if (!((VisitContextImpl) context).isRejected(kid)) {
                        if (requiresColumns) {
                            if (kid instanceof Columns) {
                                Columns columns = (Columns) kid;
                                for (int j = 0; j < columns.getRowCount(); j++) {
                                    columns.setRowIndex(j);

                                    boolean value = visitColumnContent(context, callback, columns);
                                    if (value) {
                                        columns.setRowIndex(-1);
                                        return true;
                                    }
                                }

                                columns.setRowIndex(-1);
                            }
                            else {
                                boolean value = visitColumnContent(context, callback, kid);
                                if (value) {
                                    return true;
                                }
                            }
                        }
                        else {
                            if (kid.visitTree(context, callback)) {
                                return true;
                            }
                        }
                    }
                }
            }

            if (!visitRows) {
                break;
            }

        }

        return false;
    }

    protected boolean visitColumnContent(VisitContext context, VisitCallback callback, UIComponent component) {
        if (component.getChildCount() > 0) {
            for (int i = 0; i < component.getChildCount(); i++) {
                UIComponent grandkid = component.getChildren().get(i);
                if (grandkid.visitTree(context, callback)) {
                    return true;
                }
            }
        }

        return false;
    }

    protected boolean requiresColumns() {
        return false;
    }

    protected List<UIComponent> getIterableChildren() {
        return getChildren();
    }

    @Override
    protected Boolean isNestedWithinIterator() {
        if (isNested == null) {
            isNested = ComponentUtils.isNestedWithinIterator(this);
        }

        return isNested;
    }

    // #9171
    private static class VisitContextImpl extends VisitContextWrapper {

        private Set<UIComponent> rejectedChildren;

        public VisitContextImpl(VisitContext wrapped) {
            super(wrapped);
        }

        @Override
        public VisitResult invokeVisitCallback(UIComponent component, VisitCallback callback) {
            VisitResult result = super.invokeVisitCallback(component, callback);
            if (result == VisitResult.REJECT) {
                if (rejectedChildren == null) {
                    rejectedChildren = new HashSet<>();
                }
                rejectedChildren.add(component);
            }
            return result;
        }

        public boolean isRejected(UIComponent component) {
            return rejectedChildren != null && rejectedChildren.contains(component);
        }
    }
}
