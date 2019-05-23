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
