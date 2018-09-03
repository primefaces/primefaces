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
package org.primefaces.facelets;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.el.MethodExpression;
import javax.faces.FacesException;

import javax.faces.view.facelets.FaceletContext;
import javax.faces.view.facelets.MetaRule;
import javax.faces.view.facelets.Metadata;
import javax.faces.view.facelets.MetadataTarget;
import javax.faces.view.facelets.TagAttribute;
import javax.faces.view.facelets.TagAttributeException;

/**
 * Optional Rule for binding Method[Binding|Expression] properties
 *
 * @author Mike Kienenberger
 * @author Jacob Hookom
 *
 * Implementation copied from Facelets 1.1.14, as it got hidden by JSF 2.0
 */
public class MethodRule extends MetaRule {

    private final String methodName;
    private final Class returnTypeClass;
    private final Class[] params;

    public MethodRule(String methodName, Class returnTypeClass, Class[] params) {
        this.methodName = methodName;
        this.returnTypeClass = returnTypeClass;
        this.params = params;
    }

    @Override
    public Metadata applyRule(String name, TagAttribute attribute, MetadataTarget meta) {
        if (false == name.equals(this.methodName)) {
            return null;
        }

        Class<?> type = meta.getPropertyType(name);

        if (MethodExpression.class.equals(type)) {
            Method method = meta.getWriteMethod(name);
            if (method != null) {
                return new MethodExpressionMetadata(method, attribute, this.returnTypeClass, this.params);
            }
        }
        else if (type != null && "javax.faces.el.MethodBinding".equals(type.getName())) {
            throw new FacesException("javax.faces.el.MethodBinding should not be used anymore!");
        }

        return null;
    }

    private static class MethodExpressionMetadata extends Metadata {

        private final Method method;
        private final TagAttribute attribute;
        private final Class[] paramList;
        private final Class returnType;

        public MethodExpressionMetadata(Method method, TagAttribute attribute, Class returnType, Class[] paramList) {
            this.method = method;
            this.attribute = attribute;
            this.paramList = paramList;
            this.returnType = returnType;
        }

        @Override
        public void applyMetadata(FaceletContext ctx, Object instance) {
            MethodExpression expr = this.attribute.getMethodExpression(ctx, this.returnType, this.paramList);

            try {
                this.method.invoke(instance, new Object[]{expr});
            }
            catch (InvocationTargetException e) {
                throw new TagAttributeException(this.attribute, e.getCause());
            }
            catch (Exception e) {
                throw new TagAttributeException(this.attribute, e);
            }
        }
    }

}
