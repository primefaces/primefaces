/*
 * Copyright 2009-2013 PrimeTek.
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
package org.primefaces.component.ajaxexceptionhandler;

import javax.faces.component.UIComponent;
import javax.faces.component.visit.VisitCallback;
import javax.faces.component.visit.VisitContext;
import javax.faces.component.visit.VisitResult;
import org.primefaces.util.ComponentUtils;

/**
 * {@link VisitCallback} which collects all {@link UIAjaxExceptionHandler}s.
 */
public class UIAjaxExceptionHandlerVisitCallback implements VisitCallback {

    private final Throwable throwable;
    
    private UIAjaxExceptionHandler handler;
    private UIAjaxExceptionHandler defaultHandler;

    public UIAjaxExceptionHandlerVisitCallback(Throwable throwable) {
        this.throwable = throwable;
        
        this.handler = null;
        this.defaultHandler = null;
    }
    
	public VisitResult visit(VisitContext context, UIComponent target) {;

        if (target instanceof UIAjaxExceptionHandler) {
            UIAjaxExceptionHandler currentHandler = (UIAjaxExceptionHandler) target;

            if (ComponentUtils.isValueBlank(currentHandler.getType())) {
                defaultHandler = currentHandler;
            }

            if (throwable.getClass().getName().equals(currentHandler.getType())) {
                handler = currentHandler;
                return VisitResult.COMPLETE;
            }
        }
        
        return VisitResult.ACCEPT;
	}
    
    public UIAjaxExceptionHandler getHandler() {
        return handler == null ? defaultHandler : handler;
    }
}
