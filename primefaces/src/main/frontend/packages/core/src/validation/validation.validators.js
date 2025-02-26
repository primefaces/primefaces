// see #7395
// we always add validation/beanvalidation.js on each page, also if no PrimeFaces component is available
// so... just check if primefaces.js was rendered
if (window.PrimeFaces) {

    PrimeFaces.validator['jakarta.faces.Length'] = {
        MINIMUM_MESSAGE_ID: 'jakarta.faces.validator.LengthValidator.MINIMUM',
        MAXIMUM_MESSAGE_ID: 'jakarta.faces.validator.LengthValidator.MAXIMUM',

        validate: function(element) {
            var length = element.val().length,
            min = element.data('p-minlength'),
            max = element.data('p-maxlength'),
            vc = PrimeFaces.validation.ValidationContext;

            if(max !== undefined && length > max) {
                throw vc.getMessage(this.MAXIMUM_MESSAGE_ID, max, vc.getLabel(element));
            }

            if(min !== undefined && length < min) {
                throw vc.getMessage(this.MINIMUM_MESSAGE_ID, min, vc.getLabel(element));
            }
        }
    };

    PrimeFaces.validator['jakarta.faces.LongRange'] = {
        MINIMUM_MESSAGE_ID: 'jakarta.faces.validator.LongRangeValidator.MINIMUM',
        MAXIMUM_MESSAGE_ID: 'jakarta.faces.validator.LongRangeValidator.MAXIMUM',
        NOT_IN_RANGE_MESSAGE_ID: 'jakarta.faces.validator.LongRangeValidator.NOT_IN_RANGE',
        TYPE_MESSAGE_ID: 'jakarta.faces.validator.LongRangeValidator.TYPE',
        regex: /^-?\d+$/,

        validate: function(element, value) {
            if(value !== null) {
                var min = element.data('p-minvalue'),
                max = element.data('p-maxvalue'),
                vc = PrimeFaces.validation.ValidationContext;

                if(!this.regex.test(value)) {
                    throw vc.getMessage(this.TYPE_MESSAGE_ID, vc.getLabel(element));
                }

                if((max !== undefined && min !== undefined) && (value < min || value > max)) {
                    throw vc.getMessage(this.NOT_IN_RANGE_MESSAGE_ID, min, max, vc.getLabel(element));
                }
                else if((max !== undefined && min === undefined) && (value > max)) {
                    throw vc.getMessage(this.MAXIMUM_MESSAGE_ID, max, vc.getLabel(element));
                }
                else if((min !== undefined && max === undefined) && (value < min)) {
                    throw vc.getMessage(this.MINIMUM_MESSAGE_ID, min, vc.getLabel(element));
                }
            }
        }
    };

    PrimeFaces.validator['jakarta.faces.DoubleRange'] = {
        MINIMUM_MESSAGE_ID: 'jakarta.faces.validator.DoubleRangeValidator.MINIMUM',
        MAXIMUM_MESSAGE_ID: 'jakarta.faces.validator.DoubleRangeValidator.MAXIMUM',
        NOT_IN_RANGE_MESSAGE_ID: 'jakarta.faces.validator.DoubleRangeValidator.NOT_IN_RANGE',
        TYPE_MESSAGE_ID: 'jakarta.faces.validator.DoubleRangeValidator.TYPE',
        regex: /^[-+]?\d*(\.\d+)?[d]?$/,

        validate: function(element, value) {
            if(value !== null) {
                var min = element.data('p-minvalue'),
                max = element.data('p-maxvalue'),
                vc = PrimeFaces.validation.ValidationContext;

                if(!this.regex.test(value)) {
                    throw vc.getMessage(this.TYPE_MESSAGE_ID, vc.getLabel(element));
                }

                if((max !== undefined && min !== undefined) && (value < min || value > max)) {
                    throw vc.getMessage(this.NOT_IN_RANGE_MESSAGE_ID, min, max, vc.getLabel(element));
                }
                else if((max !== undefined && min === undefined) && (value > max)) {
                    throw vc.getMessage(this.MAXIMUM_MESSAGE_ID, max, vc.getLabel(element));
                }
                else if((min !== undefined && max === undefined) && (value < min)) {
                    throw vc.getMessage(this.MINIMUM_MESSAGE_ID, min, vc.getLabel(element));
                }
            }
        }
    };

    PrimeFaces.validator['jakarta.faces.RegularExpression'] = {
        PATTERN_NOT_SET_MESSAGE_ID: 'jakarta.faces.validator.RegexValidator.PATTERN_NOT_SET',
        NOT_MATCHED_MESSAGE_ID: 'jakarta.faces.validator.RegexValidator.NOT_MATCHED',
        MATCH_EXCEPTION_MESSAGE_ID: 'jakarta.faces.validator.RegexValidator.MATCH_EXCEPTION',

        validate: function(element, value) {
            if(value !== null) {
                var pattern = element.data('p-regex'),
                vc = PrimeFaces.validation.ValidationContext;

                if(!pattern) {
                    throw vc.getMessage(this.PATTERN_NOT_SET_MESSAGE_ID);
                }

                var regex = new RegExp(pattern);
                if(!regex.test(value)) {
                    throw vc.getMessage(this.NOT_MATCHED_MESSAGE_ID, pattern);
                }
            }
        }
    };

    PrimeFaces.validator['primefaces.File'] = {
        FILE_LIMIT_MESSAGE_ID: 'primefaces.FileValidator.FILE_LIMIT',
        ALLOW_TYPES_MESSAGE_ID: 'primefaces.FileValidator.ALLOW_TYPES',
        SIZE_LIMIT_MESSAGE_ID: 'primefaces.FileValidator.SIZE_LIMIT',

        validate: function(element, value) {
            if(value !== null) {

                var filelimit = element.data('p-filelimit'),
                    allowtypes = element.data('p-allowtypes'),
                    sizelimit = element.data('p-sizelimit'),
                    vc = PrimeFaces.validation.ValidationContext,
                    messages = [];

                var allowtypesRegExp = null;
                if (allowtypes) {
                    // normally a regex is a object like /(\.|\/)(csv)$/
                    // but as we parse the data-attribute from string to RegEx object, we must remove leading and ending slashes
                    var regexParts = allowtypes.match(/^\/(.*)\/([a-z]*)$/);
                    var transformedAllowtypes = regexParts[1];
                    var flags = regexParts[2];
                    allowtypesRegExp = new RegExp(transformedAllowtypes, flags);
                }

                if (filelimit && value.length > filelimit) {
                    messages.push(vc.getMessage(this.FILE_LIMIT_MESSAGE_ID, filelimit));
                }

                for (var file of value) {
                    if (allowtypesRegExp && (!allowtypesRegExp.test(file.type) && !allowtypesRegExp.test(file.name)))  {
                        messages.push(vc.getMessage(this.ALLOW_TYPES_MESSAGE_ID, file.name, PrimeFaces.utils.formatAllowTypes(allowtypes)));
                    }

                    if (sizelimit && file.size > sizelimit) {
                        messages.push(vc.getMessage(this.SIZE_LIMIT_MESSAGE_ID, file.name, PrimeFaces.utils.formatBytes(sizelimit)));
                    }
                }

                if (messages.length > 0) {
                    throw messages;
                }
            }
        },
    };
}
