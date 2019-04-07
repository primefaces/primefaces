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
package org.primefaces.virusscan.impl;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;
import org.primefaces.util.EscapeUtils;
import org.primefaces.virusscan.VirusException;
import org.primefaces.virusscan.VirusScanner;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This is the default {@link VirusScanner} provider bundled with PrimeFaces.
 * The implementation makes use of the <a href="https://www.virustotal.com/de/documentation/public-api/">VirusTotal Public API v2.0</a>.
 * It requires {@link #CONTEXT_PARAM_KEY} to be specified.
 */
public class VirusTotalVirusScanner implements VirusScanner {

    private static final Logger LOGGER = Logger.getLogger(VirusTotalVirusScanner.class.getName());

    private static final String CONTEXT_PARAM_KEY = "primefaces.virusscan.VIRUSTOTAL_KEY";

    private static final String API_ENDPOINT = "https://www.virustotal.com/vtapi/v2/file/report?apikey=%s&resource=%s";

    @Override
    public boolean isEnabled() {
        ExternalContext ctx = FacesContext.getCurrentInstance().getExternalContext();
        return ctx.getInitParameter(CONTEXT_PARAM_KEY) != null;
    }

    @Override
    public void performVirusScan(InputStream inputStream) throws VirusException {
        ExternalContext ctx = FacesContext.getCurrentInstance().getExternalContext();
        String key = ctx.getInitParameter(CONTEXT_PARAM_KEY);
        try {
            byte[] content = IOUtils.toByteArray(inputStream);
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(content);
            String hash = DatatypeConverter.printHexBinary(md.digest());
            URL url = new URL(String.format(API_ENDPOINT, EscapeUtils.forUriComponent(key), EscapeUtils.forUriComponent(hash)));
            try (InputStream response = url.openStream()) {
                JSONObject json = new JSONObject(IOUtils.toString(response, "UTF-8"));
                int responseCode = json.getInt("response_code");
                if (LOGGER.isLoggable(Level.FINE)) {
                    LOGGER.fine(String.format("Retrieved response code %d.", responseCode));
                }
                if (responseCode == 1) {
                    // present
                    int positives = json.getInt("positives");
                    if (LOGGER.isLoggable(Level.FINE)) {
                        LOGGER.fine(String.format("Retrieved %d positives.", positives));
                    }
                    if (positives > 0) {
                        throw new VirusException();
                    }
                }
            }
        }
        catch (IOException | NoSuchAlgorithmException ex) {
            if (LOGGER.isLoggable(Level.WARNING)) {
                LOGGER.log(Level.WARNING, "Cannot perform virus scan", ex);
            }
            throw new RuntimeException("Cannot perform virus scan");
        }
    }

}
