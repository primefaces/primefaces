/*
 * The MIT License
 *
 * Copyright (c) 2009-2021 PrimeTek
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
package org.primefaces.integrationtests.tree;

import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.primefaces.selenium.AbstractPrimePageTest;
import org.primefaces.selenium.component.Messages;
import org.primefaces.selenium.component.model.Msg;

public abstract class AbstractTreeTest extends AbstractPrimePageTest {

    protected void assertConfiguration(JSONObject cfg) {
        assertConfiguration(cfg, "tree");
    }

    protected void assertConfiguration(JSONObject cfg, String widgetVar) {
        assertNoJavascriptErrors();
        System.out.println("Tree Config = " + cfg);
        Assertions.assertEquals(widgetVar, cfg.getString("widgetVar"));
    }

    protected void assertMessage(Messages messages, int index, String summary, String detail) {
        Msg message = messages.getMessage(index);
        Assertions.assertEquals(summary, message.getSummary());
        Assertions.assertEquals(detail, message.getDetail());
    }

}
