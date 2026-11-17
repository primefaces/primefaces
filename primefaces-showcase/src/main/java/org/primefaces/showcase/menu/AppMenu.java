/*
 * The MIT License
 *
 * Copyright (c) 2009-2026 PrimeFaces
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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;

@Named
@ApplicationScoped
public class AppMenu {

    private List<MenuCategory> menuCategories;

    // CHECKSTYLE:OFF
    @PostConstruct
    public void init() {
        menuCategories = new ArrayList<>();

        //GENERAL CATEGORY START
        List<MenuItem> generalMenuItems = new ArrayList<>();
        generalMenuItems.add(new MenuItem("Why PrimeFaces", "/whyprimefaces", "New"));
        generalMenuItems.add(new MenuItem("Get Started", "/getstarted"));
        generalMenuItems.add(new MenuItem("TagLib", "/taglib"));
        generalMenuItems.add(new MenuItem("Documentation", "https://primefaces.github.io/primefaces/16_0_0/#/"));
        generalMenuItems.add(new MenuItem("Support", "/support"));
        menuCategories.add(new MenuCategory("General", generalMenuItems));
        //GENERAL CATEGORY END

        //CORE CATEGORY START
        List<MenuItem> ajaxMenuItems = new ArrayList<>();
        ajaxMenuItems.add(new MenuItem("Introduction", "/ajax/basic"));
        ajaxMenuItems.add(new MenuItem("Process", "/ajax/process"));
        ajaxMenuItems.add(new MenuItem("PartialSubmit", "/ajax/partialSubmit"));
        ajaxMenuItems.add(new MenuItem("Selector", "/ajax/selector"));
        ajaxMenuItems.add(new MenuItem("Search", "/ajax/search"));
        ajaxMenuItems.add(new MenuItem("Validation", "/ajax/validation"));
        ajaxMenuItems.add(new MenuItem("RemoteCommand", "/ajax/remoteCommand"));
        ajaxMenuItems.add(new MenuItem("Observer", "/ajax/observer"));
        ajaxMenuItems.add(new MenuItem("Callback", "/ajax/callback", "New"));
        ajaxMenuItems.add(new MenuItem("Poll", "/ajax/poll"));
        ajaxMenuItems.add(new MenuItem("Fragment", "/ajax/fragment"));
        ajaxMenuItems.add(new MenuItem("Status", "/ajax/status"));
        ajaxMenuItems.add(new MenuItem("Lifecycle", "/ajax/lifecycle"));
        ajaxMenuItems.add(new MenuItem("Dropdown", "/ajax/dropdown"));

        List<MenuItem> clientSideValidationMenuItems = new ArrayList<>();
        clientSideValidationMenuItems.add(new MenuItem("Basic", "/csv/basic"));
        clientSideValidationMenuItems.add(new MenuItem("Bean", "/csv/bean"));
        clientSideValidationMenuItems.add(new MenuItem("Custom", "/csv/custom"));
        clientSideValidationMenuItems.add(new MenuItem("Complex", "/csv/complex"));
        clientSideValidationMenuItems.add(new MenuItem("Event", "/csv/event"));
        clientSideValidationMenuItems.add(new MenuItem("Immediate", "/csv/immediate"));

        List<MenuItem> dialogFrameworkMenuItems = new ArrayList<>();
        dialogFrameworkMenuItems.add(new MenuItem("Basic", "/df/basic"));
        dialogFrameworkMenuItems.add(new MenuItem("Data", "/df/data"));
        dialogFrameworkMenuItems.add(new MenuItem("Message", "/df/message"));
        dialogFrameworkMenuItems.add(new MenuItem("Nested", "/df/nested"));

        List<MenuItem> coreMenuItems = new ArrayList<>();
        coreMenuItems.add(new MenuItem("AJAX", ajaxMenuItems));
        coreMenuItems.add(new MenuItem("Client Side Validation", clientSideValidationMenuItems));
        coreMenuItems.add(new MenuItem("Dialog Framework", dialogFrameworkMenuItems));
        coreMenuItems.add(new MenuItem("Content Security", "https://primefaces.github.io/primefaces/16_0_0/#/core/contentsecuritypolicy"));
        coreMenuItems.add(new MenuItem("Accessibility", "/accessibility"));
        coreMenuItems.add(new MenuItem("PrimeFlex", "/primeflex/setup"));
        coreMenuItems.add(new MenuItem("PrimeIcons", "/icons"));
        coreMenuItems.add(new MenuItem("Theming", "/theming", "New"));
        coreMenuItems.add(new MenuItem("Theming Tokens", "/theming/tokens", "New"));
        coreMenuItems.add(new MenuItem("Theme Configurator", "/theming/configurator", "New"));
        menuCategories.add(new MenuCategory("Core", coreMenuItems));
        //CORE FRAMEWORK CATEGORY END

        //FORM CATEGORY START
        List<MenuItem> formMenuItems = new ArrayList<>();
        formMenuItems.add(new MenuItem("AutoComplete", "/input/autoComplete"));
        formMenuItems.add(new MenuItem("CascadeSelect", "/input/cascadeSelect"));
        formMenuItems.add(new MenuItem("Chips", "/input/chips"));

        List<MenuItem> colorPickerMenuItems = new ArrayList<>();
        colorPickerMenuItems.add(new MenuItem("Popup", "/input/colorpicker/colorPicker"));
        colorPickerMenuItems.add(new MenuItem("Inline", "/input/colorpicker/colorPickerInline"));
        formMenuItems.add(new MenuItem("ColorPicker", colorPickerMenuItems));

        //DatePicker Nested MenuItem
        List<MenuItem> datePickerMenuItems = new ArrayList<>();
        datePickerMenuItems.add(new MenuItem("java.util.Date", "/input/datepicker/datePicker"));
        datePickerMenuItems.add(new MenuItem("Java 8+ Date APIs", "/input/datepicker/datePickerJava8"));
        datePickerMenuItems.add(new MenuItem("Metadata", "/input/datepicker/metadata"));
        formMenuItems.add(new MenuItem("DatePicker", datePickerMenuItems));

        formMenuItems.add(new MenuItem("Inplace", "/input/inplace"));
        formMenuItems.add(new MenuItem("InputGroup", "/input/inputGroup"));
        formMenuItems.add(new MenuItem("InputMask", "/input/inputMask"));
        formMenuItems.add(new MenuItem("InputNumber", "/input/inputNumber"));
        formMenuItems.add(new MenuItem("InputOtp", "/input/inputOtp", "New"));
        formMenuItems.add(new MenuItem("InputPhone", "/input/inputPhone", "New"));
        formMenuItems.add(new MenuItem("InputText", "/input/inputText"));
        formMenuItems.add(new MenuItem("InputTextArea", "/input/inputTextarea"));
        formMenuItems.add(new MenuItem("KeyFilter", "/input/keyFilter"));
        formMenuItems.add(new MenuItem("Keyboard", "/input/keyboard"));
        formMenuItems.add(new MenuItem("Knob", "/input/knob"));
        formMenuItems.add(new MenuItem("MultiSelectListBox", "/input/multiSelectListbox"));
        formMenuItems.add(new MenuItem("Password", "/input/password"));
        formMenuItems.add(new MenuItem("Rating", "/input/rating"));
        formMenuItems.add(new MenuItem("SelectBooleanButton", "/input/booleanButton"));
        formMenuItems.add(new MenuItem("SelectBooleanCheckbox", "/input/booleanCheckbox"));
        formMenuItems.add(new MenuItem("SelectOneButton", "/input/oneButton"));
        formMenuItems.add(new MenuItem("SelectOneRadio", "/input/oneRadio"));
        formMenuItems.add(new MenuItem("SelectCheckboxMenu", "/input/checkboxMenu"));
        formMenuItems.add(new MenuItem("SelectOneMenu", "/input/oneMenu"));
        formMenuItems.add(new MenuItem("SelectOneListbox", "/input/listbox"));
        formMenuItems.add(new MenuItem("SelectManyButton", "/input/manyButton"));
        formMenuItems.add(new MenuItem("SelectManyMenu", "/input/manyMenu"));
        formMenuItems.add(new MenuItem("SelectManyCheckbox", "/input/manyCheckbox"));
        formMenuItems.add(new MenuItem("Signature", "/input/signature"));
        formMenuItems.add(new MenuItem("Slider", "/input/slider"));
        formMenuItems.add(new MenuItem("Spinner", "/input/spinner"));
        formMenuItems.add(new MenuItem("TextEditor", "/input/textEditor"));
        formMenuItems.add(new MenuItem("ToggleSwitch", "/input/toggleSwitch"));
        formMenuItems.add(new MenuItem("TriStateCheckbox", "/input/triStateCheckbox"));
        formMenuItems.add(new MenuItem("TriStateManyCheckbox", "/input/triStateManyCheckbox", "New"));
        menuCategories.add(new MenuCategory("Form", formMenuItems));
        //FORM CATEGORY END

        //BUTTON CATEGORY START
        List<MenuItem> buttonMenuItems = new ArrayList<>();
        buttonMenuItems.add(new MenuItem("Button", "/button/button"));
        buttonMenuItems.add(new MenuItem("CommandButton", "/button/commandButton"));
        buttonMenuItems.add(new MenuItem("CommandLink", "/button/commandLink"));
        buttonMenuItems.add(new MenuItem("Link", "/button/link"));
        buttonMenuItems.add(new MenuItem("LinkButton", "/button/linkButton"));
        buttonMenuItems.add(new MenuItem("SplitButton", "/button/splitButton"));
        buttonMenuItems.add(new MenuItem("Speed Dial", "/button/speedDial"));
        menuCategories.add(new MenuCategory("Button", buttonMenuItems));
        //BUTTON CATEGORY END

        //DATA CATEGORY START
        List<MenuItem> dataMenuItems = new ArrayList<>();
        dataMenuItems.add(new MenuItem("Carousel", "/data/carousel"));
        dataMenuItems.add(new MenuItem("Chronoline", "/data/chronoline"));

        //DataExporter Nested MenuItem
        List<MenuItem> dataExporterMenuItems = new ArrayList<>();
        dataExporterMenuItems.add(new MenuItem("Basic", "/data/dataexporter/basic"));
        dataExporterMenuItems.add(new MenuItem("Lazy", "/data/dataexporter/lazy"));
        dataExporterMenuItems.add(new MenuItem("Exclude", "/data/dataexporter/excludeColumns"));
        dataExporterMenuItems.add(new MenuItem("Customized", "/data/dataexporter/customizedDocuments"));
        dataExporterMenuItems.add(new MenuItem("TreeTable", "/data/dataexporter/treetable"));
        dataMenuItems.add(new MenuItem("DataExporter", dataExporterMenuItems));

        //DataScroller Nested MenuItem
        List<MenuItem> dataScrollerMenuItems = new ArrayList<>();
        dataScrollerMenuItems.add(new MenuItem("Basic", "/data/datascroller/basic"));
        dataScrollerMenuItems.add(new MenuItem("Inline", "/data/datascroller/inline"));
        dataScrollerMenuItems.add(new MenuItem("Loader", "/data/datascroller/loader"));
        dataScrollerMenuItems.add(new MenuItem("Loading", "/data/datascroller/loading"));
        dataMenuItems.add(new MenuItem("DataScroller", dataScrollerMenuItems));

        //DataTable Nested MenuItem
        List<MenuItem> dataTableMenuItems = new ArrayList<>();
        dataTableMenuItems.add(new MenuItem("Basic", "/data/datatable/basic"));
        dataTableMenuItems.add(new MenuItem("ColumnToggler", "/data/datatable/columnToggler"));
        dataTableMenuItems.add(new MenuItem("ContextMenu", "/data/datatable/contextMenu"));
        dataTableMenuItems.add(new MenuItem("Crud", "/data/datatable/crud"));
        dataTableMenuItems.add(new MenuItem("DisplayPriority", "/data/datatable/displayPriority"));
        dataTableMenuItems.add(new MenuItem("Dynamic Columns", "/data/datatable/columns"));
        dataTableMenuItems.add(new MenuItem("Edit", "/data/datatable/edit"));
        dataTableMenuItems.add(new MenuItem("Field", "/data/datatable/field"));
        dataTableMenuItems.add(new MenuItem("Filter", "/data/datatable/filter"));
        dataTableMenuItems.add(new MenuItem("Group", "/data/datatable/group"));
        dataTableMenuItems.add(new MenuItem("Lazy", "/data/datatable/lazy"));
        dataTableMenuItems.add(new MenuItem("MultiViewState", "/data/datatable/multiViewState"));
        dataTableMenuItems.add(new MenuItem("Paginator", "/data/datatable/paginator"));
        dataTableMenuItems.add(new MenuItem("Reorder", "/data/datatable/reorder"));
        dataTableMenuItems.add(new MenuItem("Resize", "/data/datatable/columnResize"));
        dataTableMenuItems.add(new MenuItem("Responsive", "/data/datatable/responsive"));
        dataTableMenuItems.add(new MenuItem("RowExpansion", "/data/datatable/expansion"));
        dataTableMenuItems.add(new MenuItem("RowGroup", "/data/datatable/rowGroup"));
        dataTableMenuItems.add(new MenuItem("RowHeader", "/data/datatable/rowHeader"));
        dataTableMenuItems.add(new MenuItem("RowStyle", "/data/datatable/rowStyle"));
        dataTableMenuItems.add(new MenuItem("Scroll", "/data/datatable/scroll"));
        dataTableMenuItems.add(new MenuItem("Selection", "/data/datatable/selection"));
        dataTableMenuItems.add(new MenuItem("Sort", "/data/datatable/sort"));
        dataTableMenuItems.add(new MenuItem("Style", "/data/datatable/style"));
        dataTableMenuItems.add(new MenuItem("SubTable", "/data/datatable/subtable"));
        //dataTableMenuItems.add(new MenuItem("SummaryRow", "/data/datatable/summaryRow"));
        dataMenuItems.add(new MenuItem("DataTable", dataTableMenuItems));

        //DataView Nested MenuItem
        List<MenuItem> dataViewMenuItems = new ArrayList<>();
        dataViewMenuItems.add(new MenuItem("Basic", "/data/dataview/basic"));
        dataViewMenuItems.add(new MenuItem("MultiViewState", "/data/dataview/multiViewState"));
        dataViewMenuItems.add(new MenuItem("Lazy", "/data/dataview/lazy"));
        dataViewMenuItems.add(new MenuItem("Responsive", "/data/dataview/responsive"));
        dataMenuItems.add(new MenuItem("DataView", dataViewMenuItems));

        //Diagram Nested MenuItem
        List<MenuItem> diagramMenuItems = new ArrayList<>();
        diagramMenuItems.add(new MenuItem("Basic", "/data/diagram/basic"));
        diagramMenuItems.add(new MenuItem("Hierarchical", "/data/diagram/hierarchical"));
        diagramMenuItems.add(new MenuItem("FlowChart", "/data/diagram/flowchart"));
        diagramMenuItems.add(new MenuItem("StateMachine", "/data/diagram/statemachine"));
        diagramMenuItems.add(new MenuItem("Editable", "/data/diagram/editable"));
        dataMenuItems.add(new MenuItem("Diagram", diagramMenuItems));

        //Gmap Nested MenuItem
        List<MenuItem> gmapMenuItems = new ArrayList<>();
        gmapMenuItems.add(new MenuItem("Basic", "/data/gmap/basic"));
        gmapMenuItems.add(new MenuItem("Event", "/data/gmap/event"));
        gmapMenuItems.add(new MenuItem("Markers", "/data/gmap/markers"));
        gmapMenuItems.add(new MenuItem("Selection", "/data/gmap/markerSelection"));
        gmapMenuItems.add(new MenuItem("Add Markers", "/data/gmap/addMarkers"));
        gmapMenuItems.add(new MenuItem("Draggable", "/data/gmap/draggableMarkers"));
        gmapMenuItems.add(new MenuItem("InfoWindow", "/data/gmap/infoWindow"));
        gmapMenuItems.add(new MenuItem("Polylines", "/data/gmap/polylines"));
        gmapMenuItems.add(new MenuItem("Polygons", "/data/gmap/polygons"));
        gmapMenuItems.add(new MenuItem("Circles", "/data/gmap/circles"));
        gmapMenuItems.add(new MenuItem("Rectangles", "/data/gmap/rectangles"));
        gmapMenuItems.add(new MenuItem("StreetView", "/data/gmap/street"));
        gmapMenuItems.add(new MenuItem("Controls", "/data/gmap/controls"));
        gmapMenuItems.add(new MenuItem("Geocode", "/data/gmap/geocode"));
        gmapMenuItems.add(new MenuItem("Dialog", "/data/gmap/mapDialog"));
        dataMenuItems.add(new MenuItem("Gmap", gmapMenuItems));

        //HorizontalTree Nested MenuItem
        List<MenuItem> horizontalTreeMenuItems = new ArrayList<>();
        horizontalTreeMenuItems.add(new MenuItem("Basic", "/data/htree/basic"));
        horizontalTreeMenuItems.add(new MenuItem("Icon", "/data/htree/icon"));
        horizontalTreeMenuItems.add(new MenuItem("Selection", "/data/htree/selection"));
        horizontalTreeMenuItems.add(new MenuItem("Events", "/data/htree/events"));
        horizontalTreeMenuItems.add(new MenuItem("ContextMenu", "/data/htree/contextMenu"));
        dataMenuItems.add(new MenuItem("HorizontalTree", horizontalTreeMenuItems));

        dataMenuItems.add(new MenuItem("Mindmap", "/data/mindmap"));
        dataMenuItems.add(new MenuItem("OrderList", "/data/orderList"));
        dataMenuItems.add(new MenuItem("Organigram", "/data/organigram"));
        dataMenuItems.add(new MenuItem("PickList", "/data/pickList"));

        //Schedule Nested MenuItem
        List<MenuItem> scheduleMenuItems = new ArrayList<>();
        scheduleMenuItems.add(new MenuItem("Basic", "/data/schedule/basic"));
        scheduleMenuItems.add(new MenuItem("Configuration", "/data/schedule/configuration"));
        scheduleMenuItems.add(new MenuItem("Lazy", "/data/schedule/lazy"));
        scheduleMenuItems.add(new MenuItem("Locale I18N", "/data/schedule/localization"));
        scheduleMenuItems.add(new MenuItem("Extender", "/data/schedule/extender"));
        dataMenuItems.add(new MenuItem("Schedule", scheduleMenuItems));

        dataMenuItems.add(new MenuItem("TagCloud", "/data/tagCloud"));

        //Timeline Nested MenuItem
        List<MenuItem> timelineMenuItems = new ArrayList<>();
        timelineMenuItems.add(new MenuItem("Basic", "/data/timeline/basic"));
        timelineMenuItems.add(new MenuItem("Limit Range", "/data/timeline/limitRange"));
        timelineMenuItems.add(new MenuItem("Custom", "/data/timeline/custom"));
        timelineMenuItems.add(new MenuItem("DragDrop", "/data/timeline/dragdrop"));
        timelineMenuItems.add(new MenuItem("Linked Timelines", "/data/timeline/linked"));
        timelineMenuItems.add(new MenuItem("All Events", "/data/timeline/allEvents"));
        timelineMenuItems.add(new MenuItem("Grouping", "/data/timeline/grouping"));
        timelineMenuItems.add(new MenuItem("Nested Grouping", "/data/timeline/nestedGrouping"));
        timelineMenuItems.add(new MenuItem("Editable Server-Side", "/data/timeline/editServer"));
        timelineMenuItems.add(new MenuItem("Lazy Loading", "/data/timeline/lazy"));
        dataMenuItems.add(new MenuItem("Timeline", timelineMenuItems));

        //Tree Nested MenuItem
        List<MenuItem> treeMenuItems = new ArrayList<>();
        treeMenuItems.add(new MenuItem("Basic", "/data/tree/basic"));
        treeMenuItems.add(new MenuItem("Icon", "/data/tree/icon"));
        treeMenuItems.add(new MenuItem("Selection", "/data/tree/selection"));
        treeMenuItems.add(new MenuItem("Events", "/data/tree/events"));
        treeMenuItems.add(new MenuItem("DragDrop", "/data/tree/dragdrop"));
        treeMenuItems.add(new MenuItem("ContextMenu", "/data/tree/contextMenu"));
        treeMenuItems.add(new MenuItem("Animate", "/data/tree/animate"));
        treeMenuItems.add(new MenuItem("Filter", "/data/tree/filter"));
        treeMenuItems.add(new MenuItem("Lazy Loading", "/data/tree/lazyloading"));
        dataMenuItems.add(new MenuItem("Tree", treeMenuItems));

        //TreeTable Nested MenuItem
        List<MenuItem> treeTableMenuItems = new ArrayList<>();
        treeTableMenuItems.add(new MenuItem("Basic", "/data/treetable/basic"));
        treeTableMenuItems.add(new MenuItem("Style", "/data/treetable/style"));
        treeTableMenuItems.add(new MenuItem("Selection", "/data/treetable/selection"));
        treeTableMenuItems.add(new MenuItem("Events", "/data/treetable/events"));
        treeTableMenuItems.add(new MenuItem("Field", "/data/treetable/field"));
        treeTableMenuItems.add(new MenuItem("ContextMenu", "/data/treetable/contextMenu"));
        treeTableMenuItems.add(new MenuItem("Scroll", "/data/treetable/scroll"));
        treeTableMenuItems.add(new MenuItem("Resize", "/data/treetable/resize"));
        treeTableMenuItems.add(new MenuItem("Sort", "/data/treetable/sort"));
        treeTableMenuItems.add(new MenuItem("Filter", "/data/treetable/filter"));
        treeTableMenuItems.add(new MenuItem("Columns", "/data/treetable/columns"));
        treeTableMenuItems.add(new MenuItem("Responsive", "/data/treetable/responsive"));
        treeTableMenuItems.add(new MenuItem("Edit", "/data/treetable/edit"));
        treeTableMenuItems.add(new MenuItem("Paginator", "/data/treetable/paginator"));
        treeTableMenuItems.add(new MenuItem("MultiViewState", "/data/treetable/multiViewState"));
        treeTableMenuItems.add(new MenuItem("DisplayPriority", "/data/treetable/displayPriority"));
        dataMenuItems.add(new MenuItem("TreeTable", treeTableMenuItems));
        menuCategories.add(new MenuCategory("Data", dataMenuItems));
        //DATA CATEGORY END

        //PANEL CATEGORY START
        List<MenuItem> panelMenuItems = new ArrayList<>();
        panelMenuItems.add(new MenuItem("Accordion", "/panel/accordionPanel"));
        panelMenuItems.add(new MenuItem("Card", "/panel/card"));
        panelMenuItems.add(new MenuItem("Dashboard", "/panel/dashboard"));
        panelMenuItems.add(new MenuItem("Divider", "/panel/divider"));
        panelMenuItems.add(new MenuItem("Fieldset", "/panel/fieldset"));
        //panelMenuItems.add(new MenuItem("Grid CSS", "/panel/grid"));
        panelMenuItems.add(new MenuItem("NotificationBar", "/panel/notificationBar"));
        panelMenuItems.add(new MenuItem("OutputPanel", "/panel/outputPanel"));
        //panelMenuItems.add(new MenuItem("FlexGrid", "/panel/flexGrid"));
        panelMenuItems.add(new MenuItem("Panel", "/panel/panel"));
        panelMenuItems.add(new MenuItem("PanelGrid", "/panel/panelGrid"));
        panelMenuItems.add(new MenuItem("Splitter", "/panel/splitter"));
        panelMenuItems.add(new MenuItem("ScrollPanel", "/panel/scrollPanel"));
        panelMenuItems.add(new MenuItem("TabView", "/panel/tabView"));
        panelMenuItems.add(new MenuItem("Toolbar", "/panel/toolbar"));
        panelMenuItems.add(new MenuItem("Wizard", "/panel/wizard"));
        menuCategories.add(new MenuCategory("Panel", panelMenuItems));
        //PANEL CATEGORY END

        //OVERLAY CATEGORY START
        List<MenuItem> overlayMenuItems = new ArrayList<>();
        overlayMenuItems.add(new MenuItem("ConfirmDialog", "/overlay/confirmDialog"));
        overlayMenuItems.add(new MenuItem("ConfirmPopup", "/overlay/confirmPopup"));
        overlayMenuItems.add(new MenuItem("Dialog", "/overlay/dialog"));
        overlayMenuItems.add(new MenuItem("OverlayPanel", "/overlay/overlayPanel"));
        overlayMenuItems.add(new MenuItem("Sidebar", "/overlay/sidebar"));

        //Tooltip Nested MenuItem
        List<MenuItem> tooltipMenuItems = new ArrayList<>();
        tooltipMenuItems.add(new MenuItem("Options", "/overlay/tooltip/tooltipOptions"));
        tooltipMenuItems.add(new MenuItem("Global", "/overlay/tooltip/tooltipGlobal"));
        overlayMenuItems.add(new MenuItem("Tooltip", tooltipMenuItems));

        menuCategories.add(new MenuCategory("Overlay", overlayMenuItems));
        //OVERLAY CATEGORY END

        //MENU CATEGORY START
        List<MenuItem> menuMenuItems = new ArrayList<>();
        menuMenuItems.add(new MenuItem("Breadcrumb", "/menu/breadcrumb"));

        //ContextMenu Nested MenuItem
        List<MenuItem> contextMenuItems = new ArrayList<>();
        contextMenuItems.add(new MenuItem("Basic", "/menu/contextmenu/basic"));
        contextMenuItems.add(new MenuItem("Tiered", "/menu/contextmenu/tiered"));
        contextMenuItems.add(new MenuItem("Target", "/menu/contextmenu/target"));
        contextMenuItems.add(new MenuItem("DataTable", "/data/datatable/contextMenu"));
        contextMenuItems.add(new MenuItem("Tree", "/data/tree/contextMenu"));
        contextMenuItems.add(new MenuItem("TreeTable", "/data/treetable/contextMenu"));
        menuMenuItems.add(new MenuItem("ContextMenu", contextMenuItems));

        menuMenuItems.add(new MenuItem("Dock", "/menu/dock"));
        menuMenuItems.add(new MenuItem("MegaMenu", "/menu/megaMenu"));
        menuMenuItems.add(new MenuItem("Menu", "/menu/menu"));
        menuMenuItems.add(new MenuItem("Menubar", "/menu/menubar"));
        menuMenuItems.add(new MenuItem("MenuButton", "/menu/menuButton"));
        menuMenuItems.add(new MenuItem("PanelMenu", "/menu/panelMenu"));
        menuMenuItems.add(new MenuItem("SlideMenu", "/menu/slideMenu"));
        menuMenuItems.add(new MenuItem("Stack", "/menu/stack"));
        menuMenuItems.add(new MenuItem("Steps", "/menu/steps"));
        menuMenuItems.add(new MenuItem("TabMenu", "/menu/tabMenu"));
        menuMenuItems.add(new MenuItem("TieredMenu", "/menu/tieredMenu"));
        menuCategories.add(new MenuCategory("Menu", menuMenuItems));
        //MENU CATEGORY END

        //CHART CATEGORY START
        List<MenuItem> chartMenuItems = new ArrayList<>();

        //Bar Nested MenuItem
        chartMenuItems.add(new MenuItem("Bar", "/chart/bar"));
        chartMenuItems.add(new MenuItem("Bubble", "/chart/bubble"));
        chartMenuItems.add(new MenuItem("Doughnut", "/chart/doughnut"));
        chartMenuItems.add(new MenuItem("Line", "/chart/line"));
        chartMenuItems.add(new MenuItem("Mixed", "/chart/mixed"));
        chartMenuItems.add(new MenuItem("Pie", "/chart/pie"));
        chartMenuItems.add(new MenuItem("Scatter", "/chart/scatter"));
        chartMenuItems.add(new MenuItem("PolarArea", "/chart/polararea"));
        chartMenuItems.add(new MenuItem("Radar", "/chart/radar"));
        chartMenuItems.add(new MenuItem("Custom", "/chart/custom"));
        chartMenuItems.add(new MenuItem("Facet", "/chart/facet"));
        chartMenuItems.add(new MenuItem("Export", "/chart/export"));
        menuCategories.add(new MenuCategory("Chart", chartMenuItems));
        //CHARTS CATEGORY END

        //MESSAGES CATEGORY START
        List<MenuItem> messagesMenuItems = new ArrayList<>();
        messagesMenuItems.add(new MenuItem("Growl", "/message/growl"));
        messagesMenuItems.add(new MenuItem("Messages", "/message/messages"));
        messagesMenuItems.add(new MenuItem("StaticMessage", "/message/staticMessage"));
        menuCategories.add(new MenuCategory("Messages", messagesMenuItems));
        //MESSAGES CATEGORY END

        //MULTIMEDIA CATEGORY START
        List<MenuItem> multimediaMenuItems = new ArrayList<>();
        multimediaMenuItems.add(new MenuItem("Audio", "/multimedia/audio"));
        multimediaMenuItems.add(new MenuItem("Barcode", "/multimedia/barcode", "Updated"));
        multimediaMenuItems.add(new MenuItem("QRCode", "/multimedia/qrcode", "Updated"));
        multimediaMenuItems.add(new MenuItem("Image Compare", "/multimedia/compare"));

        //Cropper Nested MenuItem
        List<MenuItem> cropperMenuItems = new ArrayList<>();
        cropperMenuItems.add(new MenuItem("Basic", "/multimedia/cropper/cropper"));
        cropperMenuItems.add(new MenuItem("Dynamic", "/multimedia/cropper/dynamic"));
        cropperMenuItems.add(new MenuItem("FileUpload", "/multimedia/cropper/fileupload"));
        multimediaMenuItems.add(new MenuItem("Image Cropper", cropperMenuItems));

        multimediaMenuItems.add(new MenuItem("Graphic Image", "/multimedia/graphicImage"));

        //Galleria Nested MenuItem
        List<MenuItem> galleriaMenuItems = new ArrayList<>();
        galleriaMenuItems.add(new MenuItem("Basic", "/multimedia/galleria/basic"));
        galleriaMenuItems.add(new MenuItem("Programmatic", "/multimedia/galleria/programmatic"));
        galleriaMenuItems.add(new MenuItem("Indicator", "/multimedia/galleria/indicator"));
        galleriaMenuItems.add(new MenuItem("Thumbnail", "/multimedia/galleria/thumbnail"));
        galleriaMenuItems.add(new MenuItem("Navigator", "/multimedia/galleria/navigator"));
        galleriaMenuItems.add(new MenuItem("Responsive", "/multimedia/galleria/responsive"));
        galleriaMenuItems.add(new MenuItem("FullScreen", "/multimedia/galleria/fullscreen"));
        galleriaMenuItems.add(new MenuItem("AutoPlay", "/multimedia/galleria/autoplay"));
        galleriaMenuItems.add(new MenuItem("Caption", "/multimedia/galleria/caption"));
        galleriaMenuItems.add(new MenuItem("Dynamic", "/multimedia/galleria/dynamic"));
        multimediaMenuItems.add(new MenuItem("Galleria", galleriaMenuItems));

        multimediaMenuItems.add(new MenuItem("Media", "/multimedia/media"));

        //PhotoCam Nested MenuItem
        List<MenuItem> photoCamMenuItems = new ArrayList<>();
        photoCamMenuItems.add(new MenuItem("PhotoCam", "/multimedia/photocam/photoCam.xhtml"));
        photoCamMenuItems.add(new MenuItem("Device Selection", "/multimedia/photocam/deviceSelection.xhtml"));
        multimediaMenuItems.add(new MenuItem("PhotoCam", photoCamMenuItems));

        multimediaMenuItems.add(new MenuItem("Switch", "/multimedia/switch"));
        multimediaMenuItems.add(new MenuItem("Video", "/multimedia/video"));
        menuCategories.add(new MenuCategory("Multimedia", multimediaMenuItems));
        //MULTIMEDIA CATEGORY END

        //FILE CATEGORY START
        List<MenuItem> fileMenuItems = new ArrayList<>();

        //Upload Nested MenuItem
        List<MenuItem> uploadMenuItems = new ArrayList<>();
        uploadMenuItems.add(new MenuItem("Simple", "/file/upload/simple"));
        uploadMenuItems.add(new MenuItem("Advanced", "/file/upload/advanced"));
        fileMenuItems.add(new MenuItem("FileUpload", uploadMenuItems));

        fileMenuItems.add(new MenuItem("FileDownload", "/file/download"));
        menuCategories.add(new MenuCategory("File", fileMenuItems));
        //FILE CATEGORY END

        //MISC CATEGORY START
        List<MenuItem> miscMenuItems = new ArrayList<>();
        miscMenuItems.add(new MenuItem("Avatar", "/misc/avatar"));
        miscMenuItems.add(new MenuItem("Badge", "/misc/badge"));
        miscMenuItems.add(new MenuItem("Chip", "/misc/chip"));
        miscMenuItems.add(new MenuItem("ScrollTop", "/misc/scrollTop"));
        miscMenuItems.add(new MenuItem("Skeleton", "/misc/skeleton"));
        miscMenuItems.add(new MenuItem("Tag", "/misc/tag"));
        //miscMenuItems.add(new MenuItem("Responsive", "/misc/responsive"));
        miscMenuItems.add(new MenuItem("AutoUpdate", "/misc/autoUpdate"));
        miscMenuItems.add(new MenuItem("OutputLabel", "/misc/outputLabel"));
        miscMenuItems.add(new MenuItem("FloatLabel", "/misc/floatLabel"));
        miscMenuItems.add(new MenuItem("BlockUI", "/misc/blockUI"));
        miscMenuItems.add(new MenuItem("Cache", "/misc/cache"));
        miscMenuItems.add(new MenuItem("Captcha", "/misc/captcha"));
        miscMenuItems.add(new MenuItem("Clock", "/misc/clock"));
        miscMenuItems.add(new MenuItem("Context", "/misc/context"));

        //DefaultCommand Nested MenuItem
        List<MenuItem> defaultCommandMenuItems = new ArrayList<>();
        defaultCommandMenuItems.add(new MenuItem("Basic", "/misc/defaultcommand/basic"));
        defaultCommandMenuItems.add(new MenuItem("Multiple", "/misc/defaultcommand/multiple"));
        miscMenuItems.add(new MenuItem("DefaultCommand", defaultCommandMenuItems));

        List<MenuItem> dragDropMenuItems = new ArrayList<>();
        dragDropMenuItems.add(new MenuItem("Draggable", "/dnd/draggable"));
        dragDropMenuItems.add(new MenuItem("DataView", "/dnd/dataView"));
        dragDropMenuItems.add(new MenuItem("DataTable", "/dnd/dataTable"));
        dragDropMenuItems.add(new MenuItem("Custom", "/dnd/custom"));
        miscMenuItems.add(new MenuItem("DragDrop", dragDropMenuItems));

        miscMenuItems.add(new MenuItem("Effect", "/misc/effect"));
        miscMenuItems.add(new MenuItem("ExceptionHandler", "/misc/exceptionHandler"));
        miscMenuItems.add(new MenuItem("FeedReader", "/misc/feedReader", "Updated"));
        miscMenuItems.add(new MenuItem("IdleMonitor", "/misc/idleMonitor"));
        miscMenuItems.add(new MenuItem("ImportConstants", "/misc/importConstants"));
        miscMenuItems.add(new MenuItem("ImportEnum", "/misc/importEnum"));
        //miscMenuItems.add(new MenuItem("CSP", "/misc/csp"));
        //miscMenuItems.add(new MenuItem("FontAwesome", "/misc/fa"));
        miscMenuItems.add(new MenuItem("Lifecycle", "/misc/lifecycle"));
        miscMenuItems.add(new MenuItem("Log", "/misc/log"));
        miscMenuItems.add(new MenuItem("Focus", "/misc/focus"));
        miscMenuItems.add(new MenuItem("Hotkey", "/misc/hotkey"));
        miscMenuItems.add(new MenuItem("Printer", "/misc/printer"));
        miscMenuItems.add(new MenuItem("ProgressBar", "/misc/progressBar"));
        miscMenuItems.add(new MenuItem("ResetInput", "/misc/resetInput"));
        miscMenuItems.add(new MenuItem("Resizable", "/misc/resizable"));
        miscMenuItems.add(new MenuItem("Spacer", "/misc/spacer", "Deprecated"));
        miscMenuItems.add(new MenuItem("Spotlight", "/misc/spotlight"));
        miscMenuItems.add(new MenuItem("Sticky", "/misc/sticky"));

        //Terminal Nested MenuItem
        List<MenuItem> terminalCommandMenuItems = new ArrayList<>();
        terminalCommandMenuItems.add(new MenuItem("Basic", "/misc/terminal/basic"));
        terminalCommandMenuItems.add(new MenuItem("Autocomplete", "/misc/terminal/autocomplete"));
        miscMenuItems.add(new MenuItem("Terminal", terminalCommandMenuItems));

        menuCategories.add(new MenuCategory("Misc", miscMenuItems));
        //MISC CATEGORY END
    }

    public List<MenuItem> completeMenuItem(String query) {
        String q = query.toLowerCase();
        List<MenuItem> result = new ArrayList<>();

        for (MenuCategory category : menuCategories) {
            for (MenuItem item : category.getMenuItems()) {
                if (item.getLabel() != null
                        && item.getLabel().toLowerCase().contains(q)) {
                    result.add(item);
                }
            }
        }

        result.sort(Comparator.comparing(m -> m.getParent() != null ? m.getParent().getLabel() : ""));

        return result;
    }

    public MenuItem findMenuItemByUrl(String url) {
        if (url == null) {
            return null;
        }
        for (MenuCategory category : menuCategories) {
            MenuItem found = findInItems(category.getMenuItems(), url);
            if (found != null) {
                return found;
            }
        }
        return null;
    }

    private MenuItem findInItems(List<MenuItem> items, String url) {
        if (items == null) {
            return null;
        }
        for (MenuItem item : items) {
            if (url.equals(item.getUrl())) {
                return item;
            }
            MenuItem foundInChildren = findInItems(item.getMenuItems(), url);
            if (foundInChildren != null) {
                return foundInChildren;
            }
        }
        return null;
    }

    public List<MenuCategory> getMenuCategories() {
        return menuCategories;
    }
}
