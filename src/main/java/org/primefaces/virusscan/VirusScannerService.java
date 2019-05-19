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
package org.primefaces.virusscan;

import java.io.InputStream;
import java.io.PushbackInputStream;
import java.util.Iterator;
import java.util.ServiceLoader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This service may be used to load registered {@link VirusScanner} providers and perform virus scan.
 */
public class VirusScannerService {

    private static final Logger LOGGER = Logger.getLogger(VirusScannerService.class.getName());

    private final ClassLoader classLoader;

    public VirusScannerService(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    /**
     * Perform virus scan and throw exception if at least one registered {@link VirusScanner} provider has detected a virus.
     * @param inputStream input stream to perform virus scan on
     * @throws VirusException if at least one {@link VirusScanner} provider has detected a virus
     */
    public void performVirusScan(InputStream inputStream) throws VirusException {
        if (LOGGER.isLoggable(Level.FINE)) {
            LOGGER.fine("Performing virus scan...");
        }

        // ServiceLoader is not thread-safe so it cannot be cached:
        // https://docs.oracle.com/javase/7/docs/api/java/util/ServiceLoader.html
        Iterator<VirusScanner> scanners = ServiceLoader.load(VirusScanner.class, classLoader).iterator();
        while (scanners.hasNext()) {
            try {
                VirusScanner scanner = scanners.next();
                String clazz = scanner.getClass().getName();
                if (!scanner.isEnabled()) {
                    if (LOGGER.isLoggable(Level.FINE)) {
                        LOGGER.fine(String.format("Skipping virus scan with %s provider since it is disabled", clazz));
                    }
                    continue;
                }
                if (LOGGER.isLoggable(Level.FINE)) {
                    LOGGER.fine(String.format("Performing virus scan with %s provider", clazz));
                }
                scanner.performVirusScan(new PushbackInputStream(inputStream));
                if (LOGGER.isLoggable(Level.FINE)) {
                    LOGGER.fine(String.format("No virus detected with %s provider", clazz));
                }
            }
            catch (VirusException ex) {
                if (LOGGER.isLoggable(Level.SEVERE)) {
                    LOGGER.severe("Detected a virus");
                }
                throw ex;
            }
        }
        if (LOGGER.isLoggable(Level.FINE)) {
            LOGGER.fine("No virus detected");
        }
    }

}
