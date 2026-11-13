import { ajax } from "../core/core.ajax.js";
import { BaseComponent, type BaseComponentCfg } from "../core/core.component.js";

/**
 * The configuration for the {@link  AjaxExceptionHandler} component.
 * 
 * You can access this configuration via {@link BaseComponent.cfg | cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 */
export interface AjaxExceptionHandlerCfg extends BaseComponentCfg {
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
     * @param type Name of the AJAX error, e.g. the exception type or `httpError`.
     * @param message Message with details regarding the AJAX error.
     * @param timestamp Timestamp of the AJAX error, formatted as `yyyy-MM-dd HH:mm:ss`.
     */
    onexception: (this: AjaxExceptionHandler, type: string, message: string, timestamp: string) => void;
}

/**
 * __PrimeFaces AjaxExceptionHandler Component__
 *
 * A component that keeps tracks of errors during AJAX requests and invokes a
 * JavaScript callback and/or updates Faces components when an error occurs.
 */
export class AjaxExceptionHandler<Cfg extends AjaxExceptionHandlerCfg = AjaxExceptionHandlerCfg> extends BaseComponent<AjaxExceptionHandlerCfg> {
    override init(cfg: PrimeFaces.component.PartialComponentCfg<Cfg>): void {
        super.init(cfg);
    }

    /**
     * If the component handles the passed error.
     * @param errorName The error name.
     * @return If the component handles the passed error.
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
        const timestamp = this.formatTimestamp(new Date());

        if (this.cfg.update) {
            const options: PrimeFaces.ajax.Configuration = {
                source: this.getId(),
                process: this.getId(),
                update: this.cfg.update,
                ignoreAutoUpdate: true,
                global: false,
                // errors of this request must not invoke this handler again, otherwise e.g. a connection error ends up in an endless loop
                skipErrorHandling: true,
                oncomplete: () => {
                    if (this.cfg.onexception) {
                        this.cfg.onexception.call(this, errorName, errorMessage, timestamp);
                    }
                }
            };
            ajax.Request.handle(options);
        }
        else if (this.cfg.onexception) {
            this.cfg.onexception.call(this, errorName, errorMessage, timestamp);
        }
    }

    /**
     * Formats the passed date like the server side `p:ajaxExceptionHandler` (`yyyy-MM-dd HH:mm:ss`).
     * @param date The date to format.
     * @returns The formatted date.
     */
    formatTimestamp(date: Date): string {
        const pad = (value: number): string => String(value).padStart(2, '0');

        return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())} `
            + `${pad(date.getHours())}:${pad(date.getMinutes())}:${pad(date.getSeconds())}`;
    }

    /**
     * Returns the exception type.
     * @returns The exception type.
     */
    getExceptionType(): string {
        return this.cfg.exceptionType ?? "";
    }

    /**
     * Returns if the current component is not registered to a specific exception type.
     * Global exception handlers should be called, if no component is available for a specific exception type.
     * @returns `true` if global, `false` if not.
     */
    isGlobal(): boolean {
        return !this.getExceptionType() ;
    }
};