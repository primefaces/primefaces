import { Organigram } from "./src/organigram.js";

PrimeFaces.widget.Organigram = Organigram;

declare global {
    namespace PrimeFacesTypes {
        export interface WidgetRegistry {
            Organigram: typeof Organigram;
        }
    }

    namespace PrimeFacesTypes.widget {
        export type OrganigramCfg = import("./src/organigram.js").OrganigramCfg;
    }
}
