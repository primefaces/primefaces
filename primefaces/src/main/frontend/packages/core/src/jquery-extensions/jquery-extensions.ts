import { setupFocusDelay } from "./jquery.focus.js";
import { setupFocusSelectors } from "./jquery.selectors.js";
import { setupRemoveEvent } from "./jquery.remove-event.js";
import { setupZIndex } from "./jquery.zindex.js";

/**
 * Global setup of the jQuery extensions of PrimeFaces, called once when PrimeFaces is loaded: `$.fn.focus(delay)`,
 * `$.fn.zIndex()`, the `:focusable` and `:tabbable` selectors and the `remove` event on removed elements.
 */
export function globalJQueryExtensionsSetup(): void {
    setupFocusDelay();
    setupZIndex();
    setupFocusSelectors();
    setupRemoveEvent();
}
