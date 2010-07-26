package org.primefaces.event;

import javax.faces.component.UIComponent;
import javax.faces.event.FacesEvent;
import javax.faces.event.FacesListener;

import org.primefaces.model.TreeNode;

public class NodeCollapseEvent extends FacesEvent {

	private TreeNode treeNode;
	
	public NodeCollapseEvent(UIComponent source, TreeNode treeNode) {
		super(source);
		this.treeNode = treeNode;
	}

	@Override
	public boolean isAppropriateListener(FacesListener listener) {
		return (listener instanceof NodeCollapseListener);
	}

	@Override
	public void processListener(FacesListener listener) {
		((NodeCollapseListener) listener).processNodeCollapse(this);
	}

	public TreeNode getTreeNode() {
		return treeNode;
	}

	public void setTreeNode(TreeNode treeNode) {
		this.treeNode = treeNode;
	}
}