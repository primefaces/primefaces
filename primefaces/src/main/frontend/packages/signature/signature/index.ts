import { Signature } from "./src/signature.widget.js";

PrimeFaces.widget.Signature = Signature;

declare global {
    namespace PrimeType {
        export interface WidgetRegistry {
            Signature: typeof Signature;
        }
    }

    namespace PrimeType.widget {
        export type SignatureCfg = import("./src/signature.widget.js").SignatureCfg;
    }
}
