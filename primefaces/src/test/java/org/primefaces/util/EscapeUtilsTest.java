/*
 * The MIT License
 *
 * Copyright (c) 2009-2025 PrimeTek Informatics
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

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EscapeUtilsTest {

    @Test
    void forXmlTag() {
        final String input = "hello world & >";

        // invalid
        assertEquals("hello world &amp; &gt;", EscapeUtils.forXml(input), "Not valid XML tag");
        // invalid
        assertEquals("hello world &amp; >", EscapeUtils.forXmlAttribute(input), "Not valid XML tag");
        // correct!!!
        assertEquals("hello_world_.26._.gt.", EscapeUtils.forXmlTag(input), "Valid!!");
    }

    @Test
    void forJavascriptVarName() {
        assertEquals("form_datatable_0_my_input_text", EscapeUtils.forJavaScriptVarName("form:datatable:0:my_input_text"));
    }

    @Test
    void forJavascriptNullInput() {
        assertEquals("null", EscapeUtils.forJavaScript(null));
    }
}
