import { Signature } from "./src/signature.js";

PrimeFaces.component.Signature = Signature;

declare global {
    namespace PrimeFaces {
        export interface ComponentRegistry {
            Signature: typeof Signature;
        }
    }
}
