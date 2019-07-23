/**
 * PrimeFaces DatePicker Widget
 */
PrimeFaces.widget.DatePicker = PrimeFaces.widget.BaseWidget.extend({

    init: function(cfg) {
        this._super(cfg);

        this.input = $(this.jqId + '_input');
        this.jqEl = this.cfg.inline ? $(this.jqId + '_inline') : this.input;
        var _self = this;

        //i18n and l7n
        this.configureLocale();
        
        //events
        this.bindDateSelectListener();
        this.bindViewChangeListener();
        this.bindCloseListener();
        
        //Client behaviors, input skinning and z-index
        if(!this.cfg.inline) {
            PrimeFaces.skinInput(this.jqEl);

            if(this.cfg.behaviors) {
                PrimeFaces.attachBehaviors(this.jqEl, this.cfg.behaviors);
            }

            this.cfg.onBeforeShow = function() {
                if(_self.refocusInput) {
                    _self.refocusInput = false;
                    return false;
                }
                
                this.panel.css('z-index', ++PrimeFaces.zindex);
                
                var inst = this; // the instance of prime.datePicker API

                // touch support - prevents keyboard popup
                if(PrimeFaces.env.touch && !inst.inputfield.attr("readonly") && _self.cfg.showIcon) {
                    _self.jqEl.prop("readonly", true);
                }

                //user callback
                var preShow = _self.cfg.preShow;
                if(preShow) {
                    return _self.cfg.preShow.call(_self, inst);
                }
            };
        }

        // touch support - prevents keyboard popup
        if (PrimeFaces.env.touch && !this.input.attr("readonly") && this.cfg.showIcon) {
            this.cfg.onBeforeHide = function() {
                _self.jqEl.attr("readonly", false);
            };
        }

        //Initialize datepicker
        this.cfg.panelStyleClass = (this.cfg.panelStyleClass || '') + ' p-datepicker-panel';
        this.cfg.viewDate = this.viewDateOption;
        this.cfg.appendTo = this.cfg.appendTo ? PrimeFaces.expressions.SearchExpressionFacade.resolveComponentsAsSelector(this.cfg.appendTo) : null;
        
        this.jq.datePicker(this.cfg);

        //extensions
        if(!this.cfg.inline && this.cfg.showIcon) {
            var triggerButton = this.jqEl.siblings('.ui-datepicker-trigger:button');
            triggerButton.attr('aria-label',PrimeFaces.getAriaLabel('calendar.BUTTON')).attr('aria-haspopup', true);
                        
            var title = this.jqEl.attr('title');
            if(title) {
                triggerButton.attr('title', title);
            }
            
            var buttonIndex = this.cfg.buttonTabindex||this.jqEl.attr('tabindex');
            if(buttonIndex) {
                triggerButton.attr('tabindex', buttonIndex);
            }

            PrimeFaces.skinButton(triggerButton);
        }

        //mark target and descandants of target as a trigger for a primefaces overlay
        if(!this.cfg.inline) {
            this.jq.data('primefaces-overlay-target', this.id).find('*').data('primefaces-overlay-target', this.id);
        }

        //pfs metadata
        this.input.data(PrimeFaces.CLIENT_ID_DATA, this.id);        
    },
    
    configureLocale: function() {
        var localeSettings = PrimeFaces.locales[this.cfg.userLocale];

        if(localeSettings) {
            var locale = {};
            for(var setting in localeSettings) {
                locale[setting] = localeSettings[setting];
            }
            
            this.cfg.userLocale = locale;
        }
    },
    
    bindDateSelectListener: function() {
        var _self = this;

        this.cfg.onSelect = function(event, date) {
            _self.viewDateOption = this.viewDate;
            
            _self.fireDateSelectEvent();
            
            if(!_self.cfg.inline && _self.cfg.focusOnSelect) {
                _self.refocusInput = true;
                _self.jqEl.focus();
                if(!_self.cfg.showIcon) {
                    var inst = this;
                    
                    _self.jqEl.off('click.datepicker').on('click.datepicker', function() {
                        inst.showOverlay();
                    });
                }

                setTimeout(function() {
                    _self.refocusInput = false;
                }, 10);
            }
        };
    },

    fireDateSelectEvent: function() {
        if(this.cfg.behaviors) {
            var dateSelectBehavior = this.cfg.behaviors['dateSelect'];

            if(dateSelectBehavior) {
                dateSelectBehavior.call(this);
            }
        }
    },

    bindViewChangeListener: function() {
        var _self = this;
        this.cfg.onViewDateChange = function(event, date) {
            _self.viewDateOption = date;
            
            if(_self.hasBehavior('viewChange')) {
                _self.fireViewChangeEvent(date.getFullYear(), date.getMonth());
            }
        }; 
    },

    fireViewChangeEvent: function(year, month) {
        if(this.cfg.behaviors) {
            var viewChangeBehavior = this.cfg.behaviors['viewChange'];

            if(viewChangeBehavior) {
                var ext = {
                        params: [
                            {name: this.id + '_month', value: month},
                            {name: this.id + '_year', value: year}
                        ]
                };

                viewChangeBehavior.call(this, ext);
            }
        }
    },

    bindCloseListener: function() {
        if(this.hasBehavior('close')) {
            var $this = this;
            this.cfg.onBeforeHide = function() {
                $this.fireCloseEvent();
            };
        }
    },

    fireCloseEvent: function() {
        if(this.cfg.behaviors) {
            var closeBehavior = this.cfg.behaviors['close'];
            if(closeBehavior) {
                closeBehavior.call(this);
            }
        }
    },
    
    /**
     * Sets the date value the DatePicker.
     */
    setDate: function(date) {
        this.jq.datePicker('setDate', date);
    },

    /**
     * Gets the date value of the DatePicker
     */
    getDate: function() {
        return this.jq.datePicker('getDate');
    },
    
    /**
     * Sets the displayed visible calendar date.
     */
    setViewDate: function(date) {
        this.jq.datePicker('updateViewDate', null, date);
    },

    /**
     * Gets the displayed visible calendar date.
     */
    getViewDate: function() {
        return this.jq.datePicker().data().primeDatePicker.viewDate;
    },
    
});