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
package org.primefaces.component.ajaxexceptionhandler;

import javax.faces.component.UIComponent;
import javax.faces.component.visit.VisitCallback;
import javax.faces.component.visit.VisitContext;
import javax.faces.component.visit.VisitResult;
import org.primefaces.util.ComponentUtils;

/**
 * {@link VisitCallback} which collects all {@link AjaxExceptionHandler}s.
 */
public class AjaxExceptionHandlerVisitCallback implements VisitCallback {

    private final Throwable throwable;
    
    private AjaxExceptionHandler handler;
    private AjaxExceptionHandler defaultHandler;

    public AjaxExceptionHandlerVisitCallback(Throwable throwable) {
        this.throwable = throwable;
        
        this.handler = null;
        this.defaultHandler = null;
    }
    
    public VisitResult visit(VisitContext context, UIComponent target) {;

        if (target instanceof AjaxExceptionHandler) {
            AjaxExceptionHandler currentHandler = (AjaxExceptionHandler) target;

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
    
    public AjaxExceptionHandler getHandler() {
        return handler == null ? defaultHandler : handler;
    }
}
