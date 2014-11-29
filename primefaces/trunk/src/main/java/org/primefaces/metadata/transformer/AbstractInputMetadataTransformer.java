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

import java.io.IOException;
import javax.faces.component.EditableValueHolder;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.component.html.HtmlInputSecret;
import javax.faces.component.html.HtmlInputText;
import javax.faces.context.FacesContext;
import org.primefaces.context.RequestContext;

public abstract class AbstractInputMetadataTransformer implements MetadataTransformer {
    
    private static final String ATTRIBUTE_REQUIRED_MARKER = "pfRequired";
    
    public void transform(FacesContext context, RequestContext requestContext, UIComponent component) throws IOException {
        if (component instanceof EditableValueHolder && component instanceof UIInput) {
            transformInput(context, requestContext, (UIInput) component);
        }
    }
    
    protected abstract void transformInput(FacesContext context, RequestContext requestContext, UIInput component) throws IOException;
    
    protected void setMaxlength(UIInput input, int maxlength) {
        if (input instanceof HtmlInputText) {
            ((HtmlInputText) input).setMaxlength(maxlength);
        }
        else if (input instanceof HtmlInputSecret) {
            ((HtmlInputSecret) input).setMaxlength(maxlength);
        }
    }
    
    protected int getMaxlength(UIInput input) {
        if (input instanceof HtmlInputText) {
            return ((HtmlInputText) input).getMaxlength();
        }
        else if (input instanceof HtmlInputSecret) {
            return ((HtmlInputSecret) input).getMaxlength();
        }
        
        return Integer.MIN_VALUE;
    }
    
    protected boolean isMaxlenghtSet(UIInput input) {
        return getMaxlength(input) != Integer.MIN_VALUE;
    }
    
    protected void markAsRequired(UIInput input, boolean value) {
        input.getAttributes().put(ATTRIBUTE_REQUIRED_MARKER, value);
    }
    
    public static boolean isMarkedAsRequired(UIInput input) {
        Object value = input.getAttributes().get(ATTRIBUTE_REQUIRED_MARKER);
        
        if (value == null) {
            return false;
        }
        
        return (Boolean) value;
    }
}
