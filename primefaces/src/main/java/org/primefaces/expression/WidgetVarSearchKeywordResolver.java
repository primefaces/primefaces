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
package org.primefaces.expression;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jakarta.faces.FacesException;
import jakarta.faces.component.UIComponent;
import jakarta.faces.component.search.SearchExpressionContext;
import jakarta.faces.component.search.SearchKeywordContext;
import jakarta.faces.component.search.SearchKeywordResolver;
import jakarta.faces.component.visit.VisitContext;
import jakarta.faces.context.FacesContext;

public class WidgetVarSearchKeywordResolver extends SearchKeywordResolver {

    private static final Pattern PATTERN = Pattern.compile("widgetVar\\(([\\w-_]+)\\)");

    @Override
    public boolean isResolverForKeyword(SearchExpressionContext searchExpressionContext, String keyword) {
        return keyword != null && keyword.startsWith("widgetVar(");
    }

    @Override
    public void resolve(SearchKeywordContext context, UIComponent current, String keyword) {
        try {
            Matcher matcher = PATTERN.matcher(keyword);

            if (matcher.matches()) {
                FacesContext facesContext = context.getSearchExpressionContext().getFacesContext();

                VisitContext visitContext = VisitContext.createVisitContext(context.getSearchExpressionContext().getFacesContext(), null,
                        context.getSearchExpressionContext().getVisitHints());

                String widgetVar = matcher.group(1);

                WidgetVarVisitCallback visitCallback = new WidgetVarVisitCallback(widgetVar);
                facesContext.getViewRoot().visitTree(
                        visitContext,
                        visitCallback);

                if (visitCallback.getComponent() != null) {
                    context.invokeContextCallback(visitCallback.getComponent());
                }
            }
            else {
                throw new FacesException("Expression does not match following pattern @widgetVar(var). Expression: \"" + keyword + "\"");
            }

        }
        catch (Exception e) {
            throw new FacesException("Expression does not match following pattern @widgetVar(var). Expression: \"" + keyword + "\"", e);
        }
    }

    @Override
    public boolean isPassthrough(SearchExpressionContext searchExpressionContext, String keyword) {
        return true;
    }
}
