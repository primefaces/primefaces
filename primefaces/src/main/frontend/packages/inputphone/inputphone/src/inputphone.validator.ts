import { parsePhoneNumberFromString } from "libphonenumber-js/max";

/** The message of an invalid number, the same as on the server. */
export const INVALID_MESSAGE_ID = 'primefaces.inputphone.INVALID';

/** The default message, the locales (e.g. locale-de.js) translate it. */
const INVALID_MESSAGE = '{0}: Validation Error: Value is not a valid phone number.';

/**
 * Whether a country is allowed by the onlyCountries and excludeCountries, like the server side validation.
 * @param country The ISO 3166-1 alpha-2 code, `undefined` for a number without country (e.g. +800).
 * @param onlyCountries The countries the number may belong to, all if empty.
 * @param excludeCountries The countries the number must not belong to.
 * @returns `true` if the country is allowed.
 */
export function isAllowedCountry(country: string | undefined, onlyCountries: string[], excludeCountries: string[]): boolean {
    return country
        ? (onlyCountries.length === 0 || onlyCountries.includes(country)) && !excludeCountries.includes(country)
        : onlyCountries.length === 0;
}

/**
 * Client side validator of p:inputPhone (`primefaces.InputPhone`): a value that is not empty must be a number in the
 * E.164 format that is valid for its country (libphonenumber-js), which must be one of the onlyCountries and none of
 * the excludeCountries. The renderer adds it with the lists to the hidden input with the value.
 */
export class InputPhoneValidator implements PrimeFacesTypes.validation.Validator {

    validate(element: JQuery, value: unknown): void {
        const text = value == null ? '' : String(value);
        const phoneNumber = /^\+[1-9]\d{1,14}$/.test(text) ? parsePhoneNumberFromString(text) : undefined;
        const toList = (attr: string): string[] => (element.attr(attr) ?? '').split(',').filter((code) => code);
        if (text !== '' && !(phoneNumber?.isValid()
                && isAllowedCountry(phoneNumber.country, toList('data-p-phone-only'), toList('data-p-phone-exclude')))) {
            const vc = PrimeFaces.validation.ValidationContext;
            this.registerDefaultMessage();
            throw vc.getMessage(INVALID_MESSAGE_ID, vc.getLabel(element));
        }
    }

    /**
     * Adds the default message to en_US if it has none. Done on validation, not when the script is loaded, because a
     * locale loaded later (e.g. locale-en.js) replaces the en_US locale.
     */
    private registerDefaultMessage(): void {
        const enUs = PrimeFaces.locales["en_US"];
        if (enUs) {
            enUs.messages ??= {};
            enUs.messages[INVALID_MESSAGE_ID] ??= INVALID_MESSAGE;
        }
    }
}
