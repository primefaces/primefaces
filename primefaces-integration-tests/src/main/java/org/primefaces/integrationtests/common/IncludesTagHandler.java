/*
 * The MIT License
 *
 * Copyright (c) 2009-2021 PrimeTek
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
package org.primefaces.integrationtests.common;

import javax.el.ExpressionFactory;
import javax.el.ValueExpression;
import javax.el.VariableMapper;
import javax.faces.component.UIComponent;
import javax.faces.view.facelets.FaceletContext;
import javax.faces.view.facelets.TagAttribute;
import javax.faces.view.facelets.TagAttributeException;
import javax.faces.view.facelets.TagConfig;
import javax.faces.view.facelets.TagHandler;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

/**
 * Tag handler to include a facelet multiple times with different contextes (objects from "value").
 * The attribute "value" can be either of type java.util.List or array.
 * If the "value" is null, the tag handler works as a standard ui:include.
 */
public class IncludesTagHandler extends TagHandler {

    private TagAttribute src;
    private TagAttribute value;
    private TagAttribute name;

    public IncludesTagHandler(TagConfig config) {
        super(config);

        this.src = this.getRequiredAttribute("src");
        this.value = this.getAttribute("value");
        this.name = this.getAttribute("name");
    }

    @Override
    public void apply(FaceletContext ctx, UIComponent parent) throws IOException {
        String path = this.src.getValue(ctx);
        if (path == null || path.length() == 0) {
            return;
        }

        // wrap the original mapper - this is important when some objects passed into include via ui:param
        // because ui:param invokes setVariable(...) on the set variable mappper instance
        VariableMapper origVarMapper = ctx.getVariableMapper();
        ctx.setVariableMapper(new VariableMapperWrapper(origVarMapper));

        try {
            this.nextHandler.apply(ctx, null);

            ValueExpression ve = (this.value != null) ? this.value.getValueExpression(ctx, Object.class) : null;
            Object objValue = (ve != null) ? ve.getValue(ctx) : null;
            if (objValue == null) {
                // no include
                return;
            }

            int size = 0;

            if (objValue instanceof List) {
                size = ((List) objValue).size();
            }
            else if (objValue.getClass().isArray()) {
                size = ((Object[]) objValue).length;
            }

            if (this.name == null) {
                // no include
                return;
            }

            ExpressionFactory exprFactory = ctx.getFacesContext().getApplication().getExpressionFactory();

            String strName = this.name.getValue(ctx);

            // generate unique Id as a valid Java identifier and use it as variable for the provided value expression
            String uniqueId = "a" + UUID.randomUUID().toString().replaceAll("-", "");
            ctx.getVariableMapper().setVariable(uniqueId, ve);

            // include facelet multiple times
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < size; i++) {
                if ((strName != null) && (strName.length() != 0)) {
                    // create a new value expression in the array notation and bind it to the variable "name"
                    sb.append("#{");
                    sb.append(uniqueId);
                    sb.append("[");
                    sb.append(i);
                    sb.append("]}");

                    ctx.getVariableMapper().setVariable(strName,
                        exprFactory.createValueExpression(ctx, sb.toString(), Object.class));
                }

                // included facelet can access the created above value expression
                ctx.includeFacelet(parent, path);

                // reset for next iteration
                sb.setLength(0);
            }

            // fix for cc:insertChildren (com.sun.faces.facelets.tag.composite.InsertChidrenHandler)
            if (size > 0) {
                Integer idx = (Integer) parent.getAttributes().get("idx");
                if (idx != null) {
                    parent.getAttributes().put("idx", idx + size);
                }
            }
        }
        catch (IOException e) {
            throw new TagAttributeException(this.tag, this.src, "Invalid path : " + path);
        }
        finally {
            // restore original mapper
            ctx.setVariableMapper(origVarMapper);
        }
    }
}
