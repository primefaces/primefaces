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
package org.primefaces.component.importconstants;

import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ImportConstantsTagHandlerTest {

    @Test
    public void test() {
        System.err.println(MyConstants.H2);
        Map<String, Object> constants = ImportConstantsTagHandler.collectConstants(MyConstants.class);
        Assertions.assertEquals(4, constants.size());
        Assertions.assertEquals("h1", constants.get("H1"));
        Assertions.assertEquals("h3", constants.get("H3"));
        Assertions.assertEquals("i", constants.get("I"));
        Assertions.assertEquals("h2override", constants.get("H2"));
    }
    
    class MyConstants extends MyConstants2 {
        public static final String H1 = "h1";
        public static final String H2 = "h2override";
    }
    
    class MyConstants2 extends MyConstants3 {
        public static final String H2 = "h2";
    }
    
    class MyConstants3 implements MyInterface {
        public static final String H3 = "h3";
    }
    
    interface MyInterface {
        public static final String I = "i";
    }
}
