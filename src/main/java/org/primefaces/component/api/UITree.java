/*
 * Copyright 2009-2014 PrimeTek.
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
package org.primefaces.component.api;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.el.ValueExpression;
import javax.faces.application.FacesMessage;
import javax.faces.component.EditableValueHolder;
import javax.faces.component.NamingContainer;
import javax.faces.component.UIColumn;
import javax.faces.component.UIComponent;
import javax.faces.component.UIComponentBase;
import javax.faces.component.UINamingContainer;
import javax.faces.component.visit.VisitCallback;
import javax.faces.component.visit.VisitContext;
import javax.faces.component.visit.VisitHint;
import javax.faces.component.visit.VisitResult;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.FacesEvent;
import javax.faces.event.PhaseId;
import org.primefaces.component.columns.Columns;
import org.primefaces.model.TreeNode;
import org.primefaces.util.MessageFactory;
import org.primefaces.util.SharedStringBuilder;

public abstract class UITree extends UIComponentBase implements NamingContainer {
    
    private static final String SB_GET_CONTAINER_CLIENT_ID = UITree.class.getName() + "#getContainerClientId";
    private static final String SB_GET_SELECTED_ROW_KEYS_AS_STRING = UITree.class.getName() + "#getSelectedRowKeysAsString";
    
    public final static String SEPARATOR = "_";
    
    public final static String REQUIRED_MESSAGE_ID = "primefaces.tree.REQUIRED";
    
    public final static String CHECKBOX_CLASS = "ui-selection";
    
    private String rowKey;
   
    private TreeNode rowNode;
    
    private boolean rtl;
    
    private List<TreeNode> preselection;
            
    protected enum PropertyKeys {
		var
        ,selectionMode
        ,selection
        ,saved
		,value
        ,required
        ,requiredMessage
        ,skipChildren
        ,showUnselectableCheckbox
        ,nodeVar;
            
		String toString;

		PropertyKeys(String toString) {
			this.toString = toString;
		}

		PropertyKeys() {}

        @Override
		public String toString() {
			return ((this.toString != null) ? this.toString : super.toString());
        }
	}
    
    public String getRowKey() {
        return rowKey;
    }

    public void setRowKey(String rowKey) {
        Map<String,Object> requestMap = getFacesContext().getExternalContext().getRequestMap();
        saveDescendantState();
        String nodeVar = this.getNodeVar();
        
        this.rowKey = rowKey;

        if(rowKey == null) {
            requestMap.remove(getVar());
            this.rowNode = null;
            if(nodeVar != null) {
                requestMap.remove(nodeVar);
            }
        } 
        else {
            TreeNode root = getValue();
            this.rowNode = findTreeNode(root, rowKey);
            
            if(this.rowNode != null) {
                requestMap.put(getVar(), this.rowNode.getData());
                
                if(nodeVar != null) {
                    requestMap.put(nodeVar, this.rowNode);
                }
            }
            else { 
                requestMap.remove(getVar());
                
                if(nodeVar != null) {
                    requestMap.remove(nodeVar);
                }
            }
        }

        restoreDescendantState();
    }
    
    private void addToPreselection(TreeNode node) {
        if(preselection == null) {
            preselection = new ArrayList<TreeNode>();
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
		return (java.lang.Object) getStateHelper().eval(PropertyKeys.selection, null);
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
    
    protected TreeNode findTreeNode(TreeNode searchRoot, String rowKey) {
        if(rowKey == null || searchRoot == null) {
            return null;
        }
        
        if(rowKey.equals("root")) {
            return this.getValue();
        }
        
		String[] paths = rowKey.split(SEPARATOR);
		
		if(paths.length == 0)
			return null;
		
		int childIndex = Integer.parseInt(paths[0]);
        if(childIndex >= searchRoot.getChildren().size()) 
            return null;
        
		searchRoot = searchRoot.getChildren().get(childIndex);

		if(paths.length == 1) {
			return searchRoot;
		} 
		else {
			String relativeRowKey = rowKey.substring(rowKey.indexOf(SEPARATOR) + 1);
				
			return findTreeNode(searchRoot, relativeRowKey);
		}
	}
        
    public void buildRowKeys(TreeNode node) {
        int childCount = node.getChildCount();
        if(childCount > 0) {
            for(int i = 0; i < childCount; i++) {
                TreeNode childNode = node.getChildren().get(i);
                if(childNode.isSelected()) {
                    addToPreselection(childNode);
                }
                
                String childRowKey = (node.getParent() == null) ? String.valueOf(i) : node.getRowKey() + "_" + i;
                childNode.setRowKey(childRowKey);
                buildRowKeys(childNode);
            }
        }
    }
    
    public void populateRowKeys(TreeNode node, List<String> keys) {
        int childCount = node.getChildCount();
        if(childCount > 0) {
            for(int i = 0; i < childCount; i++) {
                TreeNode childNode = node.getChildren().get(i);
                keys.add(childNode.getRowKey());
                populateRowKeys(childNode, keys);
            }
        }
    }
    
    public void updateRowKeys(TreeNode node) {
        int childCount = node.getChildCount();
        if(childCount > 0) {
            for(int i = 0; i < childCount; i++) {
                TreeNode childNode = node.getChildren().get(i);

                String childRowKey = (node.getParent() == null) ? String.valueOf(i) : node.getRowKey() + "_" + i;
                childNode.setRowKey(childRowKey);
                updateRowKeys(childNode);
            }
        }
    }
        
    public void initPreselection() {
        ValueExpression ve = this.getValueExpression("selection");
        if(ve != null) {
            if(preselection != null) {
                String selectionMode = this.getSelectionMode();
                if(selectionMode != null) {
                    if(selectionMode.equals("single")) {
                        if(this.preselection.size() > 0) {
                            ve.setValue(FacesContext.getCurrentInstance().getELContext(), this.preselection.get(0));
                        }
                    }
                    else {
                        ve.setValue(FacesContext.getCurrentInstance().getELContext(), this.preselection.toArray(new TreeNode[0]));
                    }

                    this.preselection = null;
                }
            }       
            else {
                ve.setValue(FacesContext.getCurrentInstance().getELContext(), null);
            }
        }
    }
    
    private void updateSelectedNodes(TreeNode node) {
        int childCount = node.getChildCount();
        if(childCount > 0) {
            for(int i = 0; i < childCount; i++) {
                TreeNode childNode = node.getChildren().get(i);
                if(childNode.isSelected()) {
                    addToPreselection(childNode);
                }

                updateSelectedNodes(childNode);
            }
        }
    }
    
    public void refreshSelectedNodeKeys() {
        TreeNode root = this.getValue();
        this.preselection = null;
        updateSelectedNodes(root);
        initPreselection();
    }
    
    public String getSelectedRowKeysAsString() {
        String value = null;
        Object selection = this.getSelection();
        String selectionMode = this.getSelectionMode();
        
        if(selection != null) { 
            if(selectionMode.equals("single")) {
                TreeNode node = (TreeNode) selection;
                value = node.getRowKey();
            }
            else {
                TreeNode[] nodes = (TreeNode[]) selection;
                StringBuilder builder = SharedStringBuilder.get(SB_GET_SELECTED_ROW_KEYS_AS_STRING);
                
                for(int i = 0; i < nodes.length; i++) {
                    builder.append(nodes[i].getRowKey());
                    if(i != (nodes.length - 1)) {
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
        String _rowKey = this.getRowKey();
        
        if(_rowKey == null) {
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
        if(!(event instanceof WrapperEvent)) {
            super.broadcast(event);
            return;
        }
        
        FacesContext context = FacesContext.getCurrentInstance();
        WrapperEvent wrapperEvent = (WrapperEvent) event;
        String oldRowKey = this.getRowKey();
        setRowKey(wrapperEvent.getRowKey());
        FacesEvent originalEvent = wrapperEvent.getFacesEvent();
        UIComponent source = (UIComponent) originalEvent.getSource();
        UIComponent compositeParent = null;
        
        try {
            if(!UIComponent.isCompositeComponent(source)) {
                compositeParent = UIComponent.getCompositeComponentParent(source);
            }
            if (compositeParent != null) {
                compositeParent.pushComponentToEL(context, null);
            }
            source.pushComponentToEL(context, null);
            source.broadcast(originalEvent);
        } finally {
            source.popComponentFromEL(context);
            if (compositeParent != null) {
                compositeParent.popComponentFromEL(context);
            }
        }
                
        setRowKey(oldRowKey);
    }
    
    @Override
    public void processDecodes(FacesContext context) {
        if(!isRendered()) {
            return;
        }
        
        pushComponentToEL(context, this);
        
        Map<String, SavedState> saved = (Map<String, SavedState>) getStateHelper().get(PropertyKeys.saved);
        if(saved == null) {
            getStateHelper().remove(PropertyKeys.saved);
        }
        
        processNodes(context, PhaseId.APPLY_REQUEST_VALUES);
        
        try {
            decode(context);
        }
        catch(RuntimeException e) {
            context.renderResponse();
            throw e;
        }
        
        popComponentFromEL(context);
    }
    
    @Override
    public void processValidators(FacesContext context) {
        if(!isRendered()) {
            return;
        }
        
        pushComponentToEL(context, this);
        processNodes(context, PhaseId.PROCESS_VALIDATIONS);
        validateSelection(context);
        popComponentFromEL(context);
    }
    
    protected void validateSelection(FacesContext context) {
        String selectionMode = this.getSelectionMode();
        if(selectionMode != null && this.isRequired()) {
            boolean valid = true;
            Object selection = this.getSelection();

            if(selectionMode.equals("single")) {
                if(selection == null) {
                    valid = false;
                }
            }
            else {
                TreeNode[] selectionArray = (TreeNode[]) selection;
                if(selectionArray.length == 0) {
                    valid = false;
                }
            } 

            if(!valid) {
                String requiredMessage = this.getRequiredMessage();
                FacesMessage msg;

                if(requiredMessage != null)
                    msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, requiredMessage, requiredMessage);
                else
                    msg = MessageFactory.getMessage(REQUIRED_MESSAGE_ID, FacesMessage.SEVERITY_ERROR, new Object[]{this.getClientId(context)});

                context.addMessage(this.getClientId(context), msg);
                context.validationFailed();
                context.renderResponse();
            }
        }
    }
      
    @Override
    public void processUpdates(FacesContext context) {
        if(!isRendered()) {
            return;
        }
        
		pushComponentToEL(context, this);
        
        processNodes(context, PhaseId.UPDATE_MODEL_VALUES);
        
        updateSelection(context);
        
        popComponentFromEL(context);
	}
    
    public void updateSelection(FacesContext context) {
        String selectionMode = this.getSelectionMode();
        ValueExpression selectionVE = this.getValueExpression("selection");

        if(selectionMode != null && selectionVE != null) {
            Object selection = this.getLocalSelectedNodes();
            Object previousSelection = selectionVE.getValue(context.getELContext());

            if(selectionMode.equals("single")) {
                if(previousSelection != null)
                    ((TreeNode) previousSelection).setSelected(false);
                if(selection != null)
                    ((TreeNode) selection).setSelected(true);
            } 
            else {
                TreeNode[] previousSelections = (TreeNode[]) previousSelection;
                TreeNode[] selections = (TreeNode[]) selection;

                if(previousSelections != null) {
                    for(TreeNode node : previousSelections)
                        node.setSelected(false);
                }

                if(selections != null) {
                    for(TreeNode node : selections)
                        node.setSelected(true);
                }
            }

			selectionVE.setValue(context.getELContext(), selection);
			setSelection(null);
		}
    }
    
    protected void processNodes(FacesContext context, PhaseId phaseId) {
        if(this.isSkipChildren()) {
            return;
        }      
        
        processFacets(context, phaseId);
        processColumnFacets(context, phaseId);
        
        TreeNode root = getValue();
        if(root != null) {
            processNode(context, phaseId, root, null);
		}

        setRowKey(null);
    }
    
    protected void processNode(FacesContext context, PhaseId phaseId, TreeNode treeNode, String rowKey) {
        processColumnChildren(context, phaseId, rowKey);
        
        //process child nodes if node is expanded or node itself is the root
        if(shouldVisitNode(treeNode)) {
            int childIndex = 0;
            for(Iterator<TreeNode> iterator = treeNode.getChildren().iterator(); iterator.hasNext();) {
                String childRowKey = rowKey == null ? String.valueOf(childIndex) : rowKey + SEPARATOR + childIndex;

                processNode(context, phaseId, iterator.next(), childRowKey);

                childIndex++;
            }
        }
    }
        
    protected void processFacets(FacesContext context, PhaseId phaseId) {
        setRowKey(null);
        
        if(getFacetCount() > 0) {
            for(UIComponent facet : getFacets().values()) {
                if(phaseId == PhaseId.APPLY_REQUEST_VALUES)
                    facet.processDecodes(context);
                else if (phaseId == PhaseId.PROCESS_VALIDATIONS)
                    facet.processValidators(context);
                else if (phaseId == PhaseId.UPDATE_MODEL_VALUES)
                    facet.processUpdates(context);
                else
                    throw new IllegalArgumentException();
            }
        }
    }
    
    protected void processColumnFacets(FacesContext context, PhaseId phaseId) {
        setRowKey(null);
        
        for(UIComponent child : getChildren()) {
            if(child instanceof UIColumn && child.isRendered()) {
                UIColumn column = (UIColumn) child;
                
                if(column.getFacetCount() > 0) {
                    for(UIComponent columnFacet : column.getFacets().values()) {
                        if(phaseId == PhaseId.APPLY_REQUEST_VALUES)
                            columnFacet.processDecodes(context);
                        else if (phaseId == PhaseId.PROCESS_VALIDATIONS)
                            columnFacet.processValidators(context);
                        else if (phaseId == PhaseId.UPDATE_MODEL_VALUES)
                            columnFacet.processUpdates(context);
                        else
                            throw new IllegalArgumentException();
                    }
                }
            }
        }
    }
    
    protected void processColumnChildren(FacesContext context, PhaseId phaseId, String nodeKey) {
        setRowKey(nodeKey);
        
        if(nodeKey == null)
            return;
        
        for(UIComponent child : getChildren()) {
            if(child.isRendered()) {
                if(child instanceof UIColumn) {
                    for(UIComponent grandkid : child.getChildren()) {
                        if(grandkid.isRendered()) {
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
        if(phaseId == PhaseId.APPLY_REQUEST_VALUES)
            component.processDecodes(context);
        else if(phaseId == PhaseId.PROCESS_VALIDATIONS)
            component.processValidators(context);
        else if(phaseId == PhaseId.UPDATE_MODEL_VALUES)
            component.processUpdates(context);
        else
            throw new IllegalArgumentException();
    }
    
    private void saveDescendantState() {
        FacesContext context = getFacesContext();
        
        for(UIComponent child : getChildren()) {
            saveDescendantState(child, context);
        }
    }
    
    private void saveDescendantState(UIComponent component, FacesContext context) {
        Map<String, SavedState> saved = (Map<String, SavedState>) getStateHelper().get(PropertyKeys.saved);
        
        if(component instanceof EditableValueHolder) {
            EditableValueHolder input = (EditableValueHolder) component;
            SavedState state = null;
            String clientId = component.getClientId(context);
            
            if(saved == null) {
                state = new SavedState();
                getStateHelper().put(PropertyKeys.saved, clientId, state);
            }
            
            if(state == null) {
                state = saved.get(clientId);
                
                if(state == null) {
                    state = new SavedState();
                    getStateHelper().put(PropertyKeys.saved, clientId, state);
                }
            }
            
            state.setValue(input.getLocalValue());
            state.setValid(input.isValid());
            state.setSubmittedValue(input.getSubmittedValue());
            state.setLocalValueSet(input.isLocalValueSet());
        } 
        
        for(UIComponent uiComponent : component.getChildren()) {
            saveDescendantState(uiComponent, context);
        }

        if(component.getFacetCount() > 0) {
            for(UIComponent facet : component.getFacets().values()) {
                saveDescendantState(facet, context);
            }
        }
    }
    
    private void restoreDescendantState() {
        FacesContext context = getFacesContext();

        for(UIComponent child : getChildren()) {
            restoreDescendantState(child, context);
        }
    }

    private void restoreDescendantState(UIComponent component, FacesContext context) {
        //force id reset
        String id = component.getId();
        component.setId(id);
        
        Map<String, SavedState> saved = (Map<String,SavedState>) getStateHelper().get(PropertyKeys.saved);

        if(component instanceof EditableValueHolder) {
            EditableValueHolder input = (EditableValueHolder) component;
            String clientId = component.getClientId(context);

            SavedState state = saved.get(clientId);
            if(state == null) {
                state = new SavedState();
            }
            
            input.setValue(state.getValue());
            input.setValid(state.isValid());
            input.setSubmittedValue(state.getSubmittedValue());
            input.setLocalValueSet(state.isLocalValueSet());
        }
        for(UIComponent kid : component.getChildren()) {
            restoreDescendantState(kid, context);
        }
        
        if(component.getFacetCount() > 0) {
            for(UIComponent facet : component.getFacets().values()) {
                restoreDescendantState(facet, context);
            }
        }
    }
    
    @Override
    public boolean visitTree(VisitContext context, VisitCallback callback) {
        if(!isVisitable(context))
            return false;

        FacesContext facesContext = context.getFacesContext();
        boolean visitNodes = requiresIteration(facesContext, context);

        String oldRowKey= getRowKey();
        if(visitNodes) {
            setRowKey(null);
        }
        
        pushComponentToEL(facesContext, null);

        try {
            VisitResult result = context.invokeVisitCallback(this, callback);

            if(result == VisitResult.COMPLETE)
                return true;

            if((result == VisitResult.ACCEPT) && doVisitChildren(context)) {
                if(visitFacets(context, callback, visitNodes))
                    return true;

                if(visitNodes(context, callback, visitNodes))
                    return true;
            }
        }
        finally {
            popComponentFromEL(facesContext);
            
            if(visitNodes) {
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
        if(visitNodes) {
            setRowKey(null);
        }
        
        if(getFacetCount() > 0) {
            for(UIComponent facet : getFacets().values()) {
                if(facet.visitTree(context, callback))
                    return true;
            }
        }

        return false;
    }
    
    private boolean visitColumns(VisitContext context, VisitCallback callback, String rowKey) {   
        setRowKey(rowKey);
        
        if(rowKey == null)
            return false;
        
        if(getChildCount() > 0) {
            for(UIComponent child : getChildren()) {
                if(child instanceof Columns) {
                    Columns uicolumns = (Columns) child;
                    for(int i = 0; i < uicolumns.getRowCount(); i++) {
                        uicolumns.setRowIndex(i);

                        boolean value = visitColumnContent(context, callback, uicolumns);
                        if(value) {
                            uicolumns.setRowIndex(-1);
                            return true;
                        }
                    }

                    uicolumns.setRowIndex(-1);
                }
                else if(child instanceof UIColumn) {
                    if(child.visitTree(context, callback)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }
    
    protected boolean visitColumnContent(VisitContext context, VisitCallback callback, UIComponent component) {
        if(component.getChildCount() > 0) {
            for(UIComponent grandkid : component.getChildren()) {
                if(grandkid.visitTree(context, callback)) {
                    return true;
                }
            }
        }  
        
        return false;
    }
    
    protected boolean visitNodes(VisitContext context, VisitCallback callback, boolean visitRows) {
        if(visitRows) {
            TreeNode root = getValue();
            if(root != null) {
                if(visitNode(context, callback, root, null)) {
                    return true;
                }
            }

            setRowKey(null);
        }

        return false;
    }
    
    protected boolean visitNode(VisitContext context, VisitCallback callback, TreeNode treeNode, String rowKey) {
        if(visitColumns(context, callback, rowKey)) {
            return true;
        }
        
        //visit child nodes if node is expanded or node itself is the root
        if(shouldVisitNode(treeNode)) {
            int childIndex = 0;
            for(Iterator<TreeNode> iterator = treeNode.getChildren().iterator(); iterator.hasNext();) {
                String childRowKey = rowKey == null ? String.valueOf(childIndex) : rowKey + SEPARATOR + childIndex;

                if(visitNode(context, callback, iterator.next(), childRowKey)) {
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
        catch(IllegalArgumentException e) {
            //JSF 2.0
            Object skipHint = context.getAttributes().get("javax.faces.visit.SKIP_ITERATION");
            return !Boolean.TRUE.equals(skipHint);
        }
    }
}