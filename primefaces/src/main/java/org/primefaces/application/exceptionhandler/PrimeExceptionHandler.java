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
package org.primefaces.application.exceptionhandler;

import org.primefaces.component.ajaxexceptionhandler.AjaxExceptionHandler;
import org.primefaces.component.ajaxexceptionhandler.AjaxExceptionHandlerVisitCallback;
import org.primefaces.config.PrimeConfiguration;
import org.primefaces.context.PrimeApplicationContext;
import org.primefaces.context.PrimeRequestContext;
import org.primefaces.csp.CspPhaseListener;
import org.primefaces.expression.SearchExpressionUtils;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.EscapeUtils;
import org.primefaces.util.LangUtils;
import org.primefaces.util.Lazy;
import org.primefaces.util.LimitedSizeHashMap;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import jakarta.el.ELException;
import jakarta.faces.FacesException;
import jakarta.faces.application.ProjectStage;
import jakarta.faces.application.ViewExpiredException;
import jakarta.faces.application.ViewHandler;
import jakarta.faces.component.UIComponent;
import jakarta.faces.component.UIViewRoot;
import jakarta.faces.component.visit.VisitContext;
import jakarta.faces.context.ExceptionHandler;
import jakarta.faces.context.ExceptionHandlerWrapper;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.faces.context.PartialResponseWriter;
import jakarta.faces.event.ExceptionQueuedEvent;
import jakarta.faces.event.PhaseId;
import jakarta.faces.lifecycle.ClientWindow;
import jakarta.faces.view.ViewDeclarationLanguage;

public class PrimeExceptionHandler extends ExceptionHandlerWrapper {

    private static final Logger LOGGER = Logger.getLogger(PrimeExceptionHandler.class.getName());
    private static final String DATE_FORMAT_PATTERN = "yyyy-MM-dd HH:mm:ss";
    private static final Pattern NEWLINE_PATTERN = Pattern.compile("(\r\n|\n)");

    private final Lazy<PrimeConfiguration> config;

    public PrimeExceptionHandler(ExceptionHandler wrapped) {
        super(wrapped);
        this.config = new Lazy<>(() -> PrimeApplicationContext.getCurrentInstance(FacesContext.getCurrentInstance()).getConfig());
    }

    @Override
    public void handle() throws FacesException {
        FacesContext context = FacesContext.getCurrentInstance();

        if (context == null || context.getResponseComplete()) {
            return;
        }

        Iterable<ExceptionQueuedEvent> exceptionQueuedEvents = getUnhandledExceptionQueuedEvents();
        if (exceptionQueuedEvents != null && exceptionQueuedEvents.iterator() != null) {
            Iterator<ExceptionQueuedEvent> unhandledExceptionQueuedEvents = getUnhandledExceptionQueuedEvents().iterator();

            if (unhandledExceptionQueuedEvents.hasNext()) {
                try {
                    Throwable throwable = unhandledExceptionQueuedEvents.next().getContext().getException();

                    unhandledExceptionQueuedEvents.remove();

                    Throwable rootCause = getRootCause(throwable);
                    ExceptionInfo info = createExceptionInfo(rootCause);

                    // print exception in development stage
                    if (context.getApplication().getProjectStage() == ProjectStage.Development) {
                        rootCause.printStackTrace();
                    }

                    if (isLogException(context, rootCause)) {
                        logException(rootCause);
                    }

                    if (context.getPartialViewContext().isAjaxRequest()) {
                        handleAjaxException(context, rootCause, info);
                    }
                    else {
                        handleRedirect(context, rootCause, info, false);
                    }
                }
                catch (Exception ex) {
                    LOGGER.log(Level.SEVERE, "Could not handle exception!", ex);
                }
            }

            while (unhandledExceptionQueuedEvents.hasNext()) {
                // Any remaining unhandled exceptions are not interesting. First fix the first.
                unhandledExceptionQueuedEvents.next();
                unhandledExceptionQueuedEvents.remove();
            }
        }
    }

    protected void logException(Throwable rootCause) {
        LOGGER.log(Level.SEVERE, rootCause.getMessage(), rootCause);
    }

    protected boolean isLogException(FacesContext context, Throwable rootCause) {

        if (context.isProjectStage(ProjectStage.Production)) {
            if (rootCause instanceof ViewExpiredException) {
                return false;
            }

            if (rootCause != null) {
                for (String ignore : config.get().getExceptionTypesToIgnoreInLogging()) {
                    if (ignore.trim().equals(rootCause.getClass().getName())) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    @Override
    public Throwable getRootCause(Throwable throwable) {
        while ((ELException.class.isInstance(throwable) || FacesException.class.isInstance(throwable)) && throwable.getCause() != null) {
            throwable = throwable.getCause();
        }

        return throwable;
    }

    protected void handleAjaxException(FacesContext context, Throwable rootCause, ExceptionInfo info) throws IOException {
        ExternalContext externalContext = context.getExternalContext();
        PartialResponseWriter writer = context.getPartialViewContext().getPartialResponseWriter();

        // should not happen actually
        if (writer == null) {
            return;
        }

        CspPhaseListener.initCsp(context, config.get().isCsp(), config.get().isPolicyProvided(),
                config.get().getCspReportOnlyPolicy(), config.get().getCspPolicy());

        boolean isResponseReset = false;

        //mojarra workaround to avoid invalid partial output due to open tags
        if (context.getCurrentPhaseId().equals(PhaseId.RENDER_RESPONSE)) {
            if (!externalContext.isResponseCommitted()) {
                // this doesn't flush, just clears the internal state in mojarra
                writer.flush();

                writer.endCDATA();

                writer.endInsert();
                writer.endUpdate();

                writer.startError("");
                writer.endError();

                writer.getWrapped().endElement("changes");
                writer.getWrapped().endElement("partial-response");


                String characterEncoding = externalContext.getResponseCharacterEncoding();
                externalContext.responseReset();
                externalContext.setResponseCharacterEncoding(characterEncoding);

                isResponseReset = true;
            }
        }

        AjaxExceptionHandler handlerComponent = null;

        try {
            rootCause = buildView(context, rootCause, rootCause);
            handlerComponent = findHandlerComponent(context, rootCause);
        }
        catch (Exception ex) {
            LOGGER.log(Level.WARNING, "Could not build view or lookup a AjaxExceptionHandler component!", ex);
        }

        context.getAttributes().put(ExceptionInfo.ATTRIBUTE_NAME, info);

        // redirect if no AjaxExceptionHandler available
        if (handlerComponent == null) {
            handleRedirect(context, rootCause, info, isResponseReset);
        }
        // handle custom update / onexception callback
        else {
            externalContext.addResponseHeader("Content-Type", "text/xml; charset=" + externalContext.getResponseCharacterEncoding());
            externalContext.addResponseHeader("Cache-Control", "no-cache");
            externalContext.setResponseContentType("text/xml");

            writer.startDocument();

            if (LangUtils.isNotBlank(handlerComponent.getOnexception())) {
                StringBuilder sb = new StringBuilder();
                sb.append("var hf=function(type,message,timestampp){");
                sb.append(handlerComponent.getOnexception());
                sb.append("};hf.call(this,\"");
                sb.append(info.getType());
                sb.append("\",\"");
                sb.append(EscapeUtils.forJavaScript(info.getMessage()));
                sb.append("\",\"");
                sb.append(info.getFormattedTimestamp());
                sb.append("\");");

                PrimeRequestContext.getCurrentInstance(context).getScriptsToExecute().add(sb.toString());
            }

            if (LangUtils.isNotBlank(handlerComponent.getUpdate())) {
                List<UIComponent> updates = SearchExpressionUtils.contextlessResolveComponents(context, handlerComponent, handlerComponent.getUpdate());

                if (updates != null && !updates.isEmpty()) {
                    context.setResponseWriter(writer);

                    for (int i = 0; i < updates.size(); i++) {
                        UIComponent component = updates.get(i);

                        writer.startUpdate(component.getClientId(context));
                        component.encodeAll(context);
                        writer.endUpdate();
                    }
                }
            }

            writer.endDocument();

            context.responseComplete();
        }
    }

    protected ExceptionInfo createExceptionInfo(Throwable rootCause) throws IOException {
        ExceptionInfo info = new ExceptionInfo();
        info.setException(rootCause);
        info.setMessage(rootCause.getMessage());
        info.setStackTrace(rootCause.getStackTrace());
        info.setTimestamp(LocalDateTime.now());
        info.setType(rootCause.getClass().getName());

        try (StringWriter sw = new StringWriter(); PrintWriter pw = new PrintWriter(sw)) {
            rootCause.printStackTrace(pw);
            info.setFormattedStackTrace(NEWLINE_PATTERN.matcher(EscapeUtils.forXml(sw.toString())).replaceAll("<br/>"));
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMAT_PATTERN);
        info.setFormattedTimestamp(info.getTimestamp().format(formatter));

        return info;
    }

    /**
     * Finds the proper {@link AjaxExceptionHandler} for the given {@link Throwable}.
     *
     * @param context The {@link FacesContext}.
     * @param rootCause The occurred {@link Throwable}.
     * @return The {@link AjaxExceptionHandler} or <code>null</code>.
     */
    protected AjaxExceptionHandler findHandlerComponent(FacesContext context, Throwable rootCause) {
        AjaxExceptionHandlerVisitCallback visitCallback = new AjaxExceptionHandlerVisitCallback(rootCause);

        context.getViewRoot().visitTree(
                VisitContext.createVisitContext(context, null, ComponentUtils.VISIT_HINTS_SKIP_ITERATION.get()), visitCallback);

        Map<String, AjaxExceptionHandler> handlers = visitCallback.getHandlers();

        // get handler by exception type
        AjaxExceptionHandler handler = handlers.get(rootCause.getClass().getName());

        // lookup by inheritance hierarchy
        if (handler == null) {
            Class<?> throwableClass = rootCause.getClass();
            while (handler == null && throwableClass.getSuperclass() != Object.class) {
                throwableClass = throwableClass.getSuperclass();
                handler = handlers.get(throwableClass.getName());
            }
        }

        // get default handler
        if (handler == null) {
            handler = handlers.get(null);
        }

        return handler;
    }

    /**
     * Builds the view if not already available. This is mostly required for ViewExpiredException's.
     *
     * @param context The {@link FacesContext}.
     * @param throwable The occurred {@link Throwable}.
     * @param rootCause The root cause.
     * @return The unwrapped {@link Throwable}.
     * @throws java.io.IOException If building the view fails.
     */
    protected Throwable buildView(FacesContext context, Throwable throwable, Throwable rootCause) throws IOException {
        if (context.getViewRoot() == null) {
            ViewHandler viewHandler = context.getApplication().getViewHandler();

            String viewId = viewHandler.deriveViewId(context, ComponentUtils.calculateViewId(context));
            ViewDeclarationLanguage vdl = viewHandler.getViewDeclarationLanguage(context, viewId);
            UIViewRoot viewRoot = vdl.createView(context, viewId);
            context.setViewRoot(viewRoot);

            vdl.buildView(context, viewRoot);

            // Workaround for Mojarra
            // if UIViewRoot == null -> 'IllegalArgumentException' is thrown instead of 'ViewExpiredException'
            if (rootCause == null && throwable instanceof IllegalArgumentException) {
                rootCause = new jakarta.faces.application.ViewExpiredException(viewId);
            }
        }

        return rootCause;
    }

    protected void handleRedirect(FacesContext context, Throwable rootCause, ExceptionInfo info, boolean isResponseReset) throws IOException {
        ExternalContext externalContext = context.getExternalContext();
        if (externalContext.getSession(false) != null) {
            ClientWindow clientWindow = externalContext.getClientWindow();
            if (clientWindow != null && LangUtils.isNotBlank(clientWindow.getId())) {
                Map<String, ExceptionInfo> windowsMap = (Map<String, ExceptionInfo>)
                        externalContext.getSessionMap().computeIfAbsent(ExceptionInfo.ATTRIBUTE_NAME + "_map", k -> new LimitedSizeHashMap<>(5));
                windowsMap.put(clientWindow.getId(), info);
            }
            else {
                externalContext.getSessionMap().put(ExceptionInfo.ATTRIBUTE_NAME, info);
            }
        }

        Map<String, String> errorPages = PrimeApplicationContext.getCurrentInstance(context).getConfig().getErrorPages();

        String errorPage = evaluateErrorPage(errorPages, rootCause);
        String url = constructRedirectUrl(context, errorPage);

        // workaround for mojarra -> mojarra doesn't reset PartialResponseWriter#inChanges if we call externalContext#resetResponse
        if (isResponseReset && context.getPartialViewContext().isAjaxRequest()) {
            PartialResponseWriter writer = context.getPartialViewContext().getPartialResponseWriter();
            externalContext.addResponseHeader("Content-Type", "text/xml; charset=" + externalContext.getResponseCharacterEncoding());
            externalContext.addResponseHeader("Cache-Control", "no-cache");
            externalContext.setResponseContentType("text/xml");

            writer.write("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
            writer.startElement("partial-response", null);
            writer.startElement("redirect", null);
            writer.writeAttribute("url", url, null);
            writer.endElement("redirect");
            writer.endElement("partial-response");
        }
        else {
            // workaround for IllegalStateException from redirect of committed response
            if (externalContext.isResponseCommitted() && !context.getPartialViewContext().isAjaxRequest()) {
                PartialResponseWriter writer = context.getPartialViewContext().getPartialResponseWriter();
                writer.startElement("script", null);
                writer.write("window.location.href = '" + url + "';");
                writer.endElement("script");
                writer.getWrapped().endDocument();
            }
            else {
                externalContext.redirect(url);
            }
        }

        context.responseComplete();
    }

    protected String constructRedirectUrl(FacesContext facesContext, String errorPage) {
        String url = facesContext.getExternalContext().getRequestContextPath() + errorPage;

        url = facesContext.getApplication().evaluateExpressionGet(facesContext, url, String.class);
        url = facesContext.getExternalContext().encodeActionURL(url);

        return url;
    }

    protected String evaluateErrorPage(Map<String, String> errorPages, Throwable rootCause) {

        // get error page by exception type
        String errorPage = errorPages.get(rootCause.getClass().getName());

        // lookup by inheritance hierarchy
        if (errorPage == null) {
            Class<?> throwableClass = rootCause.getClass();
            while (errorPage == null && throwableClass.getSuperclass() != Object.class) {
                throwableClass = throwableClass.getSuperclass();
                errorPage = errorPages.get(throwableClass.getName());
            }
        }

        // get default error page
        if (errorPage == null) {
            errorPage = errorPages.get(null);
        }

        if (errorPage == null) {
            throw new IllegalArgumentException(
                    "No default error page (Status 500 or java.lang.Throwable) and no error page for type \"" + rootCause.getClass() + "\" defined!");
        }

        return errorPage;
    }
}
