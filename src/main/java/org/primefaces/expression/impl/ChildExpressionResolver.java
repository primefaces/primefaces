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

    private static final Pattern CHILD_PATTERN = Pattern.compile("@child\\((\\d+)\\)");

    public UIComponent resolve(UIComponent source, UIComponent last, String expression) {

        try {
            Matcher matcher = CHILD_PATTERN.matcher(expression);

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
