/*
 * The MIT License
 *
 * Copyright (c) 2009-2020 PrimeTek
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

import org.primefaces.model.file.UploadedFile;

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
     * @param file file to perform virus scan on
     */
    void scan(UploadedFile file);

}
