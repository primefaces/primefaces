/*
 * The MIT License
 *
 * Copyright (c) 2009-2026 PrimeTek Informatics
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
package org.primefaces.behavior.ajax;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.el.ELContext;
import jakarta.el.MethodExpression;
import jakarta.el.MethodNotFoundException;
import jakarta.el.PropertyNotFoundException;
import jakarta.faces.context.FacesContext;
import jakarta.faces.event.AbortProcessingException;
import jakarta.faces.event.AjaxBehaviorEvent;
import jakarta.faces.event.AjaxBehaviorListener;

public class AjaxBehaviorListenerImpl implements AjaxBehaviorListener, Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(AjaxBehaviorListenerImpl.class.getName());

    private MethodExpression listener;
    private MethodExpression listenerWithArg;
    private MethodExpression listenerWithCustomArg;

    // required by serialization
    public AjaxBehaviorListenerImpl() {
    }

    public AjaxBehaviorListenerImpl(MethodExpression listener, MethodExpression listenerWithArg) {
        this(listener, listenerWithArg, null);
    }

    public AjaxBehaviorListenerImpl(MethodExpression listener, MethodExpression listenerWithArg, MethodExpression listenerWithCustomArg) {
        this.listener = listener;
        this.listenerWithArg = listenerWithArg;
        this.listenerWithCustomArg = listenerWithCustomArg;
    }

    @Override
    public void processAjaxBehavior(AjaxBehaviorEvent event) throws AbortProcessingException {
        FacesContext context = FacesContext.getCurrentInstance();
        final ELContext elContext = context.getELContext();

        if (LOGGER.isLoggable(Level.FINE)) {
            LOGGER.fine("Try to invoke listener: " + listener.getExpressionString());
        }

        try {
            listener.invoke(elContext, new Object[]{});
        }
        // catch MethodNotFoundException and IllegalArgumentException by spec
        // catch ArrayIndexOutOfBoundsException as JBoss hack, see #1375
        // catch PropertyNotFoundException as Mojarra hack for Composite Components, see #7819
        catch (MethodNotFoundException | IllegalArgumentException | ArrayIndexOutOfBoundsException | PropertyNotFoundException e) {
            processArgListener(context, elContext, event);
        }
    }

    private void processArgListener(FacesContext context, ELContext elContext, AjaxBehaviorEvent event) throws AbortProcessingException {
        if (LOGGER.isLoggable(Level.FINE)) {
            LOGGER.fine("Try to invoke listenerWithArg: " + listenerWithArg.getExpressionString());
        }

        try {
            listenerWithArg.invoke(elContext, new Object[]{event});
        }
        // catch MethodNotFoundException and IllegalArgumentException by spec
        // catch ArrayIndexOutOfBoundsException as JBoss hack, see #1375
        // catch PropertyNotFoundException as Mojarra hack for Composite Components, see #7819
        catch (MethodNotFoundException | IllegalArgumentException | ArrayIndexOutOfBoundsException | PropertyNotFoundException e) {
            processCustomArgListener(context, elContext, event);
        }
    }

    private void processCustomArgListener(FacesContext context, ELContext elContext, AjaxBehaviorEvent event) throws AbortProcessingException {

        if (listenerWithCustomArg == null) {

            MethodExpression argListener = context.getApplication().getExpressionFactory().
                    createMethodExpression(elContext, listener.getExpressionString(), Void.class, new Class[]{event.getClass()});

            if (LOGGER.isLoggable(Level.FINE)) {
                LOGGER.fine("Try to invoke customListener: " + argListener.getExpressionString());
            }

            argListener.invoke(elContext, new Object[]{event});
        }
        else {
            if (LOGGER.isLoggable(Level.FINE)) {
                LOGGER.fine("Try to invoke customListener: " + listenerWithCustomArg.getExpressionString());
            }

            listenerWithCustomArg.invoke(elContext, new Object[]{event});
        }
    }
}
