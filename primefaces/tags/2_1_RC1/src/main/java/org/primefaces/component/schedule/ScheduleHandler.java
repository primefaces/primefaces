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
package org.primefaces.component.schedule;

import javax.faces.view.facelets.ComponentConfig;
import javax.faces.view.facelets.ComponentHandler;
import javax.faces.view.facelets.MetaRuleset;

import org.primefaces.event.DateSelectEvent;
import org.primefaces.event.ScheduleEntryMoveEvent;
import org.primefaces.event.ScheduleEntryResizeEvent;
import org.primefaces.event.ScheduleEntrySelectEvent;

import org.primefaces.facelets.MethodRule;

public class ScheduleHandler extends ComponentHandler { 

	public ScheduleHandler(ComponentConfig config) {
		super(config);
	}
	
	@SuppressWarnings("unchecked")
	protected MetaRuleset createMetaRuleset(Class type) { 
		MetaRuleset metaRuleset = super.createMetaRuleset(type); 

		metaRuleset.addRule(new MethodRule("eventSelectListener", null, new Class[]{ScheduleEntrySelectEvent.class}));
		metaRuleset.addRule(new MethodRule("dateSelectListener", null, new Class[]{DateSelectEvent.class}));
		metaRuleset.addRule(new MethodRule("eventMoveListener", null, new Class[]{ScheduleEntryMoveEvent.class}));
		metaRuleset.addRule(new MethodRule("eventResizeListener", null, new Class[]{ScheduleEntryResizeEvent.class}));
		
		return metaRuleset; 
	} 	
}