/**
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
package org.primefaces.expression.impl;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.primefaces.expression.SearchExpressionResolver;

/**
 * {@link SearchExpressionResolver} for the "@child" keyword.
 */
public class ChildExpressionResolver implements SearchExpressionResolver {

    private static final Pattern PATTERN = Pattern.compile("@child\\((\\d+)\\)");

    @Override
    public UIComponent resolveComponent(FacesContext context, UIComponent source, UIComponent last, String expression, int options) {

        Matcher matcher = PATTERN.matcher(expression);

        if (matcher.matches()) {
            int childNumber = Integer.parseInt(matcher.group(1));

            if (childNumber + 1 > last.getChildCount()) {
                throw new FacesException("Component with clientId \""
                        + last.getClientId(context) + "\" has fewer children as \"" + childNumber + "\". Expression: \"" + expression + "\"");
            }

            List<UIComponent> list = last.getChildren();
            int count = 0;
            for (int i = 0; i < last.getChildCount(); i++) {

                String className = list.get(i).getClass().getName();
                if (!className.contains("UIInstructions") && !className.contains("UILeaf")) {
                    count++;
                }
                if (count == childNumber + 1) {
                    return last.getChildren().get(childNumber);
                }
            }
            if (count < childNumber) {
                throw new FacesException("Component with clientId \""
                        + last.getClientId(context) + "\" has fewer children as \"" + childNumber + "\". Expression: \"" + expression + "\"");
            }
        }
        else {
            throw new FacesException("Expression does not match following pattern @child(n). Expression: \"" + expression + "\"");
        }

        return null;
    }
}
