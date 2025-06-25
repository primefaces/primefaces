import { ajax } from "../core/core.ajax.js";
import { BaseWidget, type BaseWidgetCfg } from "../core/core.widget.js";

/**
 * The configuration for the {@link  AjaxExceptionHandler} widget.
 * 
 * You can access this configuration via {@link BaseWidget.cfg | cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 */
export interface AjaxExceptionHandlerCfg extends BaseWidgetCfg {
    /**
     * The type of the exception.
     */
    exceptionType: string;
    /**
     * The components to update.
     */
    update: string;
    /**
     * Callback to invoke when an AJAX request fails.
     */
    onexception: PrimeType.widget.AjaxExceptionHandler.OnExceptionCallback;
}

/**
 * __PrimeFaces AjaxExceptionHandler Widget__
 *
 * A widget that keeps tracks of errors during AJAX requests and invokes a
 * JavaScript callback and/or updates Faces components when an error occurs.
 */
export class AjaxExceptionHandler<Cfg extends AjaxExceptionHandlerCfg = AjaxExceptionHandlerCfg> extends BaseWidget<AjaxExceptionHandlerCfg> {
    override init(cfg: PrimeType.widget.PartialWidgetCfg<Cfg>): void {
        super.init(cfg);
    }

    /**
     * If the widget handles the passed error.
     * @param errorName The error name.
     * @return If the widget handles the passed error.
     */
    handles(errorName: string): boolean {
        // strip off class prefix if existing
        if (errorName.startsWith('class ')) {
            errorName = errorName.replace('class ', '');
        }

        return this.getExceptionType() === errorName;
    }

    /**
     * Handles the passed error.
     * @param errorName The error name.
     * @param errorMessage The error message.
     */
    handle(errorName: string, errorMessage: string): void {
        if (this.cfg.update) {
            const options: PrimeType.ajax.Configuration = {
                source: this.getId(),
                process: this.getId(),
                update: this.cfg.update,
                ignoreAutoUpdate: true,
                global: false,
                oncomplete: () => {
                    if (this.cfg.onexception) {
                        this.cfg.onexception.call(this, errorName, errorMessage);
                    }
                }
            };
            ajax.Request.handle(options);
        }
        else if (this.cfg.onexception) {
            this.cfg.onexception.call(this, errorName, errorMessage);
        }
    }

    /**
     * Returns the exception type.
     * @returns The exception type.
     */
    getExceptionType(): string {
        return this.cfg.exceptionType ?? "";
    }

    /**
     * Returns if the current widget is not registered to a specific exception type.
     * Global exception handlers should be called, if no widget is available for a specific exception type.
     * @returns `true` if global, `false` if not.
     */
    isGlobal(): boolean {
        return !this.getExceptionType() ;
    }
};