import { DatePicker } from "./src/datepicker.js";

PrimeFaces.component.DatePicker = DatePicker;

declare global {
    namespace PrimeFacesTypes {
        export interface ComponentRegistry {
            DatePicker: typeof DatePicker;
        }
    }

    namespace PrimeFacesTypes.component {
        export type DatePickerCfg = import("./src/datepicker.js").DatePickerCfg;
    }
}
