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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import javax.faces.FacesException;
import javax.faces.application.FacesMessage;
import javax.faces.application.ResourceDependencies;
import javax.faces.application.ResourceDependency;
import javax.faces.context.FacesContext;

import org.primefaces.PrimeFaces;
import org.primefaces.json.JSONObject;
import org.primefaces.util.MessageFactory;

@ResourceDependencies({
        @ResourceDependency(library = "primefaces", name = "jquery/jquery.js"),
        @ResourceDependency(library = "primefaces", name = "core.js"),
        @ResourceDependency(library = "primefaces", name = "components.js"),
        @ResourceDependency(library = "primefaces", name = "captcha/captcha.js")
})
public class Captcha extends CaptchaBase {

    public static final String COMPONENT_TYPE = "org.primefaces.component.Captcha";

    public static final String PUBLIC_KEY = "primefaces.PUBLIC_CAPTCHA_KEY";
    public static final String PRIVATE_KEY = "primefaces.PRIVATE_CAPTCHA_KEY";
    public static final String INVALID_MESSAGE_ID = "primefaces.captcha.INVALID";

    @Override
    protected void validateValue(FacesContext context, Object value) {
        super.validateValue(context, value);

        if (isValid()) {

            boolean result = false;

            try {
                URL url = new URL("https://www.google.com/recaptcha/api/siteverify");
                URLConnection conn = url.openConnection();
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setUseCaches(false);
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                String postBody = createPostParameters(context, value);

                OutputStream out = conn.getOutputStream();
                out.write(postBody.getBytes());
                out.flush();
                out.close();

                BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = rd.readLine()) != null) {
                    response.append(inputLine);
                }

                JSONObject json = new JSONObject(response.toString());
                result = json.getBoolean("success");

                rd.close();
            }
            catch (Exception exception) {
                throw new FacesException(exception);
            }
            finally {
                // the captcha token is valid for only one request, in case of an ajax request we have to get a new one
                if (context.getPartialViewContext().isAjaxRequest()) {
                    PrimeFaces.current().executeScript("if (document.getElementById('g-recaptcha-response')) { "
                            + "try { grecaptcha.reset(); } catch (error) { PrimeFaces.error(error); } }");
                }
            }

            if (!result) {
                setValid(false);

                String validatorMessage = getValidatorMessage();
                FacesMessage msg = null;

                if (validatorMessage != null) {
                    msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, validatorMessage, validatorMessage);
                }
                else {
                    Object[] params = new Object[2];
                    params[0] = MessageFactory.getLabel(context, this);
                    params[1] = value;

                    msg = MessageFactory.getMessage(Captcha.INVALID_MESSAGE_ID, FacesMessage.SEVERITY_ERROR, params);
                }

                context.addMessage(getClientId(context), msg);
            }
        }
    }

    private String createPostParameters(FacesContext context, Object value) throws UnsupportedEncodingException {
        String privateKey = context.getApplication().evaluateExpressionGet(context,
                context.getExternalContext().getInitParameter(Captcha.PRIVATE_KEY), String.class);

        if (privateKey == null) {
            throw new FacesException("Cannot find private key for catpcha, use primefaces.PRIVATE_CAPTCHA_KEY context-param to define one");
        }

        StringBuilder postParams = new StringBuilder();
        postParams.append("secret=").append(URLEncoder.encode(privateKey, "UTF-8"));
        postParams.append("&response=").append(value == null ? "" : URLEncoder.encode((String) value, "UTF-8"));

        String params = postParams.toString();
        postParams.setLength(0);

        return params;
    }
}