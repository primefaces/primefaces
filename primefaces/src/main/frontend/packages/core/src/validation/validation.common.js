// see #7395
// we always add validation/beanvalidation.js on each page, also if no PrimeFaces component is available
// so... just check if primefaces.js was rendered
if (window.PrimeFaces) {

    /**
     * A shortcut for `PrimeFaces.validation.validate` used by server-side renderers.
     * If the `ajax` attribute is set to `true` (the default is `false`), all inputs configured by the `process` attribute are validated
     * and all messages for the inputs configured by the `update` attribute are rendered.
     * Otherwise, if the `ajax` attribute is set to the `false`, all inputs of the parent form, of the `source` attribute, are processed and updated.
     * @function
     * @param {Partial<PrimeFaces.validation.ShorthandConfiguration>} cfg An configuration.
     * @return {boolean} `true` if the request would not result in validation errors, or `false` otherwise.
     */
    PrimeFaces.vb = function(cfg) {
        for (var option in cfg) {
            if (!cfg.hasOwnProperty(option)) {
                continue;
            }

            // just pass though if no mapping is available
            if (PrimeFaces.validation.CFG_SHORTCUTS[option]) {
                cfg[PrimeFaces.validation.CFG_SHORTCUTS[option]] = cfg[option];
                delete cfg[option];
            }
        }

        var highlight = cfg.highlight || true;
        var focus = cfg.focus || true;
        var renderMessages = cfg.renderMessages || true;
        var validateInvisibleElements = cfg.validateInvisibleElements || false;
        var logUnrenderedMessages = cfg.logUnrenderedMessages || renderMessages;

        var $source = $(cfg.source);

        var process = PrimeFaces.validation.Utils.resolveProcess(cfg, $source);
        var update = PrimeFaces.validation.Utils.resolveUpdate(cfg, $source);

        var result = PrimeFaces.validation.validate($source, process, update, highlight, focus, renderMessages, validateInvisibleElements, logUnrenderedMessages);
        return result.valid;
    };

    /**
     * A shortcut for `PrimeFaces.validation.validateInstant`. This is used by `p:clientValidator`.
     * @function
     * @param {string | HTMLElement | JQuery} element The ID of an element to validate, or the element itself.
     * @param {boolean} highlight If the invalid element should be highlighted.
     * @param {boolean} renderMessages If messages should be rendered.
     * @return {boolean} `true` if the element is valid, or `false` otherwise.
     */
    PrimeFaces.vi = function(element, highlight, renderMessages) {
        return PrimeFaces.validation.validateInstant(element, highlight, renderMessages);
    };

    /**
     * PrimeFaces Client Side Validation Framework
     */
    $.extend(PrimeFaces.locales['en_US'],{
        decimalSeparator: '.',
        groupingSeparator: ',',
        messages: {
            'javax.faces.component.UIInput.REQUIRED': '{0}: Validation Error: Value is required.',
            'javax.faces.converter.IntegerConverter.INTEGER': '{2}: \'{0}\' must be a number consisting of one or more digits.',
            'javax.faces.converter.IntegerConverter.INTEGER_detail': '{2}: \'{0}\' must be a number between -2147483648 and 2147483647 Example: {1}',
            'javax.faces.converter.LongConverter.LONG': '{2}: \'{0}\' must be a number consisting of one or more digits.',
            'javax.faces.converter.LongConverter.LONG_detail': '{2}: \'{0}\' must be a number between -9223372036854775808 to 9223372036854775807 Example: {1}',
            'javax.faces.converter.DoubleConverter.DOUBLE': '{2}: \'{0}\' must be a number consisting of one or more digits.',
            'javax.faces.converter.DoubleConverter.DOUBLE_detail': '{2}: \'{0}\' must be a number between 4.9E-324 and 1.7976931348623157E308  Example: {1}',
            'javax.faces.converter.BigDecimalConverter.DECIMAL': '{2}: \'{0}\' must be a signed decimal number.',
            'javax.faces.converter.BigDecimalConverter.DECIMAL_detail': '{2}: \'{0}\' must be a signed decimal number consisting of zero or more digits, that may be followed by a decimal point and fraction.  Example: {1}',
            'javax.faces.converter.BigIntegerConverter.BIGINTEGER': '{2}: \'{0}\' must be a number consisting of one or more digits.',
            'javax.faces.converter.BigIntegerConverter.BIGINTEGER_detail': '{2}: \'{0}\' must be a number consisting of one or more digits. Example: {1}',
            'javax.faces.converter.ByteConverter.BYTE': '{2}: \'{0}\' must be a number between -128 and 127.',
            'javax.faces.converter.ByteConverter.BYTE_detail': '{2}: \'{0}\' must be a number between -128 and 127.  Example: {1}',
            'javax.faces.converter.CharacterConverter.CHARACTER': '{1}: \'{0}\' must be a valid character.',
            'javax.faces.converter.CharacterConverter.CHARACTER_detail': '{1}: \'{0}\' must be a valid ASCII character.',
            'javax.faces.converter.ShortConverter.SHORT': '{2}: \'{0}\' must be a number consisting of one or more digits.',
            'javax.faces.converter.ShortConverter.SHORT_detail': '{2}: \'{0}\' must be a number between -32768 and 32767 Example: {1}',
            'javax.faces.converter.BooleanConverter.BOOLEAN': '{1}: \'{0}\' must be \'true\' or \'false\'',
            'javax.faces.converter.BooleanConverter.BOOLEAN_detail': '{1}: \'{0}\' must be \'true\' or \'false\'.  Any value other than \'true\' will evaluate to \'false\'.',
            'javax.faces.validator.LongRangeValidator.MAXIMUM': '{1}: Validation Error: Value is greater than allowable maximum of \'{0}\'',
            'javax.faces.validator.LongRangeValidator.MINIMUM': '{1}: Validation Error: Value is less than allowable minimum of \'{0}\'',
            'javax.faces.validator.LongRangeValidator.NOT_IN_RANGE': '{2}: Validation Error: Specified attribute is not between the expected values of {0} and {1}.',
            'javax.faces.validator.LongRangeValidator.TYPE={0}': 'Validation Error: Value is not of the correct type.',
            'javax.faces.validator.DoubleRangeValidator.MAXIMUM': '{1}: Validation Error: Value is greater than allowable maximum of \'{0}\'',
            'javax.faces.validator.DoubleRangeValidator.MINIMUM': '{1}: Validation Error: Value is less than allowable minimum of \'{0}\'',
            'javax.faces.validator.DoubleRangeValidator.NOT_IN_RANGE': '{2}: Validation Error: Specified attribute is not between the expected values of {0} and {1}',
            'javax.faces.validator.DoubleRangeValidator.TYPE={0}': 'Validation Error: Value is not of the correct type',
            'javax.faces.converter.FloatConverter.FLOAT': '{2}: \'{0}\' must be a number consisting of one or more digits.',
            'javax.faces.converter.FloatConverter.FLOAT_detail': '{2}: \'{0}\' must be a number between 1.4E-45 and 3.4028235E38  Example: {1}',
            'javax.faces.converter.DateTimeConverter.DATE': '{2}: \'{0}\' could not be understood as a date.',
            'javax.faces.converter.DateTimeConverter.DATE_detail': '{2}: \'{0}\' could not be understood as a date. Example: {1}',
            'javax.faces.converter.DateTimeConverter.TIME': '{2}: \'{0}\' could not be understood as a time.',
            'javax.faces.converter.DateTimeConverter.TIME_detail': '{2}: \'{0}\' could not be understood as a time. Example: {1}',
            'javax.faces.converter.DateTimeConverter.DATETIME': '{2}: \'{0}\' could not be understood as a date and time.',
            'javax.faces.converter.DateTimeConverter.DATETIME_detail': '{2}: \'{0}\' could not be understood as a date and time. Example: {1}',
            'javax.faces.converter.DateTimeConverter.PATTERN_TYPE': '{1}: A \'pattern\' or \'type\' attribute must be specified to convert the value \'{0}\'',
            'javax.faces.converter.NumberConverter.CURRENCY': '{2}: \'{0}\' could not be understood as a currency value.',
            'javax.faces.converter.NumberConverter.CURRENCY_detail': '{2}: \'{0}\' could not be understood as a currency value. Example: {1}',
            'javax.faces.converter.NumberConverter.PERCENT': '{2}: \'{0}\' could not be understood as a percentage.',
            'javax.faces.converter.NumberConverter.PERCENT_detail': '{2}: \'{0}\' could not be understood as a percentage. Example: {1}',
            'javax.faces.converter.NumberConverter.NUMBER': '{2}: \'{0}\' is not a number.',
            'javax.faces.converter.NumberConverter.NUMBER_detail': '{2}: \'{0}\' is not a number. Example: {1}',
            'javax.faces.converter.NumberConverter.PATTERN': '{2}: \'{0}\' is not a number pattern.',
            'javax.faces.converter.NumberConverter.PATTERN_detail': '{2}: \'{0}\' is not a number pattern. Example: {1}',
            'javax.faces.validator.LengthValidator.MINIMUM': '{1}: Validation Error: Length is less than allowable minimum of \'{0}\'',
            'javax.faces.validator.LengthValidator.MAXIMUM': '{1}: Validation Error: Length is greater than allowable maximum of \'{0}\'',
            'javax.faces.validator.RegexValidator.PATTERN_NOT_SET': 'Regex pattern must be set.',
            'javax.faces.validator.RegexValidator.PATTERN_NOT_SET_detail': 'Regex pattern must be set to non-empty value.',
            'javax.faces.validator.RegexValidator.NOT_MATCHED': 'Regex Pattern not matched',
            'javax.faces.validator.RegexValidator.NOT_MATCHED_detail': 'Regex pattern of \'{0}\' not matched',
            'javax.faces.validator.RegexValidator.MATCH_EXCEPTION': 'Error in regular expression.',
            'javax.faces.validator.RegexValidator.MATCH_EXCEPTION_detail': 'Error in regular expression, \'{0}\''
        }
    });

    /**
     * An object with the client-side implementation of some faces validators. Used for implementing client-side
     * validation for quick feedback.
     * @type {Record<string, PrimeFaces.Validator>}
     */
    PrimeFaces.validator = { };

    /**
     * An object with the client-side implementation of some faces converters.
     * @type {Record<string, PrimeFaces.Converter>}
     */
    PrimeFaces.converter = { };

    /**
     * The module for enabling client side validation of form fields.
     * @namespace
     */
    PrimeFaces.validation = {

        /**
         * Is the Ajax-complete-handler bound?
         * @type {boolean}
         * @private
         */
        ajaxCompleteBound: false,

        /**
         * Parameter shortcut mapping for the method `PrimeFaces.vb`.
         * @type {Record<string, string>}
         */
        CFG_SHORTCUTS : {
            's': 'source',
            'p': 'process',
            'u': 'update',
            'a': 'ajax',
            'h': 'highlight',
            'f': 'focus',
            'r': 'renderMessages',
            'v': 'validateInvisibleElements'
        },

        /**
         * Triggers client-side-validation of single or multiple containers (complex validation or simple inputs).
         * @function
         * @param {JQuery} source The source element.
         * @param {string | HTMLElement | JQuery} process The elements to be processed.
         * @param {string | HTMLElement | JQuery} update The elements to be updated.
         * @param {boolean} highlight If invalid elements should be highlighted.
         * @param {boolean} focus If the first invalid element should be focused.
         * @param {boolean} renderMessages If messages should be rendered.
         * @param {boolean} validateInvisibleElements If invisible elements should be validated.
         * @param {boolean} logUnrenderedMessages If unrendered messages should be logged.
         * @return {PrimeFaces.validation.ValidationResult} The validation result.
         */
        validate : function(source, process, update, highlight, focus, renderMessages, validateInvisibleElements, logUnrenderedMessages) {
            var vc = PrimeFaces.validation.ValidationContext;

            process = PrimeFaces.expressions.SearchExpressionFacade.resolveComponentsAsSelector(source, process);

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
                PrimeFaces.validation.validateInput(source, inputs.eq(i), highlight);
            }

            // validate complex validations, which can be applied to any container element
            var nonInputs = $();
            for (var i = 0; i < process.length; i++) {
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
                PrimeFaces.validation.validateComplex(source, nonInputs.eq(i), highlight);
            }

            // early exit - we don't need to render messages
            if (vc.isEmpty()) {
                return { valid: true, messages: {}, hasUnrenderedMessage: false };
            }

            // render messages
            if (renderMessages === true) {
                update = PrimeFaces.expressions.SearchExpressionFacade.resolveComponentsAsSelector(source, update);
                for (var i = 0; i < update.length; i++) {
                    var component = update.eq(i);
                    PrimeFaces.validation.Utils.renderMessages(vc.messages, component);
                }
            }

            //focus first element
            if (focus === true) {
                for (var key in vc.messages) {
                    if (vc.messages.hasOwnProperty(key)) {
                        var el = $(PrimeFaces.escapeClientId(key));
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

            var result = {
                valid: vc.isEmpty(),
                messages : vc.messages,
                hasUnrenderedMessage: false
            };

            for (let clientId in vc.messages) {
                var msgs = vc.messages[clientId];
                for (let msg of msgs) {
                    if (!msg.rendered) {
                        result.hasUnrenderedMessage = true;
                    }
                }
            }

            if (renderMessages && logUnrenderedMessages && result.hasUnrenderedMessage) {
                PrimeFaces.warn("There are some unhandled FacesMessages, this means not every FacesMessage had a chance to be rendered. These unhandled FacesMessages are:");

                for (let clientId in vc.messages) {
                    var msgs = vc.messages[clientId];
                    for (let msg of msgs) {
                        if (!msg.rendered) {
                            PrimeFaces.warn(msg.detail);
                        }
                    }
                }
            }

            vc.clear();

            return result;
        },


        /**
         * Searches for all CommandButtons with turned on dynamic CSV and triggers CSV.
         * @function
         */
        validateButtonsCsvRequirements: function () {
            $('[data-pf-validateclient-dynamic]').each((index, btn) => {
                this.validateButtonCsvRequirements(btn);
            });
        },

        /**
         * Validates the CSV-requirements of a CommandButton.
         * @function
         * @param {HTMLButtonElement} btn CommandButton which´s CSV-requirements should be validated.
         */
        validateButtonCsvRequirements: function(btn) {
            const $source = $(btn);
            const cfg = {
                ajax: btn.dataset.pfValidateclientAjax,
                process: btn.dataset.pfValidateclientProcess,
                update: btn.dataset.pfValidateclientUpdate
            };
            const process = PrimeFaces.validation.Utils.resolveProcess(cfg, $source);
            const update = PrimeFaces.validation.Utils.resolveUpdate(cfg, $source);

            const widget = PrimeFaces.getWidgetById(btn.id);

            if (widget) {
                if (PrimeFaces.validation.validate($source, process, update, false, false, false, false, false).valid) {
                    widget.jq.addClass('ui-state-csv-valid');
                    widget.jq.removeClass('ui-state-csv-invalid');
                    widget.enable();
                } else {
                    widget.jq.addClass('ui-state-csv-invalid');
                    widget.jq.removeClass('ui-state-csv-valid');
                    widget.disable();
                }
            } else {
                console.warn('No widget found for ID ' + btn.id);
            }

            PrimeFaces.validation.ValidationContext.clear();
        },

        /**
         * Performs a client-side validation of the given element. The context of this validation is a single field only.
         * If the element is valid, removes old messages from the element.
         * If the value of the element is invalid, adds the appropriate validation failure messages.
         * This is used by `p:clientValidator`.
         * @function
         * @param {string | HTMLElement | JQuery} el The ID of an input to validate, or the input itself.
         * @param {boolean} highlight If the invalid element should be highlighted.
         * @param {boolean} renderMessages If messages should be rendered.
         * @return {boolean} `true` if the element is valid, or `false` otherwise.
         */
        validateInstant : function(el, highlight, renderMessages) {
            highlight = (highlight === undefined) ? true : highlight;
            renderMessages = (renderMessages === undefined) ? true : renderMessages;

            var vc = PrimeFaces.validation.ValidationContext;

            var element = typeof el === 'string'
                ? $(PrimeFaces.escapeClientId(el))
                : $(el);
            var clientId = element.data(PrimeFaces.CLIENT_ID_DATA) || element.attr('id');

            var messageComponentId = element.data('target-message');
            var messageComponent = null;
            if (renderMessages === true) {
                if (messageComponentId) {
                    messageComponent = messageComponentId === 'p-unbound'
                        ? null
                        : $(PrimeFaces.escapeClientId(messageComponentId));
                }
                else {
                    var messageComponents = element.closest('form').find('div.ui-message');
                    messageComponent = PrimeFaces.validation.Utils.findTargetMessageComponent(clientId, messageComponents);

                    if (messageComponent) {
                        element.data('target-message', messageComponent.attr('id'));
                    }
                    else {
                        element.data('target-message', 'p-unbound');
                    }
                }

                if (messageComponent) {
                    var messageWidget = PrimeFaces.getWidgetById(messageComponent.attr('id'));
                    messageWidget.clearMessage();
                }
            }

            this.validateButtonsCsvRequirements();

            PrimeFaces.validation.validateInput(element, element, highlight);

            if (!vc.isEmpty()) {
                if (messageComponent) {
                    var messageWidget = PrimeFaces.getWidgetById(messageComponent.attr('id'));
                    messageWidget.renderMessage(vc.messages[clientId][0]);
                }

                vc.clear();
                return false;
            }
            else {
                vc.clear();
                return true;
            }
        },

        /**
         * __NOTE__: This is a internal method and should only by used by `PrimeFaces.validation.validate`.
         *
         * Performs a client-side validation of (the value of) the given input element. If the element is valid, removes old
         * messages from the element. If the value of the element is invalid, adds the appropriate validation failure
         * messages.
         * @function
         * @internal
         * @param {JQuery} source The source element.
         * @param {JQuery} element A JQuery instance with a single input element to validate.
         * @param {boolean} highlight If the invalid element should be highlighted.
         */
        validateInput : function(source, element, highlight) {
            var vc = PrimeFaces.validation.ValidationContext;

            if (element.is(':checkbox,:radio') && element.data('p-grouped')) {
                var groupName = element.attr('name');

                if (!vc.isGroupValidated(groupName)) {
                    vc.addElementGroup(groupName);
                } else {
                    return;
                }
            }

            if (element.parent().hasClass('ui-inputnumber')) {
                element = element.parent().children('input:hidden');
            }

            var submittedValue = PrimeFaces.validation.Utils.getSubmittedValue(element),
                valid = true,
                converterId = element.data('p-con');

            if (PrimeFaces.settings.considerEmptyStringNull && ((!submittedValue) || submittedValue.length === 0)) {
                submittedValue = null;
            }

            var newValue = null;
            if (converterId) {
                try {
                    newValue = PrimeFaces.converter[converterId].convert(element, submittedValue);
                }
                catch (ce) {
                    var converterMessageStr = element.data('p-cmsg'),
                        converterMsg = (converterMessageStr) ? {summary:converterMessageStr,detail:converterMessageStr} : ce;
                    valid = false;
                    vc.addMessage(element, converterMsg);
                }
            }
            else {
                newValue = submittedValue;
            }

            var required = element.data('p-required');
            if (required) {
                element.attr('aria-required', true);
            }

            if (valid && required && (newValue === null || newValue === '')) {
                var requiredMessageStr = element.data('p-rmsg');
                var requiredMsg = requiredMessageStr
                    ? { summary: requiredMessageStr, detail: requiredMessageStr }
                    : vc.getMessage('javax.faces.component.UIInput.REQUIRED', vc.getLabel(element));

                vc.addMessage(element, requiredMsg);

                valid = false;
            }

            if (valid
                && ((submittedValue !== null && PrimeFaces.trim(submittedValue).length > 0) || PrimeFaces.settings.validateEmptyFields)) {
                var validatorIds = element.data('p-val');
                if (validatorIds) {
                    validatorIds = validatorIds.split(',');

                    for (var j = 0; j < validatorIds.length; j++) {
                        var validatorId = validatorIds[j],
                            validator = PrimeFaces.validator[validatorId];

                        if (validator) {
                            try {
                                validator.validate(element, newValue);
                            }
                            catch (ve) {
                                var validatorMessageStr = element.data('p-vmsg');
                                var validatorMsg = validatorMessageStr
                                    ? {summary: validatorMessageStr, detail: validatorMessageStr}
                                    : ve;

                                if (Array.isArray(validatorMsg)) {
                                    // e.g. PrimeFaces.validator['primefaces.File'] may return an array of messages
                                    validatorMsg.forEach((msg) => vc.addMessage(element, msg));
                                }
                                else {
                                    vc.addMessage(element, validatorMsg);
                                }

                                valid = false;
                            }
                        }
                    }
                }
            }

            var highlighterType = element.data('p-hl') || 'default',
                highlighter = PrimeFaces.validator.Highlighter.types[highlighterType];

            if (valid) {
                highlighter.unhighlight(element);
                element.attr('aria-invalid', false);
            }
            else {
                if (highlight) {
                    highlighter.highlight(element);
                }
                element.attr('aria-invalid', true);
            }
        },

        /**
         * __NOTE__: This is an internal method and should only be used by `PrimeFaces.validation.validate`.
         *
         * Performs a client-side validation of (the value of) the given container element. If the element is valid,
         * removes old messages from the element. If the value of the element is invalid, adds the appropriate
         * validation failure messages.
         * @function
         * @internal
         * @param {JQuery} source the source element.
         * @param {JQuery} element A JQuery instance with a single input element to validate.
         * @param {boolean} highlight If the invalid element should be highlighted.
         * @returns {boolean} `true` if the value of the element is valid, `false` otherwise.
         */
        validateComplex : function(source, element, highlight) {
            var vc = PrimeFaces.validation.ValidationContext;
            var valid = true;

            var validatorIds = element.data('p-val');
            if (validatorIds) {
                validatorIds = validatorIds.split(',');

                for (var j = 0; j < validatorIds.length; j++) {
                    var validatorId = validatorIds[j],
                        validator = PrimeFaces.validator[validatorId];

                    if (validator) {
                        try {
                            validator.validate(source, element);
                        }
                        catch (ve) {
                            var validatorMessageStr = element.data('p-vmsg');
                            var validatorMsg = validatorMessageStr
                                ? {summary: validatorMessageStr, detail: validatorMessageStr}
                                : ve;

                            vc.addMessage(element, validatorMsg);

                            valid = false;

                            var highlighterType = element.data('p-hl');

                            var highlighter = highlighterType
                                ? PrimeFaces.validator.Highlighter.types[highlighterType]
                                : PrimeFaces.validator.Highlighter.types[validatorId];

                            if (valid) {
                                if (highlighter) {
                                    highlighter.unhighlight(element);
                                }
                                element.attr('aria-invalid', false);
                            }
                            else {
                                if (highlight && highlighter) {
                                    highlighter.highlight(element);
                                }
                                element.attr('aria-invalid', true);
                            }
                        }
                    }
                }
            }

            return valid;
        },


        /**
         * __NOTE__: This is an internal method and should only be used by PrimeFaces itself.
         *
         * Bind to Ajax-Complete-events to update CSV-state after an Ajax-call may have changed state.
         * @internal
         */
        bindAjaxComplete: function() {
            if (this.ajaxCompleteBound) return;

            var $this = this;

            $(document).on('pfAjaxComplete', function(e, xhr, settings, args) {
                $this.validateButtonsCsvRequirements();
            });

            // also bind to JSF (f:ajax) events
            // NOTE: PF always fires "complete" as last event, whereas JSF last events are either "success" or "error"
            if (window.jsf && jsf.ajax) {
                jsf.ajax.addOnEvent(function(data) {
                    if(data.status === 'success' || data.status === 'error') {
                        $this.validateButtonsCsvRequirements();
                    }
                });
            }

            this.ajaxCompleteBound = true;
        }
    };

    /**
     * The object that contains functionality related to handling faces messages, especially validation errror messages.
     * Contains methods for clearing message of an element or adding messages to an element.
     * @namespace
     */
    PrimeFaces.validation.ValidationContext = {

        /**
         * A map between the client ID of an element and a list of faces message for that element.
         * @type {Record<string, PrimeFaces.FacesMessage[]>}
         */
        messages: {},

        /**
         * A list of element groups to be validated. Usually corresponds to the name of single form element. For some
         * cases such as a select list of checkboxes, a group may correspond to multiple DOM elements.
         * @type {string[]}
         */
        elementGroups: [],

        /**
         * Highlights the passed widget as invalid.
         *
         * @param {string | HTMLElement | JQuery | PrimeFaces.widget.BaseWidget} target The target widget / element.
         */
        highlight: function(target) {
            var element = PrimeFaces.resolveAs$(target);
            var highlighterType = element.data('p-hl') || 'default';
            var highlighter = PrimeFaces.validator.Highlighter.types[highlighterType];

            highlighter.highlight(element);
            element.attr('aria-invalid', true);
        },

        /**
         * Un-Highlights the passed widget as invalid.
         *
         * @param {string | HTMLElement | JQuery | PrimeFaces.widget.BaseWidget} target The target widget / element.
         */
        unhighlight: function(target) {
            var element = PrimeFaces.resolveAs$(target);
            var highlighterType = element.data('p-hl') || 'default';
            var highlighter = PrimeFaces.validator.Highlighter.types[highlighterType];

            highlighter.unhighlight(element);
            element.attr('aria-invalid', false);
        },

        /**
         * Adds a faces message to the given element.
         * @param {string | HTMLElement | JQuery | PrimeFaces.widget.BaseWidget} target Element or widget to which to add the message.
         * @param {PrimeFaces.FacesMessage} msg Message to add to the given message.
         */
        addMessage: function(target, msg) {
            var clientId = PrimeFaces.resolveAsId(target);

            if(!this.messages[clientId]) {
                this.messages[clientId] = [];
            }

            // in case of a exception -> lets wrap it into a 'unexcepted error'
            if (!msg.hasOwnProperty('summary') && !msg.hasOwnProperty('detail')) {
                msg = {
                    summary : PrimeFaces.getLocaleSettings()['unexpectedError'],
                    detail : msg.toString()
                };
            }

            if (!msg.severity) {
                msg.severity = "error";
            }

            msg.rendered = false;

            this.messages[clientId].push(msg);
        },

        /**
         * Reports how many messages were added to this validation context. Note that each component may have several
         * messages.
         * @return {number} The number of messages added to this validation context.
         */
        getMessagesLength: function() {
            var length = 0, key;

            for (key in this.messages) {
                if (this.messages.hasOwnProperty(key)) {
                    length++;
                }
            }

            return length;
        },

        /**
         * Checks whether this validation context contains any messages at all.
         * @return {boolean} `true` if this validation context contains zero messages, or `false` otherwise.
         */
        isEmpty: function() {
            return this.getMessagesLength() === 0;
        },

        /**
         * Removes all messages from this validation context.
         */
        clear: function() {
            this.messages = {};
            this.elementGroups = [];
        },

        /**
         * Shortcut for PrimeFaces.validation.Utils.getMessage.
         * @param {string} key The i18n key of a message, such as `javax.faces.component.UIInput.REQUIRED` or
         * `javax.faces.validator.LengthValidator.MINIMUM`.
         * @return {PrimeFaces.FacesMessage | null} The localized faces message for the given key, or `null` if no
         * translation was found for the key.
         */
        getMessage: function(key) {
            var params = Array.from(arguments);
            params.shift(); // remove first param 'key'

            return PrimeFaces.validation.Utils.getMessage(key, params);
        },

        /**
         * Shortcut for PrimeFaces.validation.Utils.getLabel.
         * @param {JQuery} element A DOM element for which to find the label.
         * @return {string} The label of the given element.
         */
        getLabel: function(element) {
            return PrimeFaces.validation.Utils.getLabel(element);
        },

        /**
         * Checks whether the given element group is in the list of groups to be validated. An element group is often
         * just the name of a single INPUT, TEXTAREA or SELECT element, but may also consist of multiple DOM elements,
         * such as in the case of a select list of checkboxes.
         * @param {string} name Name of an element group to check.
         * @return {boolean} `true` if the given group is to be validated, or `false` otherwise.
         */
        isGroupValidated: function(name) {
            for (var i = 0; i < this.elementGroups.length; i++) {
                if (this.elementGroups[i] === name) {
                    return true;
                }
            }
            return false;
        },

        /**
         * Adds a group to the list of element groups to validate. An element group is often just the name of a single
         * INPUT, TEXTAREA or SELECT element, but may also consist of multiple DOM elements, such as in the case of
         * select list of checkboxes.
         * @param {string} name Name of an element group to add.
         */
        addElementGroup: function(name) {
            this.elementGroups.push(name);
        }
    };

    /**
     * Mostly internal utility methods used to validate data on the client.
     * @namespace
     */
    PrimeFaces.validation.Utils = {

        /**
         * Finds the localized text of the given message key. When the current locale does not contain a translation,
         * falls back to the default English locale.
         * @param {string} key The i18n key of a message, such as `javax.faces.component.UIInput.REQUIRED` or
         * `javax.faces.validator.LengthValidator.MINIMUM`.
         * @param {string[]} params A list of parameters for the placeholders.
         * @return {PrimeFaces.FacesMessage | null} The localized faces message for the given key, or `null` if no
         * translation was found for the key.
         */
        getMessage: function(key, params) {
            var locale = PrimeFaces.getLocaleSettings();
            var bundle = (locale.messages && locale.messages[key]) ? locale : PrimeFaces.locales['en_US'];

            var summary = bundle.messages[key];
            if (!summary) {
                return {
                    summary: "### Message '" + key + "' not found ###",
                    detail: "### Message '" + key + "' not found ###"
                };
            }

            summary = PrimeFaces.validation.Utils.format(summary, params);

            var detail = bundle.messages[key + '_detail'];
            detail = (detail) ? PrimeFaces.validation.Utils.format(detail, params) : summary;

            return {
                summary: summary,
                detail: detail
            };
        },

        /**
         * Given a message with placeholders, replaces the placeholders with the given parameters. The format of the
         * message is similar to, but not quite the same as, the format used by `java.text.MessageFormat`.
         * ```javascript
         * format("Value required for element {0}", ["email"]) // => "Value required for element email"
         * format("Use {0} braces like this: '{0}'", ["simple"]) // => "Use simple braces like this: 'simple'"
         * ```
         * @param {string} str A message with placeholders.
         * @param {string[]} params A list of parameters for the placeholders.
         * @return {string} The string with the placeholders replaced with the given params.
         */
        format: function(str, params) {
            var s = str;
            for(var i = 0; i < params.length; i++) {
                var reg = new RegExp('\\{' + i + '\\}', 'gm');
                s = s.replace(reg, params[i]);
            }

            return s;
        },

        /**
         * Finds the label of a DOM element. This is either a custom label set on a component, or just the ID of the
         * element. This label is used, for example, as part of a validation error message for the element.
         * @param {JQuery} element A DOM element for which to find the label.
         * @return {string} The label of the given element.
         */
        getLabel: function(element) {
            return element.data('p-label') || element.attr('id');
        },

        /**
         * Given a form element (such as input, textarea, select), finds the value that would be sent when the form is
         * submitted.
         * @param {JQuery} element A form element for which to find its value.
         * @return {string} The value of the form element, or the empty string when it does not have a value.
         */
        getSubmittedValue: function(element) {
            var value;

            if (element.is(':radio')) {
                value = $('input:radio[name="' + CSS.escape(element.attr('name')) + '"]:checked').val();
            }
            else if (element.is(':checkbox')) {
                value = element.data('p-grouped') ? $('input:checkbox[name="' + CSS.escape(element.attr('name')) + '"]:checked').val(): element.prop('checked').toString();
            }
            else if (element.is(':file')) {
                value = element[0].files;
            }
            else {
                value = element.val();
            }

            return value === undefined ? '': value;
        },

        /**
         * For a given ID of a component, finds the DOM element with the message for that component.
         * @param {string} clientId ID of a component for which to find the ui message.
         * @param {JQuery} messageComponents A JQuery instance with a list of `ui-message`s, or `null` if no
         * such element exists.
         * @return {JQuery | null} The DOM element with the messages for the given component, or `null` when no such
         * element could be found.
         */
        findTargetMessageComponent: function(clientId, messageComponents) {
            for (var i = 0; i < messageComponents.length; i++) {
                var messageComponent = messageComponents.eq(i);
                if (messageComponent.data('target') === clientId) {
                    return messageComponent;
                }
            }

            return null;
        },


        /**
         * Renders all given messages in the given container.
         * @param {Record<string, PrimeFaces.FacesMessage[]>} messages The messages to render.
         * @param {JQuery} container The container for the messages. Either the element with the class `ui-messages`, or
         * a parent of such an element.
         */
        renderMessages: function(messages, container) {
            var messagesComponents = $(),
                messageComponents = $(),
                growlComponents = $();

            container.each(function() {
                var $this = $(this);

                if ($this.is('div.ui-messages')) {
                    messagesComponents = messagesComponents.add($this);
                }
                else {
                    messagesComponents = messagesComponents.add($this.find('div.ui-messages'));
                }

                if ($this.is('div.ui-message')) {
                    messageComponents = messageComponents.add($this);
                }
                else {
                    messageComponents = messageComponents.add($this.find('div.ui-message'));
                }

                if ($this.is('.ui-growl-pl')) {
                    growlComponents = growlComponents.add($this);
                }
                else {
                    growlComponents = growlComponents.add($this.find('.ui-growl-pl'));
                }
            });

            // filter out by severity
            messagesComponents = messagesComponents.filter(function(idx) {
                if ($(this).is('.ui-fileupload-messages')) {
                    return false;
                }
                return $(this).data('severity').indexOf('error') !== -1;
            });
            growlComponents = growlComponents.filter(function(idx) {
                return $(this).data('severity').indexOf('error') !== -1;
            });

            for (var i = 0; i < messagesComponents.length; i++) {
                var messagesComponent = messagesComponents.eq(i),
                    globalOnly = messagesComponent.data('global'),
                    redisplay = messagesComponent.data('redisplay'),
                    showSummary = messagesComponent.data('summary'),
                    showDetail = messagesComponent.data('detail'),
                    messagesWidget = PrimeFaces.getWidgetById(messagesComponent.attr('id'));

                messagesWidget.clearMessages();

                for (let clientId in messages) {
                    for (let msg of messages[clientId]) {
                        if (globalOnly || (msg.rendered && !redisplay)) {
                            continue;
                        }

                        if (!showSummary) {
                            msg.summary = '';
                        }
                        if (!showDetail) {
                            msg.detail = '';
                        }

                        messagesWidget.appendMessage(msg);
                        msg.rendered = true;
                    }
                }
            }

            for (var i = 0; i < growlComponents.length; i++) {
                var growlComponent = growlComponents.eq(i),
                    redisplay = growlComponent.data('redisplay'),
                    globalOnly = growlComponent.data('global'),
                    showSummary = growlComponent.data('summary'),
                    showDetail = growlComponent.data('detail'),
                    growlWidget = PrimeFaces.getWidgetById(growlComponent.attr('id'));

                growlWidget.removeAll();

                for (let clientId in messages) {
                    for (let msg of messages[clientId]) {
                        if (globalOnly || (msg.rendered && !redisplay)) {
                            continue;
                        }

                        if (!showSummary) {
                            msg.summary = '';
                        }
                        if (!showDetail) {
                            msg.detail = '';
                        }

                        growlWidget.renderMessage(msg);
                        msg.rendered = true;
                    }
                }
            }

            for (var i = 0; i < messageComponents.length; i++) {
                var messageComponent = messageComponents.eq(i),
                    target = messageComponent.data('target'),
                    redisplay = messageComponent.data('redisplay'),
                    messageWidget = PrimeFaces.getWidgetById(messageComponent.attr('id'));

                messageWidget.clearMessage();

                for (let clientId in messages) {
                    if (target !== clientId) {
                        continue;
                    }
                    for (let msg of messages[clientId]) {
                        if (msg.rendered && !redisplay) {
                            continue;
                        }

                        messageWidget.renderMessage(msg);
                        msg.rendered = true;
                    }
                }
            }
        },

        /**
         * Resolves process-attribute of a PrimeFaces-component. (e.g. CommandButton)
         * @param {PrimeFaces.validation.Configuration} cfg Configuration of the PrimeFaces-component.
         * @param {JQuery} source The source element.
         * @returns {JQuery} Resolved jQuery-element.
         */
        resolveProcess: function(cfg, source) {
            if (cfg.ajax && cfg.process) {
                return PrimeFaces.expressions.SearchExpressionFacade.resolveComponentsAsSelector(source, cfg.process);
            } else {
                return source.closest('form');
            }
        },

        /**
         * Resolves update-attribute of a PrimeFaces-component. (e.g. CommandButton)
         * @param {PrimeFaces.validation.Configuration} cfg Configuration of the PrimeFaces-component.
         * @param {JQuery} source The source element.
         * @returns {JQuery} Resolved jQuery-element.
         */
        resolveUpdate: function(cfg, source) {
            if (cfg.ajax && cfg.update) {
                return PrimeFaces.expressions.SearchExpressionFacade.resolveComponentsAsSelector(source, cfg.update);
            } else {
                return source.closest('form');
            }
        }
    };

}
