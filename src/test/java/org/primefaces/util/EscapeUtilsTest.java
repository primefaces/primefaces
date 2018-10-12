/*
 * Copyright 2009-2018 PrimeTek.
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
package org.primefaces.util;

import static org.junit.Assert.*;

import org.junit.Test;

public class EscapeUtilsTest {

    @Test
    public void forXmlTag() {
        final String input = "hello world & >";

        // invalid
        assertEquals("Not valid XML tag", "hello world &amp; &gt;", EscapeUtils.forXml(input)); 
        // invalid
        assertEquals("Not valid XML tag", "hello world &amp; >", EscapeUtils.forXmlAttribute(input));
        // correct!!!
        assertEquals("Valid!!", "hello_world_.26._.gt.", EscapeUtils.forXmlTag(input));
    }

}
