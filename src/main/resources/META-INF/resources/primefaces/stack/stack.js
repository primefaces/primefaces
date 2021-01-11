/**
 * __PrimeFaces Stack Widget__
 * 
 * Stack is a navigation component that mimics the stacks feature in Mac OS X.
 * 
 * @interface {PrimeFaces.widget.StackCfg} cfg The configuration for the {@link  Stack| Stack widget}.
 * You can access this configuration via {@link PrimeFaces.widget.BaseWidget.cfg|BaseWidget.cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 * @extends {PrimeFaces.widget.BaseWidgetCfg} cfg
 * 
 * @prop {number} cfg.closeSpeed Duration in milliseconds it takes the stack to close.
 * @prop {boolean} cfg.expanded Whether the stack is currently closed or opened.
 * @prop {number} cfg.openSpeed Duration in milliseconds it takes the stack to open.
 */
PrimeFaces.widget.Stack = PrimeFaces.widget.BaseWidget.extend({

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
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
        
            item.css({paddingTop: '35px'});
            nextItem.css({top: '-50px', left: '10px'}).children('li').css({top: '55px', left: '-10px'});
            nextItem.find('li a>img').css({width: '79px', marginLeft: '0'});
        }
    },
    
    /**
     * Expands the given menu item of this stack.
     * @param {JQuery} item A menu item to expand, usually the root IMG element that is an immediate child of
     * `.ui-stack`.
     */
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
            
    /**
     * Collapses the given menu item of this stack.
     * @param {JQuery} item A menu item to collapse, usually the root IMG element that is an immediate child of
     * `.ui-stack`.
     */
    collapse: function(item) {
        item.next().removeClass('openStack').children('li').animate({top: '55px', left: '-10px'}, this.cfg.closeSpeed);
        item.next().find('li a>img').animate({width: '79px', marginLeft: '0'}, this.cfg.closeSpeed);
        item.animate({paddingTop: '35'});
        
        this.cfg.expanded = false;
    }
});