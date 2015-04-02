/*
 * Copyright 2009-2012 PrimeTek.
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
package org.primefaces.model.menu;

import java.util.List;
import java.util.Map;
import org.primefaces.component.api.Confirmable;

public interface MenuItem extends MenuElement, Confirmable {
        
    public String getIcon();
    
    public String getIconPos();
    
    public String getTitle();
    
    public boolean shouldRenderChildren();
    
    public boolean isDisabled();
    
    public String getOnclick();
    
    public String getStyle();
    
    public String getStyleClass();
    
    public String getUrl();
    
    public String getTarget();
    
    public String getOutcome();
    
    public String getFragment();

    public boolean isIncludeViewParams();
    
    public boolean isAjax();
    
    public Object getValue();
    
    public void setStyleClass(String styleClass);
    
    public Map<String, List<String>> getParams();
    
    public void setParam(String key, Object value);
    
    public boolean isDynamic();
    
    public String getCommand();
    
    public boolean isImmediate();
    
    public String getClientId();
    
    public String getContainerStyle();
    
    public String getContainerStyleClass();
}
