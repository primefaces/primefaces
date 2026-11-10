import { Organigram } from "./src/organigram.widget.js";

PrimeFaces.widget.Organigram = Organigram;

declare global {
    namespace PrimeFacesTypes {
        export interface WidgetRegistry {
            Organigram: typeof Organigram;
        }
    }

    namespace PrimeFacesTypes.widget {
        export type OrganigramCfg = import("./src/organigram.widget.js").OrganigramCfg;
    }
}
