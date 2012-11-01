/**
 * PrimeFaces Mindmap Widget
 */
PrimeFaces.widget.Mindmap = PrimeFaces.widget.BaseWidget.extend({
    
    init: function(cfg) {
        this._super(cfg);
        
        var $this = this;
        
        if(this.jq.is(':visible')) {
            this.render();
        } 
        else {
            var hiddenParent = this.jq.parents('.ui-hidden-container:first'),
            hiddenParentWidget = hiddenParent.data('widget');

            if(hiddenParentWidget) {
                hiddenParentWidget.addOnshowHandler(function() {
                    return $this.render();
                });
            }
        }
    },
    
    render: function() {
        this.cfg.width = this.jq.width();
        this.cfg.height = this.jq.height();
        this.cfg.centerX = this.cfg.width / 2;
        this.cfg.centerY = this.cfg.height / 2;
        this.raphael = new Raphael(this.id, this.cfg.width, this.cfg.height);
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
    
    createNode: function(x, y, model) {
        var node = this.raphael.ellipse(x, y, 40, 25).attr('opacity', 0)
                            .data('model', model)
                            .data('connections', [])
                            .data('widget', this);
                            
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
            
            node.mouseover(this.mouseoverNode);
            node.mouseout(this.mouseoutNode);
            
            text.mouseover(this.mouseoverText);
            text.mouseout(this.mouseoutText);
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
    
    mouseoverNode: function() {
        var _self = this.data('widget');
        
        _self.showTooltip(this);
    },
    
    mouseoutNode: function() {
        var _self = this.data('widget');
        
        _self.hideTooltip(this);
    },
    
    mouseoverText: function() {
        var node = this.data('node'),
        _self = node.data('widget');
        
        _self.showTooltip(node);
    },
    
    mouseoutText: function() {
        var node = this.data('node'),
        _self = node.data('widget');
        
        _self.hideTooltip(node);
    },
    
    showTooltip: function(node) {
        var title = node.data('title');
        
        if(title) {
            var _self = node.data('widget'),
            offset = _self.jq.offset();
            
            _self.tooltip.text(title)
                        .css(
                            {
                                'left': offset.left + node.attr('cx') + 20,
                                'top':offset.top + node.attr('cy') + 10,
                                'z-index': ++PrimeFaces.zindex
                            })
                        .show();
        }
        
        
    },
    
    hideTooltip: function(node) {
        var title = node.data('title');
        
        if(title) {
            var _self = node.data('widget');
            
            _self.tooltip.hide();
        }
    },
    
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

    hasBehavior: function(event) {
        if(this.cfg.behaviors) {
            return this.cfg.behaviors[event] != undefined;
        }
    
        return false;
    },
    
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
    
    clickNode: function() {
        var _self = this.data('widget');
        
        _self.handleNodeClick(this);
    },
    
    clickNodeText: function() {
        var node = this.data('node'),
        _self = node.data('widget');
        
        _self.handleNodeClick(node);
    },
    
    handleDblclickNode: function(node) {
        if(this.hasBehavior('dblselect')) {
            var dblselectBehavior = this.cfg.behaviors['dblselect'],
            key = node.data('model').key;

            var ext = {
                params: [
                    {name: this.id + '_nodeKey', value: key}
                ]
            };

            dblselectBehavior.call(this, node, ext);
        }
    },
    
    expandNode: function(node) {
        var _self = this,
        key = node.data('model').key;

        var selectBehavior = this.cfg.behaviors['select'],
        ext = {
            params: [
                {name: this.id + '_nodeKey', value: key}
            ]
            ,update: this.id
            ,onsuccess: function(responseXML) {
                var xmlDoc = $(responseXML.documentElement),
                updates = xmlDoc.find("update");

                for(var i=0; i < updates.length; i++) {
                    var update = updates.eq(i),
                    id = update.attr('id'),
                    content = update.text();

                    if(id == _self.id){
                        var nodeModel = $.parseJSON(content);

                        //update model
                        node.data('model', nodeModel);
                        
                        node.data('connections', []);

                        //remove other nodes
                        for(var j = 0; j < _self.nodes.length; j++) {
                            var otherNode = _self.nodes[j],
                            nodeKey = otherNode.data('model').key;

                            if(nodeKey != key) {
                                _self.removeNode(otherNode);
                            }
                        }

                        _self.nodes = [];
                        _self.nodes.push(node);

                        _self.centerNode(node);
                    }
                    else {
                        PrimeFaces.ajax.AjaxUtils.updateElement.call(this, id, content);
                    }
                }

                PrimeFaces.ajax.AjaxUtils.handleResponse.call(this, xmlDoc);

                return true;
            }
        };

        selectBehavior.call(_self, this, ext);      
    },
        
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
    
    nodeDragStart: function () {
        this.ox = this.attr("cx");
        this.oy = this.attr("cy");
    },
    
    nodeDrag: function(dx, dy) {
        //update location
        this.attr({cx: this.ox + dx, cy: this.oy + dy});
        
        //drag text
        this.data('text').attr({x: this.attr('cx'), y: this.attr('cy')});
        
        //update connections
        var _self = this.data('widget');
        _self.updateConnections(this);
        
        //flag to prevent drag to invoke nodeClick
        this.dragged = true;
    },
    
    nodeDragEnd: function () {
    },
    
    textDragStart: function () {
        this.ox = this.attr("x");
        this.oy = this.attr("y");        
    },
    
    textDrag: function(dx, dy) {
        var node = this.data('node');
        
        //update location
        this.attr({x: this.ox + dx, y: this.oy + dy});
        
        //drag node
        node.attr({cx: this.attr('x'), cy: this.attr('y')});
        
        //update connections
        var _self = node.data('widget');
        _self.updateConnections(node);
        
        //flag to prevent drag to invoke nodeClick
        node.dragged = true;
    },
    
    textDragEnd: function () {
    }
    
    ,updateConnections: function(node) {
        var connections = node.data('connections');
        
        for(var i = 0; i < connections.length; i++) {
            this.raphael.connection(connections[i]);
        }
        
        this.raphael.safari();
    }
});

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