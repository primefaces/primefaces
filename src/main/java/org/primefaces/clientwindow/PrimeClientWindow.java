/*
 * The MIT License
 *
 * Copyright (c) 2009-2020 PrimeTek
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
package org.primefaces.clientwindow;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.faces.FacesException;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.lifecycle.ClientWindow;
import javax.faces.render.ResponseStateManager;
import org.primefaces.util.LangUtils;

public class PrimeClientWindow extends ClientWindow {

    private String id;
    private Map<String, String> queryParamsMap;

    @Override
    public Map<String, String> getQueryURLParameters(FacesContext context) {
        if (queryParamsMap == null) {
            if (id != null) {
                queryParamsMap = new HashMap<>(2, 1);
                queryParamsMap.put(ResponseStateManager.CLIENT_WINDOW_URL_PARAM, id);
            }
        }
        return queryParamsMap;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void decode(FacesContext context) {
        id = context.getExternalContext().getRequestParameterMap().get(ResponseStateManager.CLIENT_WINDOW_URL_PARAM);

        boolean post = PrimeClientWindowUtils.isPost(context);

        if (LangUtils.isValueBlank(id) && post) {
            id = context.getExternalContext().getRequestParameterMap().get(ResponseStateManager.CLIENT_WINDOW_PARAM);
        }

        if (!LangUtils.isValueBlank(id)) {
            id = PrimeClientWindowUtils.secureWindowId(id);
        }

        if (LangUtils.isValueBlank(id)) {
            id = PrimeClientWindowUtils.generateNewWindowId();

            if (!post) {
                try {
                    // this will also include the new generated windowId
                    String redirectUrl = constructInitialRedirectUrl(context);

                    // remember the initial redirect windowId till the next request - see DeltaSpike #729
                    PrimeClientWindowUtils.addInitialRedirectCookie(context, id);

                    context.getExternalContext().redirect(redirectUrl);
                }
                catch (IOException e) {
                    throw new FacesException("Could not send initial redirect!", e);
                }

                context.responseComplete();
            }
        }
    }

    /**
     * Constructs the URL for the initialRedirect, which equals the current requested URL + the window id candidate.
     * We cant use
     * {@link javax.faces.application.ViewHandler#getBookmarkableURL(javax.faces.context.FacesContext, java.lang.String, java.util.Map, boolean)}
     * as the ViewRoot isn't available yet in the {@link FacesContext}.
     *
     * @param context The {@link FacesContext}.
     * @return The initial-redirect URL.
     */
    protected String constructInitialRedirectUrl(FacesContext context) {
        ExternalContext externalContext = context.getExternalContext();

        String url = externalContext.getRequestContextPath() + externalContext.getRequestServletPath();
        if (externalContext.getRequestPathInfo() != null) {
            url += externalContext.getRequestPathInfo();
        }

        Map<String, List<String>> requestParameters = new HashMap<>();
        for (Map.Entry<String, String[]> entry : externalContext.getRequestParameterValuesMap().entrySet()) {
            List<String> requestParametersValues = requestParameters.computeIfAbsent(entry.getKey(), k -> new ArrayList<>(1));
            if (entry.getValue() != null) {
                requestParametersValues.addAll(Arrays.asList(entry.getValue()));
            }
        }

        return externalContext.encodeRedirectURL(url, requestParameters);
    }
}
