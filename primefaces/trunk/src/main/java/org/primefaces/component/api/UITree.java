/*
 * Copyright 2009-2012 Prime Teknoloji.
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

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import javax.faces.component.EditableValueHolder;
import javax.faces.component.NamingContainer;
import javax.faces.component.UIColumn;
import javax.faces.component.UIComponent;
import javax.faces.component.UIComponentBase;
import javax.faces.component.UINamingContainer;
import javax.faces.component.visit.VisitCallback;
import javax.faces.component.visit.VisitContext;
import javax.faces.component.visit.VisitResult;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.FacesEvent;
import javax.faces.event.FacesListener;
import javax.faces.event.PhaseId;
import org.primefaces.model.TreeNode;

public abstract class UITree extends UIComponentBase implements NamingContainer {
    
    public final static String SEPARATOR = "_";
    
    private String rowKey;
   
    private TreeNode rowNode;
            
    protected enum PropertyKeys {
		var
        ,saved
		,value;

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
        saveDescendantState();
        
        this.rowKey = rowKey;

        if(rowKey == null) {
            FacesContext.getCurrentInstance().getExternalContext().getRequestMap().remove(getVar());
        } 
        else {
            TreeNode root = getValue();
            this.rowNode = findTreeNode(root, rowKey);
            
            FacesContext.getCurrentInstance().getExternalContext().getRequestMap().put(getVar(), this.rowNode.getData());
        }

        restoreDescendantState();
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
    
    public TreeNode getValue() {
		return (TreeNode) getStateHelper().eval(PropertyKeys.value, null);
	}
	public void setValue(TreeNode _value) {
		getStateHelper().put(PropertyKeys.value, _value);
	}
    
    protected TreeNode findTreeNode(TreeNode searchRoot, String rowKey) {
		String[] paths = rowKey.split(SEPARATOR);
		
		if(paths.length == 0)
			return null;
		
		int childIndex = Integer.parseInt(paths[0]);
		searchRoot = searchRoot.getChildren().get(childIndex);

		if(paths.length == 1) {
			return searchRoot;
		} 
		else {
			String relativeRowKey = rowKey.substring(rowKey.indexOf(SEPARATOR) + 1);
				
			return findTreeNode(searchRoot, relativeRowKey);
		}
	}
    
    @Override
    public String getContainerClientId(FacesContext context) {
        String clientId = super.getContainerClientId(context);
        String _rowKey = this.getRowKey();
        
        if(_rowKey == null) {
            return clientId;
        } 
        else {
            StringBuilder builder = new StringBuilder();
            
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
        
        WrapperEvent wrapperEvent = (WrapperEvent) event;
        FacesEvent originalEvent = wrapperEvent.getFacesEvent();
        UIComponent originalSource = (UIComponent) originalEvent.getSource();
        setRowKey(wrapperEvent.getRowKey());
        
        originalSource.broadcast(originalEvent);
    }
    
    @Override
    public void processDecodes(FacesContext context) {
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
        pushComponentToEL(context, this);
        
        processNodes(context, PhaseId.PROCESS_VALIDATIONS);
        
        popComponentFromEL(context);
    }
    
    @Override
    public void processUpdates(FacesContext context) {
        pushComponentToEL(context, this);
        
        processNodes(context, PhaseId.UPDATE_MODEL_VALUES);
        
        popComponentFromEL(context);
    }
    
    protected void processNodes(FacesContext context, PhaseId phaseId) {
              
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
        if(treeNode.isExpanded() || treeNode.getParent() == null) {
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
            if(child instanceof UIColumn && child.isRendered()) {
                for(UIComponent grandkid : child.getChildren()) {
                    if(!grandkid.isRendered())
                        continue;
                    
                    if(phaseId == PhaseId.APPLY_REQUEST_VALUES)
                        grandkid.processDecodes(context);
                    else if(phaseId == PhaseId.PROCESS_VALIDATIONS)
                        grandkid.processValidators(context);
                    else if(phaseId == PhaseId.UPDATE_MODEL_VALUES)
                        grandkid.processUpdates(context);
                    else
                        throw new IllegalArgumentException();
                }
            }
        }
    }
    
    private void saveDescendantState() {
        FacesContext context = FacesContext.getCurrentInstance();
        
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
        FacesContext context = FacesContext.getCurrentInstance();

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

        String oldRowKey= getRowKey();
        setRowKey(null);
        
        pushComponentToEL(facesContext, null);

        try {
            VisitResult result = context.invokeVisitCallback(this, callback);

            if(result == VisitResult.COMPLETE)
                return true;

            if((result == VisitResult.ACCEPT) && doVisitChildren(context)) {
                if(visitFacets(context, callback))
                    return true;

                if(visitNodes(context, callback))
                    return true;
            }
        }
        finally {
            popComponentFromEL(facesContext);
            setRowKey(oldRowKey);
        }

        return false;
    }
    
    protected boolean doVisitChildren(VisitContext context) {
        Collection<String> idsToVisit = context.getSubtreeIdsToVisit(this);

        return (!idsToVisit.isEmpty());
    }
    
    protected boolean visitFacets(VisitContext context, VisitCallback callback) {
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
                if(child instanceof UIColumn) {
                    if(child.visitTree(context, callback)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }
    
    protected boolean visitNodes(VisitContext context, VisitCallback callback) {
        TreeNode root = getValue();
        if(root != null) {
            if(visitNode(context, callback, root, null)) {
                return true;
            }
		}

        setRowKey(null);
        
        return false;
    }
    
    protected boolean visitNode(VisitContext context, VisitCallback callback, TreeNode treeNode, String rowKey) {
        if(visitColumns(context, callback, rowKey)) {
            return true;
        }
        
        //visit child nodes if node is expanded or node itself is the root
        if((treeNode.isExpanded() || treeNode.getParent() == null)) {
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

}