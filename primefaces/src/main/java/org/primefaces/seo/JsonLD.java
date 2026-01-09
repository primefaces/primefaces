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
package org.primefaces.seo;

import org.primefaces.model.seo.JsonLDItem;
import org.primefaces.model.seo.JsonLDModel;
import org.primefaces.util.FastStringWriter;
import org.primefaces.util.LangUtils;

import java.io.IOException;
import java.util.List;

import jakarta.faces.context.FacesContext;
import jakarta.faces.context.ResponseWriter;

import org.json.JSONObject;

/**
 * Create a script of type `ld+json` for Advanced SEO using JSON for Linking Data.
 * Data is messy and disconnected. JSON-LD organizes and connects it, creating a better Web.
 *
 * @see <a href="https://json-ld.org/">JSON for Linking Data</a>
 * @since 12.0.0
 */
public class JsonLD {

    private JsonLD() { }

    public static void encode(FacesContext context, JsonLDModel model, String id) throws IOException {
        ResponseWriter writer = context.getResponseWriter();

        writer.startElement("script", null);
        writer.writeAttribute("id", id, null);
        writer.writeAttribute("type", "application/ld+json", null);

        encodeModel(context, model);

        writer.endElement("script");
    }

    public static void encodeModel(FacesContext context, JsonLDModel model) throws IOException {
        if (model != null) {
            ResponseWriter writer = context.getResponseWriter();

            try (FastStringWriter fsw = new FastStringWriter()) {
                fsw.write("{");
                writeOption(fsw, "@context", model.getContext(), false);
                writeOption(fsw, "@type", model.getType(), true);
                encodeItems(fsw, model.getItemListType(), model.getItems(), true);
                fsw.write("}");

                writer.write(fsw.toString());
            }
        }
    }

    public static void encodeItems(FastStringWriter writer, String optionName, List<JsonLDItem> items, boolean hasComma) throws IOException {
        if (items == null) {
            return;
        }

        if (hasComma) {
            writer.write(",");
        }

        writer.write("\"" + optionName + "\":");
        writer.write("[");
        for (int i = 0; i < items.size(); i++) {
            JsonLDItem item = items.get(i);

            if (i != 0) {
                writer.write(",");
            }

            encodeItem(writer, item);
        }
        writer.write("]");
    }

    public static void encodeItem(FastStringWriter writer, JsonLDItem item) throws IOException {
        writer.write("{");
        writeOption(writer, "@type", item.getType(), false);
        writeOption(writer, "position", item.getPosition(), true);
        writeOption(writer, "name", item.getName(), true);
        writeOption(writer, "item", item.getItem(), true);
        writer.write("}");
    }

    public static void writeOption(FastStringWriter writer, String name, Object value, boolean hasComma) throws IOException {
        if (value != null && LangUtils.isNotBlank(value.toString())) {
            if (hasComma) {
                writer.write(",");
            }
            writer.write(JSONObject.quote(name) + ":" + JSONObject.valueToString(value));
        }
    }
}
