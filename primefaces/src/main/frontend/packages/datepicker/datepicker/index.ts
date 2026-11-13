import { DatePicker } from "./src/datepicker.js";

PrimeFaces.component.DatePicker = DatePicker;

declare global {
    namespace PrimeFaces {
        export interface ComponentRegistry {
            DatePicker: typeof DatePicker;
        }
    }
}
