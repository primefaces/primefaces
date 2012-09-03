/**
 * PrimeFaces ProgressBar widget
 */
PrimeFaces.widget.ProgressBar = PrimeFaces.widget.BaseWidget.extend({
    
    init: function(cfg) {
        this._super(cfg);
        
        this.jqValue = this.jq.children('.ui-progressbar-value');
        this.jqLabel = this.jq.children('.ui-progressbar-label');
        this.value = this.cfg.initialValue;

        if(this.cfg.ajax) {
            this.cfg.formId = this.jq.parents('form:first').attr('id');
        }

        this.enableARIA();
    },
    
    setValue: function(value) {
        if(value >= 0 && value<=100) {
            if(value == 0) {
                this.jqValue.hide().css('width', '0%').removeClass('ui-corner-right');

                this.jqLabel.hide();
            }
            else {
                this.jqValue.show().animate({
                    'width': value + '%' 
                }, 500, 'easeInOutCirc');

                if(this.cfg.labelTemplate) {
                    var formattedLabel = this.cfg.labelTemplate.replace(/{value}/gi, value);

                    this.jqLabel.html(formattedLabel).show();
                }
            }

            this.value = value;
            this.jq.attr('aria-valuenow', value);
        }
    },
    
    getValue: function() {
        return this.value;
    },
    
    start: function() {
        var _self = this;

        if(this.cfg.ajax) {

            this.progressPoll = setInterval(function() {
                var options = {
                    source: _self.id,
                    process: _self.id,
                    formId: _self.cfg._formId,
                    async: true,
                    oncomplete: function(xhr, status, args) {
                        var value = args[_self.id + '_value'];
                        _self.setValue(value);

                        //trigger complete listener
                        if(value === 100) {
                            _self.fireCompleteEvent();
                        }
                    }
                };

                PrimeFaces.ajax.AjaxRequest(options);

            }, this.cfg.interval);
        }
    },
    
    fireCompleteEvent: function() {
        clearInterval(this.progressPoll);

        if(this.cfg.behaviors) {
            var completeBehavior = this.cfg.behaviors['complete'];

            if(completeBehavior) {
                completeBehavior.call(this);
            }
        }
    },
    
    cancel: function() {
        clearInterval(this.progressPoll);
        this.setValue(0);
    },
    
    enableARIA: function() {
        this.jq.attr('role', 'progressbar')
                .attr('aria-valuemin', 0)
                .attr('aria-valuenow', this.value)
                .attr('aria-valuemax', 100);
    }

});