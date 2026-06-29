/**
 * __PrimeFaces Base Tree Widget__
 * 
 * A tree is used for displaying hierarchical data and creating a site navigation.
 * 
 * @typedef {"self" | "parent" | "ancestor"} PrimeFaces.widget.BaseTree.DragMode Drag mode for a tree widget. Defines
 * the parent-child relationship when a node is dragged.
 * 
 * @typedef {"lenient" | "strict"} PrimeFaces.widget.BaseTree.FilterMode Mode for filtering a tree widget.
 * 
 * @typedef {"single" | "multiple" | "checkbox"} PrimeFaces.widget.BaseTree.SelectionMode How the nodes of a tree are
 * selected. When set to `single`, only at most one node can be selected by clicking on it. When set to `multiple`,
 * more than one node may be selected by clicking on each node. When set to `checkbox`, each node receives a checkbox
 * next to it that may be used for selection.
 * 
 * @typedef PrimeFaces.widget.BaseTree.OnNodeClickCallback Callback that is invoked when a node is clicked, see
 * {@link BaseTreeCfg.onNodeClick}.
 * @this {PrimeFaces.widget.BaseTree} PrimeFaces.widget.BaseTree.OnNodeClickCallback
 * @param {JQuery} PrimeFaces.widget.BaseTree.OnNodeClickCallback.node The tree node that was clicked.
 * @param {JQuery.TriggeredEvent} PrimeFaces.widget.BaseTree.OnNodeClickCallback.event The mouse click event that occurred.
 * @return {boolean} PrimeFaces.widget.BaseTree.OnNodeClickCallback `true` to allow the node to be selected, `false` to
 * ignore the click.
 * 
 * @interface {PrimeFaces.widget.BaseTree.NodeIconSet} NodeIconSet A set of icons to be used for a certain node type.
 * @prop {string} NodeIconSet.expandedIcon Icon to be used when the node is expanded.
 * @prop {string} NodeIconSet.collapsedIcon Icon to be used when the node is collapsed.
 * 
 * @implements {PrimeFaces.widget.ContextMenu.ContextMenuProvider<PrimeFaces.widget.BaseTree>}
 * 
 * @prop {JQuery} cursorNode When multiple nodes are selected, the selected node on which the user clicked most
 * recently.
 * @prop {JQuery|null} focusedNode DOM element of the node which is currently focused, if any.
 * @prop {string} selections List of nodes which are currently selected. Each item is the row key of a selected node.
 * @prop {JQuery} selectionHolder DOM element of the hidden form element that holds the list of selected nodes.
 * 
 * @interface {PrimeFaces.widget.BaseTreeCfg} cfg The configuration for the {@link  BaseTree| BaseTree widget}.
 * You can access this configuration via {@link PrimeFaces.widget.BaseWidget.cfg|BaseWidget.cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 * @extends {PrimeFaces.widget.BaseWidgetCfg} cfg 
 * 
 * @prop {boolean} cfg.animate `true` is the tree is animated, or `false` otherwise.
 * @prop {boolean} cfg.cache `true` if the content of dynamically loaded nodes is cached for the next time the node is
 * expanded, or `false` to always fetch the content from the server.
 * @prop {boolean} cfg.disabled `true` is this widget is disabled, or `false` otherwise.
 * @prop {string} cfg.dragdropScope Optional scope for the dragging and dropping, passed to JQuery UI.
 * @prop {boolean} cfg.draggable `true` if nodes are draggable, or `false` otherwise.
 * @prop {PrimeFaces.widget.BaseTree.DragMode} cfg.dragMode Defines parent-child relationship when a node is dragged.
 * @prop {boolean} cfg.dropCopyNode When enabled, the copy of the selected nodes can be dropped from a tree to another
 * tree using Shift key.
 * @prop {boolean} cfg.droppable `true` if nodes are droppable, or `false` otherwise.
 * @prop {boolean} cfg.dynamic `true` if the content of nodes is loaded dynamically as needed, or `false` otherwise.
 * @prop {string} cfg.event Event for the context menu.
 * @prop {boolean} cfg.filter `true` if filtering is enabeld, `false` otherwise.
 * @prop {PrimeFaces.widget.BaseTree.FilterMode} cfg.filterMode Mode for filtering.
 * @prop {string} cfg.formId ID of the form to use for AJAX requests.
 * @prop {boolean} cfg.highlight `true` if selected nodes are highlighted, or `false` otherwise.
 * @prop {Record<string, PrimeFaces.widget.BaseTree.NodeIconSet>} iconStates A map between the type of a node and the
 * icons for that node.
 * @prop {boolean} cfg.multipleDrag When enabled, the selected multiple nodes can be dragged from a tree to another
 * tree.
 * @prop {string} cfg.nodeType Node type of nodes for which the context menu is available.
 * @prop {PrimeFaces.widget.BaseTree.OnNodeClickCallback} cfg.onNodeClick Callback that is invoked when a node is
 * clicked. If it returns `false`, the click on the node is ignored.
 * @prop {boolean} cfg.propagateDown Whether toggling a node checkbox is propagated downwards.
 * @prop {boolean} cfg.propagateUp Whether toggling a node checkbox is propagated upwards.
 * @prop {PrimeFaces.widget.BaseTree.SelectionMode} cfg.selectionMode How the node of this tree can be selected, if
 * selection is enabled at all.
 */
PrimeFaces.widget.BaseTree = PrimeFaces.widget.BaseWidget.extend({

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    init: function(cfg) {
        this._super(cfg);
        this.cfg.highlight = (this.cfg.highlight === false) ? false : true;
        this.focusedNode = null;

        if(!this.cfg.disabled) {
            if(this.cfg.selectionMode) {
                this.initSelection();
            }

            this.bindEvents();

            this.jq.data('widget', cfg.widgetVar);
        }
    },

    /**
     * Called when this tree is initialized. Performs any setup required for enabling the selection of node.
     * @protected
     */
    initSelection: function() {
        this.selectionHolder = $(this.jqId + '_selection');
        var selectionsValue = this.selectionHolder.val();
        this.selections = selectionsValue === '' ? [] : selectionsValue.split(',');

        if(this.cursorNode) {
            this.cursorNode = this.jq.find('.ui-treenode[data-rowkey="' + $.escapeSelector(this.cursorNode.data('rowkey')) + '"]');
        }

        if(this.isCheckboxSelection() && this.cfg.propagateUp) {
            this.preselectCheckbox();
        }
    },

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.widget.ContextMenu} menuWidget
     * @param {PrimeFaces.widget.BaseTree} targetWidget
     * @param {string} targetId
     * @param {PrimeFaces.widget.ContextMenuCfg} cfg 
     */
    bindContextMenu : function(menuWidget, targetWidget, targetId, cfg) {
        var nodeContentSelector = targetId + ' .ui-tree-selectable',
        nodeEvent = cfg.nodeType ? cfg.event + '.treenode.' + cfg.nodeType : cfg.event + '.treenode',
        containerEvent = cfg.event + '.tree';

        $(document).off(nodeEvent, nodeContentSelector).on(nodeEvent, nodeContentSelector, null, function(e) {
            var nodeContent = $(this);

            if($(e.target).is(':not(.ui-tree-toggler)') && (cfg.nodeType === undefined || nodeContent.parent().data('nodetype') === cfg.nodeType)) {
                var isContextMenuDelayed = targetWidget.nodeRightClick(e, nodeContent, function(){
                    menuWidget.show(e);
                });

                if(isContextMenuDelayed) {
                    e.preventDefault();
                    e.stopPropagation(); 
                }
            }
        });

        $(document).off(containerEvent, this.jqTargetId).on(containerEvent, this.jqTargetId, null, function(e) {
            if(targetWidget.isEmpty()) {
                menuWidget.show(e);
            }
        });
    },

    /**
     * Expands the given node, as if the user had clicked on the `+` icon of the node. The children of the node will now
     * be visible. 
     * @param {JQuery} node Node to expand. 
     */
    expandNode: function(node) {
        var $this = this;

        if(this.cfg.dynamic) {
            if(this.cfg.cache && $this.getNodeChildrenContainer(node).children().length > 0) {
                this.showNodeChildren(node);

                return;
            }

            if(node.data('processing')) {
                PrimeFaces.debug('Node is already being expanded, ignoring expand event.');
                return;
            }

            node.data('processing', true);

            var options = {
                source: this.id,
                process: this.id,
                update: this.id,
                formId: this.getParentFormId(),
                params: [
                    {name: this.id + '_expandNode', value: $this.getRowKey(node)}
                ],
                onsuccess: function(responseXML, status, xhr) {
                    PrimeFaces.ajax.Response.handle(responseXML, status, xhr, {
                            widget: $this,
                            handle: function(content) {
                                var nodeChildrenContainer = this.getNodeChildrenContainer(node);
                                nodeChildrenContainer.append(content);

                                this.showNodeChildren(node);

                                if(this.cfg.draggable) {
                                    this.makeDraggable(nodeChildrenContainer.find('span.ui-treenode-content'));
                                }

                                if(this.cfg.droppable) {
                                    this.makeDropPoints(nodeChildrenContainer.find('li.ui-tree-droppoint'));
                                    this.makeDropNodes(nodeChildrenContainer.find('span.ui-treenode-droppable'));
                                }
                            }
                        });

                    return true;
                },
                oncomplete: function() {
                    node.removeData('processing');
                }
            };

            if(this.hasBehavior('expand')) {
                this.callBehavior('expand', options);
            }
            else {
                PrimeFaces.ajax.Request.handle(options);
            }
        }
        else {
            this.showNodeChildren(node);
            this.fireExpandEvent(node);
        }
    },

    /**
     * Called when a node was expanded. Fire the appropriate event.
     * @protected
     * @param {JQuery} node The node for which to fire the event.
     */
    fireExpandEvent: function(node) {
        if(this.hasBehavior('expand')) {
            var ext = {
                params: [
                    {name: this.id + '_expandNode', value: this.getRowKey(node)}
                ]
            };

            this.callBehavior('expand', ext);
        }
    },

    /**
     * Called when a node was collapsed. Fire the appropriate event.
     * @protected
     * @param {JQuery} node The node for which to fire the event.
     */
    fireCollapseEvent: function(node) {
        if(this.hasBehavior('collapse')) {
            var ext = {
                params: [
                    {name: this.id + '_collapseNode', value: this.getRowKey(node)}
                ]
            };

            this.callBehavior('collapse', ext);
        }
    },

    /**
     * Finds the DOM element for the container which contains the child nodes of the given node.
     * @protected
     * @param {JQuery} node A node for which to get the children container.
     * @return {JQuery} The container with the children of the given node.
     */
    getNodeChildrenContainer: function(node) {
        throw "Unsupported Operation";
    },

    /**
     * Makes the children of the given node visible. Called when a node is expanded.
     * @protected
     * @param {JQuery} node Node with children to display.
     */
    showNodeChildren: function(node) {
        throw "Unsupported Operation";
    },

    /**
     * Saves the list of currently selected nodes in a hidden form element.
     * @protected
     */
    writeSelections: function() {
        this.selectionHolder.val(this.selections.join(','));
    },

    /**
     * Called when a node was selected. Fire the appropriate event.
     * @protected
     * @param {JQuery} node The node for which to fire the event.
     */
    fireNodeSelectEvent: function(node) {
        if(this.isCheckboxSelection() && this.cfg.dynamic) {
            var $this = this,
            options = {
                source: this.id,
                process: this.id
            };

            options.params = [
                {name: this.id + '_instantSelection', value: this.getRowKey(node)}
            ];

            options.oncomplete = function(xhr, status, args, data) {
                if(args.descendantRowKeys && args.descendantRowKeys !== '') {
                    var rowKeys = args.descendantRowKeys.split(',');
                    for(var i = 0; i < rowKeys.length; i++) {
                        $this.addToSelection(rowKeys[i]);
                    }
                    $this.writeSelections();
                }
            };

            if(this.hasBehavior('select')) {
                this.callBehavior('select', options);
            }
            else {
                PrimeFaces.ajax.Request.handle(options);
            }
        }
        else {
            if(this.hasBehavior('select')) {
                var ext = {
                    params: [
                        {name: this.id + '_instantSelection', value: this.getRowKey(node)}
                    ]
                };

                this.callBehavior('select', ext);
            }
        }
    },

    /**
     * Called when a node was unselected. Fire the appropriate event.
     * @protected
     * @param {JQuery} node The node for which to fire the event.
     */
    fireNodeUnselectEvent: function(node) {
        if(this.hasBehavior('unselect')) {
            var ext = {
                params: [
                    {name: this.id + '_instantUnselection', value: this.getRowKey(node)}
                ]
            };

            this.callBehavior('unselect', ext);
        }
    },

    /**
     * Called when a right click was performed on a node. Fire the appropriate event.
     * @protected
     * @param {JQuery} node The node for which to fire the event.
     * @param {() => void} fnShowMenu Callback that is invoked once the context menu is shown.
     */
    fireContextMenuEvent: function(node, fnShowMenu) {
        if(this.hasBehavior('contextMenu')) {
            var ext = {
                params: [
                    {name: this.id + '_contextMenuNode', value: this.getRowKey(node)}
                ],
                oncomplete: function() {
                    fnShowMenu();
                }
            };

            this.callBehavior('contextMenu', ext);
        } else {
            fnShowMenu();
        }
    },

    /**
     * Finds the row key (unique ID) of the given node.
     * @param {JQuery} node A node for which to find the row key.
     * @return {string} The key of the given node.
     */
    getRowKey: function(node) {
        return node.attr('data-rowkey');
    },

    /**
     * Checks whether the given node is currently selected, irrespective of the current selection mode.
     * @param {JQuery} node A node to check.
     * @return {boolean} `true` if the given node is selected, or `false` otherwise.
     */
    isNodeSelected: function(node) {
        return $.inArray(this.getRowKey(node), this.selections) != -1;
    },

    /**
     * Checks whether the selection mode of this tree is set to `single`.
     * @return {boolean} `true` if the current selection mode is `single`, or `false` otherwise.
     */
    isSingleSelection: function() {
        return this.cfg.selectionMode == 'single';
    },

    /**
     * Checks whether the selection mode of this tree is set to `multiple`.
     * @return {boolean} `true` if the current selection mode is `multiple`, or `false` otherwise.
     */
    isMultipleSelection: function() {
        return this.cfg.selectionMode == 'multiple';
    },

    /**
     * Checks whether the selection mode of this tree is set to `checkbox`.
     * @return {boolean} `true` if the current selection mode is `checkbox`, or `false` otherwise.
     */
    isCheckboxSelection: function() {
        return this.cfg.selectionMode == 'checkbox';
    },

    /**
     * Adds the given node to the list of selected nodes.
     * @protected
     * @param {string} rowKey Row key of the node to add to the selected nodes.
     */
    addToSelection: function(rowKey) {
        if(!PrimeFaces.inArray(this.selections, rowKey)) {
            this.selections.push(rowKey);
        }
    },

    /**
     * Removes the given node from the list of currently selected nodes.
     * @protected
     * @param {string} rowKey Row key of a node to to remove from the current selection.
     */
    removeFromSelection: function(rowKey) {
        this.selections = $.grep(this.selections, function(r) {
            return r !== rowKey;
        });
    },

    /**
     * Removes all chilren of the given node from the list of currently selected nodes.
     * @protected
     * @param {string} rowKey Row key of a node to process.
     */
    removeDescendantsFromSelection: function(rowKey) {
        var newSelections = [];
        for(var i = 0; i < this.selections.length; i++) {
            if(this.selections[i].indexOf(rowKey + '_') !== 0)
                newSelections.push(this.selections[i]);
        }
        this.selections = newSelections;
    },

    /**
     * Invoked in response to a normal click on a node.
     * @protected
     * @param {JQuery.TriggeredEvent} event Event of the click.
     * @param {JQuery} nodeContent Content of the clicked node.
     */
    nodeClick: function(event, nodeContent) {
        if($(event.target).is(':not(.ui-tree-toggler)')) {
            var node = nodeContent.parent(),
            selectable = nodeContent.hasClass('ui-tree-selectable');

            if(this.cfg.onNodeClick) {
                var retVal = this.cfg.onNodeClick.call(this, node, event);
                if (retVal === false) {
                    return;
                }
            }

            if(selectable && this.cfg.selectionMode) {
                var selected = this.isNodeSelected(node),
                metaKey = event.metaKey||event.ctrlKey,
                shiftKey = event.shiftKey;

                if(this.isCheckboxSelection()) {
                    this.toggleCheckboxNode(node);
                }
                else {
                    if(selected && (metaKey)) {
                        this.unselectNode(node);
                    }
                    else {
                        if(this.isSingleSelection()||(this.isMultipleSelection() && !metaKey)) {
                            this.unselectAllNodes();
                        }

                        if(this.isMultipleSelection() && shiftKey && this.cursorNode && (this.cursorNode.parent().is(node.parent()))) {
                            var parentList = node.parent(),
                            treenodes = parentList.children('li.ui-treenode'),
                            currentNodeIndex = treenodes.index(node),
                            cursorNodeIndex = treenodes.index(this.cursorNode),
                            startIndex = (currentNodeIndex > cursorNodeIndex) ? cursorNodeIndex : currentNodeIndex,
                            endIndex = (currentNodeIndex > cursorNodeIndex) ? (currentNodeIndex + 1) : (cursorNodeIndex + 1);

                            for(var i = startIndex; i < endIndex; i++) {
                                var treenode = treenodes.eq(i);
                                if(treenode.is(':visible')) {
                                    if(i === (endIndex - 1))
                                        this.selectNode(treenode);
                                    else
                                        this.selectNode(treenode, true);
                                }
                            }
                        }
                        else {
                            this.selectNode(node);
                            this.cursorNode = node;
                        }
                    }
                }

                if($(event.target).is(':not(:input:enabled)')) {
                    PrimeFaces.clearSelection();
                    this.focusNode(node);
                }
            }
        }
    },

    /**
     * Invoked in response to a right click on a node.
     * @protected
     * @param {JQuery.TriggeredEvent} event Event of the right click.
     * @param {JQuery} nodeContent Content of the clicked node.
     * @param {() => void} fnShowMenu Callback that is invoked when the context menu is shown. 
     * @return {boolean} `true` if the context menu was opened, or `false` otherwise.
     */
    nodeRightClick: function(event, nodeContent, fnShowMenu) {
        PrimeFaces.clearSelection();

        if($(event.target).is(':not(.ui-tree-toggler)')) {
            var node = nodeContent.parent(),
            selectable = nodeContent.hasClass('ui-tree-selectable');

            if(selectable && this.cfg.selectionMode) {
                var selected = this.isNodeSelected(node);
                if(!selected) {
                    if(this.isCheckboxSelection()) {
                        this.toggleCheckboxNode(node);
                    }
                    else {
                        this.unselectAllNodes();
                        this.selectNode(node, true);
                        this.cursorNode = node;
                    }
                }

                this.fireContextMenuEvent(node, fnShowMenu);
                return true;
            }
        }
        return false;
    },

    /**
     * A sub class may perform any setup related to registering event handlers in this method, such as listening to
     * mouse clicks or keyboard presses.
     * @protected
     */
    bindEvents: function() {
        throw "Unsupported Operation";
    },

    /**
     * This method must select the given node. When `silent` is set to `true`, no events should be triggered in response
     * to this action.
     * @param {JQuery} node A node of this tree to select.
     * @param {boolean} [silent] `true` if no events should be triggered, or `false` otherwise. 
     */
    selectNode: function(node, silent) {
        throw "Unsupported Operation";
    },

    /**
     * This method must unselect the given node. When `silent` is set to `true`, no events should be triggered in
     * response to this action.
     * @param {JQuery} node A node of this tree to unselect.
     * @param {boolean} [silent] `true` if no events should be triggered, or `false` otherwise. 
     */
    unselectNode: function(node, silent) {
        throw "Unsupported Operation";
    },

    /**
     * This method must unselect all nodes of this tree that are selected.
     */
    unselectAllNodes: function() {
        throw "Unsupported Operation";
    },

    /**
     * Called once during widget initialization if this tree has got nodes with selectable checkboxes.
     * @protected
     */
    preselectCheckbox: function() {
        throw "Unsupported Operation";
    },

    /**
     * Called when the nodes of this tree are selected via checkboxes. Must select the checkbox of the given node.
     * @protected
     * @param {JQuery} node Node with a checkbox to toggle.
     */
    toggleCheckboxNode: function(node) {
        throw "Unsupported Operation";
    },

    /**
     * Checks whether this tree is empty, that is, whether it contains any nodes.
     * @return {boolean} `true` if this tree has got no nodes, or `false` otherwise.
     */
    isEmpty: function() {
        throw "Unsupported Operation";
    },

    /**
     * When this tree has got selectable nodes with checkboxes, checks or unchecks the given checkbox.
     * @param {JQuery} checkbox A checkbox of a node to check or uncheck.
     * @param {boolean} checked `true` to check the given node, `false` to uncheck it.
     */
    toggleCheckboxState: function(checkbox, checked) {
        if(checked)
            this.uncheck(checkbox);
        else
            this.check(checkbox);
    },

    /**
     * When this tree has got selectable nodes with checkboxes, partially selects the given checkbox. Does nothing
     * otherwise.
     * @protected
     * @param {JQuery} checkbox Checkbox of a node to check partially.
     */
    partialCheck: function(checkbox) {
        var box = checkbox.children('.ui-chkbox-box'),
        icon = box.children('.ui-chkbox-icon'),
        treeNode = checkbox.closest('.ui-treenode'),
        rowKey = this.getRowKey(treeNode);

        box.removeClass('ui-state-active');
        treeNode.find('> .ui-treenode-content > .ui-treenode-label').removeClass('ui-state-highlight');
        icon.removeClass('ui-icon-blank ui-icon-check').addClass('ui-icon-minus');
        treeNode.removeClass('ui-treenode-selected ui-treenode-unselected').addClass('ui-treenode-hasselected').attr('aria-checked', false).attr('aria-selected', false);

        this.removeFromSelection(rowKey);
    },

    /**
     * When this tree has got selectable nodes with checkboxes, selects the given checkbox. Does nothing otherwise.
     * @protected
     * @param {JQuery} checkbox Checkbox of a node to check.
     */
    check: function(checkbox) {
        var box = checkbox.children('.ui-chkbox-box'),
        icon = box.children('.ui-chkbox-icon'),
        treeNode = checkbox.closest('.ui-treenode'),
        rowKey = this.getRowKey(treeNode);

        box.addClass('ui-state-active');
        icon.removeClass('ui-icon-blank ui-icon-minus').addClass('ui-icon-check');
        treeNode.removeClass('ui-treenode-hasselected ui-treenode-unselected').addClass('ui-treenode-selected').attr('aria-checked', true).attr('aria-selected', true);

        this.addToSelection(rowKey);
    },

    /**
     * When this tree has got selectable nodes with checkboxes, unselects the given checkbox. Does nothing otherwise.
     * @protected
     * @param {JQuery} checkbox Checkbox of a node to uncheck.
     */
    uncheck: function(checkbox) {
        var box = checkbox.children('.ui-chkbox-box'),
        icon = box.children('.ui-chkbox-icon'),
        treeNode = checkbox.closest('.ui-treenode'),
        rowKey = this.getRowKey(treeNode);

        box.removeClass('ui-state-active');
        icon.removeClass('ui-icon-minus ui-icon-check').addClass('ui-icon-blank');
        treeNode.removeClass('ui-treenode-hasselected ui-treenode-selected').addClass('ui-treenode-unselected').attr('aria-checked', false).attr('aria-selected', false);

        this.removeFromSelection(rowKey);
    },

    /**
     * Checks whether the given node is currently expanded, that is, whether its children are visible.
     * @param {JQuery} node Node to check. 
     * @return {boolean} `true` if the node is expanded, or `false` otherwise.
     */
    isExpanded: function(node) {
        return this.getNodeChildrenContainer(node).is(':visible');
    },

    /**
     * Puts focus on the given node.
     * @protected
     * @param {JQuery} node A node on which to put focus.
     */
    focusNode: function(node) {
        throw "Unsupported Operation";
    }

});
