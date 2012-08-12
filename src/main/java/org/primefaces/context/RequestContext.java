/*
 * Copyright 2009-2012 Prime Teknoloji.
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
package org.primefaces.context;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import javax.faces.context.FacesContext;
import org.primefaces.util.Constants;

public abstract class RequestContext {

    public static RequestContext getCurrentInstance() {
        return (RequestContext) FacesContext.getCurrentInstance().getAttributes().get(Constants.REQUEST_CONTEXT_ATTR);
    }

    public abstract boolean isAjaxRequest();

    public abstract void addCallbackParam(String name, Object value);

    public abstract Map<String, Object> getCallbackParams();

    public abstract List<String> getScriptsToExecute();

    public abstract void execute(String script);
    
    public abstract void scrollTo(String clientId);
    
    public abstract void update(String name);

    public abstract void update(Collection<String> collection);
    
    public abstract void reset(String id);
    
    public abstract void reset(Collection<String> ids);
}
