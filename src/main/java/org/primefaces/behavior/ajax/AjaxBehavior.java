/*
 * Copyright 2009-2014 PrimeTek.
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
package org.primefaces.behavior.ajax;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

import javax.el.MethodExpression;
import javax.faces.application.ResourceDependencies;
import javax.faces.application.ResourceDependency;
import javax.faces.component.behavior.ClientBehaviorHint;
import javax.faces.event.AjaxBehaviorListener;

import org.primefaces.behavior.base.AbstractBehavior;
import org.primefaces.component.api.AjaxSource;

@ResourceDependencies({
	@ResourceDependency(library="primefaces", name="jquery/jquery.js"),
    @ResourceDependency(library="primefaces", name="jquery/jquery-plugins.js"),
	@ResourceDependency(library="primefaces", name="primefaces.js")
})
public class AjaxBehavior extends AbstractBehavior implements AjaxSource {

    public final static String BEHAVIOR_ID = "org.primefaces.component.AjaxBehavior";
    
    private static final Set<ClientBehaviorHint> HINTS = Collections.unmodifiableSet(EnumSet.of(ClientBehaviorHint.SUBMITTING));
	
    public enum PropertyKeys {
    	update(String.class),
    	process(String.class),
    	global(Boolean.class),
    	async(Boolean.class),
    	oncomplete(String.class),
    	onerror(String.class),
    	onsuccess(String.class),
    	onstart(String.class),
    	listener(MethodExpression.class),
    	immediate(Boolean.class),
    	disabled(Boolean.class),
    	partialSubmit(Boolean.class),
    	resetValues(Boolean.class),
    	ignoreAutoUpdate(Boolean.class),
    	delay(String.class),
        timeout(Integer.class),
        partialSubmitFilter(String.class),
        form(String.class);

        final Class<?> expectedType;

        PropertyKeys(Class<?> expectedType) {
            this.expectedType = expectedType;
        }
    }

    public AjaxBehavior() {
        super();
    }

    @Override
    public String getRendererType() {
        return "org.primefaces.component.AjaxBehaviorRenderer";
    }
    
    @Override
    public Set<ClientBehaviorHint> getHints() {
        return HINTS;
    }

    
    public boolean isDisabled() {
        return eval(PropertyKeys.disabled, Boolean.FALSE);
    }

    public void setDisabled(boolean disabled) {
        setLiteral(PropertyKeys.disabled, disabled);
    }    

    public boolean isAsync() {
    	return eval(PropertyKeys.async, Boolean.FALSE);
    }

    public void setAsync(boolean async) {
    	setLiteral(PropertyKeys.async, async);
    }

    public boolean isGlobal() {
    	return eval(PropertyKeys.global, Boolean.TRUE);
    }

    public void setGlobal(boolean global) {
    	setLiteral(PropertyKeys.global, global);
    }

    public String getOncomplete() {
        return eval(PropertyKeys.oncomplete, null);
    }

    public void setOncomplete(String oncomplete) {
    	setLiteral(PropertyKeys.oncomplete, oncomplete);
    }

    public String getOnstart() {
    	return eval(PropertyKeys.onstart, null);
    }

    public void setOnstart(String onstart) {
    	setLiteral(PropertyKeys.onstart, onstart);
    }

    public String getOnsuccess() {
    	return eval(PropertyKeys.onsuccess, null);
    }

    public void setOnsuccess(String onsuccess) {
    	setLiteral(PropertyKeys.onsuccess, onsuccess);
    }

    public String getOnerror() {
    	return eval(PropertyKeys.onerror, null);
    }

    public void setOnerror(String onerror) {
    	setLiteral(PropertyKeys.onerror, onerror);
    }

    public String getProcess() {
    	return eval(PropertyKeys.process, null);
    }

    public void setProcess(String process) {
    	setLiteral(PropertyKeys.process, process);
    }

    public String getUpdate() {
    	return eval(PropertyKeys.update, null);
    }

    public void setUpdate(String update) {
    	setLiteral(PropertyKeys.update, update);
    }

    public String getDelay() {
    	return eval(PropertyKeys.delay, null);
    }

    public void setDelay(String delay) {
    	setLiteral(PropertyKeys.delay, delay);
    }

    public boolean isImmediate() {
    	return eval(PropertyKeys.immediate, Boolean.FALSE);
    }

    public void setImmediate(Boolean immediate) {
    	setLiteral(PropertyKeys.immediate, immediate);
    }
    
    public boolean isIgnoreAutoUpdate() {
    	return eval(PropertyKeys.ignoreAutoUpdate, Boolean.FALSE);
    }
 
    public void setIgnoreAutoUpdate(boolean ignoreAutoUpdate) {
    	setLiteral(PropertyKeys.ignoreAutoUpdate, ignoreAutoUpdate);
    }
    
    public boolean isPartialSubmit() {
    	return eval(PropertyKeys.partialSubmit, Boolean.FALSE);
    }

    public void setPartialSubmit(boolean partialSubmit) {
    	setLiteral(PropertyKeys.partialSubmit, partialSubmit);
    }
    
    public boolean isResetValues() {
    	return eval(PropertyKeys.resetValues, Boolean.FALSE);
    }

    public void setResetValues(boolean resetValues) {
    	setLiteral(PropertyKeys.resetValues, resetValues);
    }

    public MethodExpression getListener() {
    	return eval(PropertyKeys.listener, null);
    }

    public void setListener(MethodExpression listener) {
    	setLiteral(PropertyKeys.listener, listener);
    }

    public int getTimeout() {
        return eval(PropertyKeys.timeout, 0);
    }

    public void setTimeout(int timeout) {
    	setLiteral(PropertyKeys.timeout, timeout);
    }
    
    public String getPartialSubmitFilter() {
        return eval(PropertyKeys.partialSubmitFilter, null);
    }
    
    public void setPartialSubmitFilter(String partialSubmitFilter) {
        setLiteral(PropertyKeys.partialSubmitFilter, partialSubmitFilter);
    }
    
    public String getForm() {
        return eval(PropertyKeys.form, null);
    }
    
    public void setForm(String form) {
        setLiteral(PropertyKeys.form, form);
    }
    
    public boolean isImmediateSet() {
        return isAttributeSet(PropertyKeys.immediate);
    }
    
    public boolean isPartialSubmitSet() {
    	return isAttributeSet(PropertyKeys.partialSubmit);
    }

    public boolean isResetValuesSet() {
    	return isAttributeSet(PropertyKeys.resetValues);
    }
    

    public boolean isAjaxified() {
        return true;
    }


    @Override
    protected Enum<?>[] getAllProperties() {
            return PropertyKeys.values();
    }


    public void addAjaxBehaviorListener(AjaxBehaviorListener listener) {
        addBehaviorListener(listener);
    }

    public void removeAjaxBehaviorListener(AjaxBehaviorListener listener) {
        removeBehaviorListener(listener);

    }
}