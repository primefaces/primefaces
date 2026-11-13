/**
 * The options of an interaction without the index signature of the interaction options (which would hide all other
 * properties of a component configuration that extends them).
 * @typeParam T The options of an interaction, e.g. `PrimeFacesTypes.interactions.DraggableOptions`.
 */
export type KnownInteractionOptions<T> = { [K in keyof T as string extends K ? never : K]: T[K] };

/**
 * The configuration for the {@link Draggable} component.
 */
export interface DraggableCfg extends PrimeFacesTypes.component.BaseComponentCfg, KnownInteractionOptions<PrimeFacesTypes.interactions.DraggableOptions> {
    /** Callback for when dragging starts. */
    onStart: PrimeFacesTypes.component.Draggable.OnStartCallback;
    /** Callback for when dragging ends. */
    onStop: PrimeFacesTypes.component.Draggable.OnStopCallback;
    /** Callback for when dragging. */
    onDrag: PrimeFacesTypes.component.Draggable.OnDragCallback;
    /** ID of the target of this draggable. */
    target: string;
}

/**
 * __PrimeFaces Draggable Component__
 *
 * Drag&Drop utilities of PrimeFaces consists of two components; Draggable and Droppable.
 */
export class Draggable<Cfg extends DraggableCfg = DraggableCfg> extends PrimeFaces.component.BaseComponent<Cfg> {
    /** The DOM element of the target that should be made draggable. */
    jqTarget: JQuery = $();

    override init(cfg: PrimeFacesTypes.component.PartialComponentCfg<Cfg>): void {
        super.init(cfg);

        this.jqTarget = $(PrimeFaces.escapeClientId(this.cfg.target!));
        // destroyed with the target
        this.bindLifecycleTo(this.jqTarget);
        this.cfg.cancel = this.cfg.cancel || "input,textarea,button,select,option";

        if (this.cfg.appendTo) {
            this.cfg.appendTo = PrimeFaces.expressions.SearchExpressionFacade.resolveComponentsAsSelector(this.jq, this.cfg.appendTo as string | HTMLElement | JQuery);
        }

        this.cfg.start = (event: JQuery.TriggeredEvent, ui: PrimeFacesTypes.interactions.DraggableUi) => {
            if (this.cfg.onStart) {
                this.cfg.onStart.call(this, event, ui);
            }
        };

        this.cfg.stop = (event: JQuery.TriggeredEvent, ui: PrimeFacesTypes.interactions.DraggableUi) => {
            if (this.cfg.onStop) {
                this.cfg.onStop.call(this, event, ui);
            }
        };

        this.cfg.drag = (event: JQuery.TriggeredEvent, ui: PrimeFacesTypes.interactions.DraggableUi) => {
            if (this.cfg.onDrag) {
                this.cfg.onDrag.call(this, event, ui);
            }
        };

        PrimeFaces.interactions.draggable(this.jqTarget, this.cfg);

        this.addDestroyListener(() => {
            if (this.jqTarget.length) {
                PrimeFaces.interactions.destroy(this.jqTarget, 'draggable');
            }
        });
    }
}

/**
 * The configuration for the {@link Droppable} component.
 */
export interface DroppableCfg extends PrimeFacesTypes.component.BaseComponentCfg, KnownInteractionOptions<PrimeFacesTypes.interactions.DroppableOptions> {
    /** Callback for when an items is dropped. */
    onDrop: PrimeFacesTypes.component.Droppable.OnDropCallback;
    /** ID of the target of this droppable. */
    target: string;
}

/**
 * __PrimeFaces Droppable Component__
 *
 * Drag&Drop utilities of PrimeFaces consists of two components; Draggable and Droppable.
 */
export class Droppable<Cfg extends DroppableCfg = DroppableCfg> extends PrimeFaces.component.BaseComponent<Cfg> {
    /** The DOM element of the target that should be made droppable. */
    jqTarget: JQuery = $();

    override init(cfg: PrimeFacesTypes.component.PartialComponentCfg<Cfg>): void {
        super.init(cfg);
        this.jqTarget = $(PrimeFaces.escapeClientId(this.cfg.target!));
        // destroyed with the target
        this.bindLifecycleTo(this.jqTarget);

        this.bindDropListener();

        PrimeFaces.interactions.droppable(this.jqTarget, this.cfg);

        this.addDestroyListener(() => {
            if (this.jqTarget.length) {
                PrimeFaces.interactions.destroy(this.jqTarget, 'droppable');
            }
        });
    }

    /**
     * Sets up the vent listener for when an item is dropped.
     */
    private bindDropListener(): void {
        this.cfg.drop = (event: JQuery.TriggeredEvent, ui: PrimeFacesTypes.interactions.DroppableUi) => {
            if (this.cfg.onDrop) {
                this.cfg.onDrop.call(this, event, ui);
            }
            if (this.cfg.behaviors) {
                const dropBehavior = this.cfg.behaviors['drop'];

                if (dropBehavior) {
                    const ext: Partial<PrimeFacesTypes.ajax.ConfigurationExtender> = {
                        params: [
                            { name: this.getId() + '_dragId', value: ui.draggable.attr('id') },
                            { name: this.getId() + '_dropId', value: this.cfg.target }
                        ]
                    };

                    dropBehavior.call(this, ext);
                }
            }
        };
    }
}
