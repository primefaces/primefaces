/**
 * The configuration for the {@link Captcha} widget.
 */
export interface CaptchaCfg extends PrimeFacesTypes.widget.BaseWidgetCfg {
    /**
     * Name of a function in the global scope. Callback to be executed when the user submits a successful CAPTCHA
     * response. The user's response, goggle recaptcha-response, will be the input for your callback function.
     */
    callback: string;
    /** Name of the global variable of the captcha API, e.g. `grecaptcha`. */
    executor: string;
    /**
     * Name of a function in the global scope. The callback executed when the captcha response expires and the user
     * needs to solve a new captcha.
     */
    expired: string;
    /** Language in which information about this captcha is shown. */
    language: string;
    /** Public recaptcha key for a specific domain (deprecated). */
    sitekey: string;
    /** Size of the recaptcha. */
    size: string;
    /** URL for the ReCaptcha JavaScript file. Some countries do not have access to Google. */
    sourceUrl: string;
    /** Position of the input element in the tabbing order. */
    tabindex: number;
    /** Theme of the captcha. */
    theme: PrimeFacesTypes.widget.Captcha.Theme;
}

/**
 * The global variables used by the captcha widget: the init callback and the captcha API (the `executor`).
 */
type CaptchaGlobals = Record<string, (() => void) | PrimeFacesTypes.widget.Captcha.Executor | null | undefined>;

/**
 * __PrimeFaces Captcha Widget__
 *
 * Captcha is a form validation component based on Recaptcha API V2.
 */
export class Captcha<Cfg extends CaptchaCfg = CaptchaCfg> extends PrimeFaces.widget.BaseWidget<Cfg> {

    override init(cfg: PrimeFacesTypes.widget.PartialWidgetCfg<Cfg>): void {
        super.init(cfg);
        this.cfg.language = this.cfg.language || 'en';
        this.cfg.theme = this.cfg.theme || PrimeFaces.env.getThemeContrast(this.jq[0]);

        (window as unknown as CaptchaGlobals)[this.getInitCallbackName()] = () => {
            this.render();
        };

        this.appendScript();
    }

    /**
     * Appends the script to the document body.
     */
    private appendScript(): void {
        let nonce = '';
        if (PrimeFaces.csp.NONCE_VALUE) {
            nonce = 'nonce="' + PrimeFaces.csp.NONCE_VALUE + '"';
        }
        $(document.body).append('<script src="' + this.cfg.sourceUrl + '?onload=' + this.getInitCallbackName() + '&render=explicit&hl='
            + this.cfg.language + '" async defer ' + nonce + '>');
    }

    /**
     * Finds the name to use for the callback function set globally on the window.
     * @returns Name for the global callback.
     */
    private getInitCallbackName(): string {
        return this.cfg.widgetVar + '_initCallback';
    }

    /**
     * Renders the client-side parts of this widget.
     */
    private render(): void {
        const captchaExecutor = (window as unknown as CaptchaGlobals)[this.cfg.executor!] as PrimeFacesTypes.widget.Captcha.Executor;
        captchaExecutor.render(this.jq.get(0)!, {
            'sitekey': this.cfg.sitekey,
            'tabindex': this.cfg.tabindex,
            'theme': this.cfg.theme,
            'callback': this.cfg.callback,
            'expired-callback': this.cfg.expired,
            'size': this.cfg.size
        });

        if (this.cfg.size === 'invisible') {
            captchaExecutor.execute();
        }

        (window as unknown as CaptchaGlobals)[this.getInitCallbackName()] = null;
    }

    override destroy(): void {
        super.destroy();

        (window as unknown as CaptchaGlobals)[this.getInitCallbackName()] = null;
    }
}
