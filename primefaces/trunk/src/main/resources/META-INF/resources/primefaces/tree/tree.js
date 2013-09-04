/**
 * PrimeFaces Base Tree Widget
 */
PrimeFaces.widget.BaseTree = PrimeFaces.widget.BaseWidget.extend({
    
    init: function(cfg) {
        this._super(cfg);
        this.cfg.highlight = (this.cfg.highlight === false) ? false : true;

        if(this.cfg.selectionMode) {
            this.initSelection();
        }

        this.bindEvents();
        
        this.jq.data('widget', this);
    },
    
    initSelection: function() {
        this.selectionHolder = $(this.jqId + '_selection');
        var selectionsValue = this.selectionHolder.val();
        this.selections = selectionsValue === '' ? [] : selectionsValue.split(',');

        if(this.isCheckboxSelection()) {
            this.preselectCheckbox();
        }
    },
    
    expandNode: function(node) {    
        var _self = this;

        if(this.cfg.dynamic) {

            if(this.cfg.cache && _self.getNodeChildrenContainer(node).children().length > 0) {
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
                formId: this.cfg.formId
            };

            options.onsuccess = function(responseXML) {
                var xmlDoc = $(responseXML.documentElement),
                updates = xmlDoc.find("update");
                
                for(var i=0; i < updates.length; i++) {
                    var update = updates.eq(i),
                    id = update.attr('id'),
                    content = update.get(0).childNodes[0].nodeValue;

                    if(id === _self.id) {
                        var nodeChildrenContainer = _self.getNodeChildrenContainer(node);
                        nodeChildrenContainer.append(content);

                        _self.showNodeChildren(node);
  
                        if(_self.cfg.draggable) {                            
                            _self.makeDraggable(nodeChildrenContainer.find('span.ui-treenode-content'));
                        }
                        
                        if(_self.cfg.droppable) {
                            _self.makeDropPoints(nodeChildrenContainer.find('li.ui-tree-droppoint'));
                            _self.makeDropNodes(nodeChildrenContainer.find('span.ui-treenode-droppable'));
                        }
                    }
                    else {
                        PrimeFaces.ajax.AjaxUtils.updateElement.call(this, id, content);
                    }
                }

                PrimeFaces.ajax.AjaxUtils.handleResponse.call(this, xmlDoc);

                return true;
            };

            options.oncomplete = function() {
                node.removeData('processing');
            }

            options.params = [
                {name: this.id + '_expandNode', value: _self.getRowKey(node)}
            ];

            if(this.hasBehavior('expand')) {
                var expandBehavior = this.cfg.behaviors['expand'];

                expandBehavior.call(this, node, options);
            }
            else {
                PrimeFaces.ajax.AjaxRequest(options);
            }
        }
        else {
            this.showNodeChildren(node);
            this.fireExpandEvent(node);
        }
    },
    
    fireExpandEvent: function(node) {
        if(this.cfg.behaviors) {
            var expandBehavior = this.cfg.behaviors['expand'];
            if(expandBehavior) {
                var ext = {
                    params: [
                        {name: this.id + '_expandNode', value: this.getRowKey(node)}
                    ]
                };

                expandBehavior.call(this, node, ext);
            }
        }
    },
    
    fireCollapseEvent: function(node) {
        if(this.cfg.behaviors) {
            var collapseBehavior = this.cfg.behaviors['collapse'];
            if(collapseBehavior) {
                var ext = {
                    params: [
                        {name: this.id + '_collapseNode', value: this.getRowKey(node)}
                    ]
                };

                collapseBehavior.call(this, node, ext);
            }
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
        if(this.cfg.behaviors) {
            var selectBehavior = this.cfg.behaviors['select'];

            if(selectBehavior) {
                var ext = {
                    params: [
                        {name: this.id + '_instantSelection', value: this.getRowKey(node)}
                    ]
                };

                selectBehavior.call(this, node, ext);
            }
        }
    },
    
    fireNodeUnselectEvent: function(node) {
        if(this.cfg.behaviors) {
            var unselectBehavior = this.cfg.behaviors['unselect'];

            if(unselectBehavior) {
                var ext = {
                    params: [
                        {name: this.id + '_instantUnselection', value: this.getRowKey(node)}
                    ]
                };

                unselectBehavior.call(this, node, ext);
            }
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
            return r != rowKey;
        });
    },
    
    hasBehavior: function(event) {
        if(this.cfg.behaviors) {
            return this.cfg.behaviors[event] != undefined;
        }

        return false;
    },
       
    nodeClick: function(event, nodeContent) {
        PrimeFaces.clearSelection();
        
        if($(event.target).is(':not(.ui-tree-toggler)')) {
            var node = nodeContent.parent(),
            selectable = nodeContent.hasClass('ui-tree-selectable');
                    
            if(this.cfg.onNodeClick) {
                this.cfg.onNodeClick.call(this, node, event);
            }
            
            if(nodeContent.hasClass('ui-tree-selectable') && (this.cfg.selectionMode||this.cfg.draggable)) {
                var selected = this.isNodeSelected(node),
                metaKey = event.metaKey||event.ctrlKey,
                shiftKey = event.shiftKey;
                
                if(this.isCheckboxSelection()) {
                    this.toggleCheckboxNode(node);
                }
                else {
                    if(selected && metaKey) {
                        this.unselectNode(node);
                    }
                    else {
                        if(this.isSingleSelection()||(this.isMultipleSelection() && !metaKey)) {
                            this.unselectAllNodes();
                        }

                        if(this.isMultipleSelection && shiftKey) {
                            //TODO: Range selection
                        }
                        else {
                            this.selectNode(node);
                            this.cursorNode = node;
                        }
                    }
                }
            }
        }
    },
    
    bindEvents: function() {
        throw "Unsupported Operation";
    },
    
    selectNode: function(node) {        
        throw "Unsupported Operation";
    },
    
    unselectNode: function(node) {        
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
    
    toggleCheckboxState: function(checkbox, checked) {
        if(checked)
            this.uncheck(checkbox);
        else
            this.check(checkbox);
    },
    
    partialCheck: function(checkbox) {
        var icon = checkbox.find('> .ui-chkbox-box > .ui-chkbox-icon'),
        treeNode = checkbox.closest('.ui-treenode'),
        rowKey = this.getRowKey(treeNode);
        
        this.removeFromSelection(rowKey);
        
        treeNode.find('> .ui-treenode-content > .ui-treenode-label').removeClass('ui-state-highlight');
        icon.removeClass('ui-icon ui-icon-check').addClass('ui-icon ui-icon-minus');
        treeNode.removeClass('ui-treenode-selected ui-treenode-unselected').addClass('ui-treenode-hasselected').attr('aria-checked', false).attr('aria-selected', false);
    },
        
    check: function(checkbox) {
        var box = checkbox.children('.ui-chkbox-box'),
        icon = box.children('.ui-chkbox-icon'),
        treeNode = checkbox.closest('.ui-treenode'),
        rowKey = this.getRowKey(treeNode);

        box.removeClass('ui-state-hover');
        icon.removeClass('ui-icon ui-icon-minus').addClass('ui-icon ui-icon-check');
        checkbox.siblings('span.ui-treenode-label').addClass('ui-state-highlight').removeClass('ui-state-hover');
        this.addToSelection(rowKey);
        treeNode.removeClass('ui-treenode-hasselected ui-treenode-unselected').addClass('ui-treenode-selected').attr('aria-checked', true).attr('aria-selected', true);
    },
    
    uncheck: function(checkbox) {
        var box = checkbox.children('.ui-chkbox-box'),
        icon = box.children('.ui-chkbox-icon'),
        treeNode = checkbox.closest('.ui-treenode'),
        rowKey = this.getRowKey(treeNode);
        
        box.removeClass('ui-state-hover');
        icon.removeClass('ui-icon ui-icon-minus ui-icon-check');
        checkbox.siblings('span.ui-treenode-label').removeClass('ui-state-highlight');
        this.removeFromSelection(rowKey);
        treeNode.removeClass('ui-treenode-hasselected ui-treenode-selected').addClass('ui-treenode-unselected').attr('aria-checked', false).attr('aria-selected', false);
    }
    
});

/**
 * PrimeFaces Vertical Tree Widget
 */
PrimeFaces.widget.VerticalTree = PrimeFaces.widget.BaseTree.extend({
        
    init: function(cfg) {
        this._super(cfg);
    
        this.container = this.jq.children('.ui-tree-container');
        this.cfg.rtl = this.jq.hasClass('ui-tree-rtl');
        this.cfg.collapsedIcon = this.cfg.rtl ? 'ui-icon-triangle-1-w' : 'ui-icon-triangle-1-e';
        
        if(this.cfg.draggable) {
            this.initDraggable();
        }
        
        if(this.cfg.droppable) {
            this.initDroppable();
        }
    },
        
    bindEvents: function() {
        var $this = this,
        togglerSelector = this.jqId + ' .ui-tree-toggler',
        nodeLabelSelector = this.jqId  + ' .ui-tree-selectable .ui-treenode-label',
        nodeContentSelector = this.jqId + ' .ui-treenode-content';

        $(document).off('click', togglerSelector)
                    .on('click', togglerSelector, null, function(e) {
                        var toggleIcon = $(this),
                        node = toggleIcon.closest('li');

                        if(toggleIcon.hasClass($this.cfg.collapsedIcon))
                            $this.expandNode(node);
                        else
                            $this.collapseNode(node);
                    });

        if(this.cfg.highlight && this.cfg.selectionMode) {
            $(document).off('mouseout.tree mouseover.tree', nodeLabelSelector)
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
            var checkboxSelector = this.jqId + ' .ui-chkbox-box';
            
            $(document).off('mouseout.tree-checkbox mouseover.tree-checkbox click.tree-checkbox', checkboxSelector)
                        .on('mouseout.tree-checkbox', checkboxSelector, null, function() {
                            $(this).removeClass('ui-state-hover').parent().siblings('span.ui-treenode-label').removeClass('ui-state-hover');
                        })
                        .on('mouseover.tree-checkbox', checkboxSelector, null, function() {
                            $(this).addClass('ui-state-hover').parent().siblings('span.ui-treenode-label').addClass('ui-state-hover');
                        });
        }
        
        $(document).off('click.tree', nodeContentSelector)
                        .on('click.tree', nodeContentSelector, null, function(e) {
                            $this.nodeClick(e, $(this));
                        });
    },
            
    collapseNode: function(node) {
        var _self = this;
        
        //aria
        node.attr('aria-expanded', true);
        
        var toggleIcon = node.find('> .ui-treenode-content > .ui-tree-toggler'),
        nodeType = node.data('nodetype'),
        nodeIcon = toggleIcon.next(),
        iconState = this.cfg.iconStates[nodeType],
        childrenContainer = node.children('.ui-treenode-children');
        
        toggleIcon.addClass(_self.cfg.collapsedIcon).removeClass('ui-icon-triangle-1-s');
        
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
        
        this.fireCollapseEvent(node);
    },
        
    //@Override
    getNodeChildrenContainer: function(node) {
        return node.children('.ui-treenode-children');
    },
    
    //@Override
    showNodeChildren: function(node) {
        //aria
        node.attr('aria-expanded', true);
        
        var toggleIcon = node.find('> .ui-treenode-content > .ui-tree-toggler'),
        nodeType = node.data('nodetype'),
        nodeIcon = toggleIcon.next(),
        iconState = this.cfg.iconStates[nodeType];

        toggleIcon.addClass('ui-icon-triangle-1-s').removeClass(this.cfg.collapsedIcon);

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
    
    selectNode: function(node) {        
        node.attr('aria-selected', true)
            .find('> .ui-treenode-content > .ui-treenode-label').removeClass('ui-state-hover').addClass('ui-state-highlight');

        this.addToSelection(this.getRowKey(node));
        this.writeSelections();        
        this.fireNodeSelectEvent(node);
    },
    
    unselectNode: function(node) {
        var rowKey = this.getRowKey(node);
           
        node.attr('aria-selected', false).
            find('> .ui-treenode-content > .ui-treenode-label').removeClass('ui-state-highlight ui-state-hover');

        this.removeFromSelection(rowKey);

        this.writeSelections();

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
                icon.addClass('ui-icon ui-icon-minus');
            }
        });
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
            helper: function() {
                var el = $('<div class="ui-tree-draghelper ui-state-highlight"></div>');
                el.width($this.jq.width());
                el.height(20);
                
                return el;
            },
            appendTo: document.body,
            zIndex: ++PrimeFaces.zindex,
            revert: true,
            scope: dragdropScope
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
                var dragSource = $(ui.draggable.data('dragsourceid')).data('widget'),
                dragMode = ui.draggable.data('dragmode'),
                dropSource = $this,
                dropPoint = $(this),
                dropNode = dropPoint.closest('li.ui-treenode-parent'),
                dropNodeKey = $this.getRowKey(dropNode),
                dragNode = ui.draggable.closest('li.ui-treenode'),
                targetDragNode = $this.findTargetDragNode(dragNode, dragMode),
                dragNodeKey = $this.getRowKey(targetDragNode),
                dragNodeContent = dragNode.children('span.ui-treenode-content'),
                dragNodeLabel = dragNodeContent.children('span.ui-treenode-label'),
                dragNodeDropPoint = targetDragNode.next('li.ui-tree-droppoint'),
                oldParentNode = targetDragNode.parent().closest('li.ui-treenode-parent'),
                transfer = (dragSource.id !== dropSource.id);
                
                ui.helper.remove();
                dropPoint.removeClass('ui-state-hover');
                
                var validDrop = $this.validateDropPoint(dragNode, dropPoint);
                if(!validDrop) {
                    return;
                }

                targetDragNode.hide().insertAfter(dropPoint);

                if(transfer) {
                    if(dragSource.isCheckboxSelection()) {
                        dragSource.unselectSubtree(targetDragNode);
                    }
                    
                    dragNodeLabel.removeClass('ui-state-highlight');
                    dragNodeDropPoint.remove();
                    $this.updateDragDropBindings(targetDragNode);
                }
                else {
                    dragNodeDropPoint.insertAfter(targetDragNode);
                }

                if(oldParentNode.length && (oldParentNode.find('> ul.ui-treenode-children > li.ui-treenode').length === 0)) {
                    $this.makeLeaf(oldParentNode);
                }

                targetDragNode.fadeIn();
                
                if($this.isCheckboxSelection()) {                                                
                    $this.syncDNDCheckboxes(dragSource, oldParentNode, dropNode);
                }
                
                $this.syncDragDrop();
                if(transfer) {
                    dragSource.syncDragDrop();
                }
                
                $this.fireDragDropEvent({
                    'dragNodeKey': dragNodeKey,
                    'dropNodeKey': dropNodeKey,
                    'dragSource': dragSource.id,
                    'dndIndex': dropPoint.prevAll('li.ui-treenode').length,
                    'transfer': transfer
                });
            }
        });
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
                var dragSource = $(ui.draggable.data('dragsourceid')).data('widget'),
                dragMode = ui.draggable.data('dragmode'),
                dropSource = $this,
                droppable = $(this),
                dropNode = droppable.closest('li.ui-treenode'),
                dropNodeKey = $this.getRowKey(dropNode),
                dragNode = ui.draggable.closest('li.ui-treenode'),
                targetDragNode = $this.findTargetDragNode(dragNode, dragMode),
                dragNodeKey = $this.getRowKey(targetDragNode),
                dragNodeContent = dragNode.children('span.ui-treenode-content'),
                dragNodeLabel = dragNodeContent.children('span.ui-treenode-label'),
                dragNodeDropPoint = targetDragNode.next('li.ui-tree-droppoint'),
                oldParentNode = targetDragNode.parent().closest('li.ui-treenode-parent'),
                childrenContainer = dropNode.children('.ui-treenode-children'),
                transfer = (dragSource.id !== dropSource.id);

                ui.helper.remove();
                droppable.children('.ui-treenode-label').removeClass('ui-state-hover');
                
                var validDrop = $this.validateDropNode(dragNode, dropNode, oldParentNode);  
                if(!validDrop) {
                    return;
                }
                
                if(childrenContainer.children('li.ui-treenode').length === 0) {
                    $this.makeParent(dropNode);
                }

                targetDragNode.hide();
                childrenContainer.append(targetDragNode);

                if(oldParentNode.length && (oldParentNode.find('> ul.ui-treenode-children > li.ui-treenode').length === 0)) {
                    $this.makeLeaf(oldParentNode);
                }

                if(transfer) {
                    if(dragSource.isCheckboxSelection()) {
                        dragSource.unselectSubtree(targetDragNode);
                    }
                    
                    dragNodeLabel.removeClass('ui-state-highlight');
                    dragNodeDropPoint.remove();
                    $this.updateDragDropBindings(targetDragNode);
                }
                else {
                    childrenContainer.append(dragNodeDropPoint);
                }

                targetDragNode.fadeIn();
                
                if($this.isCheckboxSelection()) {                                                
                    $this.syncDNDCheckboxes(dragSource, oldParentNode, dropNode);
                }
                
                $this.syncDragDrop();
                if(transfer) {
                    dragSource.syncDragDrop();
                }

                $this.fireDragDropEvent({
                    'dragNodeKey': dragNodeKey,
                    'dropNodeKey': dropNodeKey,
                    'dragSource': dragSource.id,
                    'dndIndex': targetDragNode.prevAll('li.ui-treenode').length,
                    'transfer': transfer
                });
            }
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
        subtreeDropPoints.droppable('destroy');
        this.makeDropPoints(subtreeDropPoints);

        //descendant drop node contents
        var subtreeDropNodeContents = node.find('span.ui-treenode-content');
        subtreeDropNodeContents.droppable('destroy');
        this.makeDropNodes(subtreeDropNodeContents);

        subtreeDropNodeContents.draggable('destroy');
        if(this.cfg.draggable) {
            this.makeDraggable(subtreeDropNodeContents);
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
        var $this = this,
        checkbox = node.find('> .ui-treenode-content > .ui-chkbox');

        this.toggleCheckboxState(checkbox, true);
        
        node.children('.ui-treenode-children').find('.ui-chkbox').each(function() {
            $this.toggleCheckboxState($(this), true);
        });
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
            {name: this.id + '_dndIndex', value: event.dndIndex}
        ];
        
        if(this.hasBehavior('dragdrop')) {
            var dragdropBehavior = this.cfg.behaviors['dragdrop'];

            dragdropBehavior.call(this, event, options);
        }
        else {
            PrimeFaces.ajax.AjaxRequest(options);
        }
    }
});

/**
 * PrimeFaces Horizontal Tree Widget
 */
PrimeFaces.widget.HorizontalTree = PrimeFaces.widget.BaseTree.extend({
        
    init: function(cfg) {
        this._super(cfg);
        
        if($.browser.msie) {
            this.drawConnectors();
        }
    },
    
    //@Override
    bindEvents: function() {
        var $this = this,
        selectionMode = this.cfg.selectionMode,
        togglerSelector = this.jqId + ' .ui-tree-toggler',
        nodeContentSelector = this.jqId + ' .ui-treenode-content.ui-tree-selectable';

        $(document).off('click.tree', togglerSelector)
                    .on('click.tree', togglerSelector, null, function() {
                        var icon = $(this),
                        node = icon.closest('td.ui-treenode');
                        
                        if(node.hasClass('ui-treenode-collapsed'))
                            $this.expandNode(node);
                        else
                            $this.collapseNode(node);
                    });
                    
        if(selectionMode && this.cfg.highlight) {
            $(document).off('mouseout.tree mouseover.tree', nodeContentSelector)
                        .on('mouseover.tree', nodeContentSelector, null, function() {
                            var nodeContent = $(this);
                            if(!nodeContent.hasClass('ui-state-highlight')) {
                                nodeContent.addClass('ui-state-hover');
                                
                                if($this.isCheckboxSelection()) {
                                    nodeContent.children('div.ui-chkbox').children('div.ui-chkbox-box').addClass('ui-state-hover');
                                }
                            }
                        })
                        .on('mouseout.tree', nodeContentSelector, null, function() {
                            var nodeContent = $(this);
                            if(!nodeContent.hasClass('ui-state-highlight')) {
                                nodeContent.removeClass('ui-state-hover');
                                
                                if($this.isCheckboxSelection()) {
                                    nodeContent.children('div.ui-chkbox').children('div.ui-chkbox-box').removeClass('ui-state-hover');
                                }
                            }
                        });
        }
        
        $(document).off('click.tree', nodeContentSelector)
                .on('click.tree', nodeContentSelector, null, function(e) {
                    $this.nodeClick(e, $(this));
                });

    },
    
    //@Override
    showNodeChildren: function(node) {
        node.attr('aria-expanded', true);
                
        var childrenContainer = node.next(),
        toggleIcon = node.find('> .ui-treenode-content > .ui-tree-toggler'),
        nodeType = node.data('nodetype'),
        iconState = this.cfg.iconStates[nodeType];
        
        if(iconState) {
            toggleIcon.next().removeClass(iconState.collapsedIcon).addClass(iconState.expandedIcon);
        }
        
        toggleIcon.addClass('ui-icon-minus').removeClass('ui-icon-plus');
        node.removeClass('ui-treenode-collapsed');
        childrenContainer.show();
        
        if($.browser.msie) {
            this.drawConnectors();
        }
    },
    
    collapseNode: function(node) {
        var childrenContainer = node.next(),
        toggleIcon = node.find('> .ui-treenode-content > .ui-tree-toggler'),
        nodeType = node.data('nodetype'),
        iconState = this.cfg.iconStates[nodeType];
        
        if(iconState) {
            toggleIcon.next().addClass(iconState.collapsedIcon).removeClass(iconState.expandedIcon);
        }
        
        toggleIcon.removeClass('ui-icon-minus').addClass('ui-icon-plus');
        node.addClass('ui-treenode-collapsed');
        childrenContainer.hide();
        
        if(this.cfg.dynamic && !this.cfg.cache) {
            childrenContainer.children('.ui-treenode-children').empty();
        }
        
        this.fireCollapseEvent(node);
        
        if($.browser.msie) {
            this.drawConnectors();
        }
    },
    
    //@Override
    getNodeChildrenContainer: function(node) {
        return node.next('.ui-treenode-children-container').children('.ui-treenode-children');
    },
    
    selectNode: function(node) {        
        node.removeClass('ui-treenode-unselected').addClass('ui-treenode-selected').children('.ui-treenode-content').removeClass('ui-state-hover').addClass('ui-state-highlight');

        this.addToSelection(this.getRowKey(node));

        this.writeSelections();

        this.fireNodeSelectEvent(node);
    },
    
    unselectNode: function(node) {
        var rowKey = this.getRowKey(node);
           
        node.removeClass('ui-treenode-selected').addClass('ui-treenode-unselected').children('.ui-treenode-content').removeClass('ui-state-highlight');

        this.removeFromSelection(rowKey);

        this.writeSelections();

        this.fireNodeUnselectEvent(node);
    },
    
    unselectAllNodes: function() {
        this.selections = [];
        this.jq.find('.ui-treenode-content.ui-state-highlight').each(function() {
            $(this).removeClass('ui-state-highlight').closest('.ui-treenode').attr('aria-selected', false);
        });
    },
    
    preselectCheckbox: function() {
        var _self = this;
        
        this.jq.find('.ui-chkbox-icon').not('.ui-icon-check').each(function() {
            var icon = $(this),
            node = icon.closest('.ui-treenode'),
            childrenContainer = _self.getNodeChildrenContainer(node);

            if(childrenContainer.find('.ui-chkbox-icon.ui-icon-check').length > 0) {
                icon.addClass('ui-icon ui-icon-minus');
            }
        });
    },
    
    toggleCheckboxNode: function(node) {
        var $this = this,
        checkbox = node.find('> .ui-treenode-content > .ui-chkbox'),
        checked = checkbox.find('> .ui-chkbox-box > .ui-chkbox-icon').hasClass('ui-icon-check');

        this.toggleCheckboxState(checkbox, checked);

        if(this.cfg.propagateDown) {
            node.next('.ui-treenode-children-container').find('.ui-chkbox').each(function() {
                $this.toggleCheckboxState($(this), checked);
            });
        }

        if(this.cfg.propagateUp) {
            node.parents('td.ui-treenode-children-container').each(function() {
                var childrenContainer = $(this),
                parentNode = childrenContainer.prev('.ui-treenode-parent'),
                parentsCheckbox = parentNode.find('> .ui-treenode-content > .ui-chkbox'),
                children = childrenContainer.find('> .ui-treenode-children > table > tbody > tr > td.ui-treenode');
                
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
            
    drawConnectors: function() {
        this.jq.find('table.ui-treenode-connector-table').each(function() {
            var table = $(this);

            table.height(0).height(table.parent().height());
        });
    }
    
});