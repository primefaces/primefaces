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
package org.primefaces.application;

import java.util.Map;
import javax.faces.application.ViewHandler;
import javax.faces.application.ViewHandlerWrapper;
import javax.faces.context.FacesContext;
import org.primefaces.util.Constants;

public class DialogViewHandler extends ViewHandlerWrapper {

    private ViewHandler wrapped;

    public DialogViewHandler(ViewHandler wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public ViewHandler getWrapped() {
        return this.wrapped;
    }

    @Override
    public String getActionURL(FacesContext context, String viewId) {
        Map<String, String> params = context.getExternalContext().getRequestParameterMap();
        String pfdlgcid = params.get(Constants.DIALOG_FRAMEWORK.CONVERSATION_PARAM);
        String url = super.getActionURL(context, viewId);

        if (url.contains(Constants.DIALOG_FRAMEWORK.CONVERSATION_PARAM)) {
            return url;
        }
        else {
            if (pfdlgcid == null) {
                return url;
            }
            else {
                if (url.indexOf('?') == -1) {
                    return url + "?pfdlgcid=" + pfdlgcid;
                }
                else {
                    return url + "&pfdlgcid=" + pfdlgcid;
                }
            }
        }
    }
}
