/*
 * The MIT License
 *
 * Copyright (c) 2009-2026 PrimeFaces
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
package org.primefaces.component.wizard;

import org.primefaces.component.tabview.Tab;
import org.primefaces.mock.CollectingResponseWriter;
import org.primefaces.mock.FacesContextMock;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

class WizardRendererTest {

    @ParameterizedTest
    @CsvSource({"first,true,1,0", "second,true,1,1", "second,false,1,0", "missing,true,0,0", "hidden,true,0,0"})
    void rendersStateClasses(String step, boolean highlight, int currentCount, int completedCount) throws Exception {
        CollectingResponseWriter writer = new CollectingResponseWriter();
        FacesContextMock context = new FacesContextMock(writer);
        try {
            Wizard wizard = new Wizard();
            wizard.setStep(step);
            wizard.setHighlightCompletedSteps(highlight);
            for (String id : new String[] {"first", "hidden", "second", "third"}) {
                Tab tab = new Tab();
                tab.setId(id);
                tab.setRendered(!"hidden".equals(id));
                if ("third".equals(id)) {
                    tab.setTitleStyleClass("custom p-state-highlight");
                }
                wizard.getChildren().add(tab);
            }
            new WizardRenderer().encodeStepStatus(context, wizard);
            String html = writer.toString();
            assertEquals(currentCount, html.split("p-wizard-step-current", -1).length - 1);
            assertEquals(completedCount, html.split("p-wizard-step-completed", -1).length - 1);
            assertTrue(html.contains("data-pf-user-highlight"));
            assertTrue(html.contains("custom"));
        }
        finally {
            context.release();
        }
    }
}
