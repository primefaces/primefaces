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
package org.primefaces.component.captcha;

import java.io.IOException;
import java.util.Map;

import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.WidgetBuilder;

public class CaptchaRenderer extends CoreRenderer {

    private static final String RESPONSE_FIELD = "g-recaptcha-response";

    @Override
    public void decode(FacesContext context, UIComponent component) {
        Captcha captcha = (Captcha) component;
        Map<String, String> params = context.getExternalContext().getRequestParameterMap();

        String answer = params.get(RESPONSE_FIELD);

        if (answer != null) {
            captcha.setSubmittedValue(answer);
        }
        else {
            captcha.setSubmittedValue("");
        }
    }

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        Captcha captcha = (Captcha) component;
        String publicKey = getPublicKey(context, captcha);

        if (publicKey == null) {
            throw new FacesException("Cannot find public key for catpcha, use primefaces.PUBLIC_CAPTCHA_KEY context-param to define one");
        }

        encodeMarkup(context, captcha, publicKey);
        encodeScript(context, captcha, publicKey);
    }

    protected void encodeMarkup(FacesContext context, Captcha captcha, String publicKey) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = captcha.getClientId(context);
        captcha.setRequired(true);
        writer.startElement("div", null);
        writer.writeAttribute("id", clientId, "id");

        if (captcha.getSize() != null && "invisible".equals(captcha.getSize())) {
            writer.writeAttribute("class", "g-recaptcha", null);
            writer.writeAttribute("data-sitekey", publicKey, null);
            writer.writeAttribute("data-size", "invisible", null);
        }

        renderDynamicPassThruAttributes(context, captcha);

        writer.endElement("div");
    }

    protected void encodeScript(FacesContext context, Captcha captcha, String publicKey) throws IOException {
        String clientId = captcha.getClientId(context);
        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("Captcha", captcha.resolveWidgetVar(context), clientId);

        wb.attr("sitekey", publicKey)
                .attr("theme", captcha.getTheme(), "light")
                .attr("language", captcha.getLanguage(), "en")
                .attr("tabindex", captcha.getTabindex(), 0)
                .attr("callback", captcha.getCallback(), null)
                .attr("expired", captcha.getExpired(), null)
                .attr("size", captcha.getSize(), null);

        wb.finish();
    }

    protected String getPublicKey(FacesContext context, Captcha captcha) {
        return context.getApplication().evaluateExpressionGet(context, context.getExternalContext().getInitParameter(Captcha.PUBLIC_KEY), String.class);
    }
}
