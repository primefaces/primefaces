package org.primefaces.component.tree;

import javax.faces.view.facelets.ComponentConfig;
import javax.faces.view.facelets.ComponentHandler;
import javax.faces.view.facelets.MetaRuleset;
import org.primefaces.event.DragDropEvent;

import org.primefaces.event.NodeCollapseEvent;
import org.primefaces.event.NodeExpandEvent;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.NodeUnselectEvent;
import org.primefaces.facelets.MethodRule;

public class TreeHandler extends ComponentHandler {

	public TreeHandler(ComponentConfig config) {
		super(config);
	}
	
	@SuppressWarnings("unchecked")
	protected MetaRuleset createMetaRuleset(Class type) { 
		MetaRuleset metaRuleset = super.createMetaRuleset(type); 
		
		metaRuleset.addRule(new MethodRule("nodeSelectListener", null, new Class[]{NodeSelectEvent.class}));
        metaRuleset.addRule(new MethodRule("nodeUnselectListener", null, new Class[]{NodeUnselectEvent.class}));
		metaRuleset.addRule(new MethodRule("nodeExpandListener", null, new Class[]{NodeExpandEvent.class}));
		metaRuleset.addRule(new MethodRule("nodeCollapseListener", null, new Class[]{NodeCollapseEvent.class}));
        metaRuleset.addRule(new MethodRule("dragdropListener", null, new Class[]{DragDropEvent.class}));
		
		return metaRuleset; 
	} 
}