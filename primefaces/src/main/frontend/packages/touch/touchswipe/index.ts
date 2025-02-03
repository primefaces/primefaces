import "jquery-touchswipe";

import type { JQueryTouchSwipe } from "./src/touchswipe.js";

declare global {
    interface JQuery<TElement = HTMLElement> {
        /**
         * The main function of the TouchSwipe plugin
         * 
         * Also contains some constants and the default settings.
         */
        swipe: JQueryTouchSwipe.TouchSwipeNamespace<TElement>;
    }
}
