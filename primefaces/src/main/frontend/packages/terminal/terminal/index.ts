import { Terminal } from "./src/terminal.js";

PrimeFaces.component.Terminal = Terminal;

declare global {
    namespace PrimeFacesTypes {
        export interface ComponentRegistry {
            Terminal: typeof Terminal;
        }
    }

    namespace PrimeFacesTypes.component {
        export type TerminalCfg = import("./src/terminal.js").TerminalCfg;
    }

    namespace PrimeFacesTypes.component.Terminal {
        /**
         * The JSON response of the server for the autocompletion of a command.
         */
        export interface AutoCompleteResponse {
            /** The command entered so far, without the argument that is completed. */
            baseCommand: string;
            /** The commands or arguments matching the entered text. */
            matches: string[];
        }
    }
}
