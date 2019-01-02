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

/**
 * Service provider interface for virus scanning that might be used in file upload component for example when dealing with untrusted files.
 * @see <a href="https://github.com/primefaces/primefaces/issues/4256">fileUpload: virus scan</a>
 */
public interface VirusScanner {

    /**
     * Indicate whether this {@link VirusScanner} is enabled or not.
     * @return <code>true</code> if enabled, <code>false</code> otherwise
     */
    boolean isEnabled();

    /**
     * Perform virus scan and throw exception if a virus has been detected.
     * @param inputStream input stream to perform virus scan on
     * @throws VirusException if a virus has been detected by the scanner
     */
    void performVirusScan(InputStream inputStream) throws VirusException;

}
