/**
 * __PrimeFaces Draggable Widget__
 * 
 * Drag&Drop utilities of PrimeFaces consists of two components; Draggable and Droppable.
 * 
 * @typedef PrimeFaces.widget.Draggable.OnStartCallback Callback for when dragging starts. See also {@link
 * DraggableCfg.onStart}.
 * @this {PrimeFaces.widget.Draggable} PrimeFaces.widget.Draggable.OnStartCallback 
 * @param {JQuery.TriggeredEvent} PrimeFaces.widget.Draggable.OnStartCallback.event The drag event that occurred.
 * @param {JQueryUI.DraggableEventUIParams} PrimeFaces.widget.Draggable.OnStartCallback.ui Details about the drag event.
 * 
 * @typedef PrimeFaces.widget.Draggable.OnStopCallback Callback for when dragging ends. See also {@link
 * DraggableCfg.onStop}.
 * @this {PrimeFaces.widget.Draggable} PrimeFaces.widget.Draggable.OnStopCallback 
 * @param {JQuery.TriggeredEvent} PrimeFaces.widget.Draggable.OnStopCallback.event The drag event that occurred.
 * @param {JQueryUI.DraggableEventUIParams} PrimeFaces.widget.Draggable.OnStopCallback.ui Details about the drag event.
 * 
 * @typedef PrimeFaces.widget.Draggable.OnDragCallback Callback for when dragging. See also {@link DraggableCfg.onDrag}.
 * @this {PrimeFaces.widget.Draggable} PrimeFaces.widget.Draggable.OnDragCallback 
 * @param {JQuery.TriggeredEvent} PrimeFaces.widget.Draggable.OnDragCallback.event The drag event that occurred.
 * @param {JQueryUI.DraggableEventUIParams} PrimeFaces.widget.Draggable.OnDragCallback.ui Details about the drag event.
 * 
 * @interface {PrimeFaces.widget.DraggableCfg} cfg The configuration for the {@link  Draggable| Draggable widget}.
 * You can access this configuration via {@link PrimeFaces.widget.BaseWidget.cfg|BaseWidget.cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 * @extends {PrimeFaces.widget.BaseWidgetCfg} cfg
 * @extends {JQueryUI.DraggableOptions} cfg
 * 
 * @prop {JQuery} jqTarget The DOM element of the target that should be made draggable.
 * 
 * @prop {PrimeFaces.widget.Draggable.OnStartCallback} cfg.onStart Callback for when dragging starts.
 * @prop {PrimeFaces.widget.Draggable.OnStopCallback} cfg.onStop Callback for when dragging ends.
 * @prop {PrimeFaces.widget.Draggable.OnDragCallback} cfg.onDrag Callback for when dragging.
 * @prop {string} cfg.target ID of the target of this draggable.
 */
PrimeFaces.widget.Draggable = class Draggable extends PrimeFaces.widget.BaseWidget {

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    init(cfg) {
        super.init(cfg);

        this.jqTarget = $(PrimeFaces.escapeClientId(this.cfg.target));
        this.cfg.cancel = this.cfg.cancel || "input,textarea,button,select,option";

        if (this.cfg.appendTo) {
            this.cfg.appendTo = PrimeFaces.expressions.SearchExpressionFacade.resolveComponentsAsSelector(this.jq, this.cfg.appendTo);
        }

        var $this = this;

        this.cfg.start = function(event, ui) {
            if ($this.cfg.onStart) {
                $this.cfg.onStart.call($this, event, ui);
            }
        };

        this.cfg.stop = function(event, ui) {
            if ($this.cfg.onStop) {
                $this.cfg.onStop.call($this, event, ui);
            }
        };

        this.cfg.drag = function(event, ui) {
            if ($this.cfg.onDrag) {
                $this.cfg.onDrag.call($this, event, ui);
            }
        };

        this.jqTarget.draggable(this.cfg);

        this.addDestroyListener(function() {
            if ($this.jqTarget.length) {
                $this.jqTarget.draggable('destroy');
            }
        });
    }

}

/**
 * __PrimeFaces Droppable Widget__
 * 
 * Drag&Drop utilities of PrimeFaces consists of two components; Draggable and Droppable.
 * 
 * @typedef PrimeFaces.widget.Droppable.OnDropCallback Callback for when an items is dropped. See also {@link
 * DroppableCfg.onDrop}.
 * @this {PrimeFaces.widget.Droppable} PrimeFaces.widget.Droppable.OnDropCallback 
 * @param {JQuery.TriggeredEvent} PrimeFaces.widget.Droppable.OnDropCallback.event The drop event that occurred.
 * @param {JQueryUI.DroppableEventUIParam} PrimeFaces.widget.Droppable.OnDropCallback.ui Details about the drop event.
 * 
 * @prop {JQuery} jqTarget The DOM element of the target that should be made droppable.
 * 
 * @interface {PrimeFaces.widget.DroppableCfg} cfg The configuration for the {@link  Droppable| Droppable widget}.
 * You can access this configuration via {@link PrimeFaces.widget.BaseWidget.cfg|BaseWidget.cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 * @extends {PrimeFaces.widget.BaseWidgetCfg} cfg
 * @extends {JQueryUI.DroppableOptions} cfg
 * 
 * @prop {PrimeFaces.widget.Droppable.OnDropCallback} cfg.onDrop Callback for when an items is dropped.
 * @prop {string} cfg.target ID of the target of this droppable.
 */
PrimeFaces.widget.Droppable = class Droppable extends PrimeFaces.widget.BaseWidget {

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    init(cfg) {
        super.init(cfg);
        this.jqTarget = $(PrimeFaces.escapeClientId(this.cfg.target));

        this.bindDropListener();

        this.jqTarget.droppable(this.cfg);

        var $this = this;
        this.addDestroyListener(function() {
            if ($this.jqTarget.length) {
                $this.jqTarget.droppable('destroy');
            }
        });
    }

    /**
     * Sets up the vent listener for when an item is dropped.
     * @private
     */
    bindDropListener() {
        var $this = this;

        this.cfg.drop = function(event, ui) {
            if ($this.cfg.onDrop) {
                $this.cfg.onDrop.call($this, event, ui);
            }
            if ($this.cfg.behaviors) {
                var dropBehavior = $this.cfg.behaviors['drop'];

                if (dropBehavior) {
                    var ext = {
                        params: [
                            { name: $this.id + '_dragId', value: ui.draggable.attr('id') },
                            { name: $this.id + '_dropId', value: $this.cfg.target }
                        ]
                    };

                    dropBehavior.call($this, ext);
                }
            }
        };
    }

}