/**
 * The MIT License
 *
 * Copyright (c) 2009-2019 PrimeTek
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
package org.primefaces.model.menu;

import java.util.List;
import java.util.Map;
import javax.el.MethodExpression;
import org.primefaces.component.api.Confirmable;
import org.primefaces.util.SerializableFunction;

public interface MenuItem extends MenuElement, Confirmable {

    public String getIcon();

    public String getIconPos();

    public String getTitle();

    public boolean shouldRenderChildren();

    public boolean isDisabled();

    public String getOnclick();

    public String getStyle();

    public String getStyleClass();

    /**
     * The URL to redirect to after the menu item has been clicked. Similar to
     * {@code outcome} which allows to specify a navigation case, but the value
     * is not touched (no prepending of the contextPath, not appending the
     * sessionId or windowId), just encoded.
     *
     * Specifying a {@code url} which is not {@code null} causes {@code command}
     * to be ignored.
     *
     * @return the URL.
     */
    public String getUrl();

    public String getTarget();

    /**
     * The JSF outcome of a navigation case which is resolved by the configured
     * {@code NavigationHandler}. Similar to {@code url}, but {@code url}
     * allows to specify fully qualified URLs.
     *
     * @return the outcome.
     */
    public String getOutcome();

    public String getFragment();

    public boolean isIncludeViewParams();

    public boolean isAjax();

    public Object getValue();

    public void setStyleClass(String styleClass);

    public Map<String, List<String>> getParams();

    public void setParam(String key, Object value);

    public boolean isDynamic();

    /**
     * A {@link MethodExpression} in the form of a string which is called after the
     * menu item has been clicked. It is ignored when {@code url} is not
     * {@code null}.
     *
     * @return The outcome, which will be used for navigation.
     */
    public String getCommand();

    /**
     * Lambda alternative to the {@link #getCommand()}.
     *
     * @return The outcome, which will be used for navigation.
     */
    public SerializableFunction<MenuItem, String> getFunction();

    public boolean isImmediate();

    public String getClientId();

    public String getContainerStyle();

    public String getContainerStyleClass();

    public boolean isEscape();

    public String getRel();

}
