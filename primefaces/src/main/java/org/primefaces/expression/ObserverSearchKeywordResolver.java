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

import jakarta.faces.FacesException;
import jakarta.faces.component.UIComponent;
import jakarta.faces.component.search.SearchExpressionContext;
import jakarta.faces.component.search.SearchKeywordContext;
import jakarta.faces.component.search.SearchKeywordResolver;

public class ObserverSearchKeywordResolver extends SearchKeywordResolver {

    @Override
    public boolean isResolverForKeyword(SearchExpressionContext searchExpressionContext, String keyword) {
        return keyword != null && keyword.startsWith("obs(");
    }

    @Override
    public void resolve(SearchKeywordContext context, UIComponent current, String keyword) {
        throw new FacesException("@obs is not supported on the server side... expression \"" + keyword
                + "\" referenced from \"" + current.getClientId() + "\".");
    }

    @Override
    public boolean isPassthrough(SearchExpressionContext searchExpressionContext, String keyword) {
        return true;
    }
}
