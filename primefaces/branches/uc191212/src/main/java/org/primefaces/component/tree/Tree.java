/*
 * Generated, Do Not Modify
 */
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
package org.primefaces.component.tree;

import org.primefaces.component.api.UITree;
import javax.faces.context.FacesContext;
import javax.faces.component.UINamingContainer;
import javax.el.ValueExpression;
import javax.el.MethodExpression;
import javax.faces.render.Renderer;
import java.io.IOException;
import javax.faces.component.UIComponent;
import javax.faces.event.AbortProcessingException;
import javax.faces.application.ResourceDependencies;
import javax.faces.application.ResourceDependency;
import java.util.List;
import java.util.ArrayList;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.NodeUnselectEvent;
import org.primefaces.event.NodeExpandEvent;
import org.primefaces.event.NodeCollapseEvent;
import org.primefaces.event.DragDropEvent;
import javax.faces.component.UIComponent;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.HashMap;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.lang.StringBuilder;
import org.primefaces.model.TreeNode;
import javax.faces.event.FacesEvent;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.PhaseId;
import org.primefaces.util.Constants;
import org.primefaces.model.TreeNode;

@ResourceDependencies({
	@ResourceDependency(library="primefaces", name="primefaces.css"),
	@ResourceDependency(library="primefaces", name="jquery/jquery.js"),
	@ResourceDependency(library="primefaces", name="primefaces.js")
})
public class Tree extends UITree implements org.primefaces.component.api.Widget,javax.faces.component.behavior.ClientBehaviorHolder {


	public static final String COMPONENT_TYPE = "org.primefaces.component.Tree";
	public static final String COMPONENT_FAMILY = "org.primefaces.component";
	private static final String DEFAULT_RENDERER = "org.primefaces.component.TreeRenderer";
	private static final String OPTIMIZED_PACKAGE = "org.primefaces.component.";

	protected enum PropertyKeys {

		widgetVar
		,dynamic
		,cache
		,onNodeClick
		,selection
		,style
		,styleClass
		,selectionMode
		,highlight
		,datakey
		,animate
		,orientation
		,propagateSelectionUp
		,propagateSelectionDown
		,dir;

		String toString;

		PropertyKeys(String toString) {
			this.toString = toString;
		}

		PropertyKeys() {}

		public String toString() {
			return ((this.toString != null) ? this.toString : super.toString());
}
	}

	public Tree() {
		setRendererType(DEFAULT_RENDERER);
	}

	public String getFamily() {
		return COMPONENT_FAMILY;
	}

	public java.lang.String getWidgetVar() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.widgetVar, null);
	}
	public void setWidgetVar(java.lang.String _widgetVar) {
		getStateHelper().put(PropertyKeys.widgetVar, _widgetVar);
		handleAttribute("widgetVar", _widgetVar);
	}

	public boolean isDynamic() {
		return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.dynamic, false);
	}
	public void setDynamic(boolean _dynamic) {
		getStateHelper().put(PropertyKeys.dynamic, _dynamic);
		handleAttribute("dynamic", _dynamic);
	}

	public boolean isCache() {
		return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.cache, true);
	}
	public void setCache(boolean _cache) {
		getStateHelper().put(PropertyKeys.cache, _cache);
		handleAttribute("cache", _cache);
	}

	public java.lang.String getOnNodeClick() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.onNodeClick, null);
	}
	public void setOnNodeClick(java.lang.String _onNodeClick) {
		getStateHelper().put(PropertyKeys.onNodeClick, _onNodeClick);
		handleAttribute("onNodeClick", _onNodeClick);
	}

	public java.lang.Object getSelection() {
		return (java.lang.Object) getStateHelper().eval(PropertyKeys.selection, null);
	}
	public void setSelection(java.lang.Object _selection) {
		getStateHelper().put(PropertyKeys.selection, _selection);
		handleAttribute("selection", _selection);
	}

	public java.lang.String getStyle() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.style, null);
	}
	public void setStyle(java.lang.String _style) {
		getStateHelper().put(PropertyKeys.style, _style);
		handleAttribute("style", _style);
	}

	public java.lang.String getStyleClass() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.styleClass, null);
	}
	public void setStyleClass(java.lang.String _styleClass) {
		getStateHelper().put(PropertyKeys.styleClass, _styleClass);
		handleAttribute("styleClass", _styleClass);
	}

	public java.lang.String getSelectionMode() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.selectionMode, null);
	}
	public void setSelectionMode(java.lang.String _selectionMode) {
		getStateHelper().put(PropertyKeys.selectionMode, _selectionMode);
		handleAttribute("selectionMode", _selectionMode);
	}

	public boolean isHighlight() {
		return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.highlight, true);
	}
	public void setHighlight(boolean _highlight) {
		getStateHelper().put(PropertyKeys.highlight, _highlight);
		handleAttribute("highlight", _highlight);
	}

	public java.lang.Object getDatakey() {
		return (java.lang.Object) getStateHelper().eval(PropertyKeys.datakey, null);
	}
	public void setDatakey(java.lang.Object _datakey) {
		getStateHelper().put(PropertyKeys.datakey, _datakey);
		handleAttribute("datakey", _datakey);
	}

	public boolean isAnimate() {
		return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.animate, false);
	}
	public void setAnimate(boolean _animate) {
		getStateHelper().put(PropertyKeys.animate, _animate);
		handleAttribute("animate", _animate);
	}

	public java.lang.String getOrientation() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.orientation, "vertical");
	}
	public void setOrientation(java.lang.String _orientation) {
		getStateHelper().put(PropertyKeys.orientation, _orientation);
		handleAttribute("orientation", _orientation);
	}

	public boolean isPropagateSelectionUp() {
		return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.propagateSelectionUp, true);
	}
	public void setPropagateSelectionUp(boolean _propagateSelectionUp) {
		getStateHelper().put(PropertyKeys.propagateSelectionUp, _propagateSelectionUp);
		handleAttribute("propagateSelectionUp", _propagateSelectionUp);
	}

	public boolean isPropagateSelectionDown() {
		return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.propagateSelectionDown, true);
	}
	public void setPropagateSelectionDown(boolean _propagateSelectionDown) {
		getStateHelper().put(PropertyKeys.propagateSelectionDown, _propagateSelectionDown);
		handleAttribute("propagateSelectionDown", _propagateSelectionDown);
	}

	public java.lang.String getDir() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.dir, "ltr");
	}
	public void setDir(java.lang.String _dir) {
		getStateHelper().put(PropertyKeys.dir, _dir);
		handleAttribute("dir", _dir);
	}


    private static final Collection<String> EVENT_NAMES = Collections.unmodifiableCollection(Arrays.asList("select","unselect", "expand", "collapse"));;;

    private List<String> selectedRowKeys = new ArrayList<String>();

	private Map<String,UITreeNode> nodes;

	public UITreeNode getUITreeNodeByType(String type) {
		UITreeNode node = getTreeNodes().get(type);
		
		if(node == null)
			throw new javax.faces.FacesException("Unsupported tree node type:" + type);
		else
			return node;
	}

    private boolean isRequestSource(FacesContext context) {
        return this.getClientId(context).equals(context.getExternalContext().getRequestParameterMap().get(Constants.PARTIAL_SOURCE_PARAM));
    }
	
	public void processUpdates(FacesContext context) {
		super.processUpdates(context);
        
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
	
    public boolean isNodeExpandRequest(FacesContext context) {
		return context.getExternalContext().getRequestParameterMap().containsKey(this.getClientId(context) + "_expandNode");
	}

    public Object getLocalSelectedNodes() {
        return getStateHelper().get(PropertyKeys.selection);
    }

    public static String CONTAINER_CLASS = "ui-tree ui-widget ui-widget-content ui-corner-all";
    public static String CONTAINER_RTL_CLASS = "ui-tree ui-tree-rtl ui-widget ui-widget-content ui-corner-all";
    public static String HORIZONTAL_CONTAINER_CLASS = "ui-tree ui-tree-horizontal ui-widget ui-widget-content ui-corner-all";
    public static String ROOT_NODES_CLASS = "ui-tree-container";
    public static String PARENT_NODE_CLASS = "ui-treenode ui-treenode-parent";
    public static String LEAF_NODE_CLASS = "ui-treenode ui-treenode-leaf";
    public static String CHILDREN_NODES_CLASS = "ui-treenode-children";
    public static String NODE_CONTENT_CLASS_V = "ui-treenode-content";
    public static String SELECTABLE_NODE_CONTENT_CLASS_V = "ui-treenode-content ui-tree-selectable";
    public static String NODE_CONTENT_CLASS_H = "ui-treenode-content ui-state-default ui-corner-all";
    public static String SELECTABLE_NODE_CONTENT_CLASS_H = "ui-treenode-content ui-tree-selectable ui-state-default ui-corner-all";
    public static String EXPANDED_ICON_CLASS_V = "ui-tree-toggler ui-icon ui-icon-triangle-1-s";
    public static String COLLAPSED_ICON_CLASS_V = "ui-tree-toggler ui-icon ui-icon-triangle-1-e";
    public static String COLLAPSED_ICON_RTL_CLASS_V = "ui-tree-toggler ui-icon ui-icon-triangle-1-w";
    public static String EXPANDED_ICON_CLASS_H = "ui-tree-toggler ui-icon ui-icon-minus";
    public static String COLLAPSED_ICON_CLASS_H = "ui-tree-toggler ui-icon ui-icon-plus";
    public static String LEAF_ICON_CLASS = "ui-treenode-leaf-icon";
    public static String NODE_ICON_CLASS = "ui-treenode-icon ui-icon";
    public static String NODE_LABEL_CLASS = "ui-treenode-label ui-corner-all";

    public Map<String,UITreeNode> getTreeNodes() {
        if(nodes == null) {
			nodes = new HashMap<String,UITreeNode>();
			for(UIComponent child : getChildren()) {
                UITreeNode node = (UITreeNode) child;
				nodes.put(node.getType(), node);
			}
		}

        return nodes;
    }

    public List<String> getSelectedRowKeys() {
        return this.selectedRowKeys;
    }

    public String getSelectedRowKeysAsString() {
        StringBuilder builder = new StringBuilder();

        for(Iterator<String> iter = this.selectedRowKeys.iterator();iter.hasNext();) {
            builder.append(iter.next());

            if(iter.hasNext()) {
                builder.append(',');
            }
        }

        return builder.toString();
    }

    @Override
    public Collection<String> getEventNames() {
        return EVENT_NAMES;
    }

    @Override
    public void queueEvent(FacesEvent event) {
        FacesContext context = FacesContext.getCurrentInstance();

        if(isRequestSource(context)) {
            Map<String,String> params = context.getExternalContext().getRequestParameterMap();
            String eventName = params.get(Constants.PARTIAL_BEHAVIOR_EVENT_PARAM);
            String clientId = this.getClientId(context);
            FacesEvent wrapperEvent = null;

            AjaxBehaviorEvent behaviorEvent = (AjaxBehaviorEvent) event;

            if(eventName.equals("expand")) {
                this.setRowKey(params.get(clientId + "_expandNode"));

                wrapperEvent = new NodeExpandEvent(this, behaviorEvent.getBehavior(), this.getRowNode());
                wrapperEvent.setPhaseId(PhaseId.APPLY_REQUEST_VALUES);
            }
            else if(eventName.equals("collapse")) {
                this.setRowKey(params.get(clientId + "_collapseNode"));
                TreeNode collapsedNode = this.getRowNode();
                collapsedNode.setExpanded(false);

                wrapperEvent = new NodeCollapseEvent(this, behaviorEvent.getBehavior(), collapsedNode);
                wrapperEvent.setPhaseId(PhaseId.APPLY_REQUEST_VALUES);
            }
            else if(eventName.equals("select")) {
                setRowKey(params.get(clientId + "_instantSelection"));

                wrapperEvent = new NodeSelectEvent(this, behaviorEvent.getBehavior(), this.getRowNode());
                wrapperEvent.setPhaseId(behaviorEvent.getPhaseId());
            }
            else if(eventName.equals("unselect")) {
                setRowKey(params.get(clientId + "_instantUnselection"));

                wrapperEvent = new NodeUnselectEvent(this, behaviorEvent.getBehavior(), this.getRowNode());
                wrapperEvent.setPhaseId(behaviorEvent.getPhaseId());
            } 
            
            super.queueEvent(wrapperEvent);
            
            this.setRowKey(null);
        }
        else {
            super.queueEvent(event);
        }
    }

    private boolean isToggleRequest(FacesContext context) {
        Map<String,String> params = context.getExternalContext().getRequestParameterMap();
        String clientId = getClientId(context);

        return params.get(clientId + "_expandNode") != null || params.get(clientId + "_collapseNode") != null;
    }

    @Override
    public void processDecodes(FacesContext context) {
        if(isToggleRequest(context)) {
            this.decode(context);
            context.renderResponse();
        } else {
            super.processDecodes(context);
        }
    }

    public boolean isCheckboxSelection() {
        String selectionMode = this.getSelectionMode();
        
        return selectionMode != null && selectionMode.equals("checkbox");
    }

	protected FacesContext getFacesContext() {
		return FacesContext.getCurrentInstance();
	}
	public String resolveWidgetVar() {
		FacesContext context = FacesContext.getCurrentInstance();
		String userWidgetVar = (String) getAttributes().get("widgetVar");

		if(userWidgetVar != null)
			return userWidgetVar;
		 else
			return "widget_" + getClientId(context).replaceAll("-|" + UINamingContainer.getSeparatorChar(context), "_");
	}

	public void handleAttribute(String name, Object value) {
		List<String> setAttributes = (List<String>) this.getAttributes().get("javax.faces.component.UIComponentBase.attributesThatAreSet");
		if(setAttributes == null) {
			String cname = this.getClass().getName();
			if(cname != null && cname.startsWith(OPTIMIZED_PACKAGE)) {
				setAttributes = new ArrayList<String>(6);
				this.getAttributes().put("javax.faces.component.UIComponentBase.attributesThatAreSet", setAttributes);
			}
		}
		if(setAttributes != null) {
			if(value == null) {
				ValueExpression ve = getValueExpression(name);
				if(ve == null) {
					setAttributes.remove(name);
				} else if(!setAttributes.contains(name)) {
					setAttributes.add(name);
				}
			}
		}
	}
}