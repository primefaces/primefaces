declare global {
    interface JQuery {
        /**
         * Gets the z-index of the first element: the one of the element or of its closest positioned parent with a
         * z-index.
         * @returns The z-index, `0` if there is none.
         */
        zIndex(): number;

        /**
         * Sets the z-index of the elements.
         * @param zIndex The z-index to set.
         * @returns this for chaining.
         */
        zIndex(zIndex: number | string): this;
    }
}

/**
 * Adds `$.fn.zIndex` (formerly jQuery UI), e.g. for the overlays and the modal focus trap.
 */
export function setupZIndex(): void {
    $.fn.zIndex = function(this: JQuery, zIndex?: number | string): JQuery | number {
        if (zIndex !== undefined) {
            return this.css("zIndex", String(zIndex));
        }

        let element = this.first();
        while (element.length && element[0]!.nodeType !== Node.DOCUMENT_NODE) {
            // the z-index only applies to a positioned element; an explicit 0 of a nested element is ignored
            const position = element.css("position");
            if (position === "absolute" || position === "relative" || position === "fixed") {
                const value = parseInt(element.css("zIndex"), 10);
                if (!isNaN(value) && value !== 0) {
                    return value;
                }
            }
            element = element.parent();
        }
        return 0;
    } as JQuery["zIndex"];
}
