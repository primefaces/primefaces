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
package org.primefaces.component.api;

import java.io.IOException;
import java.sql.ResultSet;
import java.util.*;

import javax.el.ValueExpression;
import javax.faces.FacesException;
import javax.faces.application.Application;
import javax.faces.application.FacesMessage;
import javax.faces.component.*;
import javax.faces.component.visit.VisitCallback;
import javax.faces.component.visit.VisitContext;
import javax.faces.component.visit.VisitResult;
import javax.faces.context.FacesContext;
import javax.faces.event.*;
import javax.faces.model.*;
import javax.faces.render.Renderer;

import org.primefaces.component.tabview.Tab;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.Constants;
import org.primefaces.util.LangUtils;
import org.primefaces.util.SharedStringBuilder;
import org.primefaces.model.CollectionDataModel;
import org.primefaces.model.IterableDataModel;

/**
 * UITabPanel is a specialized version of UIRepeat focusing on components that repeat tabs like tabView and accordionPanel.
 * Most of the code is copied from MyFaces.
 */
public class UITabPanel extends UIPanel implements NamingContainer {

    private static final DataModel<?> EMPTY_MODEL = new ListDataModel<>(Collections.emptyList());

    private static final Class<Object[]> OBJECT_ARRAY_CLASS = Object[].class;

    private static final Object[] LEAF_NO_STATE = new Object[]{null, null};

    private static final String SB_ID = UITabPanel.class.getName() + "#id";
    // Holds for each row the states of the child components of this UIData.
    // Note that only "partial" component state is saved: the component fields
    // that are expected to vary between rows.
    private final Map<String, Collection<Object[]>> _rowStates = new HashMap<>();
    /**
     * Handle case where this table is nested inside another table. See method getDataModel for more details.
     * <p>
     * Key: parentClientId (aka rowId when nested within a parent table) Value: DataModel
     */
    private final Map<String, DataModel> _dataModelMap = new HashMap<>();
    private Object _initialDescendantComponentState = null;
    // will be set to false if the data should not be refreshed at the beginning of the encode phase
    private boolean _isValidChilds = true;
    private int _end = -1;
    private int _count;
    private int _index = -1;
    private transient Object _origValue;
    private transient Object _origVarStatus;
    private transient FacesContext _facesContext;

    public enum PropertyKeys {
        value,
        var,
        size,
        varStatus,
        offset,
        step,
        dynamic,
        prependId
    }

    public int getOffset() {
        return (Integer) getStateHelper().eval(PropertyKeys.offset, 0);
    }

    public void setOffset(int offset) {
        getStateHelper().put(PropertyKeys.offset, offset);
    }

    public int getSize() {
        return (Integer) getStateHelper().eval(PropertyKeys.size, -1);
    }

    public void setSize(int size) {
        getStateHelper().put(PropertyKeys.size, size);
    }

    public int getStep() {
        return (Integer) getStateHelper().eval(PropertyKeys.step, 1);
    }

    public void setStep(int step) {
        getStateHelper().put(PropertyKeys.step, step);
    }

    public String getVar() {
        return (String) getStateHelper().get(PropertyKeys.var);
    }

    public void setVar(String var) {
        getStateHelper().put(PropertyKeys.var, var);
    }

    public String getVarStatus() {
        return (String) getStateHelper().get(PropertyKeys.varStatus);
    }

    public void setVarStatus(String varStatus) {
        getStateHelper().put(PropertyKeys.varStatus, varStatus);
    }

    public boolean isDynamic() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.dynamic, false);
    }

    public void setDynamic(boolean _dynamic) {
        getStateHelper().put(PropertyKeys.dynamic, _dynamic);
    }

    public boolean isPrependId() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.prependId, true);
    }

    public void setPrependId(boolean _prependId) {
        getStateHelper().put(PropertyKeys.prependId, _prependId);
    }

    protected DataModel getDataModel() {
        DataModel dataModel;
        String clientID = "";

        UIComponent parent = getParent();
        if (parent != null) {
            clientID = parent.getContainerClientId(getFacesContext());
        }
        dataModel = _dataModelMap.get(clientID);
        if (dataModel == null) {
            dataModel = createDataModel();
            _dataModelMap.put(clientID, dataModel);
        }
        return dataModel;
    }

    private DataModel createDataModel() {
        Object value = getValue();

        if (value == null) {
            return EMPTY_MODEL;
        }
        else if (value instanceof DataModel) {
            return (DataModel) value;
        }
        else if (value instanceof List) {
            return new ListDataModel((List<?>) value);
        }
        else if (OBJECT_ARRAY_CLASS.isAssignableFrom(value.getClass())) {
            return new ArrayDataModel((Object[]) value);
        }
        else if (value instanceof Collection) {
            return new CollectionDataModel((Collection) value);
        }
        else if (value instanceof Iterable) {
            return new IterableDataModel((Iterable<?>) value);
        }
        else if (value instanceof ResultSet) {
            return new ResultSetDataModel((ResultSet) value);
        }
        else if (value instanceof Map) {
            return new IterableDataModel(((Map<?, ?>) value).entrySet());
        }
        else {
            return new ScalarDataModel(value);
        }
    }

    @Override
    public void setValueExpression(String name, ValueExpression binding) {
        if (name == null) {
            throw new NullPointerException("name");
        }
        else if (name.equals("value")) {
            _dataModelMap.clear();
        }
        else if (name.equals("rowIndex")) {
            throw new IllegalArgumentException("name " + name);
        }
        super.setValueExpression(name, binding);
    }

    public Object getValue() {
        return getStateHelper().eval(PropertyKeys.value);
    }

    public void setValue(Object value) {
        getStateHelper().put(PropertyKeys.value, value);
        _dataModelMap.clear();
        _rowStates.clear();
        _isValidChilds = true;
    }

    @Override
    public String getContainerClientId(FacesContext context) {
        if (isPrependId() || isRepeating()) {
            String clientId = super.getContainerClientId(context);

            int index = getIndex();
            if (index == -1) {
                return clientId;
            }

            StringBuilder sb = SharedStringBuilder.get(getFacesContext(), SB_ID, clientId.length() + 4);
            return sb.append(clientId).append(UINamingContainer.getSeparatorChar(context)).append(index).toString();
        }
        else {
            UIComponent parent = getParent();
            while (parent != null) {
                if (parent instanceof NamingContainer) {
                    return parent.getContainerClientId(context);
                }
                parent = parent.getParent();
            }

            return null;
        }
    }

    private RepeatStatus getRepeatStatus() {
        return new RepeatStatus(_count == 0, _index + getStep() >= getDataModel().getRowCount(),
                _count, _index, getOffset(), _end, getStep());
    }

    private void captureScopeValues() {
        String var = getVar();
        if (var != null) {
            _origValue = getFacesContext().getExternalContext().getRequestMap().get(var);
        }
        String varStatus = getVarStatus();
        if (varStatus != null) {
            _origVarStatus = getFacesContext().getExternalContext().getRequestMap().get(varStatus);
        }
    }

    private boolean isIndexAvailable() {
        return getDataModel().isRowAvailable();
    }

    private void restoreScopeValues() {
        String var = getVar();
        if (var != null) {
            Map<String, Object> attrs = getFacesContext().getExternalContext().getRequestMap();
            if (_origValue != null) {
                attrs.put(var, _origValue);
                _origValue = null;
            }
            else {
                attrs.remove(var);
            }
        }
        String varStatus = getVarStatus();
        if (getVarStatus() != null) {
            Map<String, Object> attrs = getFacesContext().getExternalContext().getRequestMap();
            if (_origVarStatus != null) {
                attrs.put(varStatus, _origVarStatus);
                _origVarStatus = null;
            }
            else {
                attrs.remove(varStatus);
            }
        }
    }

    /**
     * Overwrite the state of the child components of this component with data previously saved by method saveDescendantComponentStates.
     * <p>
     * The saved state info only covers those fields that are expected to vary between rows of a table. Other fields are not modified.
     */
    private void restoreDescendantComponentStates(UIComponent parent, boolean iterateFacets, Object state,
                                                  boolean restoreChildFacets) {
        int descendantStateIndex = -1;
        List<? extends Object[]> stateCollection = null;

        if (iterateFacets && parent.getFacetCount() > 0) {
            Iterator<UIComponent> childIterator = parent.getFacets().values().iterator();

            while (childIterator.hasNext()) {
                UIComponent component = childIterator.next();

                // reset the client id (see spec 3.1.6)
                component.setId(component.getId());
                if (!component.isTransient()) {
                    if (descendantStateIndex == -1) {
                        stateCollection = ((List<? extends Object[]>) state);
                        descendantStateIndex = stateCollection.isEmpty() ? -1 : 0;
                    }

                    if (descendantStateIndex != -1 && descendantStateIndex < stateCollection.size()) {
                        Object[] object = stateCollection.get(descendantStateIndex);
                        if (object[0] != null && component instanceof EditableValueHolder) {
                            ((SavedState) object[0]).restoreState((EditableValueHolder) component);
                        }
                        // If there is descendant state to restore, call it recursively, otherwise
                        // it is safe to skip iteration.
                        if (object[1] != null) {
                            restoreDescendantComponentStates(component, restoreChildFacets, object[1], true);
                        }
                        else {
                            restoreDescendantComponentWithoutRestoreState(component, restoreChildFacets, true);
                        }
                    }
                    else {
                        restoreDescendantComponentWithoutRestoreState(component, restoreChildFacets, true);
                    }
                    descendantStateIndex++;
                }
            }
        }

        if (parent.getChildCount() > 0) {
            for (int i = 0; i < parent.getChildCount(); i++) {
                UIComponent component = parent.getChildren().get(i);

                // reset the client id (see spec 3.1.6)
                component.setId(component.getId());
                if (!component.isTransient()) {
                    if (descendantStateIndex == -1) {
                        stateCollection = ((List<? extends Object[]>) state);
                        descendantStateIndex = stateCollection.isEmpty() ? -1 : 0;
                    }

                    if (descendantStateIndex != -1 && descendantStateIndex < stateCollection.size()) {
                        Object[] object = stateCollection.get(descendantStateIndex);
                        if (object[0] != null && component instanceof EditableValueHolder) {
                            ((SavedState) object[0]).restoreState((EditableValueHolder) component);
                        }
                        // If there is descendant state to restore, call it recursively, otherwise
                        // it is safe to skip iteration.
                        if (object[1] != null) {
                            restoreDescendantComponentStates(component, restoreChildFacets, object[1], true);
                        }
                        else {
                            restoreDescendantComponentWithoutRestoreState(component, restoreChildFacets, true);
                        }
                    }
                    else {
                        restoreDescendantComponentWithoutRestoreState(component, restoreChildFacets, true);
                    }
                    descendantStateIndex++;
                }
            }
        }
    }

    /**
     * Just call component.setId(component.getId()) to reset all client ids and ensure they will be calculated for the current row, but do not waste
     * time dealing with row state code.
     *
     * @param parent
     * @param iterateFacets
     * @param restoreChildFacets
     */
    private void restoreDescendantComponentWithoutRestoreState(UIComponent parent, boolean iterateFacets,
                                                               boolean restoreChildFacets) {
        if (iterateFacets && parent.getFacetCount() > 0) {
            Iterator<UIComponent> childIterator = parent.getFacets().values().iterator();

            while (childIterator.hasNext()) {
                UIComponent component = childIterator.next();

                // reset the client id (see spec 3.1.6)
                component.setId(component.getId());
                if (!component.isTransient()) {
                    restoreDescendantComponentWithoutRestoreState(component, restoreChildFacets, true);
                }
            }
        }

        if (parent.getChildCount() > 0) {
            for (int i = 0; i < parent.getChildCount(); i++) {
                UIComponent component = parent.getChildren().get(i);

                // reset the client id (see spec 3.1.6)
                component.setId(component.getId());
                if (!component.isTransient()) {
                    restoreDescendantComponentWithoutRestoreState(component, restoreChildFacets, true);
                }
            }
        }
    }

    /**
     * Walk the tree of child components of this UIData, saving the parts of their state that can vary between rows.
     * <p>
     * This is very similar to the process that occurs for normal components when the view is serialized. Transient components are skipped (no state is saved
     * for them).
     * <p>
     * If there are no children then null is returned. If there are one or more children, and all children are transient then an empty collection is returned;
     * this will happen whenever a table contains only read-only components.
     * <p>
     * Otherwise a collection is returned which contains an object for every non-transient child component; that object may itself contain a collection of the
     * state of that child's child components.
     */
    private Collection<Object[]> saveDescendantComponentStates(UIComponent parent, boolean iterateFacets,
                                                               boolean saveChildFacets) {
        Collection<Object[]> childStates = null;
        // Index to indicate how many components has been passed without state to save.
        int childEmptyIndex = 0;
        int totalChildCount = 0;

        if (iterateFacets && parent.getFacetCount() > 0) {
            Iterator<UIComponent> childIterator = parent.getFacets().values().iterator();

            while (childIterator.hasNext()) {
                UIComponent child = childIterator.next();
                if (!child.isTransient()) {
                    // Add an entry to the collection, being an array of two
                    // elements. The first element is the state of the children
                    // of this component; the second is the state of the current
                    // child itself.

                    if (child instanceof EditableValueHolder) {
                        if (childStates == null) {
                            childStates = new ArrayList<>(
                                    parent.getFacetCount()
                                            + parent.getChildCount()
                                            - totalChildCount
                                            + childEmptyIndex);
                            for (int ci = 0; ci < childEmptyIndex; ci++) {
                                childStates.add(LEAF_NO_STATE);
                            }
                        }

                        childStates.add(child.getChildCount() > 0
                                        ? new Object[]{new SavedState((EditableValueHolder) child),
                                saveDescendantComponentStates(child, saveChildFacets, true)}
                                        : new Object[]{new SavedState((EditableValueHolder) child), null});
                    }
                    else if (child.getChildCount() > 0 || (saveChildFacets && child.getFacetCount() > 0)) {
                        Object descendantSavedState = saveDescendantComponentStates(child, saveChildFacets, true);

                        if (descendantSavedState == null) {
                            if (childStates == null) {
                                childEmptyIndex++;
                            }
                            else {
                                childStates.add(LEAF_NO_STATE);
                            }
                        }
                        else {
                            if (childStates == null) {
                                childStates = new ArrayList<>(
                                        parent.getFacetCount()
                                                + parent.getChildCount()
                                                - totalChildCount
                                                + childEmptyIndex);
                                for (int ci = 0; ci < childEmptyIndex; ci++) {
                                    childStates.add(LEAF_NO_STATE);
                                }
                            }
                            childStates.add(new Object[]{null, descendantSavedState});
                        }
                    }
                    else {
                        if (childStates == null) {
                            childEmptyIndex++;
                        }
                        else {
                            childStates.add(LEAF_NO_STATE);
                        }
                    }
                }
                totalChildCount++;
            }
        }

        if (parent.getChildCount() > 0) {
            for (int i = 0; i < parent.getChildCount(); i++) {
                UIComponent child = parent.getChildren().get(i);
                if (!child.isTransient()) {
                    // Add an entry to the collection, being an array of two
                    // elements. The first element is the state of the children
                    // of this component; the second is the state of the current
                    // child itself.

                    if (child instanceof EditableValueHolder) {
                        if (childStates == null) {
                            childStates = new ArrayList<>(
                                    parent.getFacetCount()
                                            + parent.getChildCount()
                                            - totalChildCount
                                            + childEmptyIndex);
                            for (int ci = 0; ci < childEmptyIndex; ci++) {
                                childStates.add(LEAF_NO_STATE);
                            }
                        }

                        childStates.add(child.getChildCount() > 0
                                        ? new Object[]{new SavedState((EditableValueHolder) child),
                                saveDescendantComponentStates(child, saveChildFacets, true)}
                                        : new Object[]{new SavedState((EditableValueHolder) child), null});
                    }
                    else if (child.getChildCount() > 0 || (saveChildFacets && child.getFacetCount() > 0)) {
                        Object descendantSavedState = saveDescendantComponentStates(child, saveChildFacets, true);

                        if (descendantSavedState == null) {
                            if (childStates == null) {
                                childEmptyIndex++;
                            }
                            else {
                                childStates.add(LEAF_NO_STATE);
                            }
                        }
                        else {
                            if (childStates == null) {
                                childStates = new ArrayList<>(
                                        parent.getFacetCount()
                                                + parent.getChildCount()
                                                - totalChildCount
                                                + childEmptyIndex);
                                for (int ci = 0; ci < childEmptyIndex; ci++) {
                                    childStates.add(LEAF_NO_STATE);
                                }
                            }
                            childStates.add(new Object[]{null, descendantSavedState});
                        }
                    }
                    else {
                        if (childStates == null) {
                            childEmptyIndex++;
                        }
                        else {
                            childStates.add(LEAF_NO_STATE);
                        }
                    }
                }
                totalChildCount++;
            }
        }

        return childStates;
    }

    /**
     * Returns the rowCount of the underlying DataModel.
     *
     * @return
     */
    public int getRowCount() {
        return getDataModel().getRowCount();
    }

    /**
     * Returns the rowCount of the underlying DataModel.
     *
     * @return
     */
    public Object getIndexData() {
        if (!getDataModel().isRowAvailable()) {
            return null;
        }

        return getDataModel().getRowData();
    }

    /**
     * Returns the current index.
     */
    public int getIndex() {
        return _index;
    }

    public void setIndex(int index) {
        // save child state
        //_saveChildState();
        if (index < -1) {
            throw new IllegalArgumentException("rowIndex is less than -1");
        }

        if (_index == index) {
            return;
        }

        FacesContext facesContext = getFacesContext();

        if (_index == -1) {
            if (_initialDescendantComponentState == null) {
                // Create a template that can be used to initialise any row
                // that we haven't visited before, ie a "saved state" that can
                // be pushed to the "restoreState" method of all the child
                // components to set them up to represent a clean row.
                _initialDescendantComponentState = saveDescendantComponentStates(this, true, true);
            }
        }
        else {
            // If no initial component state, there are no EditableValueHolder instances,
            // and that means there is no state to be saved for the current row, so we can
            // skip row state saving code safely.
            if (_initialDescendantComponentState != null) {
                // We are currently positioned on some row, and are about to
                // move off it, so save the (partial) state of the components
                // representing the current row. Later if this row is revisited
                // then we can restore this state.
                Collection<Object[]> savedRowState = saveDescendantComponentStates(this, true, true);
                if (savedRowState != null) {
                    _rowStates.put(getContainerClientId(facesContext), savedRowState);
                }
            }
        }

        _index = index;

        DataModel<?> localModel = getDataModel();
        localModel.setRowIndex(index);

        if (_index != -1) {
            String var = getVar();
            if (var != null && localModel.isRowAvailable()) {
                getFacesContext().getExternalContext().getRequestMap()
                        .put(var, localModel.getRowData());
            }
            String varStatus = getVarStatus();
            if (varStatus != null) {
                getFacesContext().getExternalContext().getRequestMap()
                        .put(varStatus, getRepeatStatus());
            }
        }

        // restore child state
        //_restoreChildState();
        if (_index == -1) {
            // reset components to initial state
            // If no initial state, skip row restore state code
            if (_initialDescendantComponentState != null) {
                restoreDescendantComponentStates(this, true, _initialDescendantComponentState, true);
            }
            else {
                restoreDescendantComponentWithoutRestoreState(this, true, true);
            }
        }
        else {
            Object rowState = _rowStates.get(getContainerClientId(facesContext));
            if (rowState == null) {
                // We haven't been positioned on this row before, so just
                // configure the child components of this component with
                // the standard "initial" state
                // If no initial state, skip row restore state code
                if (_initialDescendantComponentState != null) {
                    restoreDescendantComponentStates(this, true, _initialDescendantComponentState, true);
                }
                else {
                    restoreDescendantComponentWithoutRestoreState(this, true, true);
                }
            }
            else {
                // We have been positioned on this row before, so configure
                // the child components of this component with the (partial)
                // state that was previously saved. Fields not in the
                // partial saved state are left with their original values.
                restoreDescendantComponentStates(this, true, rowState, true);
            }
        }
    }

    /**
     * Calculates the count value for the given index.
     *
     * @param index
     * @return
     */
    private int calculateCountForIndex(int index) {
        return (index - getOffset()) / getStep();
    }

    private void validateAttributes() throws FacesException {
        int begin = getOffset();
        int end = getDataModel().getRowCount();
        int size = getSize();
        int step = getStep();
        boolean sizeIsEnd = false;

        if (size == -1) {
            size = end;
            sizeIsEnd = true;
        }

        if (end >= 0) {
            if (size < 0) {
                throw new FacesException("iteration size cannot be less "
                        + "than zero");
            }
            else if (!sizeIsEnd && (begin + size) > end) {
                throw new FacesException("iteration size cannot be greater "
                        + "than collection size");
            }
        }

        if ((size > -1) && (begin > end)) {
            throw new FacesException("iteration offset cannot be greater "
                    + "than collection size");
        }

        if (step == -1) {
            setStep(1);
        }

        if (step < 0) {
            throw new FacesException("iteration step size cannot be less "
                    + "than zero");
        }
        else if (step == 0) {
            throw new FacesException("iteration step size cannot be equal "
                    + "to zero");
        }

        _end = size;
        //_step = step;
    }

    public void process(FacesContext faces, PhaseId phase) {
        // stop if not rendered
        if (!isRendered()) {
            return;
        }

        // validate attributes
        validateAttributes();

        // reset index
        captureScopeValues();
        setIndex(-1);

        try {
            // has children
            if (getChildCount() > 0) {
                int i = getOffset();
                int end = getSize();
                int step = getStep();
                end = (end >= 0) ? i + end : Integer.MAX_VALUE - 1;

                // grab renderer
                String rendererType = getRendererType();
                Renderer renderer = null;
                if (rendererType != null) {
                    renderer = getRenderer(faces);
                }

                _count = 0;

                setIndex(i);
                while (i <= end && isIndexAvailable()) {

                    if (PhaseId.RENDER_RESPONSE.equals(phase) && renderer != null) {
                        renderer.encodeChildren(faces, this);
                    }
                    else {
                        for (int j = 0, childCount = getChildCount(); j < childCount; j++) {
                            UIComponent child = getChildren().get(j);
                            if (PhaseId.APPLY_REQUEST_VALUES.equals(phase)) {
                                child.processDecodes(faces);
                            }
                            else if (PhaseId.PROCESS_VALIDATIONS.equals(phase)) {
                                child.processValidators(faces);
                            }
                            else if (PhaseId.UPDATE_MODEL_VALUES.equals(phase)) {
                                child.processUpdates(faces);
                            }
                            else if (PhaseId.RENDER_RESPONSE.equals(phase)) {
                                child.encodeAll(faces);
                            }
                        }
                    }

                    ++_count;

                    i += step;

                    setIndex(i);
                }
            }
        }
        catch (IOException e) {
            throw new FacesException(e);
        }
        finally {
            setIndex(-1);
            restoreScopeValues();
        }
    }

    @Override
    public boolean invokeOnComponent(FacesContext context, String clientId, ContextCallback callback) throws FacesException {
        if (!isRepeating()) {
            return super.invokeOnComponent(context, clientId, callback);
        }
        else {
            if (context == null || clientId == null || callback == null) {
                throw new NullPointerException();
            }

            final String baseClientId = getClientId(context);

            // searching for this component?
            boolean returnValue = baseClientId.equals(clientId);

            boolean isCachedFacesContext = isTemporalFacesContext();
            if (!isCachedFacesContext) {
                setTemporalFacesContext(context);
            }

            pushComponentToEL(context, this);
            try {
                if (returnValue) {
                    try {
                        callback.invokeContextCallback(context, this);
                        return true;
                    }
                    catch (Exception e) {
                        throw new FacesException(e);
                    }
                }

                // Now Look throught facets on this UIComponent
                if (getFacetCount() > 0) {
                    for (Iterator<UIComponent> it = getFacets().values().iterator(); !returnValue && it.hasNext(); ) {
                        returnValue = it.next().invokeOnComponent(context, clientId, callback);
                    }
                }

                if (returnValue) {
                    return returnValue;
                }

                // is the component an inner component?
                if (clientId.startsWith(baseClientId)) {
                    // Check if the clientId for the component, which we
                    // are looking for, has a rowIndex attached
                    char separator = UINamingContainer.getSeparatorChar(context);
                    String subId = clientId.substring(baseClientId.length() + 1);
                    //If the char next to baseClientId is the separator one and
                    //the subId matches the regular expression
                    if (clientId.charAt(baseClientId.length()) == separator
                            && subId.matches("[0-9]+" + separator + ".*")) {
                        String clientRow = subId.substring(0, subId.indexOf(separator));

                        // safe the current index, count aside
                        final int prevIndex = _index;
                        final int prevCount = _count;

                        try {
                            int invokeIndex = Integer.parseInt(clientRow);
                            // save the current scope values and set the right index
                            captureScopeValues();
                            if (invokeIndex != -1) {
                                // calculate count for RepeatStatus
                                _count = calculateCountForIndex(invokeIndex);
                            }
                            setIndex(invokeIndex);

                            if (!isIndexAvailable()) {
                                return false;
                            }

                            for (Iterator<UIComponent> it1 = getChildren().iterator();
                                 !returnValue && it1.hasNext(); ) {
                                //recursive call to find the component
                                returnValue = it1.next().invokeOnComponent(context, clientId, callback);
                            }
                        }
                        finally {
                            // restore the previous count, index and scope values
                            _count = prevCount;
                            setIndex(prevIndex);
                            restoreScopeValues();
                        }
                    }
                    else {
                        // Searching for this component's children
                        if (getChildCount() > 0) {
                            // Searching for this component's children/facets
                            for (Iterator<UIComponent> it = getChildren().iterator(); !returnValue && it.hasNext(); ) {
                                returnValue = it.next().invokeOnComponent(context, clientId, callback);
                            }
                        }
                    }
                }
            }
            finally {
                //all components must call popComponentFromEl after visiting is finished
                popComponentFromEL(context);
                if (!isCachedFacesContext) {
                    setTemporalFacesContext(null);
                }
            }

            return returnValue;
        }
    }

    @Override
    protected FacesContext getFacesContext() {
        if (_facesContext == null) {
            return super.getFacesContext();
        }
        else {
            return _facesContext;
        }
    }

    private boolean isTemporalFacesContext() {
        return _facesContext != null;
    }

    private void setTemporalFacesContext(FacesContext facesContext) {
        _facesContext = facesContext;
    }

    @Override
    public boolean visitTree(VisitContext context, VisitCallback callback) {
        if (!isRepeating()) {
            return super.visitTree(context, callback);
        }
        else {
            // override the behavior from UIComponent to visit
            // all children once per "row"

            if (ComponentUtils.isSkipIteration(context, getFacesContext())) {
                return super.visitTree(context, callback);
            }

            if (!isVisitable(context)) {
                return false;
            }

            // save the current index, count aside
            final int prevIndex = _index;
            final int prevCount = _count;

            // validate attributes
            validateAttributes();

            // reset index and save scope values
            captureScopeValues();
            setIndex(-1);

            // push the Component to EL
            pushComponentToEL(context.getFacesContext(), this);
            try {
                VisitResult res = context.invokeVisitCallback(this, callback);
                switch (res) {
                    // we are done, nothing has to be processed anymore
                    case COMPLETE:
                        return true;

                    case REJECT:
                        return false;

                    //accept
                    default:
                        // determine if we need to visit our children
                        // Note that we need to do this check because we are a NamingContainer
                        Collection<String> subtreeIdsToVisit = context
                                .getSubtreeIdsToVisit(this);
                        boolean doVisitChildren = subtreeIdsToVisit != null
                                && !subtreeIdsToVisit.isEmpty();
                        if (doVisitChildren) {
                            // visit the facets of the component
                            if (getFacetCount() > 0) {
                                for (UIComponent facet : getFacets().values()) {
                                    if (facet.visitTree(context, callback)) {
                                        return true;
                                    }
                                }
                            }

                            // visit the children once per "row"
                            if (getChildCount() > 0) {
                                int i = getOffset();
                                int end = getSize();
                                int step = getStep();
                                end = (end >= 0) ? i + end : Integer.MAX_VALUE - 1;
                                _count = 0;

                                setIndex(i);
                                while (i <= end && isIndexAvailable()) {
                                    for (int j = 0, childCount = getChildCount(); j < childCount; j++) {
                                        UIComponent child = getChildren().get(j);
                                        if (child.visitTree(context, callback)) {
                                            return true;
                                        }
                                    }

                                    _count++;
                                    i += step;

                                    setIndex(i);
                                }
                            }
                        }
                        return false;
                }
            }
            finally {
                // pop the component from EL
                popComponentFromEL(context.getFacesContext());

                // restore the previous count, index and scope values
                _count = prevCount;
                setIndex(prevIndex);
                restoreScopeValues();
            }
        }
    }

    @Override
    public void processDecodes(FacesContext context) {
        if (!isRendered()) {
            return;
        }

        pushComponentToEL(context, null);

        if (!shouldSkipChildren(context)) {
            if (isRepeating()) {
                process(context, PhaseId.APPLY_REQUEST_VALUES);
            }
            else {
                if (isDynamic()) {
                    for (int i = 0; i < getChildCount(); i++) {
                        UIComponent child = getChildren().get(i);
                        if (child instanceof Tab) {
                            Tab tab = (Tab) child;
                            if (tab.isLoaded()) {
                                tab.processDecodes(context);
                            }
                        }
                    }
                }
                else {
                    ComponentUtils.processDecodesOfFacetsAndChilds(this, context);
                }
            }
        }

        try {
            decode(context);
        }
        catch (RuntimeException e) {
            context.renderResponse();
            throw e;
        }
        finally {
            popComponentFromEL(context);
        }
    }

    @Override
    public void processValidators(FacesContext context) {
        if (!isRendered()) {
            return;
        }

        if (shouldSkipChildren(context)) {
            return;
        }

        pushComponentToEL(context, null);

        Application app = context.getApplication();
        app.publishEvent(context, PreValidateEvent.class, this);

        if (isRepeating()) {
            process(context, PhaseId.PROCESS_VALIDATIONS);
        }
        else {
            if (isDynamic()) {
                for (int i = 0; i < getChildCount(); i++) {
                    UIComponent child = getChildren().get(i);
                    if (child instanceof Tab) {
                        Tab tab = (Tab) child;
                        if (tab.isLoaded()) {
                            tab.processValidators(context);
                        }
                    }
                }
            }
            else {
                ComponentUtils.processValidatorsOfFacetsAndChilds(this, context);
            }
        }

        // check if an validation error forces the render response for our data
        if (context.getRenderResponse()) {
            _isValidChilds = false;
        }

        app.publishEvent(context, PostValidateEvent.class, this);
        popComponentFromEL(context);
    }

    @Override
    public void processUpdates(FacesContext context) {
        if (!isRendered()) {
            return;
        }

        if (shouldSkipChildren(context)) {
            return;
        }

        pushComponentToEL(context, null);

        if (isRepeating()) {
            process(context, PhaseId.UPDATE_MODEL_VALUES);
        }
        else {
            if (isDynamic()) {
                for (int i = 0; i < getChildCount(); i++) {
                    UIComponent child = getChildren().get(i);
                    if (child instanceof Tab) {
                        Tab tab = (Tab) child;
                        if (tab.isLoaded()) {
                            tab.processUpdates(context);
                        }
                    }
                }
            }
            else {
                ComponentUtils.processUpdatesOfFacetsAndChilds(this, context);
            }
        }

        if (context.getRenderResponse()) {
            _isValidChilds = false;
        }

        popComponentFromEL(context);
    }

    @Override
    public void broadcast(FacesEvent event) throws AbortProcessingException {
        if (!isRepeating()) {
            super.broadcast(event);
        }
        else {
            if (event instanceof IndexedEvent) {
                IndexedEvent idxEvent = (IndexedEvent) event;

                // safe the current index, count aside
                final int prevIndex = _index;
                final int prevCount = _count;

                try {
                    captureScopeValues();
                    if (idxEvent.getIndex() != -1) {
                        // calculate count for RepeatStatus
                        _count = calculateCountForIndex(idxEvent.getIndex());
                    }
                    setIndex(idxEvent.getIndex());
                    if (isIndexAvailable()) {
                        // get the target FacesEvent
                        FacesEvent target = idxEvent.getTarget();
                        FacesContext facesContext = getFacesContext();

                        // get the component associated with the target event and push
                        // it and its composite component parent, if available, to the
                        // component stack to have them available while processing the
                        // event (see also UIViewRoot._broadcastAll()).
                        UIComponent targetComponent = target.getComponent();
                        UIComponent compositeParent = UIComponent
                                .getCompositeComponentParent(targetComponent);
                        if (compositeParent != null) {
                            pushComponentToEL(facesContext, compositeParent);
                        }
                        pushComponentToEL(facesContext, targetComponent);

                        try {
                            // actual event broadcasting
                            targetComponent.broadcast(target);
                        }
                        finally {
                            // remove the components from the stack again
                            popComponentFromEL(facesContext);
                            if (compositeParent != null) {
                                popComponentFromEL(facesContext);
                            }
                        }
                    }
                }
                finally {
                    // restore the previous count, index and scope values
                    _count = prevCount;
                    setIndex(prevIndex);
                    restoreScopeValues();
                }
            }
            else {
                super.broadcast(event);
            }
        }
    }

    @Override
    public void queueEvent(FacesEvent event) {
        if (!isRepeating()) {
            super.queueEvent(event);
        }
        else {
            super.queueEvent(new IndexedEvent(this, event, _index));
        }
    }

    // -=Leonardo Uribe=- At the moment I haven't found any use case that
    // require to store the rowStates in the component state, mostly
    // because EditableValueHolder instances render the value into the
    // client and then this value are taken back at the beginning of the
    // next request. So, I just let this code in comments just in case
    // somebody founds an issue with this.
    /*
     @SuppressWarnings("unchecked")
     @Override
     public void restoreState(FacesContext facesContext, Object state)
     {
     if (state == null)
     {
     return;
     }

     Object[] values = (Object[])state;
     super.restoreState(facesContext,values[0]);
     if (values[1] == null)
     {
     _rowStates.clear();
     }
     else
     {
     _rowStates = (Map<String, Collection<Object[]>>) restoreAttachedState(facesContext, values[1]);
     }
     }

     @Override
     public Object saveState(FacesContext facesContext)
     {
     if (initialStateMarked())
     {
     Object parentSaved = super.saveState(facesContext);
     if (parentSaved == null && _rowStates.isEmpty())
     {
     //No values
     return null;
     }
     return new Object[]{parentSaved, saveAttachedState(facesContext, _rowStates)};
     }
     else
     {
     Object[] values = new Object[2];
     values[0] = super.saveState(facesContext);
     values[1] = saveAttachedState(facesContext, _rowStates);
     return values;
     }
     }
     */
    @Override
    public void encodeBegin(FacesContext context) throws IOException {
        _initialDescendantComponentState = null;
        if (_isValidChilds && !hasErrorMessages(context)) {
            // Clear the data model so that when rendering code calls
            // getDataModel a fresh model is fetched from the backing
            // bean via the value-binding.
            _dataModelMap.clear();

            // When the data model is cleared it is also necessary to
            // clear the saved row state, as there is an implicit 1:1
            // relation between objects in the _rowStates and the
            // corresponding DataModel element.
            _rowStates.clear();
        }
        super.encodeBegin(context);
    }

    private boolean hasErrorMessages(FacesContext context) {
        for (Iterator<FacesMessage> iter = context.getMessages(); iter.hasNext(); ) {
            FacesMessage message = iter.next();
            if (FacesMessage.SEVERITY_ERROR.compareTo(message.getSeverity()) <= 0) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void encodeChildren(FacesContext faces) throws IOException {
        if (!isRendered()) {
            return;
        }

        process(faces, PhaseId.RENDER_RESPONSE);
    }

    @Override
    public boolean getRendersChildren() {
        if (getRendererType() != null) {
            Renderer renderer = getRenderer(getFacesContext());
            if (renderer != null) {
                return renderer.getRendersChildren();
            }
        }

        return true;
    }

    public boolean isRepeating() {
        return (getVar() != null);
    }

    public void resetLoadedTabsState() {
        if (!isRepeating() && isDynamic()) {
            for (int i = 0; i < getChildCount(); i++) {
                UIComponent child = getChildren().get(i);
                if (child instanceof Tab) {
                    Tab tab = (Tab) child;
                    tab.setLoaded(false);
                }
            }
        }
    }

    protected boolean shouldSkipChildren(FacesContext context) {
        if (!ComponentUtils.isRequestSource(this, context)) {
            return false;
        }

        Map<String, String> params = context.getExternalContext().getRequestParameterMap();
        String paramValue = params.get(Constants.RequestParams.SKIP_CHILDREN_PARAM);

        if (LangUtils.isValueBlank(paramValue)) {
            return true;
        }

        return Boolean.parseBoolean(paramValue);
    }

    private final class IndexedEvent extends FacesEvent {

        private static final long serialVersionUID = 1L;
        private final FacesEvent _target;
        private final int _index;

        public IndexedEvent(UITabPanel owner, FacesEvent target, int index) {
            super(owner);
            _target = target;
            _index = index;
        }

        @Override
        public PhaseId getPhaseId() {
            return _target.getPhaseId();
        }

        @Override
        public void setPhaseId(PhaseId phaseId) {
            _target.setPhaseId(phaseId);
        }

        @Override
        public boolean isAppropriateListener(FacesListener listener) {
            return _target.isAppropriateListener(listener);
        }

        @Override
        public void processListener(FacesListener listener) {
            UITabPanel owner = (UITabPanel) getComponent();

            // safe the current index, count aside
            final int prevIndex = owner._index;
            final int prevCount = owner._count;

            try {
                owner.captureScopeValues();
                if (_index != -1) {
                    // calculate count for RepeatStatus
                    _count = calculateCountForIndex(_index);
                }
                owner.setIndex(_index);
                if (owner.isIndexAvailable()) {
                    _target.processListener(listener);
                }
            }
            finally {
                // restore the previous count, index and scope values
                owner._count = prevCount;
                owner.setIndex(prevIndex);
                owner.restoreScopeValues();
            }
        }

        public int getIndex() {
            return _index;
        }

        public FacesEvent getTarget() {
            return _target;
        }

    }
}
