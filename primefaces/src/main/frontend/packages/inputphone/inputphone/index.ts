import { InputPhone } from "./src/inputphone.js";
import { InputPhoneValidator } from "./src/inputphone.validator.js";

PrimeFaces.component.InputPhone = InputPhone;
PrimeFaces.validator["primefaces.InputPhone"] = new InputPhoneValidator();

declare global {
    namespace PrimeFacesTypes {
        export interface ComponentRegistry {
            InputPhone: typeof InputPhone;
        }
    }

    namespace PrimeFacesTypes.component {
        export type InputPhoneCfg = import("./src/inputphone.js").InputPhoneCfg;
    }

    namespace PrimeFacesTypes.validation {
        interface ValidatorInstanceMap {
            "primefaces.InputPhone"?: Validator;
        }
    }
}
