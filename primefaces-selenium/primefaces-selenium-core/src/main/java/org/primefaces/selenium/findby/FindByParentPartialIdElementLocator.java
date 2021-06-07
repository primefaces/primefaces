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
package org.primefaces.selenium.findby;

import java.util.List;

import org.openqa.selenium.*;
import org.openqa.selenium.support.pagefactory.ElementLocator;

public class FindByParentPartialIdElementLocator implements ElementLocator {

    private final WebDriver driver;
    private final ElementLocator parentLocator;
    private final FindByParentPartialId annotation;

    public FindByParentPartialIdElementLocator(WebDriver driver, ElementLocator parentLocator, FindByParentPartialId annotation) {
        this.driver = driver;
        this.parentLocator = parentLocator;
        this.annotation = annotation;
    }

    @Override
    public WebElement findElement() {
        List<WebElement> elements = findElements();
        if (elements == null || elements.isEmpty()) {
            throw new NoSuchElementException("Cannot locate element using: " + annotation.value());
        }
        return elements.get(0);
    }

    @Override
    public List<WebElement> findElements() {
        WebElement parent = parentLocator.findElement();

        String parentId = parent.getAttribute("id");
        if (parentId == null || parentId.trim().isEmpty()) {
            throw new WebDriverException("Id of parent element is null or empty!");
        }

        By by;
        if (annotation.name().length() > 0) {
            by = By.name(parentId + annotation.name());
        }
        else {
            by = By.id(parentId + annotation.value());
        }
        if (annotation.searchFromRoot()) {
            return driver.findElements(by);
        }

        return parent.findElements(by);
    }
}
