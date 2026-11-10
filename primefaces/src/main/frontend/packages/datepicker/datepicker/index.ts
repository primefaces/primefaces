import { DatePicker } from "./src/datepicker.js";

PrimeFaces.widget.DatePicker = DatePicker;

declare global {
    namespace PrimeFacesTypes {
        export interface WidgetRegistry {
            DatePicker: typeof DatePicker;
        }
    }

    namespace PrimeFacesTypes.widget {
        export type DatePickerCfg = import("./src/datepicker.js").DatePickerCfg;
    }
}
