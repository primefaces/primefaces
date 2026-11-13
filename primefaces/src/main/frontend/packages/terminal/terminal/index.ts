import { Terminal } from "./src/terminal.js";

PrimeFaces.component.Terminal = Terminal;

declare global {
    namespace PrimeFaces {
        export interface ComponentRegistry {
            Terminal: typeof Terminal;
        }
    }
}
