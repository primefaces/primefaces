import { Captcha } from "./src/captcha.js";

PrimeFaces.component.Captcha = Captcha;

declare global {
    namespace PrimeFaces {
        export interface ComponentRegistry {
            Captcha: typeof Captcha;
        }
    }
}
