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
package org.primefaces.component.resetinput;

import java.io.IOException;

import javax.el.ELException;
import javax.el.ValueExpression;
import javax.faces.FacesException;
import javax.faces.component.ActionSource;
import javax.faces.component.UIComponent;
import javax.faces.view.facelets.*;

public class ResetInputTagHandler extends TagHandler {

    private final TagAttribute target;
    private final TagAttribute clearModel;

    public ResetInputTagHandler(TagConfig tagConfig) {
        super(tagConfig);
        target = getRequiredAttribute("target");
        clearModel = getAttribute("clearModel");
    }

    @Override
    public void apply(FaceletContext faceletContext, UIComponent parent) throws IOException, FacesException, FaceletException, ELException {
        if (parent == null || !ComponentHandler.isNew(parent)) {
            return;
        }

        if (parent instanceof ActionSource) {
            ValueExpression targetVE = target.getValueExpression(faceletContext, Object.class);
            ValueExpression clearModelVE = clearModel != null ? clearModel.getValueExpression(faceletContext, Boolean.class) : null;

            ActionSource actionSource = (ActionSource) parent;
            actionSource.addActionListener(new ResetInputActionListener(targetVE, clearModelVE));
        }
        else {
            throw new TagException(tag, "ResetInput can only be attached to ActionSource components.");
        }
    }

}
