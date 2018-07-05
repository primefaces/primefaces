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
package org.primefaces.event.map;

import java.util.List;
import javax.faces.component.UIComponent;
import javax.faces.component.behavior.Behavior;
import org.primefaces.event.AbstractAjaxBehaviorEvent;
import org.primefaces.model.map.GeocodeResult;

public class GeocodeEvent extends AbstractAjaxBehaviorEvent {

    private final String query;
    private final List<GeocodeResult> results;

    public GeocodeEvent(UIComponent component, Behavior behavior, String query, List<GeocodeResult> results) {
        super(component, behavior);
        this.query = query;
        this.results = results;
    }

    public String getQuery() {
        return query;
    }

    public List<GeocodeResult> getResults() {
        return results;
    }
}
