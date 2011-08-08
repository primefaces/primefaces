/*
 * Copyright 2009-2011 Prime Technology.
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

import java.util.Iterator;
import javax.faces.component.NamingContainer;
import javax.faces.component.UIColumn;
import javax.faces.component.UIComponent;
import javax.faces.component.UIComponentBase;
import javax.faces.component.UINamingContainer;
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
        resetChildren();
        
        this.rowKey = rowKey;

        if(rowKey == null) {
            FacesContext.getCurrentInstance().getExternalContext().getRequestMap().remove(getVar());
        } 
        else {
            TreeNode root = getValue();
            this.rowNode = findTreeNode(root, rowKey);
            
            FacesContext.getCurrentInstance().getExternalContext().getRequestMap().put(getVar(), this.rowNode.getData());
        }
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
			String relativeRowKey = rowKey.substring(2);
				
			return findTreeNode(searchRoot, relativeRowKey);
		}
	}
    
    private void resetChildren() {        
        for(UIComponent kid : getChildren()) {
            if(kid instanceof UIColumn) {
                for(UIComponent grandKid : kid.getChildren())
                    grandKid.setId(grandKid.getId());   //reset clientId
            }
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
        iterate(context, PhaseId.APPLY_REQUEST_VALUES);
        decode(context);
        popComponentFromEL(context);
    }
    
    private void iterate(FacesContext context, PhaseId phaseId) {
        setRowKey(null);
        if (getFacetCount() > 0) {
            for (UIComponent facet : getFacets().values()) {
                if (phaseId == PhaseId.APPLY_REQUEST_VALUES) {
                    facet.processDecodes(context);
                } else if (phaseId == PhaseId.PROCESS_VALIDATIONS) {
                    facet.processValidators(context);
                } else if (phaseId == PhaseId.UPDATE_MODEL_VALUES) {
                    facet.processUpdates(context);
                } else {
                    throw new IllegalArgumentException();
                }
            }
        }

        setRowKey(null);
        if (getChildCount() > 0) {
            for (UIComponent column : getChildren()) {
                if (!(column instanceof UIColumn) || !column.isRendered()) {
                    continue;
                }
                if (column.getFacetCount() > 0) {
                    for (UIComponent columnFacet : column.getFacets().values()) {
                        if (phaseId == PhaseId.APPLY_REQUEST_VALUES) {
                            columnFacet.processDecodes(context);
                        } else if (phaseId == PhaseId.PROCESS_VALIDATIONS) {
                            columnFacet.processValidators(context);
                        } else if (phaseId == PhaseId.UPDATE_MODEL_VALUES) {
                            columnFacet.processUpdates(context);
                        } else {
                            throw new IllegalArgumentException();
                        }
                    }
                }
            }
        }
        
        TreeNode root = getValue();
        if(root != null) {
            int _rowKey = 0;
			for(Iterator<TreeNode> iterator = root.getChildren().iterator(); iterator.hasNext();) {
				processChildren(context, phaseId, iterator.next(), String.valueOf(_rowKey));
                _rowKey++;
			}
		}

        setRowKey(null);
    }
    
    private void processChildren(FacesContext context, PhaseId phaseId, TreeNode treeNode, String nodeKey) {
        setRowKey(nodeKey);
        
        for(UIComponent column : getChildren()) {
            if (!(column instanceof UIColumn) || !column.isRendered()) {
                continue;
            }
            for (UIComponent grandkid : column.getChildren()) {
                if (!grandkid.isRendered()) {
                    continue;
                }
                if (phaseId == PhaseId.APPLY_REQUEST_VALUES) {
                    grandkid.processDecodes(context);
                } else if (phaseId == PhaseId.PROCESS_VALIDATIONS) {
                    grandkid.processValidators(context);
                } else if (phaseId == PhaseId.UPDATE_MODEL_VALUES) {
                    grandkid.processUpdates(context);
                } else {
                    throw new IllegalArgumentException();
                }
            }
        }
        
        if(treeNode.isExpanded()) {
            int childRowKey = 0;
            for(TreeNode childNode : treeNode.getChildren()) {
                processChildren(context, phaseId, childNode, String.valueOf(nodeKey + UITree.SEPARATOR + childRowKey));
                childRowKey++;
            }
        }
    }
}

class WrapperEvent extends FacesEvent {

    public WrapperEvent(UIComponent component, FacesEvent event, String rowKey) {
        super(component);
        this.event = event;
        this.rowKey = rowKey;
    }

    private FacesEvent event = null;
    private String rowKey = null;

    public FacesEvent getFacesEvent() {
        return (this.event);
    }

    public String getRowKey() {
        return rowKey;
    }

    public PhaseId getPhaseId() {
        return (this.event.getPhaseId());
    }

    public void setPhaseId(PhaseId phaseId) {
        this.event.setPhaseId(phaseId);
    }

    public boolean isAppropriateListener(FacesListener listener) {
        return (false);
    }

    public void processListener(FacesListener listener) {
        throw new IllegalStateException();
    }
}
