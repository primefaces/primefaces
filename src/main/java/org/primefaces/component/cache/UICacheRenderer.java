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
package org.primefaces.component.cache;

import java.io.IOException;
import java.io.StringWriter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.cache.CacheProvider;
import org.primefaces.context.PrimeApplicationContext;
import org.primefaces.renderkit.CoreRenderer;

public class UICacheRenderer extends CoreRenderer {

    @Override
    public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
        UICache uiCache = (UICache) component;

        if (!uiCache.isDisabled()) {
            ResponseWriter writer = context.getResponseWriter();
            CacheProvider cacheProvider = PrimeApplicationContext.getCurrentInstance(context).getCacheProvider();
            String key = uiCache.getKey();
            String region = uiCache.getRegion();

            if (key == null) {
                key = uiCache.getClientId(context);
            }

            if (region == null) {
                region = context.getViewRoot().getViewId();
            }

            String output = (String) cacheProvider.get(region, key);
            if (output == null) {
                StringWriter stringWriter = new StringWriter();
                ResponseWriter clonedWriter = writer.cloneWithWriter(stringWriter);
                context.setResponseWriter(clonedWriter);
                renderChildren(context, uiCache);

                output = stringWriter.getBuffer().toString();
                cacheProvider.put(region, key, output);
                context.setResponseWriter(writer);

                uiCache.setCacheSetInCurrentRequest(true);
            }

            writer.write(output);
        }
        else {
            renderChildren(context, uiCache);
        }
    }

    @Override
    public boolean getRendersChildren() {
        return true;
    }
}
