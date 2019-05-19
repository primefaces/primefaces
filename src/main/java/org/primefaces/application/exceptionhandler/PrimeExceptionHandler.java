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
package org.primefaces.application.exceptionhandler;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.el.ELException;
import javax.faces.FacesException;
import javax.faces.application.ProjectStage;
import javax.faces.application.ViewExpiredException;
import javax.faces.application.ViewHandler;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.component.visit.VisitContext;
import javax.faces.context.ExceptionHandler;
import javax.faces.context.ExceptionHandlerWrapper;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.PartialResponseWriter;
import javax.faces.event.ExceptionQueuedEvent;
import javax.faces.event.PhaseId;
import javax.faces.view.ViewDeclarationLanguage;
import org.primefaces.component.ajaxexceptionhandler.AjaxExceptionHandler;
import org.primefaces.component.ajaxexceptionhandler.AjaxExceptionHandlerVisitCallback;
import org.primefaces.context.PrimeApplicationContext;
import org.primefaces.expression.SearchExpressionFacade;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.LangUtils;
import org.primefaces.util.EscapeUtils;

public class PrimeExceptionHandler extends ExceptionHandlerWrapper {

    private static final Logger LOGGER = Logger.getLogger(PrimeExceptionHandler.class.getName());
    private static final String DATE_FORMAT_PATTERN = "yyyy-MM-dd HH:mm:ss";

    private final ExceptionHandler wrapped;

    @SuppressWarnings("deprecation") // the default constructor is deprecated in JSF 2.3
    public PrimeExceptionHandler(ExceptionHandler wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public ExceptionHandler getWrapped() {
        return wrapped;
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

    protected void handleAjaxException(FacesContext context, Throwable rootCause, ExceptionInfo info) throws Exception {
        ExternalContext externalContext = context.getExternalContext();
        PartialResponseWriter writer = context.getPartialViewContext().getPartialResponseWriter();

        boolean responseResetted = false;

        if (context.getCurrentPhaseId().equals(PhaseId.RENDER_RESPONSE)) {
            if (!externalContext.isResponseCommitted()) {
                //mojarra workaround to avoid invalid partial output due to open tags
                if (writer != null) {
                    // this doesn't flush, just clears the internal state in mojarra
                    writer.flush();

                    writer.endCDATA();

                    writer.endInsert();
                    writer.endUpdate();

                    writer.startError("");
                    writer.endError();

                    writer.getWrapped().endElement("changes");
                    writer.getWrapped().endElement("partial-response");
                }

                String characterEncoding = externalContext.getResponseCharacterEncoding();
                externalContext.responseReset();
                externalContext.setResponseCharacterEncoding(characterEncoding);

                responseResetted = true;
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
            handleRedirect(context, rootCause, info, responseResetted);
        }
        // handle custom update / onexception callback
        else {
            externalContext.addResponseHeader("Content-Type", "text/xml; charset=" + externalContext.getResponseCharacterEncoding());
            externalContext.addResponseHeader("Cache-Control", "no-cache");
            externalContext.setResponseContentType("text/xml");

            writer.startDocument();
            writer.startElement("changes", null);

            if (!LangUtils.isValueBlank(handlerComponent.getUpdate())) {
                List<UIComponent> updates = SearchExpressionFacade.resolveComponents(context, handlerComponent, handlerComponent.getUpdate());

                if (updates != null && !updates.isEmpty()) {
                    context.setResponseWriter(writer);

                    for (int i = 0; i < updates.size(); i++) {
                        UIComponent component = updates.get(i);

                        writer.startElement("update", null);
                        writer.writeAttribute("id", component.getClientId(context), null);
                        writer.startCDATA();

                        component.encodeAll(context);

                        writer.endCDATA();
                        writer.endElement("update");
                    }
                }
            }

            if (!LangUtils.isValueBlank(handlerComponent.getOnexception())) {
                writer.startElement("eval", null);
                writer.startCDATA();

                writer.write("var hf=function(type,message,timestampp){");
                writer.write(handlerComponent.getOnexception());
                writer.write("};hf.call(this,\""
                        + info.getType() + "\",\""
                        + EscapeUtils.forJavaScript(info.getMessage())
                        + "\",\""
                        + info.getFormattedTimestamp()
                        + "\");");

                writer.endCDATA();
                writer.endElement("eval");
            }

            writer.endElement("changes");
            writer.endDocument();

            context.responseComplete();
        }
    }

    protected ExceptionInfo createExceptionInfo(Throwable rootCause) throws IOException {
        ExceptionInfo info = new ExceptionInfo();
        info.setException(rootCause);
        info.setMessage(rootCause.getMessage());
        info.setStackTrace(rootCause.getStackTrace());
        info.setTimestamp(new Date());
        info.setType(rootCause.getClass().getName());

        try (StringWriter sw = new StringWriter()) {
            PrintWriter pw = new PrintWriter(sw);
            rootCause.printStackTrace(pw);
            info.setFormattedStackTrace(EscapeUtils.forXml(sw.toString()).replaceAll("(\r\n|\n)", "<br/>"));
            pw.close();
        }

        SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT_PATTERN);
        info.setFormattedTimestamp(format.format(info.getTimestamp()));

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

        context.getViewRoot().visitTree(VisitContext.createVisitContext(context), visitCallback);

        Map<String, AjaxExceptionHandler> handlers = visitCallback.getHandlers();

        // get handler by exception type
        AjaxExceptionHandler handler = handlers.get(rootCause.getClass().getName());

        // lookup by inheritance hierarchy
        if (handler == null) {
            Class throwableClass = rootCause.getClass();
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
            // if UIViewRoot == null -> 'IllegalArgumentException' is throwed instead of 'ViewExpiredException'
            if (rootCause == null && throwable instanceof IllegalArgumentException) {
                rootCause = new javax.faces.application.ViewExpiredException(viewId);
            }
        }

        return rootCause;
    }

    protected void handleRedirect(FacesContext context, Throwable rootCause, ExceptionInfo info, boolean responseResetted) throws IOException {
        ExternalContext externalContext = context.getExternalContext();
        externalContext.getSessionMap().put(ExceptionInfo.ATTRIBUTE_NAME, info);

        Map<String, String> errorPages = PrimeApplicationContext.getCurrentInstance(context).getConfig().getErrorPages();
        String errorPage = evaluateErrorPage(errorPages, rootCause);

        String url = externalContext.getRequestContextPath() + errorPage;

        // workaround for mojarra -> mojarra doesn't reset PartialResponseWriter#inChanges if we call externalContext#resetResponse
        if (responseResetted && context.getPartialViewContext().isAjaxRequest()) {
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

    protected String evaluateErrorPage(Map<String, String> errorPages, Throwable rootCause) {

        // get error page by exception type
        String errorPage = errorPages.get(rootCause.getClass().getName());

        // lookup by inheritance hierarchy
        if (errorPage == null) {
            Class throwableClass = rootCause.getClass();
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
