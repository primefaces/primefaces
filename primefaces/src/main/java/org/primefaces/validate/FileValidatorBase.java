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
package org.primefaces.validate;

import org.primefaces.cdk.api.FacesValidatorBase;
import org.primefaces.cdk.api.Property;
import org.primefaces.cdk.api.validator.PrimeValidator;

@FacesValidatorBase
public abstract class FileValidatorBase extends PrimeValidator {

    @Property(description = "Regular expression for accepted file types, e.g., /(\\\\.|\\\\/)(gif|jpeg|jpg|png)$/")
    public abstract String getAllowTypes();

    @Property(description = "Maximum number of files to be uploaded.")
    public abstract Integer getFileLimit();

    @Property(description = "Individual file size limit in bytes.", implicitDefaultValue = "unlimited")
    public abstract Long getSizeLimit();

    @Property(description = "Wheter the content type should be validated based on the accept attribute of the attached component.")
    public abstract Boolean getContentType();

    @Property(description = "Whether virus scan should be performed.")
    public abstract Boolean getVirusScan();

    @Property(description = "Comma-separated list of allowed media types for content type validation."
            + " When specified, this takes precedence over the accept attribute for content type validation."
            + " Example: \"application/pdf,image/jpeg,image/png,text/plain\"")
    public abstract String getAllowMediaTypes();
}
