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

    private static VirusScannerService service;
    private ServiceLoader<VirusScanner> loader;

    private VirusScannerService() {
        loader = ServiceLoader.load(VirusScanner.class);
    }

    public static synchronized VirusScannerService getInstance() {
        if (service == null) {
            service = new VirusScannerService();
        }
        return service;
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
        Iterator<VirusScanner> scanners = loader.iterator();
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
