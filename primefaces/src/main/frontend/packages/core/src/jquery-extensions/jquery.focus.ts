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
    $.fn.focus = function(this: JQuery, ...args: unknown[]): JQuery {
        const [delay, callback] = args as [unknown, ((this: HTMLElement) => void) | undefined];
        if (typeof delay !== "number") {
            return focus.apply(this, args);
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
