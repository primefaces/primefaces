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

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.el.ELContext;
import javax.el.MethodExpression;
import javax.el.MethodNotFoundException;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.AjaxBehaviorListener;

public class AjaxBehaviorListenerImpl implements AjaxBehaviorListener, Serializable {

    private static Logger LOG = Logger.getLogger(AjaxBehaviorListenerImpl.class.getName());

    private MethodExpression listener;
    private MethodExpression listenerWithArg;

    // required by serialization
    public AjaxBehaviorListenerImpl() {}

    public AjaxBehaviorListenerImpl(MethodExpression listener, MethodExpression listenerWithArg) {
        this.listener = listener;
        this.listenerWithArg = listenerWithArg;
    }

    public void processAjaxBehavior(AjaxBehaviorEvent event) throws AbortProcessingException {
        FacesContext context = FacesContext.getCurrentInstance();
        final ELContext elContext = context.getELContext();

    	if (LOG.isLoggable(Level.FINE)) {
            LOG.fine("Try to invoke listener: " + listener.getExpressionString());
    	}

        try {
            listener.invoke(elContext, new Object[]{});
        }
        catch (MethodNotFoundException mnfe) {
            processArgListener(context, elContext, event);
        }
        catch (IllegalArgumentException iae) {
            processArgListener(context, elContext, event);
        }
    }

    private void processArgListener(FacesContext context, ELContext elContext, AjaxBehaviorEvent event) throws AbortProcessingException {
    	if (LOG.isLoggable(Level.FINE)) {
            LOG.fine("Try to invoke listenerWithArg: " + listenerWithArg.getExpressionString());
    	}

    	try {
            listenerWithArg.invoke(elContext , new Object[]{event});
        }
        catch (MethodNotFoundException mnfe) {
            processCustomListener(context, elContext, event);
        }
        catch (IllegalArgumentException e) {
            processCustomListener(context, elContext, event);
        }
    }

    private void processCustomListener(FacesContext context, ELContext elContext, AjaxBehaviorEvent event) throws AbortProcessingException {
    	MethodExpression argListener = context.getApplication().getExpressionFactory().
                    createMethodExpression(elContext, listener.getExpressionString(), null, new Class[]{event.getClass()});

    	if (LOG.isLoggable(Level.FINE)) {
            LOG.fine("Try to invoke customListener: " + argListener.getExpressionString());
    	}

        argListener.invoke(elContext, new Object[]{event});
    }
}