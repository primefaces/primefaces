/*
 * The MIT License
 *
 * Copyright (c) 2009-2026 PrimeFaces
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
package org.primefaces.component.tristatemanycheckbox;

import org.primefaces.cdk.api.FacesComponentInfo;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.MessageFactory;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import jakarta.faces.FacesException;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.application.ResourceDependency;
import jakarta.faces.component.FacesComponent;
import jakarta.faces.component.UINamingContainer;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.validator.BeanValidator;
import jakarta.faces.validator.Validator;
import jakarta.faces.validator.ValidatorException;

@FacesComponent(value = TriStateManyCheckbox.COMPONENT_TYPE, namespace = TriStateManyCheckbox.COMPONENT_FAMILY)
@FacesComponentInfo(description = "TriStateManyCheckbox is a group of checkboxes with three states each,"
        + " the value is a Map of the item values to their states.")
@ResourceDependency(library = "primefaces", name = "components.css")
@ResourceDependency(library = "primefaces", name = "tristatemanycheckbox/tristatemanycheckbox.css")
@ResourceDependency(library = "primefaces", name = "jquery/jquery.js")
@ResourceDependency(library = "primefaces", name = "core.js")
@ResourceDependency(library = "primefaces", name = "components.js")
@ResourceDependency(library = "primefaces", name = "tristatemanycheckbox/tristatemanycheckbox.js")
public class TriStateManyCheckbox extends TriStateManyCheckboxBaseImpl {

    public static final String COMPONENT_TYPE = "org.primefaces.component.TriStateManyCheckbox";

    public static final String STYLE_CLASS = "p-tristatemanycheckbox p-widget";
    public static final String VERTICAL_CLASS = "p-tristatemanycheckbox-vertical";
    public static final String ICON_CLASS = "p-chkbox-icon";

    /**
     * The states as the inputs submit them: the first (unchecked), second (checked) and third (crossed) state.
     */
    public static final List<String> STATES = List.of("0", "1", "2");

    /**
     * @param context the {@link FacesContext}
     * @param index the index of the item
     * @return the client id of the input of the item
     */
    public String getItemClientId(FacesContext context, int index) {
        return getClientId(context) + UINamingContainer.getSeparatorChar(context) + index;
    }

    /**
     * Resolves the state of a value of the Map: without converter its string representation (e.g. "1" or 1), with
     * converter the string the converter returns.
     *
     * @param context the {@link FacesContext}
     * @param value a value of the Map, {@code null} is the first state
     * @return the index of the state: 0, 1 or 2
     */
    public int getState(FacesContext context, Object value) {
        if (value == null) {
            return 0;
        }
        Converter converter = getConverter();
        String state = converter == null ? value.toString() : converter.getAsString(context, this, value);
        int index = STATES.indexOf(state);
        if (index == -1) {
            throw new FacesException("State of '" + getClientId(context) + "' must be 0, 1 or 2: " + state);
        }
        return index;
    }

    /**
     * Validates the Map: required means at least one item is not in the first state, the validators get the value of
     * each item (a state), only the bean validation gets the Map (the constraints of the Map property).
     */
    @Override
    protected void validateValue(FacesContext context, Object newValue) {
        if (!isValid() || !(newValue instanceof Map<?, ?> states)) {
            return;
        }

        if (isRequired() && states.values().stream().allMatch(state -> getState(context, state) == 0)) {
            String requiredMessage = getRequiredMessage();
            FacesMessage message = requiredMessage != null
                    ? new FacesMessage(FacesMessage.SEVERITY_ERROR, requiredMessage, requiredMessage)
                    : MessageFactory.getFacesMessage(context, REQUIRED_MESSAGE_ID, FacesMessage.SEVERITY_ERROR,
                            ComponentUtils.getLabel(context, this));
            context.addMessage(getClientId(context), message);
            setValid(false);
            return;
        }

        for (Validator validator : getValidators()) {
            Collection<?> values = validator instanceof BeanValidator ? List.of(states) : states.values();
            for (Object value : values) {
                try {
                    validator.validate(context, this, value);
                }
                catch (ValidatorException e) {
                    addValidatorMessages(context, e);
                    setValid(false);
                    return;
                }
            }
        }
    }

    protected void addValidatorMessages(FacesContext context, ValidatorException e) {
        String clientId = getClientId(context);
        String validatorMessage = getValidatorMessage();
        if (validatorMessage != null) {
            context.addMessage(clientId, new FacesMessage(FacesMessage.SEVERITY_ERROR, validatorMessage, validatorMessage));
        }
        else if (e.getFacesMessages() != null) {
            e.getFacesMessages().forEach(message -> context.addMessage(clientId, message));
        }
        else if (e.getFacesMessage() != null) {
            context.addMessage(clientId, e.getFacesMessage());
        }
    }
}
