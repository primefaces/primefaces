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

            if(this.isCheckboxSelection()) {
                this.preselectCheckboxPropagation();
            }
        }

        this.bindEvents();
    },
    
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
                            _self.toggleCheckbox($(this));
                        });
        }
        
        //node click
        $(document).off('click.tree', nodeContentSelector)
                        .on('click.tree', nodeContentSelector, null, function(e) {
                            _self.nodeClick(e, $(this));
                        });
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
    
    expandNode: function(node) {    
        var _self = this;

        if(this.cfg.dynamic) {

            if(this.cfg.cache && node.children('.ui-treenode-children').children().length > 0) {
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
                        node.children('.ui-treenode-children').append(content);

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
    
    toggleCheckbox: function(checkbox) {
        var _self = this,
        node = checkbox.parents('.ui-treenode:first'),
        checked = checkbox.children('.ui-chkbox-icon').hasClass('ui-icon-check');

        //propagate selection down
        node.find('.ui-chkbox-icon').each(function() {
            var icon = $(this),
            treeNode = icon.parents('li:first'),
            rowKey = _self.getRowKey(treeNode);

            if(checked) {
                icon.removeClass('ui-icon ui-icon-check');

                _self.removeFromSelection(rowKey);

                //aria
                treeNode.attr('aria-checked', false).attr('aria-selected', false);
            }
            else {
                if($.inArray(rowKey, _self.selections) == -1) {
                    icon.addClass('ui-icon ui-icon-check');

                    _self.addToSelection(rowKey);

                    //aria
                    treeNode.attr('aria-checked', true).attr('aria-selected', true);
                }
            }
        });

        //propagate selection up
        node.parents('li.ui-treenode-parent').each(function() {
            var parentNode = $(this),
            rowKey = _self.getRowKey(parentNode),
            icon = parentNode.children('.ui-treenode-content').find('.ui-chkbox-icon'),
            checkedChildren = parentNode.children('.ui-treenode-children').find('.ui-chkbox-icon.ui-icon-check'),
            allChildren = parentNode.children('.ui-treenode-children').find('.ui-chkbox-icon');

            if(checked) {
                if(checkedChildren.length > 0) {
                    icon.removeClass('ui-icon ui-icon-check').addClass('ui-icon ui-icon-minus');
                } 
                else {
                    icon.removeClass('ui-icon ui-icon-minus ui-icon-check');
                }

                _self.removeFromSelection(rowKey);

                //aria
                parentNode.attr('aria-checked', false).attr('aria-selected', false);                
            }
            else {
                if(checkedChildren.length === allChildren.length) {
                    icon.removeClass('ui-icon ui-icon-minus').addClass('ui-icon ui-icon-check');

                    _self.addToSelection(rowKey);

                    //aria
                    parentNode.attr('aria-checked', true).attr('aria-selected', true);
                } 
                else {
                    icon.removeClass('ui-icon ui-icon-check').addClass('ui-icon ui-icon-minus');

                    //aria
                    parentNode.attr('aria-checked', false).attr('aria-selected', false);
                }
            }

        });
        
        this.writeSelections();

        this.fireNodeSelectEvent(node);
    },
    
    preselectCheckboxPropagation: function() {
        this.jq.find('.ui-chkbox-icon').not('.ui-icon-check').each(function() {
            var icon = $(this),
            node = icon.parents('li:first');

            if(node.children('.ui-treenode-children').find('.ui-chkbox-icon.ui-icon-check').length > 0) {
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