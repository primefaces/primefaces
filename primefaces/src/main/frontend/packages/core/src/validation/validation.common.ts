import { expressions } from "../core/core.expressions.js";
import { core, type Core } from "../core/core.js";
import type { BaseWidget } from "../core/core.widget.js";
import { toMessageOrErrorList } from "./validation.helper.js";
import { validationHighlighter } from "./validation.highlighters.js";

function toBoolean(value: unknown): boolean {
    if (value === undefined || value === null) {
        return false;
    }
    if (typeof value === "boolean") {
        return value;
    }
    if (typeof value === "number") {
        return value !== 0 && !Number.isNaN(value);
    }
    if (typeof value === "string") {
        const lower = value.toLowerCase();
        return lower === "" || lower === "false" || lower === "off" || lower === "0" ? false : true;
    }
    return !!value;
}

export function registerCommonValidationMessages(): void {
    const enUs = core.locales['en_US'];
    if (enUs === undefined) {
        core.error("Cannot register common validator messages for default locale, en_US locale not found");
        return;
    }
    $.extend(enUs, {
        decimalSeparator: '.',
        groupingSeparator: ',',
    });
    enUs.messages ??= {};
    $.extend(enUs.messages, {
        'jakarta.faces.component.UIInput.REQUIRED': '{0}: Validation Error: Value is required.',
        'jakarta.faces.converter.IntegerConverter.INTEGER': '{2}: \'{0}\' must be a number consisting of one or more digits.',
        'jakarta.faces.converter.IntegerConverter.INTEGER_detail': '{2}: \'{0}\' must be a number between -2147483648 and 2147483647 Example: {1}',
        'jakarta.faces.converter.LongConverter.LONG': '{2}: \'{0}\' must be a number consisting of one or more digits.',
        'jakarta.faces.converter.LongConverter.LONG_detail': '{2}: \'{0}\' must be a number between -9223372036854775808 to 9223372036854775807 Example: {1}',
        'jakarta.faces.converter.DoubleConverter.DOUBLE': '{2}: \'{0}\' must be a number consisting of one or more digits.',
        'jakarta.faces.converter.DoubleConverter.DOUBLE_detail': '{2}: \'{0}\' must be a number between 4.9E-324 and 1.7976931348623157E308  Example: {1}',
        'jakarta.faces.converter.BigDecimalConverter.DECIMAL': '{2}: \'{0}\' must be a signed decimal number.',
        'jakarta.faces.converter.BigDecimalConverter.DECIMAL_detail': '{2}: \'{0}\' must be a signed decimal number consisting of zero or more digits, that may be followed by a decimal point and fraction.  Example: {1}',
        'jakarta.faces.converter.BigIntegerConverter.BIGINTEGER': '{2}: \'{0}\' must be a number consisting of one or more digits.',
        'jakarta.faces.converter.BigIntegerConverter.BIGINTEGER_detail': '{2}: \'{0}\' must be a number consisting of one or more digits. Example: {1}',
        'jakarta.faces.converter.ByteConverter.BYTE': '{2}: \'{0}\' must be a number between -128 and 127.',
        'jakarta.faces.converter.ByteConverter.BYTE_detail': '{2}: \'{0}\' must be a number between -128 and 127.  Example: {1}',
        'jakarta.faces.converter.CharacterConverter.CHARACTER': '{1}: \'{0}\' must be a valid character.',
        'jakarta.faces.converter.CharacterConverter.CHARACTER_detail': '{1}: \'{0}\' must be a valid ASCII character.',
        'jakarta.faces.converter.ShortConverter.SHORT': '{2}: \'{0}\' must be a number consisting of one or more digits.',
        'jakarta.faces.converter.ShortConverter.SHORT_detail': '{2}: \'{0}\' must be a number between -32768 and 32767 Example: {1}',
        'jakarta.faces.converter.BooleanConverter.BOOLEAN': '{1}: \'{0}\' must be \'true\' or \'false\'',
        'jakarta.faces.converter.BooleanConverter.BOOLEAN_detail': '{1}: \'{0}\' must be \'true\' or \'false\'.  Any value other than \'true\' will evaluate to \'false\'.',
        'jakarta.faces.validator.LongRangeValidator.MAXIMUM': '{1}: Validation Error: Value is greater than allowable maximum of \'{0}\'',
        'jakarta.faces.validator.LongRangeValidator.MINIMUM': '{1}: Validation Error: Value is less than allowable minimum of \'{0}\'',
        'jakarta.faces.validator.LongRangeValidator.NOT_IN_RANGE': '{2}: Validation Error: Specified attribute is not between the expected values of {0} and {1}.',
        'jakarta.faces.validator.LongRangeValidator.TYPE={0}': 'Validation Error: Value is not of the correct type.',
        'jakarta.faces.validator.DoubleRangeValidator.MAXIMUM': '{1}: Validation Error: Value is greater than allowable maximum of \'{0}\'',
        'jakarta.faces.validator.DoubleRangeValidator.MINIMUM': '{1}: Validation Error: Value is less than allowable minimum of \'{0}\'',
        'jakarta.faces.validator.DoubleRangeValidator.NOT_IN_RANGE': '{2}: Validation Error: Specified attribute is not between the expected values of {0} and {1}',
        'jakarta.faces.validator.DoubleRangeValidator.TYPE={0}': 'Validation Error: Value is not of the correct type',
        'jakarta.faces.converter.FloatConverter.FLOAT': '{2}: \'{0}\' must be a number consisting of one or more digits.',
        'jakarta.faces.converter.FloatConverter.FLOAT_detail': '{2}: \'{0}\' must be a number between 1.4E-45 and 3.4028235E38  Example: {1}',
        'jakarta.faces.converter.DateTimeConverter.DATE': '{2}: \'{0}\' could not be understood as a date.',
        'jakarta.faces.converter.DateTimeConverter.DATE_detail': '{2}: \'{0}\' could not be understood as a date. Example: {1}',
        'jakarta.faces.converter.DateTimeConverter.TIME': '{2}: \'{0}\' could not be understood as a time.',
        'jakarta.faces.converter.DateTimeConverter.TIME_detail': '{2}: \'{0}\' could not be understood as a time. Example: {1}',
        'jakarta.faces.converter.DateTimeConverter.DATETIME': '{2}: \'{0}\' could not be understood as a date and time.',
        'jakarta.faces.converter.DateTimeConverter.DATETIME_detail': '{2}: \'{0}\' could not be understood as a date and time. Example: {1}',
        'jakarta.faces.converter.DateTimeConverter.PATTERN_TYPE': '{1}: A \'pattern\' or \'type\' attribute must be specified to convert the value \'{0}\'',
        'jakarta.faces.converter.NumberConverter.CURRENCY': '{2}: \'{0}\' could not be understood as a currency value.',
        'jakarta.faces.converter.NumberConverter.CURRENCY_detail': '{2}: \'{0}\' could not be understood as a currency value. Example: {1}',
        'jakarta.faces.converter.NumberConverter.PERCENT': '{2}: \'{0}\' could not be understood as a percentage.',
        'jakarta.faces.converter.NumberConverter.PERCENT_detail': '{2}: \'{0}\' could not be understood as a percentage. Example: {1}',
        'jakarta.faces.converter.NumberConverter.NUMBER': '{2}: \'{0}\' is not a number.',
        'jakarta.faces.converter.NumberConverter.NUMBER_detail': '{2}: \'{0}\' is not a number. Example: {1}',
        'jakarta.faces.converter.NumberConverter.PATTERN': '{2}: \'{0}\' is not a number pattern.',
        'jakarta.faces.converter.NumberConverter.PATTERN_detail': '{2}: \'{0}\' is not a number pattern. Example: {1}',
        'jakarta.faces.validator.LengthValidator.MINIMUM': '{1}: Validation Error: Length is less than allowable minimum of \'{0}\'',
        'jakarta.faces.validator.LengthValidator.MAXIMUM': '{1}: Validation Error: Length is greater than allowable maximum of \'{0}\'',
        'jakarta.faces.validator.RegexValidator.PATTERN_NOT_SET': 'Regex pattern must be set.',
        'jakarta.faces.validator.RegexValidator.PATTERN_NOT_SET_detail': 'Regex pattern must be set to non-empty value.',
        'jakarta.faces.validator.RegexValidator.NOT_MATCHED': 'Regex Pattern not matched',
        'jakarta.faces.validator.RegexValidator.NOT_MATCHED_detail': 'Regex pattern of \'{0}\' not matched',
        'jakarta.faces.validator.RegexValidator.MATCH_EXCEPTION': 'Error in regular expression.',
        'jakarta.faces.validator.RegexValidator.MATCH_EXCEPTION_detail': 'Error in regular expression, \'{0}\'',
    });
}

/**
 * __PrimeFaces Client Side Validation Framework__
 * 
 * The class for enabling client side validation of form fields.
 */
export class Validation {
    /**
     * Is the Ajax-complete-handler bound?
     */
    private ajaxCompleteBound: boolean = false;

    /**
     * Parameter shortcut mapping for the method {@link Core.vb | PrimeFaces.vb}.
     */
    readonly CFG_SHORTCUTS: Record<string, string> = {
        's': 'source',
        'p': 'process',
        'u': 'update',
        'a': 'ajax',
        'h': 'highlight',
        'f': 'focus',
        'r': 'renderMessages',
        'v': 'validateInvisibleElements'
    };

    /**
     * The class with mostly internal utility methods used to validate data on the client.
     */
    readonly Utils: ValidationUtils = new ValidationUtils();

    /**
     * The object that contains functionality related to handling faces messages, especially validation errror messages.
     * Contains methods for clearing message of an element or adding messages to an element.
     */
    readonly ValidationContext: ValidationContext = new ValidationContext();

    /**
     * Triggers client-side-validation of single or multiple containers (complex validation or simple inputs).
     * @function
     * @param source The source element.
     * @param process The elements to be processed.
     * @param update The elements to be updated.
     * @param highlight If invalid elements should be highlighted.
     * @param focus If the first invalid element should be focused.
     * @param renderMessages If messages should be rendered.
     * @param validateInvisibleElements If invisible elements should be validated.
     * @param logUnrenderedMessages If unrendered messages should be logged.
     * @returns The validation result.
     */
    validate(
        source: JQuery,
        process: string | HTMLElement | JQuery | undefined | null,
        update: string | HTMLElement | JQuery | undefined | null,
        highlight: boolean,
        focus: boolean,
        renderMessages: boolean,
        validateInvisibleElements: boolean,
        logUnrenderedMessages: boolean,
    ): PrimeType.validation.ValidationResult {
        const vc = this.ValidationContext;

        process = expressions.SearchExpressionFacade.resolveComponentsAsSelector(source, process);

        // validate all inputs first
        var inputs = $();
        for (var i = 0; i < process.length; i++) {
            var component = process.eq(i);
            if (component.is(':input')) {
                inputs = inputs.add(component);
            }
            else {
                inputs = inputs.add(component.find(':input:enabled:not(:button)[name]'));
            }
        }
        if (validateInvisibleElements === false) {
            inputs = inputs.filter(':visible');
        }
        for (var i = 0; i < inputs.length; i++) {
            this.validateInput(source, inputs.eq(i), highlight);
        }

        // validate complex validations, which can be applied to any container element
        var nonInputs = $();
        for (let i = 0; i < process.length; i++) {
            var component = process.eq(i);
            if (component.is(':not(:input)')) {
                nonInputs = nonInputs.add(component);
            }
            nonInputs = nonInputs.add(component.find(':not(:input)'));
        }
        nonInputs = nonInputs.filter('[data-p-val]');
        if (validateInvisibleElements === false) {
            nonInputs = nonInputs.filter(':visible');
        }
        for (var i = 0; i < nonInputs.length; i++) {
            this.validateComplex(source, nonInputs.eq(i), highlight);
        }

        // early exit - we don't need to render messages
        if (vc.isEmpty()) {
            return { valid: true, messages: {}, hasUnrenderedMessage: false };
        }

        // render messages
        if (renderMessages === true) {
            const toUpdate = expressions.SearchExpressionFacade.resolveComponentsAsSelector(source, update);
            for (var i = 0; i < toUpdate.length; i++) {
                var component = toUpdate.eq(i);
                this.Utils.renderMessages(vc.messages, component);
            }
        }

        //focus first element
        if (focus === true) {
            for (var key in vc.messages) {
                if (vc.messages.hasOwnProperty(key)) {
                    var el = $(core.escapeClientId(key));
                    if (!el.is(':focusable')) {
                        el.find(':focusable:first').trigger('focus');
                    }
                    else {
                        el.trigger('focus');
                    }
                    break;
                }
            }
        }

        const result = {
            valid: vc.isEmpty(),
            messages : vc.messages,
            hasUnrenderedMessage: false
        };

        for (const clientId in vc.messages) {
            const msgs = vc.messages[clientId] ?? [];
            for (let msg of msgs) {
                if (!msg.rendered) {
                    result.hasUnrenderedMessage = true;
                }
            }
        }

        if (renderMessages && logUnrenderedMessages && result.hasUnrenderedMessage) {
            core.warn("There are some unhandled FacesMessages, this means not every FacesMessage had a chance to be rendered. These unhandled FacesMessages are:");

            for (const clientId in vc.messages) {
                const msgs = vc.messages[clientId] ?? [];
                for (const msg of msgs) {
                    if (!msg.rendered) {
                        core.warn(msg.detail);
                    }
                }
            }
        }

        vc.clear();

        return result;
    }

    /**
     * Searches for all CommandButtons with turned on dynamic CSV and triggers CSV.
     */
    validateButtonsCsvRequirements (): void {
        $('[data-pf-validateclient-dynamic]').each((_, btn) => {
            if (btn instanceof HTMLButtonElement) {
                this.validateButtonCsvRequirements(btn);
            }
        });
    }

    /**
     * Validates the CSV requirements of a `CommandButton`.
     * @param btn Command button whose CSV requirements should be validated.
     */
    validateButtonCsvRequirements(btn: HTMLElement): void {
        const $source = $(btn);
        const cfg: PrimeType.validation.Configuration = {
            ajax: toBoolean(btn.dataset.pfValidateclientAjax),
            process: btn.dataset.pfValidateclientProcess,
            update: btn.dataset.pfValidateclientUpdate
        };
        const process = this.Utils.resolveProcess(cfg, $source);
        const update = this.Utils.resolveUpdate(cfg, $source);

        const widget = core.getWidgetById(btn.id);

        if (widget) {
            if (this.validate($source, process, update, false, false, false, false, false).valid) {
                widget.jq.addClass('ui-state-csv-valid');
                widget.jq.removeClass('ui-state-csv-invalid');
                widget.enable?.();
            } else {
                widget.jq.addClass('ui-state-csv-invalid');
                widget.jq.removeClass('ui-state-csv-valid');
                widget.disable?.();
            }
        } else {
            console.warn('No widget found for ID ' + btn.id);
        }

        this.ValidationContext.clear();
    }

    /**
     * Performs a client-side validation of the given element. The context of this validation is a single field only.
     * If the element is valid, removes old messages from the element.
     * If the value of the element is invalid, adds the appropriate validation failure messages.
     * This is used by `p:clientValidator`.
     * @param el The ID of an input to validate, or the input itself.
     * @param highlight If the invalid element should be highlighted.
     * @param renderMessages If messages should be rendered.
     * @returns `true` if the element is valid, or `false` otherwise.
     */
    validateInstant(el: string | HTMLElement | JQuery, highlight: boolean, renderMessages: boolean): boolean {
        highlight = (highlight === undefined) ? true : highlight;
        renderMessages = (renderMessages === undefined) ? true : renderMessages;

        const vc = this.ValidationContext;

        const element = typeof el === 'string'
            ? $(core.escapeClientId(el))
            : $(el);
            const clientId = element.data(core.CLIENT_ID_DATA) || element.attr('id');

        const messageComponentId = element.data('target-message');
        let messageComponent: JQuery | null = null;
        if (renderMessages === true) {
            if (messageComponentId) {
                messageComponent = messageComponentId === 'p-unbound'
                    ? null
                    : $(core.escapeClientId(messageComponentId));
            }
            else {
                const messageComponents = element.closest('form').find('div.ui-message');
                messageComponent = this.Utils.findTargetMessageComponent(clientId, messageComponents);

                if (messageComponent) {
                    element.data('target-message', messageComponent.attr('id') ?? "");
                }
                else {
                    element.data('target-message', 'p-unbound');
                }
            }

            if (messageComponent) {
                const messageWidget = core.getWidgetById(messageComponent.attr('id') ?? "");
                if (messageWidget) {
                    for (const messageRenderHook of core.getHook("messageRender")) {
                        messageRenderHook.clearMessagesForWidget(messageWidget);
                    }
                }
            }
        }

        this.validateButtonsCsvRequirements();

        this.validateInput(element, element, highlight);

        if (!vc.isEmpty()) {
            if (messageComponent) {
                const messageWidget = core.getWidgetById(messageComponent.attr('id') ?? "");
                if (messageWidget) {
                    for (const messageRenderHook of core.getHook("messageRender")) {
                        messageRenderHook.renderMessageForWidget(messageWidget, vc.messages[clientId] ?? []);
                    }
                }
            }

            vc.clear();
            return false;
        }
        else {
            vc.clear();
            return true;
        }
    }

    /**
     * Performs a client-side validation of (the value of) the given input element. If the element is valid, removes old
     * messages from the element. If the value of the element is invalid, adds the appropriate validation failure
     * messages.
     * @param source The source element.
     * @param element A JQuery instance with a single input element to validate.
     * @param highlight If the invalid element should be highlighted.
     */
    private validateInput(source: JQuery, element: JQuery, highlight: boolean): void {
        var vc = this.ValidationContext;

        if (element.is(':checkbox,:radio') && element.data('p-grouped')) {
            const groupName = element.attr('name') ?? "";

            if (!vc.isGroupValidated(groupName)) {
                vc.addElementGroup(groupName);
            } else {
                return;
            }
        }

        if (element.parent().hasClass('ui-inputnumber')) {
            element = element.parent().children('input:hidden');
        }

        let submittedValue: PrimeType.validation.SubmittedValue | null = this.Utils.getSubmittedValue(element);
        let valid = true;
        const converterId = element.data('p-con');

        if (core.settings.considerEmptyStringNull && typeof submittedValue !== "number" && ((!submittedValue) || submittedValue.length === 0)) {
            submittedValue = null;
        }

        let newValue: unknown = null;
        if (converterId) {
            try {
                newValue = core.converter[converterId]?.convert(element, submittedValue);
            }
            catch (ce) {
                const converterMessageStr: unknown = element.data('p-cmsg');
                const converterMsg = converterMessageStr 
                    ? [{ summary: String(converterMessageStr), detail: String(converterMessageStr) }]
                    : toMessageOrErrorList(ce); 
                valid = false;
                for (const msg of converterMsg) {
                    vc.addMessage(element, msg);
                }
            }
        }
        else {
            newValue = submittedValue;
        }

        var required = element.data('p-required');
        if (required) {
            element.attr('aria-required', "true");
        }

        if (valid && required && (newValue === null || newValue === '')) {
            const requiredMessageStr: unknown = element.data('p-rmsg');
            const requiredMsg = requiredMessageStr
                ? { summary: String(requiredMessageStr), detail: String(requiredMessageStr) }
                : vc.getMessage('jakarta.faces.component.UIInput.REQUIRED', vc.getLabel(element));

            vc.addMessage(element, requiredMsg);

            valid = false;
        }

        let hasValue: boolean;
        if (submittedValue === null) {
            hasValue = false;
        } else if (typeof submittedValue === "number") {
            hasValue = true;
        } else if (typeof submittedValue === "string") {
            hasValue = core.trim(submittedValue).length > 0;
        } else {
            hasValue = submittedValue.length > 0;
        }

        if (valid && (hasValue || core.settings.validateEmptyFields)) {
            let validatorIds = element.data('p-val');
            if (validatorIds) {
                validatorIds = validatorIds.split(',');

                for (let j = 0; j < validatorIds.length; j++) {
                    const validatorId = validatorIds[j];
                    const validator = core.getValidatorById(validatorId);

                    if (validator) {
                        try {
                            validator.validate(element, newValue);
                        }
                        catch (ve) {
                            const validatorMessageStr: unknown = element.data('p-vmsg');
                            const validatorMsg = validatorMessageStr
                                ? [{summary: String(validatorMessageStr), detail: String(validatorMessageStr)}]
                                : toMessageOrErrorList(ve);

                            for (const msg of validatorMsg) {
                                vc.addMessage(element, msg);
                            }

                            valid = false;
                        }
                    }
                }
            }
        }

        const highlighterType = element.data('p-hl') || 'default';
        const highlighter = validationHighlighter.types[highlighterType];

        if (valid) {
            highlighter?.unhighlight(element);
            element.attr('aria-invalid', "false");
        }
        else {
            if (highlight) {
                highlighter?.highlight(element);
            }
            element.attr('aria-invalid', "true");
        }
    }

    /**
     * Performs a client-side validation of (the value of) the given container element. If the element is valid,
     * removes old messages from the element. If the value of the element is invalid, adds the appropriate
     * validation failure messages.
     * @param source the source element.
     * @param element A JQuery instance with a single input element to validate.
     * @param highlight If the invalid element should be highlighted.
     * @returns `true` if the value of the element is valid, `false` otherwise.
     */
    private validateComplex(source: JQuery, element: JQuery, highlight: boolean): boolean {
        const vc = this.ValidationContext;
        let valid = true;

        let validatorIds = element.data('p-val');
        if (validatorIds) {
            validatorIds = validatorIds.split(',');

            for (let j = 0; j < validatorIds.length; j++) {
                const validatorId = validatorIds[j];
                const validator = core.getValidatorById(validatorId);

                if (validator) {
                    try {
                        validator.validate(source, element);
                    }
                    catch (ve) {
                        var validatorMessageStr = element.data('p-vmsg');
                        var validatorMsg = validatorMessageStr
                            ? [{summary: validatorMessageStr, detail: validatorMessageStr}]
                            : toMessageOrErrorList(ve);

                        for (const msg of validatorMsg) {
                            vc.addMessage(element, msg);
                        }

                        valid = false;

                        var highlighterType = element.data('p-hl');

                        var highlighter = highlighterType
                            ? validationHighlighter.types[highlighterType]
                            : validationHighlighter.types[validatorId];

                        if (valid) {
                            if (highlighter) {
                                highlighter.unhighlight(element);
                            }
                            element.attr('aria-invalid', "false");
                        }
                        else {
                            if (highlight && highlighter) {
                                highlighter.highlight(element);
                            }
                            element.attr('aria-invalid', "true");
                        }
                    }
                }
            }
        }

        return valid;
    }


    /**
     * __NOTE__: This is an internal method and should only be used by PrimeFaces itself.
     *
     * Bind to Ajax-Complete-events to update CSV-state after an Ajax-call may have changed state.
     * @internal
     */
    bindAjaxComplete(): void {
        if (this.ajaxCompleteBound) return;

        var $this = this;

        $(document).on('pfAjaxComplete', () => $this.validateButtonsCsvRequirements());

        // also bind to Jakarta Faces (f:ajax) events
        // NOTE: PF always fires "complete" as last event, whereas Jakarta Faces last events are either "success" or "error"
        if (window.faces && faces.ajax) {
            faces.ajax.addOnEvent((data) => {
                // TODO For error (status === "error"), shouldn't we use faces.ajax.addOnError ?
                // An error event will never be passed to listeners registered via addOnEvent
                const status: string = data.status;
                if(status === 'success' || status === 'error') {
                    $this.validateButtonsCsvRequirements();
                }
            });
        }

        this.ajaxCompleteBound = true;
    }
};

/**
 * The class that contains functionality related to handling faces messages, especially validation errror messages.
 * Contains methods for clearing message of an element or adding messages to an element.
 */
export class ValidationContext {
    /**
     * A map between the client ID of an element and a list of faces message for that element.
     */
    messages: Record<string, PrimeType.FacesMessage[]> = {};

    /**
     * A list of element groups to be validated. Usually corresponds to the name of single form element. For some
     * cases such as a select list of checkboxes, a group may correspond to multiple DOM elements.
     */
    elementGroups: string[] = [];

    /**
     * Highlights the passed widget as invalid.
     *
     * @param target The target widget / element.
     */
    highlight(target: string | HTMLElement | JQuery | BaseWidget): void {
        const element = core.resolveAs$(target);
        const highlighterType = element.data('p-hl') || 'default';
        const highlighter = validationHighlighter.types[highlighterType];

        highlighter?.highlight(element);
        element.attr('aria-invalid', "true");
    }

    /**
     * Un-Highlights the passed widget as invalid.
     *
     * @param target The target widget / element.
     */
    unhighlight(target: string | HTMLElement | JQuery | BaseWidget): void {
        const element = core.resolveAs$(target);
        const highlighterType = element.data('p-hl') || 'default';
        const highlighter = validationHighlighter.types[highlighterType];

        highlighter?.unhighlight(element);
        element.attr('aria-invalid', "false");
    }

    /**
     * Adds a faces message to the given element.
     * @param target Element or widget to which to add the message.
     * @param msg Message to add to the given message.
     */
    addMessage(target: string | HTMLElement | JQuery | BaseWidget, msg: Error | PrimeType.BaseFacesMessage): void {
        const clientId = core.resolveAsId(target);

        this.messages[clientId] ??= [];

        // in case of a exception -> lets wrap it into an 'unexpected error'
        const newMessage: PrimeType.FacesMessage = msg instanceof Error
            ? {
                detail : msg.toString(),
                rendered: false,
                severity: "error",
                summary : core.getLocaleSettings().unexpectedError,
            }
            : {
                detail: msg.detail,
                rendered: false,
                severity: msg.severity ?? "error",
                summary: msg.summary,
        };

        this.messages[clientId].push(newMessage);
    }

    /**
     * Reports how many messages were added to this validation context. Note that each component may have several
     * messages.
     * @returns The number of messages added to this validation context.
     */
    getMessagesLength(): number {
        let length = 0;
        let key: string;

        for (key in this.messages) {
            if (this.messages.hasOwnProperty(key)) {
                length++;
            }
        }

        return length;
    }

    /**
     * Checks whether this validation context contains any messages at all.
     * @returns `true` if this validation context contains zero messages, or `false` otherwise.
     */
    isEmpty(): boolean {
        return this.getMessagesLength() === 0;
    }

    /**
     * Removes all messages from this validation context.
     */
    clear(): void {
        this.messages = {};
        this.elementGroups = [];
    }

    /**
     * Shortcut for {@link ValidationUtils.getMessage | PrimeFaces.validation.Utils.getMessage}.
     * @param key The i18n key of a message, such as `jakarta.faces.component.UIInput.REQUIRED` or
     * `jakarta.faces.validator.LengthValidator.MINIMUM`.
     * @param params Optional parameters to insert into the message, e.g. `{0}`
     * will be replaced with the first parameter.
     * @returns The localized faces message for the given key, or `null` if no
     * translation was found for the key.
     */
    getMessage(key: string, ...params: PrimeType.validation.MessageFormatParameter[]): PrimeType.BaseFacesMessage {
        return validation.Utils.getMessage(key, params);
    }

    /**
     * Used when bean validation is enabled. Creates a faces message with the given key and for the given element. The
     * element is used to find the label that is added to the message.
     * @param element Element for which to create the message.
     * @param defaultKey Key of the message.
     * @param msg Default message to show. May be used to find the key of the message.
     * @param params Optional parameters for the message format, placeholders
     * are indices enclosed in braces, e.g. `{0}` or `{1}`.
     * @returns A faces message with the given key for the given element.
     */
    getMessageBV(element: JQuery, defaultKey?: string, msg?: string, ...params: PrimeType.validation.MessageFormatParameter[]): PrimeType.BaseFacesMessage {
        return validation.Utils.getMessageBV(element, defaultKey, msg, ...params);
    }
    
    /**
     * Shortcut for {@link ValidationUtils.getLabel | PrimeFaces.validation.Utils.getLabel}.
     * @param element A DOM element for which to find the label.
     * @returns The label of the given element.
     */
    getLabel(element: JQuery): string {
        return validation.Utils.getLabel(element);
    }

    /**
     * Checks whether the given element group is in the list of groups to be validated. An element group is often
     * just the name of a single INPUT, TEXTAREA or SELECT element, but may also consist of multiple DOM elements,
     * such as in the case of a select list of checkboxes.
     * @param name Name of an element group to check.
     * @returns `true` if the given group is to be validated, or `false` otherwise.
     */
    isGroupValidated(name: string): boolean {
        for (var i = 0; i < this.elementGroups.length; i++) {
            if (this.elementGroups[i] === name) {
                return true;
            }
        }
        return false;
    }

    /**
     * Adds a group to the list of element groups to validate. An element group is often just the name of a single
     * INPUT, TEXTAREA or SELECT element, but may also consist of multiple DOM elements, such as in the case of
     * select list of checkboxes.
     * @param name Name of an element group to add.
     */
    addElementGroup(name: string): void {
        this.elementGroups.push(name);
    }
};

/**
 * The class with mostly internal utility methods used to validate data on the client.
 */
export class ValidationUtils {
    /**
     * Finds the localized text of the given message key. When the current locale does not contain a translation,
     * falls back to the default English locale.
     * @param key The i18n key of a message, such as `jakarta.faces.component.UIInput.REQUIRED` or
     * `jakarta.faces.validator.LengthValidator.MINIMUM`.
     * @param params A list of parameters for the placeholders.
     * @returns The localized faces message for the given key, or `null` if no
     * translation was found for the key.
     */
    getMessage(key: string, params: PrimeType.validation.MessageFormatParameter[]): PrimeType.BaseFacesMessage {
        const locale = core.getLocaleSettings();
        const bundle = (locale.messages && locale.messages[key]) ? locale : core.locales['en_US'];

        let summary = bundle?.messages?.[key];
        
        if (!summary) {
            return {
                summary: "### Message '" + key + "' not found ###",
                detail: "### Message '" + key + "' not found ###",
            };
        }

        summary = this.format(summary, params);

        let detail = bundle?.messages?.[key + '_detail'];
        detail = detail ? this.format(detail, params) : summary;

        return {
            summary: summary,
            detail: detail,
        };
    }

    /**
     * Used when bean validation is enabled. Creates a faces message with the given key and for the given element. The
     * element is used to find the label that is added to the message.
     * @param element Element for which to create the message.
     * @param defaultKey Key of the message.
     * @param msg Default message to show. May be used to find the key of the message.
     * @param params Optional parameters for the message format, placeholders
     * are indices enclosed in braces, e.g. `{0}` or `{1}`.
     * @returns A faces message with the given key for the given element.
     */
    getMessageBV(element: JQuery, defaultKey?: string, msg?: string, ...params: PrimeType.validation.MessageFormatParameter[]): PrimeType.BaseFacesMessage {
        if (msg && msg.charAt(0) !== '{') {
            return { summary : msg, detail : msg };
        }
        else {
            let key = defaultKey;
            if (msg && msg.charAt(0) === '{') {
                key = msg.substring(1, msg.length - 1);
            }

            const locale = core.getLocaleSettings();
            const bundle = (locale.messages && locale.messages[key ?? ""]) ? locale : core.locales['en_US'];

            let summary = bundle?.messages?.[key ?? ""];
            let detail = bundle?.messages?.[key + '_detail'];

            if (!summary) {
                return {
                    summary: "### Message '" + key + "' not found ###",
                    detail: "### Message '" + key + "' not found ###"
                };
            }

            summary = validation.Utils.format(summary, params);
            detail = (detail) ? validation.Utils.format(detail, params) : summary;

            // see #7069
            // simulate the message handling of the server side BeanValidator
            const wrapperBundle = (locale.messages && locale.messages['jakarta.faces.validator.BeanValidator.MESSAGE']) ? locale : core.locales['en_US'];
            const wrapper = wrapperBundle?.messages?.['jakarta.faces.validator.BeanValidator.MESSAGE'];
            const label = validation.Utils.getLabel(element);
            summary = wrapper?.replace("{0}", summary).replace("{1}", label) ?? summary;
            detail = wrapper?.replace("{0}", detail).replace("{1}", label) ?? detail;

            return { summary : summary, detail : detail };
        }
    }

    /**
     * Given a message with placeholders, replaces the placeholders with the given parameters. The format of the
     * message is similar to, but not quite the same as, the format used by `java.text.MessageFormat`.
     * ```javascript
     * format("Value required for element {0}", ["email"]) // => "Value required for element email"
     * format("Use {0} braces like this: '{0}'", ["simple"]) // => "Use simple braces like this: 'simple'"
     * ```
     * @param str A message with placeholders.
     * @param params A list of parameters for the placeholders.
     * @returns The string with the placeholders replaced with the given params.
     */
    format(str: string, params: PrimeType.validation.MessageFormatParameter[]): string {
        let s = str;
        for (let i = 0; i < params.length; i++) {
            const reg = new RegExp('\\{' + i + '\\}', 'gm');
            s = s.replace(reg, String(params[i] ?? ""));
        }

        return s;
    }

    /**
     * Finds the label of a DOM element. This is either a custom label set on a component, or just the ID of the
     * element. This label is used, for example, as part of a validation error message for the element.
     * @param element A DOM element for which to find the label.
     * @returns The label of the given element.
     */
    getLabel(element: JQuery): string {
        return element.data('p-label') || element.attr('id');
    }

    /**
     * Given a form element (such as input, textarea, select), finds the value that would be sent when the form is
     * submitted.
     * @param element A form element for which to find its value.
     * @returns The value of the form element, or the empty string when it does not have a value.
     */
    getSubmittedValue(element: JQuery): PrimeType.validation.SubmittedValue {
        let value: PrimeType.validation.SubmittedValue | undefined;

        if (element.is(':radio')) {
            value = $('input:radio[name="' + CSS.escape(element.attr('name') ?? "") + '"]:checked').val();
        }
        else if (element.is(':checkbox')) {
            value = element.data('p-grouped') ? $('input:checkbox[name="' + CSS.escape(element.attr('name') ?? "") + '"]:checked').val(): element.prop('checked').toString();
        }
        else if (element.is(':file')) {
            const input = element[0];
            value = input instanceof HTMLInputElement ? input.files ?? undefined : undefined;
        }
        else {
            value = element.val();
        }

        return value === undefined ? "" : value;
    }

    /**
     * For a given ID of a component, finds the DOM element with the message for that component.
     * @param clientId ID of a component for which to find the ui message.
     * @param messageComponents A JQuery instance with a list of `ui-message`s, or `null` if no
     * such element exists.
     * @returns The DOM element with the messages for the given component, or `null` when no such
     * element could be found.
     */
    findTargetMessageComponent(clientId: string, messageComponents: JQuery): JQuery | null {
        for (var i = 0; i < messageComponents.length; i++) {
            var messageComponent = messageComponents.eq(i);
            if (messageComponent.data('target') === clientId) {
                return messageComponent;
            }
        }

        return null;
    }

    /**
     * Renders all given messages in the given containers.
     * @param messages The messages to render.
     * @param containers The container for the messages. Note that this JQuery
     * instance may contain multiple elements -- you should iterate over them. 
     */
    renderMessages(messages: Record<string, PrimeType.FacesMessage[]>, containers: JQuery): void {
        for (const messageDisplayHook of core.getHook("messageRender")) {
            try {
                messageDisplayHook.renderMessagesInContainers(messages, containers);
            } catch (e) {
                core.error("Could not render messages");
                core.error(e);
            }
        }
    }

    /**
     * Resolves `process` attribute of a PrimeFaces component. (e.g. `CommandButton`)
     * @param cfg Configuration of the PrimeFaces component.
     * @param source The source element.
     * @returns Resolved jQuery instance.
     */
    resolveProcess(cfg: PrimeType.validation.Configuration, source: JQuery): JQuery {
        if (cfg.ajax && cfg.process) {
            return expressions.SearchExpressionFacade.resolveComponentsAsSelector(source, cfg.process);
        } else {
            return source.closest('form');
        }
    }

    /**
     * Resolves `update` attribute of a PrimeFaces component. (e.g. `CommandButton`).
     * @param cfg Configuration of the PrimeFaces component.
     * @param source The source element.
     * @returns Resolved jQuery instance.
     */
    resolveUpdate(cfg: PrimeType.validation.Configuration, source: JQuery): JQuery {
        if (cfg.ajax && cfg.update) {
            return expressions.SearchExpressionFacade.resolveComponentsAsSelector(source, cfg.update);
        } else {
            return source.closest('form');
        }
    }
};

/**
 * __PrimeFaces Client Side Validation Framework__
 * 
 * The object for enabling client side validation of form fields.
 */
export const validation: Validation = new Validation();
