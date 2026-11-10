import { InputOtp } from "./src/inputotp.js";
import { InputOtpValidator } from "./src/inputotp.validator.js";

PrimeFaces.widget.InputOtp = InputOtp;
PrimeFaces.validator["primefaces.InputOtp"] = new InputOtpValidator();

declare global {
    namespace PrimeFacesTypes {
        export interface WidgetRegistry {
            InputOtp: typeof InputOtp;
        }
    }

    namespace PrimeFacesTypes.widget {
        export type InputOtpCfg = import("./src/inputotp.js").InputOtpCfg;
    }

    namespace PrimeFacesTypes.validation {
        interface ValidatorInstanceMap {
            "primefaces.InputOtp"?: Validator;
        }
    }
}
