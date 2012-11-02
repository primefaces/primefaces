/**
 * PrimeFaces Tree Widget
 */
PrimeFaces.widget.BaseTree = PrimeFaces.widget.BaseWidget.extend({
    
    init: function(cfg) {
        this._super(cfg);
        
        this.cfg.formId = this.jq.parents('form:first').attr('id');

        this.initSelection();

        this.bindEvents();
    },
    
    initSelection: function() {
        if(this.cfg.selectionMode) {
            this.selectionHolder = $(this.jqId + '_selection');
            var selectionsValue = this.selectionHolder.val();
            this.selections = selectionsValue === '' ? [] : selectionsValue.split(',');

            if(this.isCheckboxSelection()) {
                this.preselectCheckbox();
            }
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
                    content = update.text();

                    if(id == _self.id) {
                        _self.getNodeChildrenContainer(node).append(content);

                        _self.showNodeChildren(node);
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
        this.selections.push(rowKey);
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
    
        
    nodeClick: function(e, nodeContent) {
        PrimeFaces.clearSelection();
        
        if($(e.target).is(':not(.ui-tree-toggler)')) {
            var node = nodeContent.parent(),
            metaKey = (e.metaKey||e.ctrlKey);
                    
            if(this.cfg.onNodeClick) {
                this.cfg.onNodeClick.call(this, node);
            }
            
            if(nodeContent.hasClass('ui-tree-selectable') && this.cfg.selectionMode && !this.isCheckboxSelection()) {
                if(this.isNodeSelected(node) && metaKey) {
                    this.unselectNode(node);
                }
                else {
                    this.selectNode(node, metaKey);
                }
            };
        }
    },
    
    selectNode: function(node, metaKey) {        
        throw "Unsupported Operation";
    },
    
    unselectNode: function(node) {        
        throw "Unsupported Operation";
    },
    
    preselectCheckbox: function() {
        throw "Unsupported Operation";
    },
    
        toggleCheckbox: function(checkbox, checked) {
        if(checked) {
            this.uncheck(checkbox);
        }
        else {
            this.check(checkbox);
        }
    },
    
    partialCheck: function(checkbox) {
        var icon = checkbox.find('> .ui-chkbox-box > .ui-chkbox-icon'),
        treeNode = checkbox.parents('.ui-treenode:first'),
        rowKey = this.getRowKey(treeNode);
        
        this.removeFromSelection(rowKey);
        
        icon.removeClass('ui-icon ui-icon-check').addClass('ui-icon ui-icon-minus');
        treeNode.removeClass('ui-treenode-selected ui-treenode-unselected').addClass('ui-treenode-hasselected').attr('aria-checked', false).attr('aria-selected', false);
    },
        
    check: function(checkbox) {
        var icon = checkbox.find('> .ui-chkbox-box > .ui-chkbox-icon'),
        treeNode = checkbox.parents('.ui-treenode:first'),
        rowKey = this.getRowKey(treeNode);
        
        icon.removeClass('ui-icon ui-icon-minus').addClass('ui-icon ui-icon-check');
        this.addToSelection(rowKey);
        treeNode.removeClass('ui-treenode-hasselected ui-treenode-unselected').addClass('ui-treenode-selected').attr('aria-checked', true).attr('aria-selected', true);
    },
    
    uncheck: function(checkbox) {
        var icon = checkbox.find('> .ui-chkbox-box > .ui-chkbox-icon'),
        treeNode = checkbox.parents('.ui-treenode:first'),
        rowKey = this.getRowKey(treeNode);
        
        icon.removeClass('ui-icon ui-icon-minus ui-icon-check');
        this.removeFromSelection(rowKey);
        treeNode.removeClass('ui-treenode-hasselected ui-treenode-selected').addClass('ui-treenode-unselected').attr('aria-checked', false).attr('aria-selected', false);
    }
    
});

/**
 * PrimeFaces Vertical Tree Widget
 */
PrimeFaces.widget.VerticalTree = PrimeFaces.widget.BaseTree.extend({
        
    bindEvents: function() {
        var _self = this,
        selectionMode = this.cfg.selectionMode,
        togglerSelector = this.jqId + ' .ui-tree-toggler',
        nodeLabelSelector = this.jqId  + ' .ui-treenode-label',
        nodeContentSelector = this.jqId + ' .ui-treenode-content';

        //expand-collapse
        $(document).off('click', togglerSelector)
                    .on('click', togglerSelector, null, function(e) {
                        var toggleIcon = $(this),
                        node = toggleIcon.parents('li:first');

                        if(toggleIcon.hasClass('ui-icon-triangle-1-e'))
                            _self.expandNode(node);
                        else
                            _self.collapseNode(node);
                    });

        //selection hover
        if(selectionMode && !this.isCheckboxSelection() && this.cfg.highlight) {
            $(document).off('mouseout.tree mouseover.tree', nodeLabelSelector)
                        .on('mouseout.tree', nodeLabelSelector, null, function() {
                            var element = $(this);

                            if(element.hasClass('ui-state-hover')) {
                                element.removeClass('ui-state-hover');
                            }
                        })
                        .on('mouseover.tree', nodeLabelSelector, null, function() {
                            var element = $(this);

                            if(!element.hasClass('ui-state-highlight') && element.parent().hasClass('ui-tree-selectable')) {
                                $(this).addClass('ui-state-hover');
                            }
                        });
        }
        
        //checkboxes
        if(this.isCheckboxSelection()) {
            var checkboxSelector = this.jqId + ' .ui-chkbox-box';
            
            $(document).off('mouseout.tree-checkbox mouseover.tree-checkbox click.tree-checkbox', checkboxSelector)
                        .on('mouseout.tree-checkbox', checkboxSelector, null, function() {
                            $(this).removeClass('ui-state-hover');
                        })
                        .on('mouseover.tree-checkbox', checkboxSelector, null, function() {
                            $(this).addClass('ui-state-hover');
                        })
                        .on('click.tree-checkbox', checkboxSelector, null, function() {
                            _self.clickCheckbox($(this).parent());
                        });
        }
        
        //node click
        $(document).off('click.tree', nodeContentSelector)
                        .on('click.tree', nodeContentSelector, null, function(e) {
                            _self.nodeClick(e, $(this));
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
        
        toggleIcon.addClass('ui-icon-triangle-1-e').removeClass('ui-icon-triangle-1-s');
        
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

        toggleIcon.addClass('ui-icon-triangle-1-s').removeClass('ui-icon-triangle-1-e');

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
    
    selectNode: function(node, metaKey) {        
        if(this.isSingleSelection() || (this.isMultipleSelection() && !metaKey)) {
            this.selections = [];
            this.jq.find('.ui-treenode-label.ui-state-highlight').each(function() {
                $(this).removeClass('ui-state-highlight').parents('.ui-treenode:first').attr('aria-selected', false);
            });
        }

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

    clickCheckbox: function(checkbox) {
        var _self = this,
        node = checkbox.parents('.ui-treenode:first'),
        checked = checkbox.find('> .ui-chkbox-box > .ui-chkbox-icon').hasClass('ui-icon-check');

        this.toggleCheckbox(checkbox, checked);

        if(this.cfg.propagateDown) {
            node.children('.ui-treenode-children').find('.ui-chkbox').each(function() {
                _self.toggleCheckbox($(this), checked);
            });
        }

        if(this.cfg.propagateUp) {
            node.parents('li.ui-treenode-parent').each(function() {
                var parentNode = $(this),
                parentsCheckbox = parentNode.find('> .ui-treenode-content > .ui-chkbox'),
                children = parentNode.find('> .ui-treenode-children > .ui-treenode');
                
                if(checked) {
                    if(children.filter('.ui-treenode-unselected').length === children.length)
                        _self.uncheck(parentsCheckbox);
                    else
                        _self.partialCheck(parentsCheckbox);
                }
                else {
                    if(children.filter('.ui-treenode-selected').length === children.length)
                        _self.check(parentsCheckbox);
                    else
                        _self.partialCheck(parentsCheckbox);
                }
            });
        }
        
        this.writeSelections();

        if(checked) {
            this.fireNodeUnselectEvent(node);
        }
        else {
            this.fireNodeSelectEvent(node);
        }
    },
          
    preselectCheckbox: function() {
        this.jq.find('.ui-chkbox-icon').not('.ui-icon-check').each(function() {
            var icon = $(this),
            node = icon.parents('li:first');

            if(node.children('.ui-treenode-children').find('.ui-chkbox-icon.ui-icon-check').length > 0) {
                icon.addClass('ui-icon ui-icon-minus');
            }
        });
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
        var _self = this,
        selectionMode = this.cfg.selectionMode,
        togglerSelector = this.jqId + ' .ui-tree-toggler',
        nodeContentSelector = this.jqId + ' .ui-treenode-content';

        //toggle
        $(document).off('click.tree', togglerSelector)
                    .on('click.tree', togglerSelector, null, function() {
                        var icon = $(this),
                        node = icon.parents('td.ui-treenode:first');
                        
                        if(node.hasClass('ui-treenode-collapsed')) {
                            _self.expandNode(node);
                        }
                        else {
                            _self.collapseNode(node);
                        }
                    });
                    
        //selection hover
        if(selectionMode && !this.isCheckboxSelection() && this.cfg.highlight) {
            $(document).off('mouseout.tree mouseover.tree', nodeContentSelector)
                        .on('mouseout.tree', nodeContentSelector, null, function() {
                            $(this).removeClass('ui-state-hover');
                        })
                        .on('mouseover.tree', nodeContentSelector, null, function() {
                            var element = $(this);

                            if(!element.hasClass('ui-state-highlight') && element.hasClass('ui-tree-selectable')) {
                                $(this).addClass('ui-state-hover');
                            }
                        });
        }
        
        //checkboxes
        if(this.isCheckboxSelection()) {
            var checkboxSelector = this.jqId + ' .ui-chkbox-box';
            
            $(document).off('mouseout.tree-checkbox mouseover.tree-checkbox click.tree-checkbox', checkboxSelector)
                        .on('mouseout.tree-checkbox', checkboxSelector, null, function() {
                            $(this).removeClass('ui-state-hover');
                        })
                        .on('mouseover.tree-checkbox', checkboxSelector, null, function() {
                            $(this).addClass('ui-state-hover');
                        })
                        .on('click.tree-checkbox', checkboxSelector, null, function() {
                            _self.clickCheckbox($(this).parent());
                        });
        }
        
        //node click
        $(document).off('click.tree', nodeContentSelector)
                        .on('click.tree', nodeContentSelector, null, function(e) {
                            _self.nodeClick(e, $(this));
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
    
    selectNode: function(node, metaKey) {        
        if(this.isSingleSelection() || (this.isMultipleSelection() && !metaKey)) {
            this.selections = [];
            this.jq.find('.ui-treenode-content.ui-state-highlight').removeClass('ui-state-highlight');
        }

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
    
    preselectCheckbox: function() {
        var _self = this;
        
        this.jq.find('.ui-chkbox-icon').not('.ui-icon-check').each(function() {
            var icon = $(this),
            node = icon.parents('.ui-treenode:first'),
            childrenContainer = _self.getNodeChildrenContainer(node);

            if(childrenContainer.find('.ui-chkbox-icon.ui-icon-check').length > 0) {
                icon.addClass('ui-icon ui-icon-minus');
            }
        });
    },
    
    clickCheckbox: function(checkbox) {
        var _self = this,
        node = checkbox.parents('.ui-treenode:first'),
        checked = checkbox.find('> .ui-chkbox-box > .ui-chkbox-icon').hasClass('ui-icon-check');

        this.toggleCheckbox(checkbox, checked);

        if(this.cfg.propagateDown) {
            node.next('.ui-treenode-children-container').find('.ui-chkbox').each(function() {
                _self.toggleCheckbox($(this), checked);
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
                        _self.uncheck(parentsCheckbox);
                    else
                        _self.partialCheck(parentsCheckbox);
                }
                else {
                    if(children.filter('.ui-treenode-selected').length === children.length)
                        _self.check(parentsCheckbox);
                    else
                        _self.partialCheck(parentsCheckbox);
                }
            });
        }
        
        this.writeSelections();

        if(checked) {
            this.fireNodeUnselectEvent(node);
        }
        else {
            this.fireNodeSelectEvent(node);
        }
    },
            
    drawConnectors: function() {
        this.jq.find('table.ui-treenode-connector-table').each(function() {
            var table = $(this);

            table.height(0).height(table.parent().height());
        });
    }
    
});