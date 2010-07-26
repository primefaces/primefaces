/*
 * Copyright 2009 Prime Technology.
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

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.renderkit.CoreRenderer;

public class CaptchaRenderer extends CoreRenderer {
	
	private final static String CHALLENGE_FIELD = "recaptcha_challenge_field";
	private final static String RESPONSE_FIELD = "recaptcha_response_field";

	/**
	 * Decodes the answer to the captcha challenge.
	 * If answer parameter is
	 */
	public void decode(FacesContext facesContext, UIComponent component) {
		Captcha captcha = (Captcha) component;
		Map<String,String> params = facesContext.getExternalContext().getRequestParameterMap();

		String challenge = params.get(CHALLENGE_FIELD);
		String answer = params.get(RESPONSE_FIELD);
		
		if(answer != null) {
			if(answer.equals(""))
				captcha.setSubmittedValue(answer);
			else
				captcha.setSubmittedValue(new Verification(challenge, answer));
		}
	}

	public void encodeEnd(FacesContext facesContext, UIComponent component) throws IOException {
		ResponseWriter writer = facesContext.getResponseWriter();
		Captcha captcha = (Captcha) component;
		captcha.setRequired(true);
		
		String publicKey = captcha.getPublicKey();
		
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
		writer.writeAttribute("src", "http://api.recaptcha.net/challenge?k=" + publicKey, null);
		writer.endElement("script");
		
		writer.startElement("noscript", null);
		writer.startElement("iframe", null);
		writer.writeAttribute("src", "http://api.recaptcha.net/noscript?k=" + publicKey, null);
		writer.endElement("iframe");
		writer.write("<br />");
		
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
}
