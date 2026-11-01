import { InputPhone } from "./src/inputphone.widget.js";
import { InputPhoneValidator } from "./src/inputphone.validator.js";

PrimeFaces.widget.InputPhone = InputPhone;
PrimeFaces.validator["primefaces.InputPhone"] = new InputPhoneValidator();

declare global {
    namespace PrimeFacesTypes {
        export interface WidgetRegistry {
            InputPhone: typeof InputPhone;
        }
    }

    namespace PrimeFacesTypes.widget {
        export type InputPhoneCfg = import("./src/inputphone.widget.js").InputPhoneCfg;
    }

    namespace PrimeFacesTypes.validation {
        interface ValidatorInstanceMap {
            "primefaces.InputPhone"?: Validator;
        }
    }
}
