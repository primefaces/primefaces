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
package org.primefaces.component.datatable;

import javax.faces.view.facelets.ComponentConfig;
import javax.faces.view.facelets.ComponentHandler;
import javax.faces.view.facelets.MetaRule;
import javax.faces.view.facelets.MetaRuleset;

import org.primefaces.component.api.UIData;
import org.primefaces.facelets.MethodRule;

public class DataTableHandler extends ComponentHandler {

    private static final MetaRule SORT_FUNCTION
            = new MethodRule("sortFunction", Integer.class, new Class[]{Object.class, Object.class});

    private static final MetaRule DRAGGABLE_ROWS_FUNCTION
            = new MethodRule("draggableRowsFunction", null, new Class[]{UIData.class});

    public DataTableHandler(ComponentConfig config) {
        super(config);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected MetaRuleset createMetaRuleset(Class type) {
        MetaRuleset metaRuleset = super.createMetaRuleset(type);

        metaRuleset.addRule(SORT_FUNCTION);
        metaRuleset.addRule(DRAGGABLE_ROWS_FUNCTION);

        return metaRuleset;
    }
}
