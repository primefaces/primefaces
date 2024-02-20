// ================================================================================
// NOTE: All the documentation and TypeScript declarations are in 0-galleria.d.ts
// ================================================================================

/**
 * Prime Galleria Widget
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

    $.widget("prime.galleria", {

        options: {
            id: null,
            selector: null,
            value: null,
            style: null,
            styleClass: null,
            activeIndex: 0,
            fullScreen: false,
            closeIcon: 'pi pi-times',
            numVisible: 3,
            responsiveOptions: null,
            showThumbnails: true,
            showIndicators: false,
            showIndicatorsOnItem: false,
            showCaption: false,
            showItemNavigators: false,
            showThumbnailNavigators: true,
            showItemNavigatorsOnHover: false,
            changeItemOnIndicatorHover: false,
            circular: false,
            autoPlay: false,
            transitionInterval: 4000,
            thumbnailsPosition: "bottom",
            verticalViewPortHeight: "450px",
            indicatorsPosition: "bottom",
            onItemChange: null,
            onShow: null,
            onHide: null
        },

        _create: function () {
            this.container = this.element;
            this.content = this.container.children('.ui-galleria-content');
            this.itemsContainer = this.content.children('.ui-galleria-items');
            this.captionContainer = this.content.children('.ui-galleria-caption-items');
            this.thumbnailContainer = this.content.children('.ui-galleria-thumbnail-items');
            this.items = this.itemsContainer.children('.ui-galleria-item');
            
            if (this.options.fullScreen) {
                this.container.css('display', 'none');
            }

            if (this.items.length > 0) {
                this._setInitValues();
                this._render();

                this.mask = this.container.parent('.ui-galleria-mask');
                this.itemWidget = this.itemsContainer.data('prime-galleriaItem');
                this.thumbnailWidget = this.thumbnailContainer.data('prime-galleriaThumbnail');
                
                this.transition = PrimeFaces.utils.registerCSSTransition(this.container, 'ui-galleria');

                this._bindEvents();
                this.mounted();
            }
        },

        _setInitValues: function () {
            this.state = {
                visible: false,
                slideShowActive: false,
                activeIndex: this.options.activeIndex
            };

            this.prevState = Object.assign({}, this.state);
            this.prevOptions = this.options;
        },

        setState: function (newState, callback) {
            var $this = this;
            var isUpdated = false;
            
            Object.keys(newState).forEach(function (key) {
                if ($this.state[key] !== newState[key]) {
                    $this.prevState[key] = $this.state[key];
                    $this.state[key] = newState[key];
                    
                    isUpdated = true;
                }
            });

            if (isUpdated) {
                this.updated();
                callback && callback();
            }
        },

        onActiveIndexChange: function (event) {
            var $this = this;
            
            this.setState({
                activeIndex: event.index
            }, function() {
                if ($this.options.onItemChange) {
                    $this.options.onItemChange(event);
                }
            });
        },
    
        show: function () {
            var $this = this;
            
            this.setState({ visible: true }, function () {
                if ($this.transition) {
                    $this.transition.show({
                        onEnter: function () {
                            $(document.body).addClass('ui-overflow-hidden');
                        },
                        onEntering: function () {
                            $this.mask.addClass('ui-widget-overlay').css('z-index', PrimeFaces.nextZindex());
                        },
                        onEntered: function () {
                            if ($this.options.onShow) {
                                $this.options.onShow();
                            }
                        }
                    });
                }
            });
        },
    
        hide: function () {
            var $this = this;
            
            this.setState({ visible: false }, function () {
                if ($this.transition) {
                    $this.transition.hide({
                        onExit: function () {
                            $(document.body).removeClass('ui-overflow-hidden');
                            $this.mask.addClass('ui-galleria-mask-leave');
                        },
                        onExited: function () {
                            $this.mask.removeClass('ui-galleria-mask-leave ui-widget-overlay').css('z-index', '');
                            
                            if ($this.options.onHide) {
                                $this.options.onHide();
                            }
                        }
                    });
                }
            });
        },
        
        next: function () {
            var nextActiveIndex = (this.state.activeIndex === this.items.length - 1) ? 
                (this.options.circular ? 0 : this.state.activeIndex) : this.state.activeIndex + 1;
                
            this.setState({
                activeIndex: nextActiveIndex
            });
        },
        
        prev: function () {
            var prevActiveIndex = (this.state.activeIndex === 0) ? 
                (this.options.circular ? this.items.length - 1 : this.state.activeIndex) : this.state.activeIndex - 1;
                
            this.setState({
                activeIndex: prevActiveIndex
            });
        },
    
        isAutoPlayActive: function () {
            return this.state.slideShowActive;
        },
    
        startSlideShow: function () {
            var $this = this;
            this.interval = setInterval(function () {
                var activeIndex = ($this.options.circular && ($this.items.length - 1) === $this.state.activeIndex) ? 0 : ($this.state.activeIndex + 1);
                $this.onActiveIndexChange({ index: activeIndex });
            }, this.options.transitionInterval);
    
            this.setState({ slideShowActive: true });
        },
    
        stopSlideShow: function () {
            if (this.interval) {
                clearInterval(this.interval);
            }
    
            this.setState({ slideShowActive: false });
        },

        getPositionStyleClass: function (preClassName, position) {
            var positions = ['top', 'left', 'bottom', 'right'];
            var pos = positions.filter(function(item) { return item === position; })[0];
    
            return pos ? preClassName + '-' + pos : '';
        },

        isVertical: function() {
            return this.options.thumbnailsPosition === 'left' || this.options.thumbnailsPosition === 'right';
        },

        mounted: function () {
            if (this.isVertical()) {
                this.content.css('height', this.options.verticalViewPortHeight);
            }
            
            if (this.options.autoPlay) {
                this.startSlideShow();
            }
        },

        updated: function () {
            this.itemWidget.option(this.state);

            if (this.thumbnailWidget) {
                this.thumbnailWidget.option(this.state);
            }

            if (this.state.visible) 
                this.mask.addClass('ui-galleria-visible');
            else
                this.mask.removeClass('ui-galleria-visible');
        },

        _destroy: function () {
            $(document.body).removeClass('ui-overflow-hidden');
            
            if (this.state && this.state.slideShowActive) {
                this.stopSlideShow();
            }
        },

        _bindEvents: function () {
            var $this = this;

            this.container.children('.ui-galleria-close').off('click.galleria').on('click.galleria', this.hide.bind($this));
        },

        _renderCloseButton: function () {
            if (this.options.fullScreen) {
                return (
                    '<button type="button" class="ui-galleria-close ui-corner-all ui-galleria-link" aria-label="'+PrimeFaces.getAriaLabel('close')+'">' +
                        '<span class="ui-button-icon-left ui-galleria-close-icon ' + this.options.closeIcon + '"></span>' +
                    '</button>'
                );
            }
        },

        _renderItems: function() {
            this.itemsContainer.galleriaItem({ 
                circular: this.options.circular,
                isVertical: this.isVertical(),
                showCaption: this.options.showCaption,
                showIndicators: this.options.showIndicators,
                showItemNavigators: this.options.showItemNavigators,
                changeItemOnIndicatorHover: this.options.changeItemOnIndicatorHover,
                autoPlay: this.options.autoPlay,
                slideShowActive: this.state.slideShowActive,
                activeIndex: this.state.activeIndex,
                onActiveIndexChange: this.onActiveIndexChange.bind(this),
                stopSlideShow: this.stopSlideShow.bind(this)
            });
        },

        _renderThumbnails: function () {
            this.thumbnailContainer.galleriaThumbnail({
                id: this.options.id,
                selector: this.options.selector,
                showThumbnails: this.options.showThumbnails,
                numVisible: this.options.numVisible,
                responsiveOptions: this.options.responsiveOptions,
                circular: this.options.circular,
                isVertical: this.isVertical(),
                showThumbnailNavigators: this.options.showThumbnailNavigators,
                slideShowActive: this.state.slideShowActive,
                activeIndex: this.state.activeIndex,
                onActiveIndexChange: this.onActiveIndexChange.bind(this),
                stopSlideShow: this.stopSlideShow.bind(this)
            });
        },

        _renderElement: function() {
            var thumbnailsPosStyleClass = this.options.showThumbnails && this.getPositionStyleClass('ui-galleria-thumbnails', this.options.thumbnailsPosition);
            var indicatorPosStyleClass = this.options.showIndicators && this.getPositionStyleClass('ui-galleria-indicators', this.options.indicatorsPosition);
            var galleriaStyleClass = PrimeFaces.utils.styleClass(this.options.styleClass, {
                'ui-galleria-fullscreen': this.options.fullScreen,
                'ui-galleria-indicator-onitem': this.options.showIndicatorsOnItem,
                'ui-galleria-item-nav-onhover': this.options.showItemNavigatorsOnHover && !this.options.fullScreen
            }, thumbnailsPosStyleClass, indicatorPosStyleClass);

            this.container.addClass(galleriaStyleClass);
            if (this.options.style) this.container.attr('style', this.options.style);

            this.closeButton = this._renderCloseButton();

            this.container.prepend(this.closeButton);
            this._renderThumbnails();
            this._renderItems();

            return this.container;
        },

        _render: function () {
            var element = this._renderElement();

            if (this.options.fullScreen) {
                element.unwrap('.ui-galleria-mask').wrap('<div class="ui-galleria-mask"></div>');
            }
        }
    });

}));