/*
 * Copyright 2009-2016 PrimeTek.
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
package org.primefaces.component.timeline;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseListener;
import javax.faces.view.facelets.ComponentConfig;
import javax.faces.view.facelets.ComponentHandler;
import javax.faces.view.facelets.FaceletContext;
import javax.faces.view.facelets.TagException;

public class TimelineTagHandler extends ComponentHandler {

	public TimelineTagHandler(ComponentConfig config) {
		super(config);
	}

	@Override
	public void onComponentPopulated(FaceletContext ctx, UIComponent c, UIComponent parent) {
		if (!ComponentHandler.isNew(parent)) {
			return;
		}

		UIViewRoot root = getViewRoot(ctx, parent);
		if (root == null) {
			throw new TagException(this.tag, "UIViewRoot is not available");
		}

		FacesContext fc = ctx.getFacesContext();
		Timeline timeline = (Timeline) c;
		String widgetVar = timeline.resolveWidgetVar();

		@SuppressWarnings("unchecked")
		Map<String, TimelineUpdater> map = (Map<String, TimelineUpdater>) fc.getAttributes().get(TimelineUpdater.class.getName());
		if (map == null) {
			map = new HashMap<String, TimelineUpdater>();
			fc.getAttributes().put(TimelineUpdater.class.getName(), map);
		}

		DefaultTimelineUpdater timelineUpdater = new DefaultTimelineUpdater();
		timelineUpdater.setWidgetVar(widgetVar);

		map.put(widgetVar, timelineUpdater);

		if (parent instanceof UIViewRoot) {
			List<PhaseListener> listeners = root.getPhaseListeners();
			if (!listeners.contains(timelineUpdater)) {
				root.addPhaseListener(timelineUpdater);
			}
		} else {
			root.addPhaseListener(timelineUpdater);
		}
        
        timeline.setParentComponent(parent);
	}

	private UIViewRoot getViewRoot(FaceletContext ctx, UIComponent parent) {
		UIComponent c = parent;
		do {
			if (c instanceof UIViewRoot) {
				return (UIViewRoot) c;
			} else {
				c = c.getParent();
			}
		} while (c != null);

		return ctx.getFacesContext().getViewRoot();
	}
}
