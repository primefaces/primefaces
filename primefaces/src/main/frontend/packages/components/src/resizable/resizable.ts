import type { KnownInteractionOptions } from "../dragdrop/dragdrop.js";

/**
 * The configuration for the {@link Resizable} widget.
 */
export interface ResizableCfg extends PrimeFacesTypes.widget.BaseWidgetCfg, KnownInteractionOptions<PrimeFacesTypes.interactions.ResizableOptions> {
    /** Whether AJAX requests are sent when the element is resized. */
    ajaxResize: boolean;
    /** Whether the target is constrained to its parent component, see `parentComponentId`. */
    isContainment: boolean;
    /** Client-side callback to execute during resizing. */
    onResize: PrimeFacesTypes.widget.Resizable.OnResizeCallback;
    /** Client-side callback to execute when resizing begins. */
    onStart: PrimeFacesTypes.widget.Resizable.OnStartCallback;
    /** Client-side callback to execute after resizing end. */
    onStop: PrimeFacesTypes.widget.Resizable.OnStopCallback;
    /** ID of the parent of the resizable element. */
    parentComponentId: string;
    /** ID of the target widget or element to be resized. */
    target: string;
}

/**
 * __PrimeFaces Resizable Widget__
 *
 * Resizable component is used to make another Faces component resizable.
 */
export class Resizable<Cfg extends ResizableCfg = ResizableCfg> extends PrimeFaces.widget.BaseWidget<Cfg> {
    /** The DOM element for the target widget t o be resized. */
    jqTarget: JQuery = $();
    /** Target for the resizable, when using AJAX resize. */
    target?: string | JQuery | HTMLElement;

    override init(cfg: PrimeFacesTypes.widget.PartialWidgetCfg<Cfg>): void {
        super.init(cfg);

        this.jqTarget = $(PrimeFaces.escapeClientId(this.cfg.target!));
        // destroyed with the target
        this.bindLifecycleTo(this.jqTarget);

        this.renderDeferred();
    }

    /**
     * Renders this widget, if the target widget is already visible, or adds a deferred renderer otherwise.
     */
    private renderDeferred(): void {
        if (this.jqTarget.is(':visible')) {
            this._render();
        }
        else {
            const container = this.jqTarget.parent()[0]!.closest('.p-hidden-container');
            if (container) {
                const $container = $(container);
                if ($container.length) {
                    PrimeFaces.addDeferredRender(this.id, $container.attr('id') as string, () => {
                        return this.render();
                    });
                }
            }
        }
    }

    /**
     * Renders the client-side parts of this widget, if this target widget to be resized is already visible.
     * @returns `true` if the target widget is visible, or `false` otherwise.
     */
    private render(): boolean {
        if (this.jqTarget.is(':visible')) {
            this._render();
            return true;
        }

        return false;
    }

    /**
     * Renders the client-side parts of this widget.
     */
    private _render(): void {
        if (this.cfg.ajaxResize) {
            this.cfg.formId = this.jqTarget.parents('form').first().attr('id');
        }

        if (this.cfg.isContainment) {
            this.cfg.containment = PrimeFaces.escapeClientId(this.cfg.parentComponentId!);
        }

        this.cfg.stop = (event: JQuery.TriggeredEvent, ui: PrimeFacesTypes.interactions.ResizableUi) => {
            if (this.cfg.onStop) {
                this.cfg.onStop.call(this, event, ui);
            }

            this.fireAjaxResizeEvent(event, ui);
        };

        this.cfg.start = (event: JQuery.TriggeredEvent, ui: PrimeFacesTypes.interactions.ResizableUi) => {
            if (this.cfg.onStart) {
                this.cfg.onStart.call(this, event, ui);
            }
        };

        this.cfg.resize = (event: JQuery.TriggeredEvent, ui: PrimeFacesTypes.interactions.ResizableUi) => {
            if (this.cfg.onResize) {
                this.cfg.onResize.call(this, event, ui);
            }
        };

        PrimeFaces.interactions.resizable(this.jqTarget, this.cfg);

        this.addDestroyListener(() => {
            if (this.jqTarget.length) {
                PrimeFaces.interactions.destroy(this.jqTarget, 'resizable');
            }
        });

        this.removeScriptElement(this.id);
    }

    /**
     * Triggers the behavior for when the component was resized.
     * @param event Event that triggered the resize.
     * @param ui Data of the resize event.
     */
    private fireAjaxResizeEvent(event: JQuery.TriggeredEvent, ui: PrimeFacesTypes.interactions.ResizableUi): void {
        if (this.hasBehavior('resize')) {
            const ext: Partial<PrimeFacesTypes.ajax.ConfigurationExtender> = {
                params: [
                    { name: this.getId() + '_width', value: parseInt(String(ui.helper.width())) },
                    { name: this.getId() + '_height', value: parseInt(String(ui.helper.height())) }
                ]
            };

            this.callBehavior('resize', ext);
        }
    }
}
