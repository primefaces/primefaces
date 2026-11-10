import autosize from "autosize";

import { InputText } from "./src/forms/inputtext.js";
import { InputTextarea } from "./src/forms/inputtextarea.js";
import { SelectOneMenu } from "./src/forms/selectonemenu.js";
import { SelectOneRadio } from "./src/forms/selectoneradio.js";
import { SelectBooleanCheckbox } from "./src/forms/selectbooleancheckbox.js";
import { SelectManyCheckbox } from "./src/forms/selectmanycheckbox.js";
import { SelectListbox } from "./src/forms/selectlistbox.js";
import { SelectOneListbox } from "./src/forms/selectonelistbox.js";
import { SelectManyMenu } from "./src/forms/selectmanymenu.js";
import { CascadeSelect } from "./src/forms/cascadeselect.js";
import { CommandButton } from "./src/forms/commandbutton.js";
import { CommandLink } from "./src/forms/commandlink.js";
import { Button } from "./src/forms/button.js";
import { LinkButton } from "./src/forms/linkbutton.js";
import { SelectManyButton } from "./src/forms/selectmanybutton.js";
import { SelectOneButton } from "./src/forms/selectonebutton.js";
import { SelectBooleanButton } from "./src/forms/selectbooleanbutton.js";
import { SelectCheckboxMenu } from "./src/forms/selectcheckboxmenu.js";
import { Password } from "./src/forms/password.js";
import { DefaultCommand } from "./src/forms/defaultcommand.js";
import { SplitButton } from "./src/forms/splitbutton.js";
import { MultiSelectListbox } from "./src/forms/multiselectlistbox.js";

import { BreadCrumb } from "./src/menu/breadcrumb.js";
import { ContextMenu } from "./src/menu/contextmenu.js";
import { MegaMenu } from "./src/menu/megamenu.js";
import { Menu } from "./src/menu/menu.base.js";
import { Menubar } from "./src/menu/menubar.js";
import { MenuButton } from "./src/menu/menubutton.js";
import { PanelMenu } from "./src/menu/panelmenu.js";
import { PlainMenu } from "./src/menu/plainmenu.js";
import { SlideMenu } from "./src/menu/slidemenu.js";
import { Steps } from "./src/menu/steps.js";
import { TabMenu } from "./src/menu/tabmenu.js";
import { TieredMenu } from "./src/menu/tieredmenu.js";

import { dialog, registerDialogHooks, type Dialogs, type DialogHandler as _DialogHandler } from "./src/core.dialog.js";
import { ConfirmDialog, Dialog as _Dialog, DynamicDialog } from "./src/dialog/dialog.js";

import { Growl, registerMessageRenderHookForGrowlWidget } from "./src/growl/growl.js";
import { Message, registerMessageRenderHookForMessageWidget } from "./src/message/message.js";
import { Messages, registerMessageRenderHookForMessagesWidget } from "./src/messages/messages.js";
import { registerRipple } from "./src/ripple/ripple.js";
import { StaticMessage } from "./src/staticmessage/staticmessage.js";

import { AccordionPanel } from "./src/accordion/accordion.js";
import { AutoComplete } from "./src/autocomplete/autocomplete.js";
import { BlockUI } from "./src/blockui/blockui.js";
import { Carousel } from "./src/carousel/carousel.js";
import { ConfirmPopup } from "./src/confirmpopup/confirmpopup.js";
import { ColumnToggler } from "./src/columntoggler/columntoggler.js";
import { Dashboard } from "./src/dashboard/dashboard.js";
import { DataGrid } from "./src/datagrid/datagrid.js";
import { DataList } from "./src/datalist/datalist.js";
import { DataScroller } from "./src/datascroller/datascroller.js";

import { DataTable } from "./src/datatable/datatable.js";
import { FrozenDataTable } from "./src/datatable/datatable.frozen.js";
import { Draggable, Droppable } from "./src/dragdrop/dragdrop.js";
import { Effect } from "./src/effect/effect.js";
import { Fieldset } from "./src/fieldset/fieldset.js";
import { Inplace } from "./src/inplace/inplace.js";
import { NotificationBar } from "./src/notificationbar/notificationbar.js";
import { Panel } from "./src/panel/panel.js";
import { OrderList } from "./src/orderlist/orderlist.js";
import { OutputPanel } from "./src/outputpanel/outputpanel.js";
import { OverlayPanel } from "./src/overlaypanel/overlaypanel.js";
import { Paginator } from "./src/paginator/paginator.js";
import { PickList } from "./src/picklist/picklist.js";
import { ProgressBar } from "./src/progressbar/progressbar.js";
import { Rating } from "./src/rating/rating.js";
import { Resizable } from "./src/resizable/resizable.js";
import { Slider } from "./src/slider/slider.js";
import { Spinner } from "./src/spinner/spinner.js";
import { Splitter } from "./src/splitter/splitter.js";
import { Spotlight } from "./src/spotlight/spotlight.js";
import { SpeedDial } from "./src/speeddial/speeddial.js";
import { Sticky } from "./src/sticky/sticky.js";
import { TabView } from "./src/tabview/tabview.js";
import { TagCloud } from "./src/tagcloud/tagcloud.js";
import { Tooltip } from "./src/tooltip/tooltip.js";

import { BaseTree } from "./src/tree/tree.base.js";
import { VerticalTree } from "./src/tree/tree.vertical.js";
import { HorizontalTree } from "./src/tree/tree.horizontal.js";
import { TreeTable } from "./src/treetable/treetable.js";

import { Wizard } from "./src/wizard/wizard.js";
import { TriStateCheckbox } from "./src/tristatecheckbox/tristatecheckbox.js";
import { Chip } from "./src/chip/chip.js";
import { Chips } from "./src/chips/chips.js";
import { ScrollTop } from "./src/scrolltop/scrolltop.js";
import { Sidebar } from "./src/sidebar/sidebar.js";
import { DataView } from "./src/dataview/dataview.js";
import { ToggleSwitch } from "./src/toggleswitch/toggleswitch.js";

// Add widgets and dialog to the PrimeFaces global object
exposeToGlobalScope();

function exposeToGlobalScope() {
    if (PrimeFaces.dialog) {
        return;
    }

    // Register hook contributions from this "components" module
    registerDialogHooks();
    registerMessageRenderHookForGrowlWidget();
    registerMessageRenderHookForMessageWidget()
    registerMessageRenderHookForMessagesWidget();
    registerRipple();
    
    // Expose autosize to the global scope
    Object.assign(window, { autosize });

    // Expose widgets to the global scope

    // src/accordion
    PrimeFaces.widget.AccordionPanel = AccordionPanel;

    // src/autocomplete
    PrimeFaces.widget.AutoComplete = AutoComplete;

    // src/blockui
    PrimeFaces.widget.BlockUI = BlockUI;

    // src/carousel
    PrimeFaces.widget.Carousel = Carousel;

    // src/chip
    PrimeFaces.widget.Chip = Chip;

    // src/chips
    PrimeFaces.widget.Chips = Chips;

    // src/columntoggler
    PrimeFaces.widget.ColumnToggler = ColumnToggler;

    // src/confirmpopup
    PrimeFaces.widget.ConfirmPopup = ConfirmPopup;

    // src/dashboard
    PrimeFaces.widget.Dashboard = Dashboard;

    // src/datagrid
    PrimeFaces.widget.DataGrid = DataGrid;

    // src/datalist
    PrimeFaces.widget.DataList = DataList;

    // src/datatable
    PrimeFaces.widget.DataTable = DataTable;
    PrimeFaces.widget.FrozenDataTable = FrozenDataTable;

    // src/dataview
    PrimeFaces.widget.DataView = DataView;

    // src/dialog
    PrimeFaces.dialog = dialog;
    PrimeFaces.widget.ConfirmDialog = ConfirmDialog;
    PrimeFaces.widget.Dialog = _Dialog;
    PrimeFaces.widget.DynamicDialog = DynamicDialog;

    // src/datascroller
    PrimeFaces.widget.DataScroller = DataScroller;

    // src/dragdrop
    PrimeFaces.widget.Draggable = Draggable;
    PrimeFaces.widget.Droppable = Droppable;

    // src/effect
    PrimeFaces.widget.Effect = Effect;

    // src/fieldset
    PrimeFaces.widget.Fieldset = Fieldset;

    // src/forms
    PrimeFaces.widget.Button = Button;
    PrimeFaces.widget.CascadeSelect = CascadeSelect;
    PrimeFaces.widget.CommandButton = CommandButton;
    PrimeFaces.widget.CommandLink = CommandLink;
    PrimeFaces.widget.DefaultCommand = DefaultCommand;
    PrimeFaces.widget.InputText = InputText;
    PrimeFaces.widget.InputTextarea = InputTextarea;
    PrimeFaces.widget.LinkButton = LinkButton;
    PrimeFaces.widget.MultiSelectListbox = MultiSelectListbox;
    PrimeFaces.widget.Password = Password;
    PrimeFaces.widget.SelectBooleanButton = SelectBooleanButton;
    PrimeFaces.widget.SelectBooleanCheckbox = SelectBooleanCheckbox;
    PrimeFaces.widget.SelectCheckboxMenu = SelectCheckboxMenu;
    PrimeFaces.widget.SelectListbox = SelectListbox;
    PrimeFaces.widget.SelectManyButton = SelectManyButton;
    PrimeFaces.widget.SelectManyCheckbox = SelectManyCheckbox;
    PrimeFaces.widget.SelectManyMenu = SelectManyMenu;
    PrimeFaces.widget.SelectOneButton = SelectOneButton;
    PrimeFaces.widget.SelectOneListbox = SelectOneListbox;
    PrimeFaces.widget.SelectOneMenu = SelectOneMenu;
    PrimeFaces.widget.SelectOneRadio = SelectOneRadio;
    PrimeFaces.widget.SplitButton = SplitButton;

    // src/growl
    PrimeFaces.widget.Growl = Growl;

    // src/inplace
    PrimeFaces.widget.Inplace = Inplace;

    // src/menu
    PrimeFaces.widget.BreadCrumb = BreadCrumb;
    PrimeFaces.widget.ContextMenu = ContextMenu;
    PrimeFaces.widget.MegaMenu = MegaMenu;
    PrimeFaces.widget.Menu = Menu;
    PrimeFaces.widget.MenuButton = MenuButton;
    PrimeFaces.widget.Menubar = Menubar;
    PrimeFaces.widget.PanelMenu = PanelMenu;
    PrimeFaces.widget.PlainMenu = PlainMenu;
    PrimeFaces.widget.SlideMenu = SlideMenu;
    PrimeFaces.widget.Steps = Steps;
    PrimeFaces.widget.TabMenu = TabMenu;
    PrimeFaces.widget.TieredMenu = TieredMenu;

    // src/message(s)
    PrimeFaces.widget.Message = Message;
    PrimeFaces.widget.Messages = Messages;

    // src/notificationbar
    PrimeFaces.widget.NotificationBar = NotificationBar;

    // src/orderlist
    PrimeFaces.widget.OrderList = OrderList;

    // src/outputpanel
    PrimeFaces.widget.OutputPanel = OutputPanel;

    // src/overlaypanel
    PrimeFaces.widget.OverlayPanel = OverlayPanel;

    // src/paginator
    PrimeFaces.widget.Paginator = Paginator;

    // src/panel
    PrimeFaces.widget.Panel = Panel;

    // src/picklist
    PrimeFaces.widget.PickList = PickList;

    // src/progressbar
    PrimeFaces.widget.ProgressBar = ProgressBar;

    // src/rating
    PrimeFaces.widget.Rating = Rating;

    // src/resizable
    PrimeFaces.widget.Resizable = Resizable;

    // src/scrolltop
    PrimeFaces.widget.ScrollTop = ScrollTop;

    // src/sidebar
    PrimeFaces.widget.Sidebar = Sidebar;

    // src/slider
    PrimeFaces.widget.Slider = Slider;

    // src/speeddial
    PrimeFaces.widget.SpeedDial = SpeedDial;

    // src/spinner
    PrimeFaces.widget.Spinner = Spinner;

    // src/splitter
    PrimeFaces.widget.Splitter = Splitter;

    // src/spotlight
    PrimeFaces.widget.Spotlight = Spotlight;

    // src/staticmessage
    PrimeFaces.widget.StaticMessage = StaticMessage;

    // src/sticky
    PrimeFaces.widget.Sticky = Sticky;

    // src/tabview
    PrimeFaces.widget.TabView = TabView;

    // src/tagcloud
    PrimeFaces.widget.TagCloud = TagCloud;

    // src/toggleswitch
    PrimeFaces.widget.ToggleSwitch = ToggleSwitch;

    // src/tooltip
    PrimeFaces.widget.Tooltip = Tooltip;

    // src/tree
    PrimeFaces.widget.BaseTree = BaseTree;
    PrimeFaces.widget.HorizontalTree = HorizontalTree;
    PrimeFaces.widget.VerticalTree = VerticalTree;

    // src/treetable
    PrimeFaces.widget.TreeTable = TreeTable;

    // src/tristatecheckbox
    PrimeFaces.widget.TriStateCheckbox = TriStateCheckbox;

    // src/wizard
    PrimeFaces.widget.Wizard = Wizard;
}

// Global extensions
declare global {
    namespace PrimeFacesTypes {
        interface WindowExtensions {
            autosize: typeof autosize;
        }
    }
}

// Types (src/accordion)
declare global {
    namespace PrimeFacesTypes {
        export interface WidgetRegistry {
            AccordionPanel: typeof AccordionPanel;
        }
    }
    namespace PrimeFacesTypes.widget {
        export type AccordionPanelCfg = import("./src/accordion/accordion.js").AccordionPanelCfg;
    }
    namespace PrimeFacesTypes.widget.AccordionPanel {
        /**
         * The open tab(s): with `multiple` a list of keys or indices, otherwise a key, an index or `none`.
         */
        export type Active = string | number | (string | number)[];

        /**
         * Called before a tab is opened, with the accordion as `this` and the content of the tab. `false` keeps it closed.
         */
        export type OnTabChangeCallback = (this: AccordionPanel, panel: JQuery) => boolean | void;

        /**
         * Called when a tab was opened, with the accordion as `this` and the content of the tab.
         */
        export type OnTabShowCallback = (this: AccordionPanel, panel: JQuery) => void;

        /**
         * Called when a tab was closed, with the accordion as `this`. Gets the content of the closed tab, or, when a
         * tab is closed because another one is opened, the content of the opened tab.
         */
        export type OnTabCloseCallback = (this: AccordionPanel, panel: JQuery) => void;
    }
}

// Types (src/autocomplete)
declare global {
    namespace PrimeFacesTypes {
        export interface WidgetRegistry {
            AutoComplete: typeof AutoComplete;
        }
    }
    namespace PrimeFacesTypes.widget {
        export type AutoCompleteCfg = import("./src/autocomplete/autocomplete.js").AutoCompleteCfg;
    }
    namespace PrimeFacesTypes.widget.AutoComplete {
        /**
         * Specifies the behavior of the dropdown button.
         * - `blank`: Sends an empty string.
         * - `current`: Send the input value.
         */
        export type DropdownMode = "blank" | "current";

        /**
         * Event to initiate the autocomplete search.
         * - `enter`: Starts the search for suggestion items when the enter key is pressed.
         * - `keyup`: Starts the search for suggestion items as soon as a key is released.
         */
        export type QueryEvent = "keyup" | "enter";

        /**
         * Specifies whether filter requests are evaluated by the client's browser or whether they are sent to the
         * server.
         */
        export type QueryMode = "server" | "client" | "hybrid";

        /**
         * Client side callback to invoke when the value changes, the original `onchange` handler of the input. It is
         * called with the input as `this` when the input changes, and with the widget as `this` when an item was
         * added via {@link AutoComplete.addItem}.
         */
        export type OnChangeCallback = (this: HTMLElement | AutoComplete, event?: JQuery.TriggeredEvent) => unknown;

        /**
         * The JSON response of the `completeEndpoint`.
         */
        export interface CompleteEndpointResponse {
            /** The suggestions, the `value` defaults to the `label`. */
            suggestions: { label: string; value?: string }[];
            /** Whether there are more suggestions than returned. */
            moreAvailable?: boolean;
        }
    }
}

// Types (src/blockui)
declare global {
    namespace PrimeFacesTypes {
        export interface WidgetRegistry {
            BlockUI: typeof BlockUI;
        }
    }
    namespace PrimeFacesTypes.widget {
        export type BlockUICfg = import("./src/blockui/blockui.js").BlockUICfg;
    }
}

// Types (src/carousel)
declare global {
    namespace PrimeFacesTypes {
        export interface WidgetRegistry {
            Carousel: typeof Carousel;
        }
    }
    namespace PrimeFacesTypes.widget {
        export type CarouselCfg = import("./src/carousel/carousel.js").CarouselCfg;
    }
    namespace PrimeFacesTypes.widget.Carousel {
        /**
         * Layout of the carousel: items side by side or on top of each other.
         */
        export type Orientation = "horizontal" | "vertical";

        /**
         * Number of visible and scrolled items up to a maximum screen width.
         */
        export interface ResponsiveOption {
            /** The maximum screen width, a CSS length such as `768px`. */
            breakpoint: string;
            /** Number of visible items up to the breakpoint. */
            numVisible: number;
            /** Number of items to scroll up to the breakpoint. */
            numScroll: number;
        }

        /**
         * Called when the page was changed, with the carousel as `this` and the 0-based index of the new page.
         */
        export type OnPageChangeCallback = (this: Carousel, page: number) => void;
    }
}

// Types (src/chip)
declare global {
    namespace PrimeFacesTypes {
        export interface WidgetRegistry {
            Chip: typeof Chip;
        }
    }
    namespace PrimeFacesTypes.widget {
        export type ChipCfg = import("./src/chip/chip.js").ChipCfg;
    }
}

// Types (src/chips)
declare global {
    namespace PrimeFacesTypes {
        export interface WidgetRegistry {
            Chips: typeof Chips;
        }
    }
    namespace PrimeFacesTypes.widget {
        export type ChipsCfg = import("./src/chips/chips.js").ChipsCfg;
    }
}

// Types (src/columntoggler)
declare global {
    namespace PrimeFacesTypes {
        export interface WidgetRegistry {
            ColumnToggler: typeof ColumnToggler;
        }
    }
    namespace PrimeFacesTypes.widget {
        export type ColumnTogglerCfg = import("./src/columntoggler/columntoggler.js").ColumnTogglerCfg;
    }
}

// src/confirmpopup
declare global {
    namespace PrimeFacesTypes {
        export interface WidgetRegistry {
            ConfirmPopup: typeof ConfirmPopup;
        }
        export interface PrimeFaces {
            confirmPopup?: ConfirmPopup<widget.ConfirmPopupCfg>;
            confirmPopupSource?: JQuery | null;
        }
    }
    namespace PrimeFacesTypes.widget {
        export type ConfirmPopupCfg = import("./src/confirmpopup/confirmpopup.js").ConfirmPopupCfg;
    }

    namespace PrimeFacesTypes.widget.ConfirmPopup {
        /**
         * Callback invoked after the popup is hidden.
         */
        export type HideCallback = () => void;
    }
}

// Types (src/dashboard)
declare global {
    namespace PrimeFacesTypes {
        export interface WidgetRegistry {
            Dashboard: typeof Dashboard;
        }
    }
    namespace PrimeFacesTypes.widget {
        export type DashboardCfg = import("./src/dashboard/dashboard.js").DashboardCfg;
    }
    namespace PrimeFacesTypes.widget.Dashboard {
        /**
         * The `ui` argument of {@link Dashboard.handleDrop}: the one of the sortable (legacy dashboard, with `item` and
         * `sender`) or of the droppable (responsive dashboard, with `draggable`).
         */
        export type DropUi = Partial<Pick<PrimeFacesTypes.interactions.SortableUi, "item" | "sender">>
            & Partial<Pick<PrimeFacesTypes.interactions.DroppableUi, "draggable">>;
    }
}

// Types (src/datagrid)
declare global {
    namespace PrimeFacesTypes {
        export interface WidgetRegistry {
            DataGrid: typeof DataGrid;
        }
    }
    namespace PrimeFacesTypes.widget {
        export type DataGridCfg = import("./src/datagrid/datagrid.js").DataGridCfg;
    }
}

// Types (src/datalist)
declare global {
    namespace PrimeFacesTypes {
        export interface WidgetRegistry {
            DataList: typeof DataList;
        }
    }
    namespace PrimeFacesTypes.widget {
        export type DataListCfg = import("./src/datalist/datalist.js").DataListCfg;
    }
}

// Types (src/datatable)
declare global {
    namespace PrimeFacesTypes {
        export interface WidgetRegistry {
            DataTable: typeof DataTable;
            FrozenDataTable: typeof FrozenDataTable;
        }
    }
    namespace PrimeFacesTypes.widget {
        export type DataTableCfg = import("./src/datatable/datatable.js").DataTableCfg;
        export type FrozenDataTableCfg = import("./src/datatable/datatable.frozen.js").FrozenDataTableCfg;
    }
    namespace PrimeFacesTypes.widget.DataTable {
        /**
         * Either the 0-based index of a row, or the row element (`TR`) itself.
         */
        export type RowSpecifier = number | JQuery;

        /**
         * The available sort order types for the DataTable.
         */
        export type SortOrder = "ASCENDING" | "DESCENDING" | "UNSORTED";

        /**
         * Indicates whether multiple rows or only a single row of a DataTable can be selected.
         */
        export type CmSelectionMode = "single" | "multiple";

        /**
         * Indicates whether rows are selected via radio buttons or via checkboxes.
         */
        export type SelectionMode = "radio" | "checkbox";

        /**
         * Indicates whether a DataTable can be sorted by multiple columns or only by a single column.
         */
        export type SortMode = "single" | "multiple";

        /**
         * Indicates whether multiple columns of a DataTable can be expanded at the same time, or whether other
         * expanded rows should be collapsed when a new row is expanded.
         */
        export type RowExpandMode = "single" | "multiple";

        /**
         * Indicates whether row editors are loaded eagerly or on-demand.
         */
        export type RowEditMode = "eager" | "lazy";

        /**
         * Indicates whether cell editors are loaded eagerly or on-demand.
         */
        export type CellEditMode = "eager" | "lazy";

        /**
         * Indicates the resize behavior of columns.
         */
        export type ResizeMode = "expand" | "fit";

        /**
         * Indicates how rows of a DataTable may be selected, when clicking on the row itself (not the checkbox /
         * radiobutton from p:column). `new` always unselects other rows, `add` preserves the currently selected rows,
         * and `none` disables row selection.
         */
        export type SelectionRowMode = "new" | "add" | "none";

        /**
         * When a row is editable: whether to `save` the current contents of the row or `cancel` the row edit and
         * discard all changes.
         */
        export type RowEditAction = "cancel" | "save";

        /**
         * Callback that is invoked when the user clicks on a row of the DataTable. On a click, `this` is the clicked
         * `TR` element; when the row is selected with ENTER or SPACE, `this` is the DataTable.
         * @param row The TR row that was clicked.
         */
        export type OnRowClickCallback = (this: HTMLElement | DataTable, row: JQuery) => void;

        /**
         * Describes the meta information of row, such as its index and its row key.
         */
        export interface RowMeta {
            /** The unique key of the row. `undefined` when no key was defined for the rows. */
            key: string | undefined;
            /** The 0-based index of the row in the DataTable. */
            index: number;
        }

        /**
         * Describes a sorting operation of the DataTable. The items of the DataTable may be sorted by multiple
         * columns, in which case the sorting operation is described by a list of these objects.
         */
        export interface SortMeta {
            /** ID of the column to sort by. */
            col: string;
            /** Whether to sort the items by the column value in an ascending or descending order, or unsorted. */
            order: -1 | 0 | 1;
        }

        /**
         * Describes the width information of a DOM element.
         */
        export interface WidthInfo {
            /**
             * The width of the element. It's either a unit-less numeric pixel value or a string containing the width
             * including an unit.
             */
            width: number | string;
            /** Tells whether the width includes the border-box or not. */
            isOuterWidth: boolean;
        }
    }
}

// Types (src/dataview)
declare global {
    namespace PrimeFacesTypes {
        export interface WidgetRegistry {
            DataView: typeof DataView;
        }
    }
    namespace PrimeFacesTypes.widget {
        export type DataViewCfg = import("./src/dataview/dataview.js").DataViewCfg;
    }
    namespace PrimeFacesTypes.widget.DataView {
        /**
         * The layout mode the data view. `grid` displays the item in a grid with cards, `list` displays the items in a
         * vertical list.
         */
        export type Layout = "grid" | "list";
    }
}

// Types (src/dialog)
declare global {
    namespace PrimeFacesTypes {
        export interface PrimeFaces {
            dialog: Dialogs;
            /**
             * When the global feature of the confirm dialog is used, this stores the current
             * confirm dialog instance that is used as the global confirm dialog.
             */
            confirmDialog?: ConfirmDialog | undefined;
            /**
             * When the global feature of the confirm dialog is used, this stores
             * the source element that requested the currently open global confirm dialog.
             */
            confirmSource?: JQuery | null;
        }
        export interface WidgetRegistry {
            ConfirmDialog: typeof ConfirmDialog;
            Dialog: typeof _Dialog;
            DynamicDialog: typeof DynamicDialog;
        }
        export type Dialog = Dialogs;
    }
    namespace PrimeFacesTypes.dialog {
        export type DialogHandler = _DialogHandler;

    }
    namespace PrimeFacesTypes.widget {
        export type ConfirmDialogCfg = import("./src/dialog/dialog.js").ConfirmDialogCfg;
        export type DialogCfg = import("./src/dialog/dialog.js").DialogCfg;
        export type DynamicDialogCfg = import("./src/dialog/dialog.js").DynamicDialogCfg;
    }
    namespace PrimeFacesTypes.dialog {
    }
    namespace PrimeFacesTypes.widget.Dialog {
        /**
         * Client-side callback to invoke when the dialog is closed, see
         * {@link DialogCfg.onHide}.
         * 
         * Note: The `duration` parameter appears to be legacy and may be removed
         * soon. Do not use it!
         */
        export type OnHideCallback = <Cfg extends DialogCfg>(this: _Dialog<Cfg>, duration?: string | number | undefined) => void;

        /**
         * Client-side callback to invoke when the dialog is opened, see
         * {@link DialogCfg.onShow}
         */
        export type OnShowCallback = <Cfg extends DialogCfg>(this: _Dialog<Cfg>) => void;

        /**
         * Handler for obtaining additional DOM elements which are allowed to be focused via tabbing.
         */
        export type GetModalTabbablesHandler =
            /**
             * @returns The additional DOM elements which are allowed to be focused via tabbing.
             */
            () => JQuery;

        /**
         * The client-side state of the dialog such as its width
         * and height. The client-side state can be preserved during AJAX updates by sending it to the server.
         */
        export interface ClientState {
            /**
             * The total height in pixels of the content area of the dialog.
             */
            contentHeight: number | string;
            /**
             * The total width in pixels of the content area of the dialog..
             */
            contentWidth: number | string;
            /**
             * The total height of the dialog in pixels, including the header and its content.
             */
            height: number | string;
            /**
             * Vertical and horizontal offset of the top-left corner of the dialog.
             */
            offset: JQuery.Coordinates;
            /**
             * The total width of the dialog in pixels, including the header and its content.
             */
            width: number | string;
            /**
             * Horizontal scroll position of the window.
             */
            windowScrollLeft: number;
            /**
             * Vertical scroll position of the window.
             */
            windowScrollTop: number;
        }
    }
}

// Types (src/datascroller)
declare global {
    namespace PrimeFacesTypes {
        export interface WidgetRegistry {
            DataScroller: typeof DataScroller;
        }
    }
    namespace PrimeFacesTypes.widget {
        export type DataScrollerCfg = import("./src/datascroller/datascroller.js").DataScrollerCfg;
    }
    namespace PrimeFacesTypes.widget.DataScroller {
        /**
         * Target of the scroll listener: `document` listens on the document, `inline` on the content of the data scroller.
         */
        export type Mode = "document" | "inline";

        /**
         * When more items are loaded: `scroll` while scrolling down, `manual` on a click on the `more` button.
         */
        export type LoadEvent = "scroll" | "manual";
    }
}

// Types (src/dragdrop)
declare global {
    namespace PrimeFacesTypes {
        export interface WidgetRegistry {
            Draggable: typeof Draggable;
            Droppable: typeof Droppable;
        }
    }
    namespace PrimeFacesTypes.widget {
        export type DraggableCfg = import("./src/dragdrop/dragdrop.js").DraggableCfg;
        export type DroppableCfg = import("./src/dragdrop/dragdrop.js").DroppableCfg;
    }
    namespace PrimeFacesTypes.widget.Draggable {
        /**
         * Callback for when dragging starts, with the draggable as `this`. See also {@link DraggableCfg.onStart}.
         */
        export type OnStartCallback = (this: Draggable, event: JQuery.TriggeredEvent, ui: PrimeFacesTypes.interactions.DraggableUi) => void;

        /**
         * Callback for when dragging ends, with the draggable as `this`. See also {@link DraggableCfg.onStop}.
         */
        export type OnStopCallback = (this: Draggable, event: JQuery.TriggeredEvent, ui: PrimeFacesTypes.interactions.DraggableUi) => void;

        /**
         * Callback for when dragging, with the draggable as `this`. See also {@link DraggableCfg.onDrag}.
         */
        export type OnDragCallback = (this: Draggable, event: JQuery.TriggeredEvent, ui: PrimeFacesTypes.interactions.DraggableUi) => void;
    }
    namespace PrimeFacesTypes.widget.Droppable {
        /**
         * Callback for when an items is dropped, with the droppable as `this`. See also {@link DroppableCfg.onDrop}.
         */
        export type OnDropCallback = (this: Droppable, event: JQuery.TriggeredEvent, ui: PrimeFacesTypes.interactions.DroppableUi) => void;
    }
}

// Types (src/effect)
declare global {
    namespace PrimeFacesTypes {
        export interface WidgetRegistry {
            Effect: typeof Effect;
        }
    }
    namespace PrimeFacesTypes.widget {
        export type EffectCfg = import("./src/effect/effect.js").EffectCfg;
    }
}

// Types (src/fieldset)
declare global {
    namespace PrimeFacesTypes {
        export interface WidgetRegistry {
            Fieldset: typeof Fieldset;
        }
    }
    namespace PrimeFacesTypes.widget {
        export type FieldsetCfg = import("./src/fieldset/fieldset.js").FieldsetCfg;
    }
}

// Types (src/forms)
declare global {
    namespace PrimeFacesTypes {
        export interface WidgetRegistry {
            Button: typeof Button;
            CascadeSelect: typeof CascadeSelect;
            CommandButton: typeof CommandButton;
            CommandLink: typeof CommandLink;
            DefaultCommand: typeof DefaultCommand;
            InputText: typeof InputText;
            InputTextarea: typeof InputTextarea;
            LinkButton: typeof LinkButton;
            MultiSelectListbox: typeof MultiSelectListbox;
            Password: typeof Password;
            SelectBooleanButton: typeof SelectBooleanButton;
            SelectBooleanCheckbox: typeof SelectBooleanCheckbox;
            SelectCheckboxMenu: typeof SelectCheckboxMenu;
            SelectListbox: typeof SelectListbox;
            SelectManyButton: typeof SelectManyButton;
            SelectManyCheckbox: typeof SelectManyCheckbox;
            SelectManyMenu: typeof SelectManyMenu;
            SelectOneButton: typeof SelectOneButton;
            SelectOneListbox: typeof SelectOneListbox;
            SelectOneMenu: typeof SelectOneMenu;
            SelectOneRadio: typeof SelectOneRadio;
            SplitButton: typeof SplitButton;
        }
    }

    namespace PrimeFacesTypes.widget {
        export type ButtonCfg = import("./src/forms/button.js").ButtonCfg;
        export type CascadeSelectCfg = import("./src/forms/cascadeselect.js").CascadeSelectCfg;
        export type CommandButtonCfg = import("./src/forms/commandbutton.js").CommandButtonCfg;
        export type CommandLinkCfg = import("./src/forms/commandlink.js").CommandLinkCfg;
        export type DefaultCommandCfg = import("./src/forms/defaultcommand.js").DefaultCommandCfg;
        export type InputTextCfg = import("./src/forms/inputtext.js").InputTextCfg;
        export type InputTextareaCfg = import("./src/forms/inputtextarea.js").InputTextareaCfg;
        export type LinkButtonCfg = import("./src/forms/linkbutton.js").LinkButtonCfg;
        export type MultiSelectListboxCfg = import("./src/forms/multiselectlistbox.js").MultiSelectListboxCfg;
        export type PasswordCfg = import("./src/forms/password.js").PasswordCfg;
        export type SelectBooleanButtonCfg = import("./src/forms/selectbooleanbutton.js").SelectBooleanButtonCfg;
        export type SelectBooleanCheckboxCfg = import("./src/forms/selectbooleancheckbox.js").SelectBooleanCheckboxCfg;
        export type SelectCheckboxMenuCfg = import("./src/forms/selectcheckboxmenu.js").SelectCheckboxMenuCfg;
        export type SelectListboxCfg = import("./src/forms/selectlistbox.js").SelectListboxCfg;
        export type SelectManyButtonCfg = import("./src/forms/selectmanybutton.js").SelectManyButtonCfg;
        export type SelectManyCheckboxCfg = import("./src/forms/selectmanycheckbox.js").SelectManyCheckboxCfg;
        export type SelectManyMenuCfg = import("./src/forms/selectmanymenu.js").SelectManyMenuCfg;
        export type SelectOneButtonCfg = import("./src/forms/selectonebutton.js").SelectOneButtonCfg;
        export type SelectOneListboxCfg = import("./src/forms/selectonelistbox.js").SelectOneListboxCfg;
        export type SelectOneMenuCfg = import("./src/forms/selectonemenu.js").SelectOneMenuCfg;
        export type SelectOneRadioCfg = import("./src/forms/selectoneradio.js").SelectOneRadioCfg;
        export type SplitButtonCfg = import("./src/forms/splitbutton.js").SplitButtonCfg;
    }

    namespace PrimeFacesTypes.widget.SelectCheckboxMenu {
        /**
         * Available modes for filtering the options of a select checkbox menu. When `custom` is set, a
         * `filterFunction` must be specified.
         */
        export type FilterMatchMode = "startsWith" | "contains" | "endsWith" | "custom";

        /**
         * A function for filtering the options of a select checkbox menu.
         * @param itemLabel The (searchable) label of an option.
         * @param filterValue The (searchable) value to search for.
         * @returns `true` if the item label matches the filter value, or `false` otherwise.
         */
        export type FilterFunction = (itemLabel: string, filterValue: string) => boolean;

        /**
         * Callback that is invoked when a checkbox option was checked or unchecked, with the widget as `this`.
         */
        export type OnChangeCallback = (this: SelectCheckboxMenu) => void;

        /**
         * Callback that is invoked when the overlay panel was hidden, with the widget as `this`.
         */
        export type OnHideCallback = (this: SelectCheckboxMenu) => void;

        /**
         * Callback that is invoked when the overlay panel was brought up, with the widget as `this`.
         */
        export type OnShowCallback = (this: SelectCheckboxMenu) => void;
    }

    namespace PrimeFacesTypes.widget.SelectListbox {
        /**
         * Mode of the filter of a listbox, `custom` uses the `filterFunction`.
         */
        export type FilterMatchMode = "startsWith" | "contains" | "endsWith" | "custom";

        /**
         * Filter function of a listbox.
         * @param value The (searchable) text of an option.
         * @param filter The (searchable) filter value.
         * @returns `true` if the option matches the filter.
         */
        export type FilterFunction = (value: string, filter: string) => boolean;
    }

    namespace PrimeFacesTypes.widget.SelectManyButton {
        /**
         * Called when the value has changed, with the widget as `this`.
         */
        export type ChangeCallback = (this: SelectManyButton) => void;
    }

    namespace PrimeFacesTypes.widget.SelectOneButton {
        /**
         * Called when the value has changed, with the widget as `this`.
         */
        export type ChangeCallback = (this: SelectOneButton) => void;
    }

    namespace PrimeFacesTypes.widget.SelectOneMenu {
        /**
         * Duration of toggle animation of the overlay panel.
         */
        export type EffectSpeed = "slow" | "normal" | "fast";

        /**
         * Available modes for filtering the options of a select one menu. When `custom` is set, a `filterFunction`
         * must be specified.
         */
        export type FilterMatchMode = "startsWith" | "contains" | "endsWith" | "custom";

        /**
         * A function for filtering the options of a select one menu.
         * @param itemLabel The (searchable) label of an option.
         * @param filterValue The (searchable) value to search for.
         * @returns `true` if the item label matches the filter value, or `false` otherwise.
         */
        export type FilterFunction = (itemLabel: string, filterValue: string) => boolean;
    }

    namespace PrimeFacesTypes.widget.SplitButton {
        /**
         * Available modes for filtering the options of the available buttons actions of a split button. When `custom` is set, a
         * `filterFunction` must be specified.
         */
        export type FilterMatchMode = "startsWith" | "contains" | "endsWith" | "custom";

        /**
         * A filter function that takes a term and returns whether the
         * search term matches the value.
         */
        export type FilterFunction =
            /**
             * @param value A value to check.
             * @param query A search term against which the value is checked.
             * @return `true` if the search term matches the value, or `false`
             * otherwise.
             */
            (value: string, query: string) => boolean;
    }
}

// Types (src/growl)
declare global {
    namespace PrimeFacesTypes {
        export interface WidgetRegistry {
            Growl: typeof Growl;
        }
    }
    namespace PrimeFacesTypes.widget {
        export type GrowlCfg = import("./src/growl/growl.js").GrowlCfg;
    }
}

// Types (src/inplace)
declare global {
    namespace PrimeFacesTypes {
        export interface WidgetRegistry {
            Inplace: typeof Inplace;
        }
    }
    namespace PrimeFacesTypes.widget {
        export type InplaceCfg = import("./src/inplace/inplace.js").InplaceCfg;
    }
    namespace PrimeFacesTypes.widget.Inplace {
        /**
         * Effect for the transition between the display element and the inline content.
         */
        export type EffectType = "fade" | "none" | "slide";
    }
}

// Types (src/menu)
declare global {
    namespace PrimeFacesTypes {
        export interface WidgetRegistry {
            BreadCrumb: typeof BreadCrumb;
            ContextMenu: typeof ContextMenu;
            MegaMenu: typeof MegaMenu;
            Menu: typeof Menu;
            Menubar: typeof Menubar;
            MenuButton: typeof MenuButton;
            PanelMenu: typeof PanelMenu;
            PlainMenu: typeof PlainMenu;
            SlideMenu: typeof SlideMenu;
            Steps: typeof Steps;
            TabMenu: typeof TabMenu;
            TieredMenu: typeof TieredMenu;
        }
    }

    namespace PrimeFacesTypes.widget {
        export type BreadCrumbCfg = import("./src/menu/breadcrumb.js").BreadCrumbCfg;
        export type ContextMenuCfg = import("./src/menu/contextmenu.js").ContextMenuCfg;
        export type MegaMenuCfg = import("./src/menu/megamenu.js").MegaMenuCfg;
        export type MenuCfg = import("./src/menu/menu.base.js").MenuCfg;
        export type MenubarCfg = import("./src/menu/menubar.js").MenubarCfg;
        export type MenuButtonCfg = import("./src/menu/menubutton.js").MenuButtonCfg;
        export type PanelMenuCfg = import("./src/menu/panelmenu.js").PanelMenuCfg;
        export type PlainMenuCfg = import("./src/menu/plainmenu.js").PlainMenuCfg;
        export type SlideMenuCfg = import("./src/menu/slidemenu.js").SlideMenuCfg;
        export type StepsCfg = import("./src/menu/steps.js").StepsCfg;
        export type TabMenuCfg = import("./src/menu/tabmenu.js").TabMenuCfg;
        export type TieredMenuCfg = import("./src/menu/tieredmenu.js").TieredMenuCfg;
    }

    namespace PrimeFacesTypes.widget.ContextMenu {
        /**
         * Client side callback invoked before the context menu is
         * shown.
         */
        export type BeforeShowCallback =
            /**
             * @param event Event that triggered the context menu to
             * be shown (e.g. a mouse click).
             * @return ` true` to show the context menu, `false` to
             * prevent is from getting displayed.
             */
            <Cfg extends ContextMenuCfg>(this: ContextMenu<Cfg>, event: JQuery.TriggeredEvent) => boolean;

        /**
         * Selection mode for the context, whether the user may select only
         * one or multiple items at the same time.
         */
        export type SelectionMode = "single" | "multiple";
    }

    namespace PrimeFacesTypes.widget.PlainMenu {
        /**
         * The direction in which to navigate, either to the next menu item
         * or to the previous menu item.
         */
        export type NavigationDirection = "prev" | "next";
    }

    namespace PrimeFacesTypes.widget.TieredMenu {
        /**
         * Allowed event types for toggling a tiered menu.
         */
        export type ToggleEvent = "hover" | "click";
    }
}

// Types (src/message(s))
declare global {
    namespace PrimeFacesTypes {
        export interface WidgetRegistry {
            Message: typeof Message;
            Messages: typeof Messages;
        }
    }
    namespace PrimeFacesTypes.widget {
        export type Message = import("./src/message/message.js").Message;
        export type Messages = import("./src/messages/messages.js").Messages;
    }
}

// Types (src/notificationbar)
declare global {
    namespace PrimeFacesTypes {
        export interface WidgetRegistry {
            NotificationBar: typeof NotificationBar;
        }
    }
    namespace PrimeFacesTypes.widget {
        export type NotificationBarCfg = import("./src/notificationbar/notificationbar.js").NotificationBarCfg;
    }
    namespace PrimeFacesTypes.widget.NotificationBar {
        /**
         * Possible values for the effect applied when the notification bar is shown or hidden.
         */
        export type Effect = "slide" | "fade" | "none";

        /**
         * Possible values for where the notification bar is shown.
         */
        export type Position = "top" | "bottom";

        /**
         * Possible values for speed of the effect when the notification bar is shown or hidden.
         */
        export type EffectSpeed = "fast" | "normal" | "slow";
    }
}

// Types (src/orderlist)
declare global {
    namespace PrimeFacesTypes {
        export interface WidgetRegistry {
            OrderList: typeof OrderList;
        }
    }
    namespace PrimeFacesTypes.widget {
        export type OrderListCfg = import("./src/orderlist/orderlist.js").OrderListCfg;
    }
}

// Types (src/outputpanel)
declare global {
    namespace PrimeFacesTypes {
        export interface WidgetRegistry {
            OutputPanel: typeof OutputPanel;
        }
    }
    namespace PrimeFacesTypes.widget {
        export type OutputPanelCfg = import("./src/outputpanel/outputpanel.js").OutputPanelCfg;
    }
    namespace PrimeFacesTypes.widget.OutputPanel {
        /**
         * When the deferred content is loaded: `load` directly after the page was loaded, `visible` once the panel
         * was scrolled into view.
         */
        export type DeferredMode = "load" | "visible";
    }
}

// Types (src/overlaypanel)
declare global {
    namespace PrimeFacesTypes {
        export interface WidgetRegistry {
            OverlayPanel: typeof OverlayPanel;
        }
    }
    namespace PrimeFacesTypes.widget {
        export type OverlayPanelCfg = import("./src/overlaypanel/overlaypanel.js").OverlayPanelCfg;
    }
    namespace PrimeFacesTypes.widget.OverlayPanel {
        /**
         * Called when the panel was hidden, with the overlay panel as `this`.
         */
        export type OnHideCallback = (this: OverlayPanel) => void;

        /**
         * Called when the panel was shown, with the overlay panel as `this`.
         */
        export type OnShowCallback = (this: OverlayPanel) => void;
    }
}

// Types (src/paginator)
declare global {
    namespace PrimeFacesTypes {
        export interface WidgetRegistry {
            Paginator: typeof Paginator;
        }
    }
    namespace PrimeFacesTypes.widget {
        export type PaginatorCfg = import("./src/paginator/paginator.js").PaginatorCfg;
    }
    namespace PrimeFacesTypes.widget.Paginator {
        /**
         * A pagination state, that is, a range of items that should be displayed.
         */
        export interface PaginationState {
            /** 0-based index of the first item on the current page. */
            first: number;
            /** The number of rows per page, the string `*` when "show all" was selected in the rows per page dropdown. */
            rows: number;
            /** The current page, 0-based index. */
            page: number;
        }

        /**
         * Called when the pagination state changes, with the paginator as `this` and the new values for the current page
         * and the rows per page count.
         */
        export type PaginateCallback = (this: Paginator, newState: PaginationState) => void;
    }
}

// Types (src/panel)
declare global {
    namespace PrimeFacesTypes {
        export interface WidgetRegistry {
            Panel: typeof Panel;
        }
    }
    namespace PrimeFacesTypes.widget {
        export type PanelCfg = import("./src/panel/panel.js").PanelCfg;
    }
    namespace PrimeFacesTypes.widget.Panel {
        /**
         * Orientation of the toggle effect: slide up and down, or left and right.
         */
        export type ToggleOrientation = "vertical" | "horizontal";
    }
}

// Types (src/picklist)
declare global {
    namespace PrimeFacesTypes {
        export interface WidgetRegistry {
            PickList: typeof PickList;
        }
    }
    namespace PrimeFacesTypes.widget {
        export type PickListCfg = import("./src/picklist/picklist.js").PickListCfg;
    }
    namespace PrimeFacesTypes.widget.PickList {
        /**
         * The type for the two lists comprising the pick list, i.e. whether a list contain the source or target items.
         */
        export type ListName = "source" | "target";

        /**
         * Indicates how an item was transferred from one list to the other.
         * - `command`: The item was transferred as a result of the user clicking one of the command buttons next to the
         *   lists.
         * - `dblclick`: The item was transferred as a result of a double click by the user.
         * - `dragdrop`: The item was transferred as a result of a drag&drop interaction by the user.
         * - `checkbox`: The item was transferred as a result of a checkbox click by the user.
         */
        export type TransferType = "command" | "dblclick" | "dragdrop" | "checkbox";

        /**
         * Available modes for filtering the options of a pick list. When `custom` is set, a `filterFunction` must be
         * specified.
         */
        export type FilterMatchMode = "startsWith" | "contains" | "endsWith" | "custom";

        /**
         * A function for filtering the options of a pick list box.
         * @param itemLabel The label of the currently selected text.
         * @param filterValue The value to search for.
         * @returns `true` if the item label matches the filter value, or `false` otherwise.
         */
        export type FilterFunction = (itemLabel: string, filterValue: string) => boolean;

        /**
         * Details about the pick list items that were transferred.
         */
        export interface TransferData {
            /** Items that were transferred from one list to the other. */
            items: JQuery;
            /** List from which the items were transferred. */
            from: JQuery;
            /** List to which the items were transferred. */
            to: JQuery;
            /** Type of the action that caused the items to be transferred. */
            type: TransferType;
        }

        /**
         * Called when items were transferred from one list to the other, with the pick list as `this`.
         */
        export type OnTransferCallback = (this: PickList, transferData: TransferData) => void;
    }
}

// Types (src/progressbar)
declare global {
    namespace PrimeFacesTypes {
        export interface WidgetRegistry {
            ProgressBar: typeof ProgressBar;
        }
    }
    namespace PrimeFacesTypes.widget {
        export type ProgressBarCfg = import("./src/progressbar/progressbar.js").ProgressBarCfg;
    }
}

// Types (src/rating)
declare global {
    namespace PrimeFacesTypes {
        export interface WidgetRegistry {
            Rating: typeof Rating;
        }
    }
    namespace PrimeFacesTypes.widget {
        export type RatingCfg = import("./src/rating/rating.js").RatingCfg;
    }
    namespace PrimeFacesTypes.widget.Rating {
        /**
         * Called when the user gives a rating, with the rating as `this` and the number of rated stars.
         */
        export type OnRateCallback = (this: Rating, currentNumberOfStars: number) => void;
    }
}

// Types (src/resizable)
declare global {
    namespace PrimeFacesTypes {
        export interface WidgetRegistry {
            Resizable: typeof Resizable;
        }
    }
    namespace PrimeFacesTypes.widget {
        export type ResizableCfg = import("./src/resizable/resizable.js").ResizableCfg;
    }
    namespace PrimeFacesTypes.widget.Resizable {
        /**
         * Client-side callback to execute during resizing, with the resizable as `this`. See also
         * {@link ResizableCfg.onResize}.
         */
        export type OnResizeCallback = (this: Resizable, event: JQuery.TriggeredEvent, ui: PrimeFacesTypes.interactions.ResizableUi) => void;

        /**
         * Client-side callback to execute when resizing begins, with the resizable as `this`. See also
         * {@link ResizableCfg.onStart}.
         */
        export type OnStartCallback = (this: Resizable, event: JQuery.TriggeredEvent, ui: PrimeFacesTypes.interactions.ResizableUi) => void;

        /**
         * Client-side callback to execute after resizing end, with the resizable as `this`. See also
         * {@link ResizableCfg.onStop}.
         */
        export type OnStopCallback = (this: Resizable, event: JQuery.TriggeredEvent, ui: PrimeFacesTypes.interactions.ResizableUi) => void;
    }
}

// Types (src/scrolltop)
declare global {
    namespace PrimeFacesTypes {
        export interface WidgetRegistry {
            ScrollTop: typeof ScrollTop;
        }
    }
    namespace PrimeFacesTypes.widget {
        export type ScrollTopCfg = import("./src/scrolltop/scrolltop.js").ScrollTopCfg;
    }
}

// Types (src/sidebar)
declare global {
    namespace PrimeFacesTypes {
        export interface WidgetRegistry {
            Sidebar: typeof Sidebar;
        }
    }
    namespace PrimeFacesTypes.widget {
        export type SidebarCfg = import("./src/sidebar/sidebar.js").SidebarCfg;
    }
    namespace PrimeFacesTypes.widget.Sidebar {
        /**
         * Called when the sidebar was hidden, with the sidebar as `this`. The arguments are always `undefined`.
         */
        export type OnHideCallback = (this: Sidebar, event?: JQuery.TriggeredEvent, ui?: unknown) => void;

        /**
         * Called when the sidebar was shown, with the sidebar as `this`.
         */
        export type OnShowCallback = (this: Sidebar) => void;
    }
}

// Types (src/slider)
declare global {
    namespace PrimeFacesTypes {
        export interface WidgetRegistry {
            Slider: typeof Slider;
        }
    }
    namespace PrimeFacesTypes.widget {
        export type SliderCfg = import("./src/slider/slider.js").SliderCfg;
        export type SliderEventData = import("./src/slider/slider.js").SliderEventData;
    }
}

// Types (src/speeddial)
declare global {
    namespace PrimeFacesTypes {
        export interface WidgetRegistry {
            SpeedDial: typeof SpeedDial;
        }
    }
    namespace PrimeFacesTypes.widget {
        export type SpeedDialCfg = import("./src/speeddial/speeddial.js").SpeedDialCfg;
    }
    namespace PrimeFacesTypes.widget.SpeedDial {
        /**
         * Direction in which the items open, the diagonal ones for the type `quarter-circle`.
         */
        export type OpeningDirection = "up" | "down" | "left" | "right" | "up-left" | "up-right" | "down-left" | "down-right";

        /**
         * How the items are arranged.
         */
        export type OpeningType = "linear" | "circle" | "semi-circle" | "quarter-circle";

        /**
         * Called when the button was clicked, with the speed dial as `this`.
         */
        export type OnClickCallback = (this: SpeedDial, event: JQuery.TriggeredEvent) => void;

        /**
         * Called when the items were closed, with the speed dial as `this`.
         */
        export type OnHideCallback = (this: SpeedDial) => void;

        /**
         * Called when the items were opened, with the speed dial as `this`.
         */
        export type OnShowCallback = (this: SpeedDial) => void;

        /**
         * Called when the items were opened or closed, with the speed dial as `this`.
         */
        export type OnVisibleChangeCallback = (this: SpeedDial, visible: boolean) => void;
    }
}

// Types (src/spinner)
declare global {
    namespace PrimeFacesTypes {
        export interface WidgetRegistry {
            Spinner: typeof Spinner;
        }
    }
    namespace PrimeFacesTypes.widget {
        export type SpinnerCfg = import("./src/spinner/spinner.js").SpinnerCfg;
    }
}

// Types (src/splitter)
declare global {
    namespace PrimeFacesTypes {
        export interface WidgetRegistry {
            Splitter: typeof Splitter;
        }
    }
    namespace PrimeFacesTypes.widget {
        export type SplitterCfg = import("./src/splitter/splitter.js").SplitterCfg;
    }
    namespace PrimeFacesTypes.widget.Splitter {
        /**
         * Orientation of the panels: side by side or on top of each other.
         */
        export type Layout = "horizontal" | "vertical";

        /**
         * Storage of the panel sizes of a stateful splitter.
         */
        export type StateStorage = "local" | "session";

        /**
         * Called when the resizing ended, with the splitter as `this` and the panel sizes in percent.
         */
        export type OnResizeEndCallback = (this: Splitter, panelSizes: number[]) => void;
    }
}

// Types (src/spotlight)
declare global {
    namespace PrimeFacesTypes {
        export interface WidgetRegistry {
            Spotlight: typeof Spotlight;
        }
    }
    namespace PrimeFacesTypes.widget {
        export type SpotlightCfg = import("./src/spotlight/spotlight.js").SpotlightCfg;
    }
}

// Types (src/staticmessage)
declare global {
    namespace PrimeFacesTypes {
        export interface WidgetRegistry {
            StaticMessage: typeof StaticMessage;
        }
    }
    namespace PrimeFacesTypes.widget {
        export type StaticMessageCfg = import("./src/staticmessage/staticmessage.js").StaticMessageCfg;
    }
}

// Types (src/sticky)
declare global {
    namespace PrimeFacesTypes {
        export interface WidgetRegistry {
            Sticky: typeof Sticky;
        }
    }
    namespace PrimeFacesTypes.widget {
        export type StickyCfg = import("./src/sticky/sticky.js").StickyCfg;
    }
    namespace PrimeFacesTypes.widget.Sticky {
        /**
         * The position and height of the target before it was fixed.
         */
        export interface InitialState {
            /** The height of the target. */
            height: number;
            /** The top position of the target. */
            top: number;
        }
    }
}

// Types (src/tabview)
declare global {
    namespace PrimeFacesTypes {
        export interface WidgetRegistry {
            TabView: typeof TabView;
        }
    }
    namespace PrimeFacesTypes.widget {
        export type TabViewCfg = import("./src/tabview/tabview.js").TabViewCfg;
    }
    namespace PrimeFacesTypes.widget.TabView {
        /**
         * Called when a tab is clicked, with the tab view as `this` and the 0-based index of the tab that is about to be
         * selected. `false` stays at the current tab.
         */
        export type OnTabChangeCallback = (this: TabView, index: number) => boolean | void;

        /**
         * Called when a tab is closed, with the tab view as `this` and the 0-based index of the tab that is about to be
         * closed. `false` keeps the tab open.
         */
        export type OnTabCloseCallback = (this: TabView, index: number) => boolean | void;

        /**
         * Called when a tab was shown, with the tab view as `this` and the 0-based index of the tab.
         */
        export type OnTabShowCallback = (this: TabView, index: number) => void;
    }
}

// Types (src/tagcloud)
declare global {
    namespace PrimeFacesTypes {
        export interface WidgetRegistry {
            TagCloud: typeof TagCloud;
        }
    }
    namespace PrimeFacesTypes.widget {
        export type TagCloudCfg = import("./src/tagcloud/tagcloud.js").TagCloudCfg;
    }
}

// Types (src/toggleswitch)
declare global {
    namespace PrimeFacesTypes {
        export interface WidgetRegistry {
            ToggleSwitch: typeof ToggleSwitch;
        }
    }
    namespace PrimeFacesTypes.widget {
        export type ToggleSwitchCfg = import("./src/toggleswitch/toggleswitch.js").ToggleSwitchCfg;
    }
}

// Types (src/tooltip)
declare global {
    namespace PrimeFacesTypes {
        export interface WidgetRegistry {
            Tooltip: typeof Tooltip;
        }
    }
    namespace PrimeFacesTypes.widget {
        export type TooltipCfg = import("./src/tooltip/tooltip.js").TooltipCfg;
    }
    namespace PrimeFacesTypes.widget.Tooltip {
        /**
         * Position of the tooltip, relative to the target component.
         */
        export type TooltipPosition = "right" | "left" | "top" | "bottom";

        /**
         * Called before the tooltip is shown, with the tooltip as `this`. `false` prevents the tooltip from being shown.
         */
        export type BeforeShowCallback = (this: Tooltip) => boolean | void;

        /**
         * Called after the tooltip was hidden. It is called without a `this` context.
         */
        export type OnHideCallback = () => void;

        /**
         * Called after the tooltip was shown. It is called without a `this` context.
         */
        export type OnShowCallback = () => void;
    }
}

// Types (src/tree)
declare global {
    namespace PrimeFacesTypes {
        export interface WidgetRegistry {
            BaseTree: typeof BaseTree;
            HorizontalTree: typeof HorizontalTree;
            VerticalTree: typeof VerticalTree;
        }
    }
    namespace PrimeFacesTypes.widget {
        export type BaseTreeCfg = import("./src/tree/tree.base.js").BaseTreeCfg;
        export type HorizontalTreeCfg = import("./src/tree/tree.horizontal.js").HorizontalTreeCfg;
        export type VerticalTreeCfg = import("./src/tree/tree.vertical.js").VerticalTreeCfg;
    }
    namespace PrimeFacesTypes.widget.BaseTree {
        /**
         * Drag mode for a tree widget. Defines the parent-child relationship when a node is dragged.
         */
        export type DragMode = "self" | "parent" | "ancestor";

        /**
         * Mode for filtering a tree widget.
         */
        export type FilterMode = "lenient" | "strict";

        /**
         * How the nodes of a tree are selected. When set to `single`, only at most one node can be selected by
         * clicking on it. When set to `multiple`, more than one node may be selected by clicking on each node. When set
         * to `checkbox`, each node receives a checkbox next to it that may be used for selection.
         */
        export type SelectionMode = "single" | "multiple" | "checkbox";

        /**
         * Callback that is invoked when a node is clicked, with the tree as `this`.
         * @param node The tree node that was clicked.
         * @param event The mouse click (or key) event that occurred.
         * @returns `false` to ignore the click, anything else to allow the node to be selected.
         */
        export type OnNodeClickCallback = (this: BaseTree, node: JQuery, event: JQuery.TriggeredEvent) => boolean | void;

        /**
         * A set of icons to be used for a certain node type.
         */
        export interface NodeIconSet {
            /** Icon to be used when the node is expanded. */
            expandedIcon: string;
            /** Icon to be used when the node is collapsed. */
            collapsedIcon: string;
        }
    }
    namespace PrimeFacesTypes.widget.VerticalTree {
        /**
         * Defines parent-child restrictions when a node is dropped.
         */
        export type DropRestrictMode = "none" | "sibling";

        /**
         * Whether to move or copy the node on drop.
         */
        export type DropMode = "move" | "copy";

        /**
         * Describes a drag & drop operation when a tree node is being dragged, see
         * {@link VerticalTree.droppedNodeParams}.
         */
        export interface DroppedNodeParams {
            /** Details about the drop event. */
            ui: PrimeFacesTypes.interactions.DroppableUi;
            /** Tree widget of the dragged node. */
            dragSource: VerticalTree;
            /** The node that was dragged. */
            dragNode: JQuery;
            /** The node that was the target of the drag. */
            targetDragNode: JQuery;
            /** The drop point where the node was dropped, when it was dropped on a drop point. */
            dropPoint?: JQuery;
            /** The droppable where the node was dropped, when it was dropped on a node. */
            droppable?: JQuery;
            /** The node on which the dragged node was dropped. */
            dropNode: JQuery;
            /** Whether a transfer should occur, i.e. whether the node was dropped on another tree. */
            transfer: boolean;
        }

        /**
         * Describes a drag & drop that occurred, see {@link VerticalTree.fireDragDropEvent}.
         */
        export interface DragDropEvent {
            /** Comma separated row keys of the dragged nodes. */
            dragNodeKey: string;
            /** Row key of the node on which the nodes were dropped. */
            dropNodeKey: string;
            /** ID of the tree widget of the dragged nodes. */
            dragSource: string | string[];
            /** Index of the dropped nodes within their new parent. */
            dndIndex: number | undefined;
            /** Whether the nodes were dropped on another tree. */
            transfer: boolean;
            /** Whether a copy of the nodes was dropped. */
            isDroppedNodeCopy: boolean | undefined;
        }
    }
}

// Types (src/treetable)
declare global {
    namespace PrimeFacesTypes {
        export interface WidgetRegistry {
            TreeTable: typeof TreeTable;
        }
    }
    namespace PrimeFacesTypes.widget {
        export type TreeTableCfg = import("./src/treetable/treetable.js").TreeTableCfg;
    }
    namespace PrimeFacesTypes.widget.TreeTable {
        /**
         * Indicates how a row may be selected.
         * - `single`: Only a single row may be selected at any time by clicking on it. Selecting another row will
         * unselect the currently selected row.
         * - `multiple`: Multiple rows can be selected via clicking while holding the ctrl or shift key.
         * - `checkbox`: One or more rows can be selected by clicking on the checkbox next to each row.
         */
        export type SelectionMode = "single" | "multiple" | "checkbox";

        /**
         * The available sort order types for the tree table, see {@link TreeTable.SORT_ORDER}.
         */
        export type SortOrder = "ASCENDING" | "DESCENDING" | "UNSORTED";

        /**
         * If cell editing mode is enabled, whether the cell editors are loaded lazily.
         * - `eager`: Cell editors are loaded with the original page load or when the tree table is loaded.
         * - `lazy`: Cell editors are loaded via AJAX when inline editing is requested.
         */
        export type CellEditMode = "eager" | "lazy";

        /**
         * How the data in a tree table can be edited.
         * - `row`: A row is switched to edit mode and all cells can be edited at once.
         * - `cell`: An individual cell is switched to edit mode and can be edited.
         */
        export type EditMode = "row" | "cell";

        /**
         * Defines which rows are expanded when the expand icon next to a row is clicked.
         * - `self`: Only the row itself is expanded.
         * - `children`: The row and its children are expanded.
         */
        export type ExpandMode = "children" | "self";

        /**
         * The resize behavior of the columns.
         * - `fit`: The width of the next column changes, the width of the table stays the same.
         * - `expand`: The width of the table changes.
         */
        export type ResizeMode = "fit" | "expand";

        /**
         * Describes a sorting operation of the tree table.
         */
        export interface SortMeta {
            /** ID of the column to sort by. */
            col: string;
            /** How to sort by the column, one of the values of {@link TreeTable.SORT_ORDER}. */
            order: number;
        }
    }
}

// Types (src/tristatecheckbox)
declare global {
    namespace PrimeFacesTypes {
        export interface WidgetRegistry {
            TriStateCheckbox: typeof TriStateCheckbox;
        }
    }
    namespace PrimeFacesTypes.widget {
        export type TriStateCheckboxCfg = import("./src/tristatecheckbox/tristatecheckbox.js").TriStateCheckboxCfg;
    }
    namespace PrimeFacesTypes.widget.TriStateCheckbox {
        /**
         * A modulo operation with the result being in the range `[0,mod)`.
         * @param number The dividend for the modulo operation.
         * @param mod The divisor for the modulo operation.
         * @returns The result of the modulo operation, in the range `[0,mod)`.
         */
        export type FixedMod = (number: number, mod: number) => number;
    }
}

// Types (src/wizard)
declare global {
    namespace PrimeFacesTypes {
        export interface WidgetRegistry {
            Wizard: typeof Wizard;
        }
    }
    namespace PrimeFacesTypes.widget {
        export type WizardCfg = import("./src/wizard/wizard.js").WizardCfg;
    }
    namespace PrimeFacesTypes.widget.Wizard {
        /**
         * Called before going to the previous step, with the wizard as `this`. `false` stays on the current step.
         */
        export type OnBackCallback = (this: Wizard) => boolean | void;

        /**
         * Called before going to the next step, with the wizard as `this`. `false` stays on the current step.
         */
        export type OnNextCallback = (this: Wizard) => boolean | void;
    }
}
