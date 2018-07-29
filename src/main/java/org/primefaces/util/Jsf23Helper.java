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
package org.primefaces.util;

import javax.faces.context.FacesContext;
import org.primefaces.expression.impl.Jsf23WidgetVarSearchKeywordResolver;

// helper to avoid java.lang.NoClassDefFoundError's in older environments
public class Jsf23Helper {

    public static void addSearchKeywordResolvers() {
        FacesContext.getCurrentInstance()
                            .getApplication()
                            .addSearchKeywordResolver(new Jsf23WidgetVarSearchKeywordResolver());
    }
}
