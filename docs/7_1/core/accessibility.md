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
- primefaces.paginator.aria.HEADER = Pagination
- primefaces.paginator.aria.FIRST_PAGE = First Page
- primefaces.paginator.aria.PREVIOUS_PAGE = Previous Page
- primefaces.paginator.aria.NEXT_PAGE = Next Page
- primefaces.paginator.aria.LAST_PAGE = Last Page
- primefaces.paginator.aria.ROWS_PER_PAGE = Rows Per Page
- primefaces.datatable.aria.HEADER_CHECKBOX_ALL = Select All
- primefaces.dialog.aria.CLOSE = Close
- primefaces.rowtoggler.aria.ROW_TOGGLER = Toggle Row
- primefaces.datatable.SORT_LABEL = Sort
- primefaces.datatable.SORT_ASC = Ascending
- primefaces.datatable.SORT_DESC = Descending
