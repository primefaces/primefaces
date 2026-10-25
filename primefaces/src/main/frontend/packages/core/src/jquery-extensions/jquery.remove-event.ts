/**
 * Triggers the `remove` event on removed elements, e.g. by `.remove()`, `.html()` or `.replaceWith()` (formerly the
 * jQuery UI widget factory). jQuery calls `$.cleanData` for every removed element before it deletes its data and
 * events; the event is triggered on each element that has a handler for it and does not bubble. The widgets and the
 * interactions use it to detect that an AJAX update removed them.
 */
export function setupRemoveEvent(): void {
    const cleanData = $.cleanData as (elements: ArrayLike<Element>) => void;
    ($ as unknown as { cleanData: (elements: ArrayLike<Element>) => void }).cleanData = (elements) => {
        for (let i = 0; i < elements.length; i++) {
            const element = elements[i];
            const events = element ? ($ as unknown as { _data(element: Element, key: string): Record<string, unknown> | undefined })
                ._data(element, "events") : undefined;
            if (element && events?.["remove"]) {
                $(element).triggerHandler("remove");
            }
        }
        cleanData(elements);
    };
}
