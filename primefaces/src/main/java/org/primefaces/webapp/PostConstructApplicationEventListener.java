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
package org.primefaces.webapp;

import org.primefaces.config.PrimeEnvironment;
import org.primefaces.config.StartupPrimeEnvironment;
import org.primefaces.expression.FormSearchKeywordResolver;
import org.primefaces.expression.ObserverSearchKeywordResolver;
import org.primefaces.expression.PfsSearchKeywordResolver;
import org.primefaces.expression.RowSearchKeywordResolver;
import org.primefaces.expression.WidgetVarSearchKeywordResolver;

import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.faces.context.FacesContext;
import jakarta.faces.event.AbortProcessingException;
import jakarta.faces.event.SystemEvent;
import jakarta.faces.event.SystemEventListener;

public class PostConstructApplicationEventListener implements SystemEventListener {

    private static final Logger LOGGER = Logger.getLogger(PostConstructApplicationEventListener.class.getName());

    @Override
    public boolean isListenerForSource(Object source) {
        return true;
    }

    @Override
    public void processEvent(SystemEvent event) throws AbortProcessingException {
        // temp manually instantiate as the ApplicationContext is not available yet
        PrimeEnvironment environment = new StartupPrimeEnvironment();

        LOGGER.log(Level.INFO,
                "Running on PrimeFaces {0}",
                environment.getBuildVersion());

        FacesContext context = event.getFacesContext();
        context.getApplication().addSearchKeywordResolver(new FormSearchKeywordResolver());
        context.getApplication().addSearchKeywordResolver(new WidgetVarSearchKeywordResolver());
        context.getApplication().addSearchKeywordResolver(new ObserverSearchKeywordResolver());
        context.getApplication().addSearchKeywordResolver(new PfsSearchKeywordResolver());
        context.getApplication().addSearchKeywordResolver(new RowSearchKeywordResolver());
    }
}
