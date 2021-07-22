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
package org.primefaces.selenium.component.base;

import java.util.List;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.primefaces.selenium.PrimeSelenium;
import org.primefaces.selenium.component.model.data.Page;
import org.primefaces.selenium.component.model.data.Paginator;

public abstract class AbstractPageableData extends AbstractComponent {

    @FindBy(className = "ui-paginator")
    private WebElement paginator;

    public abstract List<WebElement> getRowsWebElement();

    public WebElement getPaginatorWebElement() {
        return paginator;
    }

    public Paginator getPaginator() {
        return new Paginator(getPaginatorWebElement());
    }

    public void selectPage(Page page) {
        Page activePage = getPaginator().getActivePage();

        if (activePage != null && activePage.getNumber() == page.getNumber()) {
            // we are already on the right page
        }
        else {
            PrimeSelenium.guardAjax(page.getWebElement()).click();
        }
    }

    public void selectPage(int number) {
        for (Page page : getPaginator().getPages()) {
            if (page.getNumber() == number) {
                selectPage(page);
                break;
            }
        }
    }
}
