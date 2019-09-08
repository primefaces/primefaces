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
package org.primefaces.component.contextmenu;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.primefaces.component.api.Widget;
import org.primefaces.component.menu.AbstractMenu;
import org.primefaces.component.tieredmenu.TieredMenuRenderer;
import org.primefaces.expression.SearchExpressionFacade;
import org.primefaces.util.WidgetBuilder;

public class ContextMenuRenderer extends TieredMenuRenderer {

    @Override
    protected void encodeScript(FacesContext context, AbstractMenu abstractMenu) throws IOException {
        ContextMenu menu = (ContextMenu) abstractMenu;
        String clientId = menu.getClientId(context);

        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("ContextMenu", menu.resolveWidgetVar(context), clientId);

        String _for = menu.getFor();
        if (_for != null) {
            UIComponent target = SearchExpressionFacade.resolveComponent(context, menu, _for);

            wb.attr("target", target.getClientId(context));

            if (target instanceof Widget) {
                wb.attr("targetWidgetVar", ((Widget) target).resolveWidgetVar(context));
            }
        }

        wb.attr("nodeType", menu.getNodeType(), null)
                .attr("event", menu.getEvent(), null)
                .attr("selectionMode", menu.getSelectionMode(), "multiple")
                .callback("beforeShow", "function(event)", menu.getBeforeShow())
                .attr("targetFilter", menu.getTargetFilter(), null);

        wb.finish();
    }

    @Override
    protected void encodeMarkup(FacesContext context, AbstractMenu abstractMenu) throws IOException {
        ContextMenu menu = (ContextMenu) abstractMenu;
        String style = menu.getStyle();
        String styleClass = menu.getStyleClass();
        styleClass = styleClass == null ? ContextMenu.CONTAINER_CLASS : ContextMenu.CONTAINER_CLASS + " " + styleClass;

        encodeMenu(context, menu, style, styleClass, "menu");
    }
}
