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
package org.primefaces.component.inputmask;

import java.io.IOException;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import org.primefaces.context.PrimeApplicationContext;

import org.primefaces.renderkit.InputRenderer;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.HTML;
import org.primefaces.util.WidgetBuilder;

public class InputMaskRenderer extends InputRenderer {

    private final static Logger logger = Logger.getLogger(InputMaskRenderer.class.getName());

    private final static String REGEX_METACHARS = "<([{\\^-=$!|]})?*+.>".replaceAll(".", "\\\\$0");
    private final static Pattern REGEX_METACHARS_PATTERN = Pattern.compile("[" + REGEX_METACHARS + "]");
    
    @Override
    public void decode(FacesContext context, UIComponent component) {
        InputMask inputMask = (InputMask) component;

        if (inputMask.isDisabled() || inputMask.isReadonly()) {
            return;
        }

        decodeBehaviors(context, inputMask);

        String clientId = inputMask.getClientId(context);
        String submittedValue = (String) context.getExternalContext().getRequestParameterMap().get(clientId);

        if (submittedValue != null) {
            
            if (!translateMaskIntoRegex(inputMask).matcher(submittedValue).matches()) {
                submittedValue = null;
            }
            
            inputMask.setSubmittedValue(submittedValue);
        }
    }

    // https://github.com/digitalBush/jquery.maskedinput
    // a - Represents an alpha character (A-Z,a-z)
    // 9 - Represents a numeric character (0-9)
    // * - Represents an alphanumeric character (A-Z,a-z,0-9)
    // ? - Makes the following input optional
    protected Pattern translateMaskIntoRegex(InputMask inputMask) {
        String mask = inputMask.getMask();
        
        // Escape regex metacharacters first
        String regex = REGEX_METACHARS_PATTERN.matcher(mask).replaceAll("\\\\$0");
        
        regex = regex.replace("a", "[A-Za-z]").replace("9", "[0-9]").replace("*", "[A-Za-z0-9]");
        int optionalPos = regex.indexOf("\\?");
        if (optionalPos != -1) {
            regex = regex.substring(0, optionalPos) + "(" + regex.substring(optionalPos + 2) + ")?";
        }
        
        return Pattern.compile(regex);
    }
    
    @Override
    public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
        InputMask inputMask = (InputMask) component;

        encodeMarkup(context, inputMask);
        encodeScript(context, inputMask);
    }

    protected void encodeScript(FacesContext context, InputMask inputMask) throws IOException {
        String clientId = inputMask.getClientId(context);
        String mask = inputMask.getMask();
        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("InputMask", inputMask.resolveWidgetVar(), clientId);
        String slotChar = inputMask.getSlotChar();

        if (mask != null) {
            wb.attr("mask", mask)
                    .attr("placeholder", slotChar, null)
                    .attr("autoclear", inputMask.isAutoClear(), true);
        }

        wb.finish();
    }

    protected void encodeMarkup(FacesContext context, InputMask inputMask) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        String clientId = inputMask.getClientId(context);
        String styleClass = inputMask.getStyleClass();
        String defaultClass = InputMask.STYLE_CLASS;
        defaultClass = !inputMask.isValid() ? defaultClass + " ui-state-error" : defaultClass;
        defaultClass = inputMask.isDisabled() ? defaultClass + " ui-state-disabled" : defaultClass;
        styleClass = styleClass == null ? defaultClass : defaultClass + " " + styleClass;

        writer.startElement("input", null);
        writer.writeAttribute("id", clientId, null);
        writer.writeAttribute("name", clientId, null);
        writer.writeAttribute("type", inputMask.getType(), "text");

        String valueToRender = ComponentUtils.getValueToRender(context, inputMask);
        if (valueToRender != null) {
            writer.writeAttribute("value", valueToRender, null);
        }

        renderPassThruAttributes(context, inputMask, HTML.INPUT_TEXT_ATTRS_WITHOUT_EVENTS);
        renderDomEvents(context, inputMask, HTML.INPUT_TEXT_EVENTS);

        if (inputMask.isDisabled()) writer.writeAttribute("disabled", "disabled", "disabled");
        if (inputMask.isReadonly()) writer.writeAttribute("readonly", "readonly", "readonly");
        if (inputMask.getStyle() != null) writer.writeAttribute("style", inputMask.getStyle(), "style");
        if (inputMask.isRequired()) writer.writeAttribute("aria-required", "true", null);

        writer.writeAttribute("class", styleClass, "styleClass");

        if (PrimeApplicationContext.getCurrentInstance(context).getConfig().isClientSideValidationEnabled()) {
            renderValidationMetadata(context, inputMask);
        }

        writer.endElement("input");
    }
}
