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
package org.primefaces.component.collector;

import java.io.IOException;

import javax.el.ELException;
import javax.el.ValueExpression;
import javax.faces.FacesException;
import javax.faces.component.ActionSource;
import javax.faces.component.UIComponent;
import javax.faces.view.facelets.*;

public class CollectorTagHandler extends TagHandler {

    private final TagAttribute addTo;
    private final TagAttribute removeFrom;
    private final TagAttribute value;
    private final TagAttribute unique;

    public CollectorTagHandler(TagConfig tagConfig) {
        super(tagConfig);
        addTo = getAttribute("addTo");
        removeFrom = getAttribute("removeFrom");
        value = getRequiredAttribute("value");
        unique = getAttribute("unique");
    }

    @Override
    public void apply(FaceletContext faceletContext, UIComponent parent) throws IOException, FacesException, FaceletException, ELException {
        if (ComponentHandler.isNew(parent)) {
            ValueExpression addToVE = null;
            ValueExpression removeFromVE = null;
            ValueExpression uniqueVE = null;

            if (addTo != null) {
                addToVE = addTo.getValueExpression(faceletContext, Object.class);
            }

            if (removeFrom != null) {
                removeFromVE = removeFrom.getValueExpression(faceletContext, Object.class);
            }

            if (unique != null) {
                uniqueVE = unique.getValueExpression(faceletContext, Object.class);
            }

            ValueExpression valueVE = value.getValueExpression(faceletContext, Object.class);

            ActionSource actionSource = (ActionSource) parent;
            actionSource.addActionListener(new Collector(addToVE, removeFromVE, valueVE, uniqueVE));
        }
    }
}
