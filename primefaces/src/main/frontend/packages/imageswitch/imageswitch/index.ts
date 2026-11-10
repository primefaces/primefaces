import "./src/jquery.cycle.js";
import { ImageSwitch } from "./src/imageswitch.js";

PrimeFaces.widget.ImageSwitch = ImageSwitch;

declare global {
    namespace PrimeFacesTypes {
        export interface WidgetRegistry {
            ImageSwitch: typeof ImageSwitch;
        }
    }

    namespace PrimeFacesTypes.widget {
        export type ImageSwitchCfg = import("./src/imageswitch.js").ImageSwitchCfg;
    }
}
