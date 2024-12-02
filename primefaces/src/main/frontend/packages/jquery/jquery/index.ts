import jQuery from "jquery";
import _jQBrowser from "jquery.browser";

declare global {
    const foo: string;
    const jQBrowser: typeof _jQBrowser;
    interface Window {
        $: typeof jQuery;
        jQuery: typeof jQuery;
    }
}

/// <reference types="jquery" preserve="true" />
Object.assign(window, { $: jQuery, jQuery });

// Expose jqBrowser to the global scope
//
// We include jquery.browser in the jquery bundle because it is needed both
// by `jquery-plugins.ts` and `core.ts`. We cannot include it in `jquery-plugins.ts`,
// because there are situations when only core.ts is loaded, and vice versa.
Object.assign(window, { jQBrowser: _jQBrowser });