/**
 * PrimeFaces Base Tree Widget
 */
PrimeFaces.widget.BaseTree = PrimeFaces.widget.BaseWidget.extend({

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

    initSelection: function() {
        this.selectionHolder = $(this.jqId + '_selection');
        var selectionsValue = this.selectionHolder.val();
        this.selections = selectionsValue === '' ? [] : selectionsValue.split(',');

        if(this.cursorNode) {
            this.cursorNode = this.jq.find('.ui-treenode[data-rowkey="' + this.cursorNode.data('rowkey') + '"]');
        }

        if(this.isCheckboxSelection() && this.cfg.propagateUp) {
            this.preselectCheckbox();
        }
    },

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
                formId: this.cfg.formId,
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

    getNodeChildrenContainer: function(node) {
        throw "Unsupported Operation";
    },

    showNodeChildren: function(node) {
        throw "Unsupported Operation";
    },

    writeSelections: function() {
        this.selectionHolder.val(this.selections.join(','));
    },

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
        }
    },

    getRowKey: function(node) {
        return node.attr('data-rowkey');
    },

    isNodeSelected: function(node) {
        return $.inArray(this.getRowKey(node), this.selections) != -1;
    },

    isSingleSelection: function() {
        return this.cfg.selectionMode == 'single';
    },

    isMultipleSelection: function() {
        return this.cfg.selectionMode == 'multiple';
    },

    isCheckboxSelection: function() {
        return this.cfg.selectionMode == 'checkbox';
    },

    addToSelection: function(rowKey) {
        if(!PrimeFaces.inArray(this.selections, rowKey)) {
            this.selections.push(rowKey);
        }
    },

    removeFromSelection: function(rowKey) {
        this.selections = $.grep(this.selections, function(r) {
            return r !== rowKey;
        });
    },

    removeDescendantsFromSelection: function(rowKey) {
        var newSelections = [];
        for(var i = 0; i < this.selections.length; i++) {
            if(this.selections[i].indexOf(rowKey + '_') !== 0)
                newSelections.push(this.selections[i]);
        }
        this.selections = newSelections;
    },

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

    bindEvents: function() {
        throw "Unsupported Operation";
    },

    selectNode: function(node, silent) {
        throw "Unsupported Operation";
    },

    unselectNode: function(node, silent) {
        throw "Unsupported Operation";
    },

    unselectAllNodes: function() {
        throw "Unsupported Operation";
    },

    preselectCheckbox: function() {
        throw "Unsupported Operation";
    },

    toggleCheckboxNode: function(node) {
        throw "Unsupported Operation";
    },

    isEmpty: function() {
        throw "Unsupported Operation";
    },

    toggleCheckboxState: function(checkbox, checked) {
        if(checked)
            this.uncheck(checkbox);
        else
            this.check(checkbox);
    },

    partialCheck: function(checkbox) {
        var box = checkbox.children('.ui-chkbox-box'),
        icon = box.children('.ui-chkbox-icon'),
        treeNode = checkbox.closest('.ui-treenode'),
        rowKey = this.getRowKey(treeNode);

        treeNode.find('> .ui-treenode-content > .ui-treenode-label').removeClass('ui-state-highlight');
        icon.removeClass('ui-icon-blank ui-icon-check').addClass('ui-icon-minus');
        treeNode.removeClass('ui-treenode-selected ui-treenode-unselected').addClass('ui-treenode-hasselected').attr('aria-checked', false).attr('aria-selected', false);

        this.removeFromSelection(rowKey);
    },

    check: function(checkbox) {
        var box = checkbox.children('.ui-chkbox-box'),
        icon = box.children('.ui-chkbox-icon'),
        treeNode = checkbox.closest('.ui-treenode'),
        rowKey = this.getRowKey(treeNode);

        box.removeClass('ui-state-hover');
        icon.removeClass('ui-icon-blank ui-icon-minus').addClass('ui-icon-check');
        treeNode.removeClass('ui-treenode-hasselected ui-treenode-unselected').addClass('ui-treenode-selected').attr('aria-checked', true).attr('aria-selected', true);

        this.addToSelection(rowKey);
    },

    uncheck: function(checkbox) {
        var box = checkbox.children('.ui-chkbox-box'),
        icon = box.children('.ui-chkbox-icon'),
        treeNode = checkbox.closest('.ui-treenode'),
        rowKey = this.getRowKey(treeNode);

        box.removeClass('ui-state-hover');
        icon.removeClass('ui-icon-minus ui-icon-check').addClass('ui-icon-blank');
        treeNode.removeClass('ui-treenode-hasselected ui-treenode-selected').addClass('ui-treenode-unselected').attr('aria-checked', false).attr('aria-selected', false);

        this.removeFromSelection(rowKey);
    },

    isExpanded: function(node) {
        return this.getNodeChildrenContainer(node).is(':visible');
    },

    focusNode: function() {
        throw "Unsupported Operation";
    }

});
