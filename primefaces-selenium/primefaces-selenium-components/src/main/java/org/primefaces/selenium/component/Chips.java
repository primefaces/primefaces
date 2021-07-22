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
package org.primefaces.selenium.component;

import java.util.List;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.primefaces.selenium.PrimeSelenium;
import org.primefaces.selenium.component.base.AbstractInputComponent;
import org.primefaces.selenium.component.base.ComponentUtils;
import org.primefaces.selenium.findby.FindByParentPartialId;

/**
 * Component wrapper for the PrimeFaces {@code p:chips}.
 */
public abstract class Chips extends AbstractInputComponent {

    @FindByParentPartialId("_input")
    private WebElement input;

    @Override
    public WebElement getInput() {
        return input;
    }

    public List<String> getValues() {
        List<WebElement> chipTokens = getChipTokens();
        return chipTokens.stream()
                    .map(token -> token.findElement(By.className("ui-chips-token-label")).getText())
                    .collect(Collectors.toList());
    }

    public List<WebElement> getChipTokens() {
        return findElements(By.cssSelector("ul li.ui-chips-token"));
    }

    public void addValue(String value) {
        WebElement chipsInput = getInput();
        chipsInput.sendKeys(value);
        if (ComponentUtils.hasAjaxBehavior(getRoot(), "itemSelect")) {
            PrimeSelenium.guardAjax(chipsInput).sendKeys(Keys.ENTER);
        }
        else {
            chipsInput.sendKeys(Keys.ENTER);
        }
    }

    public void removeValue(String value) {
        for (WebElement chipToken : getChipTokens()) {
            if (chipToken.findElement(By.className("ui-chips-token-label")).getText().equals(value)) {
                WebElement closeIcon = chipToken.findElement(By.className("ui-icon-close"));
                if (ComponentUtils.hasAjaxBehavior(getRoot(), "itemUnselect")) {
                    PrimeSelenium.guardAjax(closeIcon).click();
                }
                else {
                    closeIcon.click();
                }
            }
        }
    }

    /**
     * Converts the current list into a separator delimited list for mass editing while keeping original order of the items or closes the editor turning the
     * values back into chips.
     */
    public void toggleEditor() {
        PrimeSelenium.executeScript(getWidgetByIdScript() + ".toggleEditor();");
    }
}
