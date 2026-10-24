import { DatePicker } from "./src/datepicker.widget.js";

PrimeFaces.widget.DatePicker = DatePicker;

declare global {
    namespace PrimeType {
        export interface WidgetRegistry {
            DatePicker: typeof DatePicker;
        }
    }

    namespace PrimeType.widget {
        export type DatePickerCfg = import("./src/datepicker.widget.js").DatePickerCfg;
    }
}
