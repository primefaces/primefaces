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
package org.primefaces.integrationtests.inputnumber;

import org.primefaces.integrationtests.general.utilities.TestUtils;

import java.io.Serializable;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;

import lombok.Data;

@Named
@ViewScoped
@Data
public class InputNumber006 implements Serializable {

    private static final long serialVersionUID = -3197892739640653975L;
    private Integer value;

    private Integer value2;

    @PostConstruct
    public void init() {

    }

    public String buildSomeOutput() {
        if (value == null) {
            return "no value";
        }
        else {
            return "value = " + value;
        }
    }

    public String buildSomeSecondOutput() {
        if (value2 == null) {
            return "no value";
        }
        else {
            return "value = " + value2;
        }
    }

    public void someAction() {
        TestUtils.addMessage("some action; value: " + value);
    }

    public void someSecondAction() {
        TestUtils.addMessage("some second action; value: " + value2);
    }

}
