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
package org.primefaces.component.themeswitcher;

import java.io.IOException;

import javax.faces.context.FacesContext;

import org.primefaces.component.selectonemenu.SelectOneMenu;
import org.primefaces.component.selectonemenu.SelectOneMenuRenderer;
import org.primefaces.expression.SearchExpressionFacade;
import org.primefaces.expression.SearchExpressionUtils;
import org.primefaces.util.WidgetBuilder;

public class ThemeSwitcherRenderer extends SelectOneMenuRenderer {

    @Override
    protected void encodeScript(FacesContext context, SelectOneMenu menu) throws IOException {
        ThemeSwitcher ts = (ThemeSwitcher) menu;
        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("ThemeSwitcher", ts)
                .attr("effect", ts.getEffect(), null)
                .attr("effectSpeed", ts.getEffectSpeed(), null)
                .attr("appendTo", SearchExpressionFacade.resolveClientId(context, menu, menu.getAppendTo(),
                            SearchExpressionUtils.SET_RESOLVE_CLIENT_SIDE), null)
                    .attr("syncTooltip", menu.isSyncTooltip(), false)
                    .attr("labelTemplate", menu.getLabelTemplate(), null)
                    .attr("autoWidth", menu.isAutoWidth(), true)
                    .attr("dynamic", menu.isDynamic(), false);

        if (menu.isFilter()) {
            wb.attr("filter", true)
                    .attr("filterMatchMode", menu.getFilterMatchMode(), null)
                    .nativeAttr("filterFunction", menu.getFilterFunction(), null)
                    .attr("caseSensitive", menu.isCaseSensitive(), false);
        }

        encodeClientBehaviors(context, menu);

        wb.finish();
    }
}
