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

import { Growl, registerMessageRenderHookForGrowlComponent } from "./src/growl/growl.js";
import { Message, registerMessageRenderHookForMessageComponent } from "./src/message/message.js";
import { Messages, registerMessageRenderHookForMessagesComponent } from "./src/messages/messages.js";
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

// Add components and dialog to the PrimeFaces global object
exposeToGlobalScope();

function exposeToGlobalScope() {
    if (PrimeFaces.dialog) {
        return;
    }

    // Register hook contributions from this "components" module
    registerDialogHooks();
    registerMessageRenderHookForGrowlComponent();
    registerMessageRenderHookForMessageComponent()
    registerMessageRenderHookForMessagesComponent();
    registerRipple();
    
    // Expose autosize to the global scope
    Object.assign(window, { autosize });

    // Expose components to the global scope

    // src/accordion
    PrimeFaces.component.AccordionPanel = AccordionPanel;

    // src/autocomplete
    PrimeFaces.component.AutoComplete = AutoComplete;

    // src/blockui
    PrimeFaces.component.BlockUI = BlockUI;

    // src/carousel
    PrimeFaces.component.Carousel = Carousel;

    // src/chip
    PrimeFaces.component.Chip = Chip;

    // src/chips
    PrimeFaces.component.Chips = Chips;

    // src/columntoggler
    PrimeFaces.component.ColumnToggler = ColumnToggler;

    // src/confirmpopup
    PrimeFaces.component.ConfirmPopup = ConfirmPopup;

    // src/dashboard
    PrimeFaces.component.Dashboard = Dashboard;

    // src/datagrid
    PrimeFaces.component.DataGrid = DataGrid;

    // src/datalist
    PrimeFaces.component.DataList = DataList;

    // src/datatable
    PrimeFaces.component.DataTable = DataTable;
    PrimeFaces.component.FrozenDataTable = FrozenDataTable;

    // src/dataview
    PrimeFaces.component.DataView = DataView;

    // src/dialog
    PrimeFaces.dialog = dialog;
    PrimeFaces.component.ConfirmDialog = ConfirmDialog;
    PrimeFaces.component.Dialog = _Dialog;
    PrimeFaces.component.DynamicDialog = DynamicDialog;

    // src/datascroller
    PrimeFaces.component.DataScroller = DataScroller;

    // src/dragdrop
    PrimeFaces.component.Draggable = Draggable;
    PrimeFaces.component.Droppable = Droppable;

    // src/effect
    PrimeFaces.component.Effect = Effect;

    // src/fieldset
    PrimeFaces.component.Fieldset = Fieldset;

    // src/forms
    PrimeFaces.component.Button = Button;
    PrimeFaces.component.CascadeSelect = CascadeSelect;
    PrimeFaces.component.CommandButton = CommandButton;
    PrimeFaces.component.CommandLink = CommandLink;
    PrimeFaces.component.DefaultCommand = DefaultCommand;
    PrimeFaces.component.InputText = InputText;
    PrimeFaces.component.InputTextarea = InputTextarea;
    PrimeFaces.component.LinkButton = LinkButton;
    PrimeFaces.component.MultiSelectListbox = MultiSelectListbox;
    PrimeFaces.component.Password = Password;
    PrimeFaces.component.SelectBooleanButton = SelectBooleanButton;
    PrimeFaces.component.SelectBooleanCheckbox = SelectBooleanCheckbox;
    PrimeFaces.component.SelectCheckboxMenu = SelectCheckboxMenu;
    PrimeFaces.component.SelectListbox = SelectListbox;
    PrimeFaces.component.SelectManyButton = SelectManyButton;
    PrimeFaces.component.SelectManyCheckbox = SelectManyCheckbox;
    PrimeFaces.component.SelectManyMenu = SelectManyMenu;
    PrimeFaces.component.SelectOneButton = SelectOneButton;
    PrimeFaces.component.SelectOneListbox = SelectOneListbox;
    PrimeFaces.component.SelectOneMenu = SelectOneMenu;
    PrimeFaces.component.SelectOneRadio = SelectOneRadio;
    PrimeFaces.component.SplitButton = SplitButton;

    // src/growl
    PrimeFaces.component.Growl = Growl;

    // src/inplace
    PrimeFaces.component.Inplace = Inplace;

    // src/menu
    PrimeFaces.component.BreadCrumb = BreadCrumb;
    PrimeFaces.component.ContextMenu = ContextMenu;
    PrimeFaces.component.MegaMenu = MegaMenu;
    PrimeFaces.component.Menu = Menu;
    PrimeFaces.component.MenuButton = MenuButton;
    PrimeFaces.component.Menubar = Menubar;
    PrimeFaces.component.PanelMenu = PanelMenu;
    PrimeFaces.component.PlainMenu = PlainMenu;
    PrimeFaces.component.SlideMenu = SlideMenu;
    PrimeFaces.component.Steps = Steps;
    PrimeFaces.component.TabMenu = TabMenu;
    PrimeFaces.component.TieredMenu = TieredMenu;

    // src/message(s)
    PrimeFaces.component.Message = Message;
    PrimeFaces.component.Messages = Messages;

    // src/notificationbar
    PrimeFaces.component.NotificationBar = NotificationBar;

    // src/orderlist
    PrimeFaces.component.OrderList = OrderList;

    // src/outputpanel
    PrimeFaces.component.OutputPanel = OutputPanel;

    // src/overlaypanel
    PrimeFaces.component.OverlayPanel = OverlayPanel;

    // src/paginator
    PrimeFaces.component.Paginator = Paginator;

    // src/panel
    PrimeFaces.component.Panel = Panel;

    // src/picklist
    PrimeFaces.component.PickList = PickList;

    // src/progressbar
    PrimeFaces.component.ProgressBar = ProgressBar;

    // src/rating
    PrimeFaces.component.Rating = Rating;

    // src/resizable
    PrimeFaces.component.Resizable = Resizable;

    // src/scrolltop
    PrimeFaces.component.ScrollTop = ScrollTop;

    // src/sidebar
    PrimeFaces.component.Sidebar = Sidebar;

    // src/slider
    PrimeFaces.component.Slider = Slider;

    // src/speeddial
    PrimeFaces.component.SpeedDial = SpeedDial;

    // src/spinner
    PrimeFaces.component.Spinner = Spinner;

    // src/splitter
    PrimeFaces.component.Splitter = Splitter;

    // src/spotlight
    PrimeFaces.component.Spotlight = Spotlight;

    // src/staticmessage
    PrimeFaces.component.StaticMessage = StaticMessage;

    // src/sticky
    PrimeFaces.component.Sticky = Sticky;

    // src/tabview
    PrimeFaces.component.TabView = TabView;

    // src/tagcloud
    PrimeFaces.component.TagCloud = TagCloud;

    // src/toggleswitch
    PrimeFaces.component.ToggleSwitch = ToggleSwitch;

    // src/tooltip
    PrimeFaces.component.Tooltip = Tooltip;

    // src/tree
    PrimeFaces.component.BaseTree = BaseTree;
    PrimeFaces.component.HorizontalTree = HorizontalTree;
    PrimeFaces.component.VerticalTree = VerticalTree;

    // src/treetable
    PrimeFaces.component.TreeTable = TreeTable;

    // src/tristatecheckbox
    PrimeFaces.component.TriStateCheckbox = TriStateCheckbox;

    // src/wizard
    PrimeFaces.component.Wizard = Wizard;
}

// Global types
declare global {
    namespace PrimeFaces {
        interface WindowExtensions {
            autosize: typeof autosize;
        }

        export interface Core {
            confirmPopup?: ConfirmPopup;
            confirmPopupSource?: JQuery | null;
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

        export type Dialog = Dialogs;

        export interface ComponentRegistry {
            AccordionPanel: typeof AccordionPanel;
            AutoComplete: typeof AutoComplete;
            BlockUI: typeof BlockUI;
            Carousel: typeof Carousel;
            Chip: typeof Chip;
            Chips: typeof Chips;
            ColumnToggler: typeof ColumnToggler;
            ConfirmPopup: typeof ConfirmPopup;
            Dashboard: typeof Dashboard;
            DataGrid: typeof DataGrid;
            DataList: typeof DataList;
            DataTable: typeof DataTable;
            FrozenDataTable: typeof FrozenDataTable;
            DataView: typeof DataView;
            ConfirmDialog: typeof ConfirmDialog;
            Dialog: typeof _Dialog;
            DynamicDialog: typeof DynamicDialog;
            DataScroller: typeof DataScroller;
            Draggable: typeof Draggable;
            Droppable: typeof Droppable;
            Effect: typeof Effect;
            Fieldset: typeof Fieldset;
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
            Growl: typeof Growl;
            Inplace: typeof Inplace;
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
            Message: typeof Message;
            Messages: typeof Messages;
            NotificationBar: typeof NotificationBar;
            OrderList: typeof OrderList;
            OutputPanel: typeof OutputPanel;
            OverlayPanel: typeof OverlayPanel;
            Paginator: typeof Paginator;
            Panel: typeof Panel;
            PickList: typeof PickList;
            ProgressBar: typeof ProgressBar;
            Rating: typeof Rating;
            Resizable: typeof Resizable;
            ScrollTop: typeof ScrollTop;
            Sidebar: typeof Sidebar;
            Slider: typeof Slider;
            SpeedDial: typeof SpeedDial;
            Spinner: typeof Spinner;
            Splitter: typeof Splitter;
            Spotlight: typeof Spotlight;
            StaticMessage: typeof StaticMessage;
            Sticky: typeof Sticky;
            TabView: typeof TabView;
            TagCloud: typeof TagCloud;
            ToggleSwitch: typeof ToggleSwitch;
            Tooltip: typeof Tooltip;
            BaseTree: typeof BaseTree;
            HorizontalTree: typeof HorizontalTree;
            VerticalTree: typeof VerticalTree;
            TreeTable: typeof TreeTable;
            TriStateCheckbox: typeof TriStateCheckbox;
            Wizard: typeof Wizard;
        }
    }

    namespace PrimeFaces.dialog {
        export type DialogHandler = _DialogHandler;
    }
}
