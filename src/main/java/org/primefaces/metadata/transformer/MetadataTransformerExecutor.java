/*
 * Copyright 2009-2014 PrimeTek.
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
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import org.primefaces.config.ConfigContainer;
import org.primefaces.context.RequestContext;

public class MetadataTransformerExecutor {

    private final static List<MetadataTransformer> METADATA_TRANSFORMERS = new ArrayList<MetadataTransformer>();

    private final static MetadataTransformer BV_INPUT_METADATA_TRANSFORMER = new BeanValidationInputMetadataTransformer();
    
    public static void execute(ConfigContainer config, UIComponent component) throws IOException {
        if (config.isTransformMetadataEnabled()) {

            FacesContext context = FacesContext.getCurrentInstance();
            RequestContext requestContext = RequestContext.getCurrentInstance();
            
            if (config.isBeanValidationAvailable()) {
                BV_INPUT_METADATA_TRANSFORMER.transform(context, requestContext, component);
            }

            if (METADATA_TRANSFORMERS.size() > 0) {
                for (int i = 0; i < METADATA_TRANSFORMERS.size(); i++) {
                    METADATA_TRANSFORMERS.get(i).transform(context, requestContext, component);
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
