import jQuery from "jquery";

// We include jquery.browser in the jquery bundle because it is needed both
// by `jquery-plugins.ts` and `core.ts`. We cannot include it in `jquery-plugins.ts`,
// because there are situations when only core.ts is loaded, and vice versa.
import "jquery.browser";

// Expose jQuery to the global scope
// Our code (should) import jquery, so this would not be needed
// But external code needs to access jQuery via the global scope
Object.assign(window, { jQuery, $: jQuery });

// Expose jqBrowser to the global scope
// Not needed for our code, but may already be used by external code
//
// Also, needed by some external libraries such as jquery.imageswitch
// and jquery.caretposition that demand this global variable to be
// available (instead of properly importing it as a module).
import jQBrowser from "jquery.browser";
Object.assign(window, { jQBrowser });
