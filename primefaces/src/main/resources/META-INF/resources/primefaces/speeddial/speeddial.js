/**
 * __PrimeFaces SpeedDial Widget__
 *
 * SpeedDial is a component that consists of many actions and a floating action button.
 * When pressed, a floating action button can display multiple primary actions that can be performed on a page.
 *
 * @prop {JQuery} mask The DOM element for the mask of the speeddial.
 * @prop {JQuery} container The DOM element for the container of the speeddial that contains item container and button.
 * @prop {JQuery} button The DOM element for the floating action button of the speeddial.
 * @prop {JQuery} buttonIcon The DOM element for the icon of the floating action button of the speeddial.
 * @prop {JQuery} itemContainer The DOM element for the item container of the speeddial.
 * @prop {JQuery} items The DOM elements for the speeddial items.
 * @prop {number} itemsCount The number of action items.
 * @prop {boolean} visible Whether overlay is visible or not.
 *
 * @interface {PrimeFaces.widget.SpeedDialCfg} cfg The configuration for the {@link  SpeedDial| SpeedDial widget}.
 * You can access this configuration via {@link PrimeFaces.widget.BaseWidget.cfg|BaseWidget.cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 * @extends {PrimeFaces.widget.BaseWidgetCfg} cfg
 *
 * @prop {boolean} cfg.visible Specifies the visibility of the overlay.
 * @prop {string} cfg.direction Specifies the opening direction of actions.
 * @prop {number} cfg.transitionDelay Transition delay step for each action item.
 * @prop {string} cfg.type Specifies the opening type of actions.
 * @prop {number} cfg.radius Radius for *circle types.
 * @prop {boolean} cfg.mask Whether to show a mask element behind the speeddial.
 * @prop {boolean} cfg.hideOnClickOutside Whether the actions close when clicked outside.
 * @prop {boolean} cfg.rotateAnimation Sets rotate for show icon.
 * @prop {string} cfg.showIcon Show icon class of the button element.
 *
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
        this.button = this.container.children('.ui-speeddial-button');
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
        this.cfg.rotateAnimation = this.cfg.rotateAnimation || true;
        this.cfg.showIcon = this.cfg.showIcon || 'pi pi-plus';

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

        this.button.on('click', function(e) {
            $this.onClick(e);
        });

        this.items.on('click', function(e) {
            $this.onItemClick(e);
        });
    },

    /**
     * Shows item container of the speeddial and changes or rotates floating action button icon.
     */
    show: function() {
        if (this.cfg.hideIcon) {
            this.buttonIcon.removeClass(this.cfg.showIcon);
            this.buttonIcon.addClass(this.cfg.hideIcon);
        }

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
     * Hides item container of the speeddial and changes or rotates floating action button icon.
     */
    hide: function() {
        if (this.cfg.hideIcon) {
            this.buttonIcon.removeClass(this.cfg.hideIcon);
            this.buttonIcon.addClass(this.cfg.showIcon);
        }

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
     * @param {Event} event Event that occurred.
     */
    onClick: function(event) {
        this.visible ? this.hide() : this.show();

        if(this.cfg.onClick) {
            this.cfg.onClick.call(this, event);
        }

        this.isItemClicked = true;
    },

    /**
     * Hides item container of the speeddial.
     * @private
     */
    onItemClick: function() {
        this.hide();

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
            document.addEventListener('click', this.documentClickListener);
        }
    },

    /**
     * Returns whether outside is clicked or not.
     * @private
     * @param {Event} event Event that occurred.
     * @return {boolean} outside is clicked.
     */
    isOutsideClicked: function(event) {
        var containerEl = this.container.get(0);
        return containerEl && !(containerEl.isSameNode(event.target) || containerEl.contains(event.target) || this.isItemClicked);
    },

    /**
     * Calculates transition delay of the action items according to items' index.
     * @private
     * @param {number} index index of the action item element
     * @return {string} point styles of the action item
     */
    calculateTransitionDelay: function(index) {
        var length = this.itemsCount;
        var visible = this.visible;

        return (visible ? index : length - index - 1) * this.cfg.transitionDelay;
    },

    /**
     * Calculates point styles of the action items according to items' index.
     * @private
     * @param {number} index index of the action item element
     * @return {string} point styles of the action item
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
     * @param {number} index index of the action item element
     * @return {string} styles of the action item
     */
    getItemStyle: function(index) {
        var transitionDelay = this.calculateTransitionDelay(index);
        var pointStyle = this.calculatePointStyle(index);
        pointStyle["transitionDelay"] = transitionDelay + 'ms';

        return pointStyle;
    },

});