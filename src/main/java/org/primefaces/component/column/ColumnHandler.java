package org.primefaces.component.column;

import javax.faces.view.facelets.ComponentConfig;
import javax.faces.view.facelets.ComponentHandler;
import javax.faces.view.facelets.MetaRule;
import javax.faces.view.facelets.MetaRuleset;

import org.primefaces.facelets.MethodRule;

public class ColumnHandler extends ComponentHandler {

	public ColumnHandler(ComponentConfig config) {
		super(config);
	}
	
	@SuppressWarnings("unchecked")
	protected MetaRuleset createMetaRuleset(Class type) { 
		MetaRuleset metaRuleset = super.createMetaRuleset(type); 
		Class[] paramList = new Class[]{Object.class, Object.class}; 
		
		MetaRule metaRule = new MethodRule("sortFunction", Integer.class, paramList); 
		metaRuleset.addRule(metaRule);
		
		return metaRuleset; 
	} 
}