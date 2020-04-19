/**
 * Namespace for the jQuery UI layout plugin.
 * 
 * This plug-in was inspired by the extJS border-layout, and recreates that functionality as a jQuery plug-in. The UI
 * Layout plug-in can create any UI look you want - from simple headers or sidebars, to a complex application with
 * toolbars, menus, help-panels, status bars, sub-forms, etc.
 * 
 * Combine it with other jQuery UI widgets to create a sophisticated application. There are no limitations or issues -
 * this widget is ready for production use. All feedback and requests are welcome as development is ongoing. If you
 * create a good looking application using UI Layout, please let us know.
 * 
 * See http://layout.jquery-dev.com/index.cfm and https://github.com/GedMarc/layout
 */
declare namespace JQueryLayout {

    /**
     * Names for the four border panes, used as an identifier in various settings and methods. 
     */
    export type BorderPane = "north" | "east" | "south" | "west";

    /**
     * Name of the center pane, used as an identifier in various settings and methods. 
     */
    export type CenterPane = "center";

    /**
     * A callback specifier for the callbacks offered by the layout widget. Either the name of a function that is
     * available on the global window object, or the callback function itself.
     */

    /**
     * The `start` callback fires BEFORE the event starts, so for exapmle, `onopen_start` fires before the pane starts
     * to open.
     * 
     * If a `start` callback function returns `false`, the event will be cancelled.
     * 
     * __NOTE__: If an event is 'automatically triggered' by layout logic – like closing a pane when there is
     * insufficient room – then the event cannot be cancelled. In this case, returning false will have no effect.
     */
    export type OnStartCallback =
        /**
         * @param paneName Type of the pane where the event was triggered.
         * @param paneElement The DOM element of the pane where the event was triggered.
         * @param paneState The state of the pane where the event was triggered.
         * @param paneOptions The options of the pane where the event was triggered.
         * @param layoutName If a `name` was specified when creating the layout, else returns an empty string.
         * @return `true` to cancel the event and further actions, `false` or `undefined` otherwise.
         */
        (paneName: BorderPane, paneElement: JQuery, paneState: BorderPaneState, paneOptions: BorderSubKeyLayoutSettings, layoutName: string) => boolean | undefined;

    /**
     * The `end` callback fires AFTER the event completes. So for example, `onopen_end` will fire after the pane has
     * opened, including the completion of all animations.
     * 
     * Callback options without a suffix are really `end` callbacks, so `onopen` is the same as `onopen_end`. These
     * options exist for backwards compatibility, and for simpler code, since most of the time it will be the `end`
     * callbacks you will use.
     * 
     * __NOTE__: If BOTH `onopen` and `onopen_end` options are set, the `onopen_end` option takes precidence and the
     * `onopen` option is ignored.
     */
    export type OnEndCallback =
        /**
         * @param paneName Type of the pane where the event was triggered.
         * @param paneElement The DOM element of the pane where the event was triggered.
         * @param paneState The state of the pane where the event was triggered.
         * @param paneOptions The options of the pane where the event was triggered.
         * @param layoutName If a `name` was specified when creating the layout, else returns an empty string.
         */
        (paneName: BorderPane, paneElement: JQuery, paneState: BorderPaneState, paneOptions: BorderSubKeyLayoutSettings, layoutName: string) => void;

    /**
     * Settings for the `fxSettings` option. Lets you specify the settings for the default jQuery UI effects.
     */
    export type FxSettings = JQueryUI.EffectOptions
        | JQueryUI.BlindEffect
        | JQueryUI.BounceEffect
        | JQueryUI.ClipEffect
        | JQueryUI.DropEffect
        | JQueryUI.ExplodeEffect
        | JQueryUI.FadeEffect
        | JQueryUI.FoldEffect
        | JQueryUI.HighlightEffect
        | JQueryUI.PuffEffect
        | JQueryUI.PulsateEffect
        | JQueryUI.ScaleEffect
        | JQueryUI.ShakeEffect
        | JQueryUI.SizeEffect
        | JQueryUI.SlideEffect
        | JQueryUI.TransferEffect;

    /**
     * Settings for the `effects` options. Lets you defined the settings for the various different effects offered by
     * JQuery UI.
     */
    export interface EffectSettings {
        /**
         * Settings for the blind effect. 
         */
        blind: ByPaneEffectSettings<JQueryUI.BlindEffect>;

        /**
         * Settings for the bounce effect. 
         */
        bounce: ByPaneEffectSettings<JQueryUI.BounceEffect>;

        /**
         * Settings for the clip effect. 
         */
        clip: ByPaneEffectSettings<JQueryUI.ClipEffect>;

        /**
         * Settings for the drop effect. 
         */
        drop: ByPaneEffectSettings<JQueryUI.DropEffect>;

        /**
         * Settings for the explode effect. 
         */
        explode: ByPaneEffectSettings<JQueryUI.ExplodeEffect>;

        /**
         * Settings for the fade effect. 
         */
        fade: ByPaneEffectSettings<JQueryUI.FadeEffect>;

        /**
         * Settings for the fold effect. 
         */
        fold: ByPaneEffectSettings<JQueryUI.FoldEffect>;

        /**
         * Settings for the highlight effect. 
         */
        highlight: ByPaneEffectSettings<JQueryUI.HighlightEffect>;

        /**
         * Settings for the puff effect. 
         */
        puff: ByPaneEffectSettings<JQueryUI.PuffEffect>;

        /**
         * Settings for the pulsate effect. 
         */
        pulsate: ByPaneEffectSettings<JQueryUI.PulsateEffect>;

        /**
         * Settings for the scale effect. 
         */
        scale: ByPaneEffectSettings<JQueryUI.ScaleEffect>;

        /**
         * Settings for the shake effect. 
         */
        shake: ByPaneEffectSettings<JQueryUI.ShakeEffect>;

        /**
         * Settings for the size effect. 
         */
        size: ByPaneEffectSettings<JQueryUI.SizeEffect>;

        /**
         * Settings for the slide effect. 
         */
        slide: ByPaneEffectSettings<JQueryUI.SlideEffect>;

        /**
         * Settings for the transfer effect. 
         */
        transfer: ByPaneEffectSettings<JQueryUI.TransferEffect>;
    }

    /**
     * Settings for the `effects` option that may be specified globally for all panes of separately for each pane.
     */
    export interface ByPaneEffectSettings<T> {
        /**
         * Effect options for all panes. 
         */
        all?: Partial<T & JQueryUI.EffectOptions>;

        /**
         * Effect options for the east pane. 
         */
        east?: Partial<T & JQueryUI.EffectOptions>;

        /**
         * Effect options for the north pane. 
         */
        north?: Partial<T & JQueryUI.EffectOptions>;

        /**
         * Effect options for the south pane. 
         */
        south?: Partial<T & JQueryUI.EffectOptions>;

        /**
         * Effect options for the west pane. 
         */
        west?: Partial<T & JQueryUI.EffectOptions>;
    }

    /**
     * Possible values for a position, such as the alignment of the toggler button. 
     */
    export type PositionKeyword = "left" | "center" | "right" | "top" | "middle" | "bottom";

    /**
     * Sub key version of the settings for the layout widget. Contains a set of options for each type of pane.
     */
    export interface SubKeyLayoutSettings {
        /**
         * Default settings that apply to each pane. Can be overriden for individual panes. 
         */
        defaults: Partial<DefaultSubKeyLayoutSettings>;

        /**
         * Settings for the north pane. 
         */
        north: Partial<BorderSubKeyLayoutSettings>;

        /**
         * Settings for the south pane. 
         */
        south: Partial<BorderSubKeyLayoutSettings>;

        /**
         * Settings for the east pane. 
         */
        east: Partial<BorderSubKeyLayoutSettings>;

        /**
         * Settings for the west pane. 
         */
        west: Partial<BorderSubKeyLayoutSettings>;

        /**
         * Settings for the center pane. 
         */
        center: Partial<CenterSubKeyLayoutSettings>;
    }

    /**
     * List version of the settings for the layout widget. The type of pane is added as a prefix to each option name.
     */
    export interface ListLayoutSettings {
        /**
         * When this is enabled, the layout will apply basic styles directly to resizers & buttons. This is intended for
         * quick mock-ups, so that you can `see` your layout immediately. Normally this should be set as a default
         * option, but it can be set 'per-pane':
         */
        applyDefaultStyles: boolean;

        /**
         * If `true`, then when moused-over, the pane's zIndex is raised and overflow is set to `visible`. This allows
         * pop-ups and drop-downs to overlap adjacent panes.
         * 
         * __WARNING__: Enable this only for panes that do not scroll!
         */
        showOverflowOnHover: boolean;

        /**
         * MUST be a `child` of one of the panes.
         * 
         * Selector string for INNER div/element. This div will auto-size so only it scrolls, and not the entire pane.
         * 
         * Same class-name could be used for divs inside all panes.
         */
        contentSelector: string;

        /**
         * Selector string for INNER divs/elements. These elements will be `ignored` when calculations are done to
         * auto-size the content element. This may be necessary if there are elements inside the pane that are
         * absolutely-positioned and intended to `overlay` other elements.
         * 
         * Same class-name could be used for elements inside all panes
         */
        contentIgnoreSelector: string;

        /**
         * Used for auto-generated classNames for each 'layout pane'. Defaults to `ui-layout-pane`.
         */
        paneClass: string;

        /**
         * This is used as a prefix when generating classNames for 'custom buttons'. Do not confuse with normal
         * 'toggler-buttons'. Defaults to `ui-layout-button`.
         */
        buttonClass: string;

      /**
         * This option handles of bookmarks that are passed on the URL of the page:
         * http://www.site.com/page.html#myBookmark
         * 
         * The default functionality of bookmarks is broken when using a layout because the position and scrolling of
         * pane elements are altered after the page loads. Enabling this option (enabled by default) causes the bookmark
         * to be re-applied after the layout is created, causing the `pane` containing the bookmark/anchor to be
         * scrolled to bring it into view.
         * 
         * This option should be left enabled in most cases, but if content in the layout-panes is never bookmarked,
         * then you could disabled it.
         */
        scrollToBookmarkOnLoad: boolean;

        /**
         * Extends the `default` layout effects by specifying the options for the desired effect:
         * 
         * ```javascript
         * $("body").layout({
         *   effects: {
         *     // MODIFY a standard effect
         *     slide: {
         *       all: { duration: 500, easing: "bounceInOut" },
         *     },
         *     // ADD a new effect
         *     blind: {
         *       all: {},
         *       north: { direction: "vertical" },
         *       south: { direction: "vertical" },
         *       east:  { direction: "horizontal" },
         *       west:  { direction: "horizontal" },
         *     }
         *   }
         * });
         * ```
         */
        effects: Partial<EffectSettings>;

        /**
         * When this is enabled, the layout will apply basic styles directly to resizers & buttons. This is intended for
         * quick mock-ups, so that you can `see` your layout immediately. Normally this should be set as a default
         * option, but it can be set 'per-pane':
         */
        center__applyDefaultStyles: boolean;

        /**
         * If `true`, then when moused-over, the pane's zIndex is raised and overflow is set to `visible`. This allows
         * pop-ups and drop-downs to overlap adjacent panes.
         * 
         * __WARNING__: Enable this only for panes that do not scroll!
         */
        center__showOverflowOnHover: boolean;

        /**
         * MUST be a `child` of one of the panes.
         * 
         * Selector string for INNER div/element. This div will auto-size so only it scrolls, and not the entire pane.
         * 
         * Same class-name could be used for divs inside all panes.
         */
        center__contentSelector: string;

        /**
         * Selector string for INNER divs/elements. These elements will be `ignored` when calculations are done to
         * auto-size the content element. This may be necessary if there are elements inside the pane that are
         * absolutely-positioned and intended to `overlay` other elements.
         * 
         * Same class-name could be used for elements inside all panes
         */
        center__contentIgnoreSelector: string;

        /**
         * Used for auto-generated classNames for each 'layout pane'. Defaults to `ui-layout-pane`.
         */
        center__paneClass: string;

        /**
         * This is used as a prefix when generating classNames for 'custom buttons'. Do not confuse with normal
         * 'toggler-buttons'. Defaults to `ui-layout-button`.
         */
        center__buttonClass: string;

        /**
         * Minimum width of the center pane in pixels. 
         */
        center__minWidth: number;

        /**
         * Minimum height of the center pane in pixels. 
         */
        center__minHeight: number;

        /**
         * Maximum width of the center pane in pixels. 
         */
        center__maxWidth: number;

        /**
         * Maximum height of the center pane in pixels. 
         */
        center__maxHeight: number;

        /**
         * Default values are: ".ui-layout-north", ".ui-layout-west", etc. Any valid jQuery selector string can be used
         * - classNames, IDs, etc.
         * 
         * To allow for `nesting` of layouts, there are rules for how pane-elements are related to the layout-container.
         * More flexibility was added in version 1.1.2 to handle panes are nested inside a `form` or other element.
         * 
         * Rules for the relationship between a pane and its container:
         * 
         * 1. When an `ID` is specified for paneSelector, the pane-element only needs to be a descendant of the
         * container - it does NOT have to be a `child`.
         * 1. When a 'class-name' is specified for paneSelector, the pane-element must be EITHER:
         *     - a child of the container, or...
         *     - a child of a form-element that is a child of the container (must be the 'first form' inside the container)
         */
        center__paneSelector: string;

        /**
         * When this is enabled, the layout will apply basic styles directly to resizers & buttons. This is intended for
         * quick mock-ups, so that you can `see` your layout immediately. Normally this should be set as a default
         * option, but it can be set 'per-pane':
         */
        north__applyDefaultStyles: boolean;

        /**
         * If `true`, then when moused-over, the pane's zIndex is raised and overflow is set to `visible`. This allows
         * pop-ups and drop-downs to overlap adjacent panes.
         * 
         * __WARNING__: Enable this only for panes that do not scroll!
         */
        north__showOverflowOnHover: boolean;

        /**
         * MUST be a `child` of one of the panes.
         * 
         * Selector string for INNER div/element. This div will auto-size so only it scrolls, and not the entire pane.
         * 
         * Same class-name could be used for divs inside all panes.
         */
        north__contentSelector: string;

        /**
         * Selector string for INNER divs/elements. These elements will be `ignored` when calculations are done to
         * auto-size the content element. This may be necessary if there are elements inside the pane that are
         * absolutely-positioned and intended to `overlay` other elements.
         * 
         * Same class-name could be used for elements inside all panes
         */
        north__contentIgnoreSelector: string;

        /**
         * Used for auto-generated classNames for each 'layout pane'. Defaults to `ui-layout-pane`.
         */
        north__paneClass: string;

        /**
         * This is used as a prefix when generating classNames for 'custom buttons'. Do not confuse with normal
         * 'toggler-buttons'. Defaults to `ui-layout-button`.
         */
        north__buttonClass: string;

        /**
         * Can a pane be closed?
         */
        north__closable: boolean;

        /**
         * When open, can a pane be resized?
         */
        north__resizable: boolean;

        /**
         * When closed, can a pane 'slide open' over adjacent panes?
         */
        north__slidable: boolean;

        /**
         * Used for auto-generated classNames for each 'resizer-bar'. Defaults to `ui-layout-resizer`.
         */
        north__resizerClass: string;

        /**
         * Used for auto-generated classNames for each 'toggler-button'. Defaults to `ui-layout-toggler`.
         */
        north__togglerClass: string;

        /**
         * Specifies the initial size of the panes - `height` for north & south panes - `width` for east and west. If
         * `auto`, then pane will size to fit its content - most useful for north/south panes (to auto-fit your banner
         * or toolbar), but also works for east/west panes.
         * 
         * You normally will want different sizes for the panes, but a 'default size' can be set
         * 
         * Defaults to `auto` for north and south panes, and to `200` for east and west panes.
         */
        north__size: string;

        /**
         * Minimum-size limit when resizing a pane (0 = as small as pane can go).
         */
        north__minSize: number;

        /**
         * Maximum-size limit when resizing a pane (0 = as large as pane can go).
         */
        north__maxSize: number;

        /**
         * Spacing between pane and adjacent pane - when pane is `open`.
         */
        north__spacing_open: number;

        /**
         * Spacing between pane and adjacent pane - when pane is `closed`.
         */
        north__spacing_closed: number;

        /**
         * Tooltip when resizer-bar can be `dragged` to resize a pane.
         */
        north__resizerTip: string;

        /**
         * Opacity of resizer bar when `dragging` to resize a pane. Defaults to `1`.
         * 
         * This value is passed to the ui.draggable widget
         * 
         * Leave this set to `1` if you want to use CSS to control opacity. Otherwise you must use `!important` to
         * override the specified opacity.
         */
        north__resizerDragOpacity: number;

        /**
         * When enabled, layout will `mask` iframes on the page when the resizer-bar is `dragged` to resize a pane. This
         * solves problems related to dragging an element over an iframe.
         */
        north__maskIframesOnResize: boolean;

        /**
         * Tooltip when the resizer-bar will trigger 'sliding open'.
         */
        north__sliderTip: string;

        /**
         * Cursor when resizer-bar will trigger 'sliding open' - ie, when pane is `closed`. Defaults to `pointer`.
         */
        north__sliderCursor: string;

        /**
         * Trigger events to 'slide open' a pane. Defaults to `click`.
         */
        north__slideTrigger_open: string;

        /**
         * Trigger events to 'slide close' a pane. Defaults to `mouseout`.
         */
        north__slideTrigger_close: string;

        /**
         * Tooltip on toggler when pane is `open`.
         */
        north__togglerTip_open: string;

        /**
         * Tooltip on toggler when pane is `closed`.
         */
        north__togglerTip_closed: string;

        /**
         * Length of toggler button when pane is `open`. Length means `width` for north/south togglers, and `height` for
         * east/west togglers.
         * 
         * `100%` OR `-1` means 'full height/width of resizer bar' - `0` means `hidden`.
         */
        north__togglerLength_open: number | string;

        /**
         * Length of toggler button when pane is `closed`. Length means `width` for north/south togglers, and `height`
         * for east/west togglers.
         * 
         * `100%` OR `-1` means 'full height/width of resizer bar' - `0` means `hidden`.
         */
        north__togglerLength_closed: number;

        /**
         * If true, the toggler-button is hidden when a pane is 'slid-open'. This makes sense because the user only
         * needs to 'mouse-off' to close the pane.
         */
        north__hideTogglerOnSlide: boolean;

        /**
         * Alignment of toggler button inside the resizer-bar when pane is `open`. Defaults to `center`.
         * 
         * A positive integer means a pixel offset from top or left.
         * 
         * A negative integer means a pixel offset from bottom or right.
         */
        north__togglerAlign_open: number | PositionKeyword;

        /**
         * Alignment of toggler button inside the resizer-bar when pane is `closed`. Defaults to `center`.
         * 
         * A positive integer means a pixel offset from top or left.
         * 
         * A negative integer means a pixel offset from bottom or right.
         */
        north__togglerAlign_closed: number | PositionKeyword;

        /**
         * Usually a background-image is set in CSS to customize a toggler-button. However, you can also put text inside
         * a toggler by using these options. The text is wrapped in a SPAN, which is then added inside the toggler DIV.
         * The SPAN classes identify them as either `open` or `closed` content:
         */
        north__togglerContent_open: string;

        /**
         * Usually a background-image is set in CSS to customize a toggler-button. However, you can also put text inside
         * a toggler by using these options. The text is wrapped in a SPAN, which is then added inside the toggler DIV.
         * The SPAN classes identify them as either `open` or `closed` content:
         */
        north__togglerContent_closed: string;

        /**
         * If `true`, then 'cursor hotkeys' are enabled. Can be set per-pane if desired.
         * 
         * These default hotkeys cannot be changed - only enabled or disabled.
         * 
         * The cursor/arrow key must be pressed in combination with CTRL -or- SHIFT
         * 
         * - Toggle North-pane: `CTRL+Up` or `SHIFT+Up`
         * - Toggle South-pane: `CTRL+Down`  or `SHIFT+Down`
         * - Toggle West-pane: `CTRL+Left` or `SHIFT+Left`
         * - Toggle East-pane: `CTRL+Right` or `SHIFT+Right`
         * 
         * The SHIFT+ARROW combinations are ignored if pressed while the cursor is in a form field, allowing users to
         * 'select text' — eg: SHIFT+Right in a TEXTAREA.
         */
        north__enableCursorHotkey: boolean;

        /**
         * Custom hotkeys must be pressed in combination with either the CTRL or SHIFT key - or both together. Use this
         * option to choose which modifier-key(s) to use with the `customHotKey`.
         * 
         * Defaults to `SHIFT`.
         * 
         * If this option is missing or invalid, `CTRL+SHIFT` is assumed.
         * 
         * NOTE: The ALT key cannot be used because it is not detected by some browsers.
         */
        north__customHotkeyModifier: string;

        /**
         * Animation effect for open/close. Choose a preset effect OR can specify a custom fxName as long as you also
         * specify fxSettings (even if fxSettings is just empty - {}).
         * 
         * Defaults to `slide`.
         */
        north__fxName: string;

        /**
         * Animation effect for open. Choose a preset effect OR can specify a custom fxName as long as you also
         * specify fxSettings (even if fxSettings is just empty - {}).
         * 
         * Defaults to `slide`.
         */
        north__fxName_open: string;

        /**
         * Animation effect for close. Choose a preset effect OR can specify a custom fxName as long as you also
         * specify fxSettings (even if fxSettings is just empty - {}).
         * 
         * Defaults to `slide`.
         */
        north__fxName_close: string;

        /**
         * Animation effect for size. Choose a preset effect OR can specify a custom fxName as long as you also
         * specify fxSettings (even if fxSettings is just empty - {}).
         */
        north__fxName_size: string;

        /**
         * Speed of animations – standard jQuery keyword like `fast`, or a millisecond value. Defaults to `normal`.
         */
        north__fxSpeed: number | string;

        /**
         * Speed of animations – standard jQuery keyword like `fast`, or a millisecond value. Defaults to `normal`.
         */
        north__fxSpeed_open: number | string;

        /**
         * Speed of animations – standard jQuery keyword like `fast`, or a millisecond value. Defaults to `normal`.
         */
        north__fxSpeed_close: number | string;

        /**
         * Speed of animations – standard jQuery keyword like `fast`, or a millisecond value. Defaults to `normal`.
         */
        north__fxSpeed_size: number | string;

        /**
         * You can customize the default animation settings by passing new settings.
         * 
         * If a non-standard effect is specified, then fxSettings is REQUIRED (can be an empty object though).
         */
        north__fxSettings: Partial<FxSettings>;

        /**
         * You can customize the default animation settings by passing new settings.
         * 
         * If a non-standard effect is specified, then fxSettings is REQUIRED (can be an empty object though).
         */
        north__fxSettings_open: Partial<FxSettings>;

        /**
         * You can customize the default animation settings by passing new settings.
         * 
         * If a non-standard effect is specified, then fxSettings is REQUIRED (can be an empty object though).
         */
        north__fxSettings_close: Partial<FxSettings>;

        /**
         * You can customize the default animation settings by passing new settings.
         * 
         * If a non-standard effect is specified, then fxSettings is REQUIRED (can be an empty object though).
         */
        north__fxSettings_size: Partial<FxSettings>;

        /**
         * If `true`, then pane is `closed` when layout is created.
         */
        north__initClosed: boolean;

        /**
         * If `true`, then pane is `hidden` when layout is created - no resizer or spacing is visible, as if the pane
         * did not exist!
         */
        north__initHidden: boolean;

        /**
         * Default values are: ".ui-layout-north", ".ui-layout-west", etc. Any valid jQuery selector string can be used
         * - classNames, IDs, etc.
         * 
         * To allow for `nesting` of layouts, there are rules for how pane-elements are related to the layout-container.
         * More flexibility was added in version 1.1.2 to handle panes are nested inside a `form` or other element.
         * 
         * Rules for the relationship between a pane and its container:
         * 
         * 1. When an `ID` is specified for paneSelector, the pane-element only needs to be a descendant of the
         * container - it does NOT have to be a `child`.
         * 1. When a 'class-name' is specified for paneSelector, the pane-element must be EITHER:
         *     - a child of the container, or...
         *     - a child of a form-element that is a child of the container (must be the 'first form' inside the container)
         */
        north__paneSelector: string;

        /**
         * This is the cursor when the mouse is over the 'resizer-bar'. However, if the mouse is over the
         * 'toggler-button' inside the resizer bar, then the cursor is a `pointer` - ie, the togglerCursor instead of
         * the resizerCursor.
         */
        north__resizerCursor: string;

        /**
         * If a hotkey is specified, it is automatically enabled. It does not matter whether 'cursor hotkeys' are also
         * enabled – those are separate.
         * 
         * You can specify any of the following values:
         * 
         * - letter from A to Z
         * - number from 0 to 9
         * - Javascript charCode value for the key
         */
        north__customHotkey: string;

        /**
         * Callback for when pane is `shown`, invoked immeditately after it is shown.
         */
        north__onshow: OnEndCallback | null;

        /**
         * Callback for when pane is `shown`, invoked immeditately before it is shown.
         */
        north__onshow_start: OnStartCallback | null;

        /**
         * Callback for when pane is `shown`, invoked immeditately after it is shown.
         */
        north__onshow_end: OnEndCallback | null;

        /**
         * Callback for when pane is `hidden`, invoked immeditately after it is hidden.
         */
        north__onhide: OnEndCallback | null;

        /**
         * Callback for when pane is `hidden`, invoked immeditately before it is hidden.
         */
        north__onhide_start: OnStartCallback | null;

        /**
         * Callback for when pane is `hidden`, invoked immeditately after it is hidden.
         */
        north__onhide_end: OnEndCallback | null;

        /**
         * Callback for when pane is `opened`, invoked immeditately after it is opened.
         */
        north__onopen: OnEndCallback | null;

        /**
         * Callback for when pane is `opened`, invoked immeditately before it is opened.
         */
        north__onopen_start: OnStartCallback | null;

        /**
         * Callback for when pane is `opened`, invoked immeditately after it is opened.
         */
        north__onopen_end: OnEndCallback | null;

        /**
         * Callback for when pane is `closed`, invoked immeditately after it is closed.
         */
        north__onclose: OnEndCallback | null;

        /**
         * Callback for when pane is `closed`, invoked immeditately before it is closed.
         */
        north__onclose_start: OnStartCallback | null;

        /**
         * Callback for when pane is `closed`, invoked immeditately after it is closed.
         */
        north__onclose_end: OnEndCallback | null;

        /**
         * Callback for when pane is `resized`, invoked immeditately after it is resized.
         */
        north__onresize: OnEndCallback | null;

        /**
         * Callback for when pane is `resized`, invoked immeditately before it is resized.
         */
        north__onresize_start: OnStartCallback | null;

        /**
         * Callback for when pane is `resized`, invoked immeditately after it is resized.
         */
        north__onresize_end: OnEndCallback | null;

        /**
         * Callback for when pane is `swapped`, invoked immeditately after it is swapped.
         */
        north__onswap: OnEndCallback | null;

        /**
         * Callback for when pane is `swapped`, invoked immeditately before it is swapped.
         */
        north__onswap_start: OnStartCallback | null;

        /**
         * Callback for when pane is `swapped`, invoked immeditately after it is swapped.
         */
        north__onswap_end: OnEndCallback | null;

        /**
         * When this is enabled, the layout will apply basic styles directly to resizers & buttons. This is intended for
         * quick mock-ups, so that you can `see` your layout immediately. Normally this should be set as a default
         * option, but it can be set 'per-pane':
         */
        east__applyDefaultStyles: boolean;

        /**
         * If `true`, then when moused-over, the pane's zIndex is raised and overflow is set to `visible`. This allows
         * pop-ups and drop-downs to overlap adjacent panes.
         * 
         * __WARNING__: Enable this only for panes that do not scroll!
         */
        east__showOverflowOnHover: boolean;

        /**
         * MUST be a `child` of one of the panes.
         * 
         * Selector string for INNER div/element. This div will auto-size so only it scrolls, and not the entire pane.
         * 
         * Same class-name could be used for divs inside all panes.
         */
        east__contentSelector: string;

        /**
         * Selector string for INNER divs/elements. These elements will be `ignored` when calculations are done to
         * auto-size the content element. This may be necessary if there are elements inside the pane that are
         * absolutely-positioned and intended to `overlay` other elements.
         * 
         * Same class-name could be used for elements inside all panes
         */
        east__contentIgnoreSelector: string;

        /**
         * Used for auto-generated classNames for each 'layout pane'. Defaults to `ui-layout-pane`.
         */
        east__paneClass: string;

        /**
         * This is used as a prefix when generating classNames for 'custom buttons'. Do not confuse with normal
         * 'toggler-buttons'. Defaults to `ui-layout-button`.
         */
        east__buttonClass: string;

        /**
         * Can a pane be closed?
         */
        east__closable: boolean;

        /**
         * When open, can a pane be resized?
         */
        east__resizable: boolean;

        /**
         * When closed, can a pane 'slide open' over adjacent panes?
         */
        east__slidable: boolean;

        /**
         * Used for auto-generated classNames for each 'resizer-bar'. Defaults to `ui-layout-resizer`.
         */
        east__resizerClass: string;

        /**
         * Used for auto-generated classNames for each 'toggler-button'. Defaults to `ui-layout-toggler`.
         */
        east__togglerClass: string;

        /**
         * Specifies the initial size of the panes - `height` for north & south panes - `width` for east and west. If
         * `auto`, then pane will size to fit its content - most useful for north/south panes (to auto-fit your banner
         * or toolbar), but also works for east/west panes.
         * 
         * You normally will want different sizes for the panes, but a 'default size' can be set
         * 
         * Defaults to `auto` for north and south panes, and to `200` for east and west panes.
         */
        east__size: string;

        /**
         * Minimum-size limit when resizing a pane (0 = as small as pane can go).
         */
        east__minSize: number;

        /**
         * Maximum-size limit when resizing a pane (0 = as large as pane can go).
         */
        east__maxSize: number;

        /**
         * Spacing between pane and adjacent pane - when pane is `open`.
         */
        east__spacing_open: number;

        /**
         * Spacing between pane and adjacent pane - when pane is `closed`.
         */
        east__spacing_closed: number;

        /**
         * Tooltip when resizer-bar can be `dragged` to resize a pane.
         */
        east__resizerTip: string;

        /**
         * Opacity of resizer bar when `dragging` to resize a pane. Defaults to `1`.
         * 
         * This value is passed to the ui.draggable widget
         * 
         * Leave this set to `1` if you want to use CSS to control opacity. Otherwise you must use `!important` to
         * override the specified opacity.
         */
        east__resizerDragOpacity: number;

        /**
         * When enabled, layout will `mask` iframes on the page when the resizer-bar is `dragged` to resize a pane. This
         * solves problems related to dragging an element over an iframe.
         */
        east__maskIframesOnResize: boolean;

        /**
         * Tooltip when the resizer-bar will trigger 'sliding open'.
         */
        east__sliderTip: string;

        /**
         * Cursor when resizer-bar will trigger 'sliding open' - ie, when pane is `closed`. Defaults to `pointer`.
         */
        east__sliderCursor: string;

        /**
         * Trigger events to 'slide open' a pane. Defaults to `click`.
         */
        east__slideTrigger_open: string;

        /**
         * Trigger events to 'slide close' a pane. Defaults to `mouseout`.
         */
        east__slideTrigger_close: string;

        /**
         * Tooltip on toggler when pane is `open`.
         */
        east__togglerTip_open: string;

        /**
         * Tooltip on toggler when pane is `closed`.
         */
        east__togglerTip_closed: string;

        /**
         * Length of toggler button when pane is `open`. Length means `width` for north/south togglers, and `height` for
         * east/west togglers.
         * 
         * `100%` OR `-1` means 'full height/width of resizer bar' - `0` means `hidden`.
         */
        east__togglerLength_open: number | string;

        /**
         * Length of toggler button when pane is `closed`. Length means `width` for north/south togglers, and `height`
         * for east/west togglers.
         * 
         * `100%` OR `-1` means 'full height/width of resizer bar' - `0` means `hidden`.
         */
        east__togglerLength_closed: number;

        /**
         * If true, the toggler-button is hidden when a pane is 'slid-open'. This makes sense because the user only
         * needs to 'mouse-off' to close the pane.
         */
        east__hideTogglerOnSlide: boolean;

        /**
         * Alignment of toggler button inside the resizer-bar when pane is `open`. Defaults to `center`.
         * 
         * A positive integer means a pixel offset from top or left.
         * 
         * A negative integer means a pixel offset from bottom or right.
         */
        east__togglerAlign_open: number | PositionKeyword;

        /**
         * Alignment of toggler button inside the resizer-bar when pane is `closed`. Defaults to `center`.
         * 
         * A positive integer means a pixel offset from top or left.
         * 
         * A negative integer means a pixel offset from bottom or right.
         */
        east__togglerAlign_closed: number | PositionKeyword;

        /**
         * Usually a background-image is set in CSS to customize a toggler-button. However, you can also put text inside
         * a toggler by using these options. The text is wrapped in a SPAN, which is then added inside the toggler DIV.
         * The SPAN classes identify them as either `open` or `closed` content:
         */
        east__togglerContent_open: string;

        /**
         * Usually a background-image is set in CSS to customize a toggler-button. However, you can also put text inside
         * a toggler by using these options. The text is wrapped in a SPAN, which is then added inside the toggler DIV.
         * The SPAN classes identify them as either `open` or `closed` content:
         */
        east__togglerContent_closed: string;

        /**
         * If `true`, then 'cursor hotkeys' are enabled. Can be set per-pane if desired.
         * 
         * These default hotkeys cannot be changed - only enabled or disabled.
         * 
         * The cursor/arrow key must be pressed in combination with CTRL -or- SHIFT
         * 
         * - Toggle North-pane: `CTRL+Up` or `SHIFT+Up`
         * - Toggle South-pane: `CTRL+Down`  or `SHIFT+Down`
         * - Toggle West-pane: `CTRL+Left` or `SHIFT+Left`
         * - Toggle East-pane: `CTRL+Right` or `SHIFT+Right`
         * 
         * The SHIFT+ARROW combinations are ignored if pressed while the cursor is in a form field, allowing users to
         * 'select text' — eg: SHIFT+Right in a TEXTAREA.
         */
        east__enableCursorHotkey: boolean;

        /**
         * Custom hotkeys must be pressed in combination with either the CTRL or SHIFT key - or both together. Use this
         * option to choose which modifier-key(s) to use with the `customHotKey`.
         * 
         * Defaults to `SHIFT`.
         * 
         * If this option is missing or invalid, `CTRL+SHIFT` is assumed.
         * 
         * NOTE: The ALT key cannot be used because it is not detected by some browsers.
         */
        east__customHotkeyModifier: string;

        /**
         * Animation effect for open/close. Choose a preset effect OR can specify a custom fxName as long as you also
         * specify fxSettings (even if fxSettings is just empty - {}).
         * 
         * Defaults to `slide`.
         */
        east__fxName: string;

        /**
         * Animation effect for open. Choose a preset effect OR can specify a custom fxName as long as you also
         * specify fxSettings (even if fxSettings is just empty - {}).
         * 
         * Defaults to `slide`.
         */
        east__fxName_open: string;

        /**
         * Animation effect for close. Choose a preset effect OR can specify a custom fxName as long as you also
         * specify fxSettings (even if fxSettings is just empty - {}).
         * 
         * Defaults to `slide`.
         */
        east__fxName_close: string;

        /**
         * Animation effect for size. Choose a preset effect OR can specify a custom fxName as long as you also
         * specify fxSettings (even if fxSettings is just empty - {}).
         */
        east__fxName_size: string;

        /**
         * Speed of animations – standard jQuery keyword like `fast`, or a millisecond value. Defaults to `normal`.
         */
        east__fxSpeed: number | string;

        /**
         * Speed of animations – standard jQuery keyword like `fast`, or a millisecond value. Defaults to `normal`.
         */
        east__fxSpeed_open: number | string;

        /**
         * Speed of animations – standard jQuery keyword like `fast`, or a millisecond value. Defaults to `normal`.
         */
        east__fxSpeed_close: number | string;

        /**
         * Speed of animations – standard jQuery keyword like `fast`, or a millisecond value. Defaults to `normal`.
         */
        east__fxSpeed_size: number | string;

        /**
         * You can customize the default animation settings by passing new settings.
         * 
         * If a non-standard effect is specified, then fxSettings is REQUIRED (can be an empty object though).
         */
        east__fxSettings: Partial<FxSettings>;

        /**
         * You can customize the default animation settings by passing new settings.
         * 
         * If a non-standard effect is specified, then fxSettings is REQUIRED (can be an empty object though).
         */
        east__fxSettings_open: Partial<FxSettings>;

        /**
         * You can customize the default animation settings by passing new settings.
         * 
         * If a non-standard effect is specified, then fxSettings is REQUIRED (can be an empty object though).
         */
        east__fxSettings_close: Partial<FxSettings>;

        /**
         * You can customize the default animation settings by passing new settings.
         * 
         * If a non-standard effect is specified, then fxSettings is REQUIRED (can be an empty object though).
         */
        east__fxSettings_size: Partial<FxSettings>;

        /**
         * If `true`, then pane is `closed` when layout is created.
         */
        east__initClosed: boolean;

        /**
         * If `true`, then pane is `hidden` when layout is created - no resizer or spacing is visible, as if the pane
         * did not exist!
         */
        east__initHidden: boolean;

        /**
         * Default values are: ".ui-layout-north", ".ui-layout-west", etc. Any valid jQuery selector string can be used
         * - classNames, IDs, etc.
         * 
         * To allow for `nesting` of layouts, there are rules for how pane-elements are related to the layout-container.
         * More flexibility was added in version 1.1.2 to handle panes are nested inside a `form` or other element.
         * 
         * Rules for the relationship between a pane and its container:
         * 
         * 1. When an `ID` is specified for paneSelector, the pane-element only needs to be a descendant of the
         * container - it does NOT have to be a `child`.
         * 1. When a 'class-name' is specified for paneSelector, the pane-element must be EITHER:
         *     - a child of the container, or...
         *     - a child of a form-element that is a child of the container (must be the 'first form' inside the container)
         */
        east__paneSelector: string;

        /**
         * This is the cursor when the mouse is over the 'resizer-bar'. However, if the mouse is over the
         * 'toggler-button' inside the resizer bar, then the cursor is a `pointer` - ie, the togglerCursor instead of
         * the resizerCursor.
         */
        east__resizerCursor: string;

        /**
         * If a hotkey is specified, it is automatically enabled. It does not matter whether 'cursor hotkeys' are also
         * enabled – those are separate.
         * 
         * You can specify any of the following values:
         * 
         * - letter from A to Z
         * - number from 0 to 9
         * - Javascript charCode value for the key
         */
        east__customHotkey: string;

        /**
         * Callback for when pane is `shown`, invoked immeditately after it is shown.
         */
        east__onshow: OnEndCallback | null;

        /**
         * Callback for when pane is `shown`, invoked immeditately before it is shown.
         */
        east__onshow_start: OnStartCallback | null;

        /**
         * Callback for when pane is `shown`, invoked immeditately after it is shown.
         */
        east__onshow_end: OnEndCallback | null;

        /**
         * Callback for when pane is `hidden`, invoked immeditately after it is hidden.
         */
        east__onhide: OnEndCallback | null;

        /**
         * Callback for when pane is `hidden`, invoked immeditately before it is hidden.
         */
        east__onhide_start: OnStartCallback | null;

        /**
         * Callback for when pane is `hidden`, invoked immeditately after it is hidden.
         */
        east__onhide_end: OnEndCallback | null;

        /**
         * Callback for when pane is `opened`, invoked immeditately after it is opened.
         */
        east__onopen: OnEndCallback | null;

        /**
         * Callback for when pane is `opened`, invoked immeditately before it is opened.
         */
        east__onopen_start: OnStartCallback | null;

        /**
         * Callback for when pane is `opened`, invoked immeditately after it is opened.
         */
        east__onopen_end: OnEndCallback | null;

        /**
         * Callback for when pane is `closed`, invoked immeditately after it is closed.
         */
        east__onclose: OnEndCallback | null;

        /**
         * Callback for when pane is `closed`, invoked immeditately before it is closed.
         */
        east__onclose_start: OnStartCallback | null;

        /**
         * Callback for when pane is `closed`, invoked immeditately after it is closed.
         */
        east__onclose_end: OnEndCallback | null;

        /**
         * Callback for when pane is `resized`, invoked immeditately after it is resized.
         */
        east__onresize: OnEndCallback | null;

        /**
         * Callback for when pane is `resized`, invoked immeditately before it is resized.
         */
        east__onresize_start: OnStartCallback | null;

        /**
         * Callback for when pane is `resized`, invoked immeditately after it is resized.
         */
        east__onresize_end: OnEndCallback | null;

        /**
         * Callback for when pane is `swapped`, invoked immeditately after it is swapped.
         */
        east__onswap: OnEndCallback | null;

        /**
         * Callback for when pane is `swapped`, invoked immeditately before it is swapped.
         */
        east__onswap_start: OnStartCallback | null;

        /**
         * Callback for when pane is `swapped`, invoked immeditately after it is swapped.
         */
        east__onswap_end: OnEndCallback | null;

        /**
         * When this is enabled, the layout will apply basic styles directly to resizers & buttons. This is intended for
         * quick mock-ups, so that you can `see` your layout immediately. Normally this should be set as a default
         * option, but it can be set 'per-pane':
         */
        south__applyDefaultStyles: boolean;

        /**
         * If `true`, then when moused-over, the pane's zIndex is raised and overflow is set to `visible`. This allows
         * pop-ups and drop-downs to overlap adjacent panes.
         * 
         * __WARNING__: Enable this only for panes that do not scroll!
         */
        south__showOverflowOnHover: boolean;

        /**
         * MUST be a `child` of one of the panes.
         * 
         * Selector string for INNER div/element. This div will auto-size so only it scrolls, and not the entire pane.
         * 
         * Same class-name could be used for divs inside all panes.
         */
        south__contentSelector: string;

        /**
         * Selector string for INNER divs/elements. These elements will be `ignored` when calculations are done to
         * auto-size the content element. This may be necessary if there are elements inside the pane that are
         * absolutely-positioned and intended to `overlay` other elements.
         * 
         * Same class-name could be used for elements inside all panes
         */
        south__contentIgnoreSelector: string;

        /**
         * Used for auto-generated classNames for each 'layout pane'. Defaults to `ui-layout-pane`.
         */
        south__paneClass: string;

        /**
         * This is used as a prefix when generating classNames for 'custom buttons'. Do not confuse with normal
         * 'toggler-buttons'. Defaults to `ui-layout-button`.
         */
        south__buttonClass: string;

        /**
         * Can a pane be closed?
         */
        south__closable: boolean;

        /**
         * When open, can a pane be resized?
         */
        south__resizable: boolean;

        /**
         * When closed, can a pane 'slide open' over adjacent panes?
         */
        south__slidable: boolean;

        /**
         * Used for auto-generated classNames for each 'resizer-bar'. Defaults to `ui-layout-resizer`.
         */
        south__resizerClass: string;

        /**
         * Used for auto-generated classNames for each 'toggler-button'. Defaults to `ui-layout-toggler`.
         */
        south__togglerClass: string;

        /**
         * Specifies the initial size of the panes - `height` for north & south panes - `width` for east and west. If
         * `auto`, then pane will size to fit its content - most useful for north/south panes (to auto-fit your banner
         * or toolbar), but also works for east/west panes.
         * 
         * You normally will want different sizes for the panes, but a 'default size' can be set
         * 
         * Defaults to `auto` for north and south panes, and to `200` for east and west panes.
         */
        south__size: string;

        /**
         * Minimum-size limit when resizing a pane (0 = as small as pane can go).
         */
        south__minSize: number;

        /**
         * Maximum-size limit when resizing a pane (0 = as large as pane can go).
         */
        south__maxSize: number;

        /**
         * Spacing between pane and adjacent pane - when pane is `open`.
         */
        south__spacing_open: number;

        /**
         * Spacing between pane and adjacent pane - when pane is `closed`.
         */
        south__spacing_closed: number;

        /**
         * Tooltip when resizer-bar can be `dragged` to resize a pane.
         */
        south__resizerTip: string;

        /**
         * Opacity of resizer bar when `dragging` to resize a pane. Defaults to `1`.
         * 
         * This value is passed to the ui.draggable widget
         * 
         * Leave this set to `1` if you want to use CSS to control opacity. Otherwise you must use `!important` to
         * override the specified opacity.
         */
        south__resizerDragOpacity: number;

        /**
         * When enabled, layout will `mask` iframes on the page when the resizer-bar is `dragged` to resize a pane. This
         * solves problems related to dragging an element over an iframe.
         */
        south__maskIframesOnResize: boolean;

        /**
         * Tooltip when the resizer-bar will trigger 'sliding open'.
         */
        south__sliderTip: string;

        /**
         * Cursor when resizer-bar will trigger 'sliding open' - ie, when pane is `closed`. Defaults to `pointer`.
         */
        south__sliderCursor: string;

        /**
         * Trigger events to 'slide open' a pane. Defaults to `click`.
         */
        south__slideTrigger_open: string;

        /**
         * Trigger events to 'slide close' a pane. Defaults to `mouseout`.
         */
        south__slideTrigger_close: string;

        /**
         * Tooltip on toggler when pane is `open`.
         */
        south__togglerTip_open: string;

        /**
         * Tooltip on toggler when pane is `closed`.
         */
        south__togglerTip_closed: string;

        /**
         * Length of toggler button when pane is `open`. Length means `width` for north/south togglers, and `height` for
         * east/west togglers.
         * 
         * `100%` OR `-1` means 'full height/width of resizer bar' - `0` means `hidden`.
         */
        south__togglerLength_open: number | string;

        /**
         * Length of toggler button when pane is `closed`. Length means `width` for north/south togglers, and `height`
         * for east/west togglers.
         * 
         * `100%` OR `-1` means 'full height/width of resizer bar' - `0` means `hidden`.
         */
        south__togglerLength_closed: number;

        /**
         * If true, the toggler-button is hidden when a pane is 'slid-open'. This makes sense because the user only
         * needs to 'mouse-off' to close the pane.
         */
        south__hideTogglerOnSlide: boolean;

        /**
         * Alignment of toggler button inside the resizer-bar when pane is `open`. Defaults to `center`.
         * 
         * A positive integer means a pixel offset from top or left.
         * 
         * A negative integer means a pixel offset from bottom or right.
         */
        south__togglerAlign_open: number | PositionKeyword;

        /**
         * Alignment of toggler button inside the resizer-bar when pane is `closed`. Defaults to `center`.
         * 
         * A positive integer means a pixel offset from top or left.
         * 
         * A negative integer means a pixel offset from bottom or right.
         */
        south__togglerAlign_closed: number | PositionKeyword;

        /**
         * Usually a background-image is set in CSS to customize a toggler-button. However, you can also put text inside
         * a toggler by using these options. The text is wrapped in a SPAN, which is then added inside the toggler DIV.
         * The SPAN classes identify them as either `open` or `closed` content:
         */
        south__togglerContent_open: string;

        /**
         * Usually a background-image is set in CSS to customize a toggler-button. However, you can also put text inside
         * a toggler by using these options. The text is wrapped in a SPAN, which is then added inside the toggler DIV.
         * The SPAN classes identify them as either `open` or `closed` content:
         */
        south__togglerContent_closed: string;

        /**
         * If `true`, then 'cursor hotkeys' are enabled. Can be set per-pane if desired.
         * 
         * These default hotkeys cannot be changed - only enabled or disabled.
         * 
         * The cursor/arrow key must be pressed in combination with CTRL -or- SHIFT
         * 
         * - Toggle North-pane: `CTRL+Up` or `SHIFT+Up`
         * - Toggle South-pane: `CTRL+Down`  or `SHIFT+Down`
         * - Toggle West-pane: `CTRL+Left` or `SHIFT+Left`
         * - Toggle East-pane: `CTRL+Right` or `SHIFT+Right`
         * 
         * The SHIFT+ARROW combinations are ignored if pressed while the cursor is in a form field, allowing users to
         * 'select text' — eg: SHIFT+Right in a TEXTAREA.
         */
        south__enableCursorHotkey: boolean;

        /**
         * Custom hotkeys must be pressed in combination with either the CTRL or SHIFT key - or both together. Use this
         * option to choose which modifier-key(s) to use with the `customHotKey`.
         * 
         * Defaults to `SHIFT`.
         * 
         * If this option is missing or invalid, `CTRL+SHIFT` is assumed.
         * 
         * NOTE: The ALT key cannot be used because it is not detected by some browsers.
         */
        south__customHotkeyModifier: string;

        /**
         * Animation effect for open/close. Choose a preset effect OR can specify a custom fxName as long as you also
         * specify fxSettings (even if fxSettings is just empty - {}).
         * 
         * Defaults to `slide`.
         */
        south__fxName: string;

        /**
         * Animation effect for open. Choose a preset effect OR can specify a custom fxName as long as you also
         * specify fxSettings (even if fxSettings is just empty - {}).
         * 
         * Defaults to `slide`.
         */
        south__fxName_open: string;

        /**
         * Animation effect for close. Choose a preset effect OR can specify a custom fxName as long as you also
         * specify fxSettings (even if fxSettings is just empty - {}).
         * 
         * Defaults to `slide`.
         */
        south__fxName_close: string;

        /**
         * Animation effect for size. Choose a preset effect OR can specify a custom fxName as long as you also
         * specify fxSettings (even if fxSettings is just empty - {}).
         */
        south__fxName_size: string;

        /**
         * Speed of animations – standard jQuery keyword like `fast`, or a millisecond value. Defaults to `normal`.
         */
        south__fxSpeed: number | string;

        /**
         * Speed of animations – standard jQuery keyword like `fast`, or a millisecond value. Defaults to `normal`.
         */
        south__fxSpeed_open: number | string;

        /**
         * Speed of animations – standard jQuery keyword like `fast`, or a millisecond value. Defaults to `normal`.
         */
        south__fxSpeed_close: number | string;

        /**
         * Speed of animations – standard jQuery keyword like `fast`, or a millisecond value. Defaults to `normal`.
         */
        south__fxSpeed_size: number | string;

        /**
         * You can customize the default animation settings by passing new settings.
         * 
         * If a non-standard effect is specified, then fxSettings is REQUIRED (can be an empty object though).
         */
        south__fxSettings: Partial<FxSettings>;

        /**
         * You can customize the default animation settings by passing new settings.
         * 
         * If a non-standard effect is specified, then fxSettings is REQUIRED (can be an empty object though).
         */
        south__fxSettings_open: Partial<FxSettings>;

        /**
         * You can customize the default animation settings by passing new settings.
         * 
         * If a non-standard effect is specified, then fxSettings is REQUIRED (can be an empty object though).
         */
        south__fxSettings_close: Partial<FxSettings>;

        /**
         * You can customize the default animation settings by passing new settings.
         * 
         * If a non-standard effect is specified, then fxSettings is REQUIRED (can be an empty object though).
         */
        south__fxSettings_size: Partial<FxSettings>;

        /**
         * If `true`, then pane is `closed` when layout is created.
         */
        south__initClosed: boolean;

        /**
         * If `true`, then pane is `hidden` when layout is created - no resizer or spacing is visible, as if the pane
         * did not exist!
         */
        south__initHidden: boolean;

        /**
         * Default values are: ".ui-layout-north", ".ui-layout-west", etc. Any valid jQuery selector string can be used
         * - classNames, IDs, etc.
         * 
         * To allow for `nesting` of layouts, there are rules for how pane-elements are related to the layout-container.
         * More flexibility was added in version 1.1.2 to handle panes are nested inside a `form` or other element.
         * 
         * Rules for the relationship between a pane and its container:
         * 
         * 1. When an `ID` is specified for paneSelector, the pane-element only needs to be a descendant of the
         * container - it does NOT have to be a `child`.
         * 1. When a 'class-name' is specified for paneSelector, the pane-element must be EITHER:
         *     - a child of the container, or...
         *     - a child of a form-element that is a child of the container (must be the 'first form' inside the container)
         */
        south__paneSelector: string;

        /**
         * This is the cursor when the mouse is over the 'resizer-bar'. However, if the mouse is over the
         * 'toggler-button' inside the resizer bar, then the cursor is a `pointer` - ie, the togglerCursor instead of
         * the resizerCursor.
         */
        south__resizerCursor: string;

        /**
         * If a hotkey is specified, it is automatically enabled. It does not matter whether 'cursor hotkeys' are also
         * enabled – those are separate.
         * 
         * You can specify any of the following values:
         * 
         * - letter from A to Z
         * - number from 0 to 9
         * - Javascript charCode value for the key
         */
        south__customHotkey: string;

        /**
         * Callback for when pane is `shown`, invoked immeditately after it is shown.
         */
        south__onshow: OnEndCallback | null;

        /**
         * Callback for when pane is `shown`, invoked immeditately before it is shown.
         */
        south__onshow_start: OnStartCallback | null;

        /**
         * Callback for when pane is `shown`, invoked immeditately after it is shown.
         */
        south__onshow_end: OnEndCallback | null;

        /**
         * Callback for when pane is `hidden`, invoked immeditately after it is hidden.
         */
        south__onhide: OnEndCallback | null;

        /**
         * Callback for when pane is `hidden`, invoked immeditately before it is hidden.
         */
        south__onhide_start: OnStartCallback | null;

        /**
         * Callback for when pane is `hidden`, invoked immeditately after it is hidden.
         */
        south__onhide_end: OnEndCallback | null;

        /**
         * Callback for when pane is `opened`, invoked immeditately after it is opened.
         */
        south__onopen: OnEndCallback | null;

        /**
         * Callback for when pane is `opened`, invoked immeditately before it is opened.
         */
        south__onopen_start: OnStartCallback | null;

        /**
         * Callback for when pane is `opened`, invoked immeditately after it is opened.
         */
        south__onopen_end: OnEndCallback | null;

        /**
         * Callback for when pane is `closed`, invoked immeditately after it is closed.
         */
        south__onclose: OnEndCallback | null;

        /**
         * Callback for when pane is `closed`, invoked immeditately before it is closed.
         */
        south__onclose_start: OnStartCallback | null;

        /**
         * Callback for when pane is `closed`, invoked immeditately after it is closed.
         */
        south__onclose_end: OnEndCallback | null;

        /**
         * Callback for when pane is `resized`, invoked immeditately after it is resized.
         */
        south__onresize: OnEndCallback | null;

        /**
         * Callback for when pane is `resized`, invoked immeditately before it is resized.
         */
        south__onresize_start: OnStartCallback | null;

        /**
         * Callback for when pane is `resized`, invoked immeditately after it is resized.
         */
        south__onresize_end: OnEndCallback | null;

        /**
         * Callback for when pane is `swapped`, invoked immeditately after it is swapped.
         */
        south__onswap: OnEndCallback | null;

        /**
         * Callback for when pane is `swapped`, invoked immeditately before it is swapped.
         */
        south__onswap_start: OnStartCallback | null;

        /**
         * Callback for when pane is `swapped`, invoked immeditately after it is swapped.
         */
        south__onswap_end: OnEndCallback | null;

        /**
         * When this is enabled, the layout will apply basic styles directly to resizers & buttons. This is intended for
         * quick mock-ups, so that you can `see` your layout immediately. Normally this should be set as a default
         * option, but it can be set 'per-pane':
         */
        west__applyDefaultStyles: boolean;

        /**
         * If `true`, then when moused-over, the pane's zIndex is raised and overflow is set to `visible`. This allows
         * pop-ups and drop-downs to overlap adjacent panes.
         * 
         * __WARNING__: Enable this only for panes that do not scroll!
         */
        west__showOverflowOnHover: boolean;

        /**
         * MUST be a `child` of one of the panes.
         * 
         * Selector string for INNER div/element. This div will auto-size so only it scrolls, and not the entire pane.
         * 
         * Same class-name could be used for divs inside all panes.
         */
        west__contentSelector: string;

        /**
         * Selector string for INNER divs/elements. These elements will be `ignored` when calculations are done to
         * auto-size the content element. This may be necessary if there are elements inside the pane that are
         * absolutely-positioned and intended to `overlay` other elements.
         * 
         * Same class-name could be used for elements inside all panes
         */
        west__contentIgnoreSelector: string;

        /**
         * Used for auto-generated classNames for each 'layout pane'. Defaults to `ui-layout-pane`.
         */
        west__paneClass: string;

        /**
         * This is used as a prefix when generating classNames for 'custom buttons'. Do not confuse with normal
         * 'toggler-buttons'. Defaults to `ui-layout-button`.
         */
        west__buttonClass: string;

        /**
         * Can a pane be closed?
         */
        west__closable: boolean;

        /**
         * When open, can a pane be resized?
         */
        west__resizable: boolean;

        /**
         * When closed, can a pane 'slide open' over adjacent panes?
         */
        west__slidable: boolean;

        /**
         * Used for auto-generated classNames for each 'resizer-bar'. Defaults to `ui-layout-resizer`.
         */
        west__resizerClass: string;

        /**
         * Used for auto-generated classNames for each 'toggler-button'. Defaults to `ui-layout-toggler`.
         */
        west__togglerClass: string;

        /**
         * Specifies the initial size of the panes - `height` for north & south panes - `width` for east and west. If
         * `auto`, then pane will size to fit its content - most useful for north/south panes (to auto-fit your banner
         * or toolbar), but also works for east/west panes.
         * 
         * You normally will want different sizes for the panes, but a 'default size' can be set
         * 
         * Defaults to `auto` for north and south panes, and to `200` for east and west panes.
         */
        west__size: string;

        /**
         * Minimum-size limit when resizing a pane (0 = as small as pane can go).
         */
        west__minSize: number;

        /**
         * Maximum-size limit when resizing a pane (0 = as large as pane can go).
         */
        west__maxSize: number;

        /**
         * Spacing between pane and adjacent pane - when pane is `open`.
         */
        west__spacing_open: number;

        /**
         * Spacing between pane and adjacent pane - when pane is `closed`.
         */
        west__spacing_closed: number;

        /**
         * Tooltip when resizer-bar can be `dragged` to resize a pane.
         */
        west__resizerTip: string;

        /**
         * Opacity of resizer bar when `dragging` to resize a pane. Defaults to `1`.
         * 
         * This value is passed to the ui.draggable widget
         * 
         * Leave this set to `1` if you want to use CSS to control opacity. Otherwise you must use `!important` to
         * override the specified opacity.
         */
        west__resizerDragOpacity: number;

        /**
         * When enabled, layout will `mask` iframes on the page when the resizer-bar is `dragged` to resize a pane. This
         * solves problems related to dragging an element over an iframe.
         */
        west__maskIframesOnResize: boolean;

        /**
         * Tooltip when the resizer-bar will trigger 'sliding open'.
         */
        west__sliderTip: string;

        /**
         * Cursor when resizer-bar will trigger 'sliding open' - ie, when pane is `closed`. Defaults to `pointer`.
         */
        west__sliderCursor: string;

        /**
         * Trigger events to 'slide open' a pane. Defaults to `click`.
         */
        west__slideTrigger_open: string;

        /**
         * Trigger events to 'slide close' a pane. Defaults to `mouseout`.
         */
        west__slideTrigger_close: string;

        /**
         * Tooltip on toggler when pane is `open`.
         */
        west__togglerTip_open: string;

        /**
         * Tooltip on toggler when pane is `closed`.
         */
        west__togglerTip_closed: string;

        /**
         * Length of toggler button when pane is `open`. Length means `width` for north/south togglers, and `height` for
         * east/west togglers.
         * 
         * `100%` OR `-1` means 'full height/width of resizer bar' - `0` means `hidden`.
         */
        west__togglerLength_open: number | string;

        /**
         * Length of toggler button when pane is `closed`. Length means `width` for north/south togglers, and `height`
         * for east/west togglers.
         * 
         * `100%` OR `-1` means 'full height/width of resizer bar' - `0` means `hidden`.
         */
        west__togglerLength_closed: number;

        /**
         * If true, the toggler-button is hidden when a pane is 'slid-open'. This makes sense because the user only
         * needs to 'mouse-off' to close the pane.
         */
        west__hideTogglerOnSlide: boolean;

        /**
         * Alignment of toggler button inside the resizer-bar when pane is `open`. Defaults to `center`.
         * 
         * A positive integer means a pixel offset from top or left.
         * 
         * A negative integer means a pixel offset from bottom or right.
         */
        west__togglerAlign_open: number | PositionKeyword;

        /**
         * Alignment of toggler button inside the resizer-bar when pane is `closed`. Defaults to `center`.
         * 
         * A positive integer means a pixel offset from top or left.
         * 
         * A negative integer means a pixel offset from bottom or right.
         */
        west__togglerAlign_closed: number | PositionKeyword;

        /**
         * Usually a background-image is set in CSS to customize a toggler-button. However, you can also put text inside
         * a toggler by using these options. The text is wrapped in a SPAN, which is then added inside the toggler DIV.
         * The SPAN classes identify them as either `open` or `closed` content:
         */
        west__togglerContent_open: string;

        /**
         * Usually a background-image is set in CSS to customize a toggler-button. However, you can also put text inside
         * a toggler by using these options. The text is wrapped in a SPAN, which is then added inside the toggler DIV.
         * The SPAN classes identify them as either `open` or `closed` content:
         */
        west__togglerContent_closed: string;

        /**
         * If `true`, then 'cursor hotkeys' are enabled. Can be set per-pane if desired.
         * 
         * These default hotkeys cannot be changed - only enabled or disabled.
         * 
         * The cursor/arrow key must be pressed in combination with CTRL -or- SHIFT
         * 
         * - Toggle North-pane: `CTRL+Up` or `SHIFT+Up`
         * - Toggle South-pane: `CTRL+Down`  or `SHIFT+Down`
         * - Toggle West-pane: `CTRL+Left` or `SHIFT+Left`
         * - Toggle East-pane: `CTRL+Right` or `SHIFT+Right`
         * 
         * The SHIFT+ARROW combinations are ignored if pressed while the cursor is in a form field, allowing users to
         * 'select text' — eg: SHIFT+Right in a TEXTAREA.
         */
        west__enableCursorHotkey: boolean;

        /**
         * Custom hotkeys must be pressed in combination with either the CTRL or SHIFT key - or both together. Use this
         * option to choose which modifier-key(s) to use with the `customHotKey`.
         * 
         * Defaults to `SHIFT`.
         * 
         * If this option is missing or invalid, `CTRL+SHIFT` is assumed.
         * 
         * NOTE: The ALT key cannot be used because it is not detected by some browsers.
         */
        west__customHotkeyModifier: string;

        /**
         * Animation effect for open/close. Choose a preset effect OR can specify a custom fxName as long as you also
         * specify fxSettings (even if fxSettings is just empty - {}).
         * 
         * Defaults to `slide`.
         */
        west__fxName: string;

        /**
         * Animation effect for open. Choose a preset effect OR can specify a custom fxName as long as you also
         * specify fxSettings (even if fxSettings is just empty - {}).
         * 
         * Defaults to `slide`.
         */
        west__fxName_open: string;

        /**
         * Animation effect for close. Choose a preset effect OR can specify a custom fxName as long as you also
         * specify fxSettings (even if fxSettings is just empty - {}).
         * 
         * Defaults to `slide`.
         */
        west__fxName_close: string;

        /**
         * Animation effect for size. Choose a preset effect OR can specify a custom fxName as long as you also
         * specify fxSettings (even if fxSettings is just empty - {}).
         */
        west__fxName_size: string;

        /**
         * Speed of animations – standard jQuery keyword like `fast`, or a millisecond value. Defaults to `normal`.
         */
        west__fxSpeed: number | string;

        /**
         * Speed of animations – standard jQuery keyword like `fast`, or a millisecond value. Defaults to `normal`.
         */
        west__fxSpeed_open: number | string;

        /**
         * Speed of animations – standard jQuery keyword like `fast`, or a millisecond value. Defaults to `normal`.
         */
        west__fxSpeed_close: number | string;

        /**
         * Speed of animations – standard jQuery keyword like `fast`, or a millisecond value. Defaults to `normal`.
         */
        west__fxSpeed_size: number | string;

        /**
         * You can customize the default animation settings by passing new settings.
         * 
         * If a non-standard effect is specified, then fxSettings is REQUIRED (can be an empty object though).
         */
        west__fxSettings: Partial<FxSettings>;

        /**
         * You can customize the default animation settings by passing new settings.
         * 
         * If a non-standard effect is specified, then fxSettings is REQUIRED (can be an empty object though).
         */
        west__fxSettings_open: Partial<FxSettings>;

        /**
         * You can customize the default animation settings by passing new settings.
         * 
         * If a non-standard effect is specified, then fxSettings is REQUIRED (can be an empty object though).
         */
        west__fxSettings_close: Partial<FxSettings>;

        /**
         * You can customize the default animation settings by passing new settings.
         * 
         * If a non-standard effect is specified, then fxSettings is REQUIRED (can be an empty object though).
         */
        west__fxSettings_size: Partial<FxSettings>;

        /**
         * If `true`, then pane is `closed` when layout is created.
         */
        west__initClosed: boolean;

        /**
         * If `true`, then pane is `hidden` when layout is created - no resizer or spacing is visible, as if the pane
         * did not exist!
         */
        west__initHidden: boolean;

        /**
         * Default values are: ".ui-layout-north", ".ui-layout-west", etc. Any valid jQuery selector string can be used
         * - classNames, IDs, etc.
         * 
         * To allow for `nesting` of layouts, there are rules for how pane-elements are related to the layout-container.
         * More flexibility was added in version 1.1.2 to handle panes are nested inside a `form` or other element.
         * 
         * Rules for the relationship between a pane and its container:
         * 
         * 1. When an `ID` is specified for paneSelector, the pane-element only needs to be a descendant of the
         * container - it does NOT have to be a `child`.
         * 1. When a 'class-name' is specified for paneSelector, the pane-element must be EITHER:
         *     - a child of the container, or...
         *     - a child of a form-element that is a child of the container (must be the 'first form' inside the container)
         */
        west__paneSelector: string;

        /**
         * This is the cursor when the mouse is over the 'resizer-bar'. However, if the mouse is over the
         * 'toggler-button' inside the resizer bar, then the cursor is a `pointer` - ie, the togglerCursor instead of
         * the resizerCursor.
         */
        west__resizerCursor: string;

        /**
         * If a hotkey is specified, it is automatically enabled. It does not matter whether 'cursor hotkeys' are also
         * enabled – those are separate.
         * 
         * You can specify any of the following values:
         * 
         * - letter from A to Z
         * - number from 0 to 9
         * - Javascript charCode value for the key
         */
        west__customHotkey: string;

        /**
         * Callback for when pane is `shown`, invoked immeditately after it is shown.
         */
        west__onshow: OnEndCallback | null;

        /**
         * Callback for when pane is `shown`, invoked immeditately before it is shown.
         */
        west__onshow_start: OnStartCallback | null;

        /**
         * Callback for when pane is `shown`, invoked immeditately after it is shown.
         */
        west__onshow_end: OnEndCallback | null;

        /**
         * Callback for when pane is `hidden`, invoked immeditately after it is hidden.
         */
        west__onhide: OnEndCallback | null;

        /**
         * Callback for when pane is `hidden`, invoked immeditately before it is hidden.
         */
        west__onhide_start: OnStartCallback | null;

        /**
         * Callback for when pane is `hidden`, invoked immeditately after it is hidden.
         */
        west__onhide_end: OnEndCallback | null;

        /**
         * Callback for when pane is `opened`, invoked immeditately after it is opened.
         */
        west__onopen: OnEndCallback | null;

        /**
         * Callback for when pane is `opened`, invoked immeditately before it is opened.
         */
        west__onopen_start: OnStartCallback | null;

        /**
         * Callback for when pane is `opened`, invoked immeditately after it is opened.
         */
        west__onopen_end: OnEndCallback | null;

        /**
         * Callback for when pane is `closed`, invoked immeditately after it is closed.
         */
        west__onclose: OnEndCallback | null;

        /**
         * Callback for when pane is `closed`, invoked immeditately before it is closed.
         */
        west__onclose_start: OnStartCallback | null;

        /**
         * Callback for when pane is `closed`, invoked immeditately after it is closed.
         */
        west__onclose_end: OnEndCallback | null;

        /**
         * Callback for when pane is `resized`, invoked immeditately after it is resized.
         */
        west__onresize: OnEndCallback | null;

        /**
         * Callback for when pane is `resized`, invoked immeditately before it is resized.
         */
        west__onresize_start: OnStartCallback | null;

        /**
         * Callback for when pane is `resized`, invoked immeditately after it is resized.
         */
        west__onresize_end: OnEndCallback | null;

        /**
         * Callback for when pane is `swapped`, invoked immeditately after it is swapped.
         */
        west__onswap: OnEndCallback | null;

        /**
         * Callback for when pane is `swapped`, invoked immeditately before it is swapped.
         */
        west__onswap_start: OnStartCallback | null;

        /**
         * Callback for when pane is `swapped`, invoked immeditately after it is swapped.
         */
        west__onswap_end: OnEndCallback | null;
    }

    /**
     * Settings that apply to all panees and can also be set globally.
     */
    export interface BaseSubKeyLayoutSettings {
        /**
         * When this is enabled, the layout will apply basic styles directly to resizers & buttons. This is intended for
         * quick mock-ups, so that you can `see` your layout immediately. Normally this should be set as a default
         * option, but it can be set 'per-pane':
         */
        applyDefaultStyles: boolean;

        /**
         * If `true`, then when moused-over, the pane's zIndex is raised and overflow is set to `visible`. This allows
         * pop-ups and drop-downs to overlap adjacent panes.
         * 
         * __WARNING__: Enable this only for panes that do not scroll!
         */
        showOverflowOnHover: boolean;

        /**
         * MUST be a `child` of one of the panes.
         * 
         * Selector string for INNER div/element. This div will auto-size so only it scrolls, and not the entire pane.
         * 
         * Same class-name could be used for divs inside all panes.
         */
        contentSelector: string;

        /**
         * Selector string for INNER divs/elements. These elements will be `ignored` when calculations are done to
         * auto-size the content element. This may be necessary if there are elements inside the pane that are
         * absolutely-positioned and intended to `overlay` other elements.
         * 
         * Same class-name could be used for elements inside all panes
         */
        contentIgnoreSelector: string;

        /**
         * Used for auto-generated classNames for each 'layout pane'. Defaults to `ui-layout-pane`.
         */
        paneClass: string;

        /**
         * This is used as a prefix when generating classNames for 'custom buttons'. Do not confuse with normal
         * 'toggler-buttons'. Defaults to `ui-layout-button`.
         */
        buttonClass: string;
    }

    /**
     * Settings that need to be specified separately for each border or center pane.
     */
    export interface BorderAndCenterPaneSubKeyLayoutSettings {
        /**
         * Default values are: ".ui-layout-north", ".ui-layout-west", etc. Any valid jQuery selector string can be used
         * - classNames, IDs, etc.
         * 
         * To allow for `nesting` of layouts, there are rules for how pane-elements are related to the layout-container.
         * More flexibility was added in version 1.1.2 to handle panes are nested inside a `form` or other element.
         * 
         * Rules for the relationship between a pane and its container:
         * 
         * 1. When an `ID` is specified for paneSelector, the pane-element only needs to be a descendant of the
         * container - it does NOT have to be a `child`.
         * 1. When a 'class-name' is specified for paneSelector, the pane-element must be EITHER:
         *     - a child of the container, or...
         *     - a child of a form-element that is a child of the container (must be the 'first form' inside the container)
         */
        paneSelector: string;
    }

    /**
     * Settings that can be specified either globally or separately for each border pane.
     */
    export interface DefaultAndBorderPaneSubKeyLayoutSettings {
        /**
         * Can a pane be closed?
         */
        closable: boolean;

        /**
         * When open, can a pane be resized?
         */
        resizable: boolean;

        /**
         * When closed, can a pane 'slide open' over adjacent panes?
         */
        slidable: boolean;

        /**
         * Used for auto-generated classNames for each 'resizer-bar'. Defaults to `ui-layout-resizer`.
         */
        resizerClass: string;

        /**
         * Used for auto-generated classNames for each 'toggler-button'. Defaults to `ui-layout-toggler`.
         */
        togglerClass: string;

        /**
         * Specifies the initial size of the panes - `height` for north & south panes - `width` for east and west. If
         * `auto`, then pane will size to fit its content - most useful for north/south panes (to auto-fit your banner
         * or toolbar), but also works for east/west panes.
         * 
         * You normally will want different sizes for the panes, but a 'default size' can be set
         * 
         * Defaults to `auto` for north and south panes, and to `200` for east and west panes.
         */
        size: string;

        /**
         * Minimum-size limit when resizing a pane (0 = as small as pane can go).
         */
        minSize: number;

        /**
         * Maximum-size limit when resizing a pane (0 = as large as pane can go).
         */
        maxSize: number;

        /**
         * Spacing between pane and adjacent pane - when pane is `open`.
         */
        spacing_open: number;

        /**
         * Spacing between pane and adjacent pane - when pane is `closed`.
         */
        spacing_closed: number;

        /**
         * Tooltip when resizer-bar can be `dragged` to resize a pane.
         */
        resizerTip: string;

        /**
         * Opacity of resizer bar when `dragging` to resize a pane. Defaults to `1`.
         * 
         * This value is passed to the ui.draggable widget
         * 
         * Leave this set to `1` if you want to use CSS to control opacity. Otherwise you must use `!important` to
         * override the specified opacity.
         */
        resizerDragOpacity: number;

        /**
         * When enabled, layout will `mask` iframes on the page when the resizer-bar is `dragged` to resize a pane. This
         * solves problems related to dragging an element over an iframe.
         */
        maskIframesOnResize: boolean;

        /**
         * Tooltip when the resizer-bar will trigger 'sliding open'.
         */
        sliderTip: string;

        /**
         * Cursor when resizer-bar will trigger 'sliding open' - ie, when pane is `closed`. Defaults to `pointer`.
         */
        sliderCursor: string;

        /**
         * Trigger events to 'slide open' a pane. Defaults to `click`.
         */
        slideTrigger_open: string;

        /**
         * Trigger events to 'slide close' a pane. Defaults to `mouseout`.
         */
        slideTrigger_close: string;

        /**
         * Tooltip on toggler when pane is `open`.
         */
        togglerTip_open: string;

        /**
         * Tooltip on toggler when pane is `closed`.
         */
        togglerTip_closed: string;

        /**
         * Length of toggler button when pane is `open`. Length means `width` for north/south togglers, and `height` for
         * east/west togglers.
         * 
         * `100%` OR `-1` means 'full height/width of resizer bar' - `0` means `hidden`.
         */
        togglerLength_open: number | string;

        /**
         * Length of toggler button when pane is `closed`. Length means `width` for north/south togglers, and `height`
         * for east/west togglers.
         * 
         * `100%` OR `-1` means 'full height/width of resizer bar' - `0` means `hidden`.
         */
        togglerLength_closed: number;

        /**
         * If true, the toggler-button is hidden when a pane is 'slid-open'. This makes sense because the user only
         * needs to 'mouse-off' to close the pane.
         */
        hideTogglerOnSlide: boolean;

        /**
         * Alignment of toggler button inside the resizer-bar when pane is `open`. Defaults to `center`.
         * 
         * A positive integer means a pixel offset from top or left.
         * 
         * A negative integer means a pixel offset from bottom or right.
         */
        togglerAlign_open: number | PositionKeyword;

        /**
         * Alignment of toggler button inside the resizer-bar when pane is `closed`. Defaults to `center`.
         * 
         * A positive integer means a pixel offset from top or left.
         * 
         * A negative integer means a pixel offset from bottom or right.
         */
        togglerAlign_closed: number | PositionKeyword;

        /**
         * Usually a background-image is set in CSS to customize a toggler-button. However, you can also put text inside
         * a toggler by using these options. The text is wrapped in a SPAN, which is then added inside the toggler DIV.
         * The SPAN classes identify them as either `open` or `closed` content:
         */
        togglerContent_open: string;

        /**
         * Usually a background-image is set in CSS to customize a toggler-button. However, you can also put text inside
         * a toggler by using these options. The text is wrapped in a SPAN, which is then added inside the toggler DIV.
         * The SPAN classes identify them as either `open` or `closed` content:
         */
        togglerContent_closed: string;

        /**
         * If `true`, then 'cursor hotkeys' are enabled. Can be set per-pane if desired.
         * 
         * These default hotkeys cannot be changed - only enabled or disabled.
         * 
         * The cursor/arrow key must be pressed in combination with CTRL -or- SHIFT
         * 
         * - Toggle North-pane: `CTRL+Up` or `SHIFT+Up`
         * - Toggle South-pane: `CTRL+Down`  or `SHIFT+Down`
         * - Toggle West-pane: `CTRL+Left` or `SHIFT+Left`
         * - Toggle East-pane: `CTRL+Right` or `SHIFT+Right`
         * 
         * The SHIFT+ARROW combinations are ignored if pressed while the cursor is in a form field, allowing users to
         * 'select text' — eg: SHIFT+Right in a TEXTAREA.
         */
        enableCursorHotkey: boolean;

        /**
         * Custom hotkeys must be pressed in combination with either the CTRL or SHIFT key - or both together. Use this
         * option to choose which modifier-key(s) to use with the `customHotKey`.
         * 
         * Defaults to `SHIFT`.
         * 
         * If this option is missing or invalid, `CTRL+SHIFT` is assumed.
         * 
         * NOTE: The ALT key cannot be used because it is not detected by some browsers.
         */
        customHotkeyModifier: string;

        /**
         * Animation effect for open/close. Choose a preset effect OR can specify a custom fxName as long as you also
         * specify fxSettings (even if fxSettings is just empty - {}).
         * 
         * Defaults to `slide`.
         */
        fxName: string;

        /**
         * Animation effect for open. Choose a preset effect OR can specify a custom fxName as long as you also
         * specify fxSettings (even if fxSettings is just empty - {}).
         * 
         * Defaults to `slide`.
         */
        fxName_open: string;

        /**
         * Animation effect for close. Choose a preset effect OR can specify a custom fxName as long as you also
         * specify fxSettings (even if fxSettings is just empty - {}).
         * 
         * Defaults to `slide`.
         */
        fxName_close: string;

        /**
         * Animation effect for size. Choose a preset effect OR can specify a custom fxName as long as you also
         * specify fxSettings (even if fxSettings is just empty - {}).
         */
        fxName_size: string;

        /**
         * Speed of animations – standard jQuery keyword like `fast`, or a millisecond value. Defaults to `normal`.
         */
        fxSpeed: number | string;

        /**
         * Speed of animations – standard jQuery keyword like `fast`, or a millisecond value. Defaults to `normal`.
         */
        fxSpeed_open: number | string;

        /**
         * Speed of animations – standard jQuery keyword like `fast`, or a millisecond value. Defaults to `normal`.
         */
        fxSpeed_close: number | string;

        /**
         * Speed of animations – standard jQuery keyword like `fast`, or a millisecond value. Defaults to `normal`.
         */
        fxSpeed_size: number | string;

        /**
         * You can customize the default animation settings by passing new settings.
         * 
         * If a non-standard effect is specified, then fxSettings is REQUIRED (can be an empty object though).
         */
        fxSettings: Partial<FxSettings>;

        /**
         * You can customize the default animation settings by passing new settings.
         * 
         * If a non-standard effect is specified, then fxSettings is REQUIRED (can be an empty object though).
         */
        fxSettings_open: Partial<FxSettings>;

        /**
         * You can customize the default animation settings by passing new settings.
         * 
         * If a non-standard effect is specified, then fxSettings is REQUIRED (can be an empty object though).
         */
        fxSettings_close: Partial<FxSettings>;

        /**
         * You can customize the default animation settings by passing new settings.
         * 
         * If a non-standard effect is specified, then fxSettings is REQUIRED (can be an empty object though).
         */
        fxSettings_size: Partial<FxSettings>;

        /**
         * If `true`, then pane is `closed` when layout is created.
         */
        initClosed: boolean;

        /**
         * If `true`, then pane is `hidden` when layout is created - no resizer or spacing is visible, as if the pane
         * did not exist!
         */
        initHidden: boolean;

    }

    /**
     * Settings that can be specified either globally or separately for each center pane.
     */
    export interface DefaultAndCenterPaneSubKeyLayoutSettings {
    }


    /**
     * Settings that can be specified globally (`default`).
     */
    export interface DefaultSubKeyLayoutSettings extends BaseSubKeyLayoutSettings {
        /**
         * This option handles of bookmarks that are passed on the URL of the page:
         * http://www.site.com/page.html#myBookmark
         * 
         * The default functionality of bookmarks is broken when using a layout because the position and scrolling of
         * pane elements are altered after the page loads. Enabling this option (enabled by default) causes the bookmark
         * to be re-applied after the layout is created, causing the `pane` containing the bookmark/anchor to be
         * scrolled to bring it into view.
         * 
         * This option should be left enabled in most cases, but if content in the layout-panes is never bookmarked,
         * then you could disabled it.
         */
        scrollToBookmarkOnLoad: boolean;

        /**
         * Extends the `default` layout effects by specifying the options for the desired effect:
         * 
         * ```javascript
         * $("body").layout({
         *   effects: {
         *     // MODIFY a standard effect
         *     slide: {
         *       all: { duration: 500, easing: "bounceInOut" },
         *     },
         *     // ADD a new effect
         *     blind: {
         *       all: {},
         *       north: { direction: "vertical" },
         *       south: { direction: "vertical" },
         *       east:  { direction: "horizontal" },
         *       west:  { direction: "horizontal" },
         *     }
         *   }
         * });
         * ```
         */
        effects: Partial<EffectSettings>;
    }

    /**
     * Settings that can be specified for the center pane (`default`).
     */
    export interface CenterSubKeyLayoutSettings extends BaseSubKeyLayoutSettings, DefaultAndCenterPaneSubKeyLayoutSettings, BorderAndCenterPaneSubKeyLayoutSettings {
        /**
         * Minimum width of the center pane in pixels. 
         */
        minWidth: number;

        /**
         * Minimum height of the center pane in pixels. 
         */
        minHeight: number;

        /**
         * Maximum width of the center pane in pixels. 
         */
        maxWidth: number;

        /**
         * Maximum height of the center pane in pixels. 
         */
        maxHeight: number;
    }

    /**
     * Settings that can be specified for the border panes (`north`, `east`, `south`, or `west`).
     */
    export interface BorderSubKeyLayoutSettings extends BaseSubKeyLayoutSettings, DefaultAndBorderPaneSubKeyLayoutSettings, BorderAndCenterPaneSubKeyLayoutSettings {
        /**
         * This is the cursor when the mouse is over the 'resizer-bar'. However, if the mouse is over the
         * 'toggler-button' inside the resizer bar, then the cursor is a `pointer` - ie, the togglerCursor instead of
         * the resizerCursor.
         */
        resizerCursor: string;

        /**
         * If a hotkey is specified, it is automatically enabled. It does not matter whether 'cursor hotkeys' are also
         * enabled – those are separate.
         * 
         * You can specify any of the following values:
         * 
         * - letter from A to Z
         * - number from 0 to 9
         * - Javascript charCode value for the key
         */
        customHotkey: string;

        /**
         * Callback for when pane is `shown`, invoked immeditately after it is shown.
         */
        onshow: OnEndCallback | null;

        /**
         * Callback for when pane is `shown`, invoked immeditately before it is shown.
         */
        onshow_start: OnStartCallback | null;

        /**
         * Callback for when pane is `shown`, invoked immeditately after it is shown.
         */
        onshow_end: OnEndCallback | null;

        /**
         * Callback for when pane is `hidden`, invoked immeditately after it is hidden.
         */
        onhide: OnEndCallback | null;

        /**
         * Callback for when pane is `hidden`, invoked immeditately before it is hidden.
         */
        onhide_start: OnStartCallback | null;

        /**
         * Callback for when pane is `hidden`, invoked immeditately after it is hidden.
         */
        onhide_end: OnEndCallback | null;

        /**
         * Callback for when pane is `opened`, invoked immeditately after it is opened.
         */
        onopen: OnEndCallback | null;

        /**
         * Callback for when pane is `opened`, invoked immeditately before it is opened.
         */
        onopen_start: OnStartCallback | null;

        /**
         * Callback for when pane is `opened`, invoked immeditately after it is opened.
         */
        onopen_end: OnEndCallback | null;

        /**
         * Callback for when pane is `closed`, invoked immeditately after it is closed.
         */
        onclose: OnEndCallback | null;

        /**
         * Callback for when pane is `closed`, invoked immeditately before it is closed.
         */
        onclose_start: OnStartCallback | null;

        /**
         * Callback for when pane is `closed`, invoked immeditately after it is closed.
         */
        onclose_end: OnEndCallback | null;

        /**
         * Callback for when pane is `resized`, invoked immeditately after it is resized.
         */
        onresize: OnEndCallback | null;

        /**
         * Callback for when pane is `resized`, invoked immeditately before it is resized.
         */
        onresize_start: OnStartCallback | null;

        /**
         * Callback for when pane is `resized`, invoked immeditately after it is resized.
         */
        onresize_end: OnEndCallback | null;

        /**
         * Callback for when pane is `swapped`, invoked immeditately after it is swapped.
         */
        onswap: OnEndCallback | null;

        /**
         * Callback for when pane is `swapped`, invoked immeditately before it is swapped.
         */
        onswap_start: OnStartCallback | null;

        /**
         * Callback for when pane is `swapped`, invoked immeditately after it is swapped.
         */
        onswap_end: OnEndCallback | null;
    }

    /**
     * An object that contains the DOM elements for all existing panes.
     */
    export interface PaneElements {
        /**
         * The DOM element for the north pane, if it exists, or `false` otherwise. 
         */
        north: JQuery | false;

        /**
         * The DOM element for the south pane, if it exists, or `false` otherwise. 
         */
        south: JQuery | false;

        /**
         * The DOM element for the east pane, if it exists, or `false` otherwise. 
         */
        east: JQuery | false;

        /**
         * The DOM element for the west pane, if it exists, or `false` otherwise. 
         */
        west: JQuery | false;

        /**
         * The DOM element for the center pane. 
         */
        center: JQuery;
    }

    /**
     * An object that contains the DOM elements for the content of all existing panes.
     */
    export interface PaneContents {
        /**
         * The DOM element(s) with the contents of the north pane, or `false` otherwise. 
         */
        north: JQuery | false;

        /**
         * The DOM element(s) with the contents of the south pane, or `false` otherwise. 
         */
        south: JQuery | false;

        /**
         * The DOM element(s) with the contents of the east pane, or `false` otherwise. 
         */
        east: JQuery | false;

        /**
         * The DOM element(s) with the contents of the west pane, or `false` otherwise. 
         */
        west: JQuery | false;

        /**
         * The DOM element(s) with the contents of the center pane, or `false` otherwise. 
         */
        center: JQuery;
    }

    /**
     * An object that contains the DOM elements for the resizers of all existing panes.
     */
    export interface PaneResizers {
        /**
         * The DOM element(s) with the resizer for the north pane, or `false` otherwise. 
         */
        north: JQuery | false;

        /**
         * The DOM element(s) with the resizer for the south pane, or `false` otherwise. 
         */
        south: JQuery | false;

        /**
         * The DOM element(s) with the resizer for the east pane, or `false` otherwise. 
         */
        east: JQuery | false;

        /**
         * The DOM element(s) with the resizer for the west pane, or `false` otherwise. 
         */
        west: JQuery | false;

        /**
         * The DOM element(s) with the resizer for the center pane, or `false` otherwise. 
         */
        center: JQuery;
    }

    /**
     * An object that contains the DOM elements for the togglers of all existing panes.
     */
    export interface PaneTogglers {
        /**
         * The DOM element(s) with the toggler for the north pane, or `false` otherwise. 
         */
        north: JQuery | false;

        /**
         * The DOM element(s) with the toggler for the south pane, or `false` otherwise. 
         */
        south: JQuery | false;

        /**
         * The DOM element(s) with the toggler for the east pane, or `false` otherwise. 
         */
        east: JQuery | false;

        /**
         * The DOM element(s) with the toggler for the west pane, or `false` otherwise. 
         */
        west: JQuery | false;

        /**
         * The DOM element(s) with the toggler for the center pane, or `false` otherwise. 
         */
        center: JQuery;
    }

    /**
     * An object containing the dimensions of all the elements, including the layout container.
     */
    export interface LayoutState {
        /**
         * The ID of this layout. 
         */
        id: string;

        /**
         * Whether this layout widget is already initialized. 
         */
        initialized: boolean;

        /**
         * Whether a pane is currently being resized. 
         */
        paneResizing: boolean;

        /**
         * Whether a pane is currently in the process of sliding. 
         */
        panesSliding: boolean;

        /**
         * Dimensions of the layout container with all panes. 
         */
        container: ContainerState

        /**
         * The state and current dimensions of the north pane, if it exists. 
         */
        north: BorderPaneState;

        /**
         * The state and current dimensions of the south pane, if it exists. 
         */
        south: BorderPaneState;

        /**
         * The state and current dimensions of the west pane, if it exists. 
         */
        west: BorderPaneState;

        /**
         * The state and current dimensions of the east pane, if it exists. 
         */
        east: BorderPaneState;

        /**
         * The state and current dimensions of the center pane. 
         */
        center: CenterPaneState;
    }

    /**
     * Describes an inset around an element, in pixels. 
     */
    export interface Inset {
        /**
         * Inset to the left, in pixels. 
         */
        left: number;

        /**
         * Inset to the right, in pixels. 
         */
        right: number;

        /**
         * Inset to the top, in pixels. 
         */
        top: number;

        /**
         * Inset to the bototm, in pixels. 
         */
        bottom: number;
    }

    /**
     * Describes the  limit for the position of the resizer, in pixels. 
     */
    export interface ResizerPositionLimit {
        /**
         * Minimum value for the resizer position, in pixels. 
         */
        min: number;

        /**
         * Maximum value for the resizer position, in pixels. 
         */
        max: number;
    }

    /**
     * Represents the current state shared by container, border panes and center panes, such as its computed dimensions. 
     */
    export interface BaseState {
        /**
         * The CSS styles for this pane or container. 
         */
        css: Record<string, string | number>;

        /**
         * The inner height of this pane or container in pixels. 
         */
        innerHeight: number;

        /**
         * The inner width of this pane or container in pixels. 
         */
        innerWidth: number;

        /**
         * The inset of this pane or container in pixels. 
         */
        inset: Inset

        /**
         * The outer height of this pane or container, in pixels. 
         */
        outerHeight: number;

        /**
         * The outer width of this pane or container, in pixels. 
         */
        outerWidth: number;

        /**
         * The layoutted height of this pane or container, in pixels. 
         */
        layoutHeight: number;

        /**
         * The layoutted width of this pane or container, in pixels. 
         */
        layoutWidth: number;

        /**
         * The left ofset of this pane or container, in pixels. 
         */
        offsetLeft: number;

        /**
         * The top ofset of this pane or container, in pixels. 
         */
        offsetTop: number;

        /**
         * The tag name of the HTML element for this pane or container. 
         */
        tagName: string;
    }

    /**
     * Represents the current state shared by both a border and center pane, such as its type. 
     */
    export interface BasePaneState extends BaseState {
        /**
         * The index of this pane. 
         */
        childIdx: number;

        /**
         * The type of this pane, whether it is a center or a border pane. 
         */
        edge: BorderPane | CenterPane;

        /**
         * Whether this pane is currently visible. 
         */
        isVisible: boolean;

        /**
         * When there is 'not enough room' for a pane to be displayed, it is automatically hidden. In this case,
         * isHidden=true AND noRoom=true.
         * 
         * This is different than hiding a pane using the hide() command, because when there is enough room for the pane
         * to appear (by resizing or closing a pane), the hidden pane will reappear automatically.
         * 
         * This is the only logic var that also applies to the 'center-pane'.
         */
        noRoom: boolean

        /**
         * The width of the north and bottom or the height east and west resizer, in pixels. 
         */
        resizerLength: number;
    }

    /**
     * Represents the current state of a border pane, such as its computed dimensions. 
     */
    export interface BorderPaneState extends BasePaneState {
        /**
         * Whether this pane resizes itself automatically. 
         */
        autoResize: boolean;

        /**
         * Whether this border pane is currently closed. 
         */
        isClosed: boolean;
        /**
         * A 'hidden pane' is not the same as a 'closed pane'. When a pane is hidden, it is as if it does not exist -
         * there is no resizer-bar or toggler-button visible, and the 'pane spacing' collapses.
         * 
         * So if a pane is hidden, you must have some other way to make it visible again. This means a custom button or
         * some other custom code that calls the show() command.
         */
        isHidden: boolean;

        /**
         * This is only true when the user is in the process of `dragging` the resizer bar.
         */
        isResizing: boolean;

        /**
         * When a pane is `sliding`, it was opened by sliding over-top of other panes. The pane is `open`, but only
         * temporarily - until the user moves their mouse off.
         * 
         * This is different from when a pane is 'opened normally', and becomes fixed in place, and all adjacent panes
         * are resized.
         * 
         * When isSliding=true, isClosed=false - when it slides closed again, isClosed=true.
         * 
         * When a pane is 'opened normally', isClosed=false AND isSliding=false.
         */
        isSliding: boolean;

        /**
         * The maximum allowed size of border pane, in pixels. 
         */
        maxSize: number;

        /**
         * The minimum allowed size of border pane, in pixels. 
         */
        minSize: number;

        /**
         * List of CSS selectors for all pins that were added to this border pane. 
         */
        pins: string[]

        /**
         * The limits for the resizer position, in pixels 
         */
        resizerPosition: ResizerPositionLimit;

        /**
         * The current size of this border pane, in pixels. 
         */
        size: number;
    }

    /**
     * Represents the current state of a center pane, such as its computed dimensions. 
     */
    export interface CenterPaneState extends BasePaneState {
        /**
         * The maximum height of this center pane, in pixels. 
         */
        maxHeight: number;

        /**
         * The maximum width of this center pane, in pixels. 
         */
        maxWidth: number;

        /**
         * The minimum height of this center pane, in pixels. 
         */
        minHeight: number;

        /**
         * The minimum width of this center pane, in pixels. 
         */
        minWidth: number;
    }

    /**
     * Represents the current state of a layout container, such as its ID and class name. 
     */
    export interface ContainerState extends BaseState {
        /**
         * One or more CSS classes of this container. 
         */
        className: string;

        /**
         * A selector for this container. 
         */
        selector: string;

        /**
         * Element referenced by this container, e.g. `BODY`. 
         */
        ref: string;

        /**
         * The ID of this container. 
         */
        id: string;

        /**
         * Whether this container is the BODY HTML element. 
         */
        isBody: boolean;
    }

    export interface LayoutInstance {
        /**
         * The container DOM element of this layout widget. 
         */
        container: JQuery;

        /**
         * The DOM elements representing the content of each pane. 
         */
        contents: PaneContents;

        /**
         * The current settings of this layout widget. 
         */
        options: Partial<SubKeyLayoutSettings>;

        /**
         * The DOM container elements for each pane. 
         */
        panes: PaneElements;

        /**
         * The DOM elements representing the resizers for each pane. 
         */
        resizers: PaneResizers;

        /**
         * The current layout state, such as the computed dimensions or whether the individual panes are visible. 
         */
        state: LayoutState;

        /**
         * The DOM elements representing the togglers for each pane. 
         */
        togglers: PaneTogglers;

        /**
         * Toggle the specified pane open or closed - the opposite of its current state.
         * @param pane A pane to toggle.
         */
        toggle(pane: BorderPane): void;

        /**
         * Open the specified pane. If the pane is already open, nothing happens. If the pane is currently `hidden` (not
         * just `closed`), then the pane will be unhidden and opened.
         * @param pane A pane to open.
         */
        open(pane: BorderPane): void;

        /**
         * Close the specified pane. If the pane is already closed, nothing happens.
         * @param pane A pane to close.
         */
        close(pane: BorderPane): void;

        /**
         * Hide the specified pane. When a pane is hidden, it has no spacing or resizer bar or toggler button - it is
         * completely invisible, as if it did not exist.
         * @param pane A pane to hide.
         */
        hide(pane: BorderPane): void;

        /**
         * Un-hide the specified pane. Normally also opens the pane, but if you pass `false` as the second parameter,
         * then pane will un-hide, but be `closed` (slider-bar and toggler now visible).
         * @param pane A pane to shown.
         * @param openPane Whether the pane is also opened.
         */
        show(pane: BorderPane, openPane?: boolean): void;

        /**
         * This sizes the pane in the direction it opens and closes - for north and south panes, `size=outerHeight`, for
         * east and west panes, `size=outerWidth`. The pane will only resize within its minSize and maxSize limits.
         * @param pane A pane to resize.
         * @param sizeInPixels The new size in pixels for the pane.
         */
        sizePane(pane: BorderPane, sizeInPixels: number): void;

        /**
         * Resizes the `content container` inside the specified pane. Content is resized automatically when the pane is
         * resized or opened, so this method only needs to be called manually if something changes the height of a
         * header or footer element in the pane.
         * @param pane A pane with content to resize. 
         */
        resizeContent(pane: BorderPane | CenterPane): void;

        /**
         * Resizes the entire layout to fit its container. This method runs automatically for all layouts when the
         * browser window is resized. When a layout is inside another element (its `container`), and that container
         * element can be sized without resizing the entire window, then `layout.resizeAll()` should be called so the
         * inner layout will resize when its container resizes.
         */
        resizeAll(): void;

        /**
         * Adds a button that can close a pane.
         * 
         * First create the elements to use as a button, then call this method and pass the button.
         * 
         * @param selector A jQuery selector string to access the button elements.
         * @param pane Pane to which to add the buttons.
         */
        addCloseBtn(selector: string, pane: BorderPane): void;

        /**
         * Adds a button that can open a pane.
         * 
         * First create the elements to use as a button, then call this method and pass the button.
         * 
         * @param selector A jQuery selector string to access the button elements.
         * @param pane Pane to which to add the buttons.
         */
        addOpenBtn(selector: string, pane: BorderPane): void;

        /**
         * Adds a button that can toggle a pane (opening or closing it).
         * 
         * First create the elements to use as a button, then call this method and pass the button.
         * 
         * @param selector A jQuery selector string to access the button elements.
         * @param pane Pane to which to add the buttons.
         */
        addToggleBtn(selector: string, pane: BorderPane): void;


        /**
         * Makes an element act like a `pin button` for the given pane.
         * @param selector A jQuery selector string to access the button elements.
         * @param pane Pane to which to add the buttons.
         */
        addPinBtn(selector: string, pane: BorderPane): void;

        /**
         * Raises a pane's z-index so popups work properly.
         * @param pane Pane for which to allow overflow.
         */
        allowOverflow(pane: BorderPane | CenterPane | JQuery): void;

        /**
         * Undoes the modifications made by `allowOverflow`, resetting the z-index to its orignal value.
         * @param pane Pane for which to reset the overflow.
         */
        resetOverflow(pane: BorderPane | CenterPane | JQuery): void;
    }

    export interface LayoutStatics {
        /**
         * The version of the layout plugin, as a string, e.g. `1.5.12` 
         */
        version: string;

        /**
         * The version of the layout plugin, as a number, e.g. `1.0512` 
         */
        revision: number;
    }
}

interface JQuery {
    /**
     * Initializes jQuery layout plugin on the current set of elements.
     * @param settings Settings for customizing the layout.
     * @return this jQuery instance for chaining, or `false` if the layout widget could not be initialized, e.g. if it
     * was already initialized.
     */
    layout(settings?: Partial<JQueryLayout.SubKeyLayoutSettings | JQueryLayout.ListLayoutSettings>): false | JQueryLayout.LayoutInstance;
}

interface JQueryStatic {
    /**
     * Static utility constants and methods used by the jQuery layout plugin.
     */
    layout: JQueryLayout.LayoutStatics;
}