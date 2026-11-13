import { Captcha } from "./src/captcha.js";

PrimeFaces.component.Captcha = Captcha;

declare global {
    namespace PrimeFacesTypes {
        export interface ComponentRegistry {
            Captcha: typeof Captcha;
        }
    }

    namespace PrimeFacesTypes.component {
        export type CaptchaCfg = import("./src/captcha.js").CaptchaCfg;
    }

    namespace PrimeFacesTypes.component.Captcha {
        /**
         * Captcha features light and dark modes for theme.
         */
        export type Theme = "auto" | "light" | "dark";

        /**
         * The parts of the captcha API (e.g. `grecaptcha`, see the `executor` attribute) used by the component.
         */
        export interface Executor {
            /**
             * Renders the captcha into the given container.
             * @param container The container of the captcha.
             * @param parameters The parameters of the captcha, such as the `sitekey`.
             */
            render(container: HTMLElement, parameters: Record<string, unknown>): unknown;
            /**
             * Invokes the challenge, for an invisible captcha.
             */
            execute(): unknown;
        }
    }
}
