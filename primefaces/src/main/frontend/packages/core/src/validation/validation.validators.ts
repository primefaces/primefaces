import { core } from "../core/core.js";
import { utils } from "../core/core.utils.js";
import { toValidationNumber } from "./validation.helper.js";

/**
 * Registers client-side implementations of common validators such as
 * `jakarta.faces.Length` with the core.
 */
export function registerCommonValidators(): void {
    core.validator["jakarta.faces.Length"] = new LengthValidator();
    core.validator["jakarta.faces.LongRange"] = new LongRangeValidator();
    core.validator["jakarta.faces.DoubleRange"] = new DoubleRangeValidator();
    core.validator["jakarta.faces.RegularExpression"] = new RegularExpressionValidator();
    core.validator["primefaces.File"] = new FileValidator();
}

/**
 * Client-side validator implementation for the Faces validator
 * `jakarta.faces.Length`.
 */
class LengthValidator implements PrimeType.validation.Validator {
    private readonly MINIMUM_MESSAGE_ID = 'jakarta.faces.validator.LengthValidator.MINIMUM';
    private readonly MAXIMUM_MESSAGE_ID = 'jakarta.faces.validator.LengthValidator.MAXIMUM';

    validate(element: JQuery): void {
        const value = element.val();
        if (value === undefined) {
            return;
        }
        const length = Array.isArray(value) || typeof value === "string" ? value.length : value.toString().length;
        const min = toValidationNumber(element.data('p-minlength'));
        const max = toValidationNumber(element.data('p-maxlength'));
        const vc = core.validation.ValidationContext;

        if (max !== undefined && length > max) {
            throw vc.getMessage(this.MAXIMUM_MESSAGE_ID, max, vc.getLabel(element));
        }

        if (min !== undefined && length < min) {
            throw vc.getMessage(this.MINIMUM_MESSAGE_ID, min, vc.getLabel(element));
        }
    }
}

/**
 * Client-side validator implementation for the Faces validator
 * `jakarta.faces.LongRange`.
 */
class LongRangeValidator implements PrimeType.validation.Validator {
    private readonly MINIMUM_MESSAGE_ID = 'jakarta.faces.validator.LongRangeValidator.MINIMUM';
    private readonly MAXIMUM_MESSAGE_ID = 'jakarta.faces.validator.LongRangeValidator.MAXIMUM';
    private readonly NOT_IN_RANGE_MESSAGE_ID = 'jakarta.faces.validator.LongRangeValidator.NOT_IN_RANGE';
    private readonly TYPE_MESSAGE_ID = 'jakarta.faces.validator.LongRangeValidator.TYPE';
    private readonly regex = /^-?\d+$/;

    validate(element: JQuery, value: unknown): void {
        if (value !== null) {
            const min = toValidationNumber(element.data('p-minvalue'));
            const max = toValidationNumber(element.data('p-maxvalue'));
            const vc = core.validation.ValidationContext;
            const stringValue = String(value);
            const numberValue = parseFloat(stringValue);

            if (!this.regex.test(stringValue)) {
                throw vc.getMessage(this.TYPE_MESSAGE_ID, vc.getLabel(element));
            }

            if ((max !== undefined && min !== undefined) && (numberValue < min || numberValue > max)) {
                throw vc.getMessage(this.NOT_IN_RANGE_MESSAGE_ID, min, max, vc.getLabel(element));
            }
            else if ((max !== undefined && min === undefined) && (numberValue > max)) {
                throw vc.getMessage(this.MAXIMUM_MESSAGE_ID, max, vc.getLabel(element));
            }
            else if ((min !== undefined && max === undefined) && (numberValue < min)) {
                throw vc.getMessage(this.MINIMUM_MESSAGE_ID, min, vc.getLabel(element));
            }
        }
    }
}

/**
 * Client-side validator implementation for the Faces validator
 * `jakarta.faces.DoubleRange`.
 */
class DoubleRangeValidator implements PrimeType.validation.Validator {
    private readonly MINIMUM_MESSAGE_ID = 'jakarta.faces.validator.DoubleRangeValidator.MINIMUM';
    private readonly MAXIMUM_MESSAGE_ID = 'jakarta.faces.validator.DoubleRangeValidator.MAXIMUM';
    private readonly NOT_IN_RANGE_MESSAGE_ID = 'jakarta.faces.validator.DoubleRangeValidator.NOT_IN_RANGE';
    private readonly TYPE_MESSAGE_ID = 'jakarta.faces.validator.DoubleRangeValidator.TYPE';
    private readonly regex = /^[-+]?\d*(\.\d+)?[d]?$/;

    validate(element: JQuery, value: unknown): void {
        if (value !== null) {
            const min = toValidationNumber(element.data('p-minvalue'));
            const max = toValidationNumber(element.data('p-maxvalue'));
            const vc = core.validation.ValidationContext;
            const stringValue = String(value);
            const numberValue = parseFloat(stringValue);

            if(!this.regex.test(stringValue)) {
                throw vc.getMessage(this.TYPE_MESSAGE_ID, vc.getLabel(element));
            }

            if ((max !== undefined && min !== undefined) && (numberValue < min || numberValue > max)) {
                throw vc.getMessage(this.NOT_IN_RANGE_MESSAGE_ID, min, max, vc.getLabel(element));
            }
            else if ((max !== undefined && min === undefined) && (numberValue > max)) {
                throw vc.getMessage(this.MAXIMUM_MESSAGE_ID, max, vc.getLabel(element));
            }
            else if ((min !== undefined && max === undefined) && (numberValue < min)) {
                throw vc.getMessage(this.MINIMUM_MESSAGE_ID, min, vc.getLabel(element));
            }
        }
    }
}

/**
 * Client-side validator implementation for the Faces validator
 * `jakarta.faces.RegularExpression`.
 */
class RegularExpressionValidator implements PrimeType.validation.Validator {
    private readonly PATTERN_NOT_SET_MESSAGE_ID = 'jakarta.faces.validator.RegexValidator.PATTERN_NOT_SET';
    private readonly NOT_MATCHED_MESSAGE_ID = 'jakarta.faces.validator.RegexValidator.NOT_MATCHED';

    validate(element: JQuery, value: unknown): void {
        if (value !== null) {
            const pattern = element.data('p-regex');
            const vc = core.validation.ValidationContext;
            const stringValue = String(value);

            if(!pattern) {
                throw vc.getMessage(this.PATTERN_NOT_SET_MESSAGE_ID);
            }

            var regex = new RegExp(pattern);
            if(!regex.test(stringValue)) {
                throw vc.getMessage(this.NOT_MATCHED_MESSAGE_ID, pattern);
            }
        }
    }
}

/**
 * Client-side validator implementation for the Faces validator
 * `primefaces.File`.
 */
class FileValidator implements PrimeType.validation.Validator {
    private readonly FILE_LIMIT_MESSAGE_ID = 'primefaces.FileValidator.FILE_LIMIT';
    private readonly ALLOW_TYPES_MESSAGE_ID = 'primefaces.FileValidator.ALLOW_TYPES';
    private readonly SIZE_LIMIT_MESSAGE_ID = 'primefaces.FileValidator.SIZE_LIMIT';

    validate(element: JQuery, value: unknown): void {
        if (value !== null && value instanceof FileList) {

            const fileLimit = element.data('p-filelimit');
            const allowTypes = element.data('p-allowtypes');
            const sizeLimit = element.data('p-sizelimit');
            const vc = core.validation.ValidationContext;
            const messages: PrimeType.BaseFacesMessage[] = [];

            let allowTypesRegExp = null;
            if (allowTypes) {
                // normally a regex is a object like /(\.|\/)(csv)$/
                // but as we parse the data-attribute from string to RegEx object, we must remove leading and ending slashes
                const regexParts = allowTypes.match(/^\/(.*)\/([a-z]*)$/);
                const transformedAllowTypes = regexParts[1];
                const flags = regexParts[2];
                allowTypesRegExp = new RegExp(transformedAllowTypes, flags);
            }

            if (fileLimit && value.length > fileLimit) {
                messages.push(vc.getMessage(this.FILE_LIMIT_MESSAGE_ID, fileLimit));
            }

            for (const file of value) {
                if (allowTypesRegExp && (!allowTypesRegExp.test(file.type) && !allowTypesRegExp.test(file.name)))  {
                    messages.push(vc.getMessage(this.ALLOW_TYPES_MESSAGE_ID, file.name, utils.formatAllowTypes(allowTypes)));
                }

                if (sizeLimit && file.size > sizeLimit) {
                    messages.push(vc.getMessage(this.SIZE_LIMIT_MESSAGE_ID, file.name, utils.formatBytes(sizeLimit)));
                }
            }

            if (messages.length > 0) {
                throw messages;
            }
        }
    }
}
