/**
 * PrimeFaces Tree Widget
 */
PrimeFaces.widget.Tree = PrimeFaces.widget.BaseWidget.extend({
    
    init: function(cfg) {
        this._super(cfg);
        
        this.cfg.formId = this.jq.parents('form:first').attr('id');

        if(this.cfg.selectionMode) {
            this.selectionHolder = $(this.jqId + '_selection');
            var selectionsValue = this.selectionHolder.val();
            this.selections = selectionsValue === '' ? [] : selectionsValue.split(',');

            if(this.cfg.selectionMode == 'checkbox') {
                this.preselectCheckboxPropagation();
            }
        }

        this.bindEvents();
    },
    
    bindEvents: function() {
        var _self = this,
        selectionMode = this.cfg.selectionMode,
        togglerSelector = this.jqId + ' .ui-tree-toggler',
        nodeSelector = this.jqId  + ' .ui-tree-node-content';

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
        if(selectionMode && this.cfg.highlight) {
            $(document).off('hover.tree', nodeSelector)
                        .on('hover.tree', nodeSelector, null, function() {
                            var element = $(this);

                            if(!element.hasClass('ui-state-highlight') && element.hasClass('ui-tree-selectable-node')) {
                                $(this).toggleClass('ui-state-hover');
                            }
                        });
                
        }
        
        //node click
        $(document).off('click.tree', nodeSelector)
                        .on('click.tree', nodeSelector, null, function(e) {
                            _self.nodeClick(e, $(this));
                        });
    },
    
    nodeClick: function(e, nodeContent) {
        PrimeFaces.clearSelection();
        
        var node = nodeContent.parents('li:first');

        if($(e.target).is(':not(.ui-tree-icon)')) {
            if(this.cfg.onNodeClick) {
                this.cfg.onNodeClick.call(this, node);
            }
            
            if(nodeContent.hasClass('ui-tree-selectable-node') && this.cfg.selectionMode) {
                if(this.isNodeSelected(node))
                    this.unselectNode(e, node);
                else
                    this.selectNode(e, node);
            };
        }
    },
    
    expandNode: function(node) {    
        var _self = this;

        if(this.cfg.dynamic) {

            if(this.cfg.cache && node.children('.ui-tree-nodes').children().length > 0) {
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

                    if(id == _self.id){
                        node.children('.ui-tree-nodes').append(content);

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
            //expand dom
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
    
    collapseNode: function(node) {
        var _self = this;
        
        //aria
        node.attr('aria-expanded', true);
        
        var toggleIcon = node.find('> .ui-treenode-content > .ui-tree-toggler'),
        nodeType = node.attr('class').split(' ').slice(-1),
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
    
    showNodeChildren: function(node) {
        //aria
        node.attr('aria-expanded', true);
        
        var toggleIcon = node.find('> .ui-treenode-content > .ui-tree-toggler'),
        nodeType = node.attr('class').split(' ').slice(-1),
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
    
    selectNode: function(e, node) {
        var metaKey = (e.metaKey||e.ctrlKey);

        if(this.isCheckboxSelection()) {
            this.toggleCheckbox(node, true);
        }
        else {
            if(this.isSingleSelection() || (this.isMultipleSelection() && !metaKey)) {
                //clean all selections
                this.selections = [];
                this.jq.find('.ui-tree-node-content.ui-state-highlight').each(function() {
                    $(this).removeClass('ui-state-highlight').parent().attr('aria-selected', false);
                });
            }

            //select node
            node.children('.ui-tree-node').attr('aria-selected', true);
            node.find('.ui-tree-node-content:first').removeClass('ui-state-hover').addClass('ui-state-highlight');

            this.addToSelection(this.getRowKey(node));
        }

        this.writeSelections();

        this.fireNodeSelectEvent(node);
    },
    
    unselectNode: function(e, node) {
        var rowKey = this.getRowKey(node),
        metaKey = (e.metaKey||e.ctrlKey);

        //select node
        if(this.isCheckboxSelection()) {
            this.toggleCheckbox(node, false);
            this.writeSelections();
            this.fireNodeUnselectEvent(node);
        }
        else if(metaKey) {
            //remove visual style    
            node.find('.ui-tree-node-content:first').removeClass('ui-state-highlight');

            //aria
            node.children('.ui-tree-node').attr('aria-selected', false);

            //remove from selection
            this.removeFromSelection(rowKey);

            this.writeSelections();

            this.fireNodeUnselectEvent(node);
        } 
        else if(this.isMultipleSelection()){
            this.selectNode(e, node);
        }
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
    
    toggleCheckbox: function(node, check) {
        var _self = this;

        //propagate selection down
        node.find('.ui-tree-checkbox-icon').each(function() {
            var icon = $(this),
            treeNode = icon.parents('li:first'),
            rowKey = _self.getRowKey(treeNode);

            if(check) {
                if($.inArray(rowKey, _self.selections) == -1) {
                    icon.addClass('ui-icon ui-icon-check');

                    _self.addToSelection(rowKey);

                    //aria
                    treeNode.children('.ui-tree-node').attr('aria-checked', true).attr('aria-selected', true);
                }
            }
            else {
                icon.removeClass('ui-icon ui-icon-check');

                _self.removeFromSelection(rowKey);

                //aria
                treeNode.children('.ui-tree-node').attr('aria-checked', false).attr('aria-selected', false);
            }
        });

        //propagate selection up
        node.parents('li.ui-tree-parent').each(function() {
            var parentNode = $(this),
            rowKey = _self.getRowKey(parentNode),
            icon = parentNode.children('.ui-tree-node').find('.ui-tree-checkbox-icon'),
            checkedChildren = parentNode.children('.ui-tree-nodes').find('.ui-tree-checkbox-icon.ui-icon-check'),
            allChildren = parentNode.children('.ui-tree-nodes').find('.ui-tree-checkbox-icon');

            if(check) {
                if(checkedChildren.length == allChildren.length) {
                    icon.removeClass('ui-icon ui-icon-minus').addClass('ui-icon ui-icon-check');

                    _self.addToSelection(rowKey);

                    //aria
                    parentNode.children('.ui-tree-node').attr('aria-checked', true).attr('aria-selected', true);
                } 
                else {
                    icon.removeClass('ui-icon ui-icon-check').addClass('ui-icon ui-icon-minus');

                    //aria
                    parentNode.children('.ui-tree-node').attr('aria-checked', false).attr('aria-selected', false);
                }
            }
            else {
                if(checkedChildren.length > 0) {
                    icon.removeClass('ui-icon ui-icon-check').addClass('ui-icon ui-icon-minus');

                } else {
                    icon.removeClass('ui-icon ui-icon-minus ui-icon-check');
                }

                _self.removeFromSelection(rowKey);

                //aria
                parentNode.children('.ui-tree-node').attr('aria-checked', false).attr('aria-selected', false);
            }

        });
    },
    
    preselectCheckboxPropagation: function() {
        this.jq.find('.ui-tree-checkbox-icon').not('.ui-icon-check').each(function() {
            var icon = $(this),
            node = icon.parents('li:first');

            if(node.children('.ui-tree-nodes').find('.ui-tree-checkbox-icon.ui-icon-check').length > 0) {
                icon.addClass('ui-icon ui-icon-minus');
            }
        });
    },
    
    hasBehavior: function(event) {
        if(this.cfg.behaviors) {
            return this.cfg.behaviors[event] != undefined;
        }

        return false;
    }
    
});