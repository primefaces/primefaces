/**
 * __PrimeFaces SelectListbox Widget__
 * 
 * Base class for the `SelectManyMenu` and `SelectOneListBox` widgets. Contains some common functionality such as
 * filtering and working with SELECT and OPTION elements.
 * 
 * @typedef {"startsWith" |  "contains" |  "endsWith" | "custom"} PrimeFaces.widget.SelectListbox.FilterMatchMode
 * Available modes for filtering the options of a select list box. When `custom` is set, a `filterFunction` must be
 * specified.
 * 
 * @typedef PrimeFaces.widget.SelectListbox.FilterFunction A function for filtering the options of a select list box.
 * @param {string} PrimeFaces.widget.SelectListbox.FilterFunction.itemLabel The label of the currently selected text.
 * @param {string} PrimeFaces.widget.SelectListbox.FilterFunction.filterValue The value to search for.
 * @return {boolean} PrimeFaces.widget.SelectListbox.FilterFunction `true` if the item label matches the filter value,
 * or `false` otherwise.
 * 
 * @prop {JQuery} allItems All available items, including disabled options. These are not form elements, but the DOM
 * elements presented to the user.
 * @prop {PrimeFaces.widget.SelectListbox.FilterFunction} filterMatcher The filter that was selected and is currently
 * used.
 * @prop {Record<PrimeFaces.widget.SelectListbox.FilterMatchMode, PrimeFaces.widget.SelectListbox.FilterFunction>} filterMatchers
 * Map between the available filter types and the filter implementation.
 * @prop {JQuery} input The hidden INPUT or SELECT element.
 * @prop {JQuery} items All available items, excluding disabled options. These are not form elements, but the DOM
 * elements presented to the user.
 * @prop {JQuery} listContainer Container of the list element.
 * @prop {JQuery} listElement The element that contains the available items.
 * @prop {JQuery<HTMLOptionElement>} options A list of the available OPTION elements of the hidden SELECT element.
 * 
 * @interface {PrimeFaces.widget.SelectListboxCfg} cfg The configuration for the {@link  SelectListbox| SelectListbox widget}.
 * You can access this configuration via {@link PrimeFaces.widget.BaseWidget.cfg|BaseWidget.cfg}. Please note that this
 * configuration is usually meant to be read-only and should not be modified.
 * @extends {PrimeFaces.widget.BaseWidgetCfg} cfg
 * 
 * @prop {boolean} cfg.caseSensitive `true` if filtering is case-sensitive, `false` otherwise.
 * @prop {boolean} cfg.filter `true` if the options can be filtered, or `false` otherwise.
 * @prop {PrimeFaces.widget.SelectListbox.FilterFunction} cfg.filterFunction A custom filter function that is used when
 * `filterMatchMode` is set to `custom`.
 * @prop {PrimeFaces.widget.SelectListbox.FilterMatchMode} cfg.filterMatchMode Mode of the filter. When set to `custom`
 *  a `filterFunction` must be specified.
 */
PrimeFaces.widget.SelectListbox = PrimeFaces.widget.BaseWidget.extend({

    /**
     * @override
     * @inheritdoc
     * @param {PrimeFaces.PartialWidgetCfg<TCfg>} cfg
     */
    init: function(cfg) {
        this._super(cfg);

        this.input = $(this.jqId + '_input'),
        this.listContainer = this.jq.children('.ui-selectlistbox-listcontainer');
        this.listElement = this.listContainer.children('.ui-selectlistbox-list');
        this.options = $(this.input).children('option');
        this.allItems = this.listElement.find('.ui-selectlistbox-item');
        this.items = this.allItems.filter(':not(.ui-state-disabled)');

        //scroll to selected
        var selected = this.options.filter(':selected:not(:disabled)');
        if(selected.length) {
            PrimeFaces.scrollInView(this.listContainer, this.items.eq(selected.eq(0).index()));
        }

        this.bindEvents();

        //pfs metadata
        this.input.data(PrimeFaces.CLIENT_ID_DATA, this.id);
    },

    /**
     * Sets up all event listeners for this widget instance.
     * @protected
     */
    bindEvents: function() {
        var $this = this;

        //items
        this.items.on('mouseover.selectListbox', function() {
            var item = $(this);
            if(!item.hasClass('ui-state-highlight')) {
                item.addClass('ui-state-hover');
            }
        })
        .on('mouseout.selectListbox', function() {
            $(this).removeClass('ui-state-hover');
        })
        .on('dblclick.selectListbox', function(e) {
            $this.input.trigger('dblclick');

            PrimeFaces.clearSelection();
            e.preventDefault();
        });

        //input
        this.input.on('focus.selectListbox', function() {
            $this.jq.addClass('ui-state-focus');
        }).on('blur.selectListbox', function() {
            $this.jq.removeClass('ui-state-focus');
        });

        if(this.cfg.filter) {
            this.filterInput = this.jq.find('> div.ui-selectlistbox-filter-container > input.ui-selectlistbox-filter');
            PrimeFaces.skinInput(this.filterInput);
            this.filterInput.on('keyup.selectListbox', function(e) {
                $this.filter(this.value);
            });

            this.setupFilterMatcher();
        }
    },

    /**
     * Unselects all items that are currently selected.
     */
    unselectAll: function() {
        this.items.removeClass('ui-state-highlight ui-state-hover');
        this.options.filter(':selected').prop('selected', false);
    },

    /**
     * Select the given item as the currently selected option. Does not unselect other items that are already selected.
     * @param {JQuery} item An OPTION element to set as the selected element.
     */
    selectItem: function(item) {
        item.addClass('ui-state-highlight').removeClass('ui-state-hover');
        this.options.eq(item.index()).prop('selected', true);
    },

    /**
     * Unselect the given items. Does not change other selected items. 
     * @param {JQuery} item Item to unselect.
     */
    unselectItem: function(item) {
        item.removeClass('ui-state-highlight');
        this.options.eq(item.index()).prop('selected', false);
    },

    /**
     * Finds and stores the filter function which is to be used for filtering the options of this select list box.
     * @private
     */
    setupFilterMatcher: function() {
        this.cfg.filterMatchMode = this.cfg.filterMatchMode||'startsWith';
        this.filterMatchers = {
            'startsWith': this.startsWithFilter
            ,'contains': this.containsFilter
            ,'endsWith': this.endsWithFilter
            ,'custom': this.cfg.filterFunction
        };

        this.filterMatcher = this.filterMatchers[this.cfg.filterMatchMode];
    },

    /**
     * Implementation of a `PrimeFaces.widget.SelectListbox.FilterFunction` that matches the given option when it starts
     * with the given search text.
     * @param {string} value Text of an option.
     * @param {string} filter Value of the filter.
     * @return {boolean} `true` when the text of the options starts with the filter value, or `false` otherwise.
     */
    startsWithFilter: function(value, filter) {
        return value.indexOf(filter) === 0;
    },

    /**
     * Implementation of a `PrimeFaces.widget.SelectListbox.FilterFunction` that matches the given option when it
     * contains the given search text.
     * @param {string} value Text of an option.
     * @param {string} filter Value of the filter.
     * @return {boolean} `true` when the text of the contains the filter value, or `false` otherwise.
     */
    containsFilter: function(value, filter) {
        return value.indexOf(filter) !== -1;
    },

    /**
     * Implementation of a `PrimeFaces.widget.SelectListbox.FilterFunction` that matches the given option when it ends
     * with the given search text.
     * @param {string} value Text of an option.
     * @param {string} filter Value of the filter.
     * @return {boolean} `true` when the text of the options ends with the filter value, or `false` otherwise.
     */
    endsWithFilter: function(value, filter) {
        return value.indexOf(filter, value.length - filter.length) !== -1;
    },

    /**
     * Filters the options of this select list box by the given search value.
     * @param {string} value Current value of the filter.
     */
    filter: function(value) {
        var filterValue = this.cfg.caseSensitive ? PrimeFaces.trim(value) : PrimeFaces.trim(value).toLowerCase();

        if(filterValue === '') {
            this.items.filter(':hidden').show();
        }
        else {
            for(var i = 0; i < this.options.length; i++) {
                var option = this.options.eq(i),
                itemLabel = this.cfg.caseSensitive ? option.text() : option.text().toLowerCase(),
                item = this.items.eq(i);

                if(this.filterMatcher(itemLabel, filterValue))
                    item.show();
                else
                    item.hide();
            }
        }
    }
});
