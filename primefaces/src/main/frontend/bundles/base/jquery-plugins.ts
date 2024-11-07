// TODO autosize actually has nothing to do with jQuery, doesn't register any JQ plugin
// It's also only used by the textarea component -- should perhaps be a separate lib file?
import autosize from "autosize";
import "../../src/jquery/jquery.ui.cjs";
import "../../src/jquery/jquery.caretposition.js";
import "../../src/jquery/jquery.mousewheel.cjs";
import "../../src/jquery/jquery.rangy.js";
import "../../src/jquery/jquery.ui.touch-punch.cjs";
import "../../src/jquery/jquery.ui.pfextensions.js";

// Expose autosize to the global scope
// Not needed for our code, but may already be used by external code
Object.assign(window, { autosize });