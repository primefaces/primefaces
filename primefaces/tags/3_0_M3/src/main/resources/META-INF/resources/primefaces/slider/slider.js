/**
 * PrimeFaces Slider Widget
 */
PrimeFaces.widget.Slider = function(id, cfg) {
	this.id = id;
	this.cfg = cfg;
    this.jq = jQuery(PrimeFaces.escapeClientId(this.id));
	this.input = jQuery(PrimeFaces.escapeClientId(this.cfg.input));
	if(this.cfg.output) {
		this.output = jQuery(PrimeFaces.escapeClientId(this.cfg.output));
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
}
    
PrimeFaces.widget.Slider.prototype.onSlide = function(event, ui) {
    //User callback
    if(this.cfg.onSlide) {
        this.cfg.onSlide.call(this, event, ui);
    }

    //Update input and output(if defined)
	this.input.val(ui.value);
	
	if(this.output) {
		this.output.html(ui.value);
	}
}

PrimeFaces.widget.Slider.prototype.onSlideEnd = function(event, ui) {
    //User callback
    if(this.cfg.onSlideEnd) {
        this.cfg.onSlideEnd.call(this, event, ui);
    }

    if(this.cfg.behaviors) {
        var slideEndBehavior = this.cfg.behaviors['slideEnd'];

        if(slideEndBehavior) {
            var ext = {
                params: {}
            };
            ext.params[this.id + '_ajaxSlideValue'] = ui.value;

            slideEndBehavior.call(this, event, ext);
        }
    }
}

PrimeFaces.widget.Slider.prototype.getValue = function() {
    return this.jq.slider('value');
}

PrimeFaces.widget.Slider.prototype.setValue = function(value) {
    this.jq.slider('value', value);
}

PrimeFaces.widget.Slider.prototype.enable = function() {
    this.jq.slider('enable');
}

PrimeFaces.widget.Slider.prototype.disable = function() {
    this.jq.slider('disable');
}