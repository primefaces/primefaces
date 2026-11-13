import { Stack } from "./src/stack.js";

PrimeFaces.component.Stack = Stack;

declare global {
    namespace PrimeFaces {
        export interface ComponentRegistry {
            Stack: typeof Stack;
        }
    }
}
