import { InputPhone } from "./src/inputphone.js";
import { InputPhoneValidator } from "./src/inputphone.validator.js";

PrimeFaces.component.InputPhone = InputPhone;
PrimeFaces.validator["primefaces.InputPhone"] = new InputPhoneValidator();

declare global {
    namespace PrimeFaces {
        export interface ComponentRegistry {
            InputPhone: typeof InputPhone;
        }
    }

    namespace PrimeFaces.validation {
        interface ValidatorInstanceMap {
            "primefaces.InputPhone"?: Validator;
        }
    }
}
