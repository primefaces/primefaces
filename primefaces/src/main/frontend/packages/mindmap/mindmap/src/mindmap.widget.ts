import type { MindmapConnection, RaphaelPaper } from "raphael";

declare module "raphael" {
    export interface RaphaelPaper {
        /**
         * Updates the path of an existing connection between two mindmap nodes, e.g. after a node was dragged.
         * @param connection The connection to update.
         */
        connection(connection: MindmapConnection): void;
    }
}

/**
 * The configuration for the {@link Mindmap} widget.
 */
export interface MindmapCfg extends PrimeFacesTypes.widget.DeferredWidgetCfg {
    /** Duration for all animations with nodes, in milliseconds. */
    effectSpeed: number;
    /** Horizontal coordinate for the center of the canvas. */
    centerX: number;
    /** Vertical coordinate for the center of the canvas. */
    centerY: number;
    /** Total height of the canvas. */
    height: number;
    /** Root node shown by the mindmap. */
    model: PrimeFacesTypes.widget.Mindmap.MindmapNode;
    /** Total width of the canvas. */
    width: number;
}

/**
 * __PrimeFaces Mindmap Widget__
 *
 * Mindmap is an interactive tool to visualize mindmap data featuring lazy loading, callbacks, animations and more.
 */
export class Mindmap<Cfg extends MindmapCfg = MindmapCfg> extends PrimeFaces.widget.DeferredWidget<Cfg> {
    /** A list of all drawn mindmap nodes. */
    nodes: PrimeFacesTypes.widget.Mindmap.MindmapElement[] = [];
    /** The canvas on which the mindmap is drawn. */
    raphael!: RaphaelPaper;
    /** The drawn root node for the mindmap. */
    root?: PrimeFacesTypes.widget.Mindmap.MindmapElement;
    /** The DOM element for the tooltip of a mindmap node. */
    tooltip: JQuery = $();

    override init(cfg: PrimeFacesTypes.widget.PartialWidgetCfg<Cfg>): void {
        super.init(cfg);

        this.renderDeferred();
    }

    protected override _render(): void {
        this.cfg.width = this.jq.width()!;
        this.cfg.height = this.jq.height()!;
        this.cfg.centerX = this.cfg.width / 2;
        this.cfg.centerY = this.cfg.height / 2;
        this.raphael = Raphael(this.getId(), this.cfg.width, this.cfg.height);
        this.nodes = [];

        if (this.cfg.model) {
            //root
            this.root = this.createNode(this.cfg.centerX, this.cfg.centerY, this.cfg.model);

            //children
            if (this.cfg.model.children) {
                this.createSubNodes(this.root);
            }
        }

        const tooltip = this.tooltip = $('<div class="p-tooltip p-mindmap-tooltip p-widget p-widget-content"></div>').appendTo(document.body);
        this.addRefreshListener(function() {
            tooltip.remove();
        });
        this.addDestroyListener(function() {
            tooltip.remove();
        });
    }

    /**
     * Creates a mindmap node at the given position for the given model.
     * @param x Horizontal coordinate where the node is drawn.
     * @param y Vertical coordinate where the node is drawn.
     * @param model Model with the data describing the node to be created.
     * @returns The created node.
     */
    private createNode(x: number, y: number, model: PrimeFacesTypes.widget.Mindmap.MindmapNode): PrimeFacesTypes.widget.Mindmap.MindmapElement {
        const node: PrimeFacesTypes.widget.Mindmap.MindmapElement = this.raphael.ellipse(x, y, 40, 25).attr('opacity', 0)
            .data('model', model)
            .data('connections', [])
            .data('widget', this.cfg.widgetVar);

        let label = model.label;
        const nodeWidth = node.getBBox().width;
        let title: string | null = null;

        const text: PrimeFacesTypes.widget.Mindmap.MindmapElement = this.raphael.text(x, y, label).attr('opacity', 0);

        if (nodeWidth <= text.getBBox().width) {
            title = label;
            label = label.substring(0, 12);
            text.attr('text', label + '...');
        }

        text.data('node', node);
        node.data('text', text);

        //node options
        if (model.fill) {
            node.attr({ fill: '#' + model.fill });
        }

        //title
        if (title) {
            node.data('title', title);

            node.mouseover(this.mouseoverNode);
            node.mouseout(this.mouseoutNode);

            text.mouseover(this.mouseoverText);
            text.mouseout(this.mouseoutText);
        }

        //show
        node.animate({ opacity: 1 }, this.cfg.effectSpeed!);
        text.animate({ opacity: 1 }, this.cfg.effectSpeed!);

        //make draggable
        node.drag(this.nodeDrag, this.nodeDragStart, this.nodeDragEnd);
        text.drag(this.textDrag, this.textDragStart, this.textDragEnd);

        //events
        if (model.selectable) {
            node.click(this.clickNode);
            text.click(this.clickNodeText);

            node.attr({ cursor: 'pointer' });
            text.attr({ cursor: 'pointer' });
        }

        //add to nodes
        this.nodes.push(node);

        return node;
    }

    /**
     * Callback that is invoked when the mouse cursor was moved over a mindmap node.
     * @param this The node.
     */
    private mouseoverNode(this: PrimeFacesTypes.widget.Mindmap.MindmapElement): void {
        const _self = PF(this.data('widget')) as Mindmap;

        _self.showTooltip(this);
    }

    /**
     * Callback that is invoked when the mouse cursor was moved away from a mindmap node.
     * @param this The node.
     */
    private mouseoutNode(this: PrimeFacesTypes.widget.Mindmap.MindmapElement): void {
        const _self = PF(this.data('widget')) as Mindmap;

        _self.hideTooltip(this);
    }

    /**
     * Callback that is invoked when the mouse cursor was moved over a text label.
     * @param this The text label.
     */
    private mouseoverText(this: PrimeFacesTypes.widget.Mindmap.MindmapElement): void {
        const node: PrimeFacesTypes.widget.Mindmap.MindmapElement = this.data('node'),
            _self = PF(node.data('widget')) as Mindmap;

        _self.showTooltip(node);
    }

    /**
     * Callback that is invoked when the mouse cursor was moved away from a text label.
     * @param this The text label.
     */
    private mouseoutText(this: PrimeFacesTypes.widget.Mindmap.MindmapElement): void {
        const node: PrimeFacesTypes.widget.Mindmap.MindmapElement = this.data('node'),
            _self = PF(node.data('widget')) as Mindmap;

        _self.hideTooltip(node);
    }

    /**
     * Brings up the tooltip for the given node, it it is not shown already.
     * @param node A node for which to show the tooltip.
     */
    showTooltip(node: PrimeFacesTypes.widget.Mindmap.MindmapElement): void {
        const title = node.data('title');

        if (title) {
            const _self = PF(node.data('widget')) as Mindmap,
                offset = _self.jq.offset()!;

            _self.tooltip.text(title)
                .css(
                    {
                        'left': (offset.left + node.attr('cx')! + 20) + 'px',
                        'top': (offset.top + node.attr('cy')! + 10) + 'px',
                        'z-index': PrimeFaces.nextZindex()
                    })
                .show();
        }


    }

    /**
     * Hides the tooltip for the given node, it it is shown.
     * @param node A node for which to hide the tooltip.
     */
    hideTooltip(node: PrimeFacesTypes.widget.Mindmap.MindmapElement): void {
        const title = node.data('title');

        if (title) {
            const _self = PF(node.data('widget')) as Mindmap;

            _self.tooltip.hide();
        }
    }

    /**
     * Centers the given node so that it is positioned near the center of the mindmap viewport.
     * @param node A node to center.
     */
    centerNode(node: PrimeFacesTypes.widget.Mindmap.MindmapElement): void {
        const text: PrimeFacesTypes.widget.Mindmap.MindmapElement = node.data('text');

        text.animate({ x: this.cfg.centerX, y: this.cfg.centerY }, this.cfg.effectSpeed!, '<>');

        node.animate({ cx: this.cfg.centerX, cy: this.cfg.centerY }, this.cfg.effectSpeed!, '<>',
            () => {
                this.createSubNodes(node);
            });

        //remove event handlers
        node.unclick(this.clickNode);
        text.unclick(this.clickNodeText);
        node.attr({ cursor: 'default' });
        text.attr({ cursor: 'default' });
    }

    /**
     * Creates the mindmap nodes for all immediate children of the given node.
     * @param node A node with children.
     */
    private createSubNodes(node: PrimeFacesTypes.widget.Mindmap.MindmapElement): void {
        const nodeModel: PrimeFacesTypes.widget.Mindmap.MindmapNode = node.data('model');

        if (nodeModel.children) {
            const size = nodeModel.children.length;
            let radius = 150,
                capacity = parseInt(String((radius * 2) / 25)),
                angleFactor = (360 / Math.min(size, capacity)),
                capacityCounter = 0;

            //children
            for (let i = 0; i < size; i++) {
                const childModel = nodeModel.children[i]!;
                capacityCounter++;

                //coordinates
                const angle = ((angleFactor * (i + 1)) / 180) * Math.PI,
                    x = node.attr('cx')! + radius * Math.cos(angle),
                    y = node.attr('cy')! + radius * Math.sin(angle);

                const childNode = this.createNode(x, y, childModel);

                //connection
                const connection = this.raphael.connection(node, childNode, "#000", null, this.cfg.effectSpeed!);
                node.data('connections').push(connection);
                childNode.data('connections').push(connection);

                //new ring
                if (capacityCounter === capacity) {
                    radius = radius + 125;
                    capacity = parseInt(String((radius * 2) / 25));
                    angleFactor = (360 / Math.min(capacity, (size - (i + 1))));
                    capacityCounter = 0;
                }
            }
        }

        //parent
        const parentModel = nodeModel.parent;
        if (parentModel) {
            parentModel.selectable = true;

            const parentNode = this.createNode(60, 40, parentModel);

            //connection
            const parentConnection = this.raphael.connection(node, parentNode, "#000", null, this.cfg.effectSpeed!);
            node.data('connections').push(parentConnection);
            parentNode.data('connections').push(parentConnection);
        }

    }

    /**
     * Callback that is invoked when a click was performed on a mindmap node.
     * @param node The node that received the click.
     */
    private handleNodeClick(node: PrimeFacesTypes.widget.Mindmap.MindmapElement): void {
        if (node.dragged) {
            node.dragged = false;
            return;
        }

        const clickTimeout = node.data('clicktimeout');

        if (clickTimeout) {
            clearTimeout(clickTimeout);
            node.removeData('clicktimeout');

            this.handleDblclickNode(node);
        }
        else {
            const timeout = PrimeFaces.queueTask(() => {
                this.expandNode(node);
            }, 300);

            node.data('clicktimeout', timeout);
        }
    }

    /**
     * Callback that is invoked when a click was performed on a mindmap node.
     * @param this The node.
     */
    private clickNode(this: PrimeFacesTypes.widget.Mindmap.MindmapElement): void {
        const _self = PF(this.data('widget')) as Mindmap;

        _self.handleNodeClick(this);
    }

    /**
     * Callback that is invoked when a click was performed on a text label.
     * @param this The text label.
     */
    private clickNodeText(this: PrimeFacesTypes.widget.Mindmap.MindmapElement): void {
        const node: PrimeFacesTypes.widget.Mindmap.MindmapElement = this.data('node'),
            _self = PF(node.data('widget')) as Mindmap;

        _self.handleNodeClick(node);
    }

    /**
     * Callback that is invoked when a double click was performed on a mindmap node.
     * @param node Node that received the double click.
     */
    private handleDblclickNode(node: PrimeFacesTypes.widget.Mindmap.MindmapElement): void {
        if (this.hasBehavior('dblselect')) {
            const key = node.data('model').key;

            const ext = {
                params: [
                    { name: this.id + '_nodeKey', value: key }
                ]
            };

            this.callBehavior('dblselect', ext);
        }
    }

    /**
     * Expands the given mindmap node, showing it and its children.
     * @param node A node to expand.
     */
    expandNode(node: PrimeFacesTypes.widget.Mindmap.MindmapElement): void {
        const key = node.data('model').key,
            ext: Partial<PrimeFacesTypes.ajax.ConfigurationExtender> = {
                update: this.getId(),
                params: [
                    { name: this.id + '_nodeKey', value: key }
                ],
                onsuccess: (responseXML, status, xhr) => {
                    PrimeFaces.ajax.Response.handle(responseXML, status, xhr, {
                        widget: this,
                        handle: (content) => {
                            const nodeModel = JSON.parse(content);

                            //update model
                            node.data('model', nodeModel);

                            node.data('connections', []);

                            //remove other nodes
                            for (let j = 0; j < this.nodes.length; j++) {
                                const otherNode = this.nodes[j]!,
                                    nodeKey = otherNode.data('model').key;

                                if (nodeKey !== key) {
                                    this.removeNode(otherNode);
                                }
                            }

                            this.nodes = [];
                            this.nodes.push(node);

                            this.centerNode(node);
                        }
                    });

                    return true;
                }
            };

        this.callBehavior('select', ext);
    }

    /**
     * Removes the given node and all of its connections from this mindmap.
     * @param node Mindmap node to delete.
     */
    removeNode(node: PrimeFacesTypes.widget.Mindmap.MindmapElement): void {
        //test
        node.data('text').remove();

        //connections
        const connections: MindmapConnection[] = node.data('connections');
        for (let i = 0; i < connections.length; i++) {
            connections[i]!.line.remove();
        }

        //data
        node.removeData();

        //ellipse
        node.animate({ opacity: 0 }, this.cfg.effectSpeed!, undefined, function() {
            this.remove();
        });
    }

    /**
     * Callback that is invoked once at the start when a node is dragged.
     * @param this The node.
     */
    private nodeDragStart(this: PrimeFacesTypes.widget.Mindmap.MindmapElement): void {
        this.ox = this.attr("cx");
        this.oy = this.attr("cy");
    }

    /**
     * Callback that is invoked while a node is being dragged. Updates the UI.
     * @param this The node.
     * @param dx Amount the node was dragged horizontally since the last call of this callback
     * @param dy Amount the node was dragged vertically since the last call of this callback
     */
    private nodeDrag(this: PrimeFacesTypes.widget.Mindmap.MindmapElement, dx: number, dy: number): void {
        //update location
        this.attr({ cx: this.ox! + dx, cy: this.oy! + dy });

        //drag text
        this.data('text').attr({ x: this.attr('cx'), y: this.attr('cy') });

        //update connections
        const _self = PF(this.data('widget')) as Mindmap;
        _self.updateConnections(this);

        //flag to prevent drag to invoke nodeClick
        this.dragged = true;
    }

    /**
     * Callback invoked after a node was dragged.
     * @param this The node.
     */
    private nodeDragEnd(this: PrimeFacesTypes.widget.Mindmap.MindmapElement): void {
    }

    /**
     * Callback that is invoked once at the start when a text label is dragged.
     * @param this The text label.
     */
    private textDragStart(this: PrimeFacesTypes.widget.Mindmap.MindmapElement): void {
        this.ox = this.attr("x");
        this.oy = this.attr("y");
    }

    /**
     * Callback that is invoked while a text label is being dragged. Updates the UI.
     * @param this The text label.
     * @param dx Amount the text was dragged horizontally since the last call of this callback
     * @param dy Amount the text was dragged vertically since the last call of this callback
     */
    private textDrag(this: PrimeFacesTypes.widget.Mindmap.MindmapElement, dx: number, dy: number): void {
        const node: PrimeFacesTypes.widget.Mindmap.MindmapElement = this.data('node');

        //update location
        this.attr({ x: this.ox! + dx, y: this.oy! + dy });

        //drag node
        node.attr({ cx: this.attr('x'), cy: this.attr('y') });

        //update connections
        const _self = PF(node.data('widget')) as Mindmap;
        _self.updateConnections(node);

        //flag to prevent drag to invoke nodeClick
        node.dragged = true;
    }

    /**
     * Callback invoked after a text label was dragged.
     * @param this The text label.
     */
    private textDragEnd(this: PrimeFacesTypes.widget.Mindmap.MindmapElement): void {
    }

    /**
     * Updates the connections for the given mindmap node.
     * @param node The node for which to update the connections.
     */
    private updateConnections(node: PrimeFacesTypes.widget.Mindmap.MindmapElement): void {
        const connections: MindmapConnection[] = node.data('connections');

        for (let i = 0; i < connections.length; i++) {
            this.raphael.connection(connections[i]!);
        }
    }
}
