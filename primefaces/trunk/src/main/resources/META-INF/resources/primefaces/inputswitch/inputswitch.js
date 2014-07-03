/**
 * PrimeFaces InputSwitch Widget
 */
PrimeFaces.widget.InputSwitch = PrimeFaces.widget.BaseWidget.extend({

    init: function(cfg) {
        this._super(cfg);
        
        this.onContainer = this.jq.children('.ui-inputswitch-on');
        this.onLabel = this.onContainer.children('span');
        this.offContainer = this.jq.children('.ui-inputswitch-off');
        this.offLabel = this.offContainer.children('span');
        this.handle = this.jq.children('.ui-inputswitch-handle');
        this.input = $(this.jqId + '_input');
        
        this._draw();
        
        if(!this.input.prop('disabled')) {
            this._bindEvents();
        }
    },
    
    _draw: function() {
        var	onContainerWidth = this.onContainer.width(),
        offContainerWidth = this.offContainer.width(),
        spanPadding	= this.offLabel.innerWidth() - this.offLabel.width(),
        handleMargins = this.handle.outerWidth() - this.handle.innerWidth();

        var containerWidth = (onContainerWidth > offContainerWidth) ? onContainerWidth : offContainerWidth,
        handleWidth = containerWidth;

        this.handle.css({'width':handleWidth});
        handleWidth = this.handle.width();

        containerWidth = containerWidth + handleWidth + 6;
        
        var labelWidth = containerWidth - handleWidth - spanPadding - handleMargins;

        this.jq.css({'width': containerWidth });
        this.onLabel.width(labelWidth);
        this.offLabel.width(labelWidth);
        
        //position
        this.offContainer.css({ width: this.jq.width() - 5 });
        this.offset = this.jq.width() - this.handle.outerWidth();

        if(this.input.prop('checked')) {
            this.handle.css({ 'left': this.offset});
            this.onContainer.css({ 'width': this.offset});
            this.offLabel.css({ 'margin-right': -this.offset});
        }
        else {
            this.onContainer.css({ 'width': 0 });
            this.onLabel.css({'margin-left': -this.offset});
        }
    },
    
    _bindEvents: function() {
        var $this = this;
        
        this.jq.on('click.inputSwitch', function(e) {
            $this.toggle();
        });
    },
    
    toggle: function() {
        if(this.input.prop('checked')) {
            this.uncheck();
        }
        else {
            this.check();
        }
    },
    
    check: function() {
        this.onContainer.animate({width:this.offset}, 200);
        this.onLabel.animate({marginLeft:0}, 200);
        this.offLabel.animate({marginRight:-this.offset}, 200);
        this.handle.animate({left:this.offset}, 200);
        this.input.prop('checked', true).trigger('change');
    },
    
    uncheck: function() {
        this.onContainer.animate({width:0}, 200);
        this.onLabel.animate({marginLeft:-this.offset}, 200);
        this.offLabel.animate({marginRight:0}, 200);
        this.handle.animate({left:0}, 200);
        this.input.prop('checked', false).trigger('change');
    }
    
});