import { BaseTree, type BaseTreeCfg } from "./tree.base.js";

/**
 * The configuration for the {@link VerticalTree} widget.
 */
export interface VerticalTreeCfg extends BaseTreeCfg {
    /** Whether drag & drop operations of this tree table are controlled. */
    controlled: boolean;
    /** Whether to `move` or `copy` the node on drop. */
    dropMode: PrimeFacesTypes.widget.VerticalTree.DropMode;
    /** Defines parent-child restrictions when a node is dropped. */
    dropRestrict: PrimeFacesTypes.widget.VerticalTree.DropRestrictMode;
    /** `true` if text direction is right-to-left, or `false` otherwise. */
    rtl: boolean;
    /** Client side event to invoke filtering. Default is keyup. */
    filterEvent: string;
    /** Delay to wait in milliseconds before sending each filter query. Default is 300. */
    filterDelay: number;
}

/**
 * __PrimeFaces Vertical Tree Widget__
 *
 * Tree is used for displaying hierarchical data and creating a site navigation. This implements a vertical tree.
 */
export class VerticalTree<Cfg extends VerticalTreeCfg = VerticalTreeCfg> extends BaseTree<Cfg> {
    /** The DOM element for the tree container. */
    container: JQuery = $();
    /** For drag&drop with `multipleDrag`, the row keys of the dragged nodes. */
    draggedSourceKeys?: string[] | null;
    /** List of parameter describing the drag & drop operations. */
    droppedNodeParams: PrimeFacesTypes.widget.VerticalTree.DroppedNodeParams[] = [];
    /** The DOM element for the filter input field that lets the user search the tree. */
    filterInput: JQuery = $();
    /** A list of row keys for rows that are not valid drag sources. */
    invalidSourceKeys?: string[] | null;
    /** The set-interval time ID of the timer for scrolling. */
    scrollInterval?: number;
    /** Form element that holds the current scroll state. */
    scrollStateHolder: JQuery = $();
    /** For drag&drop, whether the shift is pressed. */
    shiftKey?: boolean;

    override init(cfg: PrimeFacesTypes.widget.PartialWidgetCfg<Cfg>): void {
        super.init(cfg);

        this.container = this.jq.children('.p-tree-container');
        this.cfg.rtl = this.jq.hasClass('p-tree-rtl');
        this.scrollStateHolder = $(this.jqId + '_scrollState');

        if (!this.cfg.disabled) {
            if (this.cfg.draggable) {
                this.initDraggable();
            }

            if (this.cfg.droppable) {
                this.initDroppable();
            }
        }

        this.restoreScrollState();
    }

    override bindEvents(): void {
        const togglerSelector = '.p-tree-toggler',
            nodeContentSelector = '.p-treenode-content';

        this.jq.off('click.tree-toggle', togglerSelector)
            .on('click.tree-toggle', togglerSelector, null, (e) => {
                const toggleIcon = $(e.currentTarget),
                    node = toggleIcon.closest('li');

                if (node.attr('aria-expanded') !== 'true')
                    this.expandNode(node);
                else
                    this.collapseNode(node);
            });

        if (this.cfg.highlight && this.cfg.selectionMode) {
            this.jq.off('mouseenter.tree mouseleave.tree', nodeContentSelector)
                .on('mouseleave.tree', nodeContentSelector, null, (e) => {
                    $(e.currentTarget).removeClass('p-state-hover');
                })
                .on('mouseenter.tree', nodeContentSelector, null, (e) => {
                    $(e.currentTarget).addClass('p-state-hover');
                });
        }

        if (this.isCheckboxSelection()) {
            const checkboxSelector = '.p-chkbox-box:not(.p-state-disabled)';

            this.jq.off('mouseleave.tree-checkbox mouseenter.tree-checkbox', checkboxSelector)
                .on('mouseleave.tree-checkbox', checkboxSelector, null, (e) => {
                    $(e.currentTarget).removeClass('p-state-hover');
                })
                .on('mouseenter.tree-checkbox', checkboxSelector, null, (e) => {
                    $(e.currentTarget).addClass('p-state-hover');
                });
        }

        this.jq.off('click.tree-content', nodeContentSelector)
            .on('click.tree-content', nodeContentSelector, null, (e) => {
                this.nodeClick(e, $(e.currentTarget));
            });

        if (this.cfg.filter) {
            this.cfg.filterDelay = this.cfg.filterDelay || 300;
            this.cfg.filterEvent = this.cfg.filterEvent || 'keyup';
            const isEnterKeyFilter = this.cfg.filterEvent === 'enter';
            if (isEnterKeyFilter) {
                this.cfg.filterEvent = 'keydown';
            }
            const filterEventName = this.cfg.filterEvent + '.tree-filter';

            this.filterInput = this.jq.find('.p-tree-filter');
            PrimeFaces.skinInput(this.filterInput);

            this.filterInput.on(filterEventName, (e) => {
                if (PrimeFaces.utils.ignoreFilterKey(e) || PrimeFaces.utils.blockEnterKey(e) !== isEnterKeyFilter) {
                    return;
                }

                if (isEnterKeyFilter) {
                    this.filter();
                } else {
                    PrimeFaces.debounce(() => this.filter(), this.cfg.filterDelay);
                }
            });
        }

        this.jq.on('scroll.tree', () => {
            this.saveScrollState();
        });

        this.bindKeyEvents();
    }

    /**
     * Sets up all event listeners for keyboard interactions.
     */
    bindKeyEvents(): void {
        let pressTab = false;

        this.jq.on('mousedown.tree', (e) => {
            if ($(e.target).is(':not(:input:enabled):not(.p-treenode-label)')) {
                e.preventDefault();
            }
        });
        this.jq.children('.p-tree-container').on('focus.tree', () => {
            if (!this.focusedNode && !pressTab) {
                this.focusNode(this.getFirstNode());
            }
        });

        this.jq.off('keydown.tree blur.tree', '.p-treenode-content').on('keydown.tree', '.p-treenode-content', null, (e) => {
            if (!this.focusedNode) {
                return;
            }

            let searchRowkey = "";

            switch (e.code) {
                case 'ArrowLeft': {
                    const rowkey = this.focusedNode.data('rowkey').toString(),
                        keyLength = rowkey.length;

                    if (this.isExpanded(this.focusedNode)) {
                        this.collapseNode(this.focusedNode);
                    }
                    else {
                        let nodeToFocus: JQuery | null = null;
                        for (let i = 1; i < parseInt(String(keyLength / 2)) + 1; i++) {
                            searchRowkey = rowkey.substring(0, keyLength - 2 * i);
                            nodeToFocus = this.container.find("li:visible[data-rowkey = '" + searchRowkey + "']");
                            if (nodeToFocus.length) {
                                this.focusNode(nodeToFocus);
                                break;
                            }
                        }
                    }

                    e.preventDefault();
                    break;
                }

                case 'ArrowRight': {
                    if (!this.focusedNode.hasClass('p-treenode-leaf')) {
                        const rowkey = this.focusedNode.data('rowkey').toString();

                        if (!this.isExpanded(this.focusedNode)) {
                            this.expandNode(this.focusedNode);
                        }

                        if (!this.isExpanded(this.focusedNode) && !this.cfg.dynamic) {
                            searchRowkey = rowkey + '_0';
                            const nodeToFocus = this.container.find("li:visible[data-rowkey = '" + searchRowkey + "']");

                            if (nodeToFocus.length) {
                                this.focusNode(nodeToFocus);
                            }
                        }
                    }

                    e.preventDefault();
                    break;
                }

                case 'ArrowUp': {
                    let nodeToFocus: JQuery | null = null;
                    const prevNode = this.previousNode(this.focusedNode);

                    if (prevNode.length) {
                        nodeToFocus = prevNode.find('li.p-treenode:visible:not(.p-tree-droppoint)').last();
                        if (!nodeToFocus.length) {
                            nodeToFocus = prevNode;
                        }
                    }
                    else {
                        nodeToFocus = this.focusedNode.closest('ul').parent('li');
                    }

                    if (nodeToFocus.length) {
                        this.focusNode(nodeToFocus);
                    }

                    e.preventDefault();
                    break;
                }

                case 'ArrowDown': {
                    let nodeToFocus: JQuery | null = null;
                    const firstVisibleChildNode = this.focusedNode.find("> ul > li:visible:not(.p-tree-droppoint)").first();

                    if (firstVisibleChildNode.length) {
                        nodeToFocus = firstVisibleChildNode;
                    }
                    else if (this.nextNode(this.focusedNode).length) {
                        nodeToFocus = this.nextNode(this.focusedNode);
                    }
                    else {
                        const rowkey = this.focusedNode.data('rowkey').toString();

                        if (rowkey.length !== 1) {
                            nodeToFocus = this.searchDown(this.focusedNode);
                        }
                    }

                    if (nodeToFocus && nodeToFocus.length) {
                        this.focusNode(nodeToFocus);
                    }

                    e.preventDefault();
                    break;
                }

                case 'Enter':
                case 'NumpadEnter':
                case 'Space': {
                    if (this.cfg.selectionMode) {
                        const selectable = this.focusedNode.children('.p-treenode-content').hasClass('p-tree-selectable');

                        if (this.cfg.onNodeClick) {
                            const retVal = this.cfg.onNodeClick.call(this, this.focusedNode, e);
                            if (retVal === false) {
                                return;
                            }
                        }

                        if (selectable) {
                            const selected = this.isNodeSelected(this.focusedNode);

                            if (this.isCheckboxSelection()) {
                                this.toggleCheckboxNode(this.focusedNode);
                            }
                            else {
                                if (selected) {
                                    this.unselectNode(this.focusedNode);
                                }
                                else {
                                    if (this.isSingleSelection()) {
                                        this.unselectAllNodes();
                                    }

                                    this.selectNode(this.focusedNode);
                                    this.cursorNode = this.focusedNode;
                                }
                            }
                        }
                    }

                    e.preventDefault();
                    break;
                }

                case 'Tab': {
                    pressTab = true;
                    this.container.trigger('focus');
                    PrimeFaces.queueTask(() => {
                        pressTab = false;
                    });

                    break;
                }
            }
        })
        .on('blur.tree', '.p-treenode-content', null, () => {
            if (this.focusedNode) {
                this.getNodeContent(this.focusedNode).removeClass('p-treenode-outline');
                this.focusedNode = null;
            }
        });

        /* For copy/paste operation on drag and drop */
        const namespace = '.tree' + this.id;
        $(document.body).off(namespace).on('keydown' + namespace, (e) => {
            this.shiftKey = e.shiftKey;
        }).on('keyup' + namespace, () => {
            this.shiftKey = false;
        });
        this.addDestroyListener(() => {
            $(document.body).off(namespace);
        });
    }

    /**
     * Returns the previous node, skipping droppoints (if present), starting at the given node.
     * @param node Node where to start the search.
     * @return The previous node.
     */
    previousNode(node: JQuery): JQuery {
        let prevNode = node.prev();
        if (prevNode.length && (prevNode.hasClass("p-tree-droppoint") || prevNode.hasClass("p-treenode-hidden"))) {
            prevNode = prevNode.prev();
        }
        return prevNode;
    }

    /**
     * Returns the next node, skipping droppoints (if present), starting at the given node.
     * @param node Node where to start the search.
     * @return The next node.
     */
    nextNode(node: JQuery): JQuery {
        let nextNode = node.next();
        if (nextNode.length && (nextNode.hasClass("p-tree-droppoint") || nextNode.hasClass("p-treenode-hidden"))) {
            nextNode = nextNode.next();
        }
        return nextNode;
    }

    /**
     * Searches for a node to focus, starting at the given node.
     * @param node Node where to start the search.
     * @return A node to focus.
     */
    searchDown(node: JQuery): JQuery | null {
        const nextOfParent = this.nextNode(node.closest('ul').parent('li'));
        let nodeToFocus: JQuery | null = null;

        if (nextOfParent.length) {
            nodeToFocus = nextOfParent;
        }
        else if (node.hasClass('p-treenode-leaf') && node.closest('ul').parent('li').length == 0) {
            nodeToFocus = node;
        }
        else {
            const rowkey = node.data('rowkey').toString();

            if (rowkey.length !== 1) {
                nodeToFocus = this.searchDown(node.closest('ul').parent('li'));
            }
        }

        return nodeToFocus;
    }

    /**
     * Collapses the given node, as if the user had clicked on the `-` icon of the node. The children of the node will
     * now be visible.
     * @param node Node to collapse.
     */
    collapseNode(node: JQuery): void {
        const nodeContent = node.find('> .p-treenode-content'),
            toggleIcon = nodeContent.find('> .p-tree-toggler'),
            nodeType = node.data('nodetype'),
            nodeIcon = toggleIcon.nextAll('span.p-treenode-icon'),
            iconState = this.cfg.iconStates![nodeType],
            childrenContainer = node.children('.p-treenode-children');

        //aria
        node.attr('aria-expanded', 'false');

        if (iconState) {
            nodeIcon.removeClass(iconState.expandedIcon).addClass(iconState.collapsedIcon);
        }

        if (this.cfg.animate) {
            childrenContainer.slideUp('fast', () => {
                this.postCollapse(node, childrenContainer);
            });
        }
        else {
            childrenContainer.hide();
            this.postCollapse(node, childrenContainer);
        }
    }

    /**
     * Callback that is invoked after a node was collapsed.
     * @param node The node that was collapsed.
     * @param childrenContainer The container element with the children of the collapsed node.
     */
    postCollapse(node: JQuery, childrenContainer: JQuery): void {
        if (this.cfg.dynamic && !this.cfg.cache) {
            childrenContainer.empty();
        }

        if (!this.cfg.cache) {
            this.fireCollapseEvent(node);
        }
    }

    override getNodeChildrenContainer(node: JQuery): JQuery {
        return node.children('.p-treenode-children');
    }

    override showNodeChildren(node: JQuery): void {
        const nodeContent = node.find('> .p-treenode-content'),
            toggleIcon = nodeContent.find('> .p-tree-toggler'),
            nodeType = node.data('nodetype'),
            nodeIcon = toggleIcon.nextAll('span.p-treenode-icon'),
            iconState = this.cfg.iconStates![nodeType];

        //aria
        node.attr('aria-expanded', 'true');

        if (iconState) {
            nodeIcon.removeClass(iconState.collapsedIcon).addClass(iconState.expandedIcon);
        }

        if (this.cfg.animate) {
            node.children('.p-treenode-children').slideDown('fast');
        }
        else {
            node.children('.p-treenode-children').show();
        }
    }

    override unselectAllNodes(): void {
        this.selections = [];
        this.jq.find('.p-treenode-content.p-state-highlight').each((_i, el) => {
            $(el).removeClass('p-state-highlight').closest('.p-treenode').attr('aria-selected', 'false').removeClass('p-treenode-selected').addClass('p-treenode-unselected');
        });
    }

    override selectNode(node: JQuery, silent?: boolean): void {
        node.attr('aria-selected', 'true').removeClass('p-treenode-unselected').addClass('p-treenode-selected')
            .find('> .p-treenode-content').addClass('p-state-highlight');

        this.addToSelection(this.getRowKey(node));
        this.writeSelections();

        if (!silent)
            this.fireNodeSelectEvent(node);
    }

    override unselectNode(node: JQuery, silent?: boolean): void {
        const rowKey = this.getRowKey(node);

        node.attr('aria-selected', 'false').removeClass('p-treenode-selected').addClass('p-treenode-unselected')
            .find('> .p-treenode-content').removeClass('p-state-highlight');

        this.removeFromSelection(rowKey);
        this.writeSelections();

        if (!silent)
            this.fireNodeUnselectEvent(node);
    }

    override toggleCheckboxNode(node: JQuery): void {
        const checkbox = node.find('> .p-treenode-content > .p-chkbox'),
            checked = checkbox.find('> .p-chkbox-box > .p-chkbox-icon').hasClass('p-checkbox-checked-icon');

        if (this.cfg.propagateDown) {
            const skippedRowKeys: string[] = [];
            this.propagateCheckboxDown(this.getChildNodes(node), checked, skippedRowKeys);

            // a non-selectable child can never be selected via its checkbox, so it must not keep its parent from
            // being fully selected, see #14798
            const children = node.find('> .p-treenode-children > .p-treenode').filter((_i, el) => {
                return this.isSelectableNode($(el));
            });
            if (checked) {
                if (children.filter('.p-treenode-unselected').length === children.length)
                    this.uncheck(checkbox);
                else
                    this.partialCheck(checkbox);
            }
            else {
                if (children.filter('.p-treenode-selected').length === children.length)
                    this.check(checkbox);
                else
                    this.partialCheck(checkbox);
            }

            if (this.cfg.dynamic) {
                this.removeDescendantsFromSelection(node.data('rowkey'), skippedRowKeys);
            }
        } else {
            this.toggleCheckboxState(checkbox, checked);
        }

        if (this.cfg.propagateUp) {
            node.parents('li.p-treenode-parent').each((_i, el) => {
                const parentNode = $(el),
                    parentsCheckbox = parentNode.find('> .p-treenode-content > .p-chkbox'),
                    children = parentNode.find('> .p-treenode-children > .p-treenode').filter((_j, child) => {
                        return this.isSelectableNode($(child));
                    });

                // a non-selectable node must never be selected by the propagation either, and without any selectable
                // child there is nothing to derive the state of the node from, see #14798
                if (!this.isSelectableNode(parentNode) || !children.length) {
                    return;
                }

                if (checked) {
                    if (children.filter('.p-treenode-unselected').length === children.length)
                        this.uncheck(parentsCheckbox);
                    else
                        this.partialCheck(parentsCheckbox);
                }
                else {
                    if (children.filter('.p-treenode-selected').length === children.length)
                        this.check(parentsCheckbox);
                    else
                        this.partialCheck(parentsCheckbox);
                }
            });
        }

        this.writeSelections();

        if (checked)
            this.fireNodeUnselectEvent(node);
        else
            this.fireNodeSelectEvent(node);
    }

    override getChildNodes(node: JQuery): JQuery {
        return node.find('> .p-treenode-children > .p-treenode:not(.p-treenode-hidden)');
    }

    override preselectCheckbox(): void {
        this.jq.find('.p-chkbox-icon').not('.p-checkbox-checked-icon').each((_i, el) => {
            const icon = $(el),
                node = icon.closest('li');

            if (node.children('.p-treenode-children').find('.p-chkbox-icon.p-checkbox-checked-icon').length > 0) {
                node.addClass('p-treenode-hasselected');
                icon.addClass('p-checkbox-indeterminate-icon');
            }
        });
    }

    override check(checkbox: JQuery): void {
        super.check(checkbox);
        checkbox.parent().addClass('p-state-highlight');
    }

    override uncheck(checkbox: JQuery): void {
        super.uncheck(checkbox);
        checkbox.parent().removeClass('p-state-highlight');
    }

    /**
     * Sets up the drag functionality.
     */
    initDraggable(): void {
        this.makeDraggable(this.jq.find('.p-treenode-content'));
    }

    /**
     * Sets up the drop functionality.
     */
    initDroppable(): void {
        this.makeDropPoints(this.jq.find('li.p-tree-droppoint'));
        this.makeDropNodes(this.jq.find('.p-treenode-droppable'));
        this.initDropScrollers();
    }

    /**
     * Sets up the draggable for the given elements.
     * @param elements A list of draggable nodes to set up.
     */
    override makeDraggable(elements: JQuery): void {
        const dragdropScope = this.cfg.dragdropScope || this.getId();

        PrimeFaces.interactions.draggable(elements, {
            start: (event, ui: PrimeFacesTypes.interactions.DraggableUi) => {
                if (ui.helper) {
                    const element = $(event.target),
                        source = PF($(element.data('dragsourceid')).data('widget')) as VerticalTree;
                    let height = 20;

                    if (source.cfg.multipleDrag && element.hasClass('p-treenode-content') && element.hasClass('p-state-highlight')) {
                        source.draggedSourceKeys = this.findSelectedParentKeys(source.selections.slice());
                        height = 20 * (source.draggedSourceKeys.length || 1);
                    }

                    $(ui.helper).height(height);
                }
            },
            helper: () => {
                const el = $('<div class="p-tree-draghelper p-state-highlight"></div>');
                el.width(this.jq.width()!);
                return el;
            },
            appendTo: document.body,
            // @ts-expect-error DraggableOptions#zIndex is typed as a number, the string from nextZindex works the same
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
     * @param elements A list of drop points to set up.
     */
    override makeDropPoints(elements: JQuery): void {
        const dragdropScope = this.cfg.dragdropScope || this.getId();

        PrimeFaces.interactions.droppable(elements, {
            hoverClass: 'p-state-hover',
            accept: '.p-treenode-content',
            tolerance: 'pointer',
            scope: dragdropScope,
            drop: (event, ui: PrimeFacesTypes.interactions.DroppableUi) => {
                const dragSource = PF($(ui.draggable.data('dragsourceid')).data('widget')) as VerticalTree,
                    dropSource = this,
                    dropPoint = $(event.target),
                    dropNode = dropPoint.closest('li.p-treenode-parent'),
                    dropNodeKey = this.getRowKey(dropNode),
                    transfer = (dragSource.id !== dropSource.id),
                    isDroppedNodeCopy = this.cfg.dropMode === 'copy' || (this.cfg.dropCopyNode && this.shiftKey);
                let draggedSourceKeys: (string | undefined)[] | null | undefined = dragSource.draggedSourceKeys,
                    draggedNodes: JQuery[],
                    dragNodeKey: string | undefined;

                if (draggedSourceKeys) {
                    draggedNodes = dragSource.findNodes(draggedSourceKeys as string[]);
                }
                else {
                    draggedNodes = [ui.draggable];
                }

                if (this.cfg.controlled) {
                    this.droppedNodeParams = [];
                }

                this.invalidSourceKeys = [];

                for (let i = (draggedNodes.length - 1); i >= 0; i--) {
                    const draggedNode = $(draggedNodes[i]!),
                        dragMode = ui.draggable.data('dragmode');
                    let dragNode = draggedNode.is('li.p-treenode') ? draggedNode : draggedNode.closest('li.p-treenode');
                    dragNode = (isDroppedNodeCopy) ? dragNode.clone() : dragNode;
                    const targetDragNode = this.findTargetDragNode(dragNode, dragMode);

                    dragNodeKey = this.getRowKey(targetDragNode);

                    if (!transfer && dropNodeKey && dropNodeKey.indexOf(dragNodeKey + '_') === 0) {
                        return;
                    }

                    if (this.cfg.controlled) {
                        this.droppedNodeParams.push({
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
                        this.onDropPoint(ui, dragSource, dragNode, targetDragNode, dropPoint, dropNode, transfer);
                    }
                }

                if (!draggedSourceKeys) {
                    draggedSourceKeys = [dragNodeKey];
                }
                draggedSourceKeys = draggedSourceKeys.filter((key) => {
                    return $.inArray(key, this.invalidSourceKeys!) === -1;
                });

                const dndIndex = dropPoint.prevAll('li.p-treenode').length;
                if (draggedSourceKeys && draggedSourceKeys.length) {
                    this.fireDragDropEvent({
                        'dragNodeKey': draggedSourceKeys.reverse().join(','),
                        'dropNodeKey': dropNodeKey,
                        'dragSource': dragSource.id,
                        'dndIndex': dndIndex,
                        'transfer': transfer,
                        'isDroppedNodeCopy': isDroppedNodeCopy
                    });
                }

                dragSource.draggedSourceKeys = null;
                this.invalidSourceKeys = null;

                if (isDroppedNodeCopy) {
                    this.initDraggable();
                }
            }
        });
    }

    /**
     * Callback for when a node was dropped on a drop point.
     * @param ui Details about the drop event.
     * @param dragSource Tree widget of the dragged node.
     * @param dragNode Node that was dragged.
     * @param targetDragNode Node that was the target of the drag.
     * @param dropPoint The drop point where the node was dropped.
     * @param dropNode The node on which the dragged node was dropped.
     * @param transfer Whether a transfer should occur, i.e. whether the node was not dropped on itself.
     */
    onDropPoint(ui: PrimeFacesTypes.interactions.DroppableUi, dragSource: VerticalTree, dragNode: JQuery, targetDragNode: JQuery, dropPoint: JQuery, dropNode: JQuery, transfer: boolean): void {
        const dragNodeDropPoint = targetDragNode.next('li.p-tree-droppoint'),
            oldParentNode = targetDragNode.parent().closest('li.p-treenode-parent');

        ui.helper!.remove();
        dropPoint.removeClass('p-state-hover');

        const validDrop = this.validateDropPoint(dragNode, dropPoint);
        if (!validDrop) {
            if (this.invalidSourceKeys) {
                const dragNodeKey = this.getRowKey(targetDragNode);
                this.invalidSourceKeys.push(dragNodeKey);
            }
            return;
        }

        targetDragNode.hide().insertAfter(dropPoint);

        if (transfer) {
            if (dragSource.cfg.selectionMode) {
                dragSource.unselectSubtree(targetDragNode);
            }

            dragNodeDropPoint.remove();
            this.updateDragDropBindings(targetDragNode);
        }
        else {
            dragNodeDropPoint.insertAfter(targetDragNode);
        }

        if (oldParentNode.length && (oldParentNode.find('> ul.p-treenode-children > li.p-treenode').length === 0)) {
            this.makeLeaf(oldParentNode);
        }

        targetDragNode.fadeIn();

        if (this.isCheckboxSelection()) {
            this.syncDNDCheckboxes(dragSource, oldParentNode, dropNode);
        }

        this.syncDragDrop();
        if (transfer) {
            dragSource.syncDragDrop();
        }
    }

    /**
     * Sets up the dropables for the droppable nodes.
     * @param elements List of elements to make droppable.
     */
    override makeDropNodes(elements: JQuery): void {
        const dragdropScope = this.cfg.dragdropScope || this.getId();

        PrimeFaces.interactions.droppable(elements, {
            accept: '.p-treenode-content',
            tolerance: 'pointer',
            scope: dragdropScope,
            over: (event) => {
                $(event.target).children('.p-treenode-content').addClass('p-state-hover');
            },
            out: (event) => {
                $(event.target).children('.p-treenode-content').removeClass('p-state-hover');
            },
            drop: (event, ui: PrimeFacesTypes.interactions.DroppableUi) => {
                const dragSource = PF($(ui.draggable.data('dragsourceid')).data('widget')) as VerticalTree,
                    dropSource = this,
                    droppable = $(event.target),
                    dropNode = droppable.closest('li.p-treenode'),
                    dropNodeKey = this.getRowKey(dropNode),
                    transfer = (dragSource.id !== dropSource.id),
                    isDroppedNodeCopy = (this.cfg.dropCopyNode && this.shiftKey);
                let draggedSourceKeys: (string | undefined)[] | null | undefined = dragSource.draggedSourceKeys,
                    draggedNodes: JQuery[],
                    dragNodeKey: string | undefined,
                    dndIndex: number | undefined;

                if (draggedSourceKeys) {
                    draggedNodes = dragSource.findNodes(draggedSourceKeys as string[]);
                }
                else {
                    draggedNodes = [ui.draggable];
                }

                if (this.cfg.controlled) {
                    this.droppedNodeParams = [];
                }

                this.invalidSourceKeys = [];

                for (let i = 0; i < draggedNodes.length; i++) {
                    const draggedNode = $(draggedNodes[i]!),
                        dragMode = ui.draggable.data('dragmode');
                    let dragNode = draggedNode.is('li.p-treenode') ? draggedNode : draggedNode.closest('li.p-treenode');
                    dragNode = (isDroppedNodeCopy) ? dragNode.clone() : dragNode;
                    const targetDragNode = this.findTargetDragNode(dragNode, dragMode);

                    if (i === 0) {
                        dndIndex = dropNode.find('>.p-treenode-children>li.p-treenode').length;
                    }

                    dragNodeKey = this.getRowKey(targetDragNode);

                    if (!transfer && dropNodeKey && dropNodeKey.indexOf(dragNodeKey + '_') === 0) {
                        return;
                    }

                    if (this.cfg.controlled) {
                        this.droppedNodeParams.push({
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
                        this.onDropNode(ui, dragSource, dragNode, targetDragNode, droppable, dropNode, transfer);
                    }
                }

                if (!draggedSourceKeys) {
                    draggedSourceKeys = [dragNodeKey];
                }
                draggedSourceKeys = draggedSourceKeys.filter((key) => {
                    return $.inArray(key, this.invalidSourceKeys!) === -1;
                });

                if (draggedSourceKeys && draggedSourceKeys.length) {
                    this.fireDragDropEvent({
                        'dragNodeKey': draggedSourceKeys.reverse().join(','),
                        'dropNodeKey': dropNodeKey,
                        'dragSource': dragSource.id,
                        'dndIndex': dndIndex,
                        'transfer': transfer,
                        'isDroppedNodeCopy': isDroppedNodeCopy
                    });
                }

                dragSource.draggedSourceKeys = null;
                this.invalidSourceKeys = null;

                if (isDroppedNodeCopy) {
                    this.initDraggable();
                }
            }
        });
    }

    /**
     * Callback for when a node was dropped.
     * @param ui Details about the drop event.
     * @param dragSource Tree widget of the dragged node.
     * @param dragNode Node that was dragged.
     * @param targetDragNode Node that was the target of the drag.
     * @param droppable The droppable where the drop occurred.
     * @param dropNode The node on which the dragged node was dropped.
     * @param transfer Whether a transfer should occur.
     */
    onDropNode(ui: PrimeFacesTypes.interactions.DroppableUi, dragSource: VerticalTree, dragNode: JQuery, targetDragNode: JQuery, droppable: JQuery, dropNode: JQuery, transfer: boolean): void {
        const dragNodeDropPoint = targetDragNode.next('li.p-tree-droppoint'),
            oldParentNode = targetDragNode.parent().closest('li.p-treenode-parent'),
            childrenContainer = dropNode.children('.p-treenode-children');

        ui.helper!.remove();
        droppable.children('.p-treenode-content').removeClass('p-state-hover');

        const validDrop = this.validateDropNode(dragNode, dropNode, oldParentNode);
        if (!validDrop) {
            if (this.invalidSourceKeys) {
                const dragNodeKey = this.getRowKey(targetDragNode);
                this.invalidSourceKeys.push(dragNodeKey);
            }
            return;
        }

        if (childrenContainer.children('li.p-treenode').length === 0) {
            this.makeParent(dropNode);
        }

        targetDragNode.hide();
        childrenContainer.append(targetDragNode);

        if (oldParentNode.length && (oldParentNode.find('> ul.p-treenode-children > li.p-treenode').length === 0)) {
            this.makeLeaf(oldParentNode);
        }

        if (transfer) {
            if (dragSource.cfg.selectionMode) {
                dragSource.unselectSubtree(targetDragNode);
            }

            dragNodeDropPoint.remove();
            this.updateDragDropBindings(targetDragNode);
        }
        else {
            childrenContainer.append(dragNodeDropPoint);
        }

        targetDragNode.fadeIn();

        if (this.isCheckboxSelection()) {
            this.syncDNDCheckboxes(dragSource, oldParentNode, dropNode);
        }

        this.syncDragDrop();
        if (transfer) {
            dragSource.syncDragDrop();
        }
    }

    /**
     * Filters the given array of row keys and removes child nodes of parent node in the array.
     * @param arr A list of row keys to check.
     * @return A list of parent row keys.
     */
    findSelectedParentKeys(arr: (string | -1)[]): string[] {
        for (let i = 0; i < arr.length; i++) {
            const key = arr[i]!;
            for (let j = 0; j < arr.length && key !== -1; j++) {
                const tempKey = arr[j]!;
                if (tempKey !== -1 && key.length > tempKey.length && key.indexOf(tempKey + '_') === 0) {
                    arr[i] = -1;
                }
            }
        }

        return arr.filter((item): item is string => {
            return item !== -1;
        });
    }

    /**
     * Initializes all drop scrollers of this tree.
     */
    initDropScrollers(): void {
        const dragdropScope = this.cfg.dragdropScope || this.getId();

        this.jq.prepend('<div class="p-tree-scroller p-tree-scrollertop"></div>').append('<div class="p-tree-scroller p-tree-scrollerbottom"></div>');

        PrimeFaces.interactions.droppable(this.jq.children('div.p-tree-scroller'), {
            accept: '.p-treenode-content',
            tolerance: 'pointer',
            scope: dragdropScope,
            over: (event) => {
                const step = $(event.target).hasClass('p-tree-scrollertop') ? -10 : 10;

                this.scrollInterval = setInterval(() => {
                    this.scroll(step);
                }, 100);
            },
            out: () => {
                clearInterval(this.scrollInterval);
            }
        });
    }

    /**
     * Scrolls this tree by the given amount.
     * @param step Amount by which to scroll.
     */
    scroll(step: number): void {
        this.container.scrollTop(this.container.scrollTop()! + step);
    }

    /**
     * Updates the drag&drop settings for the given node.
     * @param node Node to update.
     */
    updateDragDropBindings(node: JQuery): void {
        //self droppoint
        node.after('<li class="p-tree-droppoint p-droppable"></li>');
        this.makeDropPoints(node.next('li.p-tree-droppoint'));

        //descendant droppoints
        const subtreeDropPoints = node.find('li.p-tree-droppoint');
        if (subtreeDropPoints.hasClass('p-droppable') && !this.shiftKey && !this.cfg.dropCopyNode) {
            PrimeFaces.interactions.destroy(subtreeDropPoints, 'droppable');
        }
        this.makeDropPoints(subtreeDropPoints);

        //descendant drop node contents
        const subtreeDropNodeContents = node.find('.p-treenode-content');
        if (subtreeDropNodeContents.hasClass('p-droppable') && !this.shiftKey && !this.cfg.dropCopyNode) {
            PrimeFaces.interactions.destroy(subtreeDropNodeContents, 'droppable');
        }
        this.makeDropNodes(subtreeDropNodeContents);

        if (this.cfg.draggable) {
            subtreeDropNodeContents.data({
                'dragsourceid': this.jqId,
                'dragmode': this.cfg.dragMode
            });
        }
    }

    /**
     * Locates the target drag node, depending on the given drag mode.
     * @param dragNode Node that was dragged.
     * @param dragMode The current drag mode of this tree.
     * @return The resolved target drag node.
     */
    findTargetDragNode(dragNode: JQuery, dragMode: PrimeFacesTypes.widget.BaseTree.DragMode): JQuery {
        let targetDragNode: JQuery | null = null;

        if (dragMode === 'self') {
            targetDragNode = dragNode;
        } else if (dragMode === 'parent') {
            targetDragNode = dragNode.parent().closest('li.p-treenode');
        } else if (dragMode === 'ancestor') {
            targetDragNode = dragNode.parent().parents('li.p-treenode:last');
        }

        if (targetDragNode!.length === 0) {
            targetDragNode = dragNode;
        }

        return targetDragNode!;
    }

    /**
     * Finds the nodes with the given row keys.
     * @param rowkeys A list of row keys.
     * @return A list of nodes corresponding to the given row keys, in that order.
     */
    findNodes(rowkeys: string[]): JQuery[] {
        const nodes: JQuery[] = [];
        for (let i = 0; i < rowkeys.length; i++) {
            nodes.push($(this.jqId + '\\:' + rowkeys[i]));
        }

        return nodes;
    }

    /**
     * Updates the row keys of all nodes.
     */
    updateRowKeys(): void {
        const children = this.jq.find('> ul.p-tree-container > li.p-treenode');
        this.updateChildrenRowKeys(children, null);
    }

    /**
     * Updates the row keys of all given children.
     * @param children List of children to update.
     * @param rowkey Base prefix for the new rowkey.
     */
    updateChildrenRowKeys(children: JQuery, rowkey: string | null): void {
        children.each((i, el) => {
            const childNode = $(el),
                newRowKey = (rowkey === null) ? i.toString() : rowkey + '_' + i;

            childNode.attr({
                'id': this.id + ':' + newRowKey,
                'data-rowkey': newRowKey
            });

            if (childNode.hasClass('p-treenode-parent')) {
                this.updateChildrenRowKeys(childNode.find('> ul.p-treenode-children > li.p-treenode'), newRowKey);
            }
        });
    }

    /**
     * After a drag&drop, validates if the drop is allowed.
     * @param dragNode Node that was dragged.
     * @param dropPoint Element where the node was dropped.
     * @return Whether the drop is allowed.
     */
    validateDropPoint(dragNode: JQuery, dropPoint: JQuery): boolean {
        //dropped before or after
        if (dragNode.next().get(0) === dropPoint.get(0) || dragNode.prev().get(0) === dropPoint.get(0)) {
            return false;
        }

        //descendant of dropnode
        if (dragNode.has(dropPoint.get(0)!).length) {
            return false;
        }

        //drop restriction
        if (this.cfg.dropRestrict) {
            if (this.cfg.dropRestrict === 'sibling' && dragNode.parent().get(0) !== dropPoint.parent().get(0)) {
                return false;
            }
        }

        return true;
    }

    /**
     * After a drag&drop, validates if the drop is allowed.
     * @param dragNode Node that was dragged.
     * @param dropNode Node on which the dragged node was dropped.
     * @param oldParentNode Old parent of the dragged node.
     * @return Whether the drop is allowed.
     */
    validateDropNode(dragNode: JQuery, dropNode: JQuery, oldParentNode: JQuery): boolean {
        //dropped on parent
        if (oldParentNode.get(0) === dropNode.get(0))
            return false;

        //descendant of dropnode
        if (dragNode.has(dropNode.get(0)!).length) {
            return false;
        }

        //drop restriction
        if (this.cfg.dropRestrict) {
            if (this.cfg.dropRestrict === 'sibling') {
                return false;
            }
        }

        return true;
    }

    /**
     * Turns the given node into a leaf node.
     * @param node A new leaf node to convert.
     */
    makeLeaf(node: JQuery): void {
        node.removeClass('p-treenode-parent').addClass('p-treenode-leaf').attr('aria-expanded', 'false');
        node.find('> .p-treenode-content > .p-tree-toggler').addClass('p-treenode-leaf-icon').removeClass('p-tree-toggler p-icon p-toggle-icon p-tree-toggle-icon');
        node.children('.p-treenode-children').hide().children().remove();
    }

    /**
     * Turns the given node into a parent node.
     * @param node A new parent node to convert.
     */
    makeParent(node: JQuery): void {
        node.removeClass('p-treenode-leaf').addClass('p-treenode-parent');
        node.find('> .p-treenode-content > span.p-treenode-leaf-icon').removeClass('p-treenode-leaf-icon').addClass('p-tree-toggler p-icon p-toggle-icon p-tree-toggle-icon');
        node.children('.p-treenode-children').append('<li class="p-tree-droppoint p-droppable"></li>');

        this.makeDropPoints(node.find('> ul.p-treenode-children > li.p-tree-droppoint'));
    }

    /**
     * Updates the tree after a drag&drop event.
     */
    syncDragDrop(): void {
        if (this.cfg.selectionMode) {
            const selectedNodes = this.findNodes(this.selections);

            this.updateRowKeys();
            this.selections = [];
            $.each(selectedNodes, (i, item) => {
                this.selections.push(item.attr('data-rowkey')!);
            });
            this.writeSelections();
        }
        else {
            this.updateRowKeys();
        }
    }

    /**
     * Updates all checkboxes after a drag&drop.
     * @param dragSource Tree widget that is the source of the drag, when dragging between two widgets.
     * @param oldParentNode Old node that was parent of the dropped node.
     * @param newParentNode New node that is to be the parent of the dropped node.
     */
    syncDNDCheckboxes(dragSource: VerticalTree, oldParentNode: JQuery, newParentNode: JQuery): void {
        if (oldParentNode.length) {
            dragSource.propagateDNDCheckbox(oldParentNode);
        }

        if (newParentNode.length) {
            this.propagateDNDCheckbox(newParentNode);
        }
    }

    /**
     * Unselects the node and all child nodes.
     * @param node Node to unselect.
     */
    unselectSubtree(node: JQuery): void {
        if (this.isCheckboxSelection()) {
            const checkbox = node.find('> .p-treenode-content > .p-chkbox');

            this.toggleCheckboxState(checkbox, true);

            node.children('.p-treenode-children').find('.p-chkbox').each((_i, el) => {
                this.toggleCheckboxState($(el), true);
            });
        }
        else {
            node.find('.p-treenode-content.p-state-highlight').each((_i, el) => {
                $(el).removeClass('p-state-highlight').closest('li.p-treenode').attr('aria-selected', 'false').removeClass('p-treenode-selected').addClass('p-treenode-unselected');
            });
        }
    }

    /**
     * Updates the drag&drop checkboxes.
     * @param node Node to which to limit the update.
     */
    propagateDNDCheckbox(node: JQuery): void {
        const checkbox = node.find('> .p-treenode-content > .p-chkbox'),
            children = node.find('> .p-treenode-children > .p-treenode');

        if (children.length) {
            if (children.filter('.p-treenode-unselected').length === children.length)
                this.uncheck(checkbox);
            else if (children.filter('.p-treenode-selected').length === children.length)
                this.check(checkbox);
            else
                this.partialCheck(checkbox);
        }

        const parent = node.parent().closest('.p-treenode-parent');
        if (parent.length) {
            this.propagateDNDCheckbox(parent);
        }
    }

    /**
     * Callback for when a drag&drop occurred. Invokes the appropriate behaviors.
     * @param event Details about the drag&drop that occurred.
     */
    fireDragDropEvent(event: PrimeFacesTypes.widget.VerticalTree.DragDropEvent): void {
        const options: PrimeFacesTypes.ajax.Configuration = {
            source: this.getId(),
            process: event.transfer ? this.id + ' ' + event.dragSource : this.getId()
        };

        options.params = [
            { name: this.id + '_dragdrop', value: true },
            { name: this.id + '_dragNode', value: event.dragNodeKey },
            { name: this.id + '_dragSource', value: event.dragSource },
            { name: this.id + '_dropNode', value: event.dropNodeKey },
            { name: this.id + '_dndIndex', value: event.dndIndex },
            { name: this.id + '_isDroppedNodeCopy', value: event.isDroppedNodeCopy }
        ];

        if (this.cfg.controlled) {
            options.oncomplete = (_xhr, _status, args) => {
                if (args!.access) {
                    for (let i = 0; i < this.droppedNodeParams.length; i++) {
                        const params = this.droppedNodeParams[i]!;
                        if (params.dropPoint)
                            this.onDropPoint(params.ui, params.dragSource, params.dragNode, params.targetDragNode, params.dropPoint, params.dropNode, params.transfer);
                        else
                            this.onDropNode(params.ui, params.dragSource, params.dragNode, params.targetDragNode, params.droppable!, params.dropNode, params.transfer);
                    }
                }
            };
        }

        if (this.hasBehavior('dragdrop')) {
            this.callBehavior('dragdrop', options);
        }
        else {
            PrimeFaces.ajax.Request.handle(options);
        }
    }

    override isEmpty(): boolean {
        return (this.container.children().length === 0);
    }

    /**
     * Finds the first node.
     * @return The first node of this tree.
     */
    getFirstNode(): JQuery {
        return this.jq.find("> ul.p-tree-container > li.p-treenode").first();
    }

    /**
     * Finds the content element for the given node.
     * @param node Node for which to find the corresponding content.
     * @return The element with the content for the given node.
     */
    getNodeContent(node: JQuery): JQuery {
        return node.find('> .p-treenode-content');
    }

    override focusNode(node: JQuery): void {
        if (this.focusedNode) {
            this.getNodeContent(this.focusedNode).removeClass('p-treenode-outline');
        }

        this.getNodeContent(node).addClass('p-treenode-outline').trigger('focus');
        this.focusedNode = node;
    }

    /**
     * Applies the current filter value by sending an AJAX to the server.
     */
    filter(): void {
        const options: PrimeFacesTypes.ajax.Configuration = {
            source: this.getId(),
            update: this.getId(),
            process: this.getId(),
            global: false,
            formId: this.getParentFormId(),
            params: [{ name: this.id + '_filtering', value: true },
                { name: this.id + '_encodeFeature', value: true }],
            onsuccess: (responseXML, status, xhr) => {
                PrimeFaces.ajax.Response.handle(responseXML, status, xhr, {
                    widget: this,
                    handle: (content) => {
                        this.container.html(content);
                    }
                });

                return true;
            },
            oncomplete: () => {
                // filterMode is "lenient" or "strict", the old "contains" mode does not exist anymore
                if ((this.cfg.filterMode as string | undefined) === 'contains') {
                    const notLeafNodes = this.container.find('li.p-treenode:not(.p-treenode-leaf):visible');
                    for (let i = 0; i < notLeafNodes.length; i++) {
                        const node = notLeafNodes.eq(i),
                            hasChildNodes = node.children('.p-treenode-children:empty').length;

                        if (hasChildNodes) {
                            node.removeClass('p-treenode-parent').addClass('p-treenode-leaf')
                                .find('> .p-treenode-content > .p-tree-toggler').removeClass('p-tree-toggler p-icon p-toggle-icon p-tree-toggle-icon').addClass('p-treenode-leaf-icon');
                        }
                    }
                }

                if (this.cfg.draggable) {
                    this.initDraggable();
                }

                if (this.cfg.droppable) {
                    this.initDroppable();
                }
            }
        };

        if (this.hasBehavior('filter')) {
            this.callBehavior('filter', options);
        }
        else {
            PrimeFaces.ajax.Request.handle(options);
        }

    }

    /**
     * Reads the saved scroll position from the hidden input field and applies it.
     */
    restoreScrollState(): void {
        const scrollState = this.scrollStateHolder.val() as string,
            scrollValues = scrollState.split(',');

        this.jq.scrollLeft(Number(scrollValues[0]));
        this.jq.scrollTop(Number(scrollValues[1]));
    }

    /**
     * Saves the current scroll position to the hidden input field.
     */
    saveScrollState(): void {
        const scrollState = this.jq.scrollLeft() + ',' + this.jq.scrollTop();

        this.scrollStateHolder.val(scrollState);
    }

    /**
     * Resets the value of the hidden input field with the current scroll position.
     */
    clearScrollState(): void {
        this.scrollStateHolder.val('0,0');
    }

}
