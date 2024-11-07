import jQuery from "jquery";

// Expose jQuery to the global scope
// Our code (should) import jquery, so this would not be needed
// But external code needs to access jQuery via the global scope
Object.assign(window, { jQuery, $: jQuery });