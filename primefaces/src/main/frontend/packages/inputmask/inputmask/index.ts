/// <reference types="inputmask" preserve="true" />

import "inputmask/dist/jquery.inputmask.js";
import _Inputmask from "inputmask/dist/jquery.inputmask.js";

import { InputMask } from "./src/inputmask.js";

PrimeFaces.widget.InputMask = InputMask;

// Expose InputMask to the global scope
Object.assign(window, { Inputmask: _Inputmask });

declare global {
    interface JQuery {
        /**
         * Sets the value of the input, the value is masked. `@types/inputmask` only declares `setValue`, but the jQuery
         * plugin expects `setvalue`.
         * @param method Name of the method to invoke.
         * @param value The new value.
         */
        inputmask(method: "setvalue", value: string): void;
    }

    namespace PrimeFacesTypes {
        export interface WidgetRegistry {
            InputMask: typeof InputMask;
        }
    }

    namespace PrimeFacesTypes.widget {
        export type InputMaskCfg = import("./src/inputmask.js").InputMaskCfg;
    }
}
