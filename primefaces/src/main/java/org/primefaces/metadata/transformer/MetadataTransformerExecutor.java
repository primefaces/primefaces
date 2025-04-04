/*
 * The MIT License
 *
 * Copyright (c) 2009-2025 PrimeTek Informatics
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

import org.primefaces.context.PrimeApplicationContext;
import org.primefaces.metadata.transformer.impl.BeanValidationInputMetadataTransformer;

import java.io.IOException;
import java.util.List;

import jakarta.faces.FacesException;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.event.AbortProcessingException;
import jakarta.faces.event.PreRenderComponentEvent;
import jakarta.faces.event.SystemEvent;
import jakarta.faces.event.SystemEventListener;

public class MetadataTransformerExecutor implements SystemEventListener {

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

            if (!applicationContext.getMetadataTransformers().isEmpty()) {
                for (int i = 0; i < applicationContext.getMetadataTransformers().size(); i++) {
                    applicationContext.getMetadataTransformers().get(i).transform(context, applicationContext, component);
                }
            }
        }
    }

    public static void registerMetadataTransformer(MetadataTransformer metadataTransformer) {
        PrimeApplicationContext.getCurrentInstance(FacesContext.getCurrentInstance())
                .getMetadataTransformers().add(metadataTransformer);
    }

    public static MetadataTransformer removeMetadataTransformer(final Class<? extends MetadataTransformer> clazz) {
        List<MetadataTransformer> transformers = PrimeApplicationContext.getCurrentInstance(FacesContext.getCurrentInstance())
                .getMetadataTransformers();
        MetadataTransformer transformer = transformers.stream()
                .filter(t -> t.getClass().equals(clazz))
                .findFirst()
                .orElse(null);
        if (transformer != null) {
            transformers.remove(transformer);
        }
        return transformer;
    }
}
