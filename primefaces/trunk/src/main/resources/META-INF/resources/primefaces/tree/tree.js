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

            if(this.cfg.selectionMode == 'checkbox')
                this.preselectCheckboxPropagation();
        }

        this.bindEvents();
    },
    
    bindEvents: function() {
        var _self = this,
        selectionMode = this.cfg.selectionMode,
        iconSelector = this.jqId + ' .ui-tree-icon',
        nodeSelector = this.jqId  + ' .ui-tree-selectable-node';

        //expand-collapse
        $(document).off('click', iconSelector)
                    .on('click', iconSelector, null, function(e) {
                        var icon = $(this),
                        node = icon.parents('li:first');

                        if(icon.hasClass('ui-icon-triangle-1-e'))
                            _self.expandNode(node);
                        else
                            _self.collapseNode(node);
                    });

        //selection hover
        if(selectionMode) {
            if(this.cfg.highlight) {
                $(document).off('hover.tree', nodeSelector)
                            .on('hover.tree', nodeSelector, null, function() {
                                var element = $(this);

                                if(!element.hasClass('ui-state-highlight'))
                                    $(this).toggleClass('ui-state-hover');
                            });
                
            }
     
            $(document).off('click.tree contextmenu.tree', nodeSelector)
                        .on('click.tree', nodeSelector, null, function(e) {
                            _self.onNodeClick(e, $(this).parents('li:first'));
                        })
                        .on('contextmenu.tree', nodeSelector, null, function(e) {
                            _self.onNodeClick(e, $(this).parents('li:first'));
                            e.preventDefault();
                        });
        }
    },
    
    onNodeClick: function(e, node) {
        PrimeFaces.clearSelection();

        if($(e.target).is(':not(.ui-tree-icon)')) {
            if(this.cfg.onNodeClick) {
                this.cfg.onNodeClick.call(this, node);
            }

            if(this.isNodeSelected(node))
                this.unselectNode(e, node);
            else
                this.selectNode(e, node);
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
                {name: this.id + '_expandNode', value: _self.getNodeId(node)}
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
                        {name: this.id + '_expandNode', value: this.getNodeId(node)}
                    ]
                };

                expandBehavior.call(this, node, ext);
            }
        }
    },
    
    collapseNode: function(node) {
        var _self = this,
        icon = node.find('.ui-tree-icon:first'),
        lastClass = node.attr('class').split(' ').slice(-1),
        nodeIcon = icon.next(),
        iconState = this.cfg.iconStates[lastClass];

        icon.addClass('ui-icon-triangle-1-e').removeClass('ui-icon-triangle-1-s');

        if(iconState) {
            nodeIcon.removeClass(iconState.expandedIcon).addClass(iconState.collapsedIcon);
        }

        //aria
        node.children('.ui-tree-node').attr('aria-expanded', false);

        var childNodeContainer = node.children('.ui-tree-nodes');
        childNodeContainer.hide();

        if(_self.cfg.dynamic && !_self.cfg.cache) {
            childNodeContainer.empty();
        }

        _self.fireCollapseEvent(node);
    },
    
    fireCollapseEvent: function(node) {
        if(this.cfg.behaviors) {
            var collapseBehavior = this.cfg.behaviors['collapse'];
            if(collapseBehavior) {
                var ext = {
                    params: [
                        {name: this.id + '_collapseNode', value: this.getNodeId(node)}
                    ]
                };

                collapseBehavior.call(this, node, ext);
            }
        }
    },
    
    showNodeChildren: function(node) {
        //aria
        node.children('.ui-tree-node').attr('aria-expanded', true);

        var icon = node.find('.ui-tree-icon:first'),
        lastClass = node.attr('class').split(' ').slice(-1),
        nodeIcon = icon.next(),
        iconState = this.cfg.iconStates[lastClass];

        icon.addClass('ui-icon-triangle-1-s').removeClass('ui-icon-triangle-1-e');

        if(iconState) {
            nodeIcon.removeClass(iconState.collapsedIcon).addClass(iconState.expandedIcon);
        }

        node.children('.ui-tree-nodes').show();
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

            this.addToSelection(this.getNodeId(node));
        }

        this.writeSelections();

        this.fireNodeSelectEvent(node);
    },
    
    unselectNode: function(e, node) {
        var nodeId = this.getNodeId(node),
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
            this.removeFromSelection(nodeId);

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
                        {name: this.id + '_instantSelection', value: this.getNodeId(node)}
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
                        {name: this.id + '_instantUnselection', value: this.getNodeId(node)}
                    ]
                };

                unselectBehavior.call(this, node, ext);
            }
        }
    },
    
    getNodeId: function(node) {
        return node.attr('id').split('_node_')[1];
    },
    
    isNodeSelected: function(node) {
        return $.inArray(this.getNodeId(node), this.selections) != -1;
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
    
    addToSelection: function(nodeId) {
        this.selections.push(nodeId);
    },
    
    removeFromSelection: function(nodeId) {
        this.selections = $.grep(this.selections, function(r) {
            return r != nodeId;
        });
    },
    
    toggleCheckbox: function(node, check) {
        var _self = this;

        //propagate selection down
        node.find('.ui-tree-checkbox-icon').each(function() {
            var icon = $(this),
            treeNode = icon.parents('li:first'),
            nodeId = _self.getNodeId(treeNode);

            if(check) {
                if($.inArray(nodeId, _self.selections) == -1) {
                    icon.addClass('ui-icon ui-icon-check');

                    _self.addToSelection(nodeId);

                    //aria
                    treeNode.children('.ui-tree-node').attr('aria-checked', true).attr('aria-selected', true);
                }
            }
            else {
                icon.removeClass('ui-icon ui-icon-check');

                _self.removeFromSelection(nodeId);

                //aria
                treeNode.children('.ui-tree-node').attr('aria-checked', false).attr('aria-selected', false);
            }
        });

        //propagate selection up
        node.parents('li').each(function() {
            var parentNode = $(this),
            nodeId = _self.getNodeId(parentNode),
            icon = parentNode.find('.ui-tree-checkbox-icon:first'),
            checkedChildren = parentNode.children('.ui-tree-nodes').find('.ui-tree-checkbox-icon.ui-icon-check'),
            allChildren = parentNode.children('.ui-tree-nodes').find('.ui-tree-checkbox-icon');

            if(check) {
                if(checkedChildren.length == allChildren.length) {
                    icon.removeClass('ui-icon ui-icon-minus').addClass('ui-icon ui-icon-check');

                    _self.addToSelection(nodeId);

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

                _self.removeFromSelection(nodeId);

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