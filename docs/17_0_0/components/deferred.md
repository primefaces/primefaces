# Deferred

Deferred loads its content via AJAX after the page was rendered and shows a placeholder until then. The rest of the
page is displayed instantly, the time consuming part follows.

## Getting Started
On the initial request only the placeholder is rendered. Once the content is requested, the `load` listener is
called, the content is rendered and replaces the placeholder.

```xhtml
<h:form>
    <p:deferred>
        <p:ajax event="load" listener="#{ordersView.load}"/>

        <p:dataTable value="#{ordersView.orders}" var="order" rows="10" paginator="true">
            <p:column headerText="Number">#{order.number}</p:column>
            <p:column headerText="Customer">#{order.customer}</p:column>
        </p:dataTable>
    </p:deferred>
</h:form>
```

Load the data in the `load` listener, not in `@PostConstruct`, and keep it at least in the view scope: once the content
is loaded, later requests of the same view render it directly without calling the listener again.

## Loading facet
By default a loading indicator is displayed as placeholder. The `loading` facet replaces it with any content,
typically a [Skeleton](/components/skeleton.md).

```xhtml
<p:deferred>
    <f:facet name="loading">
        <p:skeleton height="2rem" styleClass="mb-2"/>
        <p:skeleton height="10rem"/>
    </f:facet>
    <p:ajax event="load" listener="#{ordersView.load}"/>
    ...
</p:deferred>
```

## Modes
`mode` defines when the content is loaded.

| Mode | Description |
| --- | --- |
| load (default) | Right after the component was rendered. |
| visible | When the component enters the viewport. This also covers scroll containers, tabs and dialogs. |
| manual | Only via the client side API. |

```xhtml
<p:deferred mode="visible">
    <f:facet name="loading">
        <p:skeleton height="18rem"/>
    </f:facet>
    <p:ajax event="load" listener="#{dashboardView.loadRevenue}"/>

    <p:chart value="#{dashboardView.revenueChart}"/>
</p:deferred>
```

With `visible` the placeholder should have about the height of the later content, otherwise all placeholders of the
page are in the viewport at once and load immediately.

### Manual
With `manual` the content is loaded with `load()` of the client side component. Without a `loading` facet nothing is
displayed until then.

```xhtml
<p:commandButton type="button" value="Show history" onclick="PF('history').load()"/>

<p:deferred ref="history" mode="manual">
    <p:ajax event="load" listener="#{customerView.loadHistory}"/>
    ...
</p:deferred>
```

The `loading` facet is displayed until the content is loaded, so it can also contain the trigger:

```xhtml
<p:deferred ref="comments" mode="manual">
    <f:facet name="loading">
        <p:commandButton type="button" value="Load comments" onclick="PF('comments').load()"/>
    </f:facet>
    <p:ajax event="load" listener="#{articleView.loadComments}"/>
    ...
</p:deferred>
```

## Loaded state
The component remembers in the view state whether its content was rendered.

- Until the content is loaded, every request renders the placeholder, also an AJAX update of the component or of a
  parent. The children are handled like `rendered="false"`, they are not processed.
- The request that loads the content calls the `load` listener and renders the content. It does not process the
  children.
- Once the content is loaded, every request of the same view renders the content directly.

`loaded="true"` marks the content as loaded, e.g. when the data is available already. It is rendered directly then,
without load request and without `load` listener. With `false` or no value the component decides itself.

```xhtml
<p:deferred loaded="#{not empty reportView.report}">
    <p:ajax event="load" listener="#{reportView.loadReport}"/>
    ...
</p:deferred>
```

A stateless view (`f:view transient="true"`) can not keep the loaded state. There the placeholder is rendered on
non-AJAX requests and the content on AJAX requests, the children are processed in every request except the one that
loads the content.

## Client Side API
Client side component: `PrimeFaces.component.Deferred`

| Method | Params | Return Type | Description |
| --- | --- | --- | --- |
| load() | - | void | Loads the content. Does nothing when it is loaded already or the request is running. |
| isLoaded() | - | boolean | Whether the content is loaded. |

When the load request fails, the placeholder stays and `load()` can be called again; there is no automatic retry.

## Ajax Behavior Events

| Event | Listener Parameter | Fired |
| --- | --- | --- |
| load | jakarta.faces.event.AjaxBehaviorEvent | When the content is loaded. |

The load request is queued like every AJAX request, `async="true"` is not supported on the `load` behavior.

Without a `load` behavior the load request does not trigger `p:ajaxStatus` and skips components with `p:autoUpdate`.
With a `p:ajax` its attributes apply, so set them there if needed:

```xhtml
<p:ajax event="load" listener="#{bean.load}" global="false" ignoreAutoUpdate="true"/>
```

## Layout
- block (default): Renders a div
- inline: Renders a span

## Accessibility
The container has `aria-busy="true"` while the load request is running. The default loading indicator and the
`loading` facet are hidden from assistive technology (`aria-hidden`), except the facet in the `manual` mode.

## Notes
- The load request is a separate postback, request scoped state of the initial request is gone.
- A form is required on the page: the load request uses the parent form or, if there is none, the first form of the
  page.
- The component tree is built completely (`c:forEach`, `ui:include`), only rendering is deferred.
- Several components on a page load one after another.
- Inside iterating components (`ui:repeat`, `p:dataTable`, ...) the loaded state is not kept per row, unless the
  iterating component preserves the row state. Once one row has loaded, all rows count as loaded. Do not put inputs into
  a `p:deferred` inside an iteration.
- If the response of the load request does not reach the client, the server may already treat the content as loaded.

## Skinning
_style_ and _styleClass_ attributes are used to style the container.

| Class | Applies |
| --- | --- |
| .p-deferred | Container element |
| .p-deferred-manual | Container element in the `manual` mode |
| .p-deferred-placeholder | Wrapper of the `loading` facet (not in the `manual` mode) |
| .p-deferred-loading | Default loading indicator |

| Token | Description |
| --- | --- |
| --p-deferred-loading-padding | Padding around the default loading indicator |
| --p-deferred-loading-icon-size | Size of the default loading indicator |
| --p-deferred-loading-icon-color | Color of the default loading indicator |
| --p-deferred-loading-icon | Icon of the default loading indicator |
| --p-deferred-loading-icon-animation-duration | Duration of one rotation |
