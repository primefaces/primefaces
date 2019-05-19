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
package org.primefaces.component.inputmask;

import org.junit.Assert;
import org.junit.Test;

import java.util.regex.Pattern;

import org.primefaces.mock.FacesContextMock;

public class InputMaskTest {

    @Test
    public void translatePhoneMaskIntoRegex() {
        InputMask inputMask = new InputMask();
        inputMask.setMask("(999) 999-9999? x99999");
        Pattern pattern = new InputMaskRenderer().translateMaskIntoRegex(new FacesContextMock(), inputMask.getMask());
        Assert.assertEquals("\\([0-9][0-9][0-9]\\) [0-9][0-9][0-9]\\-[0-9][0-9][0-9][0-9] ?x?[0-9]?[0-9]?[0-9]?[0-9]?[0-9]?", pattern.pattern());
        Assert.assertTrue(pattern.matcher("(012) 345-6789").matches());
    }

    @Test
    public void issue3566() {
        InputMask inputMask = new InputMask();
        inputMask.setMask("a*9");
        Pattern pattern = new InputMaskRenderer().translateMaskIntoRegex(new FacesContextMock(), inputMask.getMask());
        Assert.assertEquals("[A-Za-z][A-Za-z0-9][0-9]", pattern.pattern());
        Assert.assertTrue(pattern.matcher("aX3").matches());
    }

}
