This is the migration guide to different PrimeFaces versions. Feel free to drop a comment to add any missing points. Please note that this is not a changelog as it only lists changes that may cause backward compatibility issues in your applications.

## 8.0 to 9.0

#### Breaking Changes

* Dock
  * Re-implemented using CSS. Deprecated props `itemWidth, maxWidth, proximity` for CSS instead. (https://primefaces.github.io/primefaces/9_0/#/components/dock)
* Diagram
  * `paintStyle` has changed from `setPaintStyle("{strokeStyle:'#98AFC7',fillStyle:'#5C738B',lineWidth:3}")` to `setPaintStyle("{stroke:'#98AFC7',fill:'#5C738B',strokeWidth:3}")`
* Dashboard
  * `disabled` attribute has been renamed into `reordering` for reordering panels
  * `disabled` attribute is now like all other disabled attributes to disable the component
* FileUpload
  * `FileUpload#performVirusScan` attribute has been renamed into `virusScan`
  * `VirusScanner#performVirusScan(InputStream in)` has been refactored into `VirusScanner#scan(UploadedFile file)`
* ChartJS
  * `offset` for Bar charts is no longer `true` by default see: (https://github.com/primefaces/primefaces/issues/5726)
* Themes
  * **Removed** `aristo` theme and made `nova-light` the default theme
* Layout
  * **Removed** `Layout` component in favor or pure CSS. PF Extensions still has a similar Layout component
* Editor
  * **Removed** `Editor` component in favor `TextEditor`.
* InputMask
  * Optional section of mask no longer starts with `?` but must be wrapped in `[` and `]`.
* Schedule
  * The JavaScript library (FullCalendar) was updated to version 5. It uses a different DOM structure with potentially new or changed CSS style classes. Check if your custom CSS for the schedule still applies if you have any.
  * If you are using a custom `extender` function, check your JavaScript code: Many FullCalendar settings have been renamed or changed - see the FullCalendar upgrade guide at https://fullcalendar.io/docs/upgrading-from-v4
  * New Locale customization labels for `year, list, moreLinkText, noEventsText`
* Client-Side-Validation
  * `PrimeFaces.util.ValidationContext` now is `PrimeFaces.validation.ValidationContext`
  * `PrimeFaces.validation.ValidationContext` is now a a real "context object" and most util methods has been moved to `PrimeFaces.validation.Utils`
* Facelet function `p:resolveWidgetVar` now returns the widget var name only, instead of `PF('widgetVarName')`.
* AutoComplete
  * Event `moreText` is renamed to `moreTextSelect`.
* DataTable
  * TODO: sorting reworked - see https://github.com/primefaces/primefaces/pull/6390

#### Others
* Cache
  * HazelCast now requires version 4.X see: (https://github.com/primefaces/primefaces/issues/5989)
* Exporter
  * PDF lib has been switched from iText to Libre OpenPDF: (https://github.com/primefaces/primefaces/issues/5749)
* Schedule
  * The schedule component has two properties `resizable` and `draggable` that were supposed to be used as the default for all events. Before  9.0, however, these two properties did not have any effect, as they were always overwritten by the `ScheduleEvent#isEditable` property. To remedy this, the `ScheduleEvent` interface now contains two new methods `ScheduleEvent#isResizable` and `ScheduleEvent#isDraggable` that should be used instead of the deprecated `ScheduleEvent#isEditable`. _Please note that these two methods may return null_, to indicate that they do not overwrite the default setting of the schedule. Likewise, the builder has `DefaultScheduleEvent.Builder#resizable` and `DefaultScheduleEvent.Builder#draggable`.
  * The event properties `resizable` and `draggable` default to `null`, and the schedule properties defaults to `true`. This means that, like previously, events are editable by default.
* DataTable, DataView, DataList and DataGrid detect lazy-attribute automatically based on value-binding to LazyDataModel. So no need to set this explicit anymore.
* DatePicker: showTime is detected automatically based on value-binding to LocalDateTime. timeOnly is detected automatically based on value-binding to LocalTime. In this usecases you don´t need to set this attributes explicit when you are happy with this default-behaviour.

#### New Features
* FileUpload
  * supports chunking and resume (see https://primefaces.github.io/primefaces/9_0/#/components/fileupload?id=chunking-and-resume)
* FileDownload
  * supports ajax (so `ajax="false"` is not required anymore) (see https://primefaces.github.io/primefaces/9_0/#/components/filedownload?id=ajax-downloading)
* MultiViewState
  * supports client window mode (see https://primefaces.github.io/primefaces/9_0/#/core/multiviewstate)
* Prime Client Window
  * improved implementation of JSF Client Window mode (see https://primefaces.github.io/primefaces/9_0/#/core/clientwindow)
* DatePicker
  * added (lazy) metamodel to set disabled and style class (see https://primefaces.github.io/primefaces/9_0/#/components/datepicker?id=date-metadata-model)

## 7.0 to 8.0

#### Breaking Changes
* PrimeFaces is now only compatible with Java8+
* Removed long time deprecated image attribute on buttons
* Facelet function `p:component` has been removed in favor of: `p:resolveFirstComponentWithId('myId', view).clientId`
* Facelet function `p:widgetVar` have been removed in favor of: `p:resolveWidgetVar('myTable', view)`
* StreamedContent API
  * `DefaultStreamedContent`: Constructors and setters have been deprecated in favor of `DefaultStreamedContent.Builder`
  * `ByteArrayContent` has been removed in favor of `DefaultStreamedContent.Builder#stream(() -> new ByteArrayInputStream(...))`
  * `LazyDefaultStreamedContent` has been removed, instead use `DefaultStreamedContent` with a `Supplier` which will always "lazy-creates" the InputStream: `DefaultStreamedContent.Builder#stream(() -> new FileInputStream(...))`
* DataTable
  * Methods `PrimeFaces#clearTableStates()` and `PrimeFaces#clearDataListStates()` have been deprecated in favor of `PrimeFaces#multiViewState()`
  * DataTable: `getFilters` / `setFilters` has been removed in favor of `getFilterBy` / `setFilterBy`
  * `LazyDataModel#load` signatures has been changed; sortBy is now a Map instead of List, filterBy Map value is now a `FilterMeta` object instead the filter value.
* Menu
  * `DynamicMenuModel` has been removed, use `BaseMenuModel` instead.
* FileUpload API
  * Models have been moved from `org.primefaces.model` to `org.primefaces.model.file`
  * `UploadedFile#getContents` has been renamed to `getContent`
  * `UploadedFile#getInputstream` has been renamed to `getInputStream`
  * `FileUpload#fileUploadListener` has been renamed into `listener`
  * In case of using multiple feature in a simple mode, use `UploadedFiles` model to get all files in a single request.
* DataExporter API
  * `org.primefaces.component.export.Exporter` interface allows you to implement your own exporter using `exporter` attribute
  * Removal of `repeat` attribute: used as a workaround in case of tables contained in an iterator component _(e.g `ui:repeat`, `p:tabView` etc.)_. No longer necessary, instead, define every ids component you wish to export (without specifying component index)
  * Apache POI minimum version 4.1.1 to use Excel export
* TextEditor for security reasons (https://github.com/primefaces/primefaces/issues/5163) now requires the OWASP Sanitizer library by default but you can choose to opt-out of using the library by setting the attribute `secure="false"`.
* InputMask for security reasons (https://github.com/primefaces/primefaces/issues/5105) validates the mask used on the server side. If you would like to disable that set validateMask="false".  Especially if you use a custom mask with `$.mask.definitions` for example: https://github.com/primefaces/primefaces/issues/3234#issuecomment-361221532
* `p:watermark`has been reimplemented with a simple HTML placeholder attribute. Therefore `PrimeFaces.cleanWatermarks()` and `PrimeFaces.showWatermarks()` has been removed.
* InputNumber: upgraded using [Autonumeric](http://autonumeric.org/)
  * `emptyValue`: `sign` is now `always`. Default value is `focus`
  * `symbolPosition`: `prefix` is now `p`, `suffix` is now `s` default is `p`
  * `decimalPlaces`: now defaults to 2. If value is Integer/Long/Short number defaults to 0.
* TimeLine: updated to the newest [vis](https://visjs.org/) timeline version.
  * `TimelineEvent` uses `java.time.LocalDateTime` instead of `java.util.Date`
  * `TimelineEvent` has now 3 more properties to fine tunning editable: editableTime, editableGroup and editableRemove.
  * `TimelineGroup` has now styleClass property similar to the one in TimelineEvent.
  * Properties deprecated in favor of others: `axisOnTop`, `groupsChangeable`, `groupsWidth`, `groupMinHeight`, `snapEvents`, `timeChangeable`.
  * Properties deprecated with NO replacement: `dragAreaWith` (use css classes instead), `unselectable`, `groupsOnRight`, `animate`, `animateZoom`, `showButtonNew`, `showNavigation`, `browserTimeZone`.
  * New properties: `maxHeight`, `orientationAxis`, `orientationItem`, `editableAdd`, `editableTime`, `editableGroup`, `editableRemove`, `editableOverrideItems`, `eventStyle `, `eventHorizontalMargin`, `eventVerticalMargin`, `groupStyle`, `snap`, `clickToUse`, `clientTimeZone`.
  * New `extender` property to specify a javascript function to extend configuration of vis.js timeline component similar to extender property in `<p:chart />` component.
* Schedule upgraded to FullCalendar 4.x
  * `ScheduleEvent` uses `java.time.LocalDateTime` instead of `java.util.Date`
  * Removed deprecated attributes
  * Localization comes with FullCalendar: does not require/use PF-localization. Just set `locale="fr"` for example on the Schedule.
  * New view names (`dayGridMonth`, `dayGridWeek`, `dayGridDay`, `timeGridWeek`, `timeGridDay`, `listYear`,` listMonth`, `listWeek`, `listDay`): old ones are translated by Schedule component
  * `ScheduleEntryMoveEvent`: new properties `yearDelta` and `monthDelta`
  * `ScheduleEntryResizeEvent`: new properties `yearDelta` and `monthDelta`: splitted properties between start and end (up to PF 7.0 only end)
  * Property `slotLabelFormat` was removed (only available in commercial version of FullCalendar - see https://fullcalendar.io/docs/slotLabelFormat)
  * `ScheduleEvent`: new properties `groupId` and `overlapAllowed`
* Calendar and DatePicker: Without a value-binding it converts OOTB to java `java.time.LocalDate`. (up to PrimeFaces 7.0:  `java.util.Date`) This may be relevant when you use Calendar or DatePicker as a filter for eg DataTable. You can add something like `<f:convertDateTime type="date" />` to your Calendar or DatePicker to get a java.util.Date.
* `org.primefaces.json` has been moved to `org.primefaces.shaded.json`

#### Others
* Fluent builders
  * Menu: instanciate menu items using builders (e.g `DefaultMenuItem#builder()`)
  * Timeline: instanciate timeline events using builders (e.g `TimelineEvent#builder()`)
  * Schedule: instanciate schedule events using builders (e.g `DefaultScheduleEvent#builder()`)
  * DefaultStreamedContent: instanciate `DefaultStreamedContent` using builders (e.g `DefaultStreamedContent#builder()`)
  * All constructors with arguments are deprecated
* Calendar and DatePicker offer built-in support for `java.time.LocalDate`, `java.time.LocalDateTime` and `java.time.LocalTime` (no converter needed anymore)
* Many events (eg SelectEvent, UnselectEvent, RowEditEvent, etc.) now use generics
* Cropper: change libraries from JCrop to Cropper.js
* PanelGrid: new layout="flex" based on PrimeFlex/FlexGrid
* Menu
  * `MenuModel#addElement()` has been deprecated, add element using `MenuModel#getElements()#add()` instead.

## 6.2 to 7.0

#### Breaking Changes
* Push has been removed. Please use the JSF2.3 socket or OmniFaces now.
* Mobile has been removed in favor of responsive features.
* `RequestContext` has been replaced by `PrimeFaces.current()`. `RequestContext` is still available as `PrimeRequestContext` but it's internal PrimeFaces API and should therefore be avoided to use.
* autoUpdate attribute of outputPanel, fragment, messages and growl has been removed. Use `p:autoUpdate` instead.
* OverlayPanel: `appendToBody` has been removed. Use `appendTo="@(body)"` instead.
* Button/Link/MenuItem: The `url`/`href` attribute isn't automatically prepended by contextPath anymore. Use the `outcome` attribute for referencing JSF views in the same application or manually prepend `url`/`href` with #{request.contextPath}. See https://github.com/primefaces/primefaces/issues/3506.
* rowsPerPageTemplate `*` support removed. Use `{ShowAll|'*'` instead.

### Others
* Schedule: Schedule was updated to the newest FullCalender version and therefore some attributes are deprecated.
  * Instead of slotMinutes, use slotDuration now (format: 12:30:00).
  * Instead of firstHour, use scrollTime now (format 12:30:00).
  * Instead of ignoreTimezone, use clientTimezone='local'
  * Instead of axisFormat, use slotLabelFormat

## 6.1 to 6.2

#### Breaking Changes
* As we upgraded to jQuery 3.x and did some refactoring on escaping, escaping ':' in PFS currently only requires 1 slash instead of 2 in the xhtml. See https://github.com/primefaces/primefaces/issues/2395
* Two attributes on p:outputPanel, 'delay' and 'global', have been removed. You need to define them via:
`<p:ajax event="load" delay="1000" global="true" />`. See https://github.com/primefaces/primefaces/issues/2829
* p:outputLabel: indicateRequired=true must be refactored to indicateRequired=auto or removed. See https://github.com/primefaces/primefaces/issues/2854.
* ROME has been updated to com.rometools:rome and therefore the package has been changed. See https://github.com/primefaces/primefaces/issues/2406
* SelectOneMenu / ThemeSwitcher: If custom content (with p:column(s) as child) is used with POJOs and you have a default f:selectItem like "Choose...", null (value="#{null}") instead of empty string (value="") should be used. Otherwise the properties on the var can't be resolved and a exception will be thrown.
* DataExporter - Apache POI minimum version 3.17 to use Excel export

### Others
* autoUpdate attribute of outputPanel, fragment, messages and growl has been deprecated. In 6.2 every component can be autoUpdateable by adding `<p:autoUpdate />` as child. See https://github.com/primefaces/primefaces/issues/2838
* p:component has been deprecated. Use `p:resolveClientId('@id(myTable)', view)` instead of `p:component('myTable')`. It also supports all search expressions other than ids. See https://github.com/primefaces/primefaces/issues/2771
* p:widgetVar has been deprecated. Use `p:resolveWidgetVar('myTable', view)` instead of `p:widgetVar('myTable')`. It also supports all search expressions other than ids. See https://github.com/primefaces/primefaces/issues/2771
* Many methods of RequestContext are deprecated now as we have done an internal splitup of internal functionalities and helpers for the end user. The deprecated methods in RequestContext are marked as @Deprecated and contain a hint for the new methods to use. The new base for helpers for the end user is now `PrimeFaces.current()`. RequestContext will be cleaned up and removed in the next release. See https://github.com/primefaces/primefaces/issues/2853


## 6.0 to 6.1
* If you use p:outputLabel, which targets a composite component, the composite component must use cc:editableValueHolder to identify the target input component. NOTE: a label must actually target a input component!
If you don't use cc:editableValueHolder, a NPE will occur. See https://github.com/primefaces/primefaces/issues/2163.
* Search Expression Framework (SEF): SEF was aligned with JSF 2.3 and @next, @previous and @child now skips plain html markup.
* DefaultTreeNode equals/hashCode has been changed to inlcude the rowKey, this could break your code. See: https://github.com/primefaces/primefaces/issues/1433

## 5.3 to 6.0
* If manually included, the primefaces.js include must be changed to include core.js and components.js
* The same also applies for the MenuRenderers of all PrimeFaces Layouts (before new layout versions will be released).
  Please change
  `@ResourceDependency(library="primefaces", name="primefaces.js")`
  to
  `@ResourceDependency(library="primefaces", name="core.js"),
    @ResourceDependency(library="primefaces", name="components.js")`
* Following components were migrated from PrimeFaces Extensions to PrimeFaces:
  - InputNumber
  - Timeline
  - Knob
  - KeyFilter
  - ImportConstants
  - ImportEnum
* ImportConstants: className has been renamed to type
* Search Expressions: SEF was refactored to skip unrendered components per default. If you would like to search for unrendered components in your application, please pass `SearchExpressionFacade#Options#VISIT_UNRENDERED` as option.
* "last" facet for resources: before 6.0, it was possible to use the last facade inside any tag to add resources. We never implemented this feature so it was just a random feature as the facet always belongs to the direct parent. Please use the "last" facet only in h:head.
* Dynamic Resource Loading: We reimplemented this feature, so that it will even work for other libraries like PrimeFaces Extensions. If you dynamically add components via Java code, please make sure you create the component via the `#createComponent` API:
`ColorPicker colorPicker = (ColorPicker) FacesContext.getCurrentInstance().getApplication().createComponent(ColorPicker.COMPONENT_TYPE);`
instead:
`ColorPicker colorPicker = new ColorPicker();`
* Per default, DataTable now skips processing child components for some events like paging. If you need child processing, you can set skipChildren="false" on p:ajax.
* GMap Rectangles: SouthWest and NorthEast was rendered the wrong direction. You probably need to switch the constructor parameters of LatLngBounds now.
* ContextMenu: p:contextMenu must be placed AFTER the target component (e.g. datatable) in the xhtml now.
* CommandButton: onclick will not be rendered if disable is set to false. This may cause unexpected behavior if the component is enabled on the client.

## 5.2 to 5.3
The captcha API changes to V2.

## 5.1 to 5.2
Fully backward compatible.

## 5.0 to 5.1
* Support for literal texts in filterBy-sortBy expressions were deprecated in 5.0 and it is removed in 5.1. These attributes only work with value expression as in the past.
* Deprecated chart components are removed.

## 4.0 to 5.0
* Chart components are deprecated in favor of new generic chart component with new Chart API. Old chart components are still supported but will be removed in a future release.
* ToolbarGroup deprecated, use left and right facets of toolbar instead. (Reverted back as of 5.0.1, both facets and toolbar groups will be supported instead)
* Defining fields in sortBy-filterBy attributes is deprecated use a value expression instead.e.g. sortBy="#{user.name}" instead of sortBy="name"
* DataTable frozen rows feature take an integer value instead of a collection from now on. This value defined how many rows from the start should be frozen.
* LazyDataModel's filters parameter changed to Map\<String,Object\> instead of Map\<String,String\> as a requirement of the new Advanced Filtering Feature.
* Dialog: appendToBody was removed in favor of appendTo="@(body)" to gain more flexibility
* Watermark: forElement was removed in favor of for="@(yourSelector)"
* Widgets must be referenced via "PF". e.g. PF('widgetVarName').show() instead of widgetVarName.show();
* DataTable layout changed to table-layout:fixed.
* PrimeFaces Push is reimplemented, PushContext is deprecated, use EventBus instead along with the new Push API.
* ScrollPanel is reimplemented, usage is backward compatible however UI is slightly different.

## 3.5 to 4.0
* MenuModel is rewritten and not backward compatible with the old version.
* DataTable sortBy and filterBy expressions require the plain property name meaning "name" instead of "#{person.name}". Backward compatibility is maintained for expressions like "#{var.property}" but not for complex expressions.
* FileUpload is reimplemented and it is backward compatible except "showButtons" option is removed.
disabledSelection option of column, moved to DataTable, change is backward compatible and the option will be removed from column in a future release.
* AutoComplete: removed process/global/onstart/oncomplete in favor of "p:ajax event="query" process/global/onstart/oncomplete"
* Preferred way of accessing widgets is via PF('widgetVarName').show(), old way (e.g. widgetVarName.show()) is still supported and will be removed in a future version.

## 3.4 to 3.5
* DateSelectEvent, ScheduleDateSelectEvent, ScheduleEntrySelectEvent classes are removed, use SelectEvent instead.
* Column class in SortEvent and ColumnResizeEvent is replaced with UIColumn to support dynamic columns.
* Scrollable and Resizable DataTable-TreeTable features are reimplemented.
* DataTable and TreeTable no longer render cell container div element with classes ui-dt-c and ui-tt-c.
* Sheet component is removed as it duplicated functionality of DataTable.
* Galleria is reimplemented.
* IE7 support is phased out.

## 3.3 to 3.4
* Tree components dom structure has been reimplemented, if you have overwritten trees look and feel, check for compatibility issues.
* RightClick event to open a contextMenu on datatable does not invoke rowSelect ajax behavior event anymore, use dedicated contextMenu event instead.
* DataTable filtering requires filteredValue reference in backing bean, for backwards compatibility if this reference is not defined, filtering will work using view state however this is a fallback and will be removed in future releases.
* Default event of overlayPanel is changed from mousedown to click.
* DataTable widget's clearFilters client side method, now does an ajax call to clear the filters and update the table compared to just clearing filters on client side in prior versions.
* Dialogs on IE7 have fixed widths to overcome IE7 auto width bugs.
height attribute of selectCheckboxMenu is renamed to scrollHeight.
* DataExporter's excludeColumns is replaced by exportable attribute of column.
effectDuration of selectOneMenu renamed to effectSpeed.
* partialSubmit is now false by default. It can be globally configured using primefaces.SUBMIT context parameter and components can override the global setting with provided partialSubmit attribute.
* RequestContext is also available for non-ajax requests as well.

## 3.2 to 3.3
* Scrollable datatable now requires the column width to be defined via width attribute of column instead of style attribute. Tiered and sliding type menus have their own components, name tieredMenu and slideMenu. readOnlyInpuText attribute of calendar is renamed to readOnlyInput.
* Menu component’s position attribute is renamed to overlay as a boolean.

## 3.1 to 3.2
* Growl now uses css to display the severity icons instead of the previous Icon attributes.
* Password component sets feedback mode to false by default.
* Incell editing datatable requires editable attribute to be set true.
* ProgressBar’s oncomplete attribute is removed in favor of complete ajax behavior.
* Incell editing datatable requires editable attribute to be set true.

## 3.0 to 3.1
* Component referencing is now aligned with JSF Spec, if PrimeFaces cannot find a component, it will throw an exception. Since PrimeFaces 2.2 we’ve been logging an info message that component cannot be found and falling back to the client id. If you haven’t ignored these messages and fixed your code since 2.2, there won’t be a problem. If not, you need to update your component referencing with respect to findComponent specification.
* primefaces.THEME_FORMS setting is removed in favor of plain css, if you need to reset the theme aware styles on input components, add a reset css instead.
* Timeline is removed
* Lightbox is reimplemented
* Selectable tree nodes get pointer cursor

## 2.x to 3.0
Taglib namespaces are changed as;

* http://primefaces.prime.com.tr/ui -> http://primefaces.org/ui
* http://primefaces.prime.com.tr/mobile -> http://primefaces.org/mobile

Component events are now decoupled and implemented as ajax behaviors to improve flexibility. Read more at [here](http://blog.primefaces.org/p=1224). Common *Listener and on*Update attributes are now removed, an example is rating component;

**2.x;**

`<p:rating value="#{ratingController.ratingValue}" rateListener="#{ratingController.handleRate}" update="messages"/>`

**3.x;***

`<p:rating value="#{ratingController.ratingValue}">
   <p:ajax event="rate" listener="#{ratingController.handleRate}" update="messages" />
</p:rating>`

User's guide and Taglib docs(IDE completion) will provide all the available events of a component.

* RequestContext now allows to execute javascript from backing beans (e.g. deciding to keep a dialog open or hide it), In 2.x this conditional javascript execution on callbacks like oncomplete are achieved via callback params, execute("script here") makes it very easy to implement the same compared to callback params. Note that callback params are still supported and will be in future as they are also used internally in PrimeFaces.
* Tag/Attribute docs are available again in facelet taglib to take advantage of quick documentation via IDE code completion.
* Aristo replaced Sam as the built-in theme, sam is available at theme gallery as a downloadable theme.

### Component Changes
**AccordionPanel**
* Reimplemented as a native PrimeFaces widget instead of using jQuery UI's accordion widget.
fillSpace,&nbsp;autoHeight, event,&nbsp;collapsible and effect attributes are removed.
* Multiple selection of tabs are now supported so activeIndex is a string instead of an integer. Provide a comma seperated list of indexes to activate multiple tabs.
onTabShow attribute is added as a client side callback to execute when a tab is shown. onTabChange is also available as a callback when a tab is clicked but not yet shown. Passed parameters to the callbacks are changed.
* Dynamic number of tabs are now support by providing a collection, use this feature instead of c:forEach and ui:repeat.
* AccordionPanel is a naming container so make sure to you are aware of how search [algorithm](http://download.oracle.com/javaee/5/api/javax/faces/component/UIComponentBase.html#findComponent(java.lang.String)) works when referencing components.
* Event listeners are reimplemented as ajax behaviors.

**AutoComplete**
* Reimplemented as a native PrimeFaces widget instead of using jQuery UI's autocomplete widget.
Custom content support is available.
panelStyle and panelStyleClass options are added to customize the overlay.
* New dropdown mode places a button next to the input element to display all the available suggestions.
CSS selectors are changed to align with general theming architecture.
* Event listeners are reimplemented as ajax behaviors.

**Calendar**
* Added timePicker functionality.
* I18N has been externalized via PrimeFacesLocales, PrimeFaces does not provide the translations anymore.
* Event listeners are reimplemented as ajax behaviors.

**Carousel**
* Reimplemented as a native PrimeFaces widget instead of using YUI's carousel widget.
* Header and Footer support is added.
* CSS selectors are changed to align with general theming architecture.

**Charts**
* Reimplemented using jqPlot canvas based library instead of YUI's flash based charting library.
- chartSeries tag support is dropped instead of ChartModel API.
* Added donut, ohlc, meter gauge, area and bubble charts.
* Built-in polling is dropped in favor of Poll component.
* Added built-in resizing, axis titles, text rotation, enhanced legend and other useful features.
* Event listeners are reimplemented as ajax behaviors.

**ColorPicker**
* Reimplemented with a jQuery plugin to drop YUI color picker.
* Value only accepts hex value as String.
* Added inline mode.

**ConfirmDialog**
* Reimplemented as a native PrimeFaces instead of using jQuery UI dialog.

**ContextMenu**
* Reimplemented as a native widget instead of wijmo.
* ContextMenu has special integration with datatable, tree and treeTable.
* Modality and Fade effect is always on.
* Can be updated directly with ajax.

**DataTable**
* Selection requires rowKeys compared to old problematic rowIndex approach. There is a new attribute called rowKey to define a row data identifier in your domain model, additionally SelectableDataModel is introduced to implement advanced and efficient selection.
* Each cell is wrapped in a div with class .ui-dt-c, column style and styleClass apply to this wrapper.
* DataTable has special integration with contextMenu.
* Notable new features are column resizing and subtable for advanced grouping.
* Lazy Loading has been greatly enhanced and showstopper bugs are fixed.
* Event listeners are reimplemented as ajax behaviors.
* New events are available to hook-in like page, sort and filter.
* page attribute removed.

**Dashboard**
* Event listeners are reimplemented as ajax behaviors.

**GraphicImage**
* More secure way to stream dynamic content is implemented under the hood.

**DragDrop**
* Event listeners are reimplemented as ajax behaviors.

**Dialog**
* Reimplemented as a native PrimeFaces instead of using jQuery UI dialog.
* Supports minimize and maximize.
* Dynamic mode allows lazy loading of content until displaying for the first time.
* Removed closeOnEscape option.
* Event listeners are reimplemented as ajax behaviors.

**Divider**
* Removed, use separator instead.

**Fieldset**
* Event listeners are reimplemented as ajax behaviors.

**FileUpload**
* Reimplemented using HTML5 widget instead of a flash based widget.
* Mode option allows to choose between simple and advanced uploader.
* Supports dragdrop from file system.
* Changes in how upload restrictions are defined (e.g. allowTypes).

**Galleria**
* Supports dynamic images via iteration.

**GMap**
* Event listeners are reimplemented as ajax behaviors.
* Support for circle and rectangles.

**Growl**
* Displayed messages are removed in case a new message is added. Previously new messages are appended to the list of messages causing confusion.

**IdleMonitor**
* Event listeners are reimplemented as ajax behaviors.

**Inplace**
* Event listeners are reimplemented as ajax behaviors.

**Layout**
* Reimplemented using a jQuery plugin.
* LayoutUnit names are changed (e.g. top \-> north, left \-> west)
size option is used instead of width and height to define unit dimension.
* Support for nested layouts.
* Event listeners are reimplemented as ajax behaviors.

**Media**
* More secure way to stream dynamic content is implemented under the hood.

**Menu**
* Reimplemented as a native widget instead of using wijmo.

**Menubar**
* Reimplemented as a native widget instead of using wijmo.
* autosubmenudisplay removed for now, might be added again in a future release.

**MenuButton**
* Menu part reimplemented as a native widget instead of using wijmo.

**Paginator**
* Reimplemented as a native widget instead of using yui.

**Panel**
* Event listeners are reimplemented as ajax behaviors.

**PickList**
* Custom content support is available.
* Item visuals are improved.

**RemoteCommand**
* Dynamic parameters can be passed into corresponding JavaScript function like remoteCommandName(\{ name1: 'value1', name2: 'value2' \});

**Slider**
* Event listeners are reimplemented as ajax behaviors.

**Spinner**
* Reimplemented as a native PrimeFaces widget instead of using a jQuery plugin.
* showOn option is removed.

**Star Rating**
* Event listeners are reimplemented as ajax behaviors.

**TabView**
Reimplemented as a native PrimeFaces widget instead of using jQuery UI's tabs widget.
event and&nbsp;{}collapsible&nbsp;attributes are removed.
New effects are added&nbsp;
* Dynamic number of tabs are now support by providing a collection, use this feature instead of c:forEach and ui:repeat.
* TabView is a naming container so make sure to you are aware of how search [algorithm](http://download.oracle.com/javaee/5/api/javax/faces/component/UIComponentBase.html#findComponent(java.lang.String)) works when referencing components.
* Event listeners are reimplemented as ajax behaviors.
* Tabs can be closable.
* Tabs can be disabled.
* Passed parameters to the client side callbacks are changed.

**ThemeSwitcher**
* Reimplemented as a native PrimeFaces widget instead of using jQuery UI's themeswitcher widget.
* No need to require internet connection as themes are changed locally.
* Used as a selectOneMenu.

**Tooltip**
* Reimplemented as a native PrimeFaces widget instead of using a jQuery plugin.
* Fixes to positioning and overlay issues.
* Removed global tooltip and custom positioning.
* More show/hide effects added.

**Tree**
* Reimplemented as a native PrimeFaces widget instead of using YUI Tree widget.
* Supports custom content in treeNodes.
* Event listeners are reimplemented as ajax behaviors.
* DragDrop feature is added.
* Special contextMenu integration is available.

**TreeTable**
* Reimplemented as a native PrimeFaces widget instead of using jQuery plugin.
* Nodes are expanded/collapsed with ajax.
* Event listeners are available as ajax behaviors.
* Selection mode is added featuring single, multiple and instant selection.
* Scrollable(horizontal and/or vertical) display.
* Special contextMenu integration is available.
* Resizable Columns.

**PrimeFaces Mobile**
* TouchFaces has been rebranded as PrimeFaces Mobile and rewritten using jQuery Mobile.