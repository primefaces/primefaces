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
package org.primefaces.component.behavior.ajax;

import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.el.ELContext;
import javax.el.ELException;
import javax.el.MethodExpression;
import javax.el.ValueExpression;
import javax.faces.FacesException;
import javax.faces.application.ResourceDependencies;
import javax.faces.application.ResourceDependency;
import javax.faces.component.UIComponentBase;
import javax.faces.component.behavior.ClientBehaviorBase;
import javax.faces.component.behavior.ClientBehaviorHint;
import javax.faces.context.FacesContext;
import org.primefaces.component.api.AjaxSource;

@ResourceDependencies({
	@ResourceDependency(library="primefaces", name="jquery/jquery.js"),
	@ResourceDependency(library="primefaces", name="primefaces.js")
})
public class AjaxBehavior extends ClientBehaviorBase implements AjaxSource {

    private String update;
    private String process;
    private Boolean global;
    private Boolean async;
    private String oncomplete;
    private String onerror;
    private String onsuccess;
    private String onstart;
    private MethodExpression listener;
    private Boolean immediate;
    private Boolean disabled;
    private Boolean partialSubmit;
    private boolean partialSubmitSet = false;
    
    public final static String BEHAVIOR_ID = "org.primefaces.component.AjaxBehavior";

    private static final Set<ClientBehaviorHint> HINTS = Collections.unmodifiableSet(EnumSet.of(ClientBehaviorHint.SUBMITTING));

    private Map<String, ValueExpression> bindings;

    @Override
    public String getRendererType() {
        return "org.primefaces.component.AjaxBehaviorRenderer";
    }
    
    @Override
    public Set<ClientBehaviorHint> getHints() {
        return HINTS;
    }
    
    public boolean isAsync() {
        Boolean result = (Boolean) eval("async", async);
        
        return ((result != null) ? result : false);
    }
    public void setAsync(boolean async) {
        this.async = async;
        
        clearInitialState();
    }

    public boolean isGlobal() {
        Boolean result = (Boolean) eval("global", global);
        
        return ((result != null) ? result : true);
    }
    public void setGlobal(boolean global) {
        this.global = global;
        
        clearInitialState();
    }

    public String getOncomplete() {
        return (String) eval("oncomplete", oncomplete);
    }
    public void setOncomplete(String oncomplete) {
        this.oncomplete = oncomplete;

        clearInitialState();
    }

    public String getOnstart() {
        return (String) eval("onstart", onstart);
    }

    public void setOnstart(String onstart) {
        this.onstart = onstart;

        clearInitialState();
    }

    public String getOnsuccess() {
        return (String) eval("onsuccess", onsuccess);
    }
    public void setOnsuccess(String onsuccess) {
        this.onsuccess = onsuccess;
        
        clearInitialState();
    }

    public String getOnerror() {
        return (String) eval("onerror", onerror);
    }
    public void setOnerror(String onerror) {
        this.onerror = onerror;
        
        clearInitialState();
    }

    public String getProcess() {
        return (String) eval("process", process);
    }
    public void setProcess(String process) {
        this.process = process;
        
        clearInitialState();
    }

    public String getUpdate() {
        return (String) eval("update", update);
    }
    public void setUpdate(String update) {
        this.update = update;

        clearInitialState();
    }

    public MethodExpression getListener() {
        return listener;
    }
    public void setListener(MethodExpression listener) {
        this.listener = listener;
    }

    public boolean isDisabled() {
        Boolean result = (Boolean) eval("disabled", disabled);
        
        return ((result != null) ? result : false);
    }
    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
        
        clearInitialState();
    }

    public boolean isImmediate() {
        Boolean result = (Boolean) eval("immediate", immediate);
        
        return ((result != null) ? result : false);
    }
    public void setImmediate(Boolean immediate) {
        this.immediate = immediate;
        
        clearInitialState();
    }
    
    public boolean isImmediateSet() {
        return ((immediate != null) || (getValueExpression("immediate") != null));
    }
    
    public boolean isPartialSubmit() {
        Boolean result = (Boolean) eval("partialSubmit", partialSubmit);
        
        return ((result != null) ? result : false);
    }
    public void setPartialSubmit(boolean partialSubmit) {
        this.partialSubmit = partialSubmit;
        this.partialSubmitSet = true;
        
        clearInitialState();
    }
    
    public boolean isPartialSubmitSet() {
        return this.partialSubmitSet || (this.getValueExpression("partialSubmit") != null);
    }

    protected Object eval(String propertyName, Object value) {
        if(value != null) {
            return value;
        }

        ValueExpression expression = getValueExpression(propertyName);
        if(expression != null) {
            return expression.getValue(FacesContext.getCurrentInstance().getELContext());
        }

        return null;
    }
    
    public ValueExpression getValueExpression(String name) {
        if(name == null) {
            throw new IllegalArgumentException();
        }

        return ((bindings == null) ? null : bindings.get(name));
    }
    
    public void setValueExpression(String name, ValueExpression expr) {
        if(name == null) {
            throw new IllegalArgumentException();
        }

        if(expr != null) {
            if(expr.isLiteralText()) {
                setLiteralValue(name, expr);
            } else {
                if(bindings == null) {
                    bindings = new HashMap<String, ValueExpression>(6,1.0f);
                }

                bindings.put(name, expr);
            }
        } 
        else {
            if(bindings != null) {
                bindings.remove(name);
                if(bindings.isEmpty()) {
                    bindings = null;
                }
            }
        }

        clearInitialState();
    }
    
    private void setLiteralValue(String propertyName, ValueExpression expression) {
        Object value;
        ELContext context = FacesContext.getCurrentInstance().getELContext();

        try {
            value = expression.getValue(context);
        } 
        catch (ELException eLException) {
            throw new FacesException(eLException);
        }
        
        if("update".equals(propertyName)) {
            update = (String)value;
        } else if ("process".equals(propertyName)) {
            process = (String)value;
        } else if ("oncomplete".equals(propertyName)) {
            oncomplete = (String)value;
        } else if ("onerror".equals(propertyName)) {
            onerror = (String)value;
        } else if ("onsuccess".equals(propertyName)) {
            onsuccess = (String)value;
        } else if ("onstart".equals(propertyName)) {
            onstart = (String)value;
        }else if ("immediate".equals(propertyName)) {
            immediate = (Boolean)value;
        } else if ("disabled".equals(propertyName)) {
            disabled = (Boolean)value;
        } else if ("async".equals(propertyName)) {
            async = (Boolean)value;
        } else if ("global".equals(propertyName)) {
            global = (Boolean)value;
        } else if ("partialSubmit".equals(propertyName)) {
            partialSubmit = (Boolean)value;
            this.partialSubmitSet = true;
        }
    }
    
    @Override
    public Object saveState(FacesContext context) {
        Object[] values;

        Object superState = super.saveState(context);

        if(initialStateMarked()) {
            if(superState == null)
                values = null;
            else
                values = new Object[] {superState};
        } 
        else {
            values = new Object[14];
      
            values[0] = superState;
            values[1] = onstart;
            values[2] = onerror;
            values[3] = onsuccess;
            values[4] = oncomplete;
            values[5] = disabled;
            values[6] = immediate;
            values[7] = process;
            values[8] = update;
            values[9] = async;
            values[10] = global;
            values[11] = partialSubmit;
            values[12] = listener;
            values[13] = saveBindings(context, bindings);
        }

        return values;
    }

    @Override
    public void restoreState(FacesContext context, Object state) {
        if(state != null) {
            Object[] values = (Object[]) state;
            super.restoreState(context, values[0]);

            if(values.length != 1) {
                onstart = (String) values[1];
                onerror = (String) values[2];
                onsuccess = (String) values[3];
                oncomplete = (String) values[4];
                disabled = (Boolean) values[5];
                immediate = (Boolean) values[6];
                process = (String) values[7];
                update = (String) values[8];
                async = (Boolean) values[9];
                global = (Boolean) values[10];
                partialSubmit = (Boolean) values[11];
                listener = (MethodExpression) values[12];
                bindings = restoreBindings(context, values[13]);

                clearInitialState();
            }
        }
    }

    
    private Object saveBindings(FacesContext context, Map<String, ValueExpression> bindings) {
        if(bindings == null) {
            return null;
        }

        Object values[] = new Object[2];
        values[0] = bindings.keySet().toArray(new String[bindings.size()]);

        Object[] bindingValues = bindings.values().toArray();
        for (int i = 0; i < bindingValues.length; i++) {
            bindingValues[i] = UIComponentBase.saveAttachedState(context, bindingValues[i]);
        }

        values[1] = bindingValues;

        return values;
    }

    private Map<String, ValueExpression> restoreBindings(FacesContext context, Object state) {
        if(state == null) {
            return null;
        }
        
        Object values[] = (Object[]) state;
        String names[] = (String[]) values[0];
        Object states[] = (Object[]) values[1];
        Map<String, ValueExpression> bindings = new HashMap<String, ValueExpression>(names.length);
        
        for (int i = 0; i < names.length; i++) {
            bindings.put(names[i], (ValueExpression) UIComponentBase.restoreAttachedState(context, states[i]));
        }
        return bindings;
    }
    
    public void addAjaxBehaviorListener(AjaxBehaviorListenerImpl listener) {
        addBehaviorListener(listener);
    }

    public void removeAjaxBehaviorListener(AjaxBehaviorListenerImpl listener) {
        removeBehaviorListener(listener);
    }
}