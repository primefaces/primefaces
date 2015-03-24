/**
 * PrimeFaces Slider Widget
 */
PrimeFaces.widget.Slider = PrimeFaces.widget.BaseWidget.extend({
    
    init: function(cfg) {
        this._super(cfg);
        
        this.cfg.displayTemplate = this.cfg.displayTemplate||(this.cfg.range ? '{min} - {max}' : '{value}');
        
        if(this.cfg.range) {
            var inputIds = this.cfg.input.split(',');
            this.input = $(PrimeFaces.escapeClientId(inputIds[0]) + ',' + PrimeFaces.escapeClientId(inputIds[1]));
        } 
        else {
            this.input = $(PrimeFaces.escapeClientId(this.cfg.input));
        }
        
        if(this.cfg.display) {
            this.output = $(PrimeFaces.escapeClientId(this.cfg.display));
        }

        this.jq.slider(this.cfg);

        this.bindEvents();
    },
    
    bindEvents: function() {
        var $this = this;
        
        this.jq.bind('slide', function(event, ui) {
            $this.onSlide(event, ui);
        });

        if(this.cfg.onSlideStart) {
            this.jq.bind('slidestart', function(event, ui) {
                $this.cfg.onSlideStart.call(this, event, ui);
            });
        }

        this.jq.bind('slidestop', function(event, ui) {
            $this.onSlideEnd(event, ui);
        });

        this.input.on('keydown.slider', function (e) { 
            var keyCode = $.ui.keyCode, 
            key = e.which;
            
            switch(key) { 
                case keyCode.UP: 
                case keyCode.DOWN: 
                case keyCode.LEFT: 
                case keyCode.RIGHT:                
                case keyCode.BACKSPACE: 
                case keyCode.DELETE: 
                case keyCode.END: 
                case keyCode.HOME:
                case keyCode.TAB: 
                break; 
            
                default:
                    var metaKey = e.metaKey||e.ctrlKey, 
                    isNumber = (key >= 48 && key <= 57) || (key >= 96 && key <= 105);
                    
                    //prevent special characters with alt and shift
                    if(e.altKey || (e.shiftKey && !(key === keyCode.UP || key === keyCode.DOWN || key === keyCode.LEFT || key === keyCode.RIGHT))) {
                        e.preventDefault();
                    }
                    
                    //prevent letters and allow letters with meta key such as ctrl+c
                    if(!isNumber && !metaKey) { 
                        e.preventDefault(); 
                    }
                break; 
            } 
        }).on('keyup.slider', function (e) { 
            $this.setValue($this.input.val()); 
        });
    },
    
    onSlide: function(event, ui) {
        if(this.cfg.onSlide) {
            this.cfg.onSlide.call(this, event, ui);
        }

        if(this.cfg.range) {
            this.input.eq(0).val(ui.values[0]);
            this.input.eq(1).val(ui.values[1]);
            
            if(this.output) {
                this.output.html(this.cfg.displayTemplate.replace('{min}', ui.values[0]).replace('{max}', ui.values[1]));
            }
        }
        else {
            this.input.val(ui.value);
            
            if(this.output) {
                this.output.html(this.cfg.displayTemplate.replace('{value}', ui.value));
            }
        }
    },
    
    onSlideEnd: function(event, ui) {
        if(this.cfg.onSlideEnd) {
            this.cfg.onSlideEnd.call(this, event, ui);
        }

        if(this.cfg.behaviors) {
            var slideEndBehavior = this.cfg.behaviors['slideEnd'];

            if(slideEndBehavior) {
                var ext = {
                    params: [
                        {name: this.id + '_slideValue', value: ui.value}
                    ]
                };

                slideEndBehavior.call(this, ext);
            }
        }
    },
    
    getValue: function() {
        return this.jq.slider('value');
    },
    
    setValue: function(value) {
        this.jq.slider('value', value);
    },
    
    getValues: function() {
        return this.jq.slider('values');
    },
    
    setValues: function(values) {
        this.jq.slider('values', values);
    },
    
    enable: function() {
        this.jq.slider('enable');
    },
    
    disable: function() {
        this.jq.slider('disable');
    }
});