import { Organigram } from "./src/organigram.js";

PrimeFaces.component.Organigram = Organigram;

declare global {
    namespace PrimeFaces {
        export interface ComponentRegistry {
            Organigram: typeof Organigram;
        }
    }
}
