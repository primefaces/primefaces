import Cookies from "js-cookie";

import "./src/core/core.js";
import "./src/core/core.env.js";
import "./src/core/core.ajax.js";
import "./src/core/core.csp.js";
import "./src/core/core.expressions.js";
import "./src/core/core.utils.js";
import "./src/core/core.widget.js";
import "./src/core/core.resources.js";
import "./src/core/core.clientwindow.js";
import "./src/ajaxexceptionhandler/ajaxexceptionhandler.js";
import "./src/ajaxstatus/ajaxstatus.js";
import "./src/poll/poll.js";
import "./src/validation/validation.common.js";
import "./src/validation/validation.converters.js";
import "./src/validation/validation.validators.js";
import "./src/validation/validation.highlighters.js";
import "./src/validation/validation.bv.js";

declare global {
    interface Window {
        Cookies: typeof Cookies;
    }
}

// Expose js-cookie to the global scope
Object.assign(window, { Cookies });