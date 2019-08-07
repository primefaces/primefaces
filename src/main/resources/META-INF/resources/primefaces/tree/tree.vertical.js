/**
 * PrimeFaces Vertical Tree Widget
 */
PrimeFaces.widget.VerticalTree = PrimeFaces.widget.BaseTree.extend({

    init: function(cfg) {
        this._super(cfg);

        this.container = this.jq.children('.ui-tree-container');
        this.cfg.rtl = this.jq.hasClass('ui-tree-rtl');
        this.cfg.collapsedIcon = this.cfg.rtl ? 'ui-icon-triangle-1-w' : 'ui-icon-triangle-1-e';
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
    },

    bindEvents: function() {
        var $this = this,
        togglerSelector = '.ui-tree-toggler',
        nodeLabelSelector = '.ui-tree-selectable .ui-treenode-label',
        nodeContentSelector = '.ui-treenode-content';

        this.jq.off('click.tree-toggle', togglerSelector)
                    .on('click.tree-toggle', togglerSelector, null, function(e) {
                        var toggleIcon = $(this),
                        node = toggleIcon.closest('li');

                        if(toggleIcon.hasClass($this.cfg.collapsedIcon))
                            $this.expandNode(node);
                        else
                            $this.collapseNode(node);
                    });

        if(this.cfg.highlight && this.cfg.selectionMode) {
            this.jq.off('mouseout.tree mouseover.tree', nodeLabelSelector)
                        .on('mouseout.tree', nodeLabelSelector, null, function() {
                            var label = $(this);

                            label.removeClass('ui-state-hover');

                            if($this.isCheckboxSelection()) {
                                label.siblings('div.ui-chkbox').children('div.ui-chkbox-box').removeClass('ui-state-hover');
                            }
                        })
                        .on('mouseover.tree', nodeLabelSelector, null, function() {
                            var label = $(this);

                            $(this).addClass('ui-state-hover');

                            if($this.isCheckboxSelection()) {
                                label.siblings('div.ui-chkbox').children('div.ui-chkbox-box').addClass('ui-state-hover');
                            }
                        });
        }

        if(this.isCheckboxSelection()) {
            var checkboxSelector = '.ui-chkbox-box:not(.ui-state-disabled)';

            this.jq.off('mouseout.tree-checkbox mouseover.tree-checkbox click.tree-checkbox', checkboxSelector)
                        .on('mouseout.tree-checkbox', checkboxSelector, null, function() {
                            $(this).removeClass('ui-state-hover').parent().siblings('span.ui-treenode-label').removeClass('ui-state-hover');
                        })
                        .on('mouseover.tree-checkbox', checkboxSelector, null, function() {
                            $(this).addClass('ui-state-hover').parent().siblings('span.ui-treenode-label').addClass('ui-state-hover');
                        });
        }

        this.jq.off('click.tree-content', nodeContentSelector)
                        .on('click.tree-content', nodeContentSelector, null, function(e) {
                            $this.nodeClick(e, $(this));
                        });

        if(this.cfg.filter) {
            this.filterInput = this.jq.find('.ui-tree-filter');
            PrimeFaces.skinInput(this.filterInput);

            this.filterInput.on('keydown.tree-filter', function(e) {
                var key = e.which,
                keyCode = $.ui.keyCode;

                if((key === keyCode.ENTER)) {
                    e.preventDefault();
                }
            })
            .on('keyup.tree-filter', function(e) {
                var keyCode = $.ui.keyCode,
                key = e.which;

                switch(key) {
                    case keyCode.UP:
                    case keyCode.LEFT:
                    case keyCode.DOWN:
                    case keyCode.RIGHT:
                    case keyCode.ENTER:
                    case keyCode.TAB:
                    case keyCode.ESCAPE:
                    case keyCode.SPACE:
                    case keyCode.HOME:
                    case keyCode.PAGE_DOWN:
                    case keyCode.PAGE_UP:
                    case keyCode.END:
                    case keyCode.DELETE:
                    case 16: //shift
                    case 17: //keyCode.CONTROL:
                    case 18: //keyCode.ALT:
                    case 91: //left window or cmd:
                    case 92: //right window:
                    case 93: //right cmd:
                    case 20: //capslock:
                    break;

                    default:
                        //function keys (F1,F2 etc.)
                        if(key >= 112 && key <= 123) {
                            break;
                        }

                        var metaKey = e.metaKey||e.ctrlKey;

                        if(!metaKey) {
                            if($this.filterTimeout) {
                                clearTimeout($this.filterTimeout);
                            }

                            $this.filterTimeout = setTimeout(function() {
                                $this.filter();
                                $this.filterTimeout = null;
                            }, 300);
                        }
                    break;
                }
            });
        }

        this.jq.on('scroll.tree', function(e) {
            $this.saveScrollState();
        });

        this.bindKeyEvents();
    },

    bindKeyEvents: function() {
        var $this = this,
        pressTab = false;

        this.jq.on('mousedown.tree', function(e) {
            if($(e.target).is(':not(:input:enabled)')) {
                e.preventDefault();
            }
        })
        .on('focus.tree', function() {
            if(!$this.focusedNode && !pressTab) {
                $this.focusNode($this.getFirstNode());
            }
        });

        this.jq.off('keydown.tree blur.tree', '.ui-treenode-label').on('keydown.tree', '.ui-treenode-label', null, function(e) {
            if(!$this.focusedNode) {
                return;
            }

            var searchRowkey = "",
            keyCode = $.ui.keyCode;

            switch(e.which) {
                case keyCode.LEFT:
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

                case keyCode.RIGHT:
                    if(!$this.focusedNode.hasClass('ui-treenode-leaf')) {
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

                case keyCode.UP:
                    var nodeToFocus = null,
                    prevNode = $this.focusedNode.prev();

                    if(prevNode.length) {
                        nodeToFocus = prevNode.find('li.ui-treenode:visible:last');
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

                case keyCode.DOWN:
                    var nodeToFocus = null,
                    firstVisibleChildNode = $this.focusedNode.find("> ul > li:visible:first");

                    if(firstVisibleChildNode.length) {
                        nodeToFocus = firstVisibleChildNode;
                    }
                    else if($this.focusedNode.next().length) {
                        nodeToFocus = $this.focusedNode.next();
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

                case keyCode.ENTER:
                case keyCode.SPACE:
                    if($this.cfg.selectionMode) {
                        var selectable = $this.focusedNode.children('.ui-treenode-content').hasClass('ui-tree-selectable');

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

                case keyCode.TAB:
                    pressTab = true;
                    $this.jq.focus();
                    setTimeout(function() {
                        pressTab = false;
                    }, 2);

                break;
            }
        })
        .on('blur.tree', '.ui-treenode-label', null, function(e) {
            if($this.focusedNode) {
                $this.getNodeLabel($this.focusedNode).removeClass('ui-treenode-outline');
                $this.focusedNode = null;
            }
        });

        /* For copy/paste operation on drag and drop */
        $(document.body).on('keydown.tree', function(e) {
            $this.shiftKey = e.shiftKey;
        })
        .on('keyup.tree', function() {
            $this.shiftKey = false;
        });
    },

    searchDown: function(node) {
        var nextOfParent = node.closest('ul').parent('li').next(),
        nodeToFocus = null;

        if(nextOfParent.length) {
            nodeToFocus = nextOfParent;
        }
        else if(node.hasClass('ui-treenode-leaf') && node.closest('ul').parent('li').length == 0){
            nodeToFocus = node;
        }
        else {
            var rowkey = node.data('rowkey').toString();

            if(rowkey.length !== 1) {
                nodeToFocus = this.searchDown(node.closest('ul').parent('li'));
            }
        }

        return nodeToFocus;
    },

    collapseNode: function(node) {
        var _self = this,
        nodeContent = node.find('> .ui-treenode-content'),
        toggleIcon = nodeContent.find('> .ui-tree-toggler'),
        nodeType = node.data('nodetype'),
        nodeIcon = toggleIcon.nextAll('span.ui-treenode-icon'),
        iconState = this.cfg.iconStates[nodeType],
        childrenContainer = node.children('.ui-treenode-children');

        //aria
        nodeContent.find('> .ui-treenode-label').attr('aria-expanded', false);

        toggleIcon.removeClass('ui-icon-triangle-1-s').addClass(_self.cfg.collapsedIcon);

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
    },

    postCollapse: function(node, childrenContainer) {
        if(this.cfg.dynamic && !this.cfg.cache) {
            childrenContainer.empty();
        }

        if(!this.cfg.cache) {
            this.fireCollapseEvent(node);
        }
    },

    //@Override
    getNodeChildrenContainer: function(node) {
        return node.children('.ui-treenode-children');
    },

    //@Override
    showNodeChildren: function(node) {
        var nodeContent = node.find('> .ui-treenode-content'),
        toggleIcon = nodeContent.find('> .ui-tree-toggler'),
        nodeType = node.data('nodetype'),
        nodeIcon = toggleIcon.nextAll('span.ui-treenode-icon'),
        iconState = this.cfg.iconStates[nodeType];

        //aria
        nodeContent.find('> .ui-treenode-label').attr('aria-expanded', true);

        toggleIcon.removeClass(this.cfg.collapsedIcon).addClass('ui-icon-triangle-1-s');

        if(iconState) {
            nodeIcon.removeClass(iconState.collapsedIcon).addClass(iconState.expandedIcon);
        }

        if(this.cfg.animate) {
            node.children('.ui-treenode-children').slideDown('fast');
        }
        else {
            node.children('.ui-treenode-children').show();
        }
    },

    unselectAllNodes: function() {
        this.selections = [];
        this.jq.find('.ui-treenode-label.ui-state-highlight').each(function() {
            $(this).removeClass('ui-state-highlight').closest('.ui-treenode').attr('aria-selected', false);
        });
    },

    selectNode: function(node, silent) {
        node.attr('aria-selected', true)
            .find('> .ui-treenode-content > .ui-treenode-label').removeClass('ui-state-hover').addClass('ui-state-highlight');

        this.addToSelection(this.getRowKey(node));
        this.writeSelections();

        if(!silent)
            this.fireNodeSelectEvent(node);
    },

    unselectNode: function(node, silent) {
        var rowKey = this.getRowKey(node);

        node.attr('aria-selected', false).
            find('> .ui-treenode-content > .ui-treenode-label').removeClass('ui-state-highlight ui-state-hover');

        this.removeFromSelection(rowKey);
        this.writeSelections();

        if(!silent)
            this.fireNodeUnselectEvent(node);
    },

    toggleCheckboxNode: function(node) {
        var $this = this,
        checkbox = node.find('> .ui-treenode-content > .ui-chkbox'),
        checked = checkbox.find('> .ui-chkbox-box > .ui-chkbox-icon').hasClass('ui-icon-check');

        this.toggleCheckboxState(checkbox, checked);

        if(this.cfg.propagateDown) {
            node.children('.ui-treenode-children').find('.ui-chkbox').each(function() {
                $this.toggleCheckboxState($(this), checked);
            });

            if(this.cfg.dynamic) {
                this.removeDescendantsFromSelection(node.data('rowkey'));
            }
        }

        if(this.cfg.propagateUp) {
            node.parents('li.ui-treenode-parent').each(function() {
                var parentNode = $(this),
                parentsCheckbox = parentNode.find('> .ui-treenode-content > .ui-chkbox'),
                children = parentNode.find('> .ui-treenode-children > .ui-treenode');

                if(checked) {
                    if(children.filter('.ui-treenode-unselected').length === children.length)
                        $this.uncheck(parentsCheckbox);
                    else
                        $this.partialCheck(parentsCheckbox);
                }
                else {
                    if(children.filter('.ui-treenode-selected').length === children.length)
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
    },

    preselectCheckbox: function() {
        this.jq.find('.ui-chkbox-icon').not('.ui-icon-check').each(function() {
            var icon = $(this),
            node = icon.closest('li');

            if(node.children('.ui-treenode-children').find('.ui-chkbox-icon.ui-icon-check').length > 0) {
                node.addClass('ui-treenode-hasselected');
                icon.removeClass('ui-icon-blank').addClass('ui-icon-minus');
            }
        });
    },

    check: function(checkbox) {
        this._super(checkbox);
        checkbox.siblings('span.ui-treenode-label').addClass('ui-state-highlight').removeClass('ui-state-hover');
    },

    uncheck: function(checkbox) {
        this._super(checkbox);
        checkbox.siblings('span.ui-treenode-label').removeClass('ui-state-highlight');
    },

    initDraggable: function() {
        this.makeDraggable(this.jq.find('span.ui-treenode-content'));
    },

    initDroppable: function() {
        this.makeDropPoints(this.jq.find('li.ui-tree-droppoint'));
        this.makeDropNodes(this.jq.find('span.ui-treenode-droppable'));
        this.initDropScrollers();
    },

    makeDraggable: function(elements) {
        var $this = this,
        dragdropScope = this.cfg.dragdropScope||this.id;

        elements.draggable({
            start: function(event, ui) {
                if(ui.helper) {
                    var element = $(event.target),
                    source = PF($(element.data('dragsourceid')).data('widget')),
                    height = 20;

                    if(source.cfg.multipleDrag && element.find('.ui-treenode-label').hasClass('ui-state-highlight')) {
                        source.draggedSourceKeys = $this.findSelectedParentKeys(source.selections.slice());
                        height = 20 * (source.draggedSourceKeys.length || 1);
                    }

                    $(ui.helper).height(height);
                }
            },
            helper: function() {
                var el = $('<div class="ui-tree-draghelper ui-state-highlight"></div>');
                el.width($this.jq.width());
                return el;
            },
            appendTo: document.body,
            zIndex: ++PrimeFaces.zindex,
            revert: true,
            scope: dragdropScope,
            containment: 'document'
        })
        .data({
            'dragsourceid': this.jqId,
            'dragmode': this.cfg.dragMode
        });
    },

    makeDropPoints: function(elements) {
        var $this = this,
        dragdropScope = this.cfg.dragdropScope||this.id;

        elements.droppable({
            hoverClass: 'ui-state-hover',
            accept: 'span.ui-treenode-content',
            tolerance: 'pointer',
            scope: dragdropScope,
            drop: function(event, ui) {
                var dragSource = PF($(ui.draggable.data('dragsourceid')).data('widget')),
                dropSource = $this,
                dropPoint = $(this),
                dropNode = dropPoint.closest('li.ui-treenode-parent'),
                dropNodeKey = $this.getRowKey(dropNode),
                transfer = (dragSource.id !== dropSource.id),
                draggedSourceKeys = dragSource.draggedSourceKeys,
                isDroppedNodeCopy = ($this.cfg.dropCopyNode && $this.shiftKey),
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
                    dragNode = draggedNode.is('li.ui-treenode') ? draggedNode : draggedNode.closest('li.ui-treenode'),
                    dragNode = (isDroppedNodeCopy) ? dragNode.clone() : dragNode,
                    targetDragNode = $this.findTargetDragNode(dragNode, dragMode);

                    dragNodeKey = $this.getRowKey(targetDragNode);

                    if(!transfer && dropNodeKey && dropNodeKey.indexOf(dragNodeKey) === 0) {
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

                if (draggedSourceKeys && draggedSourceKeys.length) {
                    draggedSourceKeys = draggedSourceKeys.reverse().join(',');
                    $this.fireDragDropEvent({
                        'dragNodeKey': draggedSourceKeys,
                        'dropNodeKey': dropNodeKey,
                        'dragSource': dragSource.id,
                        'dndIndex': dropPoint.prevAll('li.ui-treenode').length,
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
    },

    onDropPoint: function(ui, dragSource, dragNode, targetDragNode, dropPoint, dropNode, transfer) {
        var dragNodeDropPoint = targetDragNode.next('li.ui-tree-droppoint'),
        oldParentNode = targetDragNode.parent().closest('li.ui-treenode-parent');

        ui.helper.remove();
        dropPoint.removeClass('ui-state-hover');

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

        if(oldParentNode.length && (oldParentNode.find('> ul.ui-treenode-children > li.ui-treenode').length === 0)) {
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
    },

    makeDropNodes: function(elements) {
        var $this = this,
        dragdropScope = this.cfg.dragdropScope||this.id;

        elements.droppable({
            accept: '.ui-treenode-content',
            tolerance: 'pointer',
            scope: dragdropScope,
            over: function(event, ui) {
                $(this).children('.ui-treenode-label').addClass('ui-state-hover');
            },
            out: function(event, ui) {
                $(this).children('.ui-treenode-label').removeClass('ui-state-hover');
            },
            drop: function(event, ui) {
                var dragSource = PF($(ui.draggable.data('dragsourceid')).data('widget')),
                dropSource = $this,
                droppable = $(this),
                dropNode = droppable.closest('li.ui-treenode'),
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
                    dragNode = draggedNode.is('li.ui-treenode') ? draggedNode : draggedNode.closest('li.ui-treenode'),
                    dragNode = (isDroppedNodeCopy) ? dragNode.clone() : dragNode,
                    targetDragNode = $this.findTargetDragNode(dragNode, dragMode);

                    if(i === 0) {
                        dndIndex = dropNode.find('>.ui-treenode-children>li.ui-treenode').length;
                    }

                    dragNodeKey = $this.getRowKey(targetDragNode);

                    if(!transfer && dropNodeKey && dropNodeKey.indexOf(dragNodeKey) === 0) {
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
    },

    onDropNode: function(ui, dragSource, dragNode, targetDragNode, droppable, dropNode, transfer) {
        var dragNodeDropPoint = targetDragNode.next('li.ui-tree-droppoint'),
        oldParentNode = targetDragNode.parent().closest('li.ui-treenode-parent'),
        childrenContainer = dropNode.children('.ui-treenode-children');

        ui.helper.remove();
        droppable.children('.ui-treenode-label').removeClass('ui-state-hover');

        var validDrop = this.validateDropNode(dragNode, dropNode, oldParentNode);
        if(!validDrop) {
            if (this.invalidSourceKeys) {
                var dragNodeKey = this.getRowKey(targetDragNode);
                this.invalidSourceKeys.push(dragNodeKey);
            }
            return;
        }

        if(childrenContainer.children('li.ui-treenode').length === 0) {
            this.makeParent(dropNode);
        }

        targetDragNode.hide();
        childrenContainer.append(targetDragNode);

        if(oldParentNode.length && (oldParentNode.find('> ul.ui-treenode-children > li.ui-treenode').length === 0)) {
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
    },

    findSelectedParentKeys: function(arr) {
        for(var i = 0; i < arr.length; i++) {
            var key = arr[i];
            for(var j = 0; j < arr.length && key !== -1; j++) {
                var tempKey = arr[j];
                if(tempKey !== -1 && key.length > tempKey.length && key.indexOf(tempKey) === 0) {
                    arr[i] = -1;
                }
            }
        }

        return arr.filter(function(item) {
            return item !== -1;
        });
    },

    initDropScrollers: function() {
        var $this = this,
        dragdropScope = this.cfg.dragdropScope||this.id;

        this.jq.prepend('<div class="ui-tree-scroller ui-tree-scrollertop"></div>').append('<div class="ui-tree-scroller ui-tree-scrollerbottom"></div>');

        this.jq.children('div.ui-tree-scroller').droppable({
            accept: '.ui-treenode-content',
            tolerance: 'pointer',
            scope: dragdropScope,
            over: function() {
                var step = $(this).hasClass('ui-tree-scrollertop') ? -10 : 10;

                $this.scrollInterval = setInterval(function() {
                    $this.scroll(step);
                }, 100);
            },
            out: function() {
                clearInterval($this.scrollInterval);
            }
        });
    },

    scroll: function(step) {
        this.container.scrollTop(this.container.scrollTop() + step);
    },

    updateDragDropBindings: function(node) {
        //self droppoint
        node.after('<li class="ui-tree-droppoint ui-droppable"></li>');
        this.makeDropPoints(node.next('li.ui-tree-droppoint'));

        //descendant droppoints
        var subtreeDropPoints = node.find('li.ui-tree-droppoint');
        if(subtreeDropPoints.hasClass('ui-droppable') && !this.shiftKey && !this.cfg.dropCopyNode) {
            subtreeDropPoints.droppable('destroy');
        }
        this.makeDropPoints(subtreeDropPoints);

        //descendant drop node contents
        var subtreeDropNodeContents = node.find('span.ui-treenode-content');
        if(subtreeDropNodeContents.hasClass('ui-droppable') && !this.shiftKey && !this.cfg.dropCopyNode) {
            subtreeDropNodeContents.droppable('destroy');
        }
        this.makeDropNodes(subtreeDropNodeContents);

        if(this.cfg.draggable) {
            subtreeDropNodeContents.data({
                'dragsourceid': this.jqId,
                'dragmode': this.cfg.dragMode
            });
        }
    },

    findTargetDragNode: function(dragNode, dragMode) {
        var targetDragNode = null;

        if(dragMode === 'self') {
            targetDragNode = dragNode;
        } else if(dragMode === 'parent') {
            targetDragNode = dragNode.parent().closest('li.ui-treenode');
        } else if(dragMode === 'ancestor') {
            targetDragNode = dragNode.parent().parents('li.ui-treenode:last');
        }

        if(targetDragNode.length === 0) {
            targetDragNode = dragNode;
        }

        return targetDragNode;
    },

    findNodes: function(rowkeys) {
        var nodes = [];
        for(var i = 0; i < rowkeys.length; i++) {
            nodes.push($(this.jqId + '\\:' + rowkeys[i]));
        }

        return nodes;
    },

    updateRowKeys: function() {
        var children = this.jq.find('> ul.ui-tree-container > li.ui-treenode');
        this.updateChildrenRowKeys(children, null);
    },

    updateChildrenRowKeys: function(children, rowkey) {
        var $this = this;

        children.each(function(i) {
            var childNode = $(this),
            oldRowKey = childNode.attr('data-rowkey'),
            newRowKey = (rowkey === null) ? i.toString() : rowkey + '_' + i;

            childNode.attr({
                'id': $this.id + ':' + newRowKey,
                'data-rowkey' : newRowKey
            });

            if(childNode.hasClass('ui-treenode-parent')) {
                $this.updateChildrenRowKeys(childNode.find('> ul.ui-treenode-children > li.ui-treenode'), newRowKey);
            }
        });
    },

    validateDropPoint: function(dragNode, dropPoint) {
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
    },

    validateDropNode: function(dragNode, dropNode, oldParentNode) {
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
    },

    makeLeaf: function(node) {
        node.removeClass('ui-treenode-parent').addClass('ui-treenode-leaf');
        node.find('> .ui-treenode-content > .ui-tree-toggler').addClass('ui-treenode-leaf-icon').removeClass('ui-tree-toggler ui-icon ui-icon-triangle-1-s');
        node.children('.ui-treenode-children').hide().children().remove();
    },

    makeParent: function(node) {
        node.removeClass('ui-treenode-leaf').addClass('ui-treenode-parent');
        node.find('> span.ui-treenode-content > span.ui-treenode-leaf-icon').removeClass('ui-treenode-leaf-icon').addClass('ui-tree-toggler ui-icon ui-icon-triangle-1-e');
        node.children('.ui-treenode-children').append('<li class="ui-tree-droppoint ui-droppable"></li>');

        this.makeDropPoints(node.find('> ul.ui-treenode-children > li.ui-tree-droppoint'));
    },

    syncDragDrop: function() {
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
    },

    syncDNDCheckboxes: function(dragSource, oldParentNode, newParentNode) {
        if(oldParentNode.length) {
            dragSource.propagateDNDCheckbox(oldParentNode);
        }

        if(newParentNode.length) {
            this.propagateDNDCheckbox(newParentNode);
        }
    },

    unselectSubtree: function(node) {
        var $this = this;

        if(this.isCheckboxSelection()) {
            var checkbox = node.find('> .ui-treenode-content > .ui-chkbox');

            this.toggleCheckboxState(checkbox, true);

            node.children('.ui-treenode-children').find('.ui-chkbox').each(function() {
                $this.toggleCheckboxState($(this), true);
            });
        }
        else {
            node.find('.ui-treenode-label.ui-state-highlight').each(function() {
                $(this).removeClass('ui-state-highlight').closest('li.ui-treenode').attr('aria-selected', false);
            });
        }
    },

    propagateDNDCheckbox: function(node) {
        var checkbox = node.find('> .ui-treenode-content > .ui-chkbox'),
        children = node.find('> .ui-treenode-children > .ui-treenode');

        if(children.length) {
            if(children.filter('.ui-treenode-unselected').length === children.length)
                this.uncheck(checkbox);
            else if(children.filter('.ui-treenode-selected').length === children.length)
                this.check(checkbox);
            else
                this.partialCheck(checkbox);
        }

        var parent = node.parent().closest('.ui-treenode-parent');
        if(parent.length) {
            this.propagateDNDCheckbox(parent);
        }
    },

    fireDragDropEvent: function(event) {
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
    },

    isEmpty: function() {
        return (this.container.children().length === 0);
    },

    getFirstNode: function() {
        return this.jq.find('> ul.ui-tree-container > li:first-child');
    },

    getNodeLabel: function(node) {
        return node.find('> span.ui-treenode-content > span.ui-treenode-label');
    },

    focusNode: function(node) {
        if(this.focusedNode) {
            this.getNodeLabel(this.focusedNode).removeClass('ui-treenode-outline');
        }

        this.getNodeLabel(node).addClass('ui-treenode-outline').focus();
        this.focusedNode = node;
    },

    /**
     * Ajax filter
     */
    filter: function() {
        var $this = this,
        options = {
            source: this.id,
            update: this.id,
            process: this.id,
            global: false,
            formId: this.cfg.formId,
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
                    var notLeafNodes = $this.container.find('li.ui-treenode:not(.ui-treenode-leaf):visible');
                    for(var i = 0; i < notLeafNodes.length; i++) {
                        var node = notLeafNodes.eq(i),
                        hasChildNodes = node.children('.ui-treenode-children:empty').length;

                        if(hasChildNodes) {
                            node.removeClass('ui-treenode-parent').addClass('ui-treenode-leaf')
                                .find('> .ui-treenode-content > .ui-tree-toggler').removeClass('ui-tree-toggler ui-icon ui-icon-triangle-1-e').addClass('ui-treenode-leaf-icon');
                        }
                    }
                }
            }
        };

        if(this.hasBehavior('filter')) {
            this.callBehavior('filter', options);
        }
        else {
            PrimeFaces.ajax.Request.handle(options);
        }

    },

    restoreScrollState: function() {
        var scrollState = this.scrollStateHolder.val(),
        scrollValues = scrollState.split(',');

        this.jq.scrollLeft(scrollValues[0]);
        this.jq.scrollTop(scrollValues[1]);
    },

    saveScrollState: function() {
        var scrollState = this.jq.scrollLeft() + ',' + this.jq.scrollTop();

        this.scrollStateHolder.val(scrollState);
    },

    clearScrollState: function() {
        this.scrollStateHolder.val('0,0');
    }

});
