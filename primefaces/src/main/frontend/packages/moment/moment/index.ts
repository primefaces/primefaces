/// <reference types="moment-jdateformatparser" preserve="true" />

import _moment from "moment";
import _momentJDateFormatParserSetup from "moment-jdateformatparser";

declare global {
    const momentJDateFormatParserSetup: typeof _momentJDateFormatParserSetup;
    interface Window {
        moment: typeof _moment;
        momentJDateFormatParserSetup: typeof _momentJDateFormatParserSetup;
    }
}

// Expose moment to the global scope
// Not needed for our code, but might already be used by external code
Object.assign(window, { moment: _moment, momentJDateFormatParserSetup: _momentJDateFormatParserSetup });
