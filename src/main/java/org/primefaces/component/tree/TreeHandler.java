package org.primefaces.component.tree;

import javax.faces.view.facelets.ComponentConfig;
import javax.faces.view.facelets.ComponentHandler;
import javax.faces.view.facelets.MetaRule;
import javax.faces.view.facelets.MetaRuleset;
import org.primefaces.event.DragDropEvent;

import org.primefaces.event.NodeCollapseEvent;
import org.primefaces.event.NodeExpandEvent;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.NodeUnselectEvent;
import org.primefaces.facelets.MethodRule;

public class TreeHandler extends ComponentHandler {

	private static final MetaRule NODE_SELECT_LISTENER =
			new MethodRule("nodeSelectListener", null, new Class[]{NodeSelectEvent.class});
	private static final MetaRule NODE_UNSELECT_LISTENER =
			new MethodRule("nodeUnselectListener", null, new Class[]{NodeUnselectEvent.class});
	private static final MetaRule NODE_EXPAND_LISTENER =
			new MethodRule("nodeExpandListener", null, new Class[]{NodeExpandEvent.class});
	private static final MetaRule NODE_COLLAPSE_LISTENER =
			new MethodRule("nodeCollapseListener", null, new Class[]{NodeCollapseEvent.class});
	private static final MetaRule DRAG_DROP_LISTENER =
			new MethodRule("dragdropListener", null, new Class[]{DragDropEvent.class});

	public TreeHandler(ComponentConfig config) {
		super(config);
	}
	
	@SuppressWarnings("unchecked")
	protected MetaRuleset createMetaRuleset(Class type) { 
		MetaRuleset metaRuleset = super.createMetaRuleset(type); 
		
		metaRuleset.addRule(NODE_SELECT_LISTENER);
        metaRuleset.addRule(NODE_UNSELECT_LISTENER);
		metaRuleset.addRule(NODE_EXPAND_LISTENER);
		metaRuleset.addRule(NODE_COLLAPSE_LISTENER);
        metaRuleset.addRule(DRAG_DROP_LISTENER);
		
		return metaRuleset; 
	} 
}