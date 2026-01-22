/*
 * The MIT License
 *
 * Copyright (c) 2009-2026 PrimeTek Informatics
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

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import jakarta.faces.context.FacesContext;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Manages Content Security Policy (CSP) state for PrimeFaces applications.
 * <p>
 * This class handles nonce generation and validation for CSP headers, as well as tracking event handlers.
 * The nonce is a cryptographically secure random value used to whitelist inline scripts.
 * </p>
 *
 * @since 7.0
 */
public class CspState {

    private FacesContext context;
    private Map<String, Map<String, String>> eventHandlers;
    private String nonce;
    private boolean initialized = false;

    public CspState(FacesContext context) {
        this.context = context;
        this.eventHandlers = new HashMap<>(10);
    }

    /**
     * Retrieves or generates a nonce (number used once) for Content Security Policy (CSP).
     *
     * @return the nonce as a Base64 encoded string
     * @throws CspException if there's a nonce mismatch or validation fails
     */
    public String getNonce() {
        if (nonce == null) {
            // If it's a POST, we already generated a nonce in the initial request
            // -> try to reuse and validate it
            if (context.isPostback() || context.getPartialViewContext().isAjaxRequest()) {
                Map<String, Object> viewMap = context.getViewRoot().getViewMap(false);

                String nonceRequest = context.getExternalContext().getRequestParameterMap().get(Constants.RequestParams.NONCE_PARAM);
                String nonceViewState = viewMap == null ? null : (String) viewMap.get(Constants.RequestParams.NONCE_PARAM);

                // Validate if nonceRequest matches nonceViewState
                // There are some cases when the nonceViewState can be null, "" must not happen actually
                // 1) if the request is a GET / initial request
                //      this is handled by the outer IF
                // 2) if the viewMap is null
                //      this is most likely a case when stateless views are used
                // 3) if the nonceViewState is null
                //      this can happen when a navigation or forward is done inside the current request
                if (nonceViewState != null) {
                    // normalize both to make it comparable
                    nonceRequest = Objects.toString(nonceRequest, Constants.EMPTY_STRING);
                    nonceViewState = Objects.toString(nonceViewState, Constants.EMPTY_STRING);

                    if (!Objects.equals(nonceRequest, nonceViewState)) {
                        throw new CspException("CSP nonce mismatch");
                    }
                }

                // always prefer nonceViewState over nonceRequest
                nonce = LangUtils.isNotBlank(nonceViewState) ? nonceViewState : nonceRequest;

                // in case of a forward, we might create a new nonce here
                // but it also means that the request had NO CSP request param... is this valid?
                if (LangUtils.isBlank(nonce) && isForward(context)) {
                    nonce = generateNonce();
                }

                validate(nonce);
            }
            // otherwise create a new nonce
            else {
                nonce = generateNonce();
                if (!context.getViewRoot().isTransient()) {
                    context.getViewRoot().getViewMap(true).put(Constants.RequestParams.NONCE_PARAM, nonce);
                }
            }
        }

        return nonce;
    }

    /**
     * Generates a random nonce value for Content Security Policy.
     * Creates a UUID, converts it to a string, gets its bytes in UTF-8 encoding,
     * and encodes those bytes as a Base64 string.
     *
     * @return Base64 encoded nonce string
     */
    public String generateNonce() {
        return Base64.getEncoder().encodeToString(UUID.randomUUID().toString().getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Checks if the current request is a forward.
     *
     * @param context The FacesContext
     * @return true if request is forwarded, false otherwise
     */
    protected boolean isForward(FacesContext context) {
        Object request = context.getExternalContext().getRequest();
        if (request instanceof HttpServletRequest) {
            return ((HttpServletRequest) request).getAttribute("jakarta.servlet.forward.request_uri") != null;
        }
        return false;
    }

    /**
     * Currently the script nonce is user-supplied input, so we have to validate it to prevent header/XSS injections.
     *
     * @param nonce the nonce to validate
     * @throws CspException if any errors validating the nonce
     */
    private void validate(String nonce) throws CspException {
        if (LangUtils.isEmpty(nonce)) {
            throw new CspException("Missing CSP nonce");
        }
        try {
            String decodedNonce = new String(Base64.getDecoder().decode(nonce), StandardCharsets.UTF_8);
            UUID.fromString(decodedNonce);
        }
        catch (Exception e) {
            throw new CspException("Invalid CSP nonce", e);
        }
    }

    public Map<String, Map<String, String>> getEventHandlers() {
        return eventHandlers;
    }

    /**
     * To prevent CSP from being initialized twice for any reason check if we already have run once when calling initialize.
     *
     * @return true if CSP has already been initialized, false if not.
     */
    public boolean isInitialized() {
        return initialized;
    }

    public void setInitialized(boolean initialized) {
        this.initialized = initialized;
    }

}
