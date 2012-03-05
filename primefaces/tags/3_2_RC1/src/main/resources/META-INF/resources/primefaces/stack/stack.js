/**
 * PrimeFaes Stack Widget
 */
PrimeFaces.widget.Stack = PrimeFaces.widget.BaseWidget.extend({
    
    init: function(cfg) {
        this._super(cfg);
        
        var _self = this;
	
        $(this.jqId + '.ui-stack>img').toggle(function(){
            var vertical = 0;
            var horizontal = 0;
            var el=$(this);
            el.next().children().each(function(){
                $(this).animate({top: '-' + vertical + 'px', left: horizontal + 'px'}, _self.cfg.openSpeed);
                vertical = vertical + 55;
                horizontal = (horizontal+.75)*2;
            });
            el.next().animate({top: '-50px', left: '10px'}, _self.cfg.openSpeed).addClass('openStack')
            .find('li a>img').animate({width: '50px', marginLeft: '9px'}, _self.cfg.openSpeed);
            el.animate({paddingTop: '0'});
        }, function(){
            var el=$(this);
            el.next().removeClass('openStack').children('li').animate({top: '55px', left: '-10px'}, _self.cfg.closeSpeed);
            el.next().find('li a>img').animate({width: '79px', marginLeft: '0'}, _self.cfg.closeSpeed);
            el.animate({paddingTop: '35'});
        });

        if(this.cfg.expanded) {
            this.jq.children('img').click();
        }
    }
    
});