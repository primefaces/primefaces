/**
 * The configuration for the {@link Stack} widget.
 */
export interface StackCfg extends PrimeFacesTypes.widget.BaseWidgetCfg {
    /** Duration in milliseconds it takes the stack to close. */
    closeSpeed: number;
    /** Whether the stack is currently closed or opened. */
    expanded: boolean;
    /** Duration in milliseconds it takes the stack to open. */
    openSpeed: number;
}

/**
 * __PrimeFaces Stack Widget__
 *
 * Stack is a navigation component that mimics the stacks feature in Mac OS X.
 */
export class Stack<Cfg extends StackCfg = StackCfg> extends PrimeFaces.widget.BaseWidget<Cfg> {

    override init(cfg: PrimeFacesTypes.widget.PartialWidgetCfg<Cfg>): void {
        super.init(cfg);
        this.cfg.expanded = this.cfg.expanded||false;

        $(this.jqId + '.p-stack > img').on('click.stack', (e) => {
            if (this.cfg.expanded)
                this.collapse($(e.currentTarget));
            else
                this.open($(e.currentTarget));
        });

        const item = this.jq.children('img');
        if (this.cfg.expanded) {
            this.open(item);
        }
        else {
            const nextItem = item.next();

            item.css({paddingTop: '35px'});
            nextItem.css({top: '-50px', left: '10px'}).children('li').css({top: '55px', left: '-10px'});
            nextItem.find('li a>img').css({width: '79px', marginLeft: '0'});
        }
    }

    /**
     * Expands the given menu item of this stack.
     * @param item A menu item to expand, usually the root IMG element that is an immediate child of `.p-stack`.
     */
    open(item: JQuery): void {
        let vertical = 0,
        horizontal = 0;

        item.next().children().each((i, el) => {
            $(el).animate({top: '-' + vertical + 'px', left: horizontal + 'px'}, this.cfg.openSpeed as number);
            vertical = vertical + 55;
            horizontal = (horizontal+.75)*2;
        });

        item.next().animate({top: '-50px', left: '10px'}, this.cfg.openSpeed as number).addClass('openStack')
                .find('li a>img').animate({width: '50px', marginLeft: '9px'}, this.cfg.openSpeed as number);
        item.animate({paddingTop: '0'});

        this.cfg.expanded = true;
    }

    /**
     * Collapses the given menu item of this stack.
     * @param item A menu item to collapse, usually the root IMG element that is an immediate child of `.p-stack`.
     */
    collapse(item: JQuery): void {
        item.next().removeClass('openStack').children('li').animate({top: '55px', left: '-10px'}, this.cfg.closeSpeed as number);
        item.next().find('li a>img').animate({width: '79px', marginLeft: '0'}, this.cfg.closeSpeed as number);
        item.animate({paddingTop: '35'});

        this.cfg.expanded = false;
    }
}
