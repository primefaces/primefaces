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
package org.primefaces.component.autoupdate;

import java.io.IOException;

import javax.el.ELException;
import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.event.PostAddToViewEvent;
import javax.faces.event.PreRenderComponentEvent;
import javax.faces.view.facelets.*;

public class AutoUpdateTagHandler extends TagHandler {

    private static final AutoUpdateListener LISTENER = new AutoUpdateListener(false);
    private static final AutoUpdateListener LISTENER_DISABLED = new AutoUpdateListener(true);

    private final TagAttribute disabledAttribute;

    public AutoUpdateTagHandler(TagConfig tagConfig) {
        super(tagConfig);

        disabledAttribute = getAttribute("disabled");
    }

    @Override
    public void apply(FaceletContext faceletContext, UIComponent parent) throws IOException, FacesException, FaceletException, ELException {

        boolean disabled = false;
        if (disabledAttribute != null) {
            disabled = disabledAttribute.getBoolean(faceletContext);
        }

        // PostAddToViewEvent should work for stateless views
        //                  but fails for MyFaces ViewPooling
        //                  and sometimes on postbacks as PostAddToViewEvent should actually ony be called once
        parent.subscribeToEvent(PostAddToViewEvent.class, disabled ? LISTENER_DISABLED : LISTENER);

        // PreRenderComponentEvent should work for normal cases and MyFaces ViewPooling
        //                      but likely fails for stateless view as we save the clientIds in the viewRoot
        parent.subscribeToEvent(PreRenderComponentEvent.class, disabled ? LISTENER_DISABLED : LISTENER);
    }
}
