# TagCloud

TagCloud displays a collection of tag with different strengths.

## Info

| Name | Value |
| --- | --- |
| Tag | tagCloud
| Component Class | org.primefaces.component.tagcloud.TagCloud
| Component Type | org.primefaces.component.TagCloud
| Component Family | org.primefaces.component |
| Renderer Type | org.primefaces.component.TagCloudRenderer
| Renderer Class | org.primefaces.component.tagcloud.TagCloudRenderer

## Attributes

| Name | Default | Type | Description | 
| --- | --- | --- | --- |
id | null | String | Unique identifier of the component
rendered | true | Boolean | Boolean value to specify the rendering of the component, when set to false component will not be rendered.
binding | null | Object | An el expression that maps to a server side UIComponent instance in a backing bean
widgetVar | null | String | Name of the client side widget.
model | null | TagCloudModel | Backing tag cloud model.
style | null | String | Inline style of the container element.
styleClass | null | String | Style class of the container element.

## Getting started with the TagCloud
TagCloud requires a backend TagCloud model to display.

```xhtml
<p:tagCloud model="#{tagCloudBean.model}" />
```

```java
public class TagCloudBean {
    private TagCloudModel model;

    public TagCloudBean() {
        model = new DefaultTagCloudModel();
        model.addTag(new DefaultTagCloudItem("Transformers", "#", 1));
        //more
    }
    //getter
}
```
## Selecting Tags
An item is tagCloud can be selected using _select_ ajax behavior. Note that only items with | null | urls
can be selected.

```xhtml
<h:form>
    <p:growl id="msg" showDetail="true" />
    <p:tagCloud model="#{tagCloudBean.model}">
        <p:ajax event="select" update="msg" listener="#{tagCloudBean.onSelect}" />
    </p:tagCloud>
</h:form>
```
```java
public class TagCloudBean {
    //model, getter and setter
    public void onSelect(SelectEvent event) {
        TagCloudItem item = (TagCloudItem) event.getObject();
        FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Item Selected", item.getLabel());
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }
}
```
## TagCloud API
_org.primefaces.model.tagcloud.TagCloudModel_

| Method | Description |
| --- | --- |
List<TagCLoudItem> getTags() | Returns all tags in model.
void addTag(TagCloudItem item) | Adds a tag.
void removeTag(TagCloudItem item) | Removes a tag.
void clear() | Removes all tags.

PrimeFaces provides _org.primefaces.model.tagcloud.DefaultTagCloudModel_ as the default
implementation.


_org.primefaces.model.tagcloud.TagCloudItem_

| Method | Description |
| --- | --- |
String getLabel() | Returns label of the tag.
String getUrl() | Returns url of the tag.
int getStrength() | Returns strength of the tag between 1 and 5.

_org.primefaces.model.tagcloud.DefaultTagCloudItem_ is provided as the default implementation.

## Skinning
TagCloud resides in a container element that _style_ and _styleClass_ attributes apply. _.ui-tagcloud_
applies to main container and _.ui-tagcloud-strength-[1,5]_ applies to each tag. As skinning style
classes are global, see the main theming section for more information.

