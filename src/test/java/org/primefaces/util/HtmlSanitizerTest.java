/* 
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
package org.primefaces.util;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.primefaces.component.texteditor.TextEditor;
import org.primefaces.component.texteditor.TextEditorRenderer;

public class HtmlSanitizerTest {

    @Before
    public void setup() {

    }

    @After
    public void teardown() {

    }

    @Test
    public void htmlSupportedByComponentShouldBeAllowedPerDefault() {
        String value = "<p><a href=\"https://www.primefaces.org\" target=\"_blank\">Link</a> <strong>bold </strong><span class=\"ql-font-monospace\">monospace</span> <span class=\"ql-size-huge\">huge </span><span class=\"ql-font-serif ql-size-small\">small serif </span><span style=\"color: rgb(230, 0, 0);\">red </span><span style=\"background-color: rgb(255, 255, 0);\">yellow </span>x<sup>2</sup> <img src=\"data:image/png;base64,COFFEE\" /></p>";
        String sanitized = HtmlSanitizer.sanitizeHtml(value, true, true, true, true, true);
        Assert.assertTrue(sanitized.contains("<a href") && sanitized.contains("target="));
        Assert.assertTrue(sanitized.contains("<strong>bold"));
        Assert.assertTrue(sanitized.contains("<span class=\"ql-font-monospace"));
        Assert.assertTrue(sanitized.contains("<span style=\"background"));
        Assert.assertTrue(sanitized.contains("<sup>2"));
        Assert.assertTrue(sanitized.contains("<img") && sanitized.contains("COFFEE"));
    }

    @Test
    public void scriptShouldNeverBeAllowed() {
        String value = "<script>alert('oops');</script><b>test</b>";
        String sanitized = HtmlSanitizer.sanitizeHtml(value, true, true, true, true, true);
        Assert.assertEquals("<b>test</b>", sanitized);
    }

    @Test
    public void imagesShouldNotBeAllowed() {
        String value = "<img src=\"data:image/png;base64,COFFEE\" /><b>test</b>";
        String sanitized = HtmlSanitizer.sanitizeHtml(value, true, true, true, true, false);
        Assert.assertEquals("<b>test</b>", sanitized);
    }

}
