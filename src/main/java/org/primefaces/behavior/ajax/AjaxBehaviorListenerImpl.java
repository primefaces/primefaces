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

import javax.el.MethodExpression;
import javax.el.MethodNotFoundException;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.AjaxBehaviorListener;
import javax.faces.view.facelets.FaceletContext;
import javax.faces.view.facelets.TagAttribute;

public class AjaxBehaviorListenerImpl implements AjaxBehaviorListener, Serializable {

    private static final Logger LOG = Logger.getLogger(AjaxBehaviorListenerImpl.class.getName());
    private static final Class[] EMPTY_PARAMS = new Class[]{};

    private MethodExpression defaultListener;

    // required by serialization
    public AjaxBehaviorListenerImpl() {}

    public AjaxBehaviorListenerImpl(TagAttribute listenerAttribute, FaceletContext context) {
        this.defaultListener = listenerAttribute.getMethodExpression(context, null, EMPTY_PARAMS);
    }

    public void processAjaxBehavior(AjaxBehaviorEvent event) throws AbortProcessingException {
        FacesContext context = FacesContext.getCurrentInstance();

        // default listener first... (without arguments)
        try {
            processDefaultListener(context);
        }
        catch (MethodNotFoundException mnfe) {
            // custom listener... (e.g. AutoCompleteEvent)
            try {
                processCustomListener(context, event);
            }
            // arg listener... (AjaxBehaviorEvent)
            catch (MethodNotFoundException imnfe) {
                processArgListener(context, event);
            }
            catch (IllegalArgumentException iiae) {
                processArgListener(context, event);
            }
        }
        catch (IllegalArgumentException iae) {
            // custom listener... (e.g. AutoCompleteEvent)
            try {
                processCustomListener(context, event);
            }
            // arg listener... (AjaxBehaviorEvent)
            catch (MethodNotFoundException imnfe) {
                processArgListener(context, event);
            }
            catch (IllegalArgumentException iiae) {
                processArgListener(context, event);
            }
        }
    }

    protected void processDefaultListener(FacesContext context) {
    	if (LOG.isLoggable(Level.FINE)) {
            LOG.fine("Try to invoke defaultListener: " + defaultListener.getExpressionString());
    	}

        defaultListener.invoke(context.getELContext(), EMPTY_PARAMS);
    }

    protected void processArgListener(FacesContext context, AjaxBehaviorEvent event) {
        MethodExpression listener = context.getApplication().getExpressionFactory().createMethodExpression(
                context.getELContext(), defaultListener.getExpressionString(), null, new Class[]{ AjaxBehaviorEvent.class });

    	if (LOG.isLoggable(Level.FINE)) {
            LOG.fine("Try to invoke argListener: " + listener.getExpressionString());
    	}

        listener.invoke(context.getELContext(), new Object[]{ event });
    }

    protected void processCustomListener(FacesContext context, AjaxBehaviorEvent event) {
    	MethodExpression listener = context.getApplication().getExpressionFactory().createMethodExpression(
                context.getELContext(), defaultListener.getExpressionString(), null, new Class[]{ event.getClass() });

    	if (LOG.isLoggable(Level.FINE)) {
            LOG.fine("Try to invoke customListener: " + listener.getExpressionString());
    	}

        listener.invoke(context.getELContext(), new Object[]{ event });
    }
}