// Expose jQuery to the global scope
import jQuery from "jquery";
Object.assign(window, { jQuery, $: jQuery });