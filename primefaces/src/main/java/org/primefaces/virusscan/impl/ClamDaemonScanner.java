/*
 * The MIT License
 *
 * Copyright (c) 2009-2022 PrimeTek
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

    private static final String CONTEXT_PARAM_BUFFER = "primefaces.virusscan.CLAMAV_BUFFER";
    private static final String CONTEXT_PARAM_HOST = "primefaces.virusscan.CLAMAV_HOST";
    private static final String CONTEXT_PARAM_PORT = "primefaces.virusscan.CLAMAV_PORT";
    private static final String CONTEXT_PARAM_TIMEOUT = "primefaces.virusscan.CLAMAV_TIMEOUT";

    private final int buffer;
    private final String host;
    private final int port;
    private final int timeout;

    private ClamDaemonClient client;

    public ClamDaemonScanner() {
        host = getInitParameterString(CONTEXT_PARAM_HOST, ClamDaemonClient.DEFAULT_HOST);
        port = getInitParameterInt(CONTEXT_PARAM_PORT, ClamDaemonClient.DEFAULT_PORT);
        timeout = getInitParameterInt(CONTEXT_PARAM_TIMEOUT, ClamDaemonClient.DEFAULT_TIMEOUT);
        buffer = getInitParameterInt(CONTEXT_PARAM_BUFFER, ClamDaemonClient.DEFAULT_BUFFER);
    }

    public ClamDaemonScanner(String host, int port, int timeout, int buffer) {
        this.host = host;
        this.port = port;
        this.timeout = timeout;
        this.buffer = buffer;
    }

    public String getInitParameterString(String parameter, String defaultValue) {
        if (FacesContext.getCurrentInstance() != null) {
            ExternalContext ctx = FacesContext.getCurrentInstance().getExternalContext();
            if (ctx != null && ctx.getInitParameter(parameter) != null) {
                return ctx.getInitParameter(parameter);
            }
        }
        return defaultValue;
    }

    public int getInitParameterInt(String parameter, int defaultValue) {
        if (FacesContext.getCurrentInstance() != null) {
            ExternalContext ctx = FacesContext.getCurrentInstance().getExternalContext();
            if (ctx != null && ctx.getInitParameter(parameter) != null) {
                return Integer.parseInt(ctx.getInitParameter(parameter));
            }
        }
        return defaultValue;
    }

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
            final InputStream inputStream = new ByteArrayInputStream(file.getContent());
            final byte[] reply = getClamAvClient().scan(inputStream);
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
     * @return the {@link ClamDaemonClient}
     */
    public ClamDaemonClient getClamAvClient() {
        if (client == null) {
            client = new ClamDaemonClient(host, port, timeout, buffer);
        }
        return client;
    }

}
