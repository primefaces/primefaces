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

import org.openqa.selenium.WebElement;
import org.primefaces.selenium.PrimeSelenium;

public final class ComponentUtils {

    private ComponentUtils() {
        // prevent instantiation
    }

    public static boolean hasAjaxBehavior(WebElement element, String behavior) {
        if (!hasBehavior(element, behavior)) {
            return false;
        }

        String id = element.getAttribute("id");
        String result = PrimeSelenium.executeScript("return " + getWidgetByIdScript(id) + ".getBehavior('" + behavior + "').toString();");
        return isAjaxScript(result);
    }

    public static boolean hasBehavior(WebElement element, String behavior) {
        if (!isWidget(element)) {
            return false;
        }

        String id = element.getAttribute("id");
        return PrimeSelenium.executeScript("return " + getWidgetByIdScript(id) + ".hasBehavior('" + behavior + "');");
    }

    public static boolean isWidget(WebElement element) {
        String id = element.getAttribute("id");
        if (id == null || id.isEmpty()) {
            return false;
        }

        return PrimeSelenium.executeScript("return " + getWidgetByIdScript(id) + " != null;");
    }

    public static boolean isAjaxScript(String script) {
        if (script == null || script.isEmpty()) {
            return false;
        }

        return script.contains("PrimeFaces.ab(") || script.contains("pf.ab(") || script.contains("mojarra.ab(") || script.contains("jsf.ajax.request");
    }

    public static String getWidgetConfiguration(WebElement element) {
        String id = element.getAttribute("id");
        return PrimeSelenium.executeScript("return JSON.stringify(" + getWidgetByIdScript(id) + ".cfg, function(key, value) {\n" +
                    "  if (typeof value === 'function') {\n" +
                    "    return value.toString();\n" +
                    "  } else if (value && value.constructor && value.constructor.name === 'RegExp') {\n" +
                    "    return value.toString();\n" +
                    "  } else {\n" +
                    "    return value;\n" +
                    "  }\n" +
                    "});");
    }

    public static String getWidgetByIdScript(String id) {
        return "PrimeFaces.getWidgetById('" + id + "')";
    }

    /**
     * When using Chrome what can happen is the keys are sent too fast and the Javascript of the input can't process it fast enough. This method sends the keys
     * 1 at a time using Chrome so the input can properly process each key.
     *
     * @param input the input component to send keys to
     * @param value the value to send to the input
     */
    public static void sendKeys(WebElement input, CharSequence value) {
        if (input == null || value == null) {
            return;
        }

        // using classname here to prevent classloading issues
        if (PrimeSelenium.isChrome()) {
            // focus the input
            input.click();

            // Chrome send keys 1 at a time
            for (int i = 0; i < value.length(); i++) {
                char c = value.charAt(i);
                input.sendKeys(Character.toString(c));
            }
        }
        else {
            // Firefox handles it correctly
            input.sendKeys(value);
        }
    }

}
