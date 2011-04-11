/*
 * Copyright 2009-2011 Prime Technology.
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
package org.primefaces.component.api;

import javax.faces.component.UINamingContainer;
import javax.faces.context.FacesContext;

public class UIData extends javax.faces.component.UIData {

    private String baseClientId = null;

    private StringBuilder builder;

    @Override
    public String getClientId(FacesContext context) {
        if(baseClientId == null) {
            baseClientId = super.getClientId(context);
        }

        return baseClientId;
    }

    @Override
    public String getContainerClientId(FacesContext context) {
        String containerClientId = super.getContainerClientId(context);

        int rowIndex = getRowIndex();
        if (rowIndex == -1)
        {
            return containerClientId;
        }

        StringBuilder bld = getBuilder();
        
        return bld.append(containerClientId).append(UINamingContainer.getSeparatorChar(context)).append(rowIndex).toString();
    }

    public StringBuilder getBuilder() {
        if(builder == null) {
            builder = new StringBuilder();
        }
        builder.setLength(0);
        
        return builder;
    }
}
