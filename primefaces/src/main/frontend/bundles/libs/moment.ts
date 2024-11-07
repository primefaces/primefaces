import moment from "moment";
import momentJDateFormatParserSetup from "moment-jdateformatparser";

// Expose moment to the global scope
// Not needed for our code, but might already be used by external code
Object.assign(window, { moment, momentJDateFormatParserSetup });