// Expose moment to the global scope
import moment from "moment";
import momentJDateFormatParserSetup from "moment-jdateformatparser";
Object.assign(window, { moment, momentJDateFormatParserSetup });