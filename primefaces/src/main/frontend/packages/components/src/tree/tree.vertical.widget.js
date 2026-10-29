/**
 * __PrimeFaces Vertical Tree Widget__
 *
 * Tree is used for displaying hierarchical data and creating a site navigation. This implements a vertical tree.
 *
 * @typedef {"none" | "sibling"} PrimeFaces.widget.VerticalTree.DropRestrictMode Defines parent-child restrictions when
 * a node is dropped.
 *
 * @interface {PrimeFaces.widget.VerticalTree.DroppedNodeParams} DroppedNodeParams Describes a drag & drop operation
 * when a tree node is being dragged.
 * @prop {PrimeFacesTypes.interactions.DroppableUi} DroppedNodeParams.ui Details about the drop event.
 * @prop {PrimeFaces.widget.VerticalTree} DroppedNodeParams.dragSource Tree widget of the dragged node.
 * @prop {JQuery} DroppedNodeParams.dragNode The node that was dragged.
 * @prop {JQuery} DroppedNodeParams.targetDragNode The node that was the target of the drag.
 * @prop {JQuery} DroppedNodeParams.dropPoint The drop point where the node was dropped.
 * @prop {JQuery} DroppedNodeParams.dropNode The node on which the dragged node was dropped.
 * @prop {boolean} DroppedNodeParams.transfer Whether a transfer should occur, i.e. whether the node was not dropped on
 * itself.
 *
 * @prop {JQuery} container The DOM element for the tree container.
 * @prop {PrimeFaces.widget.VerticalTree.DroppedNodeParams[]} droppedNodeParams List of parameter describing the drag &
 * drop operations.
 * @prop {JQuery} filterInput The DOM element for the filter input field that lets the user search the tree.
 * @prop {string[]} invalidSourceKeys A list of row keys for rows that are not valid drag sources.
 * @prop {number} scrollInterval The set-interval time ID of the timer for scrolling.
 * @prop {JQuery} scrollStateHolder Form element that holds the current scroll state.
 * @prop {boolean} shiftKey For drag&drop, whether the shift is pressed.
 *
 * @interface {PrimeFaces.widget.VerticalTreeCfg} cfg The configuration for the
 * {@link  VerticalTree| VerticalTree widget}. You can access this configuration via
 * {@link PrimeFaces.widget.BaseWidget.cfg|BaseWidget.cfg}. Please note that this configuration is usually meant to be
 * read-only and should not be modified.
 * @extends {PrimeFaces.widget.BaseTreeCfg} cfg
 *
 * @prop {boolean} cfg.controlled Whether drag & drop operations of this tree table are controlled.
 * @prop {PrimeFaces.widget.VerticalTree.DropRestrictMode} cfg.dropRestrict Defines parent-child restrictions when a node is
 * dropped.
 * @prop {boolean} cfg.rtl `true` if text direction is right-to-left, or `false` otherwise.
 * @prop {string} cfg.filterEvent Client side event to invoke filtering. Default is keyup.
 * @prop {number} cfg.filterDelay Delay to wait in milliseconds before sending each filter query. Default is 300.
 */
PrimeFaces.widget.VerticalTree = class VerticalTree extends PrimeFaces.widget.BaseTree {

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    init(cfg) {
        super.init(cfg);

        this.container = this.jq.children('.p-tree-container');
        this.cfg.rtl = this.jq.hasClass('p-tree-rtl');
        this.scrollStateHolder = $(this.jqId + '_scrollState');

        if(!this.cfg.disabled) {
            if(this.cfg.draggable) {
                this.initDraggable();
            }

            if(this.cfg.droppable) {
                this.initDroppable();
            }
        }

        this.restoreScrollState();
    }

    /**
     * @override
     * @protected
     * @inheritdoc
     */
    bindEvents() {
        var $this = this,
        togglerSelector = '.p-tree-toggler',
        nodeContentSelector = '.p-treenode-content';

        this.jq.off('click.tree-toggle', togglerSelector)
                    .on('click.tree-toggle', togglerSelector, null, function(e) {
                        var toggleIcon = $(this),
                        node = toggleIcon.closest('li');

                        if(node.attr('aria-expanded') !== 'true')
                            $this.expandNode(node);
                        else
                            $this.collapseNode(node);
                    });

        if(this.cfg.highlight && this.cfg.selectionMode) {
            this.jq.off('mouseenter.tree mouseleave.tree', nodeContentSelector)
                        .on('mouseleave.tree', nodeContentSelector, null, function() {
                            $(this).removeClass('p-state-hover');
                        })
                        .on('mouseenter.tree', nodeContentSelector, null, function() {
                            $(this).addClass('p-state-hover');
                        });
        }

        if(this.isCheckboxSelection()) {
            var checkboxSelector = '.p-chkbox-box:not(.p-state-disabled)';

            this.jq.off('mouseleave.tree-checkbox mouseenter.tree-checkbox', checkboxSelector)
                        .on('mouseleave.tree-checkbox', checkboxSelector, null, function() {
                            $(this).removeClass('p-state-hover');
                        })
                        .on('mouseenter.tree-checkbox', checkboxSelector, null, function() {
                            $(this).addClass('p-state-hover');
                        });
        }

        this.jq.off('click.tree-content', nodeContentSelector)
                        .on('click.tree-content', nodeContentSelector, null, function(e) {
                            $this.nodeClick(e, $(this));
                        });

        if(this.cfg.filter) {
            this.cfg.filterDelay = this.cfg.filterDelay || 300;
            this.cfg.filterEvent = this.cfg.filterEvent || 'keyup';
            var isEnterKeyFilter = this.cfg.filterEvent === 'enter';
            if (isEnterKeyFilter) {
                this.cfg.filterEvent = 'keydown';
            }
            var filterEventName = this.cfg.filterEvent + '.tree-filter';
            
            this.filterInput = this.jq.find('.p-tree-filter');
            PrimeFaces.skinInput(this.filterInput);

            this.filterInput.on(filterEventName, function(e) {
                if (PrimeFaces.utils.ignoreFilterKey(e) || PrimeFaces.utils.blockEnterKey(e) !== isEnterKeyFilter) {
                    return;
                }

                if (isEnterKeyFilter) {
                    $this.filter();
                } else {
                    PrimeFaces.debounce(() => $this.filter(), $this.cfg.filterDelay);
                }
            });
        }

        this.jq.on('scroll.tree', function(e) {
            $this.saveScrollState();
        });

        this.bindKeyEvents();
    }

    /**
     * Sets up all event listeners for keyboard interactions.
     * @private
     */
    bindKeyEvents() {
        var $this = this,
        pressTab = false;

        this.jq.on('mousedown.tree', function(e) {
            if($(e.target).is(':not(:input:enabled):not(.p-treenode-label)')) {
                e.preventDefault();
            }
        });
        this.jq.children('.p-tree-container').on('focus.tree', function() {
            if(!$this.focusedNode && !pressTab) {
                $this.focusNode($this.getFirstNode());
            }
        });

        this.jq.off('keydown.tree blur.tree', '.p-treenode-content').on('keydown.tree', '.p-treenode-content', null, function(e) {
            if(!$this.focusedNode) {
                return;
            }

            var searchRowkey = "";

            switch(e.code) {
                case 'ArrowLeft':
                    var rowkey = $this.focusedNode.data('rowkey').toString(),
                    keyLength = rowkey.length;

                    if($this.isExpanded($this.focusedNode)) {
                        $this.collapseNode($this.focusedNode);
                    }
                    else {
                        var nodeToFocus = null;
                        for(var i = 1; i < parseInt(keyLength / 2) + 1; i++){
                            searchRowkey = rowkey.substring(0, keyLength - 2 * i);
                            nodeToFocus = $this.container.find("li:visible[data-rowkey = '" + searchRowkey + "']");
                            if(nodeToFocus.length) {
                                $this.focusNode(nodeToFocus);
                                break;
                            }
                        }
                    }

                    e.preventDefault();
                break;

                case 'ArrowRight':
                    if(!$this.focusedNode.hasClass('p-treenode-leaf')) {
                        var rowkey = $this.focusedNode.data('rowkey').toString(),
                        keyLength = rowkey.length;

                        if(!$this.isExpanded($this.focusedNode)) {
                            $this.expandNode($this.focusedNode);
                        }

                        if(!$this.isExpanded($this.focusedNode) && !$this.cfg.dynamic) {
                            searchRowkey = rowkey + '_0';
                            var nodeToFocus = $this.container.find("li:visible[data-rowkey = '" + searchRowkey + "']");

                            if(nodeToFocus.length) {
                                $this.focusNode(nodeToFocus);
                            }
                        }
                    }

                    e.preventDefault();
                break;

                case 'ArrowUp':
                    var nodeToFocus = null,
                    prevNode = $this.previousNode($this.focusedNode);

                    if(prevNode.length) {
                        nodeToFocus = prevNode.find('li.p-treenode:visible:not(.p-tree-droppoint)').last();
                        if(!nodeToFocus.length) {
                            nodeToFocus = prevNode;
                        }
                    }
                    else {
                        nodeToFocus = $this.focusedNode.closest('ul').parent('li');
                    }

                    if(nodeToFocus.length) {
                        $this.focusNode(nodeToFocus);
                    }

                    e.preventDefault();
                break;

                case 'ArrowDown':
                    var nodeToFocus = null,
                    firstVisibleChildNode = $this.focusedNode.find("> ul > li:visible:not(.p-tree-droppoint)").first();

                    if(firstVisibleChildNode.length) {
                        nodeToFocus = firstVisibleChildNode;
                    }
                    else if($this.nextNode($this.focusedNode).length) {
                        nodeToFocus = $this.nextNode($this.focusedNode);
                    }
                    else {
                        var rowkey = $this.focusedNode.data('rowkey').toString();

                        if(rowkey.length !== 1) {
                            nodeToFocus = $this.searchDown($this.focusedNode);
                        }
                    }

                    if(nodeToFocus && nodeToFocus.length) {
                        $this.focusNode(nodeToFocus);
                    }

                    e.preventDefault();
                break;

                case 'Enter':
                case 'NumpadEnter':
                case 'Space':
                    if($this.cfg.selectionMode) {
                        var selectable = $this.focusedNode.children('.p-treenode-content').hasClass('p-tree-selectable');

                        if($this.cfg.onNodeClick) {
                            var retVal = $this.cfg.onNodeClick.call($this, $this.focusedNode, e);
                            if (retVal === false) {
                                return;
                            }
                        }

                        if(selectable) {
                            var selected = $this.isNodeSelected($this.focusedNode);

                            if($this.isCheckboxSelection()) {
                                $this.toggleCheckboxNode($this.focusedNode);
                            }
                            else {
                                if(selected) {
                                    $this.unselectNode($this.focusedNode);
                                }
                                else {
                                    if($this.isSingleSelection()) {
                                        $this.unselectAllNodes();
                                    }

                                    $this.selectNode($this.focusedNode);
                                    $this.cursorNode = $this.focusedNode;
                                }
                            }
                        }
                    }

                    e.preventDefault();
                break;

                case 'Tab':
                    pressTab = true;
                    $this.container.trigger('focus');
                    PrimeFaces.queueTask(function() {
                        pressTab = false;
                    });

                break;
            }
        })
        .on('blur.tree', '.p-treenode-content', null, function(e) {
            if($this.focusedNode) {
                $this.getNodeContent($this.focusedNode).removeClass('p-treenode-outline');
                $this.focusedNode = null;
            }
        });

        /* For copy/paste operation on drag and drop */
        var namespace = '.tree' + this.id;
        $(document.body).off(namespace).on('keydown' + namespace, function(e) {
            $this.shiftKey = e.shiftKey;
        }).on('keyup' + namespace, function() {
            $this.shiftKey = false;
        });
        this.addDestroyListener(function() {
            $(document.body).off(namespace);
        });
    }

    /**
     * Returns the previous node, skipping droppoints (if present), starting at the given node.
     * @private
     * @param {JQuery} node Node where to start the search.
     * @return {JQuery} The previous node.
     */
    previousNode(node) {
        var prevNode = node.prev();
        if (prevNode.length && (prevNode.hasClass("p-tree-droppoint") || prevNode.hasClass("p-treenode-hidden"))) {
            prevNode = prevNode.prev();
        }
        return prevNode;
    }

    /**
     * Returns the next node, skipping droppoints (if present), starting at the given node.
     * @private
     * @param {JQuery} node Node where to start the search.
     * @return {JQuery} The next node.
     */
    nextNode(node) {
        var nextNode = node.next();
        if (nextNode.length && (nextNode.hasClass("p-tree-droppoint") || nextNode.hasClass("p-treenode-hidden"))) {
            nextNode = nextNode.next();
        }
        return nextNode;
    }

    /**
     * Searches for a node to focus, starting at the given node.
     * @private
     * @param {JQuery} node Node where to start the search.
     * @return {JQuery} A node to focus.
     */
    searchDown(node) {
        var nextOfParent = this.nextNode(node.closest('ul').parent('li')),
        nodeToFocus = null;

        if(nextOfParent.length) {
            nodeToFocus = nextOfParent;
        }
        else if(node.hasClass('p-treenode-leaf') && node.closest('ul').parent('li').length == 0){
            nodeToFocus = node;
        }
        else {
            var rowkey = node.data('rowkey').toString();

            if(rowkey.length !== 1) {
                nodeToFocus = this.searchDown(node.closest('ul').parent('li'));
            }
        }

        return nodeToFocus;
    }

    /**
     * Collapses the given node, as if the user had clicked on the `-` icon of the node. The children of the node will
     * now be visible.
     * @param {JQuery} node Node to collapse.
     */
    collapseNode(node) {
        var _self = this,
        nodeContent = node.find('> .p-treenode-content'),
        toggleIcon = nodeContent.find('> .p-tree-toggler'),
        nodeType = node.data('nodetype'),
        nodeIcon = toggleIcon.nextAll('span.p-treenode-icon'),
        iconState = this.cfg.iconStates[nodeType],
        childrenContainer = node.children('.p-treenode-children');

        //aria
        node.attr('aria-expanded', false);

        if(iconState) {
            nodeIcon.removeClass(iconState.expandedIcon).addClass(iconState.collapsedIcon);
        }

        if(this.cfg.animate) {
            childrenContainer.slideUp('fast', function() {
                _self.postCollapse(node, childrenContainer);
            });
        }
        else {
            childrenContainer.hide();
            this.postCollapse(node, childrenContainer);
        }
    }

    /**
     * Callback that is invoked after a node was collapsed.
     * @private
     * @param {JQuery} node The node that was collapsed.
     * @param {JQuery} childrenContainer The container element with the children of the collapsed node.
     */
    postCollapse(node, childrenContainer) {
        if(this.cfg.dynamic && !this.cfg.cache) {
            childrenContainer.empty();
        }

        if(!this.cfg.cache) {
            this.fireCollapseEvent(node);
        }
    }

    /**
     * @override
     * @protected
     * @inheritdoc
     * @param {JQuery} node
     * @return {JQuery}
     */
    getNodeChildrenContainer(node) {
        return node.children('.p-treenode-children');
    }

    /**
     * @override
     * @protected
     * @inheritdoc
     * @param {JQuery} node
     */
    showNodeChildren(node) {
        var nodeContent = node.find('> .p-treenode-content'),
        toggleIcon = nodeContent.find('> .p-tree-toggler'),
        nodeType = node.data('nodetype'),
        nodeIcon = toggleIcon.nextAll('span.p-treenode-icon'),
        iconState = this.cfg.iconStates[nodeType];

        //aria
        node.attr('aria-expanded', true);

        if(iconState) {
            nodeIcon.removeClass(iconState.collapsedIcon).addClass(iconState.expandedIcon);
        }

        if(this.cfg.animate) {
            node.children('.p-treenode-children').slideDown('fast');
        }
        else {
            node.children('.p-treenode-children').show();
        }
    }

    /**
     * @override
     * @inheritdoc
     */
    unselectAllNodes() {
        this.selections = [];
        this.jq.find('.p-treenode-content.p-state-highlight').each(function() {
            $(this).removeClass('p-state-highlight').closest('.p-treenode').attr('aria-selected', false).removeClass('p-treenode-selected').addClass('p-treenode-unselected');
        });
    }

    /**
     * @override
     * @inheritdoc
     * @param {JQuery} node
     * @param {boolean} [silent]
     */
    selectNode(node, silent) {
        node.attr('aria-selected', true).removeClass('p-treenode-unselected').addClass('p-treenode-selected')
            .find('> .p-treenode-content').addClass('p-state-highlight');

        this.addToSelection(this.getRowKey(node));
        this.writeSelections();

        if(!silent)
            this.fireNodeSelectEvent(node);
    }

    /**
     * @override
     * @inheritdoc
     * @param {JQuery} node
     * @param {boolean} [silent]
     */
    unselectNode(node, silent) {
        var rowKey = this.getRowKey(node);

        node.attr('aria-selected', false).removeClass('p-treenode-selected').addClass('p-treenode-unselected')
            .find('> .p-treenode-content').removeClass('p-state-highlight');

        this.removeFromSelection(rowKey);
        this.writeSelections();

        if(!silent)
            this.fireNodeUnselectEvent(node);
    }

    /**
     * @override
     * @protected
     * @inheritdoc
     * @param {JQuery} node
     */
    toggleCheckboxNode(node) {
        var $this = this,
        checkbox = node.find('> .p-treenode-content > .p-chkbox'),
        checked = checkbox.find('> .p-chkbox-box > .p-chkbox-icon').hasClass('p-checkbox-checked-icon');

        if(this.cfg.propagateDown) {
            var skippedRowKeys = [];
            this.propagateCheckboxDown(this.getChildNodes(node), checked, skippedRowKeys);

            // a non-selectable child can never be selected via its checkbox, so it must not keep its parent from
            // being fully selected, see #14798
            var children = node.find('> .p-treenode-children > .p-treenode').filter(function() {
                return $this.isSelectableNode($(this));
            });
            if(checked) {
                if(children.filter('.p-treenode-unselected').length === children.length)
                    $this.uncheck(checkbox);
                else
                    $this.partialCheck(checkbox);
            }
            else {
                if(children.filter('.p-treenode-selected').length === children.length)
                    $this.check(checkbox);
                else
                    $this.partialCheck(checkbox);
            }

            if(this.cfg.dynamic) {
                this.removeDescendantsFromSelection(node.data('rowkey'), skippedRowKeys);
            }
        } else {
            this.toggleCheckboxState(checkbox, checked);
        }

        if(this.cfg.propagateUp) {
            node.parents('li.p-treenode-parent').each(function() {
                var parentNode = $(this),
                parentsCheckbox = parentNode.find('> .p-treenode-content > .p-chkbox'),
                children = parentNode.find('> .p-treenode-children > .p-treenode').filter(function() {
                    return $this.isSelectableNode($(this));
                });

                // a non-selectable node must never be selected by the propagation either, and without any selectable
                // child there is nothing to derive the state of the node from, see #14798
                if(!$this.isSelectableNode(parentNode) || !children.length) {
                    return;
                }

                if(checked) {
                    if(children.filter('.p-treenode-unselected').length === children.length)
                        $this.uncheck(parentsCheckbox);
                    else
                        $this.partialCheck(parentsCheckbox);
                }
                else {
                    if(children.filter('.p-treenode-selected').length === children.length)
                        $this.check(parentsCheckbox);
                    else
                        $this.partialCheck(parentsCheckbox);
                }
            });
        }

        this.writeSelections();

        if(checked)
            this.fireNodeUnselectEvent(node);
        else
            this.fireNodeSelectEvent(node);
    }

    /**
     * @override
     * @protected
     * @inheritdoc
     * @param {JQuery} node
     * @return {JQuery}
     */
    getChildNodes(node) {
        return node.find('> .p-treenode-children > .p-treenode:not(.p-treenode-hidden)');
    }

    /**
     * @override
     * @protected
     * @inheritdoc
     */
    preselectCheckbox() {
        this.jq.find('.p-chkbox-icon').not('.p-checkbox-checked-icon').each(function() {
            var icon = $(this),
            node = icon.closest('li');

            if(node.children('.p-treenode-children').find('.p-chkbox-icon.p-checkbox-checked-icon').length > 0) {
                node.addClass('p-treenode-hasselected');
                icon.addClass('p-checkbox-indeterminate-icon');
            }
        });
    }

    /**
     * @override
     * @protected
     * @inheritdoc
     * @param {JQuery} checkbox
     */
    check(checkbox) {
        super.check(checkbox);
        checkbox.parent().addClass('p-state-highlight');
    }

    /**
     * @override
     * @protected
     * @inheritdoc
     * @param {JQuery} checkbox
     */
    uncheck(checkbox) {
        super.uncheck(checkbox);
        checkbox.parent().removeClass('p-state-highlight');
    }

    /**
     * Sets up the drag functionality.
     * @private
     */
    initDraggable() {
        this.makeDraggable(this.jq.find('.p-treenode-content'));
    }

    /**
     * Sets up the drop functionality.
     * @private
     */
    initDroppable() {
        this.makeDropPoints(this.jq.find('li.p-tree-droppoint'));
        this.makeDropNodes(this.jq.find('.p-treenode-droppable'));
        this.initDropScrollers();
    }

    /**
     * Sets up the draggable for the given elements.
     * @private
     * @param {JQuery} elements A list of draggable nodes to set up.
     */
    makeDraggable(elements) {
        var $this = this,
        dragdropScope = this.cfg.dragdropScope||this.id;

        PrimeFaces.interactions.draggable(elements, {
            start: function(event, ui) {
                if(ui.helper) {
                    var element = $(event.target),
                    source = PF($(element.data('dragsourceid')).data('widget')),
                    height = 20;

                    if(source.cfg.multipleDrag && element.hasClass('p-treenode-content') && element.hasClass('p-state-highlight')) {
                        source.draggedSourceKeys = $this.findSelectedParentKeys(source.selections.slice());
                        height = 20 * (source.draggedSourceKeys.length || 1);
                    }

                    $(ui.helper).height(height);
                }
            },
            helper: function() {
                var el = $('<div class="p-tree-draghelper p-state-highlight"></div>');
                el.width($this.jq.width());
                return el;
            },
            appendTo: document.body,
            zIndex: PrimeFaces.nextZindex(),
            revert: true,
            scope: dragdropScope,
            containment: 'document'
        })
        .data({
            'dragsourceid': this.jqId,
            'dragmode': this.cfg.dragMode
        });
    }

    /**
     * Sets up the drop points for the given elements.
     * @private
     * @param {JQuery} elements A list of drop points to set up.
     */
    makeDropPoints(elements) {
        var $this = this,
        dragdropScope = this.cfg.dragdropScope||this.id;

        PrimeFaces.interactions.droppable(elements, {
            hoverClass: 'p-state-hover',
            accept: '.p-treenode-content',
            tolerance: 'pointer',
            scope: dragdropScope,
            drop: function(event, ui) {
                var dragSource = PF($(ui.draggable.data('dragsourceid')).data('widget')),
                dropSource = $this,
                dropPoint = $(this),
                dropNode = dropPoint.closest('li.p-treenode-parent'),
                dropNodeKey = $this.getRowKey(dropNode),
                transfer = (dragSource.id !== dropSource.id),
                draggedSourceKeys = dragSource.draggedSourceKeys,
                isDroppedNodeCopy = $this.cfg.dropMode === 'copy' || ($this.cfg.dropCopyNode && $this.shiftKey),
                draggedNodes,
                dragNodeKey;

                if(draggedSourceKeys) {
                    draggedNodes = dragSource.findNodes(draggedSourceKeys);
                }
                else {
                    draggedNodes = [ui.draggable];
                }

                if($this.cfg.controlled) {
                    $this.droppedNodeParams = [];
                }

                $this.invalidSourceKeys = [];

                for(var i = (draggedNodes.length - 1); i >= 0; i--) {
                    var draggedNode = $(draggedNodes[i]),
                    dragMode = ui.draggable.data('dragmode'),
                    dragNode = draggedNode.is('li.p-treenode') ? draggedNode : draggedNode.closest('li.p-treenode'),
                    dragNode = (isDroppedNodeCopy) ? dragNode.clone() : dragNode,
                    targetDragNode = $this.findTargetDragNode(dragNode, dragMode);

                    dragNodeKey = $this.getRowKey(targetDragNode);

                    if(!transfer && dropNodeKey && dropNodeKey.indexOf(dragNodeKey + '_') === 0) {
                        return;
                    }

                    if($this.cfg.controlled) {
                        $this.droppedNodeParams.push({
                            'ui': ui,
                            'dragSource': dragSource,
                            'dragNode': dragNode,
                            'targetDragNode': targetDragNode,
                            'dropPoint': dropPoint,
                            'dropNode': dropNode,
                            'transfer': transfer
                        });
                    }
                    else {
                        $this.onDropPoint(ui, dragSource, dragNode, targetDragNode, dropPoint, dropNode, transfer);
                    }
                }

                if (!draggedSourceKeys) {
                    draggedSourceKeys = [dragNodeKey];
                }
                draggedSourceKeys = draggedSourceKeys.filter(function(key) {
                    return $.inArray(key, $this.invalidSourceKeys) === -1;
                });
                
                var dndIndex = dropPoint.prevAll('li.p-treenode').length;
                if (draggedSourceKeys && draggedSourceKeys.length) {
                    draggedSourceKeys = draggedSourceKeys.reverse().join(',');
                    $this.fireDragDropEvent({
                        'dragNodeKey': draggedSourceKeys,
                        'dropNodeKey': dropNodeKey,
                        'dragSource': dragSource.id,
                        'dndIndex': dndIndex,
                        'transfer': transfer,
                        'isDroppedNodeCopy': isDroppedNodeCopy
                    });
                }

                dragSource.draggedSourceKeys = null;
                $this.invalidSourceKeys = null;

                if(isDroppedNodeCopy) {
                    $this.initDraggable();
                }
            }
        });
    }

    /**
     * Callback for when a node was dropped on a drop point.
     * @private
     * @param {PrimeFacesTypes.interactions.DroppableUi} ui Details about the drop event.
     * @param {PrimeFaces.widget.VerticalTree} dragSource Tree widget of the dragged node.
     * @param {JQuery} dragNode Node that was dragged.
     * @param {JQuery} targetDragNode Node that was the target of the drag.
     * @param {JQuery} dropPoint The drop point where the node was dropped.
     * @param {JQuery} dropNode The node on which the dragged node was dropped.
     * @param {boolean} transfer Whether a transfer should occur, i.e. whether the node was not dropped on itself.
     */
    onDropPoint(ui, dragSource, dragNode, targetDragNode, dropPoint, dropNode, transfer) {
        var dragNodeDropPoint = targetDragNode.next('li.p-tree-droppoint'),
        oldParentNode = targetDragNode.parent().closest('li.p-treenode-parent');

        ui.helper.remove();
        dropPoint.removeClass('p-state-hover');

        var validDrop = this.validateDropPoint(dragNode, dropPoint);
        if(!validDrop) {
            if (this.invalidSourceKeys) {
                var dragNodeKey = this.getRowKey(targetDragNode);
                this.invalidSourceKeys.push(dragNodeKey);
            }
            return;
        }

        targetDragNode.hide().insertAfter(dropPoint);

        if(transfer) {
            if(dragSource.cfg.selectionMode) {
                dragSource.unselectSubtree(targetDragNode);
            }

            dragNodeDropPoint.remove();
            this.updateDragDropBindings(targetDragNode);
        }
        else {
            dragNodeDropPoint.insertAfter(targetDragNode);
        }

        if(oldParentNode.length && (oldParentNode.find('> ul.p-treenode-children > li.p-treenode').length === 0)) {
            this.makeLeaf(oldParentNode);
        }

        targetDragNode.fadeIn();

        if(this.isCheckboxSelection()) {
            this.syncDNDCheckboxes(dragSource, oldParentNode, dropNode);
        }

        this.syncDragDrop();
        if(transfer) {
            dragSource.syncDragDrop();
        }
    }

    /**
     * Sets up the dropables for the droppable nodes.
     * @private
     * @param {JQuery} elements List of elements to make droppable.
     */
    makeDropNodes(elements) {
        var $this = this,
        dragdropScope = this.cfg.dragdropScope||this.id;

        PrimeFaces.interactions.droppable(elements, {
            accept: '.p-treenode-content',
            tolerance: 'pointer',
            scope: dragdropScope,
            over: function(event, ui) {
                $(this).children('.p-treenode-content').addClass('p-state-hover');
            },
            out: function(event, ui) {
                $(this).children('.p-treenode-content').removeClass('p-state-hover');
            },
            drop: function(event, ui) {
                var dragSource = PF($(ui.draggable.data('dragsourceid')).data('widget')),
                dropSource = $this,
                droppable = $(this),
                dropNode = droppable.closest('li.p-treenode'),
                dropNodeKey = $this.getRowKey(dropNode),
                transfer = (dragSource.id !== dropSource.id),
                draggedSourceKeys = dragSource.draggedSourceKeys,
                isDroppedNodeCopy = ($this.cfg.dropCopyNode && $this.shiftKey),
                draggedNodes,
                dragNodeKey,
                dndIndex;

                if(draggedSourceKeys) {
                    draggedNodes = dragSource.findNodes(draggedSourceKeys);
                }
                else {
                    draggedNodes = [ui.draggable];
                }

                if($this.cfg.controlled) {
                    $this.droppedNodeParams = [];
                }

                $this.invalidSourceKeys = [];

                for(var i = 0; i < draggedNodes.length; i++) {
                    var draggedNode = $(draggedNodes[i]),
                    dragMode = ui.draggable.data('dragmode'),
                    dragNode = draggedNode.is('li.p-treenode') ? draggedNode : draggedNode.closest('li.p-treenode'),
                    dragNode = (isDroppedNodeCopy) ? dragNode.clone() : dragNode,
                    targetDragNode = $this.findTargetDragNode(dragNode, dragMode);

                    if(i === 0) {
                        dndIndex = dropNode.find('>.p-treenode-children>li.p-treenode').length;
                    }

                    dragNodeKey = $this.getRowKey(targetDragNode);

                    if(!transfer && dropNodeKey && dropNodeKey.indexOf(dragNodeKey + '_') === 0) {
                        return;
                    }

                    if($this.cfg.controlled) {
                        $this.droppedNodeParams.push({
                            'ui': ui,
                            'dragSource': dragSource,
                            'dragNode': dragNode,
                            'targetDragNode': targetDragNode,
                            'droppable': droppable,
                            'dropNode': dropNode,
                            'transfer': transfer
                        });
                    }
                    else {
                        $this.onDropNode(ui, dragSource, dragNode, targetDragNode, droppable, dropNode, transfer);
                    }
                }

                if (!draggedSourceKeys) {
                    draggedSourceKeys = [dragNodeKey];
                }
                draggedSourceKeys = draggedSourceKeys.filter(function(key) {
                    return $.inArray(key, $this.invalidSourceKeys) === -1;
                });

                if (draggedSourceKeys && draggedSourceKeys.length) {
                    draggedSourceKeys = draggedSourceKeys.reverse().join(',');
                    $this.fireDragDropEvent({
                        'dragNodeKey': draggedSourceKeys,
                        'dropNodeKey': dropNodeKey,
                        'dragSource': dragSource.id,
                        'dndIndex': dndIndex,
                        'transfer': transfer,
                        'isDroppedNodeCopy': isDroppedNodeCopy
                    });
                }

                dragSource.draggedSourceKeys = null;
                $this.invalidSourceKeys = null;

                if(isDroppedNodeCopy) {
                    $this.initDraggable();
                }
            }
        });
    }

    /**
     * Callback for when a node was dropped.
     * @private
     * @param {PrimeFacesTypes.interactions.DroppableUi} ui Details about the drop event.
     * @param {PrimeFaces.widget.VerticalTree} dragSource Tree widget of the dragged node.
     * @param {JQuery} dragNode Node that was dragged.
     * @param {JQuery} targetDragNode Node that was the target of the drag.
     * @param {JQuery} droppable The droppable where the drop occurred.
     * @param {JQuery} dropNode The node on which the dragged node was dropped.
     * @param {boolean} transfer Whether a transfer should occur.
     */
    onDropNode(ui, dragSource, dragNode, targetDragNode, droppable, dropNode, transfer) {
        var dragNodeDropPoint = targetDragNode.next('li.p-tree-droppoint'),
        oldParentNode = targetDragNode.parent().closest('li.p-treenode-parent'),
        childrenContainer = dropNode.children('.p-treenode-children');

        ui.helper.remove();
        droppable.children('.p-treenode-content').removeClass('p-state-hover');

        var validDrop = this.validateDropNode(dragNode, dropNode, oldParentNode);
        if(!validDrop) {
            if (this.invalidSourceKeys) {
                var dragNodeKey = this.getRowKey(targetDragNode);
                this.invalidSourceKeys.push(dragNodeKey);
            }
            return;
        }

        if(childrenContainer.children('li.p-treenode').length === 0) {
            this.makeParent(dropNode);
        }

        targetDragNode.hide();
        childrenContainer.append(targetDragNode);

        if(oldParentNode.length && (oldParentNode.find('> ul.p-treenode-children > li.p-treenode').length === 0)) {
            this.makeLeaf(oldParentNode);
        }

        if(transfer) {
            if(dragSource.cfg.selectionMode) {
                dragSource.unselectSubtree(targetDragNode);
            }

            dragNodeDropPoint.remove();
            this.updateDragDropBindings(targetDragNode);
        }
        else {
            childrenContainer.append(dragNodeDropPoint);
        }

        targetDragNode.fadeIn();

        if(this.isCheckboxSelection()) {
            this.syncDNDCheckboxes(dragSource, oldParentNode, dropNode);
        }

        this.syncDragDrop();
        if(transfer) {
            dragSource.syncDragDrop();
        }
    }

    /**
     * Filters the given array of row keys and removes child nodes of parent node in the array.
     * @private
     * @param {string[]} arr A list of row keys to check.
     * @return {string[]} A list of parent row keys.
     */
    findSelectedParentKeys(arr) {
        for(var i = 0; i < arr.length; i++) {
            var key = arr[i];
            for(var j = 0; j < arr.length && key !== -1; j++) {
                var tempKey = arr[j];
                if(tempKey !== -1 && key.length > tempKey.length && key.indexOf(tempKey + '_') === 0) {
                    arr[i] = -1;
                }
            }
        }

        return arr.filter(function(item) {
            return item !== -1;
        });
    }

    /**
     * Initializes all drop scrollers of this tree.
     * @private
     */
    initDropScrollers() {
        var $this = this,
        dragdropScope = this.cfg.dragdropScope||this.id;

        this.jq.prepend('<div class="p-tree-scroller p-tree-scrollertop"></div>').append('<div class="p-tree-scroller p-tree-scrollerbottom"></div>');

        PrimeFaces.interactions.droppable(this.jq.children('div.p-tree-scroller'), {
            accept: '.p-treenode-content',
            tolerance: 'pointer',
            scope: dragdropScope,
            over: function() {
                var step = $(this).hasClass('p-tree-scrollertop') ? -10 : 10;

                $this.scrollInterval = setInterval(function() {
                    $this.scroll(step);
                }, 100);
            },
            out: function() {
                clearInterval($this.scrollInterval);
            }
        });
    }

    /**
     * Scrolls this tree by the given amount.
     * @param {number} step Amount by which to scroll.
     */
    scroll(step) {
        this.container.scrollTop(this.container.scrollTop() + step);
    }

    /**
     * Updates the drag&drop settings for the given node.
     * @private
     * @param {JQuery} node Node to update.
     */
    updateDragDropBindings(node) {
        //self droppoint
        node.after('<li class="p-tree-droppoint p-droppable"></li>');
        this.makeDropPoints(node.next('li.p-tree-droppoint'));

        //descendant droppoints
        var subtreeDropPoints = node.find('li.p-tree-droppoint');
        if(subtreeDropPoints.hasClass('p-droppable') && !this.shiftKey && !this.cfg.dropCopyNode) {
            PrimeFaces.interactions.destroy(subtreeDropPoints, 'droppable');
        }
        this.makeDropPoints(subtreeDropPoints);

        //descendant drop node contents
        var subtreeDropNodeContents = node.find('.p-treenode-content');
        if(subtreeDropNodeContents.hasClass('p-droppable') && !this.shiftKey && !this.cfg.dropCopyNode) {
            PrimeFaces.interactions.destroy(subtreeDropNodeContents, 'droppable');
        }
        this.makeDropNodes(subtreeDropNodeContents);

        if(this.cfg.draggable) {
            subtreeDropNodeContents.data({
                'dragsourceid': this.jqId,
                'dragmode': this.cfg.dragMode
            });
        }
    }

    /**
     * Locates the target drag node, depending on the given drag mode.
     * @private
     * @param {JQuery} dragNode Node that was dragged.
     * @param {PrimeFaces.widget.BaseTree.DragMode} dragMode The current drag mode of this tree.
     * @return {JQuery} The resolved target drag node.
     */
    findTargetDragNode(dragNode, dragMode) {
        var targetDragNode = null;

        if(dragMode === 'self') {
            targetDragNode = dragNode;
        } else if(dragMode === 'parent') {
            targetDragNode = dragNode.parent().closest('li.p-treenode');
        } else if(dragMode === 'ancestor') {
            targetDragNode = dragNode.parent().parents('li.p-treenode:last');
        }

        if(targetDragNode.length === 0) {
            targetDragNode = dragNode;
        }

        return targetDragNode;
    }

    /**
     * Finds the nodes with the given row keys.
     * @param {string[]} rowkeys A list of row keys.
     * @return {JQuery[]} A list of nodes corresponding to the given row keys, in that order.
     */
    findNodes(rowkeys) {
        var nodes = [];
        for(var i = 0; i < rowkeys.length; i++) {
            nodes.push($(this.jqId + '\\:' + rowkeys[i]));
        }

        return nodes;
    }

    /**
     * Updates the row keys of all nodes.
     * @private
     */
    updateRowKeys() {
        var children = this.jq.find('> ul.p-tree-container > li.p-treenode');
        this.updateChildrenRowKeys(children, null);
    }

    /**
     * Updates the row keys of all given children.
     * @private
     * @param {JQuery} children List of children to update.
     * @param {string | null} rowkey Base prefix for the new rowkey.
     */
    updateChildrenRowKeys(children, rowkey) {
        var $this = this;

        children.each(function(i) {
            var childNode = $(this),
            oldRowKey = childNode.attr('data-rowkey'),
            newRowKey = (rowkey === null) ? i.toString() : rowkey + '_' + i;

            childNode.attr({
                'id': $this.id + ':' + newRowKey,
                'data-rowkey' : newRowKey
            });

            if(childNode.hasClass('p-treenode-parent')) {
                $this.updateChildrenRowKeys(childNode.find('> ul.p-treenode-children > li.p-treenode'), newRowKey);
            }
        });
    }

    /**
     * After a drag&drop, validates if the drop is allowed.
     * @private
     * @param {JQuery} dragNode Node that was dragged.
     * @param {JQuery} dropPoint Element where the node was dropped.
     * @return {boolean} Whether the drop is allowed.
     */
    validateDropPoint(dragNode, dropPoint) {
        //dropped before or after
        if(dragNode.next().get(0) === dropPoint.get(0)||dragNode.prev().get(0) === dropPoint.get(0)) {
            return false;
        }

        //descendant of dropnode
        if(dragNode.has(dropPoint.get(0)).length) {
            return false;
        }

        //drop restriction
        if(this.cfg.dropRestrict) {
            if(this.cfg.dropRestrict === 'sibling' && dragNode.parent().get(0) !== dropPoint.parent().get(0)) {
                return false;
            }
        }

        return true;
    }

    /**
     * After a drag&drop, validates if the drop is allowed.
     * @private
     * @param {JQuery} dragNode Node that was dragged.
     * @param {JQuery} dropNode Node on which the dragged node was dropped.
     * @param {JQuery} oldParentNode Old parent of the dragged node.
     * @return {boolean} Whether the drop is allowed.
     */
    validateDropNode(dragNode, dropNode, oldParentNode) {
        //dropped on parent
        if(oldParentNode.get(0) === dropNode.get(0))
            return false;

        //descendant of dropnode
        if(dragNode.has(dropNode.get(0)).length) {
            return false;
        }

        //drop restriction
        if(this.cfg.dropRestrict) {
            if(this.cfg.dropRestrict === 'sibling') {
                return false;
            }
        }

        return true;
    }

    /**
     * Turns the given node into a leaf node.
     * @private
     * @param {JQuery} node A new leaf node to convert.
     */
    makeLeaf(node) {
        node.removeClass('p-treenode-parent').addClass('p-treenode-leaf').attr('aria-expanded', false);
        node.find('> .p-treenode-content > .p-tree-toggler').addClass('p-treenode-leaf-icon').removeClass('p-tree-toggler p-icon p-toggle-icon p-tree-toggle-icon');
        node.children('.p-treenode-children').hide().children().remove();
    }

    /**
     * Turns the given node into a parent node.
     * @private
     * @param {JQuery} node A new parent node to convert.
     */
    makeParent(node) {
        node.removeClass('p-treenode-leaf').addClass('p-treenode-parent');
        node.find('> .p-treenode-content > span.p-treenode-leaf-icon').removeClass('p-treenode-leaf-icon').addClass('p-tree-toggler p-icon p-toggle-icon p-tree-toggle-icon');
        node.children('.p-treenode-children').append('<li class="p-tree-droppoint p-droppable"></li>');

        this.makeDropPoints(node.find('> ul.p-treenode-children > li.p-tree-droppoint'));
    }

    /**
     * Updates the tree after a drag&drop event.
     * @private
     */
    syncDragDrop() {
        var $this = this;

        if(this.cfg.selectionMode) {
            var selectedNodes = this.findNodes(this.selections);

            this.updateRowKeys();
            this.selections = [];
            $.each(selectedNodes, function(i, item) {
                $this.selections.push(item.attr('data-rowkey'));
            });
            this.writeSelections();
        }
        else {
            this.updateRowKeys();
        }
    }

    /**
     * Updates all checkboxes after a drag&drop.
     * @private
     * @param {PrimeFaces.widget.VerticalTree} dragSource Tree widget that is the source of the drag, when dragging
     * between two widgets.
     * @param {JQuery} oldParentNode Old node that was parent of the dropped node.
     * @param {JQuery} newParentNode New node that is to be the parent of the dropped node.
     */
    syncDNDCheckboxes(dragSource, oldParentNode, newParentNode) {
        if(oldParentNode.length) {
            dragSource.propagateDNDCheckbox(oldParentNode);
        }

        if(newParentNode.length) {
            this.propagateDNDCheckbox(newParentNode);
        }
    }

    /**
     * Unselects the node and all child nodes.
     * @param {JQuery} node Node to unselect.
     */
    unselectSubtree(node) {
        var $this = this;

        if(this.isCheckboxSelection()) {
            var checkbox = node.find('> .p-treenode-content > .p-chkbox');

            this.toggleCheckboxState(checkbox, true);

            node.children('.p-treenode-children').find('.p-chkbox').each(function() {
                $this.toggleCheckboxState($(this), true);
            });
        }
        else {
            node.find('.p-treenode-content.p-state-highlight').each(function() {
                $(this).removeClass('p-state-highlight').closest('li.p-treenode').attr('aria-selected', false).removeClass('p-treenode-selected').addClass('p-treenode-unselected');
            });
        }
    }

    /**
     * Updates the drag&drop checkboxes.
     * @private
     * @param {JQuery} node Node to which to limit the update.
     */
    propagateDNDCheckbox(node) {
        var checkbox = node.find('> .p-treenode-content > .p-chkbox'),
        children = node.find('> .p-treenode-children > .p-treenode');

        if(children.length) {
            if(children.filter('.p-treenode-unselected').length === children.length)
                this.uncheck(checkbox);
            else if(children.filter('.p-treenode-selected').length === children.length)
                this.check(checkbox);
            else
                this.partialCheck(checkbox);
        }

        var parent = node.parent().closest('.p-treenode-parent');
        if(parent.length) {
            this.propagateDNDCheckbox(parent);
        }
    }

    /**
     * Callback for when a drag&drop occurred. Invokes the appropriate behaviors.
     * @private
     * @param {JQuery.TriggeredEvent} event Event that triggered the drag&drop.
     */
    fireDragDropEvent(event) {
        var $this = this,
        options = {
            source: this.id,
            process: event.transfer ? this.id + ' ' + event.dragSource : this.id
        };

        options.params = [
            {name: this.id + '_dragdrop', value: true},
            {name: this.id + '_dragNode', value: event.dragNodeKey},
            {name: this.id + '_dragSource', value: event.dragSource},
            {name: this.id + '_dropNode', value: event.dropNodeKey},
            {name: this.id + '_dndIndex', value: event.dndIndex},
            {name: this.id + '_isDroppedNodeCopy', value: event.isDroppedNodeCopy}
        ];

        if(this.cfg.controlled) {
            options.oncomplete = function(xhr, status, args, data) {
                if(args.access) {
                    for(var i = 0; i < $this.droppedNodeParams.length; i++) {
                        var params = $this.droppedNodeParams[i];
                        if(params.dropPoint)
                            $this.onDropPoint(params.ui, params.dragSource, params.dragNode, params.targetDragNode, params.dropPoint, params.dropNode, params.transfer);
                        else
                            $this.onDropNode(params.ui, params.dragSource, params.dragNode, params.targetDragNode, params.droppable, params.dropNode, params.transfer);
                    }
                }
            };
        }

        if(this.hasBehavior('dragdrop')) {
            this.callBehavior('dragdrop', options);
        }
        else {
            PrimeFaces.ajax.Request.handle(options);
        }
    }

    /**
     * @override
     * @inheritdoc
     * @return {boolean}
     */
    isEmpty() {
        return (this.container.children().length === 0);
    }

    /**
     * Finds the first node.
     * @return {JQuery} The first node of this tree.
     */
    getFirstNode() {
        return this.jq.find("> ul.p-tree-container > li.p-treenode").first();
    }

    /**
     * Finds the content element for the given node.
     * @param {JQuery} node Node for which to find the corresponding content.
     * @return {JQuery} The element with the content for the given node.
     */
    getNodeContent(node) {
        return node.find('> .p-treenode-content');
    }

    /**
     * @override
     * @protected
     * @inheritdoc
     * @param {JQuery} node
     */
    focusNode(node) {
        if(this.focusedNode) {
            this.getNodeContent(this.focusedNode).removeClass('p-treenode-outline');
        }

        this.getNodeContent(node).addClass('p-treenode-outline').trigger('focus');
        this.focusedNode = node;
    }

    /**
     * Applies the current filter value by sending an AJAX to the server.
     */
    filter() {
        var $this = this,
        options = {
            source: this.id,
            update: this.id,
            process: this.id,
            global: false,
            formId: this.getParentFormId(),
            params: [{name: this.id + '_filtering', value: true},
                     {name: this.id + '_encodeFeature', value: true}],
            onsuccess: function(responseXML, status, xhr) {
                PrimeFaces.ajax.Response.handle(responseXML, status, xhr, {
                    widget: $this,
                    handle: function(content) {
                        $this.container.html(content);
                    }
                });

                return true;
            },
            oncomplete: function() {
                if ($this.cfg.filterMode === 'contains') {
                    var notLeafNodes = $this.container.find('li.p-treenode:not(.p-treenode-leaf):visible');
                    for(var i = 0; i < notLeafNodes.length; i++) {
                        var node = notLeafNodes.eq(i),
                        hasChildNodes = node.children('.p-treenode-children:empty').length;

                        if(hasChildNodes) {
                            node.removeClass('p-treenode-parent').addClass('p-treenode-leaf')
                                .find('> .p-treenode-content > .p-tree-toggler').removeClass('p-tree-toggler p-icon p-toggle-icon p-tree-toggle-icon').addClass('p-treenode-leaf-icon');
                        }
                    }
                }

                if ($this.cfg.draggable) {
                    $this.initDraggable();
                }

                if ($this.cfg.droppable) {
                    $this.initDroppable();
                }
            }
        };

        if(this.hasBehavior('filter')) {
            this.callBehavior('filter', options);
        }
        else {
            PrimeFaces.ajax.Request.handle(options);
        }

    }

    /**
     * Reads the saved scroll position from the hidden input field and applies it.
     * @private
     */
    restoreScrollState() {
        var scrollState = this.scrollStateHolder.val(),
        scrollValues = scrollState.split(',');

        this.jq.scrollLeft(scrollValues[0]);
        this.jq.scrollTop(scrollValues[1]);
    }

    /**
     * Saves the current scroll position to the hidden input field.
     * @private
     */
    saveScrollState() {
        var scrollState = this.jq.scrollLeft() + ',' + this.jq.scrollTop();

        this.scrollStateHolder.val(scrollState);
    }

    /**
     * Resets the value of the hidden input field with the current scroll position.
     * @private
     */
    clearScrollState() {
        this.scrollStateHolder.val('0,0');
    }

}
