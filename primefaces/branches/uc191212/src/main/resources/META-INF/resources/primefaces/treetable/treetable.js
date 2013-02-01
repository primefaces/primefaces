/**
 * PrimeFaces TreeTable Widget
 */
PrimeFaces.widget.TreeTable = PrimeFaces.widget.BaseWidget.extend({
    
    init: function(cfg) {
        this._super(cfg);
        
        this.cfg.scrollable = this.jq.hasClass('ui-treetable-scrollable');
        this.cfg.resizable = this.jq.hasClass('ui-treetable-resizable');

        this.bindToggleEvents();

        //scrolling
        if(this.cfg.scrollable) {
            this.setupScrolling();
        }

        //selection
        if(this.cfg.selectionMode) {
            this.jqSelection = $(this.jqId + '_selection');
            var selectionValue = this.jqSelection.val();

            this.selection = selectionValue === "" ? [] : selectionValue.split(',');

            this.bindSelectionEvents();
        }
    },
    
    bindToggleEvents: function() {
        var _self = this;

        //expand and collapse
        $(this.jqId + ' .ui-treetable-toggler').die('click.treetable')
            .live('click.treetable', function(e) {
                var toggler = $(this),
                node = toggler.parents('tr:first');

                if(toggler.hasClass('ui-icon-triangle-1-e'))
                    _self.expandNode(e, node);
                else {
                    _self.collapseNode(e, node);
                }
            });
    },
    
    bindSelectionEvents: function() {
        var _self = this;

        $(this.jqId + ' .ui-treetable-data tr.ui-treetable-selectable-node').die('mouseover.treetable mouseout.treetable click.treetable contextmenu.treetable')
                .live('mouseover.treetable', function(e) {
                    var element = $(this);

                    if(!element.hasClass('ui-state-highlight')) {
                        element.addClass('ui-state-hover');
                    }
                })
                .live('mouseout.treetable', function(e) {
                    var element = $(this);

                    if(!element.hasClass('ui-state-highlight')) {
                        element.removeClass('ui-state-hover');
                    }
                })
                .live('click.treetable', function(e) {
                    _self.onRowClick(e, $(this));
                    e.preventDefault();
                });
    },
    
    expandNode: function(e, node) {
        var options = {
            source: this.id,
            process: this.id,
            update: this.id
        },
        _self = this,
        nodeKey = node.attr('id').split('_node_')[1];

        options.onsuccess = function(responseXML) {
            var xmlDoc = $(responseXML.documentElement),
            updates = xmlDoc.find("update");

            for(var i=0; i < updates.length; i++) {
                var update = updates.eq(i),
                id = update.attr('id'),
                content = update.text();

                if(id == _self.id){
                    node.replaceWith(content);
                    node.find('.ui-treetable-toggler:first').addClass('ui-icon-triangle-1-s').removeClass('ui-icon-triangle-1-e');
                    node.attr('aria-expanded', true);
                }
                else {
                    PrimeFaces.ajax.AjaxUtils.updateElement.call(this, id, content);
                }
            }

            PrimeFaces.ajax.AjaxUtils.handleResponse.call(this, xmlDoc);

            return true;
        };

        options.params = [
            {name: this.id + '_expand', value: nodeKey}
        ];

        if(this.hasBehavior('expand')) {
            var expandBehavior = this.cfg.behaviors['expand'];

            expandBehavior.call(this, e, options);
        }
        else {
            PrimeFaces.ajax.AjaxRequest(options);
        }
    },
    
    collapseNode: function(e, node) {
        node.siblings('[id^="' + node.attr('id') + '_"]').remove();

        node.find('.ui-treetable-toggler:first').addClass('ui-icon-triangle-1-e').removeClass('ui-icon-triangle-1-s');

        node.attr('aria-expanded', false);

        if(this.hasBehavior('collapse')) {
            var collapseBehavior = this.cfg.behaviors['collapse'],
            nodeKey = node.attr('id').split('_node_')[1];

            var ext = {
                params : [
                    {name: this.id + '_collapse', value: nodeKey}
                ]
            };

            collapseBehavior.call(this, e, ext);
        }
    },
    
    onRowClick: function(e, node) {
    
        //Check if rowclick triggered this event not an element in row content
        if($(e.target).is('div.ui-tt-c,td')) {
            var selected = node.hasClass('ui-state-highlight');

            if(selected)
                this.unselectNode(e, node);
            else
                this.selectNode(e, node);

            PrimeFaces.clearSelection();
        }
    },
    
    selectNode: function(e, node) {
        var nodeKey = node.attr('id').split('_node_')[1],
        metaKey = (e.metaKey||e.ctrlKey);

        //unselect previous selection
        if(this.isSingleSelection() || (this.isMultipleSelection() && !metaKey)) {
            node.siblings('.ui-state-highlight').removeClass('ui-state-highlight').attr('aria-selected', false);
            this.selection = [];
        }

        //add to selection
        node.removeClass('ui-state-hover').addClass('ui-state-highlight').attr('aria-selected', true);
        this.addSelection(nodeKey);

        //save state
        this.writeSelections();

        this.fireSelectNodeEvent(e, nodeKey);
    },
    
    unselectNode: function(e, node) {
        var nodeKey = node.attr('id').split('_node_')[1],
        metaKey = metaKey = (e.metaKey||e.ctrlKey);

        if(metaKey) {
            //remove visual style
            node.removeClass('ui-state-highlight');

            //aria
            node.attr('aria-selected', false);

            //remove from selection
            this.removeSelection(nodeKey);

            //save state
            this.writeSelections();

            this.fireUnselectNodeEvent(e, nodeKey);
        }
        else if(this.isMultipleSelection()){
            this.selectNode(e, node);
        }
    },
    
    hasBehavior: function(event) {
        if(this.cfg.behaviors) {
            return this.cfg.behaviors[event] != undefined;
        }

        return false;
    },
    
    /**
     * Remove given rowIndex from selection
     */
    removeSelection: function(nodeKey) {
        this.selection = $.grep(this.selection, function(value) {
            return value != nodeKey;
        });
    },
    
    /**
     * Adds given rowIndex to selection if it doesn't exist already
     */
    addSelection: function(nodeKey) {
        if(!this.isSelected(nodeKey)) {
            this.selection.push(nodeKey);
        }
    },
    
    isSelected: function(nodeKey) {
        var selection = this.selection,
        selected = false;

        $.each(selection, function(index, value) {
            if(value === nodeKey) {
                selected = true;

                return false;       //break
            } 
            else {
                return true;        //continue
            }
        });

        return selected;
    },
    
    isSingleSelection: function() {
        return this.cfg.selectionMode == 'single';
    },
    
    isMultipleSelection: function() {
        return this.cfg.selectionMode == 'multiple';
    },
    
    /**
     * Writes selected row ids to state holder
     */
    writeSelections: function() {
        this.jqSelection.val(this.selection.join(','));
    },
    
    fireSelectNodeEvent: function(e, nodeKey) {
        if(this.hasBehavior('select')) {
            var selectBehavior = this.cfg.behaviors['select'],
            ext = {
                params: [
                    {name: this.id + '_instantSelect', value: nodeKey}
                ]
            };

            selectBehavior.call(this, e, ext);
        }
    },
    
    fireUnselectNodeEvent: function(e, nodeKey) {
        if(this.hasBehavior('unselect')) {
            var unselectBehavior = this.cfg.behaviors['unselect'],
             ext = {
                params: [
                    {name: this.id + '_instantUnselect', value: nodeKey}
                ]
            };
            
            unselectBehavior.call(this, e, ext);
        }
    },
    
    setupScrolling: function() {
        var scrollHeader = $(this.jqId + ' .ui-treetable-scrollable-header'),
        scrollBody = $(this.jqId + ' .ui-treetable-scrollable-body'),
        scrollFooter = $(this.jqId + ' .ui-treetable-scrollable-footer');

        scrollBody.scroll(function() {
            scrollHeader.scrollLeft(scrollBody.scrollLeft());
            scrollFooter.scrollLeft(scrollBody.scrollLeft());
        });
    }
});