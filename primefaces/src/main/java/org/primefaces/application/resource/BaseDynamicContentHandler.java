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
package org.primefaces.application.resource;

import org.primefaces.util.ResourceUtils;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import jakarta.faces.context.ExternalContext;

public abstract class BaseDynamicContentHandler implements DynamicContentHandler {

    public void handleCache(ExternalContext externalContext, boolean cache) {
        if (cache) {
            DateTimeFormatter httpDateFormat = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss z");
            ZonedDateTime dateTime = ZonedDateTime.now();
            dateTime = dateTime.plusYears(1);
            externalContext.setResponseHeader("Cache-Control", "max-age=29030400");
            externalContext.setResponseHeader("Expires", httpDateFormat.format(dateTime));
        }
        else {
            ResourceUtils.addNoCacheControl(externalContext);
        }
    }
}
