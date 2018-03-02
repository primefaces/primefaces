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

    private final static List<MetadataTransformer> METADATA_TRANSFORMERS = new ArrayList<MetadataTransformer>();

    private final static MetadataTransformer BV_INPUT_METADATA_TRANSFORMER = new BeanValidationInputMetadataTransformer();

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

            if (METADATA_TRANSFORMERS.size() > 0) {
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
