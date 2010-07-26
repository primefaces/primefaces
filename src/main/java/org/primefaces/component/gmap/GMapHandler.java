/*
 * Copyright 2010 Prime Technology.
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
package org.primefaces.component.gmap;

import javax.faces.view.facelets.ComponentConfig;
import javax.faces.view.facelets.ComponentHandler;
import javax.faces.view.facelets.MetaRuleset;

import org.primefaces.event.map.MarkerDragEvent;
import org.primefaces.event.map.OverlaySelectEvent;
import org.primefaces.event.map.PointSelectEvent;
import org.primefaces.event.map.StateChangeEvent;
import org.primefaces.facelets.MethodRule;

public class GMapHandler extends ComponentHandler {

	public GMapHandler(ComponentConfig config) {
		super(config);
	}
	
	@SuppressWarnings("unchecked")
	protected MetaRuleset createMetaRuleset(Class type) { 
		MetaRuleset metaRuleset = super.createMetaRuleset(type); 
		
		metaRuleset.addRule(new MethodRule("overlaySelectListener", null, new Class[]{OverlaySelectEvent.class}));
		metaRuleset.addRule(new MethodRule("stateChangeListener", null, new Class[]{StateChangeEvent.class}));
		metaRuleset.addRule(new MethodRule("pointSelectListener", null, new Class[]{PointSelectEvent.class}));
		metaRuleset.addRule(new MethodRule("markerDragListener", null, new Class[]{MarkerDragEvent.class}));
		
		return metaRuleset; 
	} 
}