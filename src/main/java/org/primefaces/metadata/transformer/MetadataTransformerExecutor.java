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
package org.primefaces.metadata.transformer;

import org.primefaces.metadata.transformer.impl.BeanValidationInputMetadataTransformer;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.PreRenderComponentEvent;
import javax.faces.event.SystemEvent;
import javax.faces.event.SystemEventListener;
import org.primefaces.context.PrimeApplicationContext;

public class MetadataTransformerExecutor implements SystemEventListener {

    private static final List<MetadataTransformer> METADATA_TRANSFORMERS = new ArrayList<>();

    private static final MetadataTransformer BV_INPUT_METADATA_TRANSFORMER = new BeanValidationInputMetadataTransformer();

    @Override
    public void processEvent(SystemEvent event) throws AbortProcessingException {
        try {
            if (event instanceof PreRenderComponentEvent) {
                PreRenderComponentEvent preRenderComponentEvent = (PreRenderComponentEvent) event;

                execute(PrimeApplicationContext.getCurrentInstance(FacesContext.getCurrentInstance()),
                        preRenderComponentEvent.getComponent());
            }
        }
        catch (IOException e) {
            throw new FacesException(e);
        }
    }

    @Override
    public boolean isListenerForSource(Object source) {
        return source instanceof UIComponent;
    }

    public static void execute(PrimeApplicationContext applicationContext, UIComponent component) throws IOException {
        if (applicationContext.getConfig().isTransformMetadataEnabled()) {

            FacesContext context = FacesContext.getCurrentInstance();

            if (applicationContext.getConfig().isBeanValidationEnabled()) {
                BV_INPUT_METADATA_TRANSFORMER.transform(context, applicationContext, component);
            }

            if (!METADATA_TRANSFORMERS.isEmpty()) {
                for (int i = 0; i < METADATA_TRANSFORMERS.size(); i++) {
                    METADATA_TRANSFORMERS.get(i).transform(context, applicationContext, component);
                }
            }
        }
    }

    public static void registerMetadataTransformer(final MetadataTransformer metadataTransformer) {
        METADATA_TRANSFORMERS.add(metadataTransformer);
    }

    public static MetadataTransformer removeMetadataTransformer(final Class<? extends MetadataTransformer> clazz) {
        Iterator<MetadataTransformer> iterator = METADATA_TRANSFORMERS.iterator();
        while (iterator.hasNext()) {
            MetadataTransformer metadataTransformer = iterator.next();
            if (metadataTransformer.getClass().equals(clazz)) {
                iterator.remove();
                return metadataTransformer;
            }
        }

        return null;
    }
}
