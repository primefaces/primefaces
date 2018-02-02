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
package org.primefaces.util;

import static org.junit.Assert.*;

import org.junit.Test;

public class ComponentUtilsTest {

    @Test
    public void escapeSelector() {
        String id = "test";

        assertEquals("test", ComponentUtils.escapeSelector(id));

        id = "form:test";
        assertEquals("form\\\\:test", ComponentUtils.escapeSelector(id));
    }

    @Test
    public void createContentDisposition() {
        assertEquals("attachment;filename=\"Test%20Spaces.txt\"; filename*=UTF-8''Test%20Spaces.txt", ComponentUtils.createContentDisposition("attachment", "Test Spaces.txt"));
    }
}
