/**
 * __PrimeFaces SpeedDial Widget__
 *
 * SpeedDial is a component that consists of many actions and a floating action button.
 * When pressed, a floating action button can display multiple primary actions that can be performed on a page.
 *
 * @typedef {"up" | "down" | "left" | "right" | "up-left" | "up-right" | "down-left" | "down-right"} PrimeFaces.widget.SpeedDial.OpeningDirection
 * The opening animation direction for speed dial actions. `up`, `down`, `left` and `right` is applicable when
 * {@link PrimeFaces.widget.SpeedDialCfg.type} is set to `semi-circle`, the others are applicable when type
 * is set to `quarter-circle`.
 *
 * @typedef {"linear" | "circle" | "semi-circle" | "quarter-circle"} PrimeFaces.widget.SpeedDial.OpeningType The
 * opening animation type for speed dial actions.
 *
 * @typedef PrimeFaces.widget.SpeedDial.OnClickCallback Callback invoked when the speed dial was clicked.
 * @this {PrimeFaces.widget.SpeedDial} PrimeFaces.widget.SpeedDial.OnClickCallback
 * @param {JQuery.TriggeredEvent} PrimeFaces.widget.SpeedDial.OnClickCallback.event The click event that occurred.
 *
 * @typedef PrimeFaces.widget.SpeedDial.OnHideCallback Callback invoked when the speed dial was closed. This is called
 * after the visible change callback.
 * @this {PrimeFaces.widget.SpeedDial} PrimeFaces.widget.SpeedDial.OnHideCallback
 *
 * @typedef PrimeFaces.widget.SpeedDial.OnShowCallback Callback invoked when the speed dial was opened. This is called
 * after the visible change callback.
 * @this {PrimeFaces.widget.SpeedDial} PrimeFaces.widget.SpeedDial.OnShowCallback
 *
 * @typedef PrimeFaces.widget.SpeedDial.OnVisibleChangeCallback Callback invoked when the visibility of the speed dial
 * changed. This is called before the hide and show callbacks.
 * @this {PrimeFaces.widget.SpeedDial} PrimeFaces.widget.SpeedDial.OnVisibleChangeCallback
 * @param {PrimeFaces.widget.SpeedDial} PrimeFaces.widget.SpeedDial.OnVisibleChangeCallback.visible Whether the speed
 * dial is now visible or hidden.
 *
 * @typedef PrimeFaces.widget.SpeedDial.OnDocumentClickCallback Callback invoked when the document was clicked. This is
 * used to detect whether the user clicked outside the speed dial so that it can be closed.
 * @param {Event} PrimeFaces.widget.SpeedDial.OnDocumentClickCallback.event Click event that occurred.
 *
 * @prop {JQuery} badge The DOM element for the badge of the floating action button of the speed dial.
 * @prop {JQuery} button The DOM element for the floating action button of the speed dial.
 * @prop {JQuery} buttonIcon The DOM element for the icon of the floating action button of the speed dial.
 * @prop {JQuery} container The DOM element for the container of the speed dial that contains item container and button.
 * @prop {PrimeFaces.widget.SpeedDial.OnDocumentClickCallback} [documentClickListener] Callback invoked when the
 * document was clicked. This is used to detect whether the user clicked outside the speed dial so that it can be
 * closed.
 * @prop {boolean} [isItemClicked] Whether the speed dial was recently clicked. Used to determine whether the user
 * clicked outside the speed dial after clicking inside of it. `undefined` when no clicks where performed yet.
 * @prop {JQuery} itemContainer The DOM element for the item container of the speed dial.
 * @prop {JQuery} items The DOM elements for the speed dial items.
 * @prop {number} itemsCount The number of action items.
 * @prop {JQuery} mask The DOM element for the mask of the speed dial.
 * @prop {boolean} visible Whether overlay is visible or not.
 *
 * @interface {PrimeFaces.widget.SpeedDialCfg} cfg The configuration for the {@link  SpeedDial|SpeedDial widget}. You
 * can access this configuration via {@link PrimeFaces.widget.BaseWidget.cfg|BaseWidget.cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 * @extends {PrimeFaces.widget.BaseWidgetCfg} cfg
 *
 * @prop {PrimeFaces.widget.SpeedDial.OpeningDirection} cfg.direction Specifies the opening animation direction of
 * actions.
 * @prop {string} cfg.hideIcon The icon class of the hide button element.
 * @prop {boolean} cfg.hideOnClickOutside Whether the actions close when clicked outside.
 * @prop {boolean} cfg.mask Whether to show a mask element behind the speed dial.
 * @prop {PrimeFaces.widget.SpeedDial.OnClickCallback} cfg.onClick The click event that occurred.
 * @prop {PrimeFaces.widget.SpeedDial.OnHideCallback} cfg.onHide Callback invoked when the speed dial was closed. This
 * is called after the visible change callback.
 * @prop {PrimeFaces.widget.SpeedDial.OnShowCallback} cfg.onShow Callback invoked when the speed dial was opened. This
 * is called after the visible change callback.
 * @prop {PrimeFaces.widget.SpeedDial.OnVisibleChangeCallback} cfg.onVisibleChange Callback invoked when the visibility
 * of the speed dial changed. This is called before the hide and show callbacks.
 * @prop {number} cfg.radius Radius for when {@link type} is set to one of the circle types.
 * @prop {boolean} cfg.keepOpen Whether the menu should be kept open on clicking menu items.
 * @prop {number} cfg.transitionDelay Transition delay step in milliseconds for each action item.
 * @prop {PrimeFaces.widget.SpeedDial.OpeningType} cfg.type Specifies the opening animation type of actions.
 * @prop {boolean} cfg.visible Specifies the visibility of the overlay.
 */
PrimeFaces.widget.SpeedDial = PrimeFaces.widget.DeferredWidget.extend({

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    init: function(cfg) {
        this._super(cfg);
        this.mask = this.jq.children('.ui-speeddial-mask');
        this.container = this.jq.children('.ui-speeddial');
        this.badge = this.container.children('.ui-overlay-badge');
        var buttonContainer = this.badge.length === 0 ? this.container : this.badge;
        this.button = buttonContainer.children('.ui-speeddial-button');
        this.buttonIcon = this.button.children('.ui-icon');
        this.itemContainer = this.container.children('.ui-speeddial-list');
        this.items = this.itemContainer.children('.ui-speeddial-item');
        this.itemsCount = this.items.length;

        this.cfg.visible = this.cfg.visible || false;
        this.cfg.direction = this.cfg.direction || 'up';
        this.cfg.transitionDelay = this.cfg.transitionDelay || 30;
        this.cfg.type = this.cfg.type || 'linear';
        this.cfg.radius = this.cfg.radius || 0;
        this.cfg.mask = this.cfg.mask || false;
        this.cfg.hideOnClickOutside = this.cfg.hideOnClickOutside || true;
        this.cfg.keepOpen = this.cfg.keepOpen || false;

        this.visible = this.cfg.visible;

        this.renderDeferred();
    },

    /**
     * @include
     * @override
     * @protected
     * @inheritdoc
     */
    _render: function() {
        this.createItemContainerStyle();
        this.updateItemStyles();

        if (this.cfg.hideOnClickOutside) {
            this.bindDocumentClickListener();
        }

        this.bindEvents();

    },

    /**
     * Creates responsive style of the item container.
     * @private
     */
    createItemContainerStyle: function () {
        if (this.cfg.type !== 'linear') {
            var button = this.button.get(0);
            var firstItem = this.items.get(0);

            if (button && firstItem) {
                var wDiff = Math.abs(button.offsetWidth - firstItem.offsetWidth);
                var hDiff = Math.abs(button.offsetHeight - firstItem.offsetHeight);
                this.itemContainer.get(0).style.setProperty('--item-diff-x', wDiff / 2 + 'px');
                this.itemContainer.get(0).style.setProperty('--item-diff-y', hDiff / 2 + 'px');
            }
        }
    },

    /**
     * Updates styles of the action items.
     * @private
     */
    updateItemStyles: function () {
        var $this = this;

        for (var i = 0; i < this.itemsCount; i++) {
            var itemStyle = $this.getItemStyle(i);
            $this.items.eq(i).css(itemStyle);
        }
    },

    /**
     * Sets up all event listeners required by this widget.
     * @private
     */
    bindEvents: function () {
        var $this = this;

        this.button.on('click.speeddial', function(e) {
            $this.onClick(e);
        });

        this.items.on('click.speeddial', function() {
            $this.onItemClick();
        });
    },

    /**
     * Shows item container of the speeddial.
     */
    show: function() {
        if (this.mask) {
            this.mask.addClass('ui-speeddial-mask-visible');
        }

        this.container.addClass('ui-speeddial-opened');
        this.visible = true;
        this.updateItemStyles();

        if(this.cfg.onVisibleChange) {
            this.cfg.onVisibleChange.call(this, true);
        }

        if(this.cfg.onShow) {
            this.cfg.onShow.call(this);
        }
    },

    /**
     * Hides item container of the speed dial.
     */
    hide: function() {
        if (this.mask) {
            this.mask.removeClass('ui-speeddial-mask-visible');
        }

        this.container.removeClass('ui-speeddial-opened');
        this.visible = false;
        this.updateItemStyles();

        if(this.cfg.onVisibleChange) {
            this.cfg.onVisibleChange.call(this, false);
        }

        if(this.cfg.onHide) {
            this.cfg.onHide.call(this);
        }
    },

    /**
     * Changes visibility of the item container.
     * @private
     * @param {JQuery.TriggeredEvent} event Event that occurred.
     */
    onClick: function(event) {
        this.visible ? this.hide() : this.show();

        if(this.cfg.onClick) {
            this.cfg.onClick.call(this, event);
        }

        this.isItemClicked = true;
    },

    /**
     * Hides item container of the speed dial.
     * @private
     */
    onItemClick: function() {
        if (!this.cfg.keepOpen) {
            this.hide();
        }

        this.isItemClicked = true;
    },

    /**
     * Adds the outside click event listener to the document.
     * @private
     */
    bindDocumentClickListener: function() {
        var $this = this;

        if (!this.documentClickListener) {
            this.documentClickListener = function (event) {
                if ($this.visible && $this.isOutsideClicked(event)) {
                    $this.hide();
                }

                $this.isItemClicked = false;
            };
            $(document).on('click', this.documentClickListener);
        }
    },

    /**
     * Returns whether outside is clicked or not.
     * @private
     * @param {JQuery.TriggeredEvent} event Event that occurred.
     * @return {boolean} Whether the outside was clicked.
     */
    isOutsideClicked: function(event) {
        var containerEl = this.container.get(0);
        return containerEl && !(containerEl.isSameNode(event.target) || containerEl.contains(event.target) || this.isItemClicked);
    },

    /**
     * Calculates transition delay of the action items according to items' index.
     * @private
     * @param {number} index Index of the action item element.
     * @return {number} Delay in milliseconds for the transition.
     */
    calculateTransitionDelay: function(index) {
        var length = this.itemsCount;
        var visible = this.visible;

        return (visible ? index : length - index - 1) * this.cfg.transitionDelay;
    },

    /**
     * Calculates point styles of the action items according to items' index.
     * @private
     * @param {number} index Index of the action item element
     * @return {JQuery.PlainObject<string | number>} Point styles of the action item.
     */
    calculatePointStyle: function(index) {
        var type = this.cfg.type;

        if (type !== 'linear') {
            var length = this.itemsCount;
            var radius = this.cfg.radius || (length * 20);

            if (type === 'circle') {
                var step = 2 * Math.PI / length;

                return {
                    left: 'calc(' + (radius * Math.cos(step * index)) + 'px + var(--item-diff-x, 0px))',
                    top: 'calc(' + (radius * Math.sin(step * index)) + 'px + var(--item-diff-y, 0px))',
                }
            }
            else if (type === 'semi-circle') {
                var direction = this.cfg.direction;
                var step = Math.PI / (length - 1);
                var x = 'calc(' + (radius * Math.cos(step * index)) + 'px + var(--item-diff-x, 0px))';
                var y = 'calc(' + (radius * Math.sin(step * index)) + 'px + var(--item-diff-y, 0px))';
                if (direction === 'up') {
                    return { left: x, bottom: y };
                }
                else if (direction === 'down') {
                    return { left: x, top: y };
                }
                else if (direction === 'left') {
                    return { right: y, top: x };
                }
                else if (direction === 'right') {
                    return { left: y, top: x };
                }
            }
            else if (type === 'quarter-circle') {
                var direction = this.cfg.direction;
                var step = Math.PI / (2 * (length - 1));
                var x = 'calc(' + (radius * Math.cos(step * index)) + 'px + var(--item-diff-x, 0px))';
                var y = 'calc(' + (radius * Math.sin(step * index)) + 'px + var(--item-diff-y, 0px))';
                if (direction === 'up-left') {
                    return { right: x, bottom: y };
                }
                else if (direction === 'up-right') {
                    return { left: x, bottom: y };
                }
                else if (direction === 'down-left') {
                    return { right: y, top: x };
                }
                else if (direction === 'down-right') {
                    return { left: y, top: x };
                }
            }
        }

        return {};
    },

    /**
     * Retrieves styles of the item according to items' index.
     * @private
     * @param {number} index Index of the action item element.
     * @return {JQuery.PlainObject<string | number>} Styles of the action item
     */
    getItemStyle: function(index) {
        var transitionDelay = this.calculateTransitionDelay(index);
        var pointStyle = this.calculatePointStyle(index);
        pointStyle["transitionDelay"] = transitionDelay + 'ms';

        return pointStyle;
    }

});