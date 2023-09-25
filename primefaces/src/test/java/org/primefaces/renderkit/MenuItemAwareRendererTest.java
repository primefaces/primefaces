/*
 * The MIT License
 *
 * Copyright (c) 2009-2023 PrimeTek Informatics
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
package org.primefaces.renderkit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.primefaces.model.menu.DefaultMenuItem;

public class MenuItemAwareRendererTest extends MenuItemAwareRenderer {

    @Test
    public void testIsMenuItemSubmitting() {
        DefaultMenuItem item = null;

        item = new DefaultMenuItem();
        item.setOncomplete("test");
        Assertions.assertFalse(isMenuItemSubmitting(null, null, item));

        item = new DefaultMenuItem();
        item.setCommand("#{test.test()}");
        Assertions.assertTrue(isMenuItemSubmitting(null, null, item));

        item = new DefaultMenuItem();
        item.setFunction((menuitem) -> {
            return "test";
        });
        Assertions.assertTrue(isMenuItemSubmitting(null, null, item));

        item = new DefaultMenuItem();
        item.setUpdate("test");
        Assertions.assertTrue(isMenuItemSubmitting(null, null, item));

        item = new DefaultMenuItem();
        item.setProcess("test");
        Assertions.assertTrue(isMenuItemSubmitting(null, null, item));

        item = new DefaultMenuItem();
        item.setProcess("test");
        item.setAjax(false);
        Assertions.assertFalse(isMenuItemSubmitting(null, null, item));

        item = new DefaultMenuItem();
        item.setResetValues(true);
        item.setAjax(false);
        Assertions.assertFalse(isMenuItemSubmitting(null, null, item));
    }

}
