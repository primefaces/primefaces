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
package org.primefaces.csp;

import org.primefaces.util.Constants;
import org.primefaces.util.LangUtils;

import javax.faces.FacesException;
import javax.faces.context.FacesContext;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

public class CspState {

    private static final Logger LOG = Logger.getLogger(CspState.class.getName());

    private FacesContext context;
    private Map<String, Map<String, String>> eventHandlers;
    private String nonce;

    public CspState(FacesContext context) {
        this.context = context;
        this.eventHandlers = new HashMap<>(10);
    }

    public String getNonce() {
        if (nonce == null) {
            if (context.isPostback()) {
                nonce = context.getExternalContext().getRequestParameterMap().get(Constants.RequestParams.NONCE_PARAM);
                validate(nonce);
            }
            else {
                nonce = Base64.getEncoder().encodeToString(UUID.randomUUID().toString().getBytes(StandardCharsets.UTF_8));
            }
        }

        return nonce;
    }

    /**
     * Currently the script nonce is user-supplied input, so we have to validate it to prevent header/XSS injections.
     * @param nonce
     * @throws FacesException
     */
    private void validate(String nonce) throws FacesException {
        if (LangUtils.isValueEmpty(nonce)) {
            throw new FacesException("Missing CSP nonce");
        }
        try {
            Base64.getDecoder().decode(nonce);
        }
        catch (Exception e) {
            throw new FacesException("Invalid CSP nonce", e);
        }
    }

    public Map<String, Map<String, String>> getEventHandlers() {
        return eventHandlers;
    }

}
