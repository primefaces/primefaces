/**
 * Copyright 2009-2017 PrimeTek.
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
package org.primefaces.context;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import org.primefaces.PrimeFaces;

import org.primefaces.util.AjaxRequestBuilder;
import org.primefaces.util.CSVBuilder;
import org.primefaces.util.StringEncrypter;
import org.primefaces.util.WidgetBuilder;

/**
 * A RequestContext is a helper class consisting of several utilities.
 * RequestContext is thread-safe and scope is same as FacesContext.
 * Current instance can be retrieved as;
 * <blockquote>
 *  RequestContext.getCurrentInstance(context);
 * </blockquote>
 */
public abstract class RequestContext {

    private static final String INSTANCE_KEY = RequestContext.class.getName();

    public static RequestContext getCurrentInstance() {
        return getCurrentInstance(FacesContext.getCurrentInstance());
    }

    public static RequestContext getCurrentInstance(FacesContext facesContext) {
        if (facesContext != null && !facesContext.isReleased()) {
            return (RequestContext) facesContext.getAttributes().get(INSTANCE_KEY);
        }

        return null;
    }

    public static void setCurrentInstance(RequestContext context, FacesContext facesContext) {
        if (context == null) {
            if (facesContext != null) {
                facesContext.getAttributes().remove(INSTANCE_KEY);
            }
        }
        else {
            facesContext.getAttributes().put(INSTANCE_KEY, context);
        }
    }

    /**
     * Use {@link PrimeFaces#isAjaxRequest()}
     * 
     * @return
     * @deprecated Use {@link PrimeFaces#isAjaxRequest()}
     */
    @Deprecated
    public abstract boolean isAjaxRequest();

    /**
     * Use PrimeFaces.ajax().addCallbackParam
     * 
     * @deprecated Use PrimeFaces.ajax().addCallbackParam
     */
    @Deprecated
    public abstract void addCallbackParam(String name, Object value);

    /**
     * @return all callback parameters added in the current request.
     */
    public abstract Map<String, Object> getCallbackParams();

    /**
     * @return all scripts added in the current request.
     */
    public abstract List<String> getScriptsToExecute();

    /**
     * Use {@link PrimeFaces#executeScript(java.lang.String)}
     * 
     * @param script
     * @deprecated  Use {@link PrimeFaces#executeScript(java.lang.String)}
     */
    @Deprecated
    public abstract void execute(String script);

    /**
     * Use {@link PrimeFaces#scrollTo(java.lang.String)}
     * 
     * @param clientId
     * @deprecated Use {@link PrimeFaces#scrollTo(java.lang.String)}
     */
    @Deprecated
    public abstract void scrollTo(String clientId);

    /**
     * Use PrimeFaces.ajax().update
     * 
     * @deprecated Use PrimeFaces.ajax().update()
     */
    @Deprecated
    public abstract void update(String name);

    /**
     * Use PrimeFaces.ajax().update
     * 
     * @deprecated Use PrimeFaces.ajax().update()
     */
    @Deprecated
    public abstract void update(Collection<String> collection);

    /**
     * Use {@link PrimeFaces#resetInputs(java.lang.String...)}
     * @param expressions
     * @deprecated Use {@link PrimeFaces#resetInputs(java.lang.String...)}
     */
    @Deprecated
    public abstract void reset(String expressions);

    /**
     * Use {@link PrimeFaces#resetInputs(java.lang.String...)}
     * @param expressions
     * @deprecated Use {@link PrimeFaces#resetInputs(java.lang.String...)}
     */
    @Deprecated
    public abstract void reset(Collection<String> expressions);

    /**
     * Use {@link PrimeFaces#resetInputs(java.lang.String...)}
     * @param expressions
     * @deprecated Use {@link PrimeFaces#resetInputs(java.lang.String...)}
     */
    @Deprecated
    public abstract void reset(String... expressions);

    /**
     * @return Shared WidgetBuilder instance of the current request
     */
    public abstract WidgetBuilder getWidgetBuilder();

    /**
     * @return Shared AjaxRequestBuilder instance of the current request
     */
    public abstract AjaxRequestBuilder getAjaxRequestBuilder();

    /**
     * @return Shared Client Side Validation builder instance of the current request
     */
    public abstract CSVBuilder getCSVBuilder();

    /**
     * Use FacesContext#getAttributes()
     */
    @Deprecated
    public abstract Map<Object, Object> getAttributes();

    /**
     * Use PrimeFaces.dialog().openDynamic()
     * 
     * @deprecated Use PrimeFaces.dialog().openDynamic()
     */
    @Deprecated
    public abstract void openDialog(String outcome);

    /**
     * Use PrimeFaces.dialog().openDynamic
     * 
     * @deprecated Use PrimeFaces.dialog().openDynamic()
     */
    @Deprecated
    public abstract void openDialog(String outcome, Map<String, Object> options, Map<String, List<String>> params);

    /**
     * Use PrimeFaces.dialog().closeDynamic
     * 
     * @deprecated Use PrimeFaces.dialog().closeDynamic
     */
    @Deprecated
    public abstract void closeDialog(Object data);

    /**
     * Use PrimeFaces.dialog().showMessageDynamic
     * 
     * @deprecated Use PrimeFaces.dialog().showMessageDynamic
     */
    @Deprecated
    public abstract void showMessageInDialog(FacesMessage message);

    /**
     * @return ApplicationContext instance.
     */
    public abstract ApplicationContext getApplicationContext();

    /**
     * @return StringEncrypter used to encode and decode a string.
     */
    public abstract StringEncrypter getEncrypter();

    /**
     * Clear resources.
     */
    public abstract void release();

    /**
     * Returns a boolean indicating whether this request was made using a secure channel, such as HTTPS.
     */
    public abstract boolean isSecure();

    /**
     * @return <code>true</code> if {@link AutoUpdatable} components should not be updated automatically in this request.
     */
    public abstract boolean isIgnoreAutoUpdate();

    public abstract boolean isRTL();

    /**
     * Use {@link PrimeFaces#clearTableState()}
     * @deprecated  Use {@link PrimeFaces#clearTableState()}
     */
    @Deprecated
    public abstract void clearTableStates();

    /**
     * Use {@link PrimeFaces#clearTableState(java.lang.String)}
     * @param clientId
     * @deprecated Use {@link PrimeFaces#clearTableState(java.lang.String)}
     */
    @Deprecated
    public abstract void clearTableState(String clientId);
}
