/*
 * The MIT License
 *
 * Copyright (c) 2009-2026 PrimeFaces
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.primefaces.behavior.callback;

import org.primefaces.cdk.api.FacesBehaviorInfo;
import org.primefaces.util.LangUtils;

import jakarta.faces.component.behavior.ClientBehaviorContext;
import jakarta.faces.component.behavior.FacesBehavior;

import org.json.JSONObject;

@FacesBehavior(CallbackBehavior.BEHAVIOR_ID)
@FacesBehaviorInfo(description = "Callback executes JavaScript on an event of the component instead of an AJAX request like p:ajax.")
public class CallbackBehavior extends CallbackBehaviorBaseImpl {

    public static final String BEHAVIOR_ID = "org.primefaces.behavior.CallbackBehavior";

    @Override
    public String getScript(ClientBehaviorContext behaviorContext) {
        String execute = getExecute();
        if (isDisabled() || LangUtils.isBlank(execute)) {
            return null;
        }

        String source = behaviorContext.getSourceId();
        if (source == null) {
            source = behaviorContext.getComponent().getClientId(behaviorContext.getFacesContext());
        }

        // ext is only defined if the widget calls the behavior, not in a DOM event handler (e.g. onclick)
        return "return (function(source,event,params,ext){" + execute + "\n}).call(this,"
                + JSONObject.quote(source) + ","
                + JSONObject.quote(behaviorContext.getEventName()) + ","
                + "typeof ext!=='undefined'&&ext&&ext.params||null,"
                + "typeof ext!=='undefined'&&ext||null);";
    }
}
