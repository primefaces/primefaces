import "./src/juxtapose.js";
import { ImageCompare } from "./src/imagecompare.js";

PrimeFaces.component.ImageCompare = ImageCompare;

declare global {
    namespace PrimeFacesTypes {
        export interface ComponentRegistry {
            ImageCompare: typeof ImageCompare;
        }
    }

    namespace PrimeFacesTypes.component {
        export type ImageCompareCfg = import("./src/imagecompare.js").ImageCompareCfg;
    }
}
