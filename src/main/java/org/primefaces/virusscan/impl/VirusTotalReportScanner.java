/*
 * The MIT License
 *
 * Copyright (c) 2009-2021 PrimeTek
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
import org.json.JSONException;
import org.json.JSONObject;
import org.primefaces.model.file.UploadedFile;
import org.primefaces.util.EscapeUtils;
import org.primefaces.util.LangUtils;
import org.primefaces.util.MessageFactory;
import org.primefaces.virusscan.VirusException;
import org.primefaces.virusscan.VirusScanner;

import javax.faces.FacesException;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This is the default {@link VirusScanner} provider bundled with PrimeFaces.
 * The implementation makes use of the <a href="https://developers.virustotal.com/reference">VirusTotal Public API v2.0</a>.
 * It requires {@link #CONTEXT_PARAM_KEY} to be specified.
 */
public class VirusTotalReportScanner implements VirusScanner {

    private static final Logger LOGGER = Logger.getLogger(VirusTotalReportScanner.class.getName());

    private static final String CONTEXT_PARAM_KEY = "primefaces.virusscan.VIRUSTOTAL_KEY";

    private static final String API_ENDPOINT = "https://www.virustotal.com/vtapi/v2/file/report?apikey=%s&resource=%s";

    @Override
    public boolean isEnabled() {
        ExternalContext ctx = FacesContext.getCurrentInstance().getExternalContext();
        return ctx.getInitParameter(CONTEXT_PARAM_KEY) != null;
    }

    /**
     * Scan file using "/file/report" endpoint
     * @throws VirusException if a virus has been detected by the scanner
     */
    @Override
    public void scan(UploadedFile file) {
        try {
            URLConnection connection = openConnection(file);
            try (InputStream response = connection.getInputStream()) {
                JSONObject json = new JSONObject(IOUtils.toString(response, StandardCharsets.UTF_8));
                handleBodyResponse(file, json);
            }
        }
        catch (JSONException | IOException ex) {
            if (LOGGER.isLoggable(Level.WARNING)) {
                LOGGER.log(Level.WARNING, "Cannot perform virus scan", ex);
            }
            throw new FacesException("Cannot perform virus scan", ex);
        }
    }

    protected void handleBodyResponse(UploadedFile file, JSONObject json) {
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
                String message = createErrorMessage(file, json);
                throw new VirusException(message);
            }
        }
    }

    protected String createErrorMessage(UploadedFile file, JSONObject json) {
        return MessageFactory.getMessage("primefaces.fileupload.VIRUS_TOTAL_FILE", file.getFileName());
    }

    protected URLConnection openConnection(UploadedFile file) throws IOException {
        HttpURLConnection connection;

        try {
            ExternalContext ctx = FacesContext.getCurrentInstance().getExternalContext();
            String key = ctx.getInitParameter(CONTEXT_PARAM_KEY);

            String hash = LangUtils.md5Hex(file.getContent());
            URL url = new URL(String.format(API_ENDPOINT, EscapeUtils.forUriComponent(key), EscapeUtils.forUriComponent(hash)));
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
        }
        catch (IOException e) {
            throw new FacesException(e);
        }

        int code = connection.getResponseCode();
        switch (code) {
            case 200:
                // OK
                break;
            case 204:
                throw new FacesException("Request rate limit exceeded. You are making more requests than allowed. "
                            + "You have exceeded one of your quotas (minute, daily or monthly). Daily quotas are reset every day at 00:00 UTC.");
            case 400:
                throw new FacesException("Bad request. Your request was somehow incorrect. "
                            + "This can be caused by missing arguments or arguments with wrong values.");
            case 403:
                throw new FacesException("Forbidden. You don't have enough privileges to make the request. "
                            + "You may be doing a request without providing an API key or you may be making a request "
                            + "to a Private API without having the appropriate privileges.");
            default:
                throw new FacesException("Unexpected HTTP code " + code + " calling Virus Total web service.");
        }

        return connection;
    }

}
