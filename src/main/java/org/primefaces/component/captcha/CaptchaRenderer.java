/**
 * Copyright 2009-2018 PrimeTek.
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
        wb.init("Captcha", captcha.resolveWidgetVar(), clientId);

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
