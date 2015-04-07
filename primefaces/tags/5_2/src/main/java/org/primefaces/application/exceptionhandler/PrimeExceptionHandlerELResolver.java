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
package org.primefaces.application.exceptionhandler;

import java.beans.FeatureDescriptor;
import java.util.Iterator;
import javax.el.ELContext;
import javax.el.ELResolver;
import javax.faces.context.FacesContext;

public class PrimeExceptionHandlerELResolver extends ELResolver {

    public static final String EL_NAME = "pfExceptionHandler";
    
    @Override
    public Object getValue(ELContext elContext, Object base, Object property) {

        if (EL_NAME.equals(property)) {
            elContext.setPropertyResolved(true);
            
            FacesContext context = FacesContext.getCurrentInstance();
            ExceptionInfo info = (ExceptionInfo) context.getAttributes().get(ExceptionInfo.ATTRIBUTE_NAME);
            
            if (info == null) {
                info = (ExceptionInfo) context.getExternalContext().getSessionMap().get(ExceptionInfo.ATTRIBUTE_NAME);
            }
            
            return info;
        }

        return null;
    }

    @Override
    public Class<?> getType(ELContext context, Object base, Object property) {
        return null;
    }

    @Override
    public void setValue(ELContext context, Object base, Object property, Object value) {
        
    }

    @Override
    public boolean isReadOnly(ELContext context, Object base, Object property) {
        return true;
    }

    @Override
    public Iterator<FeatureDescriptor> getFeatureDescriptors(ELContext context, Object base) {
        return null;
    }

    @Override
    public Class<?> getCommonPropertyType(ELContext context, Object base) {
        return null;
    }
}
