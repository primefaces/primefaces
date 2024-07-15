/*
 * The MIT License
 *
 * Copyright (c) 2009-2024 PrimeTek Informatics
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
package org.primefaces.validate.base;

import org.primefaces.el.ValueExpressionAwareAttributeHandler;

import javax.faces.view.facelets.FaceletContext;
import javax.faces.view.facelets.MetaRule;
import javax.faces.view.facelets.Metadata;
import javax.faces.view.facelets.MetadataTarget;
import javax.faces.view.facelets.TagAttribute;

public class PrimeValidatorMetaRule extends MetaRule {
    public static final PrimeValidatorMetaRule INSTANCE = new PrimeValidatorMetaRule();

    private PrimeValidatorMetaRule() { }

    @Override
    public Metadata applyRule(String name, TagAttribute attribute, MetadataTarget meta) {
        if (meta.isTargetInstanceOf(AbstractPrimeValidator.class) && !attribute.isLiteral()) {
            Class type = meta.getPropertyType(name);
            if (type == null) {
                type = Object.class;
            }

            return new ValueExpressionMetadata(name, type, attribute);
        }

        return null;
    }

    static final class ValueExpressionMetadata extends Metadata {
        private final String name;
        private final TagAttribute attr;
        private final Class type;

        ValueExpressionMetadata(String name, Class type, TagAttribute attr) {
            this.name = name;
            this.attr = attr;
            this.type = type;
        }

        @Override
        public void applyMetadata(FaceletContext ctx, Object instance) {
            ValueExpressionAwareAttributeHandler attributeHandler = ((AbstractPrimeValidator) instance).getAttributeHandler();
            attributeHandler.setValueExpression(this.name, this.attr.getValueExpression(ctx, this.type));
        }
    }
}
