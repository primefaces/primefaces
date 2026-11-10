import "./src/juxtapose.js";
import { ImageCompare } from "./src/imagecompare.js";

PrimeFaces.widget.ImageCompare = ImageCompare;

declare global {
    namespace PrimeFacesTypes {
        export interface WidgetRegistry {
            ImageCompare: typeof ImageCompare;
        }
    }

    namespace PrimeFacesTypes.widget {
        export type ImageCompareCfg = import("./src/imagecompare.js").ImageCompareCfg;
    }
}
