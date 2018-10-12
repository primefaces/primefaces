/*
 * Copyright 2009-2013 PrimeTek.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
        Pattern pattern = new InputMaskRenderer().translateMaskIntoRegex(new FacesContextMock(), inputMask);
        Assert.assertEquals("\\([0-9][0-9][0-9]\\) [0-9][0-9][0-9]\\-[0-9][0-9][0-9][0-9] ?x?[0-9]?[0-9]?[0-9]?[0-9]?[0-9]?", pattern.pattern());
        Assert.assertTrue(pattern.matcher("(012) 345-6789").matches());
    }

    @Test
    public void issue3566() {
        InputMask inputMask = new InputMask();
        inputMask.setMask("a*9");
        Pattern pattern = new InputMaskRenderer().translateMaskIntoRegex(new FacesContextMock(), inputMask);
        Assert.assertEquals("[A-Za-z][A-Za-z0-9][0-9]", pattern.pattern());
        Assert.assertTrue(pattern.matcher("aX3").matches());
    }

}
