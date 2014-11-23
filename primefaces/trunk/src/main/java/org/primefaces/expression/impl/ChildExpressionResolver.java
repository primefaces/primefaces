/*
 * Copyright 2009-2014 PrimeTek.
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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.FacesException;
import javax.faces.component.UIComponent;

import org.primefaces.expression.SearchExpressionResolver;

/**
 * {@link SearchExpressionResolver} for the "@child" keyword.
 */
public class ChildExpressionResolver implements SearchExpressionResolver {

    private static final Pattern PATTERN = Pattern.compile("@child\\((\\d+)\\)");

    public UIComponent resolveComponent(UIComponent source, UIComponent last, String expression) {

        try {
            Matcher matcher = PATTERN.matcher(expression);

            if (matcher.matches()) {

                int childNumber = Integer.parseInt(matcher.group(1));

                if (childNumber + 1 > last.getChildCount()) {
                    throw new FacesException("Component with clientId \"" +
                            last.getClientId() + "\" has fewer children as \"" + childNumber + "\". Expression: \"" + expression + "\"");
                }

                return last.getChildren().get(childNumber);

            } else {
                throw new FacesException("Expression does not match following pattern @child(n). Expression: \"" + expression + "\"");
            }

        } catch (Exception e) {
            throw new FacesException("Expression does not match following pattern @child(n). Expression: \"" + expression + "\"", e);
        }
    }
}
