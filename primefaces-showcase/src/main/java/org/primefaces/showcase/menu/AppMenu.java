/*
 * The MIT License
 *
 * Copyright (c) 2009-2021 PrimeTek
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.primefaces.showcase.menu;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Named
@ApplicationScoped
public class AppMenu {

    List<MenuCategory> menuCategories;
    List<MenuItem> menuItems;

    @PostConstruct
    public void init() {
        menuCategories = new ArrayList<>();
        menuItems = new ArrayList<>();

        //GENERAL CATEGORY START
        List<MenuItem> generalMenuItems = new ArrayList<>();
        generalMenuItems.add(new MenuItem("Get Started", "/getstarted"));
        generalMenuItems.add(new MenuItem("Documentation", "https://primefaces.github.io/primefaces/10_0_0/#/"));
        generalMenuItems.add(new MenuItem("Content Security", "https://primefaces.github.io/primefaces/10_0_0/#/core/contentsecuritypolicy"));
        menuCategories.add(new MenuCategory("General", generalMenuItems));
        //GENERAL CATEGORY END

        //SUPPORT CATEGORY START
        List<MenuItem> supportMenuItems = new ArrayList<>();
        supportMenuItems.add(new MenuItem("Forum", "https://forum.primefaces.org"));
        supportMenuItems.add(new MenuItem("Discord Chat", "https://discord.gg/gzKFYnpmCY"));
        supportMenuItems.add(new MenuItem("PRO Support", "/support"));
        menuCategories.add(new MenuCategory("Support", supportMenuItems));
        //SUPPORT CATEGORY END

        //RESOURCES CATEGORY START
        List<MenuItem> resourcesMenuItems = new ArrayList<>();
        resourcesMenuItems.add(new MenuItem("PrimeTV", "https://www.youtube.com/channel/UCTgmp69aBOlLnPEqlUyetWw"));
        resourcesMenuItems.add(new MenuItem("Source Code", "https://github.com/primefaces/primefaces"));
        resourcesMenuItems.add(new MenuItem("Store", "https://www.primefaces.org/store"));
        resourcesMenuItems.add(new MenuItem("Twitter", "https://twitter.com/primefaces?lang=en"));
        menuCategories.add(new MenuCategory("Resources", resourcesMenuItems));
        //RESOURCES CATEGORY END

        //THEMING CATEGORY START
        List<MenuItem> themingMenuItems = new ArrayList<>();
        themingMenuItems.add(new MenuItem("Introduction", "/theming"));
        themingMenuItems.add(new MenuItem("Theme Designer", "https://www.primefaces.org/designer/primefaces"));
        themingMenuItems.add(new MenuItem("Visual Editor", "https://www.primefaces.org/designer-jsf"));
        themingMenuItems.add(new MenuItem("SASS API", "https://www.primefaces.org/designer/api/primefaces/10.0.0/"));
        menuCategories.add(new MenuCategory("Theming", themingMenuItems));
        //THEMING CATEGORY END

        //PRIMEFLEX CATEGORY START
        List<MenuItem> primeFlexMenuItems = new ArrayList<>();
        primeFlexMenuItems.add(new MenuItem("Setup", "/primeflex/setup"));
        primeFlexMenuItems.add(new MenuItem("Display", "/primeflex/display"));
        primeFlexMenuItems.add(new MenuItem("Elevation", "/primeflex/elevation"));
        primeFlexMenuItems.add(new MenuItem("FlexBox", "/primeflex/flexbox"));
        primeFlexMenuItems.add(new MenuItem("Form Layout", "/primeflex/formlayout"));
        primeFlexMenuItems.add(new MenuItem("Grid System", "/primeflex/grid"));
        primeFlexMenuItems.add(new MenuItem("Spacing", "/primeflex/spacing"));
        primeFlexMenuItems.add(new MenuItem("Text", "/primeflex/text"));
        menuCategories.add(new MenuCategory("PrimeFlex", primeFlexMenuItems));
        //PRIMEFLEX CATEGORY END

        //PRIMEICONS CATEGORY START
        List<MenuItem> primeIconsMenuItems = new ArrayList<>();
        primeIconsMenuItems.add(new MenuItem("Icons v4.1", "/icons"));
        menuCategories.add(new MenuCategory("PrimeIcons", primeIconsMenuItems));
        //PRIMEICONS CATEGORY END

        //AJAX FRAMEWORK CATEGORY START
        List<MenuItem> ajaxFrameworkMenuItems = new ArrayList<>();
        ajaxFrameworkMenuItems.add(new MenuItem("Introduction", "/ui/ajax/basic"));
        ajaxFrameworkMenuItems.add(new MenuItem("Process", "/ui/ajax/process"));
        ajaxFrameworkMenuItems.add(new MenuItem("PartialSubmit", "/ui/ajax/partialSubmit"));
        ajaxFrameworkMenuItems.add(new MenuItem("Selector", "/ui/ajax/selector"));
        ajaxFrameworkMenuItems.add(new MenuItem("Search", "/ui/ajax/search"));
        ajaxFrameworkMenuItems.add(new MenuItem("Validation", "/ui/ajax/validation"));
        ajaxFrameworkMenuItems.add(new MenuItem("RemoteCommand", "/ui/ajax/remoteCommand"));
        ajaxFrameworkMenuItems.add(new MenuItem("Observer", "/ui/ajax/observer", "New"));
        ajaxFrameworkMenuItems.add(new MenuItem("Poll", "/ui/ajax/poll"));
        ajaxFrameworkMenuItems.add(new MenuItem("Fragment", "/ui/ajax/fragment"));
        ajaxFrameworkMenuItems.add(new MenuItem("Status", "/ui/ajax/status"));
        ajaxFrameworkMenuItems.add(new MenuItem("Lifecycle", "/ui/ajax/lifecycle"));
        ajaxFrameworkMenuItems.add(new MenuItem("Dropdown", "/ui/ajax/dropdown"));
        menuCategories.add(new MenuCategory("Ajax Framework", ajaxFrameworkMenuItems));
        //AJAX FRAMEWORK CATEGORY END

        //FORM CATEGORY START
        List<MenuItem> formMenuItems = new ArrayList<>();
        formMenuItems.add(new MenuItem("AutoComplete", "/ui/input/autoComplete"));

        //Calendar Nested MenuItem
        /*List<MenuItem> calendarMenuItems = new ArrayList<>();
        calendarMenuItems.add(new MenuItem("Date (java.util.Date)", "/ui/input/calendar/calendar"));
        calendarMenuItems.add(new MenuItem("LocalDate (java.time.Local*)", "/ui/input/calendar/calendarJava8"));
        formMenuItems.add(new MenuItem("Calendar", calendarMenuItems));*/

        formMenuItems.add(new MenuItem("CascadeSelect", "/ui/input/cascadeSelect", "New"));
        formMenuItems.add(new MenuItem("Chips", "/ui/input/chips"));
        formMenuItems.add(new MenuItem("ColorPicker", "/ui/input/colorPicker"));

        //DatePicker Nested MenuItem
        List<MenuItem> datePickerMenuItems = new ArrayList<>();
        datePickerMenuItems.add(new MenuItem("java.util.Date", "/ui/input/datepicker/datePicker"));
        datePickerMenuItems.add(new MenuItem("Java 8+ Date APIs", "/ui/input/datepicker/datePickerJava8"));
        datePickerMenuItems.add(new MenuItem("Metadata", "/ui/input/datepicker/metadata"));
        formMenuItems.add(new MenuItem("DatePicker", datePickerMenuItems));

        formMenuItems.add(new MenuItem("Inplace", "/ui/input/inplace"));
        formMenuItems.add(new MenuItem("InputGroup", "/ui/input/inputGroup"));
        formMenuItems.add(new MenuItem("InputMask", "/ui/input/inputMask"));
        formMenuItems.add(new MenuItem("InputNumber", "/ui/input/inputNumber"));
        //formMenuItems.add(new MenuItem("InputSwitch", "/ui/input/inputSwitch"));
        formMenuItems.add(new MenuItem("InputText", "/ui/input/inputText"));
        formMenuItems.add(new MenuItem("InputTextArea", "/ui/input/inputTextarea"));
        formMenuItems.add(new MenuItem("KeyFilter", "/ui/input/keyFilter"));
        formMenuItems.add(new MenuItem("Keyboard", "/ui/input/keyboard"));
        formMenuItems.add(new MenuItem("Knob", "/ui/input/knob"));
        formMenuItems.add(new MenuItem("MultiSelectListBox", "/ui/input/multiSelectListbox"));
        formMenuItems.add(new MenuItem("Password", "/ui/input/password"));
        formMenuItems.add(new MenuItem("Rating", "/ui/input/rating"));
        formMenuItems.add(new MenuItem("SelectBooleanButton", "/ui/input/booleanButton"));
        formMenuItems.add(new MenuItem("SelectBooleanCheckbox", "/ui/input/booleanCheckbox"));
        formMenuItems.add(new MenuItem("SelectOneButton", "/ui/input/oneButton"));
        formMenuItems.add(new MenuItem("SelectOneRadio", "/ui/input/oneRadio"));
        formMenuItems.add(new MenuItem("SelectCheckboxMenu", "/ui/input/checkboxMenu"));
        formMenuItems.add(new MenuItem("SelectOneMenu", "/ui/input/oneMenu"));
        formMenuItems.add(new MenuItem("SelectOneListbox", "/ui/input/listbox"));
        formMenuItems.add(new MenuItem("SelectManyButton", "/ui/input/manyButton"));
        formMenuItems.add(new MenuItem("SelectManyMenu", "/ui/input/manyMenu"));
        formMenuItems.add(new MenuItem("SelectManyCheckbox", "/ui/input/manyCheckbox"));
        formMenuItems.add(new MenuItem("Signature", "/ui/input/signature"));
        formMenuItems.add(new MenuItem("Slider", "/ui/input/slider"));
        formMenuItems.add(new MenuItem("Spinner", "/ui/input/spinner"));
        formMenuItems.add(new MenuItem("TextEditor", "/ui/input/textEditor"));
        formMenuItems.add(new MenuItem("ToggleSwitch", "/ui/input/toggleSwitch"));
        formMenuItems.add(new MenuItem("TriStateCheckbox", "/ui/input/triStateCheckbox"));
        menuCategories.add(new MenuCategory("Form", formMenuItems));
        //FORM CATEGORY END

        //BUTTON CATEGORY START
        List<MenuItem> buttonMenuItems = new ArrayList<>();
        buttonMenuItems.add(new MenuItem("Button", "/ui/button/button"));
        buttonMenuItems.add(new MenuItem("CommandButton", "/ui/button/commandButton"));
        buttonMenuItems.add(new MenuItem("CommandLink", "/ui/button/commandLink"));
        buttonMenuItems.add(new MenuItem("Link", "/ui/button/link"));
        buttonMenuItems.add(new MenuItem("LinkButton", "/ui/button/linkButton"));
        buttonMenuItems.add(new MenuItem("SplitButton", "/ui/button/splitButton"));
        menuCategories.add(new MenuCategory("Button", buttonMenuItems));
        //BUTTON CATEGORY END

        //DATA CATEGORY START
        List<MenuItem> dataMenuItems = new ArrayList<>();
        dataMenuItems.add(new MenuItem("Carousel", "/ui/data/carousel"));
        dataMenuItems.add(new MenuItem("Chronoline", "/ui/data/chronoline", "New"));

        //DataExporter Nested MenuItem
        List<MenuItem> dataExporterMenuItems = new ArrayList<>();
        dataExporterMenuItems.add(new MenuItem("Basic", "/ui/data/dataexporter/basic"));
        dataExporterMenuItems.add(new MenuItem("Lazy", "/ui/data/dataexporter/lazy"));
        dataExporterMenuItems.add(new MenuItem("Exclude", "/ui/data/dataexporter/excludeColumns"));
        dataExporterMenuItems.add(new MenuItem("Customized", "/ui/data/dataexporter/customizedDocuments"));
        dataExporterMenuItems.add(new MenuItem("TreeTable", "/ui/data/dataexporter/treetable"));
        dataMenuItems.add(new MenuItem("DataExporter", dataExporterMenuItems));

        /*
        //DataGrid Nested MenuItem
        List<MenuItem> dataGridMenuItems = new ArrayList<>();
        dataGridMenuItems.add(new MenuItem("Basic", "/ui/data/datagrid/basic"));
        dataGridMenuItems.add(new MenuItem("MultiViewState", "/ui/data/datagrid/multiViewState"));
        dataGridMenuItems.add(new MenuItem("Responsive", "/ui/data/datagrid/responsive"));
        dataMenuItems.add(new MenuItem("DataGrid", dataGridMenuItems));

        //DataList Nested MenuItem
        List<MenuItem> dataListMenuItems = new ArrayList<>();
        dataListMenuItems.add(new MenuItem("Basic", "/ui/data/datalist/basic"));
        dataListMenuItems.add(new MenuItem("Paginator", "/ui/data/datalist/paginator"));
        dataListMenuItems.add(new MenuItem("MultiViewState", "/ui/data/datalist/multiViewState"));
        dataListMenuItems.add(new MenuItem("Lazy", "/ui/data/datalist/lazy"));
        dataMenuItems.add(new MenuItem("DataList", dataListMenuItems));*/

        //DataScroller Nested MenuItem
        List<MenuItem> dataScrollerMenuItems = new ArrayList<>();
        dataScrollerMenuItems.add(new MenuItem("Basic", "/ui/data/datascroller/basic"));
        dataScrollerMenuItems.add(new MenuItem("Inline", "/ui/data/datascroller/inline"));
        dataScrollerMenuItems.add(new MenuItem("Loader", "/ui/data/datascroller/loader"));
        dataMenuItems.add(new MenuItem("DataScroller", dataScrollerMenuItems));

        //DataTable Nested MenuItem
        List<MenuItem> dataTableMenuItems = new ArrayList<>();
        dataTableMenuItems.add(new MenuItem("Basic", "/ui/data/datatable/basic"));
        dataTableMenuItems.add(new MenuItem("ColumnToggler", "/ui/data/datatable/columnToggler"));
        dataTableMenuItems.add(new MenuItem("ContextMenu", "/ui/data/datatable/contextMenu"));
        dataTableMenuItems.add(new MenuItem("Crud", "/ui/data/datatable/crud"));
        dataTableMenuItems.add(new MenuItem("DisplayPriority", "/ui/data/datatable/displayPriority"));
        dataTableMenuItems.add(new MenuItem("Dynamic Columns", "/ui/data/datatable/columns"));
        dataTableMenuItems.add(new MenuItem("Edit", "/ui/data/datatable/edit"));
        dataTableMenuItems.add(new MenuItem("Facets", "/ui/data/datatable/facets"));
        dataTableMenuItems.add(new MenuItem("Field", "/ui/data/datatable/field"));
        dataTableMenuItems.add(new MenuItem("Filter", "/ui/data/datatable/filter"));
        dataTableMenuItems.add(new MenuItem("Gridlines", "/ui/data/datatable/gridlines", "New"));
        dataTableMenuItems.add(new MenuItem("Group", "/ui/data/datatable/group"));
        dataTableMenuItems.add(new MenuItem("Lazy", "/ui/data/datatable/lazy"));
        dataTableMenuItems.add(new MenuItem("MultiViewState", "/ui/data/datatable/multiViewState"));
        dataTableMenuItems.add(new MenuItem("Paginator", "/ui/data/datatable/paginator"));
        dataTableMenuItems.add(new MenuItem("Reorder", "/ui/data/datatable/reorder"));
        dataTableMenuItems.add(new MenuItem("Resize", "/ui/data/datatable/columnResize"));
        dataTableMenuItems.add(new MenuItem("Responsive", "/ui/data/datatable/responsive"));
        dataTableMenuItems.add(new MenuItem("RowAdd", "/ui/data/datatable/addRow"));
        dataTableMenuItems.add(new MenuItem("RowColor", "/ui/data/datatable/rowColor"));
        dataTableMenuItems.add(new MenuItem("RowExpansion", "/ui/data/datatable/expansion"));
        dataTableMenuItems.add(new MenuItem("RowGroup", "/ui/data/datatable/rowGroup"));
        dataTableMenuItems.add(new MenuItem("RTL", "/ui/data/datatable/rtl"));
        dataTableMenuItems.add(new MenuItem("Scroll", "/ui/data/datatable/scroll"));
        dataTableMenuItems.add(new MenuItem("Selection", "/ui/data/datatable/selection"));
        dataTableMenuItems.add(new MenuItem("Size", "/ui/data/datatable/size", "New"));
        dataTableMenuItems.add(new MenuItem("Sort", "/ui/data/datatable/sort"));
        dataTableMenuItems.add(new MenuItem("StickyHeader", "/ui/data/datatable/sticky"));
        dataTableMenuItems.add(new MenuItem("StripedRows", "/ui/data/datatable/striped", "New"));
        //dataTableMenuItems.add(new MenuItem("SubTable", "/ui/data/datatable/subTable"));
        //dataTableMenuItems.add(new MenuItem("SummaryRow", "/ui/data/datatable/summaryRow"));
        dataMenuItems.add(new MenuItem("DataTable", dataTableMenuItems));

        //DataView Nested MenuItem
        List<MenuItem> dataViewMenuItems = new ArrayList<>();
        dataViewMenuItems.add(new MenuItem("Basic", "/ui/data/dataview/basic"));
        dataViewMenuItems.add(new MenuItem("MultiViewState", "/ui/data/dataview/multiViewState"));
        dataViewMenuItems.add(new MenuItem("Lazy", "/ui/data/dataview/lazy"));
        dataViewMenuItems.add(new MenuItem("Responsive", "/ui/data/dataview/responsive"));
        dataMenuItems.add(new MenuItem("DataView", dataViewMenuItems));

        //Diagram Nested MenuItem
        List<MenuItem> diagramMenuItems = new ArrayList<>();
        diagramMenuItems.add(new MenuItem("Basic", "/ui/data/diagram/basic"));
        diagramMenuItems.add(new MenuItem("Hierarchical", "/ui/data/diagram/hierarchical"));
        diagramMenuItems.add(new MenuItem("FlowChart", "/ui/data/diagram/flowchart"));
        diagramMenuItems.add(new MenuItem("StateMachine", "/ui/data/diagram/statemachine"));
        diagramMenuItems.add(new MenuItem("Editable", "/ui/data/diagram/editable"));
        dataMenuItems.add(new MenuItem("Diagram", diagramMenuItems));

        //Gmap Nested MenuItem
        List<MenuItem> gmapMenuItems = new ArrayList<>();
        gmapMenuItems.add(new MenuItem("Basic", "/ui/data/gmap/basic"));
        gmapMenuItems.add(new MenuItem("Event", "/ui/data/gmap/event"));
        gmapMenuItems.add(new MenuItem("Markers", "/ui/data/gmap/markers"));
        gmapMenuItems.add(new MenuItem("Selection", "/ui/data/gmap/markerSelection"));
        gmapMenuItems.add(new MenuItem("Add Markers", "/ui/data/gmap/addMarkers"));
        gmapMenuItems.add(new MenuItem("Draggable", "/ui/data/gmap/draggableMarkers"));
        gmapMenuItems.add(new MenuItem("InfoWindow", "/ui/data/gmap/infoWindow"));
        gmapMenuItems.add(new MenuItem("Polylines", "/ui/data/gmap/polylines"));
        gmapMenuItems.add(new MenuItem("Polygons", "/ui/data/gmap/polygons"));
        gmapMenuItems.add(new MenuItem("Circles", "/ui/data/gmap/circles"));
        gmapMenuItems.add(new MenuItem("Rectangles", "/ui/data/gmap/rectangles"));
        gmapMenuItems.add(new MenuItem("StreetView", "/ui/data/gmap/street"));
        gmapMenuItems.add(new MenuItem("Controls", "/ui/data/gmap/controls"));
        gmapMenuItems.add(new MenuItem("Geocode", "/ui/data/gmap/geocode"));
        gmapMenuItems.add(new MenuItem("Dialog", "/ui/data/gmap/mapDialog"));
        dataMenuItems.add(new MenuItem("Gmap", gmapMenuItems));

        //HorizontalTree Nested MenuItem
        List<MenuItem> horizontalTreeMenuItems = new ArrayList<>();
        horizontalTreeMenuItems.add(new MenuItem("Basic", "/ui/data/htree/basic"));
        horizontalTreeMenuItems.add(new MenuItem("Icon", "/ui/data/htree/icon"));
        horizontalTreeMenuItems.add(new MenuItem("Selection", "/ui/data/htree/selection"));
        horizontalTreeMenuItems.add(new MenuItem("Events", "/ui/data/htree/events"));
        horizontalTreeMenuItems.add(new MenuItem("ContextMenu", "/ui/data/htree/contextMenu"));
        dataMenuItems.add(new MenuItem("HorizontalTree", horizontalTreeMenuItems));

        dataMenuItems.add(new MenuItem("Mindmap", "/ui/data/mindmap"));
        dataMenuItems.add(new MenuItem("OrderList", "/ui/data/orderList"));
        dataMenuItems.add(new MenuItem("Organigram", "/ui/data/organigram"));
        dataMenuItems.add(new MenuItem("PickList", "/ui/data/pickList"));
        dataMenuItems.add(new MenuItem("Repeat", "/ui/data/repeat"));
        dataMenuItems.add(new MenuItem("Ring", "/ui/data/ring"));

        //Schedule Nested MenuItem
        List<MenuItem> scheduleMenuItems = new ArrayList<>();
        scheduleMenuItems.add(new MenuItem("Basic", "/ui/data/schedule/basic"));
        scheduleMenuItems.add(new MenuItem("Configuration", "/ui/data/schedule/configuration"));
        scheduleMenuItems.add(new MenuItem("Lazy", "/ui/data/schedule/lazy"));
        scheduleMenuItems.add(new MenuItem("Locale IL8N", "/ui/data/schedule/localization"));
        scheduleMenuItems.add(new MenuItem("Extender", "/ui/data/schedule/extender"));
        dataMenuItems.add(new MenuItem("Schedule", scheduleMenuItems));

        dataMenuItems.add(new MenuItem("TagCloud", "/ui/data/tagCloud"));

        //Timeline Nested MenuItem
        List<MenuItem> timelineMenuItems = new ArrayList<>();
        timelineMenuItems.add(new MenuItem("Basic", "/ui/data/timeline/basic"));
        timelineMenuItems.add(new MenuItem("Limit Range", "/ui/data/timeline/limitRange"));
        timelineMenuItems.add(new MenuItem("Custom", "/ui/data/timeline/custom"));
        timelineMenuItems.add(new MenuItem("DragDrop", "/ui/data/timeline/dragdrop"));
        timelineMenuItems.add(new MenuItem("Linked Timelines", "/ui/data/timeline/linked"));
        timelineMenuItems.add(new MenuItem("All Events", "/ui/data/timeline/allEvents"));
        timelineMenuItems.add(new MenuItem("Grouping", "/ui/data/timeline/grouping"));
        timelineMenuItems.add(new MenuItem("Nested Grouping", "/ui/data/timeline/nestedGrouping"));
        timelineMenuItems.add(new MenuItem("Editable Server-Side", "/ui/data/timeline/editServer"));
        timelineMenuItems.add(new MenuItem("Lazy Loading", "/ui/data/timeline/lazy"));
        dataMenuItems.add(new MenuItem("Timeline", timelineMenuItems));

        //Tree Nested MenuItem
        List<MenuItem> treeMenuItems = new ArrayList<>();
        treeMenuItems.add(new MenuItem("Basic", "/ui/data/tree/basic"));
        treeMenuItems.add(new MenuItem("Icon", "/ui/data/tree/icon"));
        treeMenuItems.add(new MenuItem("Selection", "/ui/data/tree/selection"));
        treeMenuItems.add(new MenuItem("Events", "/ui/data/tree/events"));
        treeMenuItems.add(new MenuItem("DragDrop", "/ui/data/tree/dragdrop"));
        treeMenuItems.add(new MenuItem("ContextMenu", "/ui/data/tree/contextMenu"));
        treeMenuItems.add(new MenuItem("Animate", "/ui/data/tree/animate"));
        treeMenuItems.add(new MenuItem("RTL", "/ui/data/tree/rtl"));
        treeMenuItems.add(new MenuItem("Filter", "/ui/data/tree/filter"));
        treeMenuItems.add(new MenuItem("Lazy Loading", "/ui/data/tree/lazyloading"));
        dataMenuItems.add(new MenuItem("Tree", treeMenuItems));

        //TreeTable Nested MenuItem
        List<MenuItem> treeTableMenuItems = new ArrayList<>();
        treeTableMenuItems.add(new MenuItem("Basic", "/ui/data/treetable/basic"));
        treeTableMenuItems.add(new MenuItem("Size", "/ui/data/treetable/size", "New"));
        treeTableMenuItems.add(new MenuItem("Gridlines", "/ui/data/treetable/gridlines", "New"));
        treeTableMenuItems.add(new MenuItem("Selection", "/ui/data/treetable/selection"));
        treeTableMenuItems.add(new MenuItem("Events", "/ui/data/treetable/events"));
        treeTableMenuItems.add(new MenuItem("ContextMenu", "/ui/data/treetable/contextMenu"));
        treeTableMenuItems.add(new MenuItem("Scroll", "/ui/data/treetable/scroll"));
        treeTableMenuItems.add(new MenuItem("Resize", "/ui/data/treetable/resize"));
        treeTableMenuItems.add(new MenuItem("Sort", "/ui/data/treetable/sort"));
        treeTableMenuItems.add(new MenuItem("Filter", "/ui/data/treetable/filter"));
        treeTableMenuItems.add(new MenuItem("Columns", "/ui/data/treetable/columns"));
        treeTableMenuItems.add(new MenuItem("Responsive", "/ui/data/treetable/responsive"));
        treeTableMenuItems.add(new MenuItem("Edit", "/ui/data/treetable/edit"));
        treeTableMenuItems.add(new MenuItem("Paginator", "/ui/data/treetable/paginator"));
        treeTableMenuItems.add(new MenuItem("MultiViewState", "/ui/data/treetable/multiViewState"));
        treeTableMenuItems.add(new MenuItem("DisplayPriority", "/ui/data/treetable/displayPriority"));
        dataMenuItems.add(new MenuItem("TreeTable", treeTableMenuItems));
        menuCategories.add(new MenuCategory("Data", dataMenuItems));
        //DATA CATEGORY END

        //PANEL CATEGORY START
        List<MenuItem> panelMenuItems = new ArrayList<>();
        panelMenuItems.add(new MenuItem("Accordion", "/ui/panel/accordionPanel"));
        panelMenuItems.add(new MenuItem("Card", "/ui/panel/card", "New"));
        panelMenuItems.add(new MenuItem("Dashboard", "/ui/panel/dashboard"));
        panelMenuItems.add(new MenuItem("Divider", "/ui/panel/divider", "New"));
        panelMenuItems.add(new MenuItem("Fieldset", "/ui/panel/fieldset"));
        //panelMenuItems.add(new MenuItem("Grid CSS", "/ui/panel/grid"));
        //panelMenuItems.add(new MenuItem("NotificationBar", "/ui/panel/notificationBar"));
        panelMenuItems.add(new MenuItem("OutputPanel", "/ui/panel/outputPanel"));
        //panelMenuItems.add(new MenuItem("FlexGrid", "/ui/panel/flexGrid"));
        panelMenuItems.add(new MenuItem("Panel", "/ui/panel/panel"));
        panelMenuItems.add(new MenuItem("PanelGrid", "/ui/panel/panelGrid"));
        panelMenuItems.add(new MenuItem("Splitter", "/ui/panel/splitter", "New"));
        panelMenuItems.add(new MenuItem("ScrollPanel", "/ui/panel/scrollPanel"));
        panelMenuItems.add(new MenuItem("TabView", "/ui/panel/tabView"));
        panelMenuItems.add(new MenuItem("Toolbar", "/ui/panel/toolbar"));
        panelMenuItems.add(new MenuItem("Wizard", "/ui/panel/wizard"));
        menuCategories.add(new MenuCategory("Panel", panelMenuItems));
        //PANEL CATEGORY END

        //OVERLAY CATEGORY START
        List<MenuItem> overlayMenuItems = new ArrayList<>();
        overlayMenuItems.add(new MenuItem("ConfirmDialog", "/ui/overlay/confirmDialog"));
        overlayMenuItems.add(new MenuItem("ConfirmPopup", "/ui/overlay/confirmPopup", "New"));
        overlayMenuItems.add(new MenuItem("Dialog", "/ui/overlay/dialog"));
        overlayMenuItems.add(new MenuItem("LightBox", "/ui/overlay/lightBox"));
        overlayMenuItems.add(new MenuItem("OverlayPanel", "/ui/overlay/overlayPanel"));
        overlayMenuItems.add(new MenuItem("Sidebar", "/ui/overlay/sidebar"));

        //Tooltip Nested MenuItem
        List<MenuItem> tooltipMenuItems = new ArrayList<>();
        tooltipMenuItems.add(new MenuItem("Options", "/ui/overlay/tooltip/tooltipOptions"));
        tooltipMenuItems.add(new MenuItem("Global", "/ui/overlay/tooltip/tooltipGlobal"));
        overlayMenuItems.add(new MenuItem("Tooltip", tooltipMenuItems));

        menuCategories.add(new MenuCategory("Overlay", overlayMenuItems));
        //OVERLAY CATEGORY END

        //MENU CATEGORY START
        List<MenuItem> menuMenuItems = new ArrayList<>();
        menuMenuItems.add(new MenuItem("Breadcrumb", "/ui/menu/breadcrumb"));

        //ContextMenu Nested MenuItem
        List<MenuItem> contextMenuItems = new ArrayList<>();
        contextMenuItems.add(new MenuItem("Basic", "/ui/menu/contextmenu/basic"));
        contextMenuItems.add(new MenuItem("Tiered", "/ui/menu/contextmenu/tiered"));
        contextMenuItems.add(new MenuItem("Target", "/ui/menu/contextmenu/target"));
        contextMenuItems.add(new MenuItem("DataTable", "/ui/data/datatable/contextMenu"));
        contextMenuItems.add(new MenuItem("Tree", "/ui/data/tree/contextMenu"));
        contextMenuItems.add(new MenuItem("TreeTable", "/ui/data/treetable/contextMenu"));
        menuMenuItems.add(new MenuItem("ContextMenu", contextMenuItems));

        menuMenuItems.add(new MenuItem("Dock", "/ui/menu/dock"));
        menuMenuItems.add(new MenuItem("MegaMenu", "/ui/menu/megaMenu"));
        menuMenuItems.add(new MenuItem("Menu", "/ui/menu/menu"));
        menuMenuItems.add(new MenuItem("Menubar", "/ui/menu/menubar"));
        menuMenuItems.add(new MenuItem("MenuButton", "/ui/menu/menuButton"));
        menuMenuItems.add(new MenuItem("PanelMenu", "/ui/menu/panelMenu"));
        menuMenuItems.add(new MenuItem("SlideMenu", "/ui/menu/slideMenu"));
        menuMenuItems.add(new MenuItem("Stack", "/ui/menu/stack"));
        menuMenuItems.add(new MenuItem("Steps", "/ui/menu/steps"));
        menuMenuItems.add(new MenuItem("TabMenu", "/ui/menu/tabMenu"));
        menuMenuItems.add(new MenuItem("TieredMenu", "/ui/menu/tieredMenu"));
        menuCategories.add(new MenuCategory("Menu", menuMenuItems));
        //MENU CATEGORY END

        //CHARTS CATEGORY START
        List<MenuItem> chartsMenuItems = new ArrayList<>();

        //Bar Nested MenuItem
        chartsMenuItems.add(new MenuItem("Bar", "/ui/chartjs/bar/bar"));
        chartsMenuItems.add(new MenuItem("Bubble", "/ui/chartjs/bubble"));
        chartsMenuItems.add(new MenuItem("Donut", "/ui/chartjs/donut"));
        chartsMenuItems.add(new MenuItem("Line", "/ui/chartjs/line"));
        chartsMenuItems.add(new MenuItem("Pie", "/ui/chartjs/pie"));
        chartsMenuItems.add(new MenuItem("Scatter", "/ui/chartjs/scatter"));
        chartsMenuItems.add(new MenuItem("PolarArea", "/ui/chartjs/polararea"));
        chartsMenuItems.add(new MenuItem("Radar", "/ui/chartjs/radar"));
        chartsMenuItems.add(new MenuItem("Mixed", "/ui/chartjs/mixed"));
        chartsMenuItems.add(new MenuItem("Interactive", "/ui/chartjs/interactive"));
        chartsMenuItems.add(new MenuItem("Export", "/ui/chartjs/export"));
        menuCategories.add(new MenuCategory("Charts", chartsMenuItems));
        //CHARTS CATEGORY END

        //MESSAGES CATEGORY START
        List<MenuItem> messagesMenuItems = new ArrayList<>();
        messagesMenuItems.add(new MenuItem("Growl", "/ui/message/growl"));
        messagesMenuItems.add(new MenuItem("Messages", "/ui/message/messages"));
        messagesMenuItems.add(new MenuItem("StaticMessage", "/ui/message/staticMessage"));
        menuCategories.add(new MenuCategory("Messages", messagesMenuItems));
        //MESSAGES CATEGORY END

        //MULTIMEDIA CATEGORY START
        List<MenuItem> multimediaMenuItems = new ArrayList<>();
        multimediaMenuItems.add(new MenuItem("Audio", "/ui/multimedia/audio", "New"));
        multimediaMenuItems.add(new MenuItem("Barcode", "/ui/multimedia/barcode"));
        multimediaMenuItems.add(new MenuItem("Compare", "/ui/multimedia/compare"));
        multimediaMenuItems.add(new MenuItem("ContentFlow", "/ui/multimedia/contentFlow"));

        //Cropper Nested MenuItem
        List<MenuItem> cropperMenuItems = new ArrayList<>();
        cropperMenuItems.add(new MenuItem("Basic", "/ui/multimedia/cropper/cropper"));
        cropperMenuItems.add(new MenuItem("Dynamic", "/ui/multimedia/cropper/dynamic"));
        cropperMenuItems.add(new MenuItem("FileUpload", "/ui/multimedia/cropper/fileupload"));
        multimediaMenuItems.add(new MenuItem("Cropper", cropperMenuItems));

        multimediaMenuItems.add(new MenuItem("Graphic Image", "/ui/multimedia/graphicImage"));
        multimediaMenuItems.add(new MenuItem("Galleria", "/ui/multimedia/galleria"));
        multimediaMenuItems.add(new MenuItem("Media", "/ui/multimedia/media"));

        //PhotoCam Nested MenuItem
        List<MenuItem> photoCamMenuItems = new ArrayList<>();
        photoCamMenuItems.add(new MenuItem("PhotoCam", "/ui/multimedia/photocam/photoCam.xhtml"));
        photoCamMenuItems.add(new MenuItem("Device Selection", "/ui/multimedia/photocam/deviceSelection.xhtml"));
        multimediaMenuItems.add(new MenuItem("PhotoCam", photoCamMenuItems));

        multimediaMenuItems.add(new MenuItem("Switch", "/ui/multimedia/switch"));
        multimediaMenuItems.add(new MenuItem("Video", "/ui/multimedia/video", "New"));
        menuCategories.add(new MenuCategory("Multimedia", multimediaMenuItems));
        //MULTIMEDIA CATEGORY END

        //FILE CATEGORY START
        List<MenuItem> fileMenuItems = new ArrayList<>();

        //Upload Nested MenuItem
        List<MenuItem> uploadMenuItems = new ArrayList<>();
        uploadMenuItems.add(new MenuItem("Basic", "/ui/file/upload/basic"));
        uploadMenuItems.add(new MenuItem("Basic Auto", "/ui/file/upload/basicAuto"));
        uploadMenuItems.add(new MenuItem("Single", "/ui/file/upload/single"));
        uploadMenuItems.add(new MenuItem("Multiple", "/ui/file/upload/multiple"));
        uploadMenuItems.add(new MenuItem("Auto", "/ui/file/upload/auto"));
        uploadMenuItems.add(new MenuItem("DragDrop", "/ui/file/upload/dnd"));
        uploadMenuItems.add(new MenuItem("Chunked", "/ui/file/upload/chunked"));
        uploadMenuItems.add(new MenuItem("Tooltips", "/ui/file/upload/tooltips"));
        fileMenuItems.add(new MenuItem("Upload", uploadMenuItems));

        fileMenuItems.add(new MenuItem("Download", "/ui/file/download"));
        menuCategories.add(new MenuCategory("File", fileMenuItems));
        //FILE CATEGORY END

        //DRAGDROP CATEGORY START
        List<MenuItem> dragDropMenuItems = new ArrayList<>();
        dragDropMenuItems.add(new MenuItem("Draggable", "/ui/dnd/draggable"));
        dragDropMenuItems.add(new MenuItem("DataView", "/ui/dnd/dataView"));
        dragDropMenuItems.add(new MenuItem("DataTable", "/ui/dnd/dataTable"));
        dragDropMenuItems.add(new MenuItem("Custom", "/ui/dnd/custom"));
        menuCategories.add(new MenuCategory("DragDrop", dragDropMenuItems));
        //DRAGDROP CATEGORY END

        //CLIENT SIDE VALIDATION CATEGORY START
        List<MenuItem> clientSideValidationMenuItems = new ArrayList<>();
        clientSideValidationMenuItems.add(new MenuItem("Basic", "/ui/csv/basic"));
        clientSideValidationMenuItems.add(new MenuItem("Bean", "/ui/csv/bean"));
        clientSideValidationMenuItems.add(new MenuItem("Custom", "/ui/csv/custom"));
        clientSideValidationMenuItems.add(new MenuItem("Event", "/ui/csv/event"));
        menuCategories.add(new MenuCategory("Client Side Validation", clientSideValidationMenuItems));
        //CLIENT SIDE VALIDATION CATEGORY END

        //DIALOG FRAMEWORK CATEGORY START
        List<MenuItem> dialogFrameworkMenuItems = new ArrayList<>();
        dialogFrameworkMenuItems.add(new MenuItem("Basic", "/ui/df/basic"));
        dialogFrameworkMenuItems.add(new MenuItem("Data", "/ui/df/data"));
        dialogFrameworkMenuItems.add(new MenuItem("Message", "/ui/df/message"));
        dialogFrameworkMenuItems.add(new MenuItem("Nested", "/ui/df/nested"));
        menuCategories.add(new MenuCategory("Dialog Framework", dialogFrameworkMenuItems));
        //DIALOG FRAMEWORK CATEGORY END

        //MISC CATEGORY START
        List<MenuItem> miscMenuItems = new ArrayList<>();
        miscMenuItems.add(new MenuItem("Avatar", "/ui/misc/avatar", "New"));
        miscMenuItems.add(new MenuItem("Badge", "/ui/misc/badge", "New"));
        miscMenuItems.add(new MenuItem("Chip", "/ui/misc/chip", "New"));
        miscMenuItems.add(new MenuItem("ScrollTop", "/ui/misc/scrollTop", "New"));
        miscMenuItems.add(new MenuItem("Skeleton", "/ui/misc/skeleton", "New"));
        miscMenuItems.add(new MenuItem("Tag", "/ui/misc/tag", "New"));
        //miscMenuItems.add(new MenuItem("Responsive", "/ui/misc/responsive"));
        miscMenuItems.add(new MenuItem("AutoUpdate", "/ui/misc/autoUpdate"));
        miscMenuItems.add(new MenuItem("OutputLabel", "/ui/misc/outputLabel"));
        miscMenuItems.add(new MenuItem("BlockUI", "/ui/misc/blockUI"));
        miscMenuItems.add(new MenuItem("Cache", "/ui/misc/cache"));
        miscMenuItems.add(new MenuItem("Captcha", "/ui/misc/captcha"));
        miscMenuItems.add(new MenuItem("Clock", "/ui/misc/clock"));
        miscMenuItems.add(new MenuItem("Context", "/ui/misc/context"));

        //DefaultCommand Nested MenuItem
        List<MenuItem> defaultCommandMenuItems = new ArrayList<>();
        defaultCommandMenuItems.add(new MenuItem("Basic", "/ui/misc/defaultcommand/basic"));
        defaultCommandMenuItems.add(new MenuItem("Multiple", "/ui/misc/defaultcommand/multiple"));
        miscMenuItems.add(new MenuItem("DefaultCommand", defaultCommandMenuItems));

        miscMenuItems.add(new MenuItem("Effect", "/ui/misc/effect"));
        miscMenuItems.add(new MenuItem("ExceptionHandler", "/ui/misc/exceptionHandler"));
        miscMenuItems.add(new MenuItem("FeedReader", "/ui/misc/feedReader"));
        miscMenuItems.add(new MenuItem("IdleMonitor", "/ui/misc/idleMonitor"));
        miscMenuItems.add(new MenuItem("ImportConstants", "/ui/misc/importConstants"));
        miscMenuItems.add(new MenuItem("ImportEnum", "/ui/misc/importEnum"));
        //miscMenuItems.add(new MenuItem("CSP", "/ui/misc/csp"));
        //miscMenuItems.add(new MenuItem("FontAwesome", "/ui/misc/fa"));
        miscMenuItems.add(new MenuItem("Lifecycle", "/ui/misc/lifecycle"));
        miscMenuItems.add(new MenuItem("Log", "/ui/misc/log"));
        miscMenuItems.add(new MenuItem("Focus", "/ui/misc/focus"));
        miscMenuItems.add(new MenuItem("Hotkey", "/ui/misc/hotkey"));
        miscMenuItems.add(new MenuItem("Printer", "/ui/misc/printer"));
        miscMenuItems.add(new MenuItem("ProgressBar", "/ui/misc/progressBar"));
        miscMenuItems.add(new MenuItem("ResetInput", "/ui/misc/resetInput"));
        miscMenuItems.add(new MenuItem("Resizable", "/ui/misc/resizable"));
        //miscMenuItems.add(new MenuItem("Separator", "/ui/misc/separator"));
        //miscMenuItems.add(new MenuItem("Spacer", "/ui/misc/spacer"));
        miscMenuItems.add(new MenuItem("Spotlight", "/ui/misc/spotlight"));
        miscMenuItems.add(new MenuItem("Sticky", "/ui/misc/sticky"));

        //Terminal Nested MenuItem
        List<MenuItem> terminalCommandMenuItems = new ArrayList<>();
        terminalCommandMenuItems.add(new MenuItem("Basic", "/ui/misc/terminal/basic"));
        terminalCommandMenuItems.add(new MenuItem("Autocomplete", "/ui/misc/terminal/autocomplete"));
        miscMenuItems.add(new MenuItem("Terminal", terminalCommandMenuItems));

        miscMenuItems.add(new MenuItem("Watermark", "/ui/misc/watermark"));
        menuCategories.add(new MenuCategory("Misc", miscMenuItems));
        //MISC CATEGORY END

        for (MenuCategory category: menuCategories) {
            for (MenuItem menuItem: category.getMenuItems()) {
                menuItem.setParentLabel(category.getLabel());
                if (menuItem.getUrl() != null) {
                    menuItems.add(menuItem);
                }
                if (menuItem.getMenuItems() != null) {
                    for (MenuItem item: menuItem.getMenuItems()) {
                        item.setParentLabel(menuItem.getLabel());
                        if (item.getUrl() != null) {
                            menuItems.add(item);
                        }
                    }
                }
            }
        }
    }

    public List<MenuItem> completeMenuItem(String query) {
        String queryLowerCase = query.toLowerCase();
        List<MenuItem> filteredItems = new ArrayList<>();
        for (MenuItem item: menuItems) {
            if (item.getUrl() != null && (item.getLabel().toLowerCase().contains(queryLowerCase) || item.getParentLabel().toLowerCase().contains(queryLowerCase))) {
                filteredItems.add(item);
            }
            if (item.getBadge() != null) {
                if (item.getBadge().toLowerCase().contains(queryLowerCase)){
                    filteredItems.add(item);
                }
            }
        }
        filteredItems.sort(Comparator.comparing(MenuItem::getParentLabel));
        return filteredItems;
    }

    public List<MenuItem> getMenuItems() {
        return menuItems;
    }

    public List<MenuCategory> getMenuCategories() {
        return menuCategories;
    }
}
