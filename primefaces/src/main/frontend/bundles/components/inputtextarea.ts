import autosize from "autosize";

import "../../src/forms/forms.inputtextarea.js";

// Expose autosize to the global scope
// Not needed for our code, but may already be used by external code
Object.assign(window, { autosize });
