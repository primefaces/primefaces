import "./src/juxtapose.js";
import { ImageCompare } from "./src/imagecompare.js";

PrimeFaces.component.ImageCompare = ImageCompare;

declare global {
    namespace PrimeFaces {
        export interface ComponentRegistry {
            ImageCompare: typeof ImageCompare;
        }
    }
}
