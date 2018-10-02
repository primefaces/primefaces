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
package org.primefaces.component.ajaxexceptionhandler;

import java.util.HashMap;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.component.visit.VisitCallback;
import javax.faces.component.visit.VisitContext;
import javax.faces.component.visit.VisitResult;

import org.primefaces.component.api.UIData;
import org.primefaces.util.LangUtils;

/**
 * {@link VisitCallback} which collects all {@link AjaxExceptionHandler}s.
 */
public class AjaxExceptionHandlerVisitCallback implements VisitCallback {

    private final Throwable throwable;

    private final Map<String, AjaxExceptionHandler> handlers;

    public AjaxExceptionHandlerVisitCallback(Throwable throwable) {
        this.throwable = throwable;

        handlers = new HashMap<>();
    }

    @Override
    public VisitResult visit(VisitContext context, UIComponent target) {

        if (target instanceof AjaxExceptionHandler) {
            AjaxExceptionHandler currentHandler = (AjaxExceptionHandler) target;

            if (LangUtils.isValueBlank(currentHandler.getType())) {
                handlers.put(null, currentHandler);
            }
            else {
                handlers.put(currentHandler.getType(), currentHandler);

                // exact type matched - we don't need to search more generic handlers
                if (throwable.getClass().getName().equals(currentHandler.getType())) {
                    return VisitResult.COMPLETE;
                }
            }
        }
        else if (target instanceof UIData) {
            return VisitResult.REJECT;
        }

        return VisitResult.ACCEPT;
    }

    public Map<String, AjaxExceptionHandler> getHandlers() {
        return handlers;
    }
}
