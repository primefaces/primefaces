/**
 * PrimeFaces Mindmap Widget
 */
PrimeFaces.widget.Mindmap = PrimeFaces.widget.BaseWidget.extend({
    
    init: function(cfg) {
        this._super(cfg);
        this.width = this.jq.width();
        this.height = this.jq.height();
        this.centerX = this.width / 2;
        this.centerY = this.height / 2;
        this.raphael = new Raphael(this.id, this.width, this.height);
        this.connections = [];
        
        if(this.cfg.model) {            
            //root
            this.root = this.raphael.ellipse(this.centerX, this.centerY, 30, 20)
                            .data('model', this.cfg.model)
                            .data('connections', [])
                            .data('widget', this);
                            
            if(this.cfg.model.fill) {
                this.root.attr({fill: '#' + this.cfg.model.fill});
            }
                            
            this.root.drag(this.move, this.dragger, this.up);
                            
            this.raphael.text(this.centerX, this.centerY, this.cfg.model.data);
            
            //children
            if(this.cfg.model.children) {
                this.createSubNodes(this.root);
            }
        }
    },
    
    createSubNodes: function(node) {        
        var nodeModel = node.data('model'),
        size = nodeModel.children.length,
        angleFactor = (360 / size);

        for(var i = 0 ; i < size; i++) { 
            var childModel = nodeModel.children[i];

            //coordinates
            var angle = ((angleFactor * (i + 1)) / 180) * Math.PI,
            x = node.attr('cx') + 100 * Math.cos(angle),
            y = node.attr('cy') + 100 * Math.sin(angle);

            var childNode = this.raphael.ellipse(x, y, 30, 20)
                        .data('model', childModel)
                        .data('connections', [])
                        .data('parent', node)
                        .data('widget', this);

            if(childModel.fill) {
                childNode.attr({fill: '#' + childModel.fill});
            }

            //label of node
            var nodeText = this.raphael.text(x, y, childModel.data);

            //drag support
            childNode.drag(this.move, this.dragger, this.up);
            
            //connection
            var connection = this.raphael.connection(node, childNode, "#000");
            node.data('connections').push(connection);
            childNode.data('connections').push(connection);

            if(childModel.children) {
                this.createSubNodes(childNode);
            }
        }
        
    },
    
    dragger : function () {
        this.ox = this.type == "rect" ? this.attr("x") : this.attr("cx");
        this.oy = this.type == "rect" ? this.attr("y") : this.attr("cy");
        this.animate({"fill-opacity": .2}, 500);
    },
    
    move: function(dx, dy) {
        var att = this.type == "rect" ? {x: this.ox + dx, y: this.oy + dy} : {cx: this.ox + dx, cy: this.oy + dy};
        this.attr(att);
        
        var widget = this.data('widget'),
        connections = this.data('connections');
        
        for(var i = 0; i < connections.length; i++) {
            widget.raphael.connection(connections[i]);
        }
        
        widget.raphael.safari();
    },
    
    up : function () {
        this.animate({"fill-opacity": 0}, 500);
    }
});

Raphael.fn.connection = function (obj1, obj2, line, bg) {
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
        var color = typeof line == "string" ? line : "#000";
        return {
            bg: bg && bg.split && this.path(path).attr({stroke: bg.split("|")[0], fill: "none", "stroke-width": bg.split("|")[1] || 3}),
            line: this.path(path).attr({stroke: color, fill: "none"}),
            from: obj1,
            to: obj2
        };
    }
};