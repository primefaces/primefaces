/**
 * Converts the value to a number.
 * 
 * - When a number, returns that number if not NaN, or undefined otherwise.
 * - When a string, uses parseFloat to parse as a number, then proceed as above.
 * - When a boolean, return 0 if false and 1 if true.
 * - Otherwise, return undefined.
 * @param value Value to convert.
 * @returns The value as a number, undefined if not a number.
 */
export function toValidationNumber(value: unknown): number | undefined {
    if (value === undefined || value === null) {
        return undefined;
    }
    if (typeof value === "number") {
        return Number.isNaN(value) ? undefined : value;
    }
    if (typeof value === "string") {
        const parsed = value.length > 0 ? parseFloat(value) : undefined;
        return Number.isNaN(parsed) ? undefined : parsed;
    }
    if (typeof value === "boolean") {
        return value ? 1 : 0;
    }
    return undefined;
}

/**
 * Converts the value to a Date.
 * 
 * - When a Date, returns that unchanged.
 * - When a number or string, constructs a new Date object from that string
 *   and returns if if valid, and undefined if invalid.
 * - Otherwise, return undefined.
 * @param value Value to convert.
 * @returns The value as a Date, undefined if not a valid Date.
 */
export function toValidationDate(value: unknown): Date | undefined {
    if (value === undefined || value === null) {
        return undefined;
    }
    if (value instanceof Date) {
        return value;
    }
    if (typeof value === "number" || typeof value === "string") {
        // When value is not a valid date, the timestamp will be set to NaN
        const date = new Date(value);
        return Number.isNaN(date.getTime()) ? undefined : date;
    }
    return undefined;
}

/**
 * Converts the value to a list of Faces messages or validation errors.
 * @param value Value to convert.
 * @returns List of converted values.
 */
export function toMessageOrErrorList(value: unknown): (Error | PrimeType.BaseFacesMessage)[] {
    // e.g. PrimeFaces.validator['primefaces.File'] may return an array of messages
    return Array.isArray(value) ?  value.map(v => toMessageOrError(v)): [toMessageOrError(value)];
}


/**
 * Converts the value to a Faces messages or validation error.
 * 
 * - When an Error, returns that unchanged.
 * - When an object with a `summary` or `detail` property, returns that.
 * - Otherwise, converts the value to an error and returns that.
 * @param value Value to convert.
 * @returns Converted value.
 */
function toMessageOrError(value: unknown): Error | PrimeType.BaseFacesMessage {
    if (value instanceof Error) {
        return value;
    }
    if (typeof value === "object" && value !== null) {
        if ("summary" in value || "detail" in value) {
            const message = value as PrimeType.BaseFacesMessage;
            if (typeof message.summary !== "string") {
                message.summary = "";
            }
            if (typeof message.detail !== "string") {
                message.detail = "";
            }
            return message;
        }
    }
    return toError(value);
}

/**
 * Converts a value to an error.
 * 
 * - When an Error, returns that unchanged.
 * - Otherwise, parses the value as a string and returns a new Error with the
 * string as the error message.
 * @param value Value to convert.
 * @returns The converted error.
 */
function toError(value: unknown): Error {
    return value instanceof Error ? value : new Error(String(value ?? ""));
}
