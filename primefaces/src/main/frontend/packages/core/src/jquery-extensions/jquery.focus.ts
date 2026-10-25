declare global {
    interface JQuery {
        /**
         * Focuses the elements after the given delay.
         * @param delay Delay in milliseconds.
         * @param callback Called with each element after it was focused.
         * @returns this for chaining.
         */
        focus(delay: number, callback?: (this: HTMLElement) => void): this;
    }
}

/**
 * Extends `$.fn.focus`: with a number as first argument, the elements are focused after that delay, otherwise it is the
 * jQuery `focus` method.
 */
export function setupFocusDelay(): void {
    const focus = $.fn.focus as (...args: unknown[]) => JQuery;
    $.fn.focus = function(this: JQuery, delay?: unknown, callback?: (this: HTMLElement) => void): JQuery {
        if (typeof delay !== "number") {
            // eslint-disable-next-line prefer-rest-params
            return focus.apply(this, Array.from(arguments));
        }
        return this.each(function() {
            const element = this;
            setTimeout(() => {
                $(element).trigger("focus");
                callback?.call(element);
            }, delay);
        });
    } as typeof $.fn.focus;
}
