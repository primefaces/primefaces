import { Stack } from "./src/stack.widget.js";

PrimeFaces.widget.Stack = Stack;

declare global {
    namespace PrimeFacesTypes {
        export interface WidgetRegistry {
            Stack: typeof Stack;
        }
    }

    namespace PrimeFacesTypes.widget {
        export type StackCfg = import("./src/stack.widget.js").StackCfg;
    }
}
