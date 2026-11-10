import autosize from "autosize";

import { InputText } from "./src/forms/forms.inputtext.widget.js";
import { InputTextarea } from "./src/forms/forms.inputtextarea.widget.js";
import "./src/forms/forms.selectonemenu.widget.js";
import { SelectOneRadio } from "./src/forms/forms.selectoneradio.widget.js";
import { SelectBooleanCheckbox } from "./src/forms/forms.selectbooleancheckbox.widget.js";
import { SelectManyCheckbox } from "./src/forms/forms.selectmanycheckbox.widget.js";
import { SelectListbox } from "./src/forms/forms.selectlistbox.widget.js";
import { SelectOneListbox } from "./src/forms/forms.selectonelistbox.widget.js";
import { SelectManyMenu } from "./src/forms/forms.selectmanymenu.widget.js";
import "./src/forms/forms.cascadeselect.widget.js";
import { CommandButton } from "./src/forms/forms.commandbutton.widget.js";
import { CommandLink } from "./src/forms/forms.commandlink.widget.js";
import { Button } from "./src/forms/forms.button.widget.js";
import { LinkButton } from "./src/forms/forms.linkbutton.widget.js";
import { SelectManyButton } from "./src/forms/forms.selectmanybutton.widget.js";
import { SelectOneButton } from "./src/forms/forms.selectonebutton.widget.js";
import { SelectBooleanButton } from "./src/forms/forms.selectbooleanbutton.widget.js";
import "./src/forms/forms.selectcheckboxmenu.widget.js";
import "./src/forms/forms.password.widget.js";
import { DefaultCommand } from "./src/forms/forms.defaultcommand.widget.js";
import { SplitButton } from "./src/forms/forms.splitbutton.widget.js";
import { MultiSelectListbox } from "./src/forms/forms.multiselectlistbox.widget.js";

import { BreadCrumb } from "./src/menu/menu.breadcrumb.widget.js";
import { ContextMenu } from "./src/menu/menu.contextmenu.widget.js";
import { MegaMenu } from "./src/menu/menu.megamenu.widget.js";
import { Menu } from "./src/menu/menu.base.widget.js";
import { Menubar } from "./src/menu/menu.menubar.widget.js";
import { MenuButton } from "./src/menu/menu.menubutton.widget.js";
import { PanelMenu } from "./src/menu/menu.panelmenu.widget.js";
import { PlainMenu } from "./src/menu/menu.plainmenu.widget.js";
import { SlideMenu } from "./src/menu/menu.slidemenu.widget.js";
import { Steps } from "./src/menu/menu.steps.widget.js";
import { TabMenu } from "./src/menu/menu.tabmenu.widget.js";
import { TieredMenu } from "./src/menu/menu.tieredmenu.widget.js";

import { dialog, registerDialogHooks, type Dialogs, type DialogHandler as _DialogHandler } from "./src/core.dialog.js";
import { ConfirmDialog, Dialog as _Dialog, DynamicDialog } from "./src/dialog/dialog.widget.js";

import { Growl, registerMessageRenderHookForGrowlWidget } from "./src/growl/growl.widget.js";
import { Message, registerMessageRenderHookForMessageWidget } from "./src/message/message.widget.js";
import { Messages, registerMessageRenderHookForMessagesWidget } from "./src/messages/messages.widget.js";
import { registerRipple } from "./src/ripple/ripple.js";
import { StaticMessage } from "./src/staticmessage/staticmessage.widget.js";

import { AccordionPanel } from "./src/accordion/accordion.widget.js";
import "./src/autocomplete/autocomplete.widget.js";
import { BlockUI } from "./src/blockui/blockui.widget.js";
import "./src/carousel/carousel.widget.js";
import { ConfirmPopup } from "./src/confirmpopup/confirmpopup.widget.js";
import "./src/columntoggler/columntoggler.widget.js";
import "./src/dashboard/dashboard.widget.js";
import "./src/datagrid/datagrid.widget.js";
import "./src/datalist/datalist.widget.js";
import { DataScroller } from "./src/datascroller/datascroller.widget.js";

import "./src/datatable/datatable.widget.js";
import "./src/datatable/datatable.frozen.widget.js";
import "./src/dragdrop/dragdrop.widget.js";
import { Effect } from "./src/effect/effect.widget.js";
import { Fieldset } from "./src/fieldset/fieldset.widget.js";
import { Inplace } from "./src/inplace/inplace.widget.js";
import "./src/notificationbar/notificationbar.widget.js";
import { Panel } from "./src/panel/panel.widget.js";
import "./src/orderlist/orderlist.widget.js";
import { OutputPanel } from "./src/outputpanel/outputpanel.widget.js";
import "./src/overlaypanel/overlaypanel.widget.js";
import "./src/paginator/paginator.widget.js";
import "./src/picklist/picklist.widget.js";
import { ProgressBar } from "./src/progressbar/progressbar.widget.js";
import "./src/rating/rating.widget.js";
import "./src/resizable/resizable.widget.js";
import { Slider } from "./src/slider/slider.widget.js";
import { Spinner } from "./src/spinner/spinner.widget.js";
import { Splitter } from "./src/splitter/splitter.widget.js";
import { Spotlight } from "./src/spotlight/spotlight.widget.js";
import { SpeedDial } from "./src/speeddial/speeddial.widget.js";
import { Sticky } from "./src/sticky/sticky.widget.js";
import "./src/tabview/tabview.widget.js";
import { TagCloud } from "./src/tagcloud/tagcloud.widget.js";
import "./src/tooltip/tooltip.widget.js";

import "./src/tree/tree.base.widget.js";
import "./src/tree/tree.vertical.widget.js";
import "./src/tree/tree.horizontal.widget.js";
import "./src/treetable/treetable.widget.js";

import { Wizard } from "./src/wizard/wizard.widget.js";
import "./src/tristatecheckbox/tristatecheckbox.widget.js";
import { Chip } from "./src/chip/chip.widget.js";
import { Chips } from "./src/chips/chips.widget.js";
import { ScrollTop } from "./src/scrolltop/scrolltop.widget.js";
import { Sidebar } from "./src/sidebar/sidebar.widget.js";
import "./src/dataview/dataview.widget.js";
import { ToggleSwitch } from "./src/toggleswitch/toggleswitch.widget.js";

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

    // src/blockui
    PrimeFaces.widget.BlockUI = BlockUI;

    // src/chip
    PrimeFaces.widget.Chip = Chip;

    // src/chips
    PrimeFaces.widget.Chips = Chips;

    // src/confirmpopup
    PrimeFaces.widget.ConfirmPopup = ConfirmPopup;

    // src/dialog
    PrimeFaces.dialog = dialog;
    PrimeFaces.widget.ConfirmDialog = ConfirmDialog;
    PrimeFaces.widget.Dialog = _Dialog;
    PrimeFaces.widget.DynamicDialog = DynamicDialog;

    // src/datascroller
    PrimeFaces.widget.DataScroller = DataScroller;

    // src/effect
    PrimeFaces.widget.Effect = Effect;

    // src/fieldset
    PrimeFaces.widget.Fieldset = Fieldset;

    // src/forms
    PrimeFaces.widget.Button = Button;
    PrimeFaces.widget.CommandButton = CommandButton;
    PrimeFaces.widget.CommandLink = CommandLink;
    PrimeFaces.widget.DefaultCommand = DefaultCommand;
    PrimeFaces.widget.InputText = InputText;
    PrimeFaces.widget.InputTextarea = InputTextarea;
    PrimeFaces.widget.LinkButton = LinkButton;
    PrimeFaces.widget.MultiSelectListbox = MultiSelectListbox;
    PrimeFaces.widget.SelectBooleanButton = SelectBooleanButton;
    PrimeFaces.widget.SelectBooleanCheckbox = SelectBooleanCheckbox;
    PrimeFaces.widget.SelectListbox = SelectListbox;
    PrimeFaces.widget.SelectManyButton = SelectManyButton;
    PrimeFaces.widget.SelectManyCheckbox = SelectManyCheckbox;
    PrimeFaces.widget.SelectManyMenu = SelectManyMenu;
    PrimeFaces.widget.SelectOneButton = SelectOneButton;
    PrimeFaces.widget.SelectOneListbox = SelectOneListbox;
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

    // src/outputpanel
    PrimeFaces.widget.OutputPanel = OutputPanel;

    // src/panel
    PrimeFaces.widget.Panel = Panel;

    // src/progressbar
    PrimeFaces.widget.ProgressBar = ProgressBar;

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

    // src/tagcloud
    PrimeFaces.widget.TagCloud = TagCloud;

    // src/toggleswitch
    PrimeFaces.widget.ToggleSwitch = ToggleSwitch;

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
        export type AccordionPanelCfg = import("./src/accordion/accordion.widget.js").AccordionPanelCfg;
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

// Types (src/blockui)
declare global {
    namespace PrimeFacesTypes {
        export interface WidgetRegistry {
            BlockUI: typeof BlockUI;
        }
    }
    namespace PrimeFacesTypes.widget {
        export type BlockUICfg = import("./src/blockui/blockui.widget.js").BlockUICfg;
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
        export type ChipCfg = import("./src/chip/chip.widget.js").ChipCfg;
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
        export type ChipsCfg = import("./src/chips/chips.widget.js").ChipsCfg;
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
        export type ConfirmPopupCfg = import("./src/confirmpopup/confirmpopup.widget.js").ConfirmPopupCfg;
    }

    namespace PrimeFacesTypes.widget.ConfirmPopup {
        /**
         * Callback invoked after the popup is hidden.
         */
        export type HideCallback = () => void;
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
        export type ConfirmDialogCfg = import("./src/dialog/dialog.widget.js").ConfirmDialogCfg;
        export type DialogCfg = import("./src/dialog/dialog.widget.js").DialogCfg;
        export type DynamicDialogCfg = import("./src/dialog/dialog.widget.js").DynamicDialogCfg;
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
        export type DataScrollerCfg = import("./src/datascroller/datascroller.widget.js").DataScrollerCfg;
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

// Types (src/effect)
declare global {
    namespace PrimeFacesTypes {
        export interface WidgetRegistry {
            Effect: typeof Effect;
        }
    }
    namespace PrimeFacesTypes.widget {
        export type EffectCfg = import("./src/effect/effect.widget.js").EffectCfg;
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
        export type FieldsetCfg = import("./src/fieldset/fieldset.widget.js").FieldsetCfg;
    }
}

// Types (src/forms)
declare global {
    namespace PrimeFacesTypes {
        export interface WidgetRegistry {
            Button: typeof Button;
            CommandButton: typeof CommandButton;
            CommandLink: typeof CommandLink;
            DefaultCommand: typeof DefaultCommand;
            InputText: typeof InputText;
            InputTextarea: typeof InputTextarea;
            LinkButton: typeof LinkButton;
            MultiSelectListbox: typeof MultiSelectListbox;
            SelectBooleanButton: typeof SelectBooleanButton;
            SelectBooleanCheckbox: typeof SelectBooleanCheckbox;
            SelectListbox: typeof SelectListbox;
            SelectManyButton: typeof SelectManyButton;
            SelectManyCheckbox: typeof SelectManyCheckbox;
            SelectManyMenu: typeof SelectManyMenu;
            SelectOneButton: typeof SelectOneButton;
            SelectOneListbox: typeof SelectOneListbox;
            SelectOneRadio: typeof SelectOneRadio;
            SplitButton: typeof SplitButton;
        }
    }

    namespace PrimeFacesTypes.widget {
        export type ButtonCfg = import("./src/forms/forms.button.widget.js").ButtonCfg;
        export type CommandButtonCfg = import("./src/forms/forms.commandbutton.widget.js").CommandButtonCfg;
        export type CommandLinkCfg = import("./src/forms/forms.commandlink.widget.js").CommandLinkCfg;
        export type DefaultCommandCfg = import("./src/forms/forms.defaultcommand.widget.js").DefaultCommandCfg;
        export type InputTextCfg = import("./src/forms/forms.inputtext.widget.js").InputTextCfg;
        export type InputTextareaCfg = import("./src/forms/forms.inputtextarea.widget.js").InputTextareaCfg;
        export type LinkButtonCfg = import("./src/forms/forms.linkbutton.widget.js").LinkButtonCfg;
        export type MultiSelectListboxCfg = import("./src/forms/forms.multiselectlistbox.widget.js").MultiSelectListboxCfg;
        export type SelectBooleanButtonCfg = import("./src/forms/forms.selectbooleanbutton.widget.js").SelectBooleanButtonCfg;
        export type SelectBooleanCheckboxCfg = import("./src/forms/forms.selectbooleancheckbox.widget.js").SelectBooleanCheckboxCfg;
        export type SelectListboxCfg = import("./src/forms/forms.selectlistbox.widget.js").SelectListboxCfg;
        export type SelectManyButtonCfg = import("./src/forms/forms.selectmanybutton.widget.js").SelectManyButtonCfg;
        export type SelectManyCheckboxCfg = import("./src/forms/forms.selectmanycheckbox.widget.js").SelectManyCheckboxCfg;
        export type SelectManyMenuCfg = import("./src/forms/forms.selectmanymenu.widget.js").SelectManyMenuCfg;
        export type SelectOneButtonCfg = import("./src/forms/forms.selectonebutton.widget.js").SelectOneButtonCfg;
        export type SelectOneListboxCfg = import("./src/forms/forms.selectonelistbox.widget.js").SelectOneListboxCfg;
        export type SelectOneRadioCfg = import("./src/forms/forms.selectoneradio.widget.js").SelectOneRadioCfg;
        export type SplitButtonCfg = import("./src/forms/forms.splitbutton.widget.js").SplitButtonCfg;
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
        export type GrowlCfg = import("./src/growl/growl.widget.js").GrowlCfg;
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
        export type InplaceCfg = import("./src/inplace/inplace.widget.js").InplaceCfg;
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
        export type BreadCrumbCfg = import("./src/menu/menu.breadcrumb.widget.js").BreadCrumbCfg;
        export type ContextMenuCfg = import("./src/menu/menu.contextmenu.widget.js").ContextMenuCfg;
        export type MegaMenuCfg = import("./src/menu/menu.megamenu.widget.js").MegaMenuCfg;
        export type MenuCfg = import("./src/menu/menu.base.widget.js").MenuCfg;
        export type MenubarCfg = import("./src/menu/menu.menubar.widget.js").MenubarCfg;
        export type MenuButtonCfg = import("./src/menu/menu.menubutton.widget.js").MenuButtonCfg;
        export type PanelMenuCfg = import("./src/menu/menu.panelmenu.widget.js").PanelMenuCfg;
        export type PlainMenuCfg = import("./src/menu/menu.plainmenu.widget.js").PlainMenuCfg;
        export type SlideMenuCfg = import("./src/menu/menu.slidemenu.widget.js").SlideMenuCfg;
        export type StepsCfg = import("./src/menu/menu.steps.widget.js").StepsCfg;
        export type TabMenuCfg = import("./src/menu/menu.tabmenu.widget.js").TabMenuCfg;
        export type TieredMenuCfg = import("./src/menu/menu.tieredmenu.widget.js").TieredMenuCfg;
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
        export type Message = import("./src/message/message.widget.js").Message;
        export type Messages = import("./src/messages/messages.widget.js").Messages;
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
        export type OutputPanelCfg = import("./src/outputpanel/outputpanel.widget.js").OutputPanelCfg;
    }
    namespace PrimeFacesTypes.widget.OutputPanel {
        /**
         * When the deferred content is loaded: `load` directly after the page was loaded, `visible` once the panel
         * was scrolled into view.
         */
        export type DeferredMode = "load" | "visible";
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
        export type PanelCfg = import("./src/panel/panel.widget.js").PanelCfg;
    }
    namespace PrimeFacesTypes.widget.Panel {
        /**
         * Orientation of the toggle effect: slide up and down, or left and right.
         */
        export type ToggleOrientation = "vertical" | "horizontal";
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
        export type ProgressBarCfg = import("./src/progressbar/progressbar.widget.js").ProgressBarCfg;
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
        export type ScrollTopCfg = import("./src/scrolltop/scrolltop.widget.js").ScrollTopCfg;
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
        export type SidebarCfg = import("./src/sidebar/sidebar.widget.js").SidebarCfg;
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
        export type SliderCfg = import("./src/slider/slider.widget.js").SliderCfg;
        export type SliderEventData = import("./src/slider/slider.widget.js").SliderEventData;
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
        export type SpeedDialCfg = import("./src/speeddial/speeddial.widget.js").SpeedDialCfg;
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
        export type SpinnerCfg = import("./src/spinner/spinner.widget.js").SpinnerCfg;
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
        export type SplitterCfg = import("./src/splitter/splitter.widget.js").SplitterCfg;
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
        export type SpotlightCfg = import("./src/spotlight/spotlight.widget.js").SpotlightCfg;
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
        export type StaticMessageCfg = import("./src/staticmessage/staticmessage.widget.js").StaticMessageCfg;
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
        export type StickyCfg = import("./src/sticky/sticky.widget.js").StickyCfg;
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

// Types (src/tagcloud)
declare global {
    namespace PrimeFacesTypes {
        export interface WidgetRegistry {
            TagCloud: typeof TagCloud;
        }
    }
    namespace PrimeFacesTypes.widget {
        export type TagCloudCfg = import("./src/tagcloud/tagcloud.widget.js").TagCloudCfg;
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
        export type ToggleSwitchCfg = import("./src/toggleswitch/toggleswitch.widget.js").ToggleSwitchCfg;
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
        export type WizardCfg = import("./src/wizard/wizard.widget.js").WizardCfg;
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
