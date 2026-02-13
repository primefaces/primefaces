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

import org.primefaces.cdk.api.FacesComponentBase;
import org.primefaces.cdk.api.Property;
import org.primefaces.component.column.Column;
import org.primefaces.component.columngroup.ColumnGroup;
import org.primefaces.component.columns.Columns;
import org.primefaces.component.tree.UITreeNode;
import org.primefaces.model.CheckboxTreeNode;
import org.primefaces.model.LazyTreeNode;
import org.primefaces.model.TreeNode;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.LangUtils;
import org.primefaces.util.Lazy;
import org.primefaces.util.MessageFactory;
import org.primefaces.util.SharedStringBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import jakarta.el.ValueExpression;
import jakarta.faces.FacesException;
import jakarta.faces.application.Application;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.component.ContextCallback;
import jakarta.faces.component.EditableValueHolder;
import jakarta.faces.component.NamingContainer;
import jakarta.faces.component.UIColumn;
import jakarta.faces.component.UIComponent;
import jakarta.faces.component.UIComponentBase;
import jakarta.faces.component.UINamingContainer;
import jakarta.faces.component.visit.VisitCallback;
import jakarta.faces.component.visit.VisitContext;
import jakarta.faces.component.visit.VisitResult;
import jakarta.faces.context.FacesContext;
import jakarta.faces.event.AbortProcessingException;
import jakarta.faces.event.FacesEvent;
import jakarta.faces.event.PhaseId;
import jakarta.faces.event.PostValidateEvent;
import jakarta.faces.event.PreValidateEvent;

@FacesComponentBase
public abstract class UITree extends UIComponentBase implements NamingContainer {

    public static final String SEPARATOR = "_";
    public static final String REQUIRED_MESSAGE_ID = "primefaces.tree.REQUIRED";
    public static final String CHECKBOX_CLASS = "ui-selection";
    public static final String ROOT_ROW_KEY = "root";
    private static final String SB_GET_CONTAINER_CLIENT_ID = UITree.class.getName() + "#getContainerClientId";
    private static final String SB_GET_SELECTED_ROW_KEYS_AS_STRING = UITree.class.getName() + "#getSelectedRowKeysAsString";

    private String rowKey;

    private TreeNode<?> rowNode;

    private boolean rtl;

    private List<TreeNode<?>> preselection;

    public enum PropertyKeys {
        saved,
    }

    public String getRowKey() {
        return rowKey;
    }

    public void setRowKey(String rowKey) {
        setRowKey(getValue(), rowKey);
    }

    public void setRowKey(TreeNode<?> root, String rowKey) {
        setRowKey(null, root, rowKey);
    }

    public void setRowKey(Lazy<TreeNode<?>> root, String rowKey) {
        setRowKey(root, null, rowKey);
    }

    protected void setRowKey(Lazy<TreeNode<?>> lazyRoot, TreeNode<?> root, String rowKey) {
        Map<String, Object> requestMap = getFacesContext().getExternalContext().getRequestMap();
        saveDescendantState();
        String nodeVar = getNodeVar();

        this.rowKey = rowKey;

        if (rowKey == null) {
            requestMap.remove(getVar());
            rowNode = null;
            if (nodeVar != null) {
                requestMap.remove(nodeVar);
            }
        }
        else {
            rowNode = findTreeNode(lazyRoot == null ? root : lazyRoot.get(), rowKey);

            if (rowNode != null) {
                requestMap.put(getVar(), rowNode.getData());

                if (nodeVar != null) {
                    requestMap.put(nodeVar, rowNode);
                }
            }
            else {
                requestMap.remove(getVar());

                if (nodeVar != null) {
                    requestMap.remove(nodeVar);
                }
            }
        }

        restoreDescendantState();
    }

    private void addToPreselection(TreeNode<?> node) {
        if (preselection == null) {
            preselection = new ArrayList<>();
        }

        preselection.add(node);
    }

    public TreeNode<?> getRowNode() {
        return rowNode;
    }

    public abstract Object getLocalSelectedNodes();

    @Property(description = "Name of the request-scoped variable that'll be used to refer each treenode data during rendering.")
    public abstract String getVar();

    @Property(description = "Name of the request-scoped variable that'll be used to refer current treenode using EL.")
    public abstract String getNodeVar();

    @Property(description = "A TreeNode instance as the backing model.", required = true)
    public abstract TreeNode<?> getValue();

    @Property(description = "Defines the selectionMode, valid values are \"single\", \"multiple\" and \"checkbox\".")
    public abstract String getSelectionMode();

    @Property(description = "TreeNode array to reference the selections.")
    public abstract Object getSelection();

    public abstract void setSelection(Object selection);

    @Property(description = "Validation constraint for selection.")
    public abstract boolean isRequired();

    @Property(description = "Message for required selection validation.")
    public abstract String getRequiredMessage();

    @Property(defaultValue = "false",
        description = "Ignores processing of children during lifecycle, improves performance if table only has output components.")
    public abstract boolean isSkipChildren();

    @Property(defaultValue = "false",
        description = "Defines if in checkbox selection mode, a readonly checkbox should be displayed for an unselectable node.")
    public abstract boolean isShowUnselectableCheckbox();

    @Property(defaultValue = "true", description = "Defines downwards selection propagation for checkbox mode.")
    public abstract boolean isPropagateSelectionDown();

    @Property(defaultValue = "true", description = "Defines upwards selection propagation for checkbox mode.")
    public abstract boolean isPropagateSelectionUp();

    protected TreeNode<?> findTreeNode(TreeNode<?> searchRoot, String rowKey) {
        if (rowKey == null || searchRoot == null) {
            return null;
        }

        if (rowKey.equals(ROOT_ROW_KEY)) {
            return getValue();
        }

        String[] paths = rowKey.split(SEPARATOR);

        if (paths.length == 0) {
            return null;
        }

        int childIndex = Integer.parseInt(paths[0]);
        if (childIndex >= searchRoot.getChildCount()) {
            return null;
        }

        searchRoot = searchRoot.getChildren().get(childIndex);

        if (paths.length == 1) {
            return searchRoot;
        }
        else {
            String relativeRowKey = rowKey.substring(rowKey.indexOf(SEPARATOR) + 1);

            return findTreeNode(searchRoot, relativeRowKey);
        }
    }

    public void buildRowKeys(TreeNode<?> node) {
        if (node instanceof LazyTreeNode && !((LazyTreeNode) node).isLoaded()) {
            return;
        }

        int childCount = node.getChildCount();
        if (childCount > 0) {
            for (int i = 0; i < childCount; i++) {
                TreeNode<?> childNode = node.getChildren().get(i);
                if (childNode.isSelected()) {
                    addToPreselection(childNode);
                }

                String childRowKey = (node.getParent() == null) ? String.valueOf(i) : node.getRowKey() + "_" + i;
                childNode.setRowKey(childRowKey);
                buildRowKeys(childNode);
            }
        }
    }

    public void populateRowKeys(TreeNode<?> node, List<String> keys) {
        if (node == null) {
            return;
        }

        if (node instanceof LazyTreeNode && !((LazyTreeNode) node).isLoaded()) {
            return;
        }

        int childCount = node.getChildCount();
        if (childCount > 0) {
            for (int i = 0; i < childCount; i++) {
                TreeNode<?> childNode = node.getChildren().get(i);
                keys.add(childNode.getRowKey());
                populateRowKeys(childNode, keys);
            }
        }
    }

    public void updateRowKeys(TreeNode<?> node) {
        if (node instanceof LazyTreeNode && !((LazyTreeNode) node).isLoaded()) {
            return;
        }

        int childCount = node.getChildCount();
        if (childCount > 0) {
            for (int i = 0; i < childCount; i++) {
                TreeNode<?> childNode = node.getChildren().get(i);

                String childRowKey = (node.getParent() == null) ? String.valueOf(i) : node.getRowKey() + "_" + i;
                childNode.setRowKey(childRowKey);
                updateRowKeys(childNode); // should we comment this to enable lazy loading?
            }
        }
    }

    public void initPreselection() {
        ValueExpression ve = getValueExpression("selection");
        if (ve != null) {
            if (preselection != null) {
                if (isSelectionEnabled()) {
                    if (isMultipleSelectionMode()) {
                        Class<?> selectionType = getSelectionType();
                        ve.setValue(getFacesContext().getELContext(), selectionType.isArray()
                                ? preselection.toArray(new TreeNode[0])
                                : preselection);
                    }
                    else {
                        if (!preselection.isEmpty()) {
                            ve.setValue(getFacesContext().getELContext(), preselection.get(0));
                        }
                    }

                    preselection = null;
                }
            }
            else {
                ve.setValue(getFacesContext().getELContext(), null);
            }
        }
    }

    private void updateSelectedNodes(TreeNode<?> node) {
        int childCount = node.getChildCount();
        if (childCount > 0) {
            for (int i = 0; i < childCount; i++) {
                TreeNode<?> childNode = node.getChildren().get(i);
                if (childNode.isSelected()) {
                    addToPreselection(childNode);
                }

                updateSelectedNodes(childNode);
            }
        }
    }

    public void refreshSelectedNodeKeys() {
        TreeNode<?> root = getValue();
        preselection = null;
        updateSelectedNodes(root);
        initPreselection();
    }

    public String getSelectedRowKeysAsString() {
        String value = null;
        Object selection = getSelection();

        if (selection != null) {
            if (isMultipleSelectionMode()) {
                Class<?> selectionType = getSelectionType();
                if (!selectionType.isArray() && !List.class.isAssignableFrom(selection.getClass())) {
                    throw new FacesException("Multiple selection reference must be an Array or a List for Tree " + getClientId());
                }

                List<TreeNode<?>> nodes = selectionType.isArray()
                        ? Arrays.asList((TreeNode<?>[]) selection)
                        : (List<TreeNode<?>>) selection;
                StringBuilder builder = SharedStringBuilder.get(SB_GET_SELECTED_ROW_KEYS_AS_STRING);

                for (int i = 0; i < nodes.size(); i++) {
                    if (i > 0) {
                        builder.append(",");
                    }
                    builder.append(nodes.get(i).getRowKey());
                }

                value = builder.toString();
            }
            else {
                TreeNode node = (TreeNode) selection;
                value = node.getRowKey();
            }
        }

        return value;
    }

    @Override
    public String getContainerClientId(FacesContext context) {
        String clientId = super.getContainerClientId(context);
        String _rowKey = getRowKey();

        if (_rowKey == null) {
            return clientId;
        }
        else {
            StringBuilder builder = SharedStringBuilder.get(context, SB_GET_CONTAINER_CLIENT_ID);

            return builder.append(clientId).append(UINamingContainer.getSeparatorChar(context)).append(rowKey).toString();
        }
    }

    @Override
    public void queueEvent(FacesEvent event) {
        super.queueEvent(new WrapperEvent(this, event, getRowKey()));
    }

    @Override
    public void broadcast(FacesEvent event) throws AbortProcessingException {
        if (!(event instanceof WrapperEvent)) {
            super.broadcast(event);
            return;
        }

        TreeNode<?> root = getValue();
        FacesContext context = getFacesContext();
        WrapperEvent wrapperEvent = (WrapperEvent) event;
        String oldRowKey = getRowKey();
        setRowKey(root, wrapperEvent.getRowKey());
        FacesEvent originalEvent = wrapperEvent.getFacesEvent();
        UIComponent source = (UIComponent) originalEvent.getSource();
        UIComponent compositeParent = null;

        try {
            if (!UIComponent.isCompositeComponent(source)) {
                compositeParent = UIComponent.getCompositeComponentParent(source);
            }
            if (compositeParent != null) {
                compositeParent.pushComponentToEL(context, null);
            }
            source.pushComponentToEL(context, null);
            source.broadcast(originalEvent);
        }
        finally {
            source.popComponentFromEL(context);
            if (compositeParent != null) {
                compositeParent.popComponentFromEL(context);
            }
        }

        setRowKey(root, oldRowKey);
    }

    @Override
    public void processDecodes(FacesContext context) {
        if (!isRendered()) {
            return;
        }

        pushComponentToEL(context, this);

        preDecode(context);
        Map<String, SavedState> saved = (Map<String, SavedState>) getStateHelper().get(PropertyKeys.saved);
        if (saved == null) {
            getStateHelper().remove(PropertyKeys.saved);
        }

        processNodes(context, PhaseId.APPLY_REQUEST_VALUES, getValue());

        try {
            decode(context);
        }
        catch (RuntimeException e) {
            context.renderResponse();
            throw e;
        }

        popComponentFromEL(context);
    }

    @Override
    public void processValidators(FacesContext context) {
        if (!isRendered()) {
            return;
        }

        pushComponentToEL(context, this);
        Application application = context.getApplication();
        application.publishEvent(context, PreValidateEvent.class, this);
        preValidate(context);
        processNodes(context, PhaseId.PROCESS_VALIDATIONS, getValue());
        validateSelection(context);
        application.publishEvent(context, PostValidateEvent.class, this);
        popComponentFromEL(context);
    }

    protected void validateSelection(FacesContext context) {
        if (isSelectionEnabled() && isRequired()) {
            Class<?> selectionType = getSelectionType();
            Object localSelection = getLocalSelectedNodes();
            boolean isValueBlank = !isMultipleSelectionMode()
                    ? localSelection == null
                    : (selectionType.isArray()
                        ? ((TreeNode<?>[]) localSelection).length == 0
                        : ((List<TreeNode<?>>) localSelection).isEmpty());
            if (isValueBlank) {
                updateSelection(context);
            }

            boolean valid = true;
            Object selection = getSelection();
            if (isMultipleSelectionMode()) {
                if (!selectionType.isArray() && !List.class.isAssignableFrom(selection.getClass())) {
                    throw new FacesException("Multiple selection reference must be an Array or a List for Tree " + getClientId());
                }

                List<TreeNode<?>> nodes = selectionType.isArray()
                        ? Arrays.asList((TreeNode<?>[]) selection)
                        : (List<TreeNode<?>>) selection;
                if (nodes.isEmpty()) {
                    valid = false;
                }
            }
            else {
                if (selection == null) {
                    valid = false;
                }
            }

            if (!valid) {
                String requiredMessage = getRequiredMessage();
                FacesMessage msg;

                if (requiredMessage != null) {
                    msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, requiredMessage, requiredMessage);
                }
                else {
                    msg = MessageFactory.getFacesMessage(context, REQUIRED_MESSAGE_ID, FacesMessage.SEVERITY_ERROR, getClientId(context));
                }

                context.addMessage(getClientId(context), msg);
                context.validationFailed();
                context.renderResponse();
            }
        }
    }

    @Override
    public void processUpdates(FacesContext context) {
        if (!isRendered()) {
            return;
        }

        pushComponentToEL(context, this);

        preUpdate(context);

        processNodes(context, PhaseId.UPDATE_MODEL_VALUES, getValue());

        if (isSelectionEnabled()) {
            updateSelection(context);
        }

        popComponentFromEL(context);
    }

    public void updateSelection(FacesContext context) {
        boolean propagateSelectionDown = isPropagateSelectionDown();
        boolean propagateSelectionUp = isPropagateSelectionUp();

        ValueExpression selectionVE = getValueExpression("selection");
        if (selectionVE != null) {
            Class<?> selectionType = getSelectionType();
            Object selection = getLocalSelectedNodes();
            Object previousSelection = selectionVE.getValue(context.getELContext());

            if (isMultipleSelectionMode()) {
                List<TreeNode<?>> previousSelections = selectionType.isArray()
                        ? (previousSelection == null ? null : Arrays.asList((TreeNode<?>[]) previousSelection))
                        : (List<TreeNode<?>>) previousSelection;
                List<TreeNode<?>> selections = selectionType.isArray()
                        ? (selection == null ? null : Arrays.asList((TreeNode<?>[]) selection))
                        : (List<TreeNode<?>>) selection;

                if (previousSelections != null) {
                    for (TreeNode<?> node : previousSelections) {
                        if (node instanceof CheckboxTreeNode) {
                            ((CheckboxTreeNode<?>) node).setSelected(false, propagateSelectionDown, propagateSelectionUp);
                        }
                        else {
                            node.setSelected(false);
                        }
                    }
                }

                if (selections != null) {
                    for (TreeNode<?> node : selections) {
                        if (node instanceof CheckboxTreeNode) {
                            ((CheckboxTreeNode<?>) node).setSelected(true, propagateSelectionDown, propagateSelectionUp);
                        }
                        else {
                            node.setSelected(true);
                        }
                    }
                }
            }
            else {
                if (previousSelection != null) {
                    ((TreeNode<?>) previousSelection).setSelected(false);
                }
                if (selection != null) {
                    ((TreeNode<?>) selection).setSelected(true);
                }
            }

            selectionVE.setValue(context.getELContext(), selection);
            setSelection(null);
        }
    }

    protected void processNodes(FacesContext context, PhaseId phaseId, TreeNode<?> root) {
        if (isSkipChildren()) {
            return;
        }

        processFacets(context, phaseId, root);
        processColumnFacets(context, phaseId, root);

        if (root != null) {
            processNode(context, phaseId, root, root, null);
        }

        setRowKey(root, null);
    }

    protected void processNode(FacesContext context, PhaseId phaseId, TreeNode<?> root, TreeNode<?> treeNode, String rowKey) {
        processColumnChildren(context, phaseId, root, rowKey);

        //process child nodes if node is expanded or node itself is the root
        if (treeNode != null && shouldVisitNode(treeNode) && treeNode.getChildCount() > 0) {
            int childIndex = 0;
            for (Iterator<? extends TreeNode<?>> iterator = treeNode.getChildren().iterator(); iterator.hasNext(); ) {
                String childRowKey = childRowKey(rowKey, childIndex);

                processNode(context, phaseId, root, iterator.next(), childRowKey);

                childIndex++;
            }
        }
    }

    protected void processFacets(FacesContext context, PhaseId phaseId, TreeNode<?> root) {
        setRowKey(root, null);

        if (getFacetCount() > 0) {
            for (UIComponent facet : getFacets().values()) {
                if (phaseId == PhaseId.APPLY_REQUEST_VALUES) {
                    facet.processDecodes(context);
                }
                else if (phaseId == PhaseId.PROCESS_VALIDATIONS) {
                    facet.processValidators(context);
                }
                else if (phaseId == PhaseId.UPDATE_MODEL_VALUES) {
                    facet.processUpdates(context);
                }
                else {
                    throw new IllegalArgumentException();
                }
            }
        }
    }

    protected void processColumnFacets(FacesContext context, PhaseId phaseId, TreeNode<?> root) {
        setRowKey(root, null);

        for (UIComponent child : getChildren()) {
            if (child instanceof UIColumn && child.isRendered()) {
                UIColumn column = (UIColumn) child;

                if (column.getFacetCount() > 0) {
                    for (UIComponent columnFacet : column.getFacets().values()) {
                        if (phaseId == PhaseId.APPLY_REQUEST_VALUES) {
                            columnFacet.processDecodes(context);
                        }
                        else if (phaseId == PhaseId.PROCESS_VALIDATIONS) {
                            columnFacet.processValidators(context);
                        }
                        else if (phaseId == PhaseId.UPDATE_MODEL_VALUES) {
                            columnFacet.processUpdates(context);
                        }
                        else {
                            throw new IllegalArgumentException();
                        }
                    }
                }
            }
        }
    }

    protected void processColumnChildren(FacesContext context, PhaseId phaseId, TreeNode<?> root, String nodeKey) {
        setRowKey(root, nodeKey);

        if (nodeKey == null) {
            return;
        }

        for (UIComponent child : getChildren()) {
            if (child.isRendered()) {
                if (child instanceof UIColumn) {
                    for (UIComponent grandkid : child.getChildren()) {
                        if (grandkid.isRendered()) {
                            processComponent(context, grandkid, phaseId);
                        }
                    }
                }
                else {
                    processComponent(context, child, phaseId);
                }
            }

        }
    }

    protected void processComponent(FacesContext context, UIComponent component, PhaseId phaseId) {
        if (phaseId == PhaseId.APPLY_REQUEST_VALUES) {
            component.processDecodes(context);
        }
        else if (phaseId == PhaseId.PROCESS_VALIDATIONS) {
            component.processValidators(context);
        }
        else if (phaseId == PhaseId.UPDATE_MODEL_VALUES) {
            component.processUpdates(context);
        }
        else {
            throw new IllegalArgumentException();
        }
    }

    private void saveDescendantState() {
        FacesContext context = getFacesContext();

        for (UIComponent child : getChildren()) {
            saveDescendantState(child, context);
        }
    }

    private void saveDescendantState(UIComponent component, FacesContext context) {
        Map<String, SavedState> saved = (Map<String, SavedState>) getStateHelper().get(PropertyKeys.saved);

        if (component instanceof EditableValueHolder) {
            EditableValueHolder input = (EditableValueHolder) component;
            SavedState state = null;
            String clientId = component.getClientId(context);

            if (saved == null) {
                state = new SavedState();
                getStateHelper().put(PropertyKeys.saved, clientId, state);
            }

            if (state == null) {
                state = saved.get(clientId);

                if (state == null) {
                    state = new SavedState();
                    getStateHelper().put(PropertyKeys.saved, clientId, state);
                }
            }

            state.setValue(input.getLocalValue());
            state.setValid(input.isValid());
            state.setSubmittedValue(input.getSubmittedValue());
            state.setLocalValueSet(input.isLocalValueSet());
        }

        for (UIComponent uiComponent : component.getChildren()) {
            saveDescendantState(uiComponent, context);
        }

        if (component.getFacetCount() > 0) {
            for (UIComponent facet : component.getFacets().values()) {
                saveDescendantState(facet, context);
            }
        }
    }

    private void restoreDescendantState() {
        FacesContext context = getFacesContext();

        for (UIComponent child : getChildren()) {
            restoreDescendantState(child, context);
        }
    }

    private void restoreDescendantState(UIComponent component, FacesContext context) {
        //force id reset
        String id = component.getId();
        component.setId(id);

        Map<String, SavedState> saved = (Map<String, SavedState>) getStateHelper().get(PropertyKeys.saved);

        if (component instanceof EditableValueHolder) {
            EditableValueHolder input = (EditableValueHolder) component;
            String clientId = component.getClientId(context);

            SavedState state = saved.get(clientId);
            if (state == null) {
                state = new SavedState();
            }

            input.setValue(state.getValue());
            input.setValid(state.isValid());
            input.setSubmittedValue(state.getSubmittedValue());
            input.setLocalValueSet(state.isLocalValueSet());
        }
        for (UIComponent kid : component.getChildren()) {
            restoreDescendantState(kid, context);
        }

        if (component.getFacetCount() > 0) {
            for (UIComponent facet : component.getFacets().values()) {
                restoreDescendantState(facet, context);
            }
        }
    }

    @Override
    public boolean invokeOnComponent(FacesContext context, String clientId, ContextCallback callback)
            throws FacesException {

        // skip if the component is not a children of the UITree
        if (!clientId.startsWith(getClientId(context))) {
            return false;
        }

        return super.invokeOnComponent(context, clientId, callback);
    }

    @Override
    public boolean visitTree(VisitContext context, VisitCallback callback) {
        if (!isVisitable(context)) {
            return false;
        }

        FacesContext facesContext = context.getFacesContext();
        boolean visitNodes = !ComponentUtils.isSkipIteration(context, facesContext);
        Lazy<TreeNode<?>> root = new Lazy<>(() -> getValue());

        String oldRowKey = getRowKey();
        if (visitNodes) {
            setRowKey(root, null);
        }

        pushComponentToEL(facesContext, null);

        try {
            VisitResult result = context.invokeVisitCallback(this, callback);

            if (result == VisitResult.COMPLETE) {
                return true;
            }

            if ((result == VisitResult.ACCEPT) && doVisitChildren(context)) {
                if (visitFacets(context, root, callback, visitNodes)) {
                    return true;
                }

                if (requiresColumns() && visitColumnsAndColumnFacets(context, callback, visitNodes, root)) {
                    return true;
                }

                if (visitNodes(context, root, callback, visitNodes)) {
                    return true;
                }
            }
        }
        finally {
            popComponentFromEL(facesContext);

            if (visitNodes) {
                setRowKey(root, oldRowKey);
            }
        }

        return false;
    }

    protected boolean doVisitChildren(VisitContext context) {
        Collection<String> idsToVisit = context.getSubtreeIdsToVisit(this);

        return (!idsToVisit.isEmpty());
    }

    protected boolean visitFacets(VisitContext context, Lazy<TreeNode<?>> root, VisitCallback callback, boolean visitNodes) {
        if (visitNodes) {
            setRowKey(root, null);
        }

        if (getFacetCount() > 0) {
            for (UIComponent facet : getFacets().values()) {
                if (facet.visitTree(context, callback)) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean visitColumns(VisitContext context, Lazy<TreeNode<?>> root, VisitCallback callback, String rowKey, boolean visitNodes) {
        String treeNodeType = null;
        if (visitNodes) {
            setRowKey(root, rowKey);

            if (rowKey == null) {
                return false;
            }

            TreeNode rowNode = getRowNode();
            if (rowNode != null) {
                treeNodeType = rowNode.getType();
            }
        }

        if (getChildCount() > 0) {
            for (UIComponent child : getChildren()) {
                if (child instanceof Columns) {
                    Columns columns = (Columns) child;
                    for (int i = 0; i < columns.getRowCount(); i++) {
                        columns.setRowIndex(i);

                        boolean value = visitColumnContent(context, callback, columns);
                        if (value) {
                            columns.setRowIndex(-1);
                            return true;
                        }
                    }

                    columns.setRowIndex(-1);
                }
                else if (child instanceof UIColumn) {
                    if (child instanceof UITreeNode) {
                        UITreeNode uiTreeNode = (UITreeNode) child;
                        if (visitNodes && treeNodeType != null && !treeNodeType.equals(uiTreeNode.getType())) {
                            continue;
                        }
                    }
                    if (child.visitTree(context, callback)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    protected boolean visitColumnContent(VisitContext context, VisitCallback callback, UIComponent component) {
        if (component.getChildCount() > 0) {
            for (UIComponent grandkid : component.getChildren()) {
                if (grandkid.visitTree(context, callback)) {
                    return true;
                }
            }
        }

        return false;
    }

    protected boolean visitNodes(VisitContext context, Lazy<TreeNode<?>> root, VisitCallback callback, boolean visitRows) {
        if (visitRows) {
            if (root != null) {
                if (visitNode(context, root, callback, root.get(), null)) {
                    return true;
                }
            }

            setRowKey(root, null);
        }
        else {
            // visit without iterating over the tree model
            if (visitColumns(context, root, callback, null, false)) {
                return true;
            }
        }

        return false;
    }

    protected boolean visitNode(VisitContext context, Lazy<TreeNode<?>> root, VisitCallback callback, TreeNode<?> treeNode, String rowKey) {
        if (visitColumns(context, root, callback, rowKey, true)) {
            return true;
        }

        //visit child nodes if node is expanded or node itself is the root
        if (treeNode != null && shouldVisitNode(treeNode) && treeNode.getChildCount() > 0) {
            int childIndex = 0;
            for (Iterator<? extends TreeNode<?>> iterator = treeNode.getChildren().iterator(); iterator.hasNext(); ) {
                String childRowKey = childRowKey(rowKey, childIndex);

                if (visitNode(context, root, callback, iterator.next(), childRowKey)) {
                    return true;
                }

                childIndex++;
            }
        }

        return false;
    }

    protected boolean requiresColumns() {
        return false;
    }

    protected boolean visitColumnsAndColumnFacets(VisitContext context, VisitCallback callback, boolean visitRows, Lazy<TreeNode<?>> root) {
        if (visitRows) {
            setRowKey(root, null);
        }

        if (getChildCount() > 0) {
            for (UIComponent child : getChildren()) {
                VisitResult result = context.invokeVisitCallback(child, callback); // visit the column directly
                if (result == VisitResult.COMPLETE) {
                    return true;
                }

                if (child instanceof org.primefaces.component.api.UIColumn) {
                    if (child.getFacetCount() > 0) {
                        if (child instanceof Columns) {
                            Columns columns = (Columns) child;
                            for (int i = 0; i < columns.getRowCount(); i++) {
                                columns.setRowIndex(i);
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

    protected boolean visitColumnFacets(VisitContext context, VisitCallback callback, UIComponent component) {
        for (UIComponent columnFacet : component.getFacets().values()) {
            if (columnFacet.visitTree(context, callback)) {
                return true;
            }
        }

        return false;
    }

    protected boolean visitColumnGroup(VisitContext context, VisitCallback callback, ColumnGroup group) {
        if (group.getChildCount() > 0) {
            for (UIComponent row : group.getChildren()) {
                if (row.getChildCount() > 0) {
                    for (UIComponent col : row.getChildren()) {
                        if (col instanceof Column && col.getFacetCount() > 0) {
                            boolean value = visitColumnFacets(context, callback, col);
                            if (value) {
                                return true;
                            }
                        }
                    }
                }
            }
        }

        return false;
    }

    public boolean isRTLRendering() {
        return rtl;
    }

    public void setRTLRendering(boolean rtl) {
        this.rtl = rtl;
    }

    protected boolean shouldVisitNode(TreeNode<?> node) {
        return (node.isExpanded() || node.getParent() == null);
    }

    @Override
    public void encodeBegin(FacesContext context) throws IOException {

        preEncode(context);

        super.encodeBegin(context);
    }

    protected void preDecode(FacesContext context) {
    }

    protected void preValidate(FacesContext context) {
    }

    protected void preUpdate(FacesContext context) {
    }

    protected void preEncode(FacesContext context) {
    }

    public boolean isSelectionEnabled() {
        String selectionMode = getSelectionMode();
        return LangUtils.isNotBlank(selectionMode);
    }

    public boolean isMultipleSelectionMode() {
        String selectionMode = getSelectionMode();
        return !"single".equalsIgnoreCase(selectionMode);
    }

    public Class<?> getSelectionType() {
        ValueExpression selectionVE = getValueExpression("selection");
        return selectionVE == null ? null : selectionVE.getType(getFacesContext().getELContext());
    }

    public boolean isCheckboxSelectionMode() {
        String selectionMode = getSelectionMode();
        return "checkbox".equals(selectionMode);
    }

    protected String childRowKey(String parentRowKey, int childIndex) {
        return parentRowKey == null ? String.valueOf(childIndex) : parentRowKey + SEPARATOR + childIndex;
    }
}
