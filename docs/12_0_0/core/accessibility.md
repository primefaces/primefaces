# Accessibility

## WAI-ARIA

WAI-ARIA (Web Accessibility Initiative – Accessible Rich Internet Applications) is a technical
specification published by the World Wide Web Consortium (W3C) that specifies how to increase
the accessibility of web pages, in particular, dynamic content and user interface components
developed with Ajax, HTML, JavaScript and related technologies. – Wikipedia

ARIA compatibility is an important goal of PrimeFaces as a result keyboard support as well as
screen reader support are available to many components. Many of these features are built-in and
does not require any configuration to use them. However for screen readers, localized texts might
be necessary so that component can read the aria labels and messages from a bundle. PrimeFaces
provides English translations by default and you may use the following keys in your JSF message
bundle to provide your own values.

- primefaces.datatable.aria.FILTER_BY = Filter by {0}
- primefaces.datatable.aria.HEADER_CHECKBOX_ALL = Select All
- primefaces.dialog.aria.CLOSE = Close
- primefaces.messages.aria.CLOSE = Close
- primefaces.messages.aria.ERROR = Error
- primefaces.messages.aria.FATAL = Fatal
- primefaces.messages.aria.INFO = Information
- primefaces.messages.aria.WARN = Warning
- primefaces.paginator.aria.FIRST_PAGE = First Page
- primefaces.paginator.aria.HEADER = Pagination
- primefaces.paginator.aria.LAST_PAGE = Last Page
- primefaces.paginator.aria.NEXT_PAGE = Next Page
- primefaces.paginator.aria.PREVIOUS_PAGE = Previous Page
- primefaces.paginator.aria.ROWS_PER_PAGE = Rows Per Page
- primefaces.panel.aria.OPTIONS_MENU = Toggle Options Menu
- primefaces.panel.aria.TOGGLE = Toggle Panel
- primefaces.rowgrouptoggler.aria.ROW_GROUP_TOGGLER = Toggle Row Group
- primefaces.rowtoggler.aria.ROW_TOGGLER = Toggle Row

## HTML5 Compliance

Along with ARIA standards, PrimeFaces strives to be fully [W3C Validation](https://validator.w3.org/) compliant with the
HTML5 specification.

Before JSF 4.0 all script links required `type="text/javascript"`. However, according to the HTML5 specification
this is not necessary as all scripts by default are Javascript.  If running an application on JSF lower than 4.0 you
can remove the `type="text/javascript"` on all `<script>` links with the following line in your `faces-config.xml`.

```xml
<factory>
   <faces-context-factory>org.primefaces.application.factory.Html5FacesContextFactory</faces-context-factory>
</factory>

```