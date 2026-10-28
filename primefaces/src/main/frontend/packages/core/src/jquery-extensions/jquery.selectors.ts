/**
 * Checks whether an element can get the focus: enabled form elements, links with href, areas of a visible image map
 * and elements with a tabindex, if visible.
 * @param element The element to check.
 * @param hasTabindex Whether the element has a tabindex attribute.
 * @returns `true` if the element can get the focus.
 */
function isFocusable(element: Element, hasTabindex: boolean): boolean {
    const nodeName = element.nodeName.toLowerCase();

    if (nodeName === "area") {
        const map = element.parentElement as HTMLMapElement | null;
        if (!(element as HTMLAreaElement).href || !map?.name || map.nodeName.toLowerCase() !== "map") {
            return false;
        }
        const image = $("img[usemap='#" + CSS.escape(map.name) + "']");
        return image.length > 0 && image.is(":visible");
    }

    let focusableIfVisible: boolean;
    if (/^(input|select|textarea|button|object)$/.test(nodeName)) {
        // a disabled fieldset disables its form elements
        focusableIfVisible = !(element as HTMLInputElement).disabled && !(element.closest("fieldset") as HTMLFieldSetElement | null)?.disabled;
    }
    else if (nodeName === "a") {
        focusableIfVisible = !!(element as HTMLAnchorElement).href || hasTabindex;
    }
    else {
        focusableIfVisible = hasTabindex;
    }

    return focusableIfVisible && $(element).is(":visible") && $(element).css("visibility") === "visible";
}

/**
 * Adds the `:focusable` and `:tabbable` selectors (formerly jQuery UI), e.g. for the focus traps of dialogs and
 * overlays.
 */
export function setupFocusSelectors(): void {
    Object.assign($.expr.pseudos, {
        /**
         * The `:focusable` selector: elements that can get the focus, by mouse or script.
         * @param element The element to check.
         * @returns `true` if the element can get the focus.
         */
        focusable: (element: Element) => isFocusable(element, element.getAttribute("tabindex") !== null),
        /**
         * The `:tabbable` selector: focusable elements reachable with the Tab key (no negative tabindex).
         * @param element The element to check.
         * @returns `true` if the element can be reached with the Tab key.
         */
        tabbable: (element: Element) => {
            const tabIndex = element.getAttribute("tabindex");
            const hasTabindex = tabIndex !== null;
            return (!hasTabindex || Number(tabIndex) >= 0) && isFocusable(element, hasTabindex);
        }
    });
}
