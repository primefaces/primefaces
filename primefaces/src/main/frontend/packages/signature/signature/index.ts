import { Signature } from "./src/signature.js";

PrimeFaces.widget.Signature = Signature;

declare global {
    namespace PrimeFacesTypes {
        export interface WidgetRegistry {
            Signature: typeof Signature;
        }
    }

    namespace PrimeFacesTypes.widget {
        export type SignatureCfg = import("./src/signature.js").SignatureCfg;
    }
}
