/*
 * Copyright 2009-2012 Prime Teknoloji.
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

    private static final Logger logger = Logger.getLogger(CaptchaRenderer.class.getName());
	
	private final static String CHALLENGE_FIELD = "recaptcha_challenge_field";
	private final static String RESPONSE_FIELD = "recaptcha_response_field";

    @Override
	public void decode(FacesContext context, UIComponent component) {
		Captcha captcha = (Captcha) component;
		Map<String,String> params = context.getExternalContext().getRequestParameterMap();

		String challenge = params.get(CHALLENGE_FIELD);
		String answer = params.get(RESPONSE_FIELD);
		
		if(answer != null) {
			if(answer.equals(""))
				captcha.setSubmittedValue(answer);
			else
				captcha.setSubmittedValue(new Verification(challenge, answer));
		} else {
            captcha.setSubmittedValue("");
        }
	}

    @Override
	public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
		Captcha captcha = (Captcha) component;
		captcha.setRequired(true);
        String protocol = captcha.isSecure() ? "https" : "http";
		
		String publicKey = getPublicKey(context, captcha);

        if(publicKey == null) {
            throw new FacesException("Cannot find public key for catpcha, use primefaces.PUBLIC_CAPTCHA_KEY context-param to define one");
        }
		
		writer.startElement("script", null);
		writer.writeAttribute("type", "text/javascript", null);
        
		writer.write("var RecaptchaOptions = {");
		writer.write("theme:\"" + captcha.getTheme() + "\"");
		writer.write(",lang:\"" + captcha.getLanguage() + "\"");
		if(captcha.getTabindex() != 0) {
			writer.write(",tabIndex:" + captcha.getTabindex());
		}
		writer.write("};");
		writer.endElement("script");
		
		writer.startElement("script", null);
		writer.writeAttribute("type", "text/javascript", null);
		writer.writeAttribute("src", protocol + "://www.google.com/recaptcha/api/challenge?k=" + publicKey, null);
		writer.endElement("script");
		
		writer.startElement("noscript", null);
		writer.startElement("iframe", null);
		writer.writeAttribute("src", protocol + "://www.google.com/recaptcha/api/noscript?k=" + publicKey, null);
		writer.endElement("iframe");
		
		writer.startElement("textarea", null);
		writer.writeAttribute("id", CHALLENGE_FIELD, null);
		writer.writeAttribute("name", CHALLENGE_FIELD, null);
		writer.writeAttribute("rows", "3", null);
		writer.writeAttribute("columns", "40", null);
		writer.endElement("textarea");
		
		writer.startElement("input", null);
		writer.writeAttribute("id", RESPONSE_FIELD, null);
		writer.writeAttribute("name", RESPONSE_FIELD, null);
		writer.writeAttribute("type", "hidden", null);
		writer.writeAttribute("value", "manual_challenge", null);
		writer.endElement("input");
		
		writer.endElement("noscript");
	}

    protected String getPublicKey(FacesContext context, Captcha captcha) {
        return context.getExternalContext().getInitParameter(Captcha.PUBLIC_KEY);
    }
}