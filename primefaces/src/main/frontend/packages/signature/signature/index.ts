import { Signature } from "./src/signature.js";

PrimeFaces.component.Signature = Signature;

declare global {
    namespace PrimeFacesTypes {
        export interface ComponentRegistry {
            Signature: typeof Signature;
        }
    }

    namespace PrimeFacesTypes.component {
        export type SignatureCfg = import("./src/signature.js").SignatureCfg;
    }
}
