/**
 * The MIT License
 *
 * Copyright (c) 2009-2019 PrimeTek
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
package org.primefaces.component.ajaxexceptionhandler;

import java.util.HashMap;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.component.UIData;
import javax.faces.component.visit.VisitCallback;
import javax.faces.component.visit.VisitContext;
import javax.faces.component.visit.VisitResult;

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
