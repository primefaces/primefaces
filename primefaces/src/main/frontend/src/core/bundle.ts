import "./core/core.js";
import "./core/core.env.js";
import "./core/core.ajax.js";
import "./core/core.csp.js";
import "./core/core.expressions.js";
import "./core/core.utils.js";
import "./core/core.widget.js";
import "./core/core.resources.js";
import "./core/core.clientwindow.js";
import "./ajaxstatus/ajaxstatus.js";
import "./poll/poll.js";
import "./validation/validation.common.js";
import "./validation/validation.converters.js";
import "./validation/validation.validators.js";
import "./validation/validation.highlighters.js";

// Expose js-cookie to the global scope
// Not needed for our code, but may already be used by external code
import Cookies from "js-cookie";
Object.assign(window, { Cookies });