import { Stack } from "./src/stack.js";

PrimeFaces.component.Stack = Stack;

declare global {
    namespace PrimeFacesTypes {
        export interface ComponentRegistry {
            Stack: typeof Stack;
        }
    }

    namespace PrimeFacesTypes.component {
        export type StackCfg = import("./src/stack.js").StackCfg;
    }
}
