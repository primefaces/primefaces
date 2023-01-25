// ================================================================================
// NOTE: All the documentation and TypeScript declarations are in 0-galleria.d.ts
// ================================================================================

/**
 * Prime Thumbnail Widget For Galleria
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

    $.widget("prime.galleriaThumbnail", {

        options: {
            id: null,
            selector: null,
            showThumbnails: true,
            numVisible: 3,
            responsiveOptions: null,
            circular: false,
            isVertical: false,
            showThumbnailNavigators: true,
            slideShowActive: false,
            activeIndex: 0,
            onActiveIndexChange: null,
            stopSlideShow: null
        },

        _create: function () {
            this.container = this.element;

            if (this.options.showThumbnails) {
                this.thumbnailItems = this.container.children('.ui-galleria-thumbnail-item');

                this._setInitValues();
                this._render();

                this.wrapper = this.container.closest('.ui-galleria-thumbnail-wrapper');
                this.containerInWrapper = this.wrapper.children('.ui-galleria-thumbnail-container');
                this.navBackwardBtn = this.containerInWrapper.children('.ui-galleria-thumbnail-prev');
                this.navForwardBtn = this.containerInWrapper.children('.ui-galleria-thumbnail-next');

                this._bindEvents();
                this.mounted();
            }
            else {
                this.container.hide();
            }
        },

        _setInitValues: function () {
            this.state = {
                numVisible: this.options.numVisible,
                totalShiftedItems: 0
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
                this.updated(this.options, this.prevState);
                callback && callback();
            }
        },

        step: function (dir) {
            var totalShiftedItems = this.state.totalShiftedItems + dir;
    
            if (dir < 0 && (-1 * totalShiftedItems) + this.state.numVisible > (this.thumbnailItems.length - 1)) {
                totalShiftedItems = this.state.numVisible - this.thumbnailItems.length;
            }
            else if (dir > 0 && totalShiftedItems > 0) {
                totalShiftedItems = 0;
            }
    
            if (this.options.circular) {
                if (dir < 0 && this.thumbnailItems.length - 1 === this.options.activeIndex) {
                    totalShiftedItems = 0;
                }
                else if (dir > 0 && this.options.activeIndex === 0) {
                    totalShiftedItems = this.state.numVisible - this.thumbnailItems.length;
                }
            }
    
            if (this.container) {
                var transform = this.options.isVertical ? 'translate3d(0,' + (totalShiftedItems * (100/ this.state.numVisible)) + '%, 0)' : 'translate3d(' + (totalShiftedItems * (100/ this.state.numVisible)) + '%, 0, 0)';
                var transition = 'transform 500ms ease 0s';

                this.container.removeClass('ui-items-hidden').css({ 'transform': transform, 'transition': transition });
            }

            this.setState({
                totalShiftedItems: totalShiftedItems
            });
        },
    
        stopSlideShow: function () {
            if (this.options.slideShowActive && this.options.stopSlideShow) {
                this.options.stopSlideShow();
            }
        },
    
        getMedianIndex: function () {
            var index = Math.floor(this.state.numVisible / 2);
    
            return (this.state.numVisible % 2) ? index : index - 1;
        },
    
        navBackward: function (e) {
            this.stopSlideShow();
    
            var prevItemIndex = this.options.activeIndex !== 0 ? this.options.activeIndex - 1 : 0;
            var diff = prevItemIndex + this.state.totalShiftedItems;
            if ((this.state.numVisible - diff - 1) > this.getMedianIndex() && ((-1 * this.state.totalShiftedItems) !== 0 || this.options.circular)) {
                this.step(1);
            }
    
            this.options.onActiveIndexChange({
                index: this.options.circular && this.options.activeIndex === 0 ? this.thumbnailItems.length - 1 : prevItemIndex
            });
    
            e.preventDefault();
        },
    
        navForward: function (e) {
            this.stopSlideShow();
    
            var nextItemIndex = this.options.activeIndex + 1;
            if (nextItemIndex + this.state.totalShiftedItems > this.getMedianIndex() && ((-1 * this.state.totalShiftedItems) < this.getTotalPageNumber() - 1 || this.options.circular)) {
                this.step(-1);
            }
    
            this.options.onActiveIndexChange({
                index: this.options.circular && (this.thumbnailItems.length - 1) === this.options.activeIndex ? 0 : nextItemIndex
            });
    
            e.preventDefault();
        },
    
        onItemClick: function (event) {
            this.stopSlideShow();
    
            var selectedItemIndex = $(event.currentTarget).index();
            if (selectedItemIndex !== this.options.activeIndex) {
                var diff = selectedItemIndex + this.state.totalShiftedItems;
                var dir = 0;
                if (selectedItemIndex < this.options.activeIndex) {
                    dir = (this.state.numVisible - diff - 1) - this.getMedianIndex();
                    if (dir > 0 && (-1 * this.state.totalShiftedItems) !== 0) {
                        this.step(dir);
                    }
                }
                else {
                    dir = this.getMedianIndex() - diff;
                    if (dir < 0 && (-1 * this.state.totalShiftedItems) < this.getTotalPageNumber() - 1) {
                        this.step(dir);
                    }
                }
    
                this.options.onActiveIndexChange({
                    index: selectedItemIndex
                });
            }
        },
    
        onTransitionEnd: function (e) {
            if (this.container && e.originalEvent && e.originalEvent.propertyName === 'transform') {
                this.container.addClass('ui-items-hidden').css('transition', '');
            }
        },
    
        onTouchStart: function (e) {
            var touchobj = e.changedTouches[0];
    
            this.startPos = {
                x: touchobj.pageX,
                y: touchobj.pageY
            };
        },
    
        onTouchMove: function (e) {
            e.preventDefault();
        },
    
        onTouchEnd: function (e) {
            var touchobj = e.changedTouches[0];
    
            if (this.options.isVertical) {
                this.changePageOnTouch(e, (touchobj.pageY - this.startPos.y));
            }
            else {
                this.changePageOnTouch(e, (touchobj.pageX - this.startPos.x));
            }
        },
    
        changePageOnTouch: function (e, diff) {
            if (diff < 0) {           // left
                this.navForward(e);
            }
            else {                    // right
                this.navBackward(e);
            }
        },
    
        getTotalPageNumber: function () {
            return this.thumbnailItems.length > this.state.numVisible ? (this.thumbnailItems.length - this.state.numVisible) + 1 : 0;
        },
    
        createStyle: function () {
            if (!this.thumbnailsStyle) {
                this.thumbnailsStyle = document.createElement('style');
                document.body.appendChild(this.thumbnailsStyle);
            }
    
            var galleriaSelector = this.options.selector ? this.options.selector : '#' + this.options.id;
            var innerHTML =  galleriaSelector + ' .ui-galleria-thumbnail-items .ui-galleria-thumbnail-item { flex: 1 0 ' + (100/ this.state.numVisible) + '%; }';
    
            if (this.options.responsiveOptions) {
                this.responsiveOptions = Array.from(this.options.responsiveOptions);
                this.responsiveOptions.sort(function (data1, data2) {
                    var value1 = data1.breakpoint;
                    var value2 = data2.breakpoint;
                    var result = null;
    
                    if (value1 == null && value2 != null)
                        result = -1;
                    else if (value1 != null && value2 == null)
                        result = 1;
                    else if (value1 == null && value2 == null)
                        result = 0;
                    else if (typeof value1 === 'string' && typeof value2 === 'string')
                        result = value1.localeCompare(value2, undefined, { numeric: true });
                    else
                        result = (value1 < value2) ? -1 : (value1 > value2) ? 1 : 0;
    
                    return -1 * result;
                });
    
                for (var i = 0; i < this.responsiveOptions.length; i++) {
                    var res = this.responsiveOptions[i];
    
                    innerHTML += '@media screen and (max-width:' + res.breakpoint + ') { ' + galleriaSelector + ' .ui-galleria-thumbnail-items .ui-galleria-thumbnail-item { flex: 1 0 ' + (100/ res.numVisible) + '%; }}';
                }
            }
    
            this.thumbnailsStyle.innerHTML = innerHTML;
        },
    
        calculatePosition: function () {
            if (this.container && this.responsiveOptions) {
                var windowWidth = window.innerWidth;
                var matchedResponsiveData = {
                    numVisible: this.options.numVisible
                };
    
                for (var i = 0; i < this.responsiveOptions.length; i++) {
                    var res = this.responsiveOptions[i];
    
                    if (parseInt(res.breakpoint, 10) >= windowWidth) {
                        matchedResponsiveData = res;
                    }
                }
    
                if (this.state.numVisible !== matchedResponsiveData.numVisible) {
                    this.setState({
                        numVisible: matchedResponsiveData.numVisible
                    });
                }
            }
        },
    
        bindDocumentListeners: function () {
            if (!this.documentResizeListener) {
                var $this = this;
                this.documentResizeListener = function () {
                    $this.calculatePosition();
                };
    
                window.addEventListener('resize', this.documentResizeListener);
            }
        },
    
        unbindDocumentListeners: function () {
            if(this.documentResizeListener) {
                window.removeEventListener('resize', this.documentResizeListener);
                this.documentResizeListener = null;
            }
        },
    
        mounted: function () {    
            this.createStyle();
            this.calculatePosition();
    
            if (this.options.responsiveOptions) {
                this.bindDocumentListeners();
            }

            this._updateUI();
        },
    
        updated: function (prevOptions, prevState) {
            var totalShiftedItems = this.state.totalShiftedItems;

            if (prevState.numVisible !== this.state.numVisible || prevOptions.activeIndex !== this.options.activeIndex) {
                if (this.options.activeIndex <= this.getMedianIndex()) {
                    totalShiftedItems = 0;
                }
                else if (this.thumbnailItems.length - this.state.numVisible + this.getMedianIndex() < this.options.activeIndex) {
                    totalShiftedItems = this.state.numVisible - this.thumbnailItems.length;
                }
                else if (this.thumbnailItems.length - this.state.numVisible < this.options.activeIndex && this.state.numVisible % 2 === 0) {
                    totalShiftedItems = (this.options.activeIndex * -1) + this.getMedianIndex() + 1;
                }
                else {
                    totalShiftedItems = (this.options.activeIndex * -1) + this.getMedianIndex();
                }
    
                if (totalShiftedItems !== this.state.totalShiftedItems) {
                    this.setState({
                        totalShiftedItems: totalShiftedItems
                    });
                }
    
                var transform = this.options.isVertical ? 'translate3d(0, ' + (totalShiftedItems * (100/ this.state.numVisible)) + '%, 0)' : 'translate3d(' + (totalShiftedItems * (100/ this.state.numVisible)) + '%, 0, 0)';
                this.container.css('transform', transform);
    
                if (prevOptions.activeIndex !== this.options.activeIndex) {
                    this.container.removeClass('ui-items-hidden').css('transition', 'transform 500ms ease 0s');
                }

                this._updateUI();
            }
        },

        _setOption: function (key, value) {
            this._super(key, value);
            this.updated(this.prevOptions, this.state);
        },

        _setOptions: function (options) {
            if (this.options.showThumbnails) {
                var $this = this;
                this.prevOptions = Object.assign({}, this.options);
                
                $.each(options, function (key, value) {
                    $this._setOption(key, value);
                });

                this.prevOptions = options;
            }
        },

        _updateUI: function () {
            var firstIndex = this.state.totalShiftedItems * -1;
            var lastIndex = firstIndex + this.state.numVisible - 1;

            this.thumbnailItems.removeClass('ui-galleria-thumbnail-item-current ui-galleria-thumbnail-item-active ui-galleria-thumbnail-item-start ui-galleria-thumbnail-item-end');
            
            for (var index = 0; index < this.thumbnailItems.length; index++) {
                var item = this.thumbnailItems.eq(index);
                var active = firstIndex <= index && lastIndex >= index;
                var start = firstIndex === index;
                var end = lastIndex === index;
                var current = this.options.activeIndex === index;

                var itemClass = PrimeFaces.utils.styleClass({
                    'ui-galleria-thumbnail-item-current': current,
                    'ui-galleria-thumbnail-item-active': active,
                    'ui-galleria-thumbnail-item-start': start,
                    'ui-galleria-thumbnail-item-end': end
                });

                item.addClass(itemClass).children('.ui-galleria-thumbnail-item-content').attr('tabindex', (active ? '0' : ''));
            }
            
            if (this.options.showThumbnailNavigators) {
                var isNavBackwardBtnDisabled = (!this.options.circular && this.options.activeIndex === 0) || (this.thumbnailItems.length <= this.state.numVisible);
                var isNavForwardBtnDisabled = (!this.options.circular && this.options.activeIndex === (this.thumbnailItems.length - 1)) || (this.thumbnailItems.length <= this.state.numVisible);
                var toggleDisabled = function (el, disabled) {
                    if (disabled)
                        el.attr('disabled', 'disabled').addClass('ui-state-disabled');
                    else
                        el.removeAttr('disabled').removeClass('ui-state-disabled');
                };

                toggleDisabled(this.navBackwardBtn, isNavBackwardBtnDisabled);
                toggleDisabled(this.navForwardBtn, isNavForwardBtnDisabled);
            }
        },
    
        _destroy: function() {
            if (this.options.responsiveOptions) {
                this.unbindDocumentListeners();
            }
        },

        _bindEvents: function() {
            var $this = this;

            if (this.options.showThumbnailNavigators) {
                this.navBackwardBtn.off('click.galleria-thumbnail-nav').on('click.galleria-thumbnail-nav', this.navBackward.bind($this));
                this.navForwardBtn.off('click.galleria-thumbnail-nav').on('click.galleria-thumbnail-nav', this.navForward.bind($this));
            }

            this.container.off('transitionend.galleria-thumbnail touchstart.galleria-thumbnail touchmove.galleria-thumbnail touchend.galleria-thumbnail')
                .on('transitionend.galleria-thumbnail', this.onTransitionEnd.bind($this))
                .on('touchstart.galleria-thumbnail', this.onTouchStart.bind($this))
                .on('touchmove.galleria-thumbnail', this.onTouchMove.bind($this))
                .on('touchend.galleria-thumbnail', this.onTouchEnd.bind($this));
            
            this.container.children().off('click.galleria-thumbnail-item keydown.galleria-thumbnail-item')
                .on('click.galleria-thumbnail-item', this.onItemClick.bind($this))
                .on('keydown.galleria-thumbnail-item', function (e) {
                    if (e.key === 'Enter') {
                        $this.onItemClick(e);
                    }
                });
        },
    
        _renderBackwardNavigator: function () {
            if (this.options.showThumbnailNavigators) {
                var iconStyleClass = PrimeFaces.utils.styleClass('ui-galleria-thumbnail-prev-icon ui-icon', {
                    'ui-icon-circle-triangle-w': !this.options.isVertical,
                    'ui-icon-circle-triangle-n': this.options.isVertical
                });
    
                return (
                    '<button type="button" class="ui-galleria-thumbnail-prev ui-corner-all ui-galleria-link">' +
                        '<span class="' + iconStyleClass + '"></span>' +
                    '</button>'
                );
            }
    
            return '';
        },
    
        _renderForwardNavigator: function () {
            if (this.options.showThumbnailNavigators) {
                var iconStyleClass = PrimeFaces.utils.styleClass('ui-galleria-thumbnail-next-icon ui-icon', {
                    'ui-icon-circle-triangle-e': !this.options.isVertical,
                    'ui-icon-circle-triangle-s': this.options.isVertical
                });
    
                return (
                    '<button type="button" class="ui-galleria-thumbnail-next ui-corner-all ui-galleria-link">' +
                        '<span class="' + iconStyleClass + '"></span>' +
                    '</button>'
                );
            }
    
            return '';
        },

        _renderContent: function () {
            var backwardNavigator = this._renderBackwardNavigator();
            var forwardNavigator = this._renderForwardNavigator();

            this.container.wrap(
                '<div class="ui-galleria-thumbnail-container">' +
                    '<div class="ui-galleria-thumbnail-items-container">' +
                    '</div>' +
                '</div>'
            );

            this.container.parent().before(backwardNavigator).after(forwardNavigator);

            return this.container.closest('.ui-galleria-thumbnail-container');
        },

        _render: function () {
            var content = this._renderContent();

            content.wrap('<div class="ui-galleria-thumbnail-wrapper"></div>');
        }
    });

}));