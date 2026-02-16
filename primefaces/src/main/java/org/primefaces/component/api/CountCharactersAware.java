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
package org.primefaces.component.api;

import org.primefaces.cdk.api.Property;

/**
 * Interface for components that are aware of counting characters or bytes in the input,
 * typically to support character limit counters in the UI.
 * <p>
 * Implementing this interface allows components to configure a label to show remaining character count,
 * define their counter template, and toggle between counting characters or bytes.
 *
 * <ul>
 *   <li>{@link #getCounter()} - Returns the id of the label component where the character count is displayed.</li>
 *   <li>{@link #getCounterTemplate()} - Returns the template string for the counter display; supports placeholders
 *   such as <code>{0}</code> for remaining/entered characters and <code>{1}</code> for total length.</li>
 *   <li>{@link #isCountBytesAsChars()} - Determines whether to count bytes (UTF-8/16) instead of characters.</li>
 * </ul>
 */
public interface CountCharactersAware {

    @Property(description = "ID of the label component to display remaining and entered characters.")
    String getCounter();

    @Property(description = "Template text to display in counter.", implicitDefaultValue = "{0}")
    String getCounterTemplate();

    @Property(description = "Specifies whether to count bytes (UTF-8/16) instead of characters.", defaultValue = "false")
    boolean isCountBytesAsChars();
}
