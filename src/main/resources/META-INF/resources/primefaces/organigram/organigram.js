PrimeFaces.widget.Organigram = PrimeFaces.widget.BaseWidget.extend({

    init: function(cfg) {
        this._super(cfg);

        this.draw();
    },

    draw : function() {
        this.source = this.jq.children('ul');
        this.target = this.jq.children('div');
        this.target.empty();

        this.drawNode(this.source.find('li:first').data("rowkey"), this.source.find('li:first'), this.target, 0, 0);

        this.setupSelection();
        this.setupDragAndDrop();
        this.setupControls();
    },

    setupSelection : function() {
        var widget = this;

        var selectableNodes = this.target.find(".ui-organigram-node.selectable");
        selectableNodes.on("click", function() {
            widget.selectNode(widget, $(this), "select");
        });

        if (this.cfg.autoScrollToSelection === true) {
            this.scrollToSelection();
        }
    },

    selectNode : function(widget, node, event) {
        if (!node.hasClass("selected")) {
            widget.target.find(".ui-organigram-node.selected").removeClass("selected");
            node.addClass("selected");

            if (widget.hasBehavior(event)) {
                var options = {
                    params: [
                                { name: widget.id + "_selectNode", value: node.data("rowkey") }
                            ]
                };
                widget.cfg.behaviors[event].call(widget, options);
            }
        }
    },

    setupDragAndDrop : function() {
        var widget = this;

        var nodes = this.target.find(".ui-organigram-node");
        var draggableNodes = nodes.filter(".draggable");
        var droppableNodes = nodes.filter(".droppable");

        var initialDragPosition = {
            x: 0,
            y: 0
        };
        draggableNodes.draggable({
            zIndex: 100,
            opacity: 0.7,
            cursor: "move",
            helper: "clone",
            distance: 60,
            revert: "invalid",
            revertDuration: 200,
            snapMode: "inner",
            // overwrite start/drag to support browser scaling/zooming
            start: function(event) {
                initialDragPosition.x = event.clientX;
                initialDragPosition.y = event.clientY;
            },
            drag: function(event, ui) {
                var original = ui.originalPosition;
                var zoomFactor = 1.0;
                if (widget.zoomFactor) {
                    zoomFactor = widget.zoomFactor;
                }
                ui.position = {
                    left: (event.clientX - initialDragPosition.x + original.left) / zoomFactor,
                    top:  (event.clientY - initialDragPosition.y + original.top ) / zoomFactor
                };
            }
        });

        droppableNodes.droppable({
            accept: ".ui-organigram-node.draggable",
            activeClass: "drag-active",
            hoverClass: "drop-hover"
        });

        // drop & drop happens in the target dom
        // but moving the actual nodes happens in the source li/ul dom
        // after dropping, we redraw the organigram from the source
        droppableNodes.on("drop", function (event, ui) {

            // lookup target node in source DOM
            var targetId = $(this).data("rowkey");
            var targetLi = widget.source.find("li").filter(function () {
                return $(this).data("rowkey") === targetId;
            });
            var targetUl = targetLi.children("ul");

            // lookup source node in source DOM
            var sourceId = ui.draggable.data("rowkey");
            var sourceLi = widget.source.find("li").filter(function () {
                return $(this).data("rowkey") === sourceId;
            });
            var sourceUl = sourceLi.parent("ul");

            // ignore the current drop?
            var ignore = false;

            // ignore moving to the current parent
            if (sourceLi.data("parent-rowkey") === targetId)
            {
                ignore = true;
            }

            // ignore moving to the direct child
            if (targetLi.data("parent-rowkey") === sourceId)
            {
                ignore = true;
            }

            // ignore moving to childs
            targetLi.parents().each(function() {
                if ($(this).data("parent-rowkey") === sourceId) {
                    ignore = true;
                    return false;
                }
            });

            if (ignore) {
                return false;
            }

            // add new children
            if (targetUl.length > 0) {
                targetUl.append(sourceLi);
            }
            else {
                targetLi.append("<ul></ul>");
                targetLi.children("ul").append(sourceLi);
            }

            // remove children if empty
            if (sourceUl.children().length === 0) {
                sourceUl.remove();
            }

            // add "leaf" class if the last item was removed from the parent
            var oldParent = widget.source.find("li").filter(function () {
                return $(this).data("rowkey") === sourceLi.data("parent-rowkey");
            });
            if (oldParent.children("ul").length === 0) {
                oldParent.addClass("leaf");
            }

            // remove leave class if a node was added to a leaf node
            targetLi.removeClass("leaf");

            // update parent
            sourceLi.data("parent-rowkey", targetId);
            sourceLi.attr("data-parent-rowkey", targetId);

            // call behavior
            if (widget.hasBehavior("dragdrop")) {
                var options = {
                    params: [
                                { name: widget.id + "_dragNode", value: sourceId },
                                { name: widget.id + "_dropNode", value: targetId }
                            ]
                };
                widget.cfg.behaviors["dragdrop"].call(widget, options);
            }

            widget.redraw = true;
        });

        // redraw from source after drop
        draggableNodes.on("dragstop", function (event, ui) {
            // redraw only if the item was dropped successfully
            if (widget.redraw) {
                widget.draw();

                widget.redraw = false;
            }
        });
    },

    /**
     * Setup global controls.
     * Currently zoom-in and zoom-out.
     */
    setupControls : function() {
        var widget = this;

        if (this.cfg.zoom) {

            this.jq.children(".controls").remove();

            var controls = $("<div class='controls'></div>").appendTo(this.jq);
            var controlsTable = $("<table></table>").appendTo(controls);

            if (!this.zoomFactor) {
                this.zoomFactor = 1.0;
            }
            else {
                this.zoom(this.zoomFactor);
            }

            var zoomIn = $("<tr><td><div class='control zoom-in' title='Zoom In'>&nbsp;</div></td></tr>").appendTo(controlsTable);
            zoomIn.on("click", function() {
                widget.zoomFactor += 0.1;
                widget.zoom(widget.zoomFactor);
            });

            var zoomOut = $("<tr><td><div class='control zoom-out' title='Zoom Out'>&nbsp;</div></td></tr>").appendTo(controlsTable);
            zoomOut.on("click", function() {
                widget.zoomFactor -= 0.1;
                widget.zoom(widget.zoomFactor);
            });
        }
    },

    /**
     * Applies css zoom/scale to the target DOM.
     *
     * @param {type} zoom The zoom factor. (e.g. 1.0 or 0.5)
     */
    zoom : function(zoom) {
        var element = this.target.find(">:first-child");

        // avoid both zoom and transform in IE, otherwise it will be zoomed * 2
        if (PrimeFaces.env.isIE()) {
            element.css("zoom", zoom);
        }
        else {
            element.css("-moz-transform", "scale(" + zoom + ")");
            element.css("-moz-transform-origin", "0 0");
            element.css("-o-transform", "scale(" + zoom + ")");
            element.css("-o-transform-origin", "0 0");
            element.css("-webkit-transform", "scale(" + zoom + ")");
            element.css("-webkit-transform-origin", "0 0");
            element.css("transform", "scale(" + zoom + ")");
            element.css("transform-origin", "0 0");
        }
    },

    drawNode : function(parentRowKey, nodeSource, appendTo, level) {

        var childNodes = nodeSource.children("ul:first").children("li");
        var isLastLevel = childNodes.length === 0;

        // clone node content - ignore subnodes
        var nodeContent = nodeSource.clone()
                .children("ul,li")
                .remove()
                .end()
                .html();

        var node = $("<div>");

        // copy class and style from source
        node.attr("class", nodeSource.attr("class"));
        node.attr("style", nodeSource.attr("style"));

        // styling
        node.addClass("ui-organigram-node");
        node.addClass("level-" + level);

        // set metadata
        node.attr("data-level", level);
        node.attr("data-rowkey", nodeSource.data("rowkey"));
        node.attr("data-parent-rowkey", parentRowKey);

        // top icons
        var topIconContainer = $("<div class='ui-organigram-icon-container'></div>").appendTo(node);
        if (nodeSource.data("icon")) {
            var icon = $("<div class='ui-organigram-icon ui-icon'></div>").appendTo(topIconContainer);
            icon.addClass(nodeSource.data("icon"));

            var iconPos = nodeSource.data("icon-pos");
            if (iconPos && (iconPos === "left" || iconPos === "right")) {
                icon.addClass(nodeSource.data("icon-pos"));
            }
        }

        // content
        node.append(nodeContent);

        // bottom icons
        var bottomIconContainer = $("<div class='ui-organigram-icon-container'></div>").appendTo(node);

        // we don't need to render a table in the last level as it can't have further childs
        if (isLastLevel) {
            appendTo.append(node);
        }
        else if (childNodes.length > 0) {
            var table = $("<table cellpadding='0' cellspacing='0' border='0'/>").appendTo(appendTo);
            var row = $("<tr/>").appendTo(table);

            var leafChildNodes = childNodes.filter(".leaf:not(.skip-leaf)");
            var nonLeafChildNodes = childNodes.filter(":not(.leaf),.skip-leaf");
            var childNodeCount = nonLeafChildNodes.length;
            if (leafChildNodes && leafChildNodes.length > 0) {
                childNodeCount += 1;
            }

            var cell = $("<td colspan='" + (childNodeCount * 2) + "'/>").appendTo(row);
            cell.append(node);

            this.addExpander(nodeSource, node, bottomIconContainer);
            this.drawLines(childNodeCount, table);
            this.drawChildNodes(nodeSource.data("rowkey"), leafChildNodes, nonLeafChildNodes, table, level);

            // handle initial collapsed state
            if (nodeSource.hasClass("collapsed")) {
                var collapsedIcon = "ui-icon-plusthick";
                if (nodeSource.data("collapsed-icon")) {
                    collapsedIcon = nodeSource.data("collapsed-icon");
                }
                var expandedIcon = "ui-icon-minusthick";
                if (nodeSource.data("expanded-icon")) {
                    expandedIcon = nodeSource.data("expanded-icon");
                }

                // hide childs
                row.nextAll("tr").hide();

                // switch expander icons
                node.find(".expander").removeClass(expandedIcon).addClass(collapsedIcon);
            }
            else {
                if (!nodeSource.hasClass("expanded")) {
                    node.addClass("expanded");
                }
            }
        }
    },

    addExpander : function(nodeSource, node, bottomIconContainer) {

        if (node.hasClass("collapsible")) {

            var collapsedIcon = nodeSource.data("collapsed-icon") ? nodeSource.data("collapsed-icon") : "ui-icon-plusthick";
            var expandedIcon = nodeSource.data("expanded-icon") ? nodeSource.data("expanded-icon") : "ui-icon-minusthick";
            var initialIcon = node.hasClass("collapsed") ? collapsedIcon : expandedIcon;

            var widget = this;
            var expander = $("<div class='expander ui-icon " + initialIcon + "'>&nbsp;</div>").appendTo(bottomIconContainer);

            expander.click(function (e) {
                // reinit dom references - they are lost sometimes
                var expander = $(this);
                var node = expander.closest(".ui-organigram-node");
                var row = node.closest("tr");

                if (node.hasClass("collapsed")) {
                    node.removeClass("collapsed").addClass("expanded");
                    expander.removeClass(collapsedIcon).addClass(expandedIcon);

                    row.nextAll("tr").show();

                    // maintain state in source
                    nodeSource.removeClass("collapsed");

                    // call behavior
                    if (widget.hasBehavior("expand")) {
                        var options = {
                            params: [
                                        { name: widget.id + "_expandNode", value: node.data("rowkey") }
                                    ]
                        };
                        widget.cfg.behaviors["expand"].call(widget, options);
                    }
                }
                else {
                    node.removeClass("expanded").addClass("collapsed");
                    expander.removeClass(expandedIcon).addClass(collapsedIcon);

                    row.nextAll("tr").hide();

                    // maintain state in source
                    nodeSource.addClass("collapsed");

                    // call behavior
                    if (widget.hasBehavior("collapse")) {
                        var options = {
                            params: [
                                        { name: widget.id + '_collapseNode', value: node.data("rowkey") }
                                    ]
                        };
                        widget.cfg.behaviors["collapse"].call(widget, options);
                    }
                }

                // avoid bubbling to the parent select click handler
                e.stopPropagation();
            });
        }
    },

    drawLines : function(childNodeCount, table) {

        // draw vertical row
        var verticalColspan = childNodeCount * 2;
        var verticalRow = $("<tr/>").appendTo(table);
        var verticalCell = $("<td colspan='" + verticalColspan + "'/>").appendTo(verticalRow);
        verticalCell.append($("<div class='line down'></div>"));


        // draw horizontal row/cells
        var horizontalRow = $("<tr/>").appendTo(table);
        for (var i = 0; i < childNodeCount; i++) {
            horizontalRow.append($("<td class='line left top'></td>"));
            horizontalRow.append($("<td class='line right top'></td>"));
        }

        // remove the line from the first and last cell
        horizontalRow.find("td:first").removeClass("top");
        horizontalRow.find("td:last").removeClass("top");
    },

    drawChildNodes : function(parentRowKey, leafChildNodes, nonLeafChildNodes, table, level) {
        var row = $("<tr/>").appendTo(table);

        // draw leaf nodes in a different way
        if (leafChildNodes && leafChildNodes.length > 0) {
            var cell = $("<td colspan='2'/>").appendTo(row);

            var leafTable = $("<table cellpadding='0' cellspacing='0' border='0'/>").appendTo(cell);

            for (var j = 0; j < leafChildNodes.length; j++) {
                // add connector line
                if (j !== 0 && this.cfg.leafNodeConnectorHeight > 0) {
                    leafTable.append($("<tr><td><div class='line down' style='height:" + this.cfg.leafNodeConnectorHeight + "px'></div></td></tr>"));
                }

                var leafRow = $("<tr/>").appendTo(leafTable);
                var leafCell = $("<td/>").appendTo(leafRow);

                var childNode = $(leafChildNodes[j]);
                this.drawNode(parentRowKey, childNode, leafCell, level + 1);
            }
        }

        // draw normal nodes
        for (var i = 0; i < nonLeafChildNodes.length; i++) {
            var cell = $("<td colspan='2'/>").appendTo(row);

            var childNode = $(nonLeafChildNodes[i]);
            this.drawNode(parentRowKey, childNode, cell, level + 1);
        }
    },

    // contextMenu integration
    bindContextMenu : function(menuWidget, targetWidget, targetId, cfg) {
        var selector = targetId + " .ui-organigram-node.selectable",
        event = cfg.event + ".organigram";

        if (cfg.nodeType) {
            selector += "." + cfg.nodeType;
        }

        $(document).off(event, selector).on(event, selector, null, function(e) {
            targetWidget.selectNode(targetWidget, $(this), "contextmenu");
            menuWidget.show(e);
        });
    },

    scrollToSelection : function() {
        var selection = this.target.find(".ui-organigram-node.selected");
        if (selection.length > 0) {
            var offset = selection.offset();
            this.target.animate({
                scrollTop: offset.top ,
                scrollLeft: offset.left
            },{
                easing: 'easeInCirc'
            },1000);
        }
    }
});