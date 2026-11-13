import "./src/jquery.keypad.js";
import { KeyboardUtils } from "./src/keyboard.utils.js";
import { Keyboard } from "./src/keyboard.js";

// KeyboardUtils is not a component, but it was always available in the component scope
Object.assign(PrimeFaces.component, { KeyboardUtils });
PrimeFaces.component.Keyboard = Keyboard;

declare global {
    namespace PrimeFaces {
        export interface ComponentRegistry {
            Keyboard: typeof Keyboard;
        }
    }
}
