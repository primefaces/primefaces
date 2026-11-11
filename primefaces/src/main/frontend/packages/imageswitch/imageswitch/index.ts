import "./src/jquery.cycle.js";
import { ImageSwitch } from "./src/imageswitch.js";

PrimeFaces.component.ImageSwitch = ImageSwitch;

declare global {
    namespace PrimeFacesTypes {
        export interface ComponentRegistry {
            ImageSwitch: typeof ImageSwitch;
        }
    }

    namespace PrimeFacesTypes.component {
        export type ImageSwitchCfg = import("./src/imageswitch.js").ImageSwitchCfg;
    }
}
