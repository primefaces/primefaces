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
package org.primefaces.virusscan.impl;

import org.primefaces.model.file.UploadedFile;
import org.primefaces.util.Lazy;
import org.primefaces.util.MessageFactory;
import org.primefaces.virusscan.VirusException;
import org.primefaces.virusscan.VirusScanner;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;

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

    private Lazy<ClamDaemonClient> client = new Lazy<>(() -> {
        String host = ClamDaemonClient.DEFAULT_HOST;
        int port = ClamDaemonClient.DEFAULT_PORT;
        int timeout = ClamDaemonClient.DEFAULT_TIMEOUT;
        int bufferSize = ClamDaemonClient.DEFAULT_BUFFER;

        if (FacesContext.getCurrentInstance() != null) {
            ExternalContext ctx = FacesContext.getCurrentInstance().getExternalContext();
            if (ctx.getInitParameter(CONTEXT_PARAM_HOST) != null) {
                host = ctx.getInitParameter(CONTEXT_PARAM_HOST);
            }
            if (ctx.getInitParameter(CONTEXT_PARAM_PORT) != null) {
                port = Integer.parseInt(ctx.getInitParameter(CONTEXT_PARAM_PORT));
            }
            if (ctx.getInitParameter(CONTEXT_PARAM_TIMEOUT) != null) {
                timeout = Integer.parseInt(ctx.getInitParameter(CONTEXT_PARAM_TIMEOUT));
            }
            if (ctx.getInitParameter(CONTEXT_PARAM_BUFFER) != null) {
                bufferSize = Integer.parseInt(ctx.getInitParameter(CONTEXT_PARAM_BUFFER));
            }
        }

        return new ClamDaemonClient(host, port, timeout, bufferSize);
    });

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
            InputStream inputStream = new ByteArrayInputStream(file.getContent());
            byte[] reply = getClamAvClient().scan(inputStream);

            String message = new String(reply, StandardCharsets.US_ASCII).trim();

            LOGGER.log(Level.FINE, "Scanner replied with message: {0}", message);

            if (!ClamDaemonClient.isCleanReply(reply)) {
                String error = createErrorMessage(file, message);
                LOGGER.log(Level.WARNING, "ClamAV Error: {0}", error);
                throw new VirusException(error);
            }
        }
        catch (VirusException ex) {
            throw ex;
        }
        catch (RuntimeException | IOException ex) {
            String error = String.format("Unexpected error scanning file - %s", ex.getMessage());
            throw new VirusException(error);
        }
    }

    protected String createErrorMessage(UploadedFile file, String response) {
        return MessageFactory.getMessage(FacesContext.getCurrentInstance(), "primefaces.fileupload.CLAM_AV_FILE", file.getFileName(), response);
    }

    /**
     * Returns a new ClamAvClient which can be overridden in unit tests.
     *
     * @return the {@link ClamDaemonClient}
     */
    public ClamDaemonClient getClamAvClient() {
        return client.get();
    }
}