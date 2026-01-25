/*
 * The MIT License
 *
 * Copyright (c) 2009-2026 PrimeTek Informatics
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
package org.primefaces.behavior.printer;

import org.primefaces.cdk.api.FacesBehaviorDescription;
import org.primefaces.expression.SearchExpressionUtils;
import org.primefaces.util.Constants;
import org.primefaces.util.LangUtils;

import jakarta.faces.application.ResourceDependency;
import jakarta.faces.component.behavior.ClientBehaviorContext;
import jakarta.faces.component.behavior.FacesBehavior;
import jakarta.faces.context.FacesContext;

import org.json.JSONObject;

@FacesBehavior(PrinterBehavior.BEHAVIOR_ID)
@FacesBehaviorDescription("Printer allows sending a specific Faces component to the printer, not the whole page.")
@ResourceDependency(library = "primefaces", name = "jquery/jquery.js")
@ResourceDependency(library = "primefaces", name = "jquery/jquery-plugins.js")
@ResourceDependency(library = "primefaces", name = "printer/printer.js")
@ResourceDependency(library = "primefaces", name = "core.js")
public class PrinterBehavior extends PrinterBehaviorBaseImpl {

    public static final String BEHAVIOR_ID = "org.primefaces.component.PrinterBehavior";

    @Override
    public String getScript(ClientBehaviorContext behaviorContext) {
        FacesContext context = behaviorContext.getFacesContext();

        String component = SearchExpressionUtils.resolveClientIdForClientSide(
                    context, behaviorContext.getComponent(), getTarget());

        String config = getConfiguration();
        if (LangUtils.isNotBlank(config)) {
            // escape it for safety
            JSONObject jsonObject = new JSONObject('{' + config + '}');
            config = jsonObject.toString();
        }
        else {
            config = Constants.EMPTY_STRING;
        }

        return String.format("PrimeFaces.expressions.SearchExpressionFacade.resolveComponentsAsSelector(document.body,'%s').print(%s);return false;",
                    component, config);
    }

}
