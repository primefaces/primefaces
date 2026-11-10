import { Captcha } from "./src/captcha.js";

PrimeFaces.widget.Captcha = Captcha;

declare global {
    namespace PrimeFacesTypes {
        export interface WidgetRegistry {
            Captcha: typeof Captcha;
        }
    }

    namespace PrimeFacesTypes.widget {
        export type CaptchaCfg = import("./src/captcha.js").CaptchaCfg;
    }

    namespace PrimeFacesTypes.widget.Captcha {
        /**
         * Captcha features light and dark modes for theme.
         */
        export type Theme = "auto" | "light" | "dark";

        /**
         * The parts of the captcha API (e.g. `grecaptcha`, see the `executor` attribute) used by the widget.
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
