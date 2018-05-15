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
package org.primefaces.component.imagecompare;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.renderkit.CoreRenderer;
import org.primefaces.util.WidgetBuilder;

public class ImageCompareRenderer extends CoreRenderer {

    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        ImageCompare compare = (ImageCompare) component;

        encodeMarkup(context, compare);
        encodeScript(context, compare);
    }

    protected void encodeScript(FacesContext context, ImageCompare compare) throws IOException {
        String clientId = compare.getClientId(context);

        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("ImageCompare", compare.resolveWidgetVar(), clientId)
                .attr("leftimage", getResourceURL(context, compare.getLeftImage()))
                .attr("rightimage", getResourceURL(context, compare.getRightImage()));
        wb.finish();
    }

    protected void encodeMarkup(FacesContext context, ImageCompare compare) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        writer.startElement("div", compare);
        writer.writeAttribute("style", "width: " + compare.getWidth() + "px;" + "height: " + compare.getHeight() + "px;", null);
        writer.writeAttribute("id", compare.getClientId(context), "id");
        renderDynamicPassThruAttributes(context, compare);
        writer.endElement("div");
    }


}
