/**
 * __PrimeFaces Mindmap Widget__
 * 
 * Mindmap is an interactive tool to visualize mindmap data featuring lazy loading, callbacks, animations and more.
 * 
 * @interface {PrimeFaces.widget.Mindmap.MindmapNode} MindmapNode Model that describes a node of the mindmap, such as
 * its parent and children, its label, its geometry etc.
 * @prop {PrimeFaces.widget.Mindmap.MindmapNode[]} MindmapNode.children The children of this node.
 * @prop {PrimeFaces.widget.Mindmap.MindmapNode | null} MindmapNode.parent The parent of this node, or `null` if it has
 * got no parent.
 * @prop {string} MindmapNode.label The label text of this node.
 * @prop {string} [MindmapNode.key] The unique ID of this node.
 * @prop {string} [MindmapNode.fill] The fill color of this node.
 * @prop {boolean} [MindmapNode.selectable] `true` if this node can be selected, or `false` otherwise.
 * 
 * @prop {boolean} dragged Whether a node was being dragged.
 * @prop {import("raphael").RaphaelElement[]} nodes A list of all drawn mindmap nodes.
 * @prop {number} ox When a node is being dragged, the original horizontal coordinated where the drag started.
 * @prop {number} oy When a node is being dragged, the original vertical coordinated where the drag started.
 * @prop {import("raphael").RaphaelPaper} raphael The canvas on which the mindmap is drawn.
 * @prop {import("raphael").RaphaelElement} root The drawn root node for the mindmap. 
 * @prop {JQuery} tooltip The DOM element for the tooltip of a mindmap node.
 * 
 * @interface {PrimeFaces.widget.MindmapCfg} cfg The configuration for the {@link  Mindmap| Mindmap widget}.
 * You can access this configuration via {@link PrimeFaces.widget.BaseWidget.cfg|BaseWidget.cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 * @extends {PrimeFaces.widget.DeferredWidgetCfg} cfg
 * 
 * @prop {number} cfg.effectSpeed Duration for all animations with nodes, in milliseconds.
 * @prop {number} cfg.centerX Horizontal coordinate for the center of the canvas.
 * @prop {number} cfg.centerY Vertical coordinate for the center of the canvas.
 * @prop {number} cfg.height Total height of the canvas. 
 * @prop {PrimeFaces.widget.Mindmap.MindmapNode} cfg.model Root node shown by the mindmap.
 * @prop {number} cfg.width Total width of the canvas.
 */
PrimeFaces.widget.Mindmap = PrimeFaces.widget.DeferredWidget.extend({

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    init: function(cfg) {
        this._super(cfg);

        this.renderDeferred();
    },

    /**
     * @include
     * @override
     * @protected
     * @inheritdoc
     */
    _render: function() {
        this.cfg.width = this.jq.width();
        this.cfg.height = this.jq.height();
        this.cfg.centerX = this.cfg.width / 2;
        this.cfg.centerY = this.cfg.height / 2;
        this.raphael = Raphael(this.id, this.cfg.width, this.cfg.height);
        this.nodes = [];

        if(this.cfg.model) {
            //root
            this.root = this.createNode(this.cfg.centerX, this.cfg.centerY, this.cfg.model);

            //children
            if(this.cfg.model.children) {
                this.createSubNodes(this.root);
            }
        }

        this.tooltip = $('<div class="ui-tooltip ui-mindmap-tooltip ui-widget ui-widget-content ui-corner-all"></div>').appendTo(document.body);
    },

    /**
     * Creates a mindmap node at the given position for the given model.
     * @param {number} x Horizontal coordinate where the node is drawn.
     * @param {number} y Vertical coordinate where the node is drawn.
     * @param {PrimeFaces.widget.Mindmap.MindmapNode} model Model with the data describing the node to be created.
     * @return {import("raphael").RaphaelElement} The created node.
     * @private
     */
    createNode: function(x, y, model) {
        var node = this.raphael.ellipse(x, y, 40, 25).attr('opacity', 0)
                            .data('model', model)
                            .data('connections', [])
                            .data('widget', this.cfg.widgetVar);

        var label = model.label,
        nodeWidth = node.getBBox().width,
        title = null;

        var text = this.raphael.text(x, y, label).attr('opacity', 0);

        if(nodeWidth <= text.getBBox().width) {
            title = label;
            label = label.substring(0, 12);
            text.attr('text', label + '...');
        }

        text.data('node', node);
        node.data('text', text);

        //node options
        if(model.fill) {
            node.attr({fill: '#' + model.fill});
        }

        //title
        if(title) {
            node.data('title', title);

            node.on("mouseover", this.mouseoverNode);
            node.on("mouseout", this.mouseoutNode);

            text.on("mouseover", this.mouseoverText);
            text.on("mouseout", this.mouseoutText);
        }

        //show
        node.animate({opacity:1}, this.cfg.effectSpeed);
        text.animate({opacity:1}, this.cfg.effectSpeed);

        //make draggable
        node.drag(this.nodeDrag, this.nodeDragStart, this.nodeDragEnd);
        text.drag(this.textDrag, this.textDragStart, this.textDragEnd);

        //events
        if(model.selectable) {
            node.click(this.clickNode);
            text.click(this.clickNodeText);

            node.attr({cursor:'pointer'});
            text.attr({cursor:'pointer'});
        }

        //add to nodes
        this.nodes.push(node);

        return node;
    },

    /**
     * Callback that is invoked when the mouse cursor was moved over a mindmap node.
     * @private
     */
    mouseoverNode: function() {
        var _self = PF(this.data('widget'));

        _self.showTooltip(this);
    },

    /**
     * Callback that is invoked when the mouse cursor was moved away from a mindmap node.
     * @private
     */
    mouseoutNode: function() {
        var _self = PF(this.data('widget'));

        _self.hideTooltip(this);
    },

    /**
     * Callback that is invoked when the mouse cursor was moved over a text label.
     * @private
     */
    mouseoverText: function() {
        var node = this.data('node'),
        _self = PF(node.data('widget'));

        _self.showTooltip(node);
    },

    /**
     * Callback that is invoked when the mouse cursor was moved away from a text label.
     * @private
     */
    mouseoutText: function() {
        var node = this.data('node'),
        _self = PF(node.data('widget'));

        _self.hideTooltip(node);
    },

    /**
     * Brings up the tooltip for the given node, it it is not shown already.
     * @param {import("raphael").RaphaelElement} node A node for which to show the tooltip. 
     */
    showTooltip: function(node) {
        var title = node.data('title');

        if(title) {
            var _self = PF(node.data('widget')),
            offset = _self.jq.offset();

            _self.tooltip.text(title)
                        .css(
                            {
                                'left': (offset.left + node.attr('cx') + 20) + 'px',
                                'top': (offset.top + node.attr('cy') + 10)  + 'px',
                                'z-index': PrimeFaces.nextZindex()
                            })
                        .show();
        }


    },

    /**
     * Hides the tooltip for the given node, it it is shown.
     * @param {import("raphael").RaphaelElement} node A node for which to hide the tooltip. 
     */
    hideTooltip: function(node) {
        var title = node.data('title');

        if(title) {
            var _self = PF(node.data('widget'));

            _self.tooltip.hide();
        }
    },

    /**
     * Centers the given node so that it is positioned near the center of the mindmap viewport.
     * @param {import("raphael").RaphaelElement} node A node to center. 
     */
    centerNode: function(node) {
        var _self = this,
        text = node.data('text');

        text.animate({x: this.cfg.centerX, y: this.cfg.centerY}, this.cfg.effectSpeed, '<>');

        node.animate({cx: this.cfg.centerX, cy: this.cfg.centerY}, this.cfg.effectSpeed, '<>',
            function() {
                _self.createSubNodes(node);
            });

        //remove event handlers
        node.unclick(this.clickNode);
        text.unclick(this.clickNodeText);
        node.attr({cursor:'default'});
        text.attr({cursor:'default'});
    },

    /**
     * Creates the mindmap nodes for all immediate children of the given node.
     * @param {import("raphael").RaphaelElement} node A node with children.
     * @private
     */
    createSubNodes: function(node) {
        var nodeModel = node.data('model');

        if(nodeModel.children) {
            var size = nodeModel.children.length,
            radius = 150,
            capacity = parseInt((radius*2) / 25),
            angleFactor = (360 / Math.min(size, capacity)),
            capacityCounter = 0;

            //children
            for(var i = 0 ; i < size; i++) {
                var childModel = nodeModel.children[i];
                capacityCounter++;

                //coordinates
                var angle = ((angleFactor * (i + 1)) / 180) * Math.PI,
                x = node.attr('cx') + radius * Math.cos(angle),
                y = node.attr('cy') + radius * Math.sin(angle);

                var childNode = this.createNode(x, y, childModel);

                //connection
                var connection = this.raphael.connection(node, childNode, "#000", null, this.cfg.effectSpeed);
                node.data('connections').push(connection);
                childNode.data('connections').push(connection);

                //new ring
                if(capacityCounter === capacity) {
                    radius = radius + 125;
                    capacity = parseInt((radius*2) / 25);
                    angleFactor = (360 / Math.min(capacity, (size - (i + 1) )));
                    capacityCounter = 0;
                }
            }
        }

        //parent
        var parentModel = nodeModel.parent;
        if(parentModel) {
            parentModel.selectable = true;

            var parentNode = this.createNode(60, 40, parentModel);

            //connection
            var parentConnection = this.raphael.connection(node, parentNode, "#000", null, this.cfg.effectSpeed);
            node.data('connections').push(parentConnection);
            parentNode.data('connections').push(parentConnection);
        }

    },

    /**
     * Callback that is invoked when a click was performed on a mindmap node.
     * @param {import("raphael").RaphaelElement} node The node that received the click.
     * @private
     */
    handleNodeClick: function(node) {
        if(node.dragged) {
            node.dragged = false;
            return;
        }

        var _self = this,
        clickTimeout = node.data('clicktimeout');

        if(clickTimeout) {
            clearTimeout(clickTimeout);
            node.removeData('clicktimeout');

            _self.handleDblclickNode(node);
        }
        else {
            var timeout = setTimeout(function() {
                _self.expandNode(node);
            }, 300);

            node.data('clicktimeout', timeout);
        }
    },

    /**
     * Callback that is invoked when a click was performed on a mindmap node.
     * @private
     */
    clickNode: function() {
        var _self = PF(this.data('widget'));

        _self.handleNodeClick(this);
    },

    /**
     * Callback that is invoked when a click was performed on a text label.
     * @private
     */
    clickNodeText: function() {
        var node = this.data('node'),
        _self = PF(node.data('widget'));

        _self.handleNodeClick(node);
    },

    /**
     * Callback that is invoked when a double click was performed on a mindmap node.
     * @param {import("raphael").RaphaelElement} node Node that received the double click.
     * @private
     */
    handleDblclickNode: function(node) {
        if(this.hasBehavior('dblselect')) {
            var key = node.data('model').key;

            var ext = {
                params: [
                    {name: this.id + '_nodeKey', value: key}
                ]
            };

            this.callBehavior('dblselect', ext);
        }
    },

    /**
     * Expands the given mindmap node, showing it and its children.
     * @param {import("raphael").RaphaelElement} node A node to expand. 
     */
    expandNode: function(node) {
        var $this = this,
        key = node.data('model').key,
        ext = {
            update: this.id,
            params: [
                {name: this.id + '_nodeKey', value: key}
            ],
            onsuccess: function(responseXML, status, xhr) {
                PrimeFaces.ajax.Response.handle(responseXML, status, xhr, {
                        widget: $this,
                        handle: function(content) {
                            var nodeModel = JSON.parse(content);

                            //update model
                            node.data('model', nodeModel);

                            node.data('connections', []);

                            //remove other nodes
                            for(var j = 0; j < this.nodes.length; j++) {
                                var otherNode = this.nodes[j],
                                nodeKey = otherNode.data('model').key;

                                if(nodeKey !== key) {
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
    },

    /**
     * Removes the given node and all of its connections from this mindmap.
     * @param {import("raphael").RaphaelElement} node Mindmap node to delete.
     */
    removeNode: function(node) {
        //test
        node.data('text').remove();

        //connections
        var connections = node.data('connections');
        for(var i = 0; i < connections.length; i++) {
            connections[i].line.remove();
        }

        //data
        node.removeData();

        //ellipse
        node.animate({opacity:0}, this.cfg.effectSpeed, null, function() {
            this.remove();
        });
    },

    /**
     * Callback that is invoked once at the start when a node is dragged.
     * @private
     */
    nodeDragStart: function () {
        this.ox = this.attr("cx");
        this.oy = this.attr("cy");
    },

    /**
     * Callback that is invoked while a node is being dragged. Updates the UI.
     * @param {number} dx Amount the node was dragged horizontally since the last call of this callback 
     * @param {number} dy Amount the node was dragged vertically since the last call of this callback
     * @private
     */
    nodeDrag: function(dx, dy) {
        //update location
        this.attr({cx: this.ox + dx, cy: this.oy + dy});

        //drag text
        this.data('text').attr({x: this.attr('cx'), y: this.attr('cy')});

        //update connections
        var _self = PF(this.data('widget'));
        _self.updateConnections(this);

        //flag to prevent drag to invoke nodeClick
        this.dragged = true;
    },

    /**
     * Callback invoked after a node was dragged.
     * @private
     */
    nodeDragEnd: function () {
    },

    /**
     * Callback that is invoked once at the start when a text label is dragged.
     * @private
     */
    textDragStart: function () {
        this.ox = this.attr("x");
        this.oy = this.attr("y");
    },

    /**
     * Callback that is invoked while a text label is being dragged. Updates the UI.
     * @param {number} dx Amount the text was dragged horizontally since the last call of this callback 
     * @param {number} dy Amount the text was dragged vertically since the last call of this callback
     * @private
     */
    textDrag: function(dx, dy) {
        var node = this.data('node');

        //update location
        this.attr({x: this.ox + dx, y: this.oy + dy});

        //drag node
        node.attr({cx: this.attr('x'), cy: this.attr('y')});

        //update connections
        var _self = PF(node.data('widget'));
        _self.updateConnections(node);

        //flag to prevent drag to invoke nodeClick
        node.dragged = true;
    },

    /**
     * Callback invoked after a text label was dragged.
     * @private
     */
    textDragEnd: function () {
    },

    /**
     * Updates the connections for the given mindmap node.
     * @param {import("raphael").RaphaelElement} node The node for which to update the connections.
     * @private
     */
    updateConnections: function(node) {
        var connections = node.data('connections');

        for(var i = 0; i < connections.length; i++) {
            this.raphael.connection(connections[i]);
        }

        this.raphael.safari();
    }
});

// Documented in mindmap.d.ts
Raphael.fn.connection = function (obj1, obj2, line, bg, effectSpeed) {
    if (obj1.line && obj1.from && obj1.to) {
        line = obj1;
        obj1 = line.from;
        obj2 = line.to;
    }
    var bb1 = obj1.getBBox(),
        bb2 = obj2.getBBox(),
        p = [{x: bb1.x + bb1.width / 2, y: bb1.y - 1},
        {x: bb1.x + bb1.width / 2, y: bb1.y + bb1.height + 1},
        {x: bb1.x - 1, y: bb1.y + bb1.height / 2},
        {x: bb1.x + bb1.width + 1, y: bb1.y + bb1.height / 2},
        {x: bb2.x + bb2.width / 2, y: bb2.y - 1},
        {x: bb2.x + bb2.width / 2, y: bb2.y + bb2.height + 1},
        {x: bb2.x - 1, y: bb2.y + bb2.height / 2},
        {x: bb2.x + bb2.width + 1, y: bb2.y + bb2.height / 2}],
        d = {}, dis = [];
    for (var i = 0; i < 4; i++) {
        for (var j = 4; j < 8; j++) {
            var dx = Math.abs(p[i].x - p[j].x),
                dy = Math.abs(p[i].y - p[j].y);
            if ((i == j - 4) || (((i != 3 && j != 6) || p[i].x < p[j].x) && ((i != 2 && j != 7) || p[i].x > p[j].x) && ((i != 0 && j != 5) || p[i].y > p[j].y) && ((i != 1 && j != 4) || p[i].y < p[j].y))) {
                dis.push(dx + dy);
                d[dis[dis.length - 1]] = [i, j];
            }
        }
    }
    if (dis.length == 0) {
        var res = [0, 4];
    } else {
        res = d[Math.min.apply(Math, dis)];
    }
    var x1 = p[res[0]].x,
        y1 = p[res[0]].y,
        x4 = p[res[1]].x,
        y4 = p[res[1]].y;
    dx = Math.max(Math.abs(x1 - x4) / 2, 10);
    dy = Math.max(Math.abs(y1 - y4) / 2, 10);
    var x2 = [x1, x1, x1 - dx, x1 + dx][res[0]].toFixed(3),
        y2 = [y1 - dy, y1 + dy, y1, y1][res[0]].toFixed(3),
        x3 = [0, 0, 0, 0, x4, x4, x4 - dx, x4 + dx][res[1]].toFixed(3),
        y3 = [0, 0, 0, 0, y1 + dy, y1 - dy, y4, y4][res[1]].toFixed(3);
    var path = ["M", x1.toFixed(3), y1.toFixed(3), "C", x2, y2, x3, y3, x4.toFixed(3), y4.toFixed(3)].join(",");
    if (line && line.line) {
        line.bg && line.bg.attr({path: path});
        line.line.attr({path: path});
    } else {
        var color = typeof line == "string" ? line : "#000",
        path = this.path(path).attr({stroke: color, fill: "none"}).attr('opacity', 0).animate({opacity:1}, effectSpeed);
        path.toBack();

        return {
            bg: bg && bg.split && this.path(path).attr({stroke: bg.split("|")[0], fill: "none", "stroke-width": bg.split("|")[1] || 3}),
            line: path,
            from: obj1,
            to: obj2
        };
    }
};