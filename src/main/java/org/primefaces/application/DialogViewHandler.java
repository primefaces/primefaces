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
package org.primefaces.application;

import java.util.Map;
import javax.faces.application.ViewHandler;
import javax.faces.application.ViewHandlerWrapper;
import javax.faces.context.FacesContext;
import org.primefaces.util.Constants;

public class DialogViewHandler extends ViewHandlerWrapper {

    private ViewHandler wrapped;

    @SuppressWarnings("deprecation") // the default constructor is deprecated in JSF 2.3
    public DialogViewHandler(ViewHandler wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public ViewHandler getWrapped() {
        return this.wrapped;
    }

    @Override
    public String getActionURL(FacesContext context, String viewId) {
        Map<String, String> params = context.getExternalContext().getRequestParameterMap();
        String pfdlgcid = params.get(Constants.DIALOG_FRAMEWORK.CONVERSATION_PARAM);
        String url = super.getActionURL(context, viewId);

        if (url.contains(Constants.DIALOG_FRAMEWORK.CONVERSATION_PARAM)) {
            return url;
        }
        else {
            if (pfdlgcid == null) {
                return url;
            }
            else {
                if (url.indexOf('?') == -1) {
                    return url + "?pfdlgcid=" + pfdlgcid;
                }
                else {
                    return url + "&pfdlgcid=" + pfdlgcid;
                }
            }
        }
    }
}
