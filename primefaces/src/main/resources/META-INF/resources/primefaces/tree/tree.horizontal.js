/**
 * __PrimeFaces Horizontal Tree Widget__
 * 
 * Tree is used for displaying hierarchical data and creating a site navigation. This implements a horizontal tree.
 * 
 * @interface {PrimeFaces.widget.HorizontalTreeCfg} cfg The configuration for the
 * {@link  HorizontalTree| HorizontalTree widget}. You can access this configuration via
 * {@link PrimeFaces.widget.BaseWidget.cfg|BaseWidget.cfg}. Please note that this configuration is usually meant to be
 * read-only and should not be modified.
 * @extends {PrimeFaces.widget.BaseTreeCfg} cfg
 */
PrimeFaces.widget.HorizontalTree = PrimeFaces.widget.BaseTree.extend({

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    init: function(cfg) {
        this._super(cfg);

        if(PrimeFaces.env.isIE() && !this.cfg.disabled) {
            this.drawConnectors();
        }
    },

    /**
     * @override
     * @protected
     * @inheritdoc
     */
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
            this.jq.off('mouseenter.tree mouseleave.tree', nodeContentSelector)
                        .on('mouseenter.tree', nodeContentSelector, null, function() {
                            $(this).addClass('ui-state-hover');
                        })
                        .on('mouseleave.tree', nodeContentSelector, null, function() {
                            $(this).removeClass('ui-state-hover');
                        });
        }

        if(this.isCheckboxSelection()) {
            var checkboxSelector = '.ui-chkbox-box:not(.ui-state-disabled)';

            this.jq.off('mouseleave.tree-checkbox mouseenter.tree-checkbox', checkboxSelector)
                        .on('mouseleave.tree-checkbox', checkboxSelector, null, function() {
                            $(this).removeClass('ui-state-hover');
                        })
                        .on('mouseenter.tree-checkbox', checkboxSelector, null, function() {
                            $(this).addClass('ui-state-hover');
                        });
        }

        this.jq.off('click.tree-content', nodeContentSelector)
                .on('click.tree-content', nodeContentSelector, null, function(e) {
                    $this.nodeClick(e, $(this));
                });

    },

    /**
     * @override
     * @protected
     * @inheritdoc
     * @param {JQuery} node
     */
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

    /**
     * Collapses the given node, as if the user had clicked on the `-` icon of the node. The children of the node will
     * now be visible. 
     * @param {JQuery} node Node to collapse. 
     */
    collapseNode: function(node) {
        var childrenContainer = node.next(),
        toggleIcon = node.find('> .ui-treenode-content > .ui-tree-toggler'),
        nodeType = node.data('nodetype'),
        iconState = this.cfg.iconStates[nodeType];

        if(iconState) {
            toggleIcon.nextAll('span.ui-treenode-icon').removeClass(iconState.expandedIcon).addClass(iconState.collapsedIcon);
        }

        toggleIcon.removeClass('ui-icon-minus').addClass('ui-icon-plus');
        node.addClass('ui-treenode-collapsed');
        childrenContainer.hide();

        if(this.cfg.dynamic && !this.cfg.cache) {
            childrenContainer.children('.ui-treenode-children').empty();
        }

        if(!this.cfg.cache) {
            this.fireCollapseEvent(node);
        }

        if($.browser.msie) {
            this.drawConnectors();
        }
    },

    /**
     * @override
     * @protected
     * @inheritdoc
     * @param {JQuery} node
     * @return {JQuery}
     */
    getNodeChildrenContainer: function(node) {
        return node.next('.ui-treenode-children-container').children('.ui-treenode-children');
    },

    /**
     * @override
     * @inheritdoc
     * @param {JQuery} node
     * @param {boolean} [silent]
     */
    selectNode: function(node, silent) {
        node.removeClass('ui-treenode-unselected').addClass('ui-treenode-selected').children('.ui-treenode-content').addClass('ui-state-highlight');

        this.addToSelection(this.getRowKey(node));
        this.writeSelections();

        if(!silent)
            this.fireNodeSelectEvent(node);
    },

    /**
     * @override
     * @inheritdoc
     * @param {JQuery} node
     * @param {boolean} [silent]
     */
    unselectNode: function(node, silent) {
        var rowKey = this.getRowKey(node);

        node.removeClass('ui-treenode-selected').addClass('ui-treenode-unselected').children('.ui-treenode-content').removeClass('ui-state-highlight');

        this.removeFromSelection(rowKey);
        this.writeSelections();

        if(!silent)
            this.fireNodeUnselectEvent(node);
    },

    /**
     * @override
     * @inheritdoc
     */
    unselectAllNodes: function() {
        this.selections = [];
        this.jq.find('.ui-treenode-content.ui-state-highlight').each(function() {
            $(this).removeClass('ui-state-highlight').closest('.ui-treenode').attr('aria-selected', false);
        });
    },

    /**
     * @override
     * @protected
     * @inheritdoc
     */
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

    /**
     * @override
     * @protected
     * @inheritdoc
     * @param {JQuery} node
     */
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

    /**
     * @override
     * @protected
     * @inheritdoc
     * @param {JQuery} checkbox
     */
    check: function(checkbox) {
        this._super(checkbox);
        checkbox.parent('.ui-treenode-content').addClass('ui-state-highlight');
    },

    /**
     * @override
     * @protected
     * @inheritdoc
     * @param {JQuery} checkbox
     */
    uncheck: function(checkbox) {
        this._super(checkbox);
        checkbox.parent('.ui-treenode-content').removeClass('ui-state-highlight');
    },

    /**
     * Draws the lines connection the tree nodes.
     * @private
     */
    drawConnectors: function() {
        this.jq.find('table.ui-treenode-connector-table').each(function() {
            var table = $(this),
            row = table.closest('tr');

            table.height(0).height(row.height());
        });
    },

    /**
     * @override
     * @inheritdoc
     * @return {boolean}
     */
    isEmpty: function() {
        return this.jq.children('table').length === 0;
    },

    /**
     * This implementation does nothing, focus is not supported in horizontal mode.
     * @override
     * @inheritdoc
     * @protected
     * @param {JQuery} node
     */
    focusNode: function(node) {
        //focus not supported in horizontal mode
    },

    /**
     * @override
     * @protected
     * @inheritdoc
     * @param {JQuery} checkbox
     */
    partialCheck: function(checkbox) {
        var box = checkbox.children('.ui-chkbox-box'),
        icon = box.children('.ui-chkbox-icon'),
        treeNode = checkbox.closest('.ui-treenode'),
        rowKey = this.getRowKey(treeNode);

        box.removeClass('ui-state-active');
        treeNode.find('> .ui-treenode-content').removeClass('ui-state-highlight');
        icon.removeClass('ui-icon-blank ui-icon-check').addClass('ui-icon-minus');
        treeNode.removeClass('ui-treenode-selected ui-treenode-unselected').addClass('ui-treenode-hasselected').attr('aria-checked', false).attr('aria-selected', false);

        this.removeFromSelection(rowKey);
     }

});