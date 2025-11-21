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
package org.primefaces.application.resource;

import org.primefaces.util.Constants;

import java.io.IOException;
import java.util.Set;

import jakarta.faces.context.ResponseWriter;
import jakarta.faces.context.ResponseWriterWrapper;

/**
 * A ResponseWriter decorator that automatically handles HTML5 boolean attributes.
 * Boolean attributes are written in naked form (e.g., "disabled") when their value
 * is "true" or Boolean.TRUE. If the value is "false", they are omitted entirely.
 */
public class BooleanAwareResponseWriter extends ResponseWriterWrapper {

    // -------------------------------------------------------------------------
    //  HTML5 Boolean Attributes (complete, spec-correct)
    // -------------------------------------------------------------------------
    private static final Set<String> BOOLEAN_ATTRS = Set.of(
            "allowfullscreen",
            "async",
            "autofocus",
            "autoplay",
            "checked",
            "controls",
            "default",
            "defer",
            "disabled",
            "formnovalidate",
            "hidden",
            "ismap",
            "itemscope",
            "loop",
            "multiple",
            "muted",
            "nomodule",
            "novalidate",
            "open",
            "playsinline",
            "readonly",
            "required",
            "reversed",
            "selected"
    );

    public BooleanAwareResponseWriter(ResponseWriter wrapped) {
        super(wrapped);
    }

    // -------------------------------------------------------------------------
    //  Automatic boolean-attribute handling
    // -------------------------------------------------------------------------
    @Override
    public void writeAttribute(String name, Object value, String property) throws IOException {

        if (name != null && BOOLEAN_ATTRS.contains(name.toLowerCase())) {
            // Omit attribute if value is explicitly false (Boolean.FALSE, string "false", or null)
            boolean shouldOmit = false;

            if (value == null) {
                shouldOmit = true;
            }
            else if (value instanceof Boolean) {
                shouldOmit = !((Boolean) value);
            }
            else {
                String s = value.toString().trim().toLowerCase();
                shouldOmit = "false".equals(s);
            }

            if (shouldOmit) {
                // If false or null → output nothing
                return;
            }

            // Otherwise, output the attribute (including empty string, "true", etc.)
            // Use writeAttribute with empty string to ensure JSF buffers it correctly
            // This will output name="" which is valid HTML5 for boolean attributes
            getWrapped().writeAttribute(name, Constants.EMPTY_STRING, property);
            return;
        }

        // Non-boolean attributes → normal delegation
        getWrapped().writeAttribute(name, value, property);
    }
}
