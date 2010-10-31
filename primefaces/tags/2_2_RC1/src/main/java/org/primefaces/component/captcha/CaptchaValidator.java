/*
 * Copyright 2010 Prime Technology.
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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import javax.faces.FacesException;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import javax.servlet.http.HttpServletRequest;

import org.primefaces.util.MessageFactory;

public class CaptchaValidator implements Validator {

	public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
		String result = null;
		Verification verification = (Verification) value;

		try {
			URL url = new URL("http://api-verify.recaptcha.net/verify");
			URLConnection conn = url.openConnection();
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setUseCaches(false);
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			String postBody = createPostParameters(context, verification);
			
			OutputStream out = conn.getOutputStream();
			out.write(postBody.getBytes());
			out.flush();
			out.close();
			
			BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			result = rd.readLine();
			rd.close();
		}catch(Exception exception) {
			throw new RuntimeException(exception);
		}
		
		boolean isValid = Boolean.valueOf(result);
		
		if(isValid == false) {
			Object[] params = new Object[2];
			params[0] = MessageFactory.getLabel(context, component);
			params[1] = verification.getAnswer();
			
			throw new ValidatorException(MessageFactory.getMessage(Captcha.INVALID_MESSAGE_ID, FacesMessage.SEVERITY_ERROR, params));
		}
	}
	
	private String createPostParameters(FacesContext facesContext, Verification verification) throws UnsupportedEncodingException {
		String challenge = verification.getChallenge();
		String answer = verification.getAnswer();
		String remoteAddress = ((HttpServletRequest) facesContext.getExternalContext().getRequest()).getRemoteAddr();
		String privateKey = facesContext.getExternalContext().getInitParameter(Captcha.PRIVATE_KEY);

        if(privateKey == null) {
            throw new FacesException("Private Captcha Key is not defined using primefaces.PRIVATE_CAPTCHA_KEY context-param");
        }

		StringBuilder postParams = new StringBuilder();
		postParams.append("privatekey=").append(URLEncoder.encode(privateKey, "UTF-8"));
		postParams.append("&remoteip=").append(URLEncoder.encode(remoteAddress, "UTF-8"));
		postParams.append("&challenge=").append(URLEncoder.encode(challenge, "UTF-8"));
		postParams.append("&response=").append(URLEncoder.encode(answer, "UTF-8"));
	
		return postParams.toString();
	}
}