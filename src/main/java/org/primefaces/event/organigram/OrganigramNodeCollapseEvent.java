/**
 * Copyright 2009-2018 PrimeTek.
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
package org.primefaces.event.organigram;

import javax.faces.component.UIComponent;
import javax.faces.component.behavior.Behavior;
import org.primefaces.model.OrganigramNode;

/**
 * Event class for the "collapse" event.
 */
public class OrganigramNodeCollapseEvent extends AbstractOrganigramNodeEvent {

    private static final long serialVersionUID = 1L;

    public OrganigramNodeCollapseEvent(UIComponent component, Behavior behavior, OrganigramNode organigramNode) {
        super(component, behavior, organigramNode);
    }
}
