/**
 * PrimeFaes Stack Widget
 */
PrimeFaces.widget.Stack = PrimeFaces.widget.BaseWidget.extend({
    
    init: function(cfg) {
        this._super(cfg);
        this.cfg.expanded = this.cfg.expanded||false;
        var $this = this;

        $(this.jqId + '.ui-stack > img').on('click.stack', function() {
            if($this.cfg.expanded)
                $this.collapse($(this));
            else
                $this.open($(this));
        });

        var item = this.jq.children('img');
        if(this.cfg.expanded) {
            this.open(item);
        }
        else {
            var nextItem = item.next();
        
            item.css({paddingTop: '35'});
            nextItem.css({top: '-50px', left: '10px'}).children('li').css({top: '55px', left: '-10px'});
            nextItem.find('li a>img').css({width: '79px', marginLeft: '0'});
        }
    },
    
    open: function(item) {
        var vertical = 0,
        horizontal = 0,
        $this = this;

        item.next().children().each(function(){
            $(this).animate({top: '-' + vertical + 'px', left: horizontal + 'px'}, $this.cfg.openSpeed);
            vertical = vertical + 55;
            horizontal = (horizontal+.75)*2;
        });

        item.next().animate({top: '-50px', left: '10px'}, this.cfg.openSpeed).addClass('openStack')
                .find('li a>img').animate({width: '50px', marginLeft: '9px'}, this.cfg.openSpeed);
        item.animate({paddingTop: '0'});
        
        this.cfg.expanded = true;
    },
            
    collapse: function(item) {
        item.next().removeClass('openStack').children('li').animate({top: '55px', left: '-10px'}, this.cfg.closeSpeed);
        item.next().find('li a>img').animate({width: '79px', marginLeft: '0'}, this.cfg.closeSpeed);
        item.animate({paddingTop: '35'});
        
        this.cfg.expanded = false;
    }
});