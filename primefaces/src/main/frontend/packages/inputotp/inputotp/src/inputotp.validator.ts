/**
 * Client side validator of p:inputOtp (`primefaces.InputOtp`): a value that is not empty must have all characters,
 * like the server side validation. The renderer adds it to the hidden input with the value.
 */
export class InputOtpValidator implements PrimeFacesTypes.validation.Validator {
    private readonly MINIMUM_MESSAGE_ID = 'jakarta.faces.validator.LengthValidator.MINIMUM';
    private readonly MAXIMUM_MESSAGE_ID = 'jakarta.faces.validator.LengthValidator.MAXIMUM';

    validate(element: JQuery): void {
        const length = Number(element.data('p-otp-length'));
        // the submitted code like on the server, not the converted value (e.g. "012345" converted to a number)
        const text = String(element.val() ?? '');
        // one character per input, a character outside the BMP (e.g. an emoji) is one code point
        const textLength = Array.from(text).length;
        if (text !== '' && textLength !== length) {
            const vc = PrimeFaces.validation.ValidationContext;
            throw vc.getMessage(textLength < length ? this.MINIMUM_MESSAGE_ID : this.MAXIMUM_MESSAGE_ID, length, vc.getLabel(element));
        }
    }
}
