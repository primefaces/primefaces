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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.primefaces.model.file.UploadedFile;
import org.primefaces.util.MessageFactory;
import org.primefaces.virusscan.VirusException;
import org.primefaces.virusscan.VirusScanner;

/**
 * ClamAV Daemon custom {@link VirusScanner} provider bundled with PrimeFaces.
 * Streams the file over TCP to a ClamAV service running on host:port.
 * It requires
 * {@link #CONTEXT_PARAM_HOST} and {@link #CONTEXT_PARAM_PORT} to be specified.
 */
public class ClamDaemonScanner implements VirusScanner {

    private static final Logger LOGGER = Logger.getLogger(ClamDaemonScanner.class.getName());

    private static final String CONTEXT_PARAM_HOST = "primefaces.virusscan.CLAMAV_HOST";
    private static final String CONTEXT_PARAM_PORT = "primefaces.virusscan.CLAMAV_PORT";
    private static final String CONTEXT_PARAM_TIMEOUT = "primefaces.virusscan.CLAMAV_TIMEOUT";
    private static final String CONTEXT_PARAM_BUFFER = "primefaces.virusscan.CLAMAV_BUFFER";

    private ClamDaemonClient client;

    @Override
    public boolean isEnabled() {
        ExternalContext ctx = FacesContext.getCurrentInstance().getExternalContext();
        return ctx.getInitParameter(CONTEXT_PARAM_HOST) != null && ctx.getInitParameter(CONTEXT_PARAM_PORT) != null;
    }

    /**
     * Scan file using send to ClamAV service running at host and port over TCP.
     *
     * @throws VirusException if a virus has been detected by the scanner
     */
    @Override
    public void scan(UploadedFile file) {
        try {
            final ClamDaemonClient client = this.getClamAvClient();
            final InputStream inputStream = new ByteArrayInputStream(file.getContent());
            final byte[] reply = client.scan(inputStream);
            final String message = new String(reply, StandardCharsets.US_ASCII).trim();
            if (LOGGER.isLoggable(Level.INFO)) {
                LOGGER.log(Level.INFO, "Scanner replied with message:" + message);
            }
            if (!ClamDaemonClient.isCleanReply(reply)) {
                String error = createErrorMessage(file, message);
                if (LOGGER.isLoggable(Level.WARNING)) {
                    LOGGER.log(Level.WARNING, "ClamAV Error:" + error);
                }
                throw new VirusException(error);
            }
        }
        catch (final VirusException ex) {
            throw ex;
        }
        catch (final RuntimeException | IOException ex) {
            final String error = String.format("Unexpected error scanning file - %s", ex.getMessage());
            throw new VirusException(error);
        }
    }

    protected String createErrorMessage(UploadedFile file, String response) {
        return MessageFactory.getMessage("primefaces.fileupload.CLAM_AV_FILE", file.getFileName(), response);
    }

    /**
     * Returns a new ClamAvClient which can be overridden in unit tests.
     *
     * @return the {@link ClamAVClient}
     */
    ClamDaemonClient getClamAvClient() {
        if (client != null) {
            return client;
        }
        ExternalContext ctx = FacesContext.getCurrentInstance().getExternalContext();
        String host = ctx.getInitParameter(CONTEXT_PARAM_HOST);
        int port = Integer.parseInt(ctx.getInitParameter(CONTEXT_PARAM_PORT));

        int timeout = 60000; // default to 1 minute
        if (ctx.getInitParameter(CONTEXT_PARAM_TIMEOUT) != null) {
            timeout = Integer.parseInt(ctx.getInitParameter(CONTEXT_PARAM_TIMEOUT));
        }

        int chunkSize = 2048; // default buffer size in bytes
        if (ctx.getInitParameter(CONTEXT_PARAM_BUFFER) != null) {
            chunkSize = Integer.parseInt(ctx.getInitParameter(CONTEXT_PARAM_BUFFER));
        }
        client = new ClamDaemonClient(host, port, timeout, chunkSize);
        return client;
    }

}
