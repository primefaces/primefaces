/*
 * Copyright 2009-2014 PrimeTek.
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
import java.util.logging.Logger;
import javax.faces.FacesException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.renderkit.CoreRenderer;

public class CaptchaRenderer extends CoreRenderer {

    private static final Logger LOG = Logger.getLogger(CaptchaRenderer.class.getName());

    private final static String RESPONSE_FIELD = "g-recaptcha-response";

    @Override
    public void decode(FacesContext context, UIComponent component) {
        Captcha captcha = (Captcha) component;
        Map<String,String> params = context.getExternalContext().getRequestParameterMap();

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
        ResponseWriter writer = context.getResponseWriter();
        Captcha captcha = (Captcha) component;
        String protocol = captcha.isSecure() ? "https" : "http";
        String publicKey = getPublicKey(context, captcha);

        if (publicKey == null) {
            throw new FacesException("Cannot find public key for catpcha, use primefaces.PUBLIC_CAPTCHA_KEY context-param to define one");
        }
        
        writer.startElement("script", null);
        writer.writeAttribute("type", "text/javascript", null);
        writer.writeAttribute("src", protocol + "://www.google.com/recaptcha/api.js?hl=" + captcha.getLanguage(), null);
        writer.endElement("script");
        
        encodeMarkup(context, captcha, publicKey);
        encodeScript(context, captcha, publicKey);
        
    }
    
    protected void encodeMarkup(FacesContext context, Captcha captcha, String publicKey) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = captcha.getClientId(context);

        captcha.setRequired(true);

        writer.startElement("div", null);
        writer.writeAttribute("id", clientId, "id");
        writer.writeAttribute("class", "g-recaptcha", null);
        writer.writeAttribute("data-sitekey", publicKey, null);
        writer.endElement("div");
    }

    protected void encodeScript(FacesContext context, Captcha captcha, String publicKey) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = captcha.getClientId(context);
        String widgetVar = captcha.resolveWidgetVar();

        startScript(writer, clientId);

        writer.write("$(function() {");
        writer.write("PrimeFaces.cw('Captcha','" + widgetVar + "',{");
        writer.write("id:'" + clientId + "'");
        writer.write(",render:{");
        writer.write("\"sitekey\":\"" + publicKey + "\"");
        writer.write(",\"theme\":\"" + captcha.getTheme() + "\"");
        if (captcha.getTabindex() != 0) {
            writer.write(",\"tabIndex\":" + captcha.getTabindex());
        }
        writer.write("}");
        writer.write("});});");

        endScript(writer);
    }

    protected String getPublicKey(FacesContext context, Captcha captcha) {
        return context.getExternalContext().getInitParameter(Captcha.PUBLIC_KEY);
    }
}