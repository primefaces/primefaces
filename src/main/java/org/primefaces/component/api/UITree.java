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
import java.util.*;

import javax.el.ValueExpression;
import javax.faces.FacesException;
import javax.faces.application.Application;
import javax.faces.application.FacesMessage;
import javax.faces.component.*;
import javax.faces.component.UIColumn;
import javax.faces.component.visit.VisitCallback;
import javax.faces.component.visit.VisitContext;
import javax.faces.component.visit.VisitHint;
import javax.faces.component.visit.VisitResult;
import javax.faces.context.FacesContext;
import javax.faces.event.*;

import org.primefaces.component.columns.Columns;
import org.primefaces.component.tree.UITreeNode;
import org.primefaces.model.CheckboxTreeNode;
import org.primefaces.model.TreeNode;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.MessageFactory;
import org.primefaces.util.SharedStringBuilder;

public abstract class UITree extends UIComponentBase implements NamingContainer {

    public static final String SEPARATOR = "_";
    public static final String REQUIRED_MESSAGE_ID = "primefaces.tree.REQUIRED";
    public static final String CHECKBOX_CLASS = "ui-selection";
    public static final String ROOT_ROW_KEY = "root";
    private static final String SB_GET_CONTAINER_CLIENT_ID = UITree.class.getName() + "#getContainerClientId";
    private static final String SB_GET_SELECTED_ROW_KEYS_AS_STRING = UITree.class.getName() + "#getSelectedRowKeysAsString";
    private String rowKey;

    private TreeNode rowNode;

    private boolean rtl;

    private List<TreeNode> preselection;

    private Boolean isNested = null;

    public enum PropertyKeys {
        var,
        selectionMode,
        selection,
        saved,
        value,
        required,
        requiredMessage,
        skipChildren,
        showUnselectableCheckbox,
        nodeVar,
        propagateSelectionDown,
        propagateSelectionUp;
    }

    public String getRowKey() {
        return rowKey;
    }

    public void setRowKey(String rowKey) {
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
            TreeNode root = getValue();
            rowNode = findTreeNode(root, rowKey);

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

    private void addToPreselection(TreeNode node) {
        if (preselection == null) {
            preselection = new ArrayList<>();
        }

        preselection.add(node);
    }

    public TreeNode getRowNode() {
        return rowNode;
    }

    public java.lang.String getVar() {
        return (String) getStateHelper().eval(PropertyKeys.var, null);
    }

    public void setVar(java.lang.String _var) {
        getStateHelper().put(PropertyKeys.var, _var);
    }

    public java.lang.String getNodeVar() {
        return (String) getStateHelper().eval(PropertyKeys.nodeVar, null);
    }

    public void setNodeVar(java.lang.String _nodeVar) {
        getStateHelper().put(PropertyKeys.nodeVar, _nodeVar);
    }

    public TreeNode getValue() {
        return (TreeNode) getStateHelper().eval(PropertyKeys.value, null);
    }

    public void setValue(TreeNode _value) {
        getStateHelper().put(PropertyKeys.value, _value);
    }

    public java.lang.String getSelectionMode() {
        return (String) getStateHelper().eval(PropertyKeys.selectionMode, null);
    }

    public void setSelectionMode(java.lang.String _selectionMode) {
        getStateHelper().put(PropertyKeys.selectionMode, _selectionMode);
    }

    public java.lang.Object getSelection() {
        return getStateHelper().eval(PropertyKeys.selection, null);
    }

    public void setSelection(java.lang.Object _selection) {
        getStateHelper().put(PropertyKeys.selection, _selection);
    }

    public boolean isRequired() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.required, false);
    }

    public void setRequired(boolean _required) {
        getStateHelper().put(PropertyKeys.required, _required);
    }

    public java.lang.String getRequiredMessage() {
        return (java.lang.String) getStateHelper().eval(PropertyKeys.requiredMessage, null);
    }

    public void setRequiredMessage(java.lang.String _requiredMessage) {
        getStateHelper().put(PropertyKeys.requiredMessage, _requiredMessage);
    }

    public boolean isSkipChildren() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.skipChildren, false);
    }

    public void setSkipChildren(boolean _skipChildren) {
        getStateHelper().put(PropertyKeys.skipChildren, _skipChildren);
    }

    public boolean isShowUnselectableCheckbox() {
        return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.showUnselectableCheckbox, false);
    }

    public void setShowUnselectableCheckbox(boolean _showUnselectableCheckbox) {
        getStateHelper().put(PropertyKeys.showUnselectableCheckbox, _showUnselectableCheckbox);
    }

    public Object getLocalSelectedNodes() {
        return getStateHelper().get(PropertyKeys.selection);
    }

    public boolean isPropagateSelectionDown() {
        return (Boolean) getStateHelper().eval(PropertyKeys.propagateSelectionDown, true);
    }

    public void setPropagateSelectionDown(boolean _propagateSelectionDown) {
        getStateHelper().put(PropertyKeys.propagateSelectionDown, _propagateSelectionDown);
    }

    public boolean isPropagateSelectionUp() {
        return (Boolean) getStateHelper().eval(PropertyKeys.propagateSelectionUp, true);
    }

    public void setPropagateSelectionUp(boolean _propagateSelectionUp) {
        getStateHelper().put(PropertyKeys.propagateSelectionUp, _propagateSelectionUp);
    }

    protected TreeNode findTreeNode(TreeNode searchRoot, String rowKey) {
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
        if (childIndex >= searchRoot.getChildren().size()) {
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

    public void buildRowKeys(TreeNode node) {
        int childCount = node.getChildCount();
        if (childCount > 0) {
            for (int i = 0; i < childCount; i++) {
                TreeNode childNode = node.getChildren().get(i);
                if (childNode.isSelected()) {
                    addToPreselection(childNode);
                }

                String childRowKey = (node.getParent() == null) ? String.valueOf(i) : node.getRowKey() + "_" + i;
                childNode.setRowKey(childRowKey);
                buildRowKeys(childNode);
            }
        }
    }

    public void populateRowKeys(TreeNode node, List<String> keys) {
        if (node != null) {
            int childCount = node.getChildCount();
            if (childCount > 0) {
                for (int i = 0; i < childCount; i++) {
                    TreeNode childNode = node.getChildren().get(i);
                    keys.add(childNode.getRowKey());
                    populateRowKeys(childNode, keys);
                }
            }
        }
    }

    public void updateRowKeys(TreeNode node) {
        int childCount = node.getChildCount();
        if (childCount > 0) {
            for (int i = 0; i < childCount; i++) {
                TreeNode childNode = node.getChildren().get(i);

                String childRowKey = (node.getParent() == null) ? String.valueOf(i) : node.getRowKey() + "_" + i;
                childNode.setRowKey(childRowKey);
                updateRowKeys(childNode);
            }
        }
    }

    public void initPreselection() {
        ValueExpression ve = getValueExpression(UITree.PropertyKeys.selection.toString());
        if (ve != null) {
            if (preselection != null) {
                String selectionMode = getSelectionMode();
                if (selectionMode != null) {
                    if (selectionMode.equals("single")) {
                        if (!preselection.isEmpty()) {
                            ve.setValue(FacesContext.getCurrentInstance().getELContext(), preselection.get(0));
                        }
                    }
                    else {
                        ve.setValue(FacesContext.getCurrentInstance().getELContext(), preselection.toArray(new TreeNode[0]));
                    }

                    preselection = null;
                }
            }
            else {
                ve.setValue(FacesContext.getCurrentInstance().getELContext(), null);
            }
        }
    }

    private void updateSelectedNodes(TreeNode node) {
        int childCount = node.getChildCount();
        if (childCount > 0) {
            for (int i = 0; i < childCount; i++) {
                TreeNode childNode = node.getChildren().get(i);
                if (childNode.isSelected()) {
                    addToPreselection(childNode);
                }

                updateSelectedNodes(childNode);
            }
        }
    }

    public void refreshSelectedNodeKeys() {
        TreeNode root = getValue();
        preselection = null;
        updateSelectedNodes(root);
        initPreselection();
    }

    public String getSelectedRowKeysAsString() {
        String value = null;
        Object selection = getSelection();
        String selectionMode = getSelectionMode();

        if (selection != null) {
            if (selectionMode.equals("single")) {
                TreeNode node = (TreeNode) selection;
                value = node.getRowKey();
            }
            else {
                TreeNode[] nodes = (TreeNode[]) selection;
                StringBuilder builder = SharedStringBuilder.get(SB_GET_SELECTED_ROW_KEYS_AS_STRING);

                for (int i = 0; i < nodes.length; i++) {
                    builder.append(nodes[i].getRowKey());
                    if (i != (nodes.length - 1)) {
                        builder.append(",");
                    }
                }

                value = builder.toString();
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

        FacesContext context = FacesContext.getCurrentInstance();
        WrapperEvent wrapperEvent = (WrapperEvent) event;
        String oldRowKey = getRowKey();
        setRowKey(wrapperEvent.getRowKey());
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

        setRowKey(oldRowKey);
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

        processNodes(context, PhaseId.APPLY_REQUEST_VALUES);

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
        processNodes(context, PhaseId.PROCESS_VALIDATIONS);
        validateSelection(context);
        application.publishEvent(context, PostValidateEvent.class, this);
        popComponentFromEL(context);
    }

    protected void validateSelection(FacesContext context) {
        String selectionMode = getSelectionMode();
        if (selectionMode != null && isRequired()) {
            boolean valid = true;
            Object selection = getSelection();

            if (selectionMode.equals("single")) {
                if (selection == null) {
                    valid = false;
                }
            }
            else {
                TreeNode[] selectionArray = (TreeNode[]) selection;
                if (selectionArray.length == 0) {
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
                    msg = MessageFactory.getMessage(REQUIRED_MESSAGE_ID, FacesMessage.SEVERITY_ERROR, new Object[]{getClientId(context)});
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

        processNodes(context, PhaseId.UPDATE_MODEL_VALUES);

        updateSelection(context);

        popComponentFromEL(context);
    }

    public void updateSelection(FacesContext context) {
        String selectionMode = getSelectionMode();
        boolean propagateSelectionDown = isPropagateSelectionDown();
        boolean propagateSelectionUp = isPropagateSelectionUp();

        ValueExpression selectionVE = getValueExpression(UITree.PropertyKeys.selection.toString());

        if (selectionMode != null && selectionVE != null) {
            Object selection = getLocalSelectedNodes();
            Object previousSelection = selectionVE.getValue(context.getELContext());

            if (selectionMode.equals("single")) {
                if (previousSelection != null) {
                    ((TreeNode) previousSelection).setSelected(false);
                }
                if (selection != null) {
                    ((TreeNode) selection).setSelected(true);
                }
            }
            else {
                TreeNode[] previousSelections = (TreeNode[]) previousSelection;
                TreeNode[] selections = (TreeNode[]) selection;

                if (previousSelections != null) {
                    for (TreeNode node : previousSelections) {
                        if (node instanceof CheckboxTreeNode) {
                            ((CheckboxTreeNode) node).setSelected(false, propagateSelectionDown, propagateSelectionUp);
                        }
                        else {
                            node.setSelected(false);
                        }
                    }
                }

                if (selections != null) {
                    for (TreeNode node : selections) {
                        if (node instanceof CheckboxTreeNode) {
                            ((CheckboxTreeNode) node).setSelected(true, propagateSelectionDown, propagateSelectionUp);
                        }
                        else {
                            node.setSelected(true);
                        }
                    }
                }
            }

            selectionVE.setValue(context.getELContext(), selection);
            setSelection(null);
        }
    }

    protected void processNodes(FacesContext context, PhaseId phaseId) {
        if (isSkipChildren()) {
            return;
        }

        processFacets(context, phaseId);
        processColumnFacets(context, phaseId);

        TreeNode root = getValue();
        if (root != null) {
            processNode(context, phaseId, root, null);
        }

        setRowKey(null);
    }

    protected void processNode(FacesContext context, PhaseId phaseId, TreeNode treeNode, String rowKey) {
        processColumnChildren(context, phaseId, rowKey);

        //process child nodes if node is expanded or node itself is the root
        if (shouldVisitNode(treeNode)) {
            int childIndex = 0;
            for (Iterator<TreeNode> iterator = treeNode.getChildren().iterator(); iterator.hasNext(); ) {
                String childRowKey = rowKey == null ? String.valueOf(childIndex) : rowKey + SEPARATOR + childIndex;

                processNode(context, phaseId, iterator.next(), childRowKey);

                childIndex++;
            }
        }
    }

    protected void processFacets(FacesContext context, PhaseId phaseId) {
        setRowKey(null);

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

    protected void processColumnFacets(FacesContext context, PhaseId phaseId) {
        setRowKey(null);

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

    protected void processColumnChildren(FacesContext context, PhaseId phaseId, String nodeKey) {
        setRowKey(nodeKey);

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
        boolean visitNodes = requiresIteration(facesContext, context);

        String oldRowKey = getRowKey();
        if (visitNodes) {
            setRowKey(null);
        }

        pushComponentToEL(facesContext, null);

        try {
            VisitResult result = context.invokeVisitCallback(this, callback);

            if (result == VisitResult.COMPLETE) {
                return true;
            }

            if ((result == VisitResult.ACCEPT) && doVisitChildren(context)) {
                if (visitFacets(context, callback, visitNodes)) {
                    return true;
                }

                if (visitNodes(context, callback, visitNodes)) {
                    return true;
                }
            }
        }
        finally {
            popComponentFromEL(facesContext);

            if (visitNodes) {
                setRowKey(oldRowKey);
            }
        }

        return false;
    }

    protected boolean doVisitChildren(VisitContext context) {
        Collection<String> idsToVisit = context.getSubtreeIdsToVisit(this);

        return (!idsToVisit.isEmpty());
    }

    protected boolean visitFacets(VisitContext context, VisitCallback callback, boolean visitNodes) {
        if (visitNodes) {
            setRowKey(null);
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

    private boolean visitColumns(VisitContext context, VisitCallback callback, String rowKey) {
        setRowKey(rowKey);

        if (rowKey == null) {
            return false;
        }

        TreeNode rowNode = getRowNode();
        String treeNodeType = null;
        if (rowNode != null) {
            treeNodeType = rowNode.getType();
        }

        if (getChildCount() > 0) {
            for (UIComponent child : getChildren()) {
                if (child instanceof Columns) {
                    Columns uicolumns = (Columns) child;
                    for (int i = 0; i < uicolumns.getRowCount(); i++) {
                        uicolumns.setRowIndex(i);

                        boolean value = visitColumnContent(context, callback, uicolumns);
                        if (value) {
                            uicolumns.setRowIndex(-1);
                            return true;
                        }
                    }

                    uicolumns.setRowIndex(-1);
                }
                else if (child instanceof UIColumn) {
                    if (child instanceof UITreeNode) {
                        UITreeNode uiTreeNode = (UITreeNode) child;
                        if (treeNodeType != null && !treeNodeType.equals(uiTreeNode.getType())) {
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

    protected boolean visitNodes(VisitContext context, VisitCallback callback, boolean visitRows) {
        if (visitRows) {
            TreeNode root = getValue();
            if (root != null) {
                if (visitNode(context, callback, root, null)) {
                    return true;
                }
            }

            setRowKey(null);
        }

        return false;
    }

    protected boolean visitNode(VisitContext context, VisitCallback callback, TreeNode treeNode, String rowKey) {
        if (visitColumns(context, callback, rowKey)) {
            return true;
        }

        //visit child nodes if node is expanded or node itself is the root
        if (shouldVisitNode(treeNode)) {
            int childIndex = 0;
            for (Iterator<TreeNode> iterator = treeNode.getChildren().iterator(); iterator.hasNext(); ) {
                String childRowKey = rowKey == null ? String.valueOf(childIndex) : rowKey + SEPARATOR + childIndex;

                if (visitNode(context, callback, iterator.next(), childRowKey)) {
                    return true;
                }

                childIndex++;
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

    protected boolean shouldVisitNode(TreeNode node) {
        return (node.isExpanded() || node.getParent() == null);
    }

    protected boolean requiresIteration(FacesContext context, VisitContext visitContext) {
        try {
            //JSF 2.1
            VisitHint skipHint = VisitHint.valueOf("SKIP_ITERATION");
            return !visitContext.getHints().contains(skipHint);
        }
        catch (IllegalArgumentException e) {
            //JSF 2.0
            Object skipHint = context.getAttributes().get("javax.faces.visit.SKIP_ITERATION");
            return !Boolean.TRUE.equals(skipHint);
        }
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

    protected Boolean isNestedWithinIterator() {
        if (isNested == null) {
            isNested = ComponentUtils.isNestedWithinIterator(this);
        }
        return isNested;
    }
}
