import { core } from "../core/core.js";
import { validation } from "./validation.common.js";
import { toValidationDate, toValidationNumber } from "./validation.helper.js";

// Bean Validation Integration for PrimeFaces Client Side Validation Framework

/**
 * Registers client-side implementations of Jakarta bean validators such as
 * `Size` with the core.
 */
export function registerBeanValidationValidators(): void {
    core.validator["AssertFalse"] = new AssertFalseValidator();
    core.validator["AssertTrue"] = new AssertTrueValidator();
    core.validator["DecimalMax"] = new DecimalMaxValidator();
    core.validator["DecimalMin"] = new DecimalMinValidator();
    core.validator["DecimalMin"] = new DecimalMinValidator();
    core.validator["Digits"] = new DigitsValidator();
    core.validator["Email"] = new EmailValidator();
    core.validator["Future"] = new FutureValidator();
    core.validator["FutureOrPresent"] = new FutureOrPresentValidator();
    core.validator["Max"] = new MaxValidator();
    core.validator["Min"] = new MinValidator();
    core.validator["Negative"] = new NegativeValidator();
    core.validator["NegativeOrZero"] = new NegativeOrZeroValidator();
    core.validator["NotBlank"] = new NotBlankValidator();
    core.validator["NotEmpty"] = new NotEmptyValidator();
    core.validator["NotNull"] = new NotNullValidator();
    core.validator["Null"] = new NullValidator();
    core.validator["Past"] = new PastValidator();
    core.validator["PastOrPresent"] = new PastOrPresentValidator();
    core.validator["Pattern"] = new PatternValidator();
    core.validator["Positive"] = new PositiveValidator();
    core.validator["PositiveOrZero"] = new PositiveOrZeroValidator();
    core.validator["Size"] = new SizeValidator();
}

/**
 * Registers messages of Jakarta bean validators such as `Size` with the core.
 */
export function registerBeanValidationMessages(): void {
    const enUs = core.locales['en_US'];
    if (enUs === undefined) {
        core.error("Cannot register common validator messages for default locale, en_US locale not found");
        return;
    }
    enUs.messages ??= {};
    $.extend(enUs.messages, {
        'jakarta.faces.validator.BeanValidator.MESSAGE': '{0}',
        'jakarta.validation.constraints.AssertFalse.message': 'must be false.',
        'jakarta.validation.constraints.AssertTrue.message': 'must be true.',
        'jakarta.validation.constraints.DecimalMax.message': 'must be less than or equal to {0}.',
        'jakarta.validation.constraints.DecimalMin.message': 'must be greater than or equal to {0}.',
        'jakarta.validation.constraints.Digits.message': 'numeric value out of bounds (<{0} digits>.<{1} digits> expected).',
        'jakarta.validation.constraints.Email.message': 'must be a well-formed email address.',
        'jakarta.validation.constraints.Future.message': 'must be a future date.',
        'jakarta.validation.constraints.FutureOrPresent.message': 'must be a date in the present or in the future.',
        'jakarta.validation.constraints.Max.message': 'must be less than or equal to {0}.',
        'jakarta.validation.constraints.Min.message': 'must be greater than or equal to {0}.',
        'jakarta.validation.constraints.Negative.message': 'must be less than 0.',
        'jakarta.validation.constraints.NegativeOrZero.message': 'must be less than or equal to 0.',
        'jakarta.validation.constraints.NotBlank.message': 'must not be blank.',
        'jakarta.validation.constraints.NotEmpty.message': 'must not be empty.',
        'jakarta.validation.constraints.NotNull.message': 'must not be null.',
        'jakarta.validation.constraints.Null.message': 'must be null.',
        'jakarta.validation.constraints.Past.message': 'must be a past date.',
        'jakarta.validation.constraints.PastOrPresent.message': 'must be a date in the past or in the present.',
        'jakarta.validation.constraints.Pattern.message': 'must match "{0}".',
        'jakarta.validation.constraints.Positive.message': 'must be greater than 0.',
        'jakarta.validation.constraints.PositiveOrZero.message': 'must be greater than or equal to 0.',
        'jakarta.validation.constraints.Size.message': 'size must be between {0} and {1}.',
    });
}

/**
 * Client-side validator implementation for the Jakarta Bean Validation
 * validator `AssertFalse`.
 */
class AssertFalseValidator implements PrimeType.validation.Validator {
    private readonly MESSAGE_ID = 'jakarta.validation.constraints.AssertFalse.message';

    validate(element: JQuery, value: unknown): void {
        if(value === true) {
            const vc = core.validation.ValidationContext;
            throw vc.getMessageBV(element, this.MESSAGE_ID, element.data('p-afalse-msg'));
        }
    }
}

/**
 * Client-side validator implementation for the Jakarta Bean Validation
 * validator `AssertTrue`.
 */
class AssertTrueValidator implements PrimeType.validation.Validator {
    private readonly MESSAGE_ID = 'jakarta.validation.constraints.AssertTrue.message';

    validate(element: JQuery, value: unknown): void {
        if(value === false) {
            const vc = core.validation.ValidationContext;
            throw vc.getMessageBV(element, this.MESSAGE_ID, element.data('p-atrue-msg'));
        }
    }
}

/**
 * Client-side validator implementation for the Jakarta Bean Validation
 * validator `DecimalMax`.
 */
class DecimalMaxValidator implements PrimeType.validation.Validator {

    private readonly MESSAGE_ID = 'jakarta.validation.constraints.DecimalMax.message';

    validate(element: JQuery, value: unknown): void {
        const valueNumber = toValidationNumber(value);
        if (valueNumber !== undefined) {
            const max = toValidationNumber(element.data('p-maxvalue'));
            const vc = validation.ValidationContext;

            if (max !== undefined && valueNumber > max) {
                throw vc.getMessageBV(element, this.MESSAGE_ID, element.data('p-decimalmax-msg'), max);
            }
        }
    }
}

/**
 * Client-side validator implementation for the Jakarta Bean Validation
 * validator `DecimalMin`.
 */
class DecimalMinValidator implements PrimeType.validation.Validator {

    private readonly MESSAGE_ID = 'jakarta.validation.constraints.DecimalMin.message';

    validate(element: JQuery, value: unknown): void {
        const valueNumber = toValidationNumber(value);
        if (valueNumber !== undefined) {
            const min = toValidationNumber(element.data('p-minvalue'));
            const vc = validation.ValidationContext;

            if (min !== undefined && valueNumber < min) {
                throw vc.getMessageBV(element, this.MESSAGE_ID, element.data('p-decimalmin-msg'), min);
            }
        }
    }
}

/**
 * Client-side validator implementation for the Jakarta Bean Validation
 * validator `Digits`.
 */
class DigitsValidator implements PrimeType.validation.Validator {

    private readonly MESSAGE_ID = 'jakarta.validation.constraints.Digits.message';

    validate(element: JQuery, value: unknown): void {
        if (value !== null) {
            const digitsInteger = toValidationNumber(element.data('p-dintvalue'));
            const digitsFraction = toValidationNumber(element.data('p-dfracvalue'));
            const vc = validation.ValidationContext;
            const locale = core.getLocaleSettings();

            const tokens = String(value).split(locale.decimalSeparator ?? ".");
            const intValue = tokens[0]?.replace(new RegExp(locale.groupingSeparator ?? ",", 'g'), '');
            const decimalValue = tokens[1];

            const violatesDigitsIntegerConstraint = digitsInteger !== undefined && intValue && digitsInteger < intValue.length;
            const violatesDigitsFractionConstraint = digitsFraction !== undefined && decimalValue && decimalValue.length > digitsFraction;

            if (violatesDigitsIntegerConstraint || violatesDigitsFractionConstraint) {
                throw vc.getMessageBV(element, this.MESSAGE_ID, element.data('p-digits-msg'), digitsInteger ?? "", digitsFraction ?? "");
            }
        }
    }
}

/**
 * Client-side validator implementation for the Jakarta Bean Validation
 * validator `Email`.
 */
class EmailValidator implements PrimeType.validation.Validator {

    private readonly MESSAGE_ID = 'jakarta.validation.constraints.Email.message';

    // source: https://stackoverflow.com/questions/13992403/regex-validation-of-email-addresses-according-to-rfc5321-rfc5322/26989421#26989421
    private readonly EMAIL_ADDRESS_REGEX = /^([!#-'*+\/-9=?A-Z^-~-]+(\.[!#-'*+\/-9=?A-Z^-~-]+)*|"([]!#-[^-~ \t]|(\\[\t -~]))+")@([!#-'*+\/-9=?A-Z^-~-]+(\.[!#-'*+\/-9=?A-Z^-~-]+)*|\[[\t -Z^-~]*\])$/;

    validate(element: JQuery, value: unknown): void {
        if (value !== null && !this.EMAIL_ADDRESS_REGEX.test(String(value))) {
            const vc = validation.ValidationContext;
            throw vc.getMessageBV(element, this.MESSAGE_ID, element.data('p-email-msg'));
        }
    }
}

/**
 * Client-side validator implementation for the Jakarta Bean Validation
 * validator `Future`.
 */
class FutureValidator implements PrimeType.validation.Validator {

    private readonly MESSAGE_ID = 'jakarta.validation.constraints.Future.message';

    validate(element: JQuery, value: unknown): void {
        const valueDate = toValidationDate(value);
        if (valueDate !== undefined && valueDate <= new Date()) {
            const vc = validation.ValidationContext;
            throw vc.getMessageBV(element, this.MESSAGE_ID, element.data('p-future-msg'));
        }
    }
}

/**
 * Client-side validator implementation for the Jakarta Bean Validation
 * validator `FutureOrPresent`.
 */
class FutureOrPresentValidator implements PrimeType.validation.Validator {

    private readonly MESSAGE_ID = 'jakarta.validation.constraints.FutureOrPresent.message';

    validate(element: JQuery, value: unknown): void {
        const valueDate = toValidationDate(value);
        if (valueDate !== undefined && valueDate < new Date()) {
            const vc = validation.ValidationContext;
            throw vc.getMessageBV(element, this.MESSAGE_ID, element.data('p-futureorpresent-msg'));
        }
    }
}

/**
 * Client-side validator implementation for the Jakarta Bean Validation
 * validator `Max`.
 */
class MaxValidator implements PrimeType.validation.Validator {

    private readonly MESSAGE_ID = 'jakarta.validation.constraints.Max.message';

    validate(element: JQuery, value: unknown): void {
        const valueNumber = toValidationNumber(value);
        if (valueNumber !== undefined) {
            const max = toValidationNumber(element.data('p-maxvalue'));
            const vc = validation.ValidationContext;

            if (max !== undefined && valueNumber > max) {
                throw vc.getMessageBV(element, this.MESSAGE_ID, element.data('p-max-msg'), max);
            }
        }
    }
}

/**
 * Client-side validator implementation for the Jakarta Bean Validation
 * validator `Min`.
 */
class MinValidator implements PrimeType.validation.Validator {

    private readonly MESSAGE_ID = 'jakarta.validation.constraints.Min.message';

    validate(element: JQuery, value: unknown): void {
        const valueNumber = toValidationNumber(value);
        if (valueNumber !== undefined) {
            const min = toValidationNumber(element.data('p-minvalue'));
            const vc = validation.ValidationContext;

            if (min !== undefined && valueNumber < min) {
                throw vc.getMessageBV(element, this.MESSAGE_ID, element.data('p-min-msg'), min);
            }
        }
    }
}

/**
 * Client-side validator implementation for the Jakarta Bean Validation
 * validator `Negative`.
 */
class NegativeValidator implements PrimeType.validation.Validator {

    private readonly MESSAGE_ID = 'jakarta.validation.constraints.Negative.message';

    validate(element: JQuery, value: unknown): void {
        const valueNumber = toValidationNumber(value);
        if (valueNumber !== undefined && valueNumber >= 0) {
            const max = toValidationNumber(element.data('p-maxvalue'));
            const vc = validation.ValidationContext;
            throw vc.getMessageBV(element, this.MESSAGE_ID, element.data('p-negative-msg'), max ?? "");
        }
    }
}

/**
 * Client-side validator implementation for the Jakarta Bean Validation
 * validator `NegativeOrZero`.
 */
class NegativeOrZeroValidator implements PrimeType.validation.Validator {

    private readonly MESSAGE_ID = 'jakarta.validation.constraints.NegativeOrZero.message';

    validate(element: JQuery, value: unknown): void {
        const valueNumber = toValidationNumber(value);
        if (valueNumber !== undefined && valueNumber > 0) {
            const max = toValidationNumber(element.data('p-maxvalue'));
            const vc = validation.ValidationContext;
            throw vc.getMessageBV(element, this.MESSAGE_ID, element.data('p-negativeorzero-msg'), max ?? "");
        }
    }
}

/**
 * Client-side validator implementation for the Jakarta Bean Validation
 * validator `NotBlank`.
 */
class NotBlankValidator implements PrimeType.validation.Validator {

    private readonly MESSAGE_ID = 'jakarta.validation.constraints.NotBlank.message';

    validate(element: JQuery, value: unknown): void {
        if (value === null || value === undefined || 0 === String(value).trim().length) {
            const vc = validation.ValidationContext;
            throw vc.getMessageBV(element, this.MESSAGE_ID, element.data('p-notblank-msg'));
        }
    }
}

/**
 * Client-side validator implementation for the Jakarta Bean Validation
 * validator `NotEmpty`.
 */
class NotEmptyValidator implements PrimeType.validation.Validator {

    private readonly MESSAGE_ID = 'jakarta.validation.constraints.NotEmpty.message';

    validate(element: JQuery, value: unknown): void {
        if (value === null || value === undefined || 0 === String(value).length) {
            const vc = validation.ValidationContext;
            throw vc.getMessageBV(element, this.MESSAGE_ID, element.data('p-notempty-msg'));
        }
    }
}

/**
 * Client-side validator implementation for the Jakarta Bean Validation
 * validator `NotNull`.
 */
class NotNullValidator implements PrimeType.validation.Validator {

    private readonly MESSAGE_ID = 'jakarta.validation.constraints.NotNull.message';

    validate(element: JQuery, value: unknown): void {
        if (value === null || value === undefined) {
            const vc = validation.ValidationContext;
            throw vc.getMessageBV(element, this.MESSAGE_ID, element.data('p-notnull-msg'));
        }
    }
}

/**
 * Client-side validator implementation for the Jakarta Bean Validation
 * validator `Null`.
 */
class NullValidator implements PrimeType.validation.Validator {

    private readonly MESSAGE_ID = 'jakarta.validation.constraints.Null.message';

    validate(element: JQuery, value: unknown): void {
        if (value !== null) {
            const vc = validation.ValidationContext;
            throw vc.getMessageBV(element, this.MESSAGE_ID, element.data('p-null-msg'));
        }
    }
};

/**
 * Client-side validator implementation for the Jakarta Bean Validation
 * validator `Past`.
 */
class PastValidator implements PrimeType.validation.Validator {

    private readonly MESSAGE_ID = 'jakarta.validation.constraints.Past.message';

    validate(element: JQuery, value: unknown): void {
        const valueDate = toValidationDate(value);
        if (valueDate !== undefined && valueDate >= new Date()) {
            const vc = validation.ValidationContext;
            throw vc.getMessageBV(element, this.MESSAGE_ID, element.data('p-past-msg'));
        }
    }
}

/**
 * Client-side validator implementation for the Jakarta Bean Validation
 * validator `PastOrPresent`.
 */
class PastOrPresentValidator implements PrimeType.validation.Validator {

    private readonly MESSAGE_ID = 'jakarta.validation.constraints.PastOrPresent.message';

    validate(element: JQuery, value: unknown): void {
        const valueDate = toValidationDate(value);
        if (valueDate !== undefined && valueDate > new Date()) {
            const vc = validation.ValidationContext;
            throw vc.getMessageBV(element, this.MESSAGE_ID, element.data('p-pastorpresent-msg'));
        }
    }
}

/**
 * Client-side validator implementation for the Jakarta Bean Validation
 * validator `Pattern`.
 */
class PatternValidator implements PrimeType.validation.Validator {

    private readonly MESSAGE_ID = 'jakarta.validation.constraints.Pattern.message';

    validate(element: JQuery, value: unknown): void {
        if (value !== null) {
            const pattern = element.data('p-pattern');
            const vc = validation.ValidationContext;
            const regex = new RegExp(pattern);

            if (!regex.test(String(value))) {
                throw vc.getMessageBV(element, this.MESSAGE_ID, element.data('p-pattern-msg'), pattern);
            }
        }
    }
}

/**
 * Client-side validator implementation for the Jakarta Bean Validation
 * validator `Positive`.
 */
class PositiveValidator implements PrimeType.validation.Validator {

    private readonly MESSAGE_ID = 'jakarta.validation.constraints.Positive.message';

    validate(element: JQuery, value: unknown): void {
        const valueNumber = toValidationNumber(value);
        if (valueNumber !== undefined && valueNumber <= 0) {
            const min = toValidationNumber(element.data('p-minvalue'));
            const vc = validation.ValidationContext;
            throw vc.getMessageBV(element, this.MESSAGE_ID, element.data('p-positive-msg'), min ?? "");
        }
    }
}

/**
 * Client-side validator implementation for the Jakarta Bean Validation
 * validator `PositiveOrZero`.
 */
class PositiveOrZeroValidator implements PrimeType.validation.Validator {

    private readonly MESSAGE_ID = 'jakarta.validation.constraints.PositiveOrZero.message';

    validate(element: JQuery, value: unknown): void {
        const valueNumber = toValidationNumber(value);
        if (valueNumber !== undefined && valueNumber < 0) {
            const min = toValidationNumber(element.data('p-minvalue'));
            const vc = validation.ValidationContext;
            throw vc.getMessageBV(element, this.MESSAGE_ID, element.data('p-positiveorzero-msg'), min ?? "");
        }
    }
}

/**
 * Client-side validator implementation for the Jakarta Bean Validation
 * validator `Size`.
 */
class SizeValidator implements PrimeType.validation.Validator {

    private readonly MESSAGE_ID = 'jakarta.validation.constraints.Size.message';

    validate(element: JQuery, value: unknown): void {
        if (value !== null) {
            const elementValue = element.val();
            const length = Array.isArray(elementValue) || typeof elementValue === "string" ? elementValue.length : 0;
            const min = toValidationNumber(element.data('p-minlength'));
            const max = toValidationNumber(element.data('p-maxlength'));
            const vc = validation.ValidationContext;

            if ((min !== undefined && length < min) || (max !== undefined && length > max)) {
                throw vc.getMessageBV(element, this.MESSAGE_ID, element.data('p-size-msg'), min ?? "", max ?? "");
            }
        }
    }
}
