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

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.component.search.SearchExpressionContext;
import javax.faces.component.search.SearchKeywordContext;
import javax.faces.component.search.SearchKeywordResolver;
import org.primefaces.expression.SearchExpressionHint;
import org.primefaces.expression.SearchExpressionUtils;

public class Jsf23WidgetVarSearchKeywordResolver extends SearchKeywordResolver {

    private static final Pattern PATTERN = Pattern.compile("widgetVar\\((\\w+)\\)");

    @Override
    public boolean isResolverForKeyword(SearchExpressionContext searchExpressionContext, String keyword) {
        return keyword != null && keyword.startsWith("widgetVar(");
    }

    @Override
    public void resolve(SearchKeywordContext context, UIComponent current, String keyword) {
        try {
            Matcher matcher = PATTERN.matcher(keyword);

            if (matcher.matches()) {

                WidgetVarVisitCallback visitCallback = new WidgetVarVisitCallback(matcher.group(1));
                context.getSearchExpressionContext().getFacesContext().getViewRoot().visitTree(
                        SearchExpressionUtils.createVisitContext(
                                context.getSearchExpressionContext().getFacesContext(), SearchExpressionHint.SKIP_UNRENDERED),
                        visitCallback);

                context.invokeContextCallback(visitCallback.getComponent());
            }
            else {
                throw new FacesException("Expression does not match following pattern @widgetVar(var). Expression: \"" + keyword + "\"");
            }

        }
        catch (Exception e) {
            throw new FacesException("Expression does not match following pattern @widgetVar(var). Expression: \"" + keyword + "\"", e);
        }
    }
}
