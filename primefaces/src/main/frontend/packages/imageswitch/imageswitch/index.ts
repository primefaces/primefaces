import "./src/jquery.cycle.js";
import { ImageSwitch } from "./src/imageswitch.js";

PrimeFaces.component.ImageSwitch = ImageSwitch;

declare global {
    namespace PrimeFaces {
        export interface ComponentRegistry {
            ImageSwitch: typeof ImageSwitch;
        }
    }
}
