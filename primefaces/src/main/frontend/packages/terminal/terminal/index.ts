import { Terminal } from "./src/terminal.js";

PrimeFaces.widget.Terminal = Terminal;

declare global {
    namespace PrimeFacesTypes {
        export interface WidgetRegistry {
            Terminal: typeof Terminal;
        }
    }

    namespace PrimeFacesTypes.widget {
        export type TerminalCfg = import("./src/terminal.js").TerminalCfg;
    }

    namespace PrimeFacesTypes.widget.Terminal {
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
