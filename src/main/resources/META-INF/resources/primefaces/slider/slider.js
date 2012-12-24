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

        var $this = this;

        //Create slider
        this.jq.slider(this.cfg);

        //Slide handler
        this.jq.bind('slide', function(event, ui) {
            $this.onSlide(event, ui);
        });

        //Slide start handler
        if(this.cfg.onSlideStart) {
            this.jq.bind('slidestart', function(event, ui) {
                $this.cfg.onSlideStart.call(this, event, ui);
            });
        }

        //Slide end handler
        this.jq.bind('slidestop', function(event, ui) {
            $this.onSlideEnd(event, ui);
        });

        this.input.keypress(function(e){
            var charCode = (e.which) ? e.which : e.keyCode
            if(charCode > 31 && (charCode < 48 || charCode > 57))
                return false;
            else
                return true;
        });

        this.input.keyup(function() {
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
                        {name: this.id + '_ajaxSlideValue', value: ui.value}
                    ]
                };

                slideEndBehavior.call(this, event, ext);
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