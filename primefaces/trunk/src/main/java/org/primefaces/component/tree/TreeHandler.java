package org.primefaces.component.tree;

import java.util.List;

import javax.faces.view.facelets.ComponentConfig;
import javax.faces.view.facelets.ComponentHandler;
import javax.faces.view.facelets.MetaRuleset;

import org.primefaces.event.NodeCollapseEvent;
import org.primefaces.event.NodeExpandEvent;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.facelets.MethodRule;

public class TreeHandler extends ComponentHandler {

	public TreeHandler(ComponentConfig config) {
		super(config);
	}
	
	@SuppressWarnings("unchecked")
	protected MetaRuleset createMetaRuleset(Class type) { 
		MetaRuleset metaRuleset = super.createMetaRuleset(type); 
		Class[] selectEventClasses = new Class[]{NodeSelectEvent.class};
		Class[] expandEventClasses = new Class[]{NodeExpandEvent.class};
		Class[] collapseEventClasses = new Class[]{NodeCollapseEvent.class};
		
		metaRuleset.addRule(new MethodRule("nodeSelectListener", List.class, selectEventClasses));
		metaRuleset.addRule(new MethodRule("nodeExpandListener", List.class, expandEventClasses));
		metaRuleset.addRule(new MethodRule("nodeCollapseListener", List.class, collapseEventClasses));
		
		return metaRuleset; 
	} 
}