import { Organigram } from "./src/organigram.js";

PrimeFaces.component.Organigram = Organigram;

declare global {
    namespace PrimeFacesTypes {
        export interface ComponentRegistry {
            Organigram: typeof Organigram;
        }
    }

    namespace PrimeFacesTypes.component {
        export type OrganigramCfg = import("./src/organigram.js").OrganigramCfg;
    }
}
