# Whats new?

This page contains a list of big features. Please check the GitHub issues for all changes.

## 8.0

  * Fluent builders
    * Menu: instantiate menu items using builders (e.g DefaultMenuItem#builder())
    * Timeline: instantiate timeline events using builders (e.g TimelineEvent#builder())
    * Schedule: instantiate schedule events using builders (e.g DefaultScheduleEvent#builder())
    * DefaultStreamedContent: instantiate DefaultStreamedContent using builders (e.g DefaultStreamedContent#builder())
  * Calendar and DatePicker offer built-in support for java.time.LocalDate, java.time.LocalDateTime and java.time.LocalTime (no converter needed anymore)
  * Many events (eg SelectEvent, UnselectEvent, RowEditEvent, etc.) now use generics
  * Cropper: change libraries from JCrop to Cropper.js
  * PanelGrid: new layout="flex" based on PrimeFlex/FlexGrid
  * MenuModel#addElement() has been deprecated, add element using MenuModel#getElements()#add() instead.

