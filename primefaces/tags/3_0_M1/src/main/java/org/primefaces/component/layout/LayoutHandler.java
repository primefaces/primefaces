/*
 * Copyright 2009 Prime Technology.
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
package org.primefaces.component.layout;

import javax.faces.view.facelets.ComponentConfig;
import javax.faces.view.facelets.ComponentHandler;
import javax.faces.view.facelets.MetaRuleset;

import org.primefaces.event.CloseEvent;
import org.primefaces.event.ResizeEvent;
import org.primefaces.event.ToggleEvent;
import org.primefaces.facelets.MethodRule;

public class LayoutHandler extends ComponentHandler {

	public LayoutHandler(ComponentConfig config) {
		super(config);
	}
	
	@SuppressWarnings("unchecked")
	protected MetaRuleset createMetaRuleset(Class type) { 
		MetaRuleset metaRuleset = super.createMetaRuleset(type); 
		
		metaRuleset.addRule(new MethodRule("toggleListener", null, new Class[]{ToggleEvent.class}));
		metaRuleset.addRule(new MethodRule("closeListener", null, new Class[]{CloseEvent.class}));
		metaRuleset.addRule(new MethodRule("resizeListener", null, new Class[]{ResizeEvent.class}));
		
		return metaRuleset;  
	} 
}