/**
 * PrimeFaces Base Tree Widget
 */
PrimeFaces.widget.BaseTree = PrimeFaces.widget.BaseWidget.extend({
    
    init: function(cfg) {
        this._super(cfg);
        this.cfg.highlight = (this.cfg.highlight === false) ? false : true;
        this.focusedNode = null;

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
                var expandBehavior = this.cfg.behaviors['expand'];
                expandBehavior.call(this, options);
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
        if(this.cfg.behaviors) {
            var expandBehavior = this.cfg.behaviors['expand'];
            if(expandBehavior) {
                var ext = {
                    params: [
                        {name: this.id + '_expandNode', value: this.getRowKey(node)}
                    ]
                };

                expandBehavior.call(this, ext);
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

                collapseBehavior.call(this, ext);
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
        if(this.isCheckboxSelection() && this.cfg.dynamic) {
            var $this = this,
            options = {
                source: this.id,
                process: this.id
            };
            
            options.params = [
                {name: this.id + '_instantSelection', value: this.getRowKey(node)}
            ];
            
            options.oncomplete = function(xhr, status, args) {
                if(args.descendantRowKeys && args.descendantRowKeys !== '') {
                    var rowKeys = args.descendantRowKeys.split(',');
                    for(var i = 0; i < rowKeys.length; i++) {
                        $this.addToSelection(rowKeys[i]);
                    }
                    $this.writeSelections();
                }     
            }
            
            if(this.hasBehavior('select')) {
                var selectBehavior = this.cfg.behaviors['select'];
                selectBehavior.call(this, options);
            }
            else {
                PrimeFaces.ajax.AjaxRequest(options);
            }
        }
        else {
            if(this.hasBehavior('select')) {
                var selectBehavior = this.cfg.behaviors['select'],
                ext = {
                    params: [
                        {name: this.id + '_instantSelection', value: this.getRowKey(node)}
                    ]
                };
                
                selectBehavior.call(this, ext);
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

                unselectBehavior.call(this, ext);
            }
        }
    },
            
    fireContextMenuEvent: function(node) {
        if(this.hasBehavior('contextMenu')) {
            var contextMenuBehavior = this.cfg.behaviors['contextMenu'],
            ext = {
                params: [
                    {name: this.id + '_contextMenuNode', value: this.getRowKey(node)}
                ]
            };

            contextMenuBehavior.call(this, ext);
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
            
            if(selectable && (this.cfg.selectionMode||this.cfg.draggable)) {
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

                        this.selectNode(node);
                        this.cursorNode = node;
                    }
                }

                if($(event.target).is(':not(:input:enabled)')) {
                    this.focusNode(node);
                }
            }
        }
    },
            
    nodeRightClick: function(event, nodeContent) {
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
                    }
                }
                
                this.fireContextMenuEvent(node);
            }
        }
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
                        
        this.bindKeyEvents();                
    },
            
    bindKeyEvents: function() {
        var $this = this;

        this.jq.on('mousedown.tree', function(e) {
            if($(e.target).is(':not(:input:enabled)')) {
                e.preventDefault();
            }
        })
        .on('focus.tree', function() {
            if(!$this.focusedNode) {
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
                case keyCode.NUMPAD_ENTER:
                case keyCode.SPACE:
                    if($this.cfg.selectionMode) {
                        var selectable = $this.focusedNode.children('.ui-treenode-content').hasClass('ui-tree-selectable');
                        
                        if($this.cfg.onNodeClick) {
                            $this.cfg.onNodeClick.call($this, $this.focusedNode, e);
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
            }       
        })
        .on('blur.tree', '.ui-treenode-label', null, function(e) {
            if($this.focusedNode) {
                $this.getNodeLabel($this.focusedNode).removeClass('ui-treenode-outline');
                $this.focusedNode = null;
            }
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
        var nodeContent = node.find('> .ui-treenode-content'), 
        toggleIcon = nodeContent.find('> .ui-tree-toggler'),
        nodeType = node.data('nodetype'),
        nodeIcon = toggleIcon.nextAll('span.ui-treenode-icon'),
        iconState = this.cfg.iconStates[nodeType];

        //aria
        nodeContent.find('> .ui-treenode-label').attr('aria-expanded', true);
        
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
                    if(dragSource.cfg.selectionMode) {
                        dragSource.unselectSubtree(targetDragNode);
                    }

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
                    if(dragSource.cfg.selectionMode) {
                        dragSource.unselectSubtree(targetDragNode);
                    }

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
            {name: this.id + '_dndIndex', value: event.dndIndex}
        ];
        
        if(this.hasBehavior('dragdrop')) {
            var dragdropBehavior = this.cfg.behaviors['dragdrop'];

            dragdropBehavior.call(this, options);
        }
        else {
            PrimeFaces.ajax.AjaxRequest(options);
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
    }

});

/**
 * PrimeFaces Horizontal Tree Widget
 */
PrimeFaces.widget.HorizontalTree = PrimeFaces.widget.BaseTree.extend({
        
    init: function(cfg) {
        this._super(cfg);
        
        if(PrimeFaces.isIE()) {
            this.drawConnectors();
        }
    },
    
    //@Override
    bindEvents: function() {
        var $this = this,
        selectionMode = this.cfg.selectionMode,
        togglerSelector = '.ui-tree-toggler',
        nodeContentSelector = '.ui-treenode-content.ui-tree-selectable';

        this.jq.off('click.tree-toggle', togglerSelector)
                    .on('click.tree-toggle', togglerSelector, null, function() {
                        var icon = $(this),
                        node = icon.closest('td.ui-treenode');
                        
                        if(node.hasClass('ui-treenode-collapsed'))
                            $this.expandNode(node);
                        else
                            $this.collapseNode(node);
                    });
                    
        if(selectionMode && this.cfg.highlight) {
            this.jq.off('mouseout.tree mouseover.tree', nodeContentSelector)
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
        
        this.jq.off('click.tree-content', nodeContentSelector)
                .on('click.tree-content', nodeContentSelector, null, function(e) {
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
            toggleIcon.nextAll('span.ui-treenode-icon').removeClass(iconState.collapsedIcon).addClass(iconState.expandedIcon);
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
            toggleIcon.nextAll('span.ui-treenode-icon').addClass(iconState.collapsedIcon).removeClass(iconState.expandedIcon);
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
    
    selectNode: function(node, silent) {        
        node.removeClass('ui-treenode-unselected').addClass('ui-treenode-selected').children('.ui-treenode-content').removeClass('ui-state-hover').addClass('ui-state-highlight');

        this.addToSelection(this.getRowKey(node));
        this.writeSelections();

        if(!silent)
            this.fireNodeSelectEvent(node);
    },
    
    unselectNode: function(node, silent) {
        var rowKey = this.getRowKey(node);
           
        node.removeClass('ui-treenode-selected').addClass('ui-treenode-unselected').children('.ui-treenode-content').removeClass('ui-state-highlight');

        this.removeFromSelection(rowKey);
        this.writeSelections();

        if(!silent)
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
                icon.removeClass('ui-icon-blank').addClass('ui-icon-minus');
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
            
            if(this.cfg.dynamic) {
                this.removeDescendantsFromSelection(node.data('rowkey'));
            }
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
        
    check: function(checkbox) {
        this._super(checkbox);
        checkbox.parent('.ui-treenode-content').addClass('ui-state-highlight').removeClass('ui-state-hover');
    },
            
    uncheck: function(checkbox) {
        this._super(checkbox);
        checkbox.parent('.ui-treenode-content').removeClass('ui-state-highlight');
    },
            
    drawConnectors: function() {
        this.jq.find('table.ui-treenode-connector-table').each(function() {
            var table = $(this);

            table.height(0).height(table.parent().height());
        });
    },
            
    isEmpty: function() {
        return this.jq.children('table').length === 0;
    },
    
    focusNode: function(node) {
        //focus not supported in horizontal mode
    },
    
    //@Override
    partialCheck: function(checkbox) {
        var box = checkbox.children('.ui-chkbox-box'),
        icon = box.children('.ui-chkbox-icon'),
        treeNode = checkbox.closest('.ui-treenode'),
        rowKey = this.getRowKey(treeNode);

        treeNode.find('> .ui-treenode-content').removeClass('ui-state-highlight');
        icon.removeClass('ui-icon-blank ui-icon-check').addClass('ui-icon-minus');
        treeNode.removeClass('ui-treenode-selected ui-treenode-unselected').addClass('ui-treenode-hasselected').attr('aria-checked', false).attr('aria-selected', false);
    
        this.removeFromSelection(rowKey);
     }        
    
});