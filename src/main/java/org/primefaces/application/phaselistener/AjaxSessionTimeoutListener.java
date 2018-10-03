/*
 * Copyright 2018 Fryske Akademy.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.primefaces.application.phaselistener;

import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import javax.servlet.DispatcherType;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class provides the possibility to deal with session timeouts and ajax
 * requests in a user friendly way. Typically you would write a timeout.xhtml in
 * which you inform the user and provide a link or meta refresh to a protected
 * page, override {@link #getTimeoutPath(javax.servlet.http.HttpServletRequest)
 * } and declare as phase-listener in faces-config. This is how it works:
 * <ol>
 * <li>ajax request encounters invalid session</li>
 * <li>this listener detects {@link DispatcherType#FORWARD} to {@link #getLoginPath()
 * }</li>
 * <li>{@link #handleAjaxAfterTimeout(javax.faces.context.FacesContext, javax.servlet.http.HttpServletRequest)
 * } is called</li>
 * <li>In this method a redirect is done to {@link #getTimeoutPath(javax.servlet.http.HttpServletRequest) },
 * which should be a <strong>protected</strong> page, possibly via a non-protected page using
 * meta refresh and showing some friendly message</li>
 * <li>your security setup redirects to the login page</li>
 * <li>after login you land on the page from step 4</li>
 * </ol>
 * declare this phase-listener in your faces-config
 *
 * @author eduard
 */
public class AjaxSessionTimeoutListener implements PhaseListener {

    private static final Logger LOGGER = Logger.getLogger(AjaxSessionTimeoutListener.class.getName());

    public static final String LOGINPATH = "/login.xhtml";

    /**
     * @return {@link #LOGINPATH}
     */
    protected String getLoginPath() {
        return LOGINPATH;
    }

    /**
     * If this is a {@link DispatcherType#FORWARD} to {@link #getLoginPath() }
     * and the response hasn't been rendered yet, call {@link #handleAjaxAfterTimeout(javax.faces.context.FacesContext, javax.servlet.http.HttpServletRequest)
     * }.
     *
     * @param event
     */
    @Override
    public void beforePhase(final PhaseEvent event) {
        final FacesContext facesContext = event.getFacesContext();
        if (!facesContext.getPartialViewContext().isAjaxRequest() || facesContext.getRenderResponse()) {
            if (facesContext.getRenderResponse() && LOGGER.isLoggable(Level.FINE)) {
                final HttpServletRequest request = HttpServletRequest.class.cast(facesContext.getExternalContext().getRequest());
                LOGGER.fine(String.format("response rendered, not handling %s", request.getServletPath()));
            }
            return;
        }

        final HttpServletRequest request = HttpServletRequest.class.cast(facesContext.getExternalContext().getRequest());
        if (request.getDispatcherType() == DispatcherType.FORWARD && getLoginPath().equals(request.getServletPath())) { // isLoginRedirection()
            try {
                handleAjaxAfterTimeout(facesContext, request);
            } catch (final IOException e) {
                LOGGER.log(Level.SEVERE, "unable to redirect", e);
            }
        }
    }

    /**
     * redirect to {@link #getTimeoutPath(javax.servlet.http.HttpServletRequest)
     * }.
     *
     * @param facesContext
     * @param request the original ajax request
     * @throws java.io.IOException
     */
    protected void handleAjaxAfterTimeout(FacesContext facesContext, HttpServletRequest request) throws IOException {
        facesContext.getExternalContext().redirect(getTimeoutPath(request));
    }

    /**
     * This implementation returns to the context path root, you may choose to
     * override and redirect to a "timeout.xhtml" where you provide users some
     * info and options
     *
     * @return
     */
    protected String getTimeoutPath(HttpServletRequest request) {
        return request.getContextPath();
    }

    @Override
    public void afterPhase(final PhaseEvent event) {
    }

    @Override
    public PhaseId getPhaseId() {
        return PhaseId.RESTORE_VIEW;
    }
}
