/**
 * The configuration for the {@link Organigram} widget.
 */
export interface OrganigramCfg extends PrimeFacesTypes.widget.BaseWidgetCfg {
    /** Whether the organigram scrolls to the selected node after it was drawn. */
    autoScrollToSelection: boolean;
    /** Base namespace for the events triggered by this widget. */
    event: string;
    /** The height of the connector line for leaf nodes. */
    leafNodeConnectorHeight: number;
    /** Whether zooming is enabled. */
    zoom: boolean;
}

/**
 * __PrimeFaces Organigram Widget__
 *
 * Organigram is a data component to display an organizational hierarchy.
 */
export class Organigram<Cfg extends OrganigramCfg = OrganigramCfg> extends PrimeFaces.widget.BaseWidget<Cfg> {
    /** Whether the organigram requires redrawing. */
    redraw: boolean = false;
    /** The DOM elements for the source nodes when drawing the lines connecting two nodes. */
    source: JQuery = $();
    /** The DOM elements for the target nodes when drawing the lines connecting two nodes. */
    target: JQuery = $();
    /** The current zoom factor of the organigram. */
    zoomFactor?: number;

    override init(cfg: PrimeFacesTypes.widget.PartialWidgetCfg<Cfg>): void {
        super.init(cfg);

        this.draw();
    }

    /**
     * Draws (renders) the organigram with the current nodes.
     */
    private draw(): void {
        this.source = this.jq.children('ul');
        this.target = this.jq.children('div');
        this.target.empty();

        this.drawNode(this.source.find('li:first').data("rowkey"), this.source.find('li:first'), this.target, 0);

        this.setupSelection();
        this.setupDragAndDrop();
        this.setupControls();
    }

    /**
     * Sets up the event listeners for when a node is selected.
     */
    private setupSelection(): void {
        const selectableNodes = this.target.find(".p-organigram-node.selectable");
        selectableNodes.on("click", (e) => {
            this.selectNode(this, $(e.currentTarget), "select");
        });

        if (this.cfg.autoScrollToSelection === true) {
            this.scrollToSelection();
        }
    }

    /**
     * Selects the given organigram node.
     * @param widget This widget instance.
     * @param node The node to select, with the class `.p-organigram-node`.
     * @param event Name of the event that triggered the selection, usually `select` or `contextmenu`.
     */
    private selectNode(widget: Organigram<Cfg>, node: JQuery, event: string): void {
        if (!node.hasClass("selected")) {
            widget.target.find(".p-organigram-node.selected").removeClass("selected");
            node.addClass("selected");

            if (widget.hasBehavior(event)) {
                const options = {
                    params: [
                                { name: widget.id + "_selectNode", value: node.data("rowkey") }
                            ]
                };
                widget.cfg.behaviors![event]!.call(widget, options);
            }
        }
    }

    /**
     * Sets up the event listeners for dragging and dropping nodes.
     */
    private setupDragAndDrop(): void {
        const nodes = this.target.find(".p-organigram-node");
        const draggableNodes = nodes.filter(".draggable");
        const droppableNodes = nodes.filter(".droppable");

        const initialDragPosition = {
            x: 0,
            y: 0
        };
        PrimeFaces.interactions.draggable(draggableNodes, {
            zIndex: 100,
            opacity: 0.7,
            cursor: "move",
            helper: "clone",
            distance: 60,
            revert: "invalid",
            revertDuration: 200,
            snapMode: "inner",
            // overwrite start/drag to support browser scaling/zooming
            start: (event) => {
                initialDragPosition.x = event.clientX!;
                initialDragPosition.y = event.clientY!;
            },
            drag: (event, ui: { originalPosition: { left: number; top: number }; position: { left: number; top: number } }) => {
                const original = ui.originalPosition;
                const zoomFactor = this.zoomFactor || 1.0;
                ui.position = {
                    left: (event.clientX! - initialDragPosition.x + original.left) / zoomFactor,
                    top:  (event.clientY! - initialDragPosition.y + original.top ) / zoomFactor
                };
            }
        });

        PrimeFaces.interactions.droppable(droppableNodes, {
            accept: ".p-organigram-node.draggable",
            tolerance: "pointer",
            activeClass: "drag-active",
            hoverClass: "drop-hover",
            zoomFactor: () => {return this.zoomFactor || 1.0}
        });

        // drop & drop happens in the target dom
        // but moving the actual nodes happens in the source li/ul dom
        // after dropping, we redraw the organigram from the source
        droppableNodes.on("drop", (event, ui: { draggable: JQuery }) => {

            // lookup target node in source DOM
            const targetId = $(event.currentTarget).data("rowkey");
            const targetLi = this.source.find("li").filter((i, el) => {
                return $(el).data("rowkey") === targetId;
            });
            const targetUl = targetLi.children("ul");

            // lookup source node in source DOM
            const sourceId = ui.draggable.data("rowkey");
            const sourceLi = this.source.find("li").filter((i, el) => {
                return $(el).data("rowkey") === sourceId;
            });
            const sourceUl = sourceLi.parent("ul");

            // ignore the current drop?
            let ignore = false;

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
            targetLi.parents().each((i, el) => {
                if ($(el).data("parent-rowkey") === sourceId) {
                    ignore = true;
                    return false;
                }
                return undefined;
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
            const oldParent = this.source.find("li").filter((i, el) => {
                return $(el).data("rowkey") === sourceLi.data("parent-rowkey");
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
            if (this.hasBehavior("dragdrop")) {
                const options = {
                    params: [
                                { name: this.id + "_dragNode", value: sourceId },
                                { name: this.id + "_dropNode", value: targetId }
                            ]
                };
                this.cfg.behaviors!["dragdrop"]!.call(this, options);
            }

            this.redraw = true;
            return undefined;
        });

        // redraw from source after drop
        draggableNodes.on("dragstop", () => {
            // redraw only if the item was dropped successfully
            if (this.redraw) {
                this.draw();

                this.redraw = false;
            }
        });
    }

    /**
     * Sets up the buttons for the global controls, such as the buttons for zooming in and out.
     */
    private setupControls(): void {
        if (this.cfg.zoom) {

            this.jq.children(".controls").remove();

            const controls = $("<div class='controls'></div>").appendTo(this.jq);
            const controlsTable = $("<table></table>").appendTo(controls);

            if (!this.zoomFactor) {
                this.zoomFactor = 1.0;
            }
            else {
                this.zoom(this.zoomFactor);
            }

            const zoomIn = $("<tr><td><div class='control zoom-in p-icon p-zoom-in-icon p-organigram-zoom-in-icon' title='Zoom In'>&nbsp;</div></td></tr>").appendTo(controlsTable);
            zoomIn.on("click", () => {
                this.zoomFactor! += 0.1;
                this.zoom(this.zoomFactor!);
            });

            const zoomOut = $("<tr><td><div class='control zoom-out p-icon p-zoom-out-icon p-organigram-zoom-out-icon' title='Zoom Out'>&nbsp;</div></td></tr>").appendTo(controlsTable);
            zoomOut.on("click", () => {
                this.zoomFactor! -= 0.1;
                this.zoom(this.zoomFactor!);
            });
        }
    }

    /**
     * Applies the given zoom factor (scaling) to the organigram.
     *
     * @param zoom The zoom factor. Must be a positive number. `1.0` means no zoom, `2.0` means zoomed-in,
     * `0.5` means zoomed-out.
     */
    zoom(zoom: number): void {
        const element = this.target.find(">:first-child");
        element.css("-moz-transform", "scale(" + zoom + ")");
        element.css("-moz-transform-origin", "0 0");
        element.css("-o-transform", "scale(" + zoom + ")");
        element.css("-o-transform-origin", "0 0");
        element.css("-webkit-transform", "scale(" + zoom + ")");
        element.css("-webkit-transform-origin", "0 0");
        element.css("transform", "scale(" + zoom + ")");
        element.css("transform-origin", "0 0");
    }

    /**
     * Draws the given organigram node.
     * @param parentRowKey Row key of the node to draw.
     * @param nodeSource Element of the node to draw.
     * @param appendTo Element to which the node is appended.
     * @param level Nesting level of the node.
     */
    private drawNode(parentRowKey: string, nodeSource: JQuery, appendTo: JQuery, level: number): void {

        const childNodes = nodeSource.children("ul:first").children("li");
        const isLastLevel = childNodes.length === 0;

        // clone node content - ignore subnodes
        const nodeContent = nodeSource.clone()
                .children("ul,li")
                .remove()
                .end()
                .html();

        const node = $("<div></div>");

        // copy class and style from source
        node.attr("class", nodeSource.attr("class")!);
        node.attr("style", nodeSource.attr("style")!);

        // styling
        node.addClass("p-organigram-node");
        node.addClass("level-" + level);

        // set metadata
        node.attr("data-level", level);
        node.attr("data-rowkey", nodeSource.data("rowkey"));
        node.attr("data-parent-rowkey", parentRowKey);

        // top icons
        const topIconContainer = $("<div class='p-organigram-icon-container'></div>").appendTo(node);
        if (nodeSource.data("icon")) {
            const icon = $("<div class='p-organigram-icon'></div>").appendTo(topIconContainer);
            icon.addClass(nodeSource.data("icon"));

            const iconPos = nodeSource.data("icon-pos");
            if (iconPos && (iconPos === "left" || iconPos === "right")) {
                icon.addClass(nodeSource.data("icon-pos"));
            }
        }

        // content
        node.append(nodeContent);

        // bottom icons
        const bottomIconContainer = $("<div class='p-organigram-icon-container'></div>").appendTo(node);

        // we don't need to render a table in the last level as it can't have further childs
        if (isLastLevel) {
            appendTo.append(node);
        }
        else if (childNodes.length > 0) {
            const table = $("<table cellpadding='0' cellspacing='0' border='0'></table>").appendTo(appendTo);
            const row = $("<tr></tr>").appendTo(table);

            const leafChildNodes = childNodes.filter(".leaf:not(.skip-leaf)");
            const nonLeafChildNodes = childNodes.filter(":not(.leaf),.skip-leaf");
            let childNodeCount = nonLeafChildNodes.length;
            if (leafChildNodes && leafChildNodes.length > 0) {
                childNodeCount += 1;
            }

            const cell = $("<td colspan='" + (childNodeCount * 2) + "'></td>").appendTo(row);
            cell.append(node);

            this.addExpander(nodeSource, node, bottomIconContainer);
            this.drawLines(childNodeCount, table);
            this.drawChildNodes(nodeSource.data("rowkey"), leafChildNodes, nonLeafChildNodes, table, level);

            // handle initial collapsed state (the expander icon already matches it, see addExpander)
            if (nodeSource.hasClass("collapsed")) {
                // hide childs
                row.nextAll("tr").hide();
            }
            else {
                if (!nodeSource.hasClass("expanded")) {
                    node.addClass("expanded");
                }
            }
        }
    }

    /**
     * Adds an expander button for expanding or collapsing the given node.
     * @param nodeSource Node to use as a source.
     * @param node Node to collapse and expand.
     * @param bottomIconContainer Container element to which the expander button is added.
     */
    private addExpander(nodeSource: JQuery, node: JQuery, bottomIconContainer: JQuery): void {

        if (node.hasClass("collapsible")) {

            // the icons of the node (collapsedIcon / expandedIcon) are switched, the default toggle icon is the same for
            // both states: its CSS follows the collapsed class of the node
            const defaultIcon = "p-icon p-toggle-icon p-organigram-toggle-icon";
            const collapsedIcon: string = nodeSource.data("collapsed-icon") ? nodeSource.data("collapsed-icon") : defaultIcon;
            const expandedIcon: string = nodeSource.data("expanded-icon") ? nodeSource.data("expanded-icon") : defaultIcon;
            const initialIcon = node.hasClass("collapsed") ? collapsedIcon : expandedIcon;

            const expander = $("<div class='expander " + initialIcon + "'>&nbsp;</div>").appendTo(bottomIconContainer);

            expander.on("click", (e) => {
                // reinit dom references - they are lost sometimes
                const expander = $(e.currentTarget);
                const node = expander.closest(".p-organigram-node");
                const row = node.closest("tr");

                if (node.hasClass("collapsed")) {
                    node.removeClass("collapsed").addClass("expanded");
                    expander.removeClass(collapsedIcon).addClass(expandedIcon);

                    row.nextAll("tr").show();

                    // maintain state in source
                    nodeSource.removeClass("collapsed");

                    // call behavior
                    if (this.hasBehavior("expand")) {
                        const options = {
                            params: [
                                        { name: this.id + "_expandNode", value: node.data("rowkey") }
                                    ]
                        };
                        this.cfg.behaviors!["expand"]!.call(this, options);
                    }
                }
                else {
                    node.removeClass("expanded").addClass("collapsed");
                    expander.removeClass(expandedIcon).addClass(collapsedIcon);

                    row.nextAll("tr").hide();

                    // maintain state in source
                    nodeSource.addClass("collapsed");

                    // call behavior
                    if (this.hasBehavior("collapse")) {
                        const options = {
                            params: [
                                        { name: this.id + '_collapseNode', value: node.data("rowkey") }
                                    ]
                        };
                        this.cfg.behaviors!["collapse"]!.call(this, options);
                    }
                }

                // avoid bubbling to the parent select click handler
                e.stopPropagation();
            });
        }
    }

    /**
     * Draws the lines connecting the nodes.
     * @param childNodeCount Number of children in the sub table.
     * @param table The DOM element for the sub table for which to draw the children.
     */
    private drawLines(childNodeCount: number, table: JQuery): void {

        // draw vertical row
        const verticalColspan = childNodeCount * 2;
        const verticalRow = $("<tr></tr>").appendTo(table);
        const verticalCell = $("<td colspan='" + verticalColspan + "'></td>").appendTo(verticalRow);
        verticalCell.append($("<div class='line down'></div>"));


        // draw horizontal row/cells
        const horizontalRow = $("<tr></tr>").appendTo(table);
        for (let i = 0; i < childNodeCount; i++) {
            horizontalRow.append($("<td class='line left top'></td>"));
            horizontalRow.append($("<td class='line right top'></td>"));
        }

        // remove the line from the first and last cell
        horizontalRow.find("td:first").removeClass("top");
        horizontalRow.find("td:last").removeClass("top");
    }

    /**
     * Draws the child nodes of the given parent node.
     * @param parentRowKey Row key of the parent node with children to draw.
     * @param leafChildNodes  Children of the parent that are leaf nodes, i.e. do no have any children.
     * @param nonLeafChildNodes Children of the parent that are not leaf nodes, i.e. do have at least one
     * child.
     * @param table The DOM element for the sub table for which to draw the children.
     * @param level The nesting level of the parent node.
     */
    private drawChildNodes(parentRowKey: string, leafChildNodes: JQuery, nonLeafChildNodes: JQuery, table: JQuery, level: number): void {
        const row = $("<tr></tr>").appendTo(table);

        // draw leaf nodes in a different way
        if (leafChildNodes && leafChildNodes.length > 0) {
            const cell = $("<td colspan='2'></td>").appendTo(row);

            const leafTable = $("<table cellpadding='0' cellspacing='0' border='0'></table>").appendTo(cell);

            for (let j = 0; j < leafChildNodes.length; j++) {
                // add connector line
                if (j !== 0 && this.cfg.leafNodeConnectorHeight! > 0) {
                    leafTable.append($("<tr><td><div class='line down' style='height:" + this.cfg.leafNodeConnectorHeight + "px'></div></td></tr>"));
                }

                const leafRow = $("<tr></tr>").appendTo(leafTable);
                const leafCell = $("<td></td>").appendTo(leafRow);

                const childNode = $(leafChildNodes[j]!);
                this.drawNode(parentRowKey, childNode, leafCell, level + 1);
            }
        }

        // draw normal nodes
        for (let i = 0; i < nonLeafChildNodes.length; i++) {
            const cell = $("<td colspan='2'></td>").appendTo(row);

            const childNode = $(nonLeafChildNodes[i]!);
            this.drawNode(parentRowKey, childNode, cell, level + 1);
        }
    }

    override bindContextMenu(
        menuWidget: PrimeFacesTypes.widget.ContextMenuLikeWidget,
        targetWidget: this,
        targetId: string,
        cfg: PrimeFacesTypes.widget.PartialWidgetCfg<PrimeFacesTypes.widget.ContextMenuLikeWidgetCfg>,
    ): void {
        let selector = targetId + " .p-organigram-node.selectable";
        const event = cfg.event + ".organigram" + this.id;

        if (cfg.nodeType) {
            selector += "." + cfg.nodeType;
        }

        $(document).off(event, selector).on(event, selector, null, (e) => {
            targetWidget.selectNode(targetWidget, $(e.currentTarget), "contextmenu");
            menuWidget.show(e);
        });
        this.addDestroyListener(() => {
            $(document).off(event);
        });
    }

    /**
     * Scrolls the organigram to the currently selected node, so that the node is in view.
     */
    scrollToSelection(): void {
        const selection = this.target.find(".p-organigram-node.selected");
        if (selection.length > 0) {
            const offset = selection.offset()!;
            this.target.animate({
                scrollTop: offset.top ,
                scrollLeft: offset.left
            },{
                easing: 'easeInCirc'
            });
        }
    }
}
