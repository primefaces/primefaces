/**
 * The configuration for the {@link TagCloud} component.
 */
export interface TagCloudCfg extends PrimeFaces.component.BaseComponentCfg {
}

/**
 * __PrimeFaces TagCloud Component__
 *
 * TagCloud displays a collection of tags with different strengths.
 */
export class TagCloud<Cfg extends TagCloudCfg = TagCloudCfg> extends PrimeFaces.component.BaseComponent<Cfg> {

    override init(cfg: PrimeFaces.component.PartialComponentCfg<Cfg>): void {
        super.init(cfg);

        this.jq.find('a').on('mouseover.tagcloud', (e) => {
            $(e.currentTarget).addClass('p-state-hover');
        })
        .on('mouseout.tagcloud', (e) => {
            $(e.currentTarget).removeClass('p-state-hover');
        })
        .on('click.tagcloud', (e) => {
            const link = $(e.currentTarget);

            if (link.attr('href') === '#') {
                this.fireSelectEvent(link);
                e.preventDefault();
            }
        });
    }

    private fireSelectEvent(link: JQuery): void {
        if (this.hasBehavior('select')) {
            const ext = {
                params: [
                    { name: this.getId() + '_itemIndex', value: link.parent().index() }
                ]
            };

            this.callBehavior('select', ext);
        }
    }
}
