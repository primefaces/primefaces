// ================================================================================
// NOTE: All the documentation and TypeScript declarations are in 0-galleria.d.ts
// ================================================================================

/**
 * Prime Item Widget For Galleria
 */
 (function (factory) {
    if (typeof define === 'function' && define.amd) {
        // AMD. Register as an anonymous module.
        define(['jquery'], factory);
    } else if (typeof module === 'object' && module.exports) {
        // Node/CommonJS
        module.exports = function (root, jQuery) {
            factory(jQuery);
            return jQuery;
        };
    } else {
        // Browser globals
        factory(jQuery);
    }
}(function ($) {

    $.widget("prime.galleriaItem", {

        options: {
            id: null,
            circular: false,
            isVertical: false,
            showCaption: false,
            showIndicators: false,
            showItemNavigators: false,
            changeItemOnIndicatorHover: false,
            autoPlay: false,
            slideShowActive: false,
            activeIndex: 0,
            onActiveIndexChange: null,
            stopSlideShow: null
        },

        _create: function () {
            this.container = this.element;
            this.captionContainer = this.container.nextAll('.ui-galleria-caption-items');
            this.indicatorContainer = this.container.nextAll('.ui-galleria-indicators');
            this.indicators = this.indicatorContainer.children('.ui-galleria-indicator');
            this.items = this.container.children('.ui-galleria-item');

            this._setInitValues();
            this._render();

            this.wrapper = this.container.closest('.ui-galleria-item-wrapper');
            this.containerInWrapper = this.wrapper.children('.ui-galleria-item-container');
            this.navBackwardBtn = this.containerInWrapper.children('.ui-galleria-item-prev');
            this.navBackwardBtn.attr('aria-label', PrimeFaces.getAriaLabel('previous'));
            this.navForwardBtn = this.containerInWrapper.children('.ui-galleria-item-next');
            this.navForwardBtn.attr('aria-label', PrimeFaces.getAriaLabel('next'));
            
            if (this.indicatorContainer.length === 0) {
                this.indicators = this.wrapper.find('> .ui-galleria-indicators > .ui-galleria-indicator');
            }
            this.indicators.each(function(index, item) {
                var indicator = $(item).find('button');
                indicator.attr('aria-label', PrimeFaces.getAriaLabel('pageLabel').replace('{page}', (index + 1)));
            });
                                

            this._bindEvents();
            this.mounted();
        },

        _setInitValues: function () {
            this.prevOptions = this.options;
        },

        next: function () {
            var nextItemIndex = this.options.activeIndex + 1;
    
            this.options.onActiveIndexChange({
                index: this.options.circular && (this.items.length - 1) === this.options.activeIndex ? 0 : nextItemIndex
            });
        },
    
        prev: function () {
            var prevItemIndex = this.options.activeIndex !== 0 ? this.options.activeIndex - 1 : 0;
    
            this.options.onActiveIndexChange({
                index: this.options.circular && this.options.activeIndex === 0 ? this.items.length - 1 : prevItemIndex
            });
        },
    
        stopSlideShow: function () {
            if (this.options.slideShowActive && this.options.stopSlideShow) {
                this.options.stopSlideShow();
            }
        },
    
        navBackward: function (e) {
            this.stopSlideShow();
            this.prev();
    
            e.preventDefault();
        },
    
        navForward: function (e) {
            this.stopSlideShow();
            this.next();
    
            e.preventDefault();
        },
    
        onIndicatorClick: function (e) {
            var index = $(e.currentTarget).index();

            this.stopSlideShow();
            this.options.onActiveIndexChange({
                index: index
            });
        },
    
        onIndicatorMouseEnter: function (e) {
            if (this.options.changeItemOnIndicatorHover) {
                var index = $(e.currentTarget).index();

                this.stopSlideShow();
                this.options.onActiveIndexChange({
                    index: index
                });
            }
        },
    
        onIndicatorKeyDown: function (e) {
            if (e.key === 'Enter') {
                var index = $(e.currentTarget).index();

                this.stopSlideShow();
                this.options.onActiveIndexChange({
                    index: index
                });
            }
        },

        mounted: function () {
            this._updateUI();
        },

        updated: function (prevOptions) {
            if (prevOptions.activeIndex !== this.options.activeIndex) {
                this._updateUI();
            }
        },

        _setOption: function (key, value) {
            this._super(key, value);
            this.updated(this.prevOptions);
        },

        _setOptions: function (options) {
            var $this = this;
            this.prevOptions = Object.assign({}, this.options);
            
            $.each(options, function (key, value) {
                $this._setOption(key, value);
            });

            this.prevOptions = options;
        },

        _updateUI: function () {
            var transform = this.options.isVertical ? 'translate3d(0, ' + (this.options.activeIndex * -100) + '%, 0)' : 'translate3d(' + (this.options.activeIndex * -100) + '%, 0, 0)';
            var transition = 'transform 500ms ease 0s';
            this.container.css({ 'transform': transform, 'transition': transition });
            this.captionContainer.css({ 'transform': transform, 'transition': transition });

            if (this.options.showItemNavigators) {
                var isNavBackwardBtnDisabled = !this.options.circular && this.options.activeIndex === 0;
                var isNavForwardBtnDisabled = !this.options.circular && this.options.activeIndex === (this.items.length - 1);
                var toggleDisabled = function (el, disabled) {
                    if (disabled)
                        el.attr('disabled', 'disabled').addClass('ui-state-disabled');
                    else
                        el.removeAttr('disabled').removeClass('ui-state-disabled');
                };

                toggleDisabled(this.navBackwardBtn, isNavBackwardBtnDisabled);
                toggleDisabled(this.navForwardBtn, isNavForwardBtnDisabled);
            }

            if (this.options.showIndicators) {
                this.indicators.removeClass('ui-state-highlight');

                for (var index = 0; index < this.indicators.length; index++) {
                    if (this.options.activeIndex === index) {
                        var indicator = this.indicators.eq(index);
                        indicator.addClass('ui-state-highlight');
                        break;
                    }
                }
            }
        },

        _bindEvents: function () {
            var $this = this;

            if (this.options.showItemNavigators) {
                this.navBackwardBtn.off('click.galleria-item-nav').on('click.galleria-item-nav', this.navBackward.bind($this));
                this.navForwardBtn.off('click.galleria-item-nav').on('click.galleria-item-nav', this.navForward.bind($this));
            }

            this.indicators.off('click.galleria-indicator mouseenter.galleria-indicator keydown.galleria-indicator')
                .on('click.galleria-indicator', this.onIndicatorClick.bind($this))
                .on('mouseenter.galleria-indicator', this.onIndicatorMouseEnter.bind($this))
                .on('keydown.galleria-indicator', this.onIndicatorKeyDown.bind($this));
        },

        _renderBackwardNavigator: function () {
            if (this.options.showItemNavigators) {
                return (
                    '<button type="button" class="ui-galleria-item-prev ui-galleria-item-nav ui-corner-all ui-galleria-link">' +
                        '<span class="ui-galleria-item-prev-icon ui-icon ui-icon-circle-triangle-w"></span>' +
                    '</button>'
                );
            }

            return '';
        },

        _renderForwardNavigator: function () {
            if (this.options.showItemNavigators) {
                return (
                    '<button type="button" class="ui-galleria-item-next ui-galleria-item-nav ui-corner-all ui-galleria-link">' +
                        '<span class="ui-galleria-item-next-icon ui-icon ui-icon-circle-triangle-e"></span>' +
                    '</button>'
                );
            }

            return '';
        },

        _renderCaption: function () {
            if (this.options.showCaption) {
                this.captionContainer.show();
            }

            return this.captionContainer;
        },

        _renderIndicator: function () {
            return (
                '<li class="ui-galleria-indicator" tabindex="0">' +
                    '<button type="button" tabindex="-1" class="ui-galleria-link"></button>' +
                '</li>'
            );
        },

        _renderIndicators: function () {
            if (this.options.showIndicators) {
                var indicators = '';

                for (var i = 0; i < this.items.length; i++) {
                    indicators += this._renderIndicator();
                }

                return (
                    '<ul class="ui-galleria-indicators">' +
                        indicators +
                    '</ul>'
                );
            }

            return '';
        },

        _render: function () {
            var backwardNavigator = this._renderBackwardNavigator();
            var forwardNavigator = this._renderForwardNavigator();
            var caption = this._renderCaption();
            var indicators = this.indicatorContainer.length ? this.indicatorContainer : this._renderIndicators();

            this.container.wrap(
                '<div class="ui-galleria-item-wrapper">' +
                    '<div class="ui-galleria-item-container">' +
                    '</div>' +
                '</div>'
            );

            this.container.before(backwardNavigator);
            this.container.after(forwardNavigator, caption);
            this.container.parent().after(indicators);
        }
    });

}));

