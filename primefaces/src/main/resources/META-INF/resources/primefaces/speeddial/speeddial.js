/**
 * __PrimeFaces SpeedDial Widget__
 *
 * SpeedDial when pressed, a floating action button can display multiple primary actions that can be performed on a page.
 *
 * @interface {PrimeFaces.widget.SpeedDialCfg} cfg The configuration for the {@link  SpeedDial| SpeedDial widget}.
 * You can access this configuration via {@link PrimeFaces.widget.BaseWidget.cfg|BaseWidget.cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 * @extends {PrimeFaces.widget.BaseWidgetCfg} cfg
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

        this.updateItemStyles();

        if (this.cfg.hideOnClickOutside) {
            this.bindDocumentClickListener();
        }

        this.bindEvents();

    },

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

    isVisible: function() {
        return this.visible;//event if check
    },

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
    },

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
    },

    onClick: function() {
        this.container.css('z-index', PrimeFaces.nextZindex());
        this.isVisible() ? this.hide() : this.show();

        this.isItemClicked = true;
    },

    onItemClick: function(e, item) {
        this.hide();

        this.isItemClicked = true;
    },

    bindDocumentClickListener: function() {
        var $this = this;

        if (!this.documentClickListener) {
            this.documentClickListener = function (event) {
                if ($this.isVisible() && $this.isOutsideClicked(event)) {
                    $this.hide();
                }

                $this.isItemClicked = false;
            };
            document.addEventListener('click', this.documentClickListener);
        }
    },

    isOutsideClicked: function(event) {
        var containerEl = this.container.get(0);
        return containerEl && !(containerEl.isSameNode(event.target) || containerEl.contains(event.target) || this.isItemClicked);
    },

    calculateTransitionDelay: function(index) {
        var length = this.itemsCount;
        var visible = this.isVisible();

        return (visible ? index : length - index - 1) * this.cfg.transitionDelay;
    },

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

    getItemStyle: function(index) {
        var transitionDelay = this.calculateTransitionDelay(index);
        var pointStyle = this.calculatePointStyle(index);

        return {
            transitionDelay: transitionDelay + 'ms',
            ...pointStyle
        };
    },

});