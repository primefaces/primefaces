/*
 * The MIT License
 *
 * Copyright (c) 2009-2026 PrimeTek Informatics
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
package org.primefaces.cdk.impl;

import java.util.Arrays;
import java.util.Set;

public final class CdkUtils {

    private CdkUtils() {
    }

    public static boolean shouldIgnoreProperty(Class<?> clazz, String property) {
        return shouldIgnoreProperty(clazz.getName(), property);
    }

    public static boolean shouldIgnoreProperty(String clazz, String property) {
        Set<String> properties = Set.of("attributes", "behaviors", "rendererType", "bindings", "passThroughAttributes", "systemEventListeners", "valid",
                "actionExpression", "methodBindingActionListener", "localValueSet", "saved", "lastId", "rowIndex");
        return properties.contains(property);
    }

    public static boolean isJavaKeyword(String name) {
        String[] javaKeywords = {
            "abstract", "assert", "boolean", "break", "byte",
            "case", "catch", "char", "class", "const",
            "continue", "default", "do", "double", "else",
            "enum", "extends", "final", "finally", "float",
            "for", "goto", "if", "implements", "import",
            "instanceof", "int", "interface", "long", "native",
            "new", "package", "private", "protected", "public",
            "return", "short", "static", "strictfp", "super",
            "switch", "synchronized", "this", "throw", "throws",
            "transient", "try", "void", "volatile", "while",
            "true", "false", "null"
        };
        return Arrays.asList(javaKeywords).contains(name);
    }

    public static String getWellKnownDefaultValue(String name) {
        switch (name) {
            case "rendered": return "true";
            default:         return "";
        }
    }

    public static String getWellKnownDescription(String name) {
        switch (name) {
            case "id":              return "Unique identifier of the component in a namingContainer.";
            case "rendered":        return "Boolean value to specify the rendering of the component, when set to false component will not be rendered.";
            case "binding":         return "An EL expression referring to a server side UIComponent instance in a backing bean.";
            case "ariaLabel":       return "The aria-label attribute is used to define a string that labels the current element for accessibility.";
            case "accesskey":       return "Access key to transfer focus to the input element.";
            case "dir":             return "Direction indication for text that does not inherit directionality.";
            case "immediate":       return "When set true, process validations logic is executed at apply request values phase for this component.";
            case "required":        return "Marks component as required.";
            case "label":           return "A localized user presentable name.";
            case "disabled":        return "Specifies that an element should be disabled.";
            case "readonly":        return "Specifies that an input field is read-only.";
            case "style":           return "Specifies an inline CSS style for an element.";
            case "styleClass":      return "Specifies one or more CSS class names for an element.";
            case "tabindex":        return "Specifies the tab order of an element.";
            case "title":           return "Specifies extra information about an element (displayed as a tooltip).";
            case "onclick":         return "Fires when a mouse click on the element.";
            case "ondblclick":      return "Fires when a mouse double-click on the element.";
            case "onmousedown":     return "Fires when a mouse button is pressed down on an element.";
            case "onmouseup":       return "Fires when a mouse button is released over an element.";
            case "onmouseover":     return "Fires when the mouse pointer moves onto an element.";
            case "onmouseout":      return "Fires when the mouse pointer moves out of an element.";
            case "onmousemove":     return "Fires when the mouse pointer is moving while it is over an element.";
            case "onkeydown":       return "Fires when a user is pressing a key.";
            case "onkeyup":         return "Fires when a user releases a key.";
            case "onkeypress":      return "Fires when a user presses a key.";
            case "onfocus":         return "Fires when an element gets focus.";
            case "onblur":          return "Fires when an element loses focus.";
            case "onchange":        return "Fires when the value of an element has been changed.";
            case "width":           return "Specifies the width of an element.";
            case "height":          return "Specifies the height of an element.";
            default:                return "";
        }
    }

    /**
     * Capitalises the first character of {@code s}.
     */
    public static String capitalize(String s) {
        return (s == null || s.isEmpty()) ? s : Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }
}
