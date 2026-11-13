import { InputOtp } from "./src/inputotp.js";
import { InputOtpValidator } from "./src/inputotp.validator.js";

PrimeFaces.component.InputOtp = InputOtp;
PrimeFaces.validator["primefaces.InputOtp"] = new InputOtpValidator();

declare global {
    namespace PrimeFaces {
        export interface ComponentRegistry {
            InputOtp: typeof InputOtp;
        }
    }

    namespace PrimeFaces.validation {
        interface ValidatorInstanceMap {
            "primefaces.InputOtp"?: Validator;
        }
    }
}
