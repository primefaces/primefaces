/**
 * PrimeFaces Slider Widget
 */
PrimeFaces.widget.Slider = PrimeFaces.widget.BaseWidget.extend({
    
    init: function(cfg) {
        this._super(cfg);
        
        this.input = $(PrimeFaces.escapeClientId(this.cfg.input));
        if(this.cfg.output) {
            this.output = $(PrimeFaces.escapeClientId(this.cfg.output));
        }
        var _self = this;

        //Create slider
        this.jq.slider(this.cfg);

        //Slide handler
        this.jq.bind('slide', function(event, ui) {
            _self.onSlide(event, ui);
        });

        //Slide start handler
        if(this.cfg.onSlideStart) {
            this.jq.bind('slidestart', function(event, ui) {_self.cfg.onSlideStart.call(this, event, ui);});
        }

        //Slide end handler
        this.jq.bind('slidestop', function(event, ui) {_self.onSlideEnd(event, ui);});

        this.input.keypress(function(e){
            var charCode = (e.which) ? e.which : e.keyCode
            if(charCode > 31 && (charCode < 48 || charCode > 57))
                return false;
            else
                return true;
        });

        this.input.keyup(function(){
        _self.setValue(_self.input.val());
        });
    },
    
    onSlide: function(event, ui) {
        //User callback
        if(this.cfg.onSlide) {
            this.cfg.onSlide.call(this, event, ui);
        }

        //Update input and output(if defined)
        this.input.val(ui.value);

        if(this.output) {
            this.output.html(ui.value);
        }
    },
    
    onSlideEnd: function(event, ui) {
        //User callback
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
    
    enable: function() {
        this.jq.slider('enable');
    },
    
    disable: function() {
        this.jq.slider('disable');
    }

});